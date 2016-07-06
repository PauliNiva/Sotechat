package sotechat.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.joda.time.DateTime;

import java.util.*;

import sotechat.controller.MessageBroker;
import sotechat.domain.Person;
import sotechat.service.DatabaseService;
import sotechat.wrappers.ConvInfo;
import sotechat.wrappers.MsgToClient;
import sotechat.wrappers.MsgToServer;

/**
 * Muistaa Chattiin kirjoitetut viestit.
 */
@Component
public class ChatLogger {

    /**
     * Kuinka usein siivotaan vanhoja viesteja muistista.
     */
    private static final int CLEAN_FREQUENCY_IN_MS = 1000 * 60 * 60 * 24; // 1pv

    /**
     * Siivouksessa poistetaan keskustelut, joiden uusin viesti on
     * vanhempi kuin tassa muuttujassa maaritelty.
     * */
    private static final int DAYS_OLD_TO_BE_DELETED = 3;

    /**
     * Viive, kun saman kanavan lokeihin pyydetaan useita tiedotuksia.
     */
    private static final int CLBC_DELAY_MS = 200;

    /**
     * Avaimena <code>channelId</code>, arvona <code>Long</code>
     * milloin viimeisin tiedotus tietylle kanavalle oli.
     */
    private Map<String, Long> lastBroadcast;

    /**
     * Avaimena kanavan id ja arvona lista viesteja (kanavan lokitiedot).
     */
    private Map<String, List<MsgToClient>> logs;

    /**
     * Kasittelee sessioita.
     */
    @Autowired
    private SessionRepo sessionRepo;

    /**
     * Mapper.
     */
    @Autowired
    private Mapper mapper;

    /**
     * Tietokantapalvelut.
     */
    @Autowired
    private DatabaseService databaseService;

    /**
     * Konstruktori. Alustaa lokitiedoille uuden hajautustaulun.
     */
    public ChatLogger() {
        this.logs = new HashMap<>();
        this.lastBroadcast = new HashMap<>();
        waitAndTryToInitializeDependencies();
    }

    /**
     * Viime hetken korjaus, selitetty alemmassa metodissa.
     */
    public void waitAndTryToInitializeDependencies() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                tryToInitializeDependencies();
            }
        }, 100);
    }

    /**
     * TODO: Refactoroi.
     * Kaytannossa allaoleva metodi suoritetaan kerran,
     * palvelimen kaynnistyessa. Kyseessa on viime
     * hetken korjaus, jolla saatiin purkattua kasaan
     * tarvittavat riippuvuudet, jotta asiakaskayttajien ja
     * ammattilaiskayttajien timeout disconnect toimii
     * jarkevalla ja intuitiivisella tavalla. Metodi
     * odottelee, kunnes Mapper ja DatabaseService ovat
     * alustettu, ja sen jalkeen antaa Mapperille
     * riippuvuuden DatabaseServiceen. Miksi riippuvuuksia
     * ei olla asetettu normaalilla tavalla? Jostain syysta
     * joillain tietokoneilla Spring kuvittelee loytaneensa
     * kehariippuvuuden ja kieltaytyy kaynnistymasta.
     * Talla purkalla saadaan Spring kaynnistymaan.
     * Spring siis luulee, etta DatabaseServicesta on
     * riippuvuus takaisin Mapperiin, vaikka nain ei ole.
     *
     * Lisaksi tama metodi kirjaa palvelimen kaynnistymisen
     * yhteydessa Mapperiin tietokannasta varatut ID:t ja
     * usernamet.
     *
     */
    public void tryToInitializeDependencies() {
        if (mapper == null || databaseService == null) {
            waitAndTryToInitializeDependencies();
            return;
        }
        mapper.setDatabaseService(databaseService);

        List<Person> persons = databaseService.getAllPersons();
        for (Person person : persons) {
            String username = person.getUserName();
            String userId = person.getUserId();
            mapper.mapProUsernameToUserId(username, userId);
            mapper.reserveId(userId);
            List<ConvInfo> list = databaseService
                    .getConvInfoListOfUserId(userId);
            for (ConvInfo conv : list) {
                String channelId = conv.getChannelId();
                mapper.reserveId(channelId);
            }
        }
    }

    /**
     * Asettaa <code>Mapper</code>-olion.
     *
     * @param pMapper Asetettava <code>Mapper</code>-olio.
     */
    public void setMapper(final Mapper pMapper) {
        this.mapper = pMapper;
    }

    /**
     * Konstruktori testausta varten.
     *
     * @param pSessionRepo     SessionRepo.
     * @param pDatabaseService DatabaseService.
     */
    public ChatLogger(
            final SessionRepo pSessionRepo,
            final DatabaseService pDatabaseService
    ) {
        this();
        this.sessionRepo = pSessionRepo;
        this.databaseService = pDatabaseService;
    }

    /**
     * Kirjaa viestin lokitietoihin, muistiin ja tietokantaan.
     * Palauttaa viestin clienteille lahetettavassa muodossa.
     *
     * @param msgToServer Viesti palvelimelle.
     * @return msgToClient Viesti clientille.
     */
    public final synchronized MsgToClient logNewMessage(
            final MsgToServer msgToServer) {
        /* Esikasitellaan tietoja muuttujiin. */
        String channelId = msgToServer.getChannelId();
        String messageId = pollNextFreeMessageIdFor(channelId);
        String userId = msgToServer.getUserId();
        Session session = sessionRepo.getSessionFromUserId(userId);
        String username = session.get("username");
        String timeStamp = new DateTime().toString();
        String content = msgToServer.getContent();
        MsgToClient msgToClient = new MsgToClient(
                messageId, username, channelId, timeStamp, content
        );

        /* Tallennetaan seka muistiin etta tietokantaan. */
        saveToMemory(msgToClient);
        saveToDatabase(msgToClient);

        /* Palautetaan viesti MsgToClient -oliona lahetysta varten. */
        return msgToClient;
    }

    /**
     * Tallentaa viestin muistiin.
     *
     * @param msgToClient Tallennettava viesti.
     */
    private void saveToMemory(final MsgToClient msgToClient) {
        String channelId = msgToClient.getChannelId();
        List<MsgToClient> list = logs.get(channelId);
        if (list == null) {
            list = new ArrayList<>();
            logs.put(channelId, list);
        }
        list.add(msgToClient);
    }

    /**
     * Tallentaa viestin tietokantaan.
     *
     * @param msgToClient Tallennettava viesti.
     */
    private void saveToDatabase(final MsgToClient msgToClient) {
        String username = msgToClient.getUsername();
        String content = msgToClient.getContent();
        String timeStamp = msgToClient.getTimeStamp();
        String channelId = msgToClient.getChannelId();
        databaseService.saveMsg(username, content, timeStamp, channelId);
    }

    /**
     * Lahettaa pyydetyn kanavan lokit kaikille kanavan tilanneille.
     * Tama metodi ohjaa tehtavan mahdollisen timerin
     * kautta metodille actuallyBroadcast.
     * <p>
     * Operaatio on suhteellisen raskas ja palvelinta voisi kyykyttaa
     * aiheuttamalla esim. tuhansia paivityksia sekunnissa.
     * Suojakeinona palvelunestohyokkayksia vastaan tiedotusten
     * valille on asetettu minimiviive.
     *
     * @param channelId p.
     * @param broker p.
     */
    public synchronized void broadcast(final String channelId,
                                       final MessageBroker broker) {
        long timeNow = new DateTime().getMillis();
        Long lastBroadcastTime = lastBroadcast.get(channelId);
        if (lastBroadcastTime == null) {
            lastBroadcastTime = 0L;
        }
        if (lastBroadcastTime + CLBC_DELAY_MS < timeNow) {
            /* Jos ei olla juuri asken tiedotettu, tehdaan se nyt. */
            actuallyBroadcast(channelId, broker);
            lastBroadcastTime = new DateTime().getMillis();
        } else if (lastBroadcastTime < timeNow) {
            /* Jos ollaan askettain lahetetty lokit, halutaan
             * viivastyttaa seuraavaa lahetysta, mutta ei
              * haluta kaynnistaa useita ajastimia yhdelle
              * kanavalle. Sen vuoksi
              * lastBroadcastTime asetetaan tulevaisuuteen ja
              * else if -ehdossa tarkistetaan sen avulla, onko
              * uusi lahetys jo ajastettu. */
            lastBroadcastTime += CLBC_DELAY_MS;
            long delayToNext = lastBroadcastTime - timeNow;
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    actuallyBroadcast(channelId, broker);
                }
            }, delayToNext);
        }
        lastBroadcast.put(channelId, lastBroadcastTime);
    }

    /**
     * Lahettaa kanavan chat-lokitiedot kanavan kuuntelijoille.
     *
     * @param channelId Kanavatunnus.
     * @param broker    Viestin valittaja.
     */
    private synchronized void actuallyBroadcast(final String channelId,
                                                final MessageBroker broker) {
        String channelIdWithPath = "/toClient/chat/" + channelId;
        for (MsgToClient msg : getLogs(channelId)) {
            broker.convertAndSend(channelIdWithPath, msg);
        }
    }

    /**
     * Palauttaa JSON-ystavallisen listauksen Stringina kaikista kanavista,
     * joilla kayttaja on koskaan ollut.
     *
     * @param userId KayttajanId.
     * @return merkkijono muotoa ["kanava1", "kanava2"].
     */
    public final synchronized List<ConvInfo> getChannelsByUserId(
            final String userId) {
        return databaseService.getConvInfoListOfUserId(userId);
    }

    /**
     * Getteri halutun kanavan lokeille.
     * Yrittaa hakea ensin muistista, sitten tietokannasta.
     * Hakee kanavan lokitiedot.
     * Yrittaa ensin hakea muistista ja jos ei loyda sieltä, sen jalkeen
     * hakee tietokannasta.
     *
     * @param channelId Kanavan id.
     * @return Lista <code>msgToClient</code>-olioita.
     */
    public final synchronized List<MsgToClient> getLogs(
            final String channelId) {
        List<MsgToClient> list = logs.get(channelId);
        if (list == null) {
            /* Jos ei loydy muistista, haetaan tietokannasta muistiin. */
            list = databaseService.retrieveMessages(channelId);
            logs.put(channelId, list);
        }
        return list;
    }

    /**
     * Antaa seuraavan vapaan ID:n viestille AngularJS:aa varten.
     * Joka kanavan viestit saapumisjarjestyksessa: 1,2,3...
     *
     * @param channelId <code>channelId</code>.
     * @return <code>MessageId</code>.
     */
    private synchronized String pollNextFreeMessageIdFor(
            final String channelId) {
        List<MsgToClient> list = logs.get(channelId);
        if (list == null) {
            return "1";
        }
        /** Huom: ID ei saa alkaa nollasta! */
        return (1 + list.size()) + "";
    }

    /**
     * Poistaa vanhan viestit muistista. Palvelimen ollessa paalla pitkaan
     * muisti voi loppua. Taman vuoksi vanhat viestit on hyva siivota pois
     * muistista esim. kerran paivassa (jattaen ne kuitenkin tietokantaan).
     */
    @Scheduled(fixedRate = CLEAN_FREQUENCY_IN_MS)
    public synchronized void work() {
        removeOldMessagesFromMemory(DAYS_OLD_TO_BE_DELETED);
    }


    /**
     * Siivoaa <code>ChatLogger</code>:in muistista vanhat viestit, jattaen ne
     * tietokantaan. Keskustelun vanhuus maaraytyy sen uusimman viestin mukaan.
     *
     * @param daysOld Kuinka monta paivaa vanhat keskustelut poistetaan.
     */
    public final synchronized void removeOldMessagesFromMemory(
            final int daysOld
    ) {
        Long now = DateTime.now().getMillis();
        Long threshold = now - daysOld * CLEAN_FREQUENCY_IN_MS;
        DateTime trdate = new DateTime(threshold);
        Iterator<Map.Entry<String, List<MsgToClient>>> iterator =
                logs.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<MsgToClient>> entry = iterator.next();
            String channelId = entry.getKey();
            List<MsgToClient> listOfMsgs = entry.getValue();
            if (listOfMsgs == null || listOfMsgs.isEmpty()) {
                iterator.remove();
            } else {
                MsgToClient last = listOfMsgs.get(listOfMsgs.size() - 1);
                DateTime lastdate = DateTime.parse(last.getTimeStamp());
                if (lastdate.isBefore(trdate)) {
                    /* Poistetaan ChatLoggerin logeista. */
                    iterator.remove();
                    /* Poistetaan myos Mapperin kanavista. */
                    mapper.forgetChannel(channelId);
                }
            }
        }
    }

}

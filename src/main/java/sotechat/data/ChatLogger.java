package sotechat.data;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sotechat.controller.MessageBroker;
import sotechat.service.DatabaseService;
import sotechat.wrappers.ConvInfo;
import sotechat.wrappers.MsgToClient;
import sotechat.wrappers.MsgToServer;

import java.util.*;

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
     * Avain = kanavan id. Arvo = lista viesteja (kanavan lokit).
     */
    private Map<String, List<MsgToClient>> logs;

    /**
     * Sessioiden kasittely.
     */
    @Autowired
    private SessionRepo sessionRepo;

    /**
     * Tietokantapalvelut.
     */
    @Autowired
    private DatabaseService databaseService;

    /**
     * Konstruktori.
     */
    public ChatLogger() {
        this.logs = new HashMap<>();
        this.lastBroadcast = new HashMap<>();
    }

    /**
     * Injektoiva konstruktori testausta varten.
     *
     * @param pSessionRepo     SessionRepo
     * @param pDatabaseService DatabaseService
     */
    public ChatLogger(
            final SessionRepo pSessionRepo,
            final DatabaseService pDatabaseService
    ) {
        super();
        this.sessionRepo = pSessionRepo;
        this.databaseService = pDatabaseService;
    }

    /**
     * Kirjaa viesti lokeihin; seka muistiin etta tietokantaan.
     * Palauttaa viestin clienteille lahetettavassa muodossa.
     *
     * @param msgToServer msgToServer.
     * @return msgToClient msgToClient.
     */
    public final synchronized MsgToClient logNewMessage(
            final MsgToServer msgToServer
    ) {
        /** Esikasitellaan tietoja muuttujiin. */
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

        /** Tallennetaan seka muistiin etta tietokantaan. */
        saveToMemory(msgToClient);
        saveToDatabase(msgToClient);

        /** Palautetaan viesti MsgToClient -oliona lahetysta varten. */
        return msgToClient;
    }

    /**
     * Tallentaa viestin muistiin.
     *
     * @param msgToClient Tallennettava viesti.
     */
    private void saveToMemory(
            final MsgToClient msgToClient
    ) {
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
    private void saveToDatabase(
            final MsgToClient msgToClient
    ) {
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
    public synchronized void broadcast(
            final String channelId,
            final MessageBroker broker
    ) {
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
     * Lahettaa kanavan chat-logit kanavan subscribaajille.
     *
     * @param channelId channelId
     * @param broker    broker
     */
    private synchronized void actuallyBroadcast(
            final String channelId,
            final MessageBroker broker
    ) {
        String channelIdWithPath = "/toClient/chat/" + channelId;
        for (MsgToClient msg : getLogs(channelId)) {
            broker.convertAndSend(channelIdWithPath, msg);
        }
    }

    /**
     * Palauttaa JSON-ystavallisen listauksen Stringina kaikista kanavista,
     * joilla kayttaja on koskaan ollut.
     *
     * @param userId userId
     * @return String muotoa ["kanava1", "kanava2"]
     */
    public final synchronized List<ConvInfo> getChannelsByUserId(
            final String userId
    ) {
        return databaseService.getConvInfoListOfUserId(userId);
    }

    /**
     * Getteri halutun kanavan lokeille.
     * Yrittaa hakea ensin muistista, sitten tietokannasta.
     *
     * @param channelId kanavan id
     * @return lista msgToClient-olioita.
     */
    public final synchronized List<MsgToClient> getLogs(
            final String channelId
    ) {
        List<MsgToClient> list = logs.get(channelId);
        if (list == null) {
            /* Jos ei loydy muistista, haetaan tietokannasta muistiin. */
            list = databaseService.retrieveMessages(channelId);
            logs.put(channelId, list);
        }
        return list;
    }

    /**
     * Antaa seuraavan vapaan ID:n viestille AngularJS varten.
     * Joka kanavan viestit saapumisjarjestyksessa: 1,2,3...
     *
     * @param channelId channelId
     * @return messageId
     */
    private synchronized String pollNextFreeMessageIdFor(
            final String channelId
    ) {
        List<MsgToClient> list = logs.get(channelId);
        if (list == null) {
            return "1";
        }
        /** Huom: ID ei saa alkaa nollasta! */
        return (1 + list.size()) + "";
    }

    /**
     * Palvelimen ollessa paalla pitkaan muisti voi loppua.
     * Taman vuoksi vanhat viestit on hyva siivota pois muistista esim.
     * kerran paivassa (jattaen ne kuitenkin tietokantaan).
     * TODO Taskin suorittaminen hyydyttamatta palvelinta siivouksen ajaksi.
     */
    @Scheduled(fixedRate = CLEAN_FREQUENCY_IN_MS)
    public synchronized void work() {
        removeOldMessagesFromMemory(DAYS_OLD_TO_BE_DELETED);
    }


    /** Siivoaa ChatLoggerin muistista vanhat viestit, jattaen ne tietokantaan.
     * Keskustelun vanhuus maaraytyy sen uusimman viestin mukaan.
     *
     * @param daysOld kuinka monta paivaa vanhat keskustelut poistetaan
     */
    public final synchronized void removeOldMessagesFromMemory(
            final int daysOld
    ) {
        Long now = DateTime.now().getMillis();
        Long threshold = now - daysOld * CLEAN_FREQUENCY_IN_MS;
        Iterator<Map.Entry<String, List<MsgToClient>>> iterator =
                logs.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<MsgToClient>> entry = iterator.next();
            List<MsgToClient> listOfMsgs = entry.getValue();
            if (listOfMsgs == null || listOfMsgs.isEmpty()) {
                iterator.remove();
            } else {
                MsgToClient last = listOfMsgs.get(listOfMsgs.size() - 1);
                DateTime trdate = new DateTime(threshold);
                DateTime lastdate = DateTime.parse(last.getTimeStamp());
                if (lastdate.isBefore(trdate)) {
                    iterator.remove();
                }
            }
        }
    }


}

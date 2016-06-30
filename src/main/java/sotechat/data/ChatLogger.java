package sotechat.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import org.joda.time.DateTime;

import sotechat.controller.MessageBroker;
import sotechat.service.DatabaseService;
import sotechat.wrappers.ConvInfo;
import sotechat.wrappers.MsgToClient;
import sotechat.wrappers.MsgToServer;

/**
 * Chattiin kirjoitettujen viestien kirjaaminen ja valittaminen.
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
     */
    private static final int DAYS_OLD_TO_BE_DELETED = 3;

    /**
     * Avaimena kanavan id ja arvona lista viesteja (kanavan lokitiedot).
     */
    private HashMap<String, List<MsgToClient>> logs;

    /**
     * Sailo <code>Session</code>-olioille.
     */
    @Autowired
    private SessionRepo sessionRepo;

    /**
     * Palvelu tietokannalle.
     */
    @Autowired
    private DatabaseService databaseService;

    /**
     * Konstruktori. Alustaa lokitiedoille uuden hajautustaulun.
     */
    public ChatLogger() {
        this.logs = new HashMap<>();
    }

    /**
     * Konstruktori testausta varten.
     *
     * @param pSessionRepo     SessionRepo.
     * @param pDatabaseService DatabaseService.
     */
    public ChatLogger(final SessionRepo pSessionRepo,
                      final DatabaseService pDatabaseService) {
        this.sessionRepo = pSessionRepo;
        this.databaseService = pDatabaseService;
        this.logs = new HashMap<>();
    }

    /**
     * Kirjaa viestin lokitietoihin, muistiin ja tietokantaan.
     * Palauttaa viestin clienteille lahetettavassa muodossa.
     *
     * @param msgToServer Viesti palvelimelle.
     * @return msgToClient Viesti clientille.
     */
    public final synchronized MsgToClient logNewMessage(
            final MsgToServer msgToServer
    ) {
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
     * @param msgToClient Viesti clientille.
     */
    private void saveToDatabase(final MsgToClient msgToClient) {
        String username = msgToClient.getUsername();
        String content = msgToClient.getContent();
        String timeStamp = msgToClient.getTimeStamp();
        String channelId = msgToClient.getChannelId();
        databaseService.saveMsg(username, content, timeStamp, channelId);
    }

    /**
     * Lahettaa kanavan chat-lokitiedot kanavan kuuntelijoille.
     * Samanaikaisuusongelmien ehkaisemiseksi taman logNewMessage -metodin
     * taytyy sijaita samassa luokassa.
     * TODO Protection against flooding (max 1 broadcast/second/channel).
     *
     * @param channelId Kanavatunnus.
     * @param broker    Viestin valittaja.
     */
    public synchronized void broadcast(final String channelId,
                                       final MessageBroker broker) {
        String channelIdWithPath = "/toClient/chat/" + channelId;
        for (MsgToClient msg : getLogs(channelId)) {
            broker.convertAndSend(channelIdWithPath, msg);
        }
    }

    /**
     * Palauttaa JSON-ystavallisen listauksen Stringina kaikista kanavista,
     * joilla kayttaja on ollut.
     *
     * @param userId KayttajanId.
     * @return merkkijono muotoa ["kanava1", "kanava2"].
     */
    public final synchronized List<ConvInfo> getChannelsByUserId(
            final String userId) {
        return databaseService.getConvInfoListOfUserId(userId);
    }

    /**
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
        /** Huom: ID ei saa alkaa nollasta, koska tietokantaimplementaatiossa
         * ID:t alkavat myos ykkosesta. */
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
            String channelId = entry.getKey();
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

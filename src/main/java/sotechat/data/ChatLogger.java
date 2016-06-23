package sotechat.data;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import sotechat.service.DatabaseService;
import sotechat.wrappers.ConvInfo;
import sotechat.wrappers.MsgToClient;
import sotechat.wrappers.MsgToServer;

import java.util.*;

/** Chattiin kirjoitettujen viestien kirjaaminen ja valittaminen.
 */
@Component
public class ChatLogger {

    /** Avain = kanavan id. Arvo = lista viesteja (kanavan lokit). */
    private HashMap<String, List<MsgToClient>> logs;

    /** Session Repository. */
    @Autowired
    private SessionRepo sessionRepo;

    /** Database Service. */
    @Autowired
    private DatabaseService databaseService;

    /** Konstruktori. */
    public ChatLogger() {
        this.logs = new HashMap<>();
    }

    /**
     * Injektoiva konstruktori testausta varten
     * @param sessionRepo SessionRepo
     * @param databaseService DatabaseService
     */
    public ChatLogger(SessionRepo sessionRepo,
                      DatabaseService databaseService) {
        this.sessionRepo = sessionRepo;
        this.databaseService = databaseService;
        this.logs = new HashMap<>();
    }

    /** Kirjaa viesti lokeihin; seka muistiin etta tietokantaan.
     * Palauttaa viestin clienteille lahetettavassa muodossa.
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

    /** Saves msg to memory.
     * @param msgToClient msg to be saved.
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

    /** Tries to save message to database.
     * @param msgToClient msgToClient.
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

    /** Metodi lahettaa kanavan chat-logit kanavan subscribaajille.
     * Huom: samanaikaisuusongelmien korjaamiseksi samassa
     *      luokassa logNewMessage -metodin kanssa.
     * TODO: Protection against flooding (max 1 broadcast/second/channel).
     * @param channelId channelId
     * @param broker broker
     */
    public synchronized void broadcast(
            final String channelId,
            final SimpMessagingTemplate broker
    ) {
        String channelIdWithPath = "/toClient/chat/" + channelId;
        for (MsgToClient msg : getLogs(channelId)) {
            broker.convertAndSend(channelIdWithPath, msg);
        }
    }

    /** Palauttaa JSON-ystavallisen listauksen Stringina kaikista kanavista,
     * joilla kayttaja on ollut.
     *
     * @param userId userId
     * @return String muotoa ["kanava1", "kanava2"]
     */
    public final synchronized List<ConvInfo> getChannelsByUserId(
            final String userId
    ) {
        return databaseService.getConvInfoListOfUserId(userId);
    }

    /** Getteri halutun kanavan logeille.
     * Yrittaa hakea ensin muistista, sitten tietokannasta.
     * @param channelId kanavan id
     * @return lista msgToClient-olioita.
     */
    public final synchronized List<MsgToClient> getLogs(
            final String channelId
    ) {
        List<MsgToClient> list = logs.get(channelId);
        if (list == null) {
            /** Jos ei loydy muistista, haetaan tietokannasta muistiin. */
            list = databaseService.retrieveMessages(channelId);
            logs.put(channelId, list);
        }
        return list;
    }

    /** Antaa seuraavan vapaan ID:n viestille AngularJS varten.
     * Joka kanavan viestit saapumisjarjestyksessa: 1,2,3...
     * @param channelId channelId
     * @return messageId
     */
    private synchronized String pollNextFreeMessageIdFor(
            final String channelId
    ) {
        List<MsgToClient> list = logs.get(channelId);
        if (list == null) {
            return "0";
        }
        return list.size() + "";
    }

    /** Palvelimen ollessa paalla pitkaan muisti voi loppua.
     * Taman vuoksi tata metodia on kutsuttava esim. kerran viikossa.
     * Siivoaa ChatLoggerin muistista vanhat viestit, jattaen ne tietokantaan.
     * Keskustelun vanhuus maaraytyy sen uusimman viestin mukaan.
     * TODO: Testaa
     * @param daysOld kuinka monta paivaa vanhat keskustelut poistetaan
     */
    public final synchronized void removeOldMessagesFromMemory(
            final int daysOld
    ) {
        Long now = Long.parseLong(new DateTime().toString());
        Long threshold = now + daysOld * 1000 * 60 * 60 * 24;
        Iterator<Map.Entry<String, List<MsgToClient>>> iterator =
                logs.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<MsgToClient>> entry = iterator.next();
            String channelId = entry.getKey();
            List<MsgToClient> listOfMsgs = entry.getValue();
            if (listOfMsgs == null || listOfMsgs.isEmpty()) {
                logs.remove(channelId);
            } else {
                MsgToClient last = listOfMsgs.get(listOfMsgs.size() - 1);
                Long time = Long.parseLong(last.getTimeStamp());
                if (time < threshold) {
                    logs.remove(channelId);
                }
            }
        }

    }
}

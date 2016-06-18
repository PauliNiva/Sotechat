package sotechat.data;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import sotechat.service.DatabaseService;
import sotechat.wrappers.MsgToClient;
import sotechat.wrappers.MsgToServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** Chattiin kirjoitettujen viestien kirjaaminen ja valittaminen.
 */
@Component
public class ChatLogger {

    /** Avain = kanavan id. Arvo = lista viesteja (kanavan lokit). */
    private HashMap<String, List<MsgToClient>> logs;

    /** Mapper. */
    @Autowired
    private Mapper mapper;

    /** Database Service. */
    @Autowired
    private DatabaseService databaseService;

    /** Konstruktori.
     */
    public ChatLogger() {
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
        String username = mapper.getUsernameFromId(userId);
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
     * In case of failure, does NOT throw an exception.
     * @param msgToClient msgToClient.
     */
    private void saveToDatabase(
            final MsgToClient msgToClient
    ) {
        String username = msgToClient.getUsername();
        String content = msgToClient.getContent();
        String timeStamp = msgToClient.getTimeStamp();
        String channelId = msgToClient.getChannelId();
        try {
            databaseService.saveMsg(username, content, timeStamp, channelId);
        } catch (Exception e) {
            System.out.println("Database exception! " + e.toString());
            /* Do not throw this exception! Even if saving message to db
               fails, we still want to deliver the message. */
        }
    }

    /** Metodi lahettaa kanavan chat-logit kanavan subscribaajille.
     * Huom: samanaikaisuusongelmien korjaamiseksi samassa
     *      luokassa logNewMessage -metodin kanssa.
     * TODO: Protection against flooding (max 1 broadcast/second/channel).
     * @param channelId channelId
     * @param broker broker
     */
    public final synchronized void broadcast(
            final String channelId,
            final SimpMessagingTemplate broker
    ) {
        String channelIdWithPath = "/toClient/chat/" + channelId;
        for (MsgToClient msg : getLogs(channelId)) {
            broker.convertAndSend(channelIdWithPath, msg);
        }
    }

    private synchronized List<String> getChannelsByUserId(
            final String userId
    ) {
        return null;
    }

    /** Getteri kanavan lokeille.
     * Yrittaa hakea ensin muistista, sitten tietokannasta.
     * @param channelId kanavan id
     * @return lista msgToClient-olioita.
     */
    private synchronized List<MsgToClient> getLogs(
            final String channelId
    ) {
        List<MsgToClient> list = logs.get(channelId);
        if (list == null) {
            //TODO: hae tietokannasta!
            list = new ArrayList<>();
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
}

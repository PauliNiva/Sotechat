package sotechat.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sotechat.wrappers.MsgToClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Luokan tehtava on kirjata lokeihin chattiin kirjoitetut viestit.
 */
@Component
public class ChatLogger {

    /** Avain = kanavan id. Arvo = lista viesteja (kanavan lokit). */
    private HashMap<String, List<MsgToClient>> logs;

    /**
     * Konstruktori.
     */
    public ChatLogger(

    ) {
        this.logs = new HashMap<>();
    }

    /** Kirjaa lokeihin ylos viesti.
     * @param msg msg.
     */
    public final synchronized void log(final MsgToClient msg) {
        String channelId = msg.getChannelId();
        List<MsgToClient> list = logs.get(channelId);
        if (list == null) {
            list = new ArrayList<>();
            logs.put(channelId, list);
        }
        list.add(msg);
    }

    /** Getteri kanavan lokeille.
     * @param channelId kanavan id
     * @return lista msgToClient-olioita.
     */
    public final synchronized List<MsgToClient> getLogs(final String channelId) {
        List<MsgToClient> list = logs.get(channelId);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }
}

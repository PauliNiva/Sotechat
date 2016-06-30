package sotechat.service;

import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;

import sotechat.data.ChatLogger;
import sotechat.data.Mapper;
import sotechat.data.Session;
import sotechat.data.SessionRepo;
import sotechat.data.Channel;
import sotechat.wrappers.MsgToServer;
import sotechat.wrappers.QueueItem;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/** Tarjoaa palvelut jonoon lisaamiseen.
 *  Jonosta poistamiseen ja jonon tarkasteluun.
 */
@Service
public class QueueService {

    /** Jonottajia kuvaavat oliot sailotaan tanne. */
    private List<QueueItem> queue;

    /** Mapper. */
    @Autowired
    private Mapper mapper;

    /** Session Repo. */
    @Autowired
    private SessionRepo sessionRepo;

    /** Database Service. */
    @Autowired
    private DatabaseService databaseService;

    /** Chat Logger. */
    @Autowired
    private ChatLogger chatLogger;

    /** Konstruktori. */
    public QueueService() {
        this.queue = new ArrayList<>();
    }

    /** Pyynto liittya jonoon, validoitava ennen taman metodin kutsua.
     * @param request req
     * @param payload payload
     */
    public final synchronized void joinQueue(
            final HttpServletRequest request,
            final JsonObject payload
    ) {
        /** Kaivetaan requestista ja payloadista tietoja.*/
        String sessionId = request.getSession().getId();
        Session session = sessionRepo.getSessionFromSessionId(sessionId);
        String username = payload.get("username").getAsString();
        String startMessage = payload.get("startMessage").getAsString();

        /** Siirretaan tehtava overloadatulle metodille. */
        joinQueue(session, username, startMessage);
    }

    /** Overloadattu metodi joinQueue testauksen helpottamiseksi.
     * @param session palvelimelta loytyva sessio-olio
     * @param username kayttajan antama
     * @param startMsg kayttajan antama
     */
    public final synchronized void joinQueue(
            final Session session,
            final String username,
            final String startMsg
    ) {
        String userId = session.get("userId");
        String channelId = session.get("channelId");
        String category = session.get("category");

        /** Muistetaan kayttajan valitsema nimimerkki. */
        session.set("username", username);

        /** Kirjataan kayttajalle oikeus kuunnella kanavaa. */
        Channel channel = mapper.getChannel(channelId);
        channel.allowParticipation(session);

        /** Asetetaan kayttaja jonoon odottamaan palvelua. */
        QueueItem item = new QueueItem(channelId, category, username);
        queue.add(item);

        /** Asetetaan kayttajan tilaksi "jono". */
        session.set("state", "queue");

        /** Luodaan tietokantaan uusi keskustelu. */
        databaseService.createConversation(username, channelId, category);

        /** Luodaan aloitusviestista msgToServer-olio. */
        MsgToServer msg = MsgToServer.create(userId, channelId, startMsg);

        /** Kirjataan viesti lokeihin, mutta ei laheteta sita viela, koska
         * kanavalla ei ole ketaan. Kun kanavalle liittyy joku,
         * sille lahetetaan lokit. */
        chatLogger.logNewMessage(msg);
    }

    /** Suoritetaan jonosta nostaminen (oletettavasti validoitu jo).
     * @param channelId kanavaId
     * @param accessor taalta autentikaatiotiedot
     * @return String pro username, kenelle popattu kanava kuuluu
     */
    public final synchronized String popQueue(
            final String channelId,
            final SimpMessageHeaderAccessor accessor
    ) {
        Channel channel = mapper.getChannel(channelId);
        if (!removeFromQueue(channelId)) {
            /** Poppaus epaonnistui. Ehtiko joku muu popata samaan aikaan? */
            return channel.getAssignedPro();
        }

        String sessionId =  accessor.getSessionAttributes()
                .get("SPRING.SESSION.ID").toString();
        Session session = sessionRepo.getSessionFromSessionId(sessionId);

        /** Lisataan popattu kanava poppaajan kanaviin. */
        channel.allowParticipation(session);

        /** Muutetaan popattavan kanavan henkiloiden tilaa. */
        mapper.getChannel(channelId).setRegUserSessionStatesToChat();

        /** Lisätään poppaaja tietokannassa olevaan keskusteluun */
        String userId = session.get("userId");
        databaseService.addPersonToConversation(userId, channelId);

        /** Muistetaan ja palautetaan poppaajan nimi. */
        String username = session.get("username");
        channel.setAssignedPro(username);
        return username;
    }

    /** Poistaa jonosta alkion, jonka channelId sama kuin parametrissa.
     * @param channelId haettu channelId
     * @return true jos poisto onnistui, fail jos alkiota ei loytynyt.
     */
    public boolean removeFromQueue(
            final String channelId
    ) {
        /** Etsitaan jonosta oikea alkio. */
        for (int i = 0; i < queue.size(); i++) {
            QueueItem item = queue.get(i);
            if (item.getChannelId().equals(channelId)) {
                /** Loytyi, poistetaan. */
                queue.remove(i);
                return true;
            }
        }
        /** Ei loytynyt. */
        return false;
    }

    /**
     * Tyhjentaa jonon.
     */
    public void clearQueue() {
        queue.clear();
    }

    /** Palauttaa parametrina annettua kanavaid:ta vastaavaa alkiota
     * edeltavan jonon pituuden parametrina annetussa kategoriassa.
     * @param channelId kanavaid, jota vastaavaa alkiota edeltävän jonon pituus
     *                  halutaan selvittää
     * @param category aihealue, jonka alkiot otetaan laskussa mukaan
     * @return sijainti jonossa, kyseisen kategorian alla, alkaen ykkosesta.
     * jos haettua alkiota ei loydy, palauttaa -1.
     */
    public final int getPositionInQueue(
            final String channelId,
            final String category
    ) {
        int countItemsOfSameCategory = 1;
        for (int i = 0; i < queue.size(); i++) {
            QueueItem item = queue.get(i);
            if (item.getChannelId().equals(channelId)) {
                return countItemsOfSameCategory;
            }
            if (item.getCategory().equals(category)) {
                countItemsOfSameCategory++;
            }
        }
        return -1;
    }

    /** Palauttaa jonon Stringina, joka nayttaa JSON-ystavalliselta taulukolta.
     * Esim: {"jono": [{"channelId": "xyz", "category": "1", "username": "Ra"}]}
     * @return string
     */
    @Override
    public String toString() {
        StringBuilder output = new StringBuilder("{\"jono\": [");
        for (int i = 0; i < queue.size(); i++) {
            QueueItem item = queue.get(i);
            if (i > 0) {
                output.append(", ");
            }
            output.append(item.toString());
        }
        output.append("]}");
        return output.toString();
    }

    /**
     * Palauttaa jonon pituuden.
     * @return Jonon pituus
     */
    public final int getQueueLength() {
        return this.queue.size();
    }
}

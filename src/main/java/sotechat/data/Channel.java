package sotechat.data;

import java.util.HashSet;
import java.util.Set;

/** Kanavaan liittyvat tiedot keskitetty kanava-olioon. Kasitteiden selitykset:
 * - Current subscribers: juuri nyt aktiiviset WS yhteydet kanavalle.
 * - Active userIds: oikeus kuunnella ja lahettaa viesteja kanavalle.
 *      HUOM: Yllapidetaan tietoa myos Session-olioissa (jotta O(1) haut).
 * - Historic userIds: koskaan kanavalla olleet.
 */
public class Channel {

    private String channelId;
    private Set<Session> currentSubscribers;
    private Set<String> activeUserIds;
    private Set<String> historicUserIds;

    public Channel(
            final String pChannelId
    ) {
        this.channelId = pChannelId;
        currentSubscribers = new HashSet<>();
        activeUserIds = new HashSet<>();
        historicUserIds = new HashSet<>();
    }

    /** Kirjaa Channel-olioon ja Session-olioon oikeuden osallistua kanavalle.
     * @param session session-olio
     */
    public final synchronized void allowParticipation(
            final Session session
    ) {
        session.addChannel(channelId);
        String userId = session.get("userId");
        activeUserIds.add(userId);
        historicUserIds.add(userId);
    }

    /** Kirjataan subscribe ylos, seka /queue/ etta /chat/ tapauksissa.
     * @param session sessio-olio
     */
    public final synchronized void addSubscriber(
            final Session session
    ) {
        currentSubscribers.add(session);
        String userId = session.get("userId");
        activeUserIds.add(userId);
        historicUserIds.add(userId);
    }

    /** Kutsutaan, kun WS yhteys disconnectaa, seka poistu-nappia painettaessa.
     * @param session p
     */
    public final synchronized void removeSubscriber(
            final Session session
    ) {
        currentSubscribers.remove(session);
    }

    /** Kutsutaan, kun WS yhteys on ollut pitkaan disconnectissa (timeout)
     * seka poistu-nappia painettaessa.
     * @param userId p
     */
    public final synchronized void removeActiveUserId(
            final String userId
    ) {
        activeUserIds.remove(userId);
    }

    /** Palauttaa true jos annettu userId on lahiaikoina ollut kanavalla.
     * @param userId userId
     * @return true jos on lahiaikoina ollut kanavalla
     */
    public final synchronized boolean hasActiveUser(
            final String userId
    ) {
        return activeUserIds.contains(userId);
    }

    /** Palauttaa true jos annettu userId on lahiaikoina ollut kanavalla.
     * @param userId userId
     * @return true jos on lahiaikoina ollut kanavalla
     */
    public final synchronized boolean hasHistoricUser(
            final String userId
    ) {
        return historicUserIds.contains(userId);
    }

    /**
     * Getteri channelId:lle
     * @return channelId
     */
    public final synchronized String getId() {
        return this.channelId;
    }

    /** Palauttaa listan sessioita, jotka ovat subscribanneet kanavaID:lle.
     * @return lista sessioita
     */
    public final synchronized Set<Session> getCurrentSubscribers() {
        return currentSubscribers;
    }

    /** Palauttaa listan userId:ta, jotka lasketaan aktiivisiksi kanavalle.
     * Hetkeksi dropannut henkilo lasketaan aktiiviseksi timeouttiin saakka.
     * @return lista sessioita
     */
    public final synchronized Set<String> getActiveUserIds() {
        return activeUserIds;
    }

    /** Palauttaa listan userId:ta, jotka ovat joskus olleet kanavalla.
     * Kaytetaan validoidessa lokienhakupyyntoa.
     * @return lista sessioita
     */
    public final synchronized Set<String> getHistoricUserIds() {
        return historicUserIds;
    }



}

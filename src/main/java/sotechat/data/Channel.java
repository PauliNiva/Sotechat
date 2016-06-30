package sotechat.data;

import java.util.HashSet;
import java.util.Set;

/**
 * Luokka sisaltaa kanavan tiedot. Tietoa yllapidetaan myos
 * <code>Session</code>-olioissa hakuoperaatioiden nopeuttamiseksi.
 */
public class Channel {

    /**
     * Kanavatunnus.
     */
    private String channelId;

    /**
     * Kanavan tamanhetkiset tilaajat, eli kaikki kenella on aktiivinen
     * <code>WebSocket</code>-yhteys.
     */
    private Set<Session> currentSubscribers;

    /**
     * Aktiiviset kayttajatunnukset.
     */
    private Set<String> activeUserIds;

    /**
     * Historia kaikista kanavalla joskus olleista kayttajatunnuksista.
     */
    private Set<String> historicUserIds;

    /**
     * Kanavaan liittyvan ammattilaisen kayttajatunnus.
     */
    private String assignedPro;

    /**
     * Onko kanava viela kaytossa.
     */
    private boolean active;

    /**
     * Konstruktori.
     *
     * @param pChannelId Kanavatunnus.
     */
    public Channel(final String pChannelId) {
        this.channelId = pChannelId;
        currentSubscribers = new HashSet<>();
        activeUserIds = new HashSet<>();
        historicUserIds = new HashSet<>();
        this.assignedPro = "";
        this.active = true;
    }

    /**
     * Kirjaa <code>Channel</code>-olioon ja <code>Session</code>-olioon
     * oikeuden osallistua kanavalle.
     *
     * @param session <code>Session</code>-olio.
     */
    public final synchronized void allowParticipation(final Session session) {
        session.addChannel(channelId);
        String userId = session.get("userId");
        activeUserIds.add(userId);
        historicUserIds.add(userId);
    }

    /**
     * Asettaa kanavan tavallisten kayttajien tilaksi "chat".
     */
    public void setRegUserSessionStatesToChat() {
        for (Session member : getCurrentSubscribers()) {
            /** Hoitajan tilan kuuluu aina olla "pro". */
            if (!member.get("state").equals("pro")) {
                member.set("state", "chat");
            }
        }
    }


    /**
     * Kirjataan tilaus ylos "/queue/", seka "/chat/" tapauksissa.
     *
     * @param session <code>Session</code>-olio.
     */
    public synchronized void addSubscriber(final Session session) {
        currentSubscribers.add(session);
        String userId = session.get("userId");
        activeUserIds.add(userId);
        historicUserIds.add(userId);
    }

    /**
     * Kutsutaan <code>WebSocket</code>-yhteyden katketessa, seka poistu-nappia
     * painettaessa.
     *
     * @param session p.
     */
    public final synchronized void removeSubscriber(final Session session) {
        currentSubscribers.remove(session);
    }

    /**
     * Kutsutaan <code>WebSocket</code>-yhteyden ollessa pitkaan katkenneena,
     * seka poistu-nappia painettaessa.
     *
     * @param userId p.
     */
    public final synchronized void removeActiveUserId(final String userId) {
        activeUserIds.remove(userId);
    }

    /**
     * Palauttaa <code>true</code> jos annettu kayttajaId on lahiaikoina ollut
     * kanavalla.
     *
     * @param userId Kayttajan Id.
     * @return <code>true</code> jos kayttajaId on ollut lahiaikoina kanavalla.
     */
    public final synchronized boolean hasActiveUser(final String userId) {
        return activeUserIds.contains(userId);
    }

    /**
     * Palauttaa <code>true</code> jos kayttajaId on joskus ollut kanavalla.
     * @param userId kayttajaId.
     * @return <code>true</code> jos on kayttajaId on joskus ollut kanavalla.
     */
    public final synchronized boolean hasHistoricUser(final String userId) {
        return historicUserIds.contains(userId);
    }

    /**
     * Hakee kanavatunnuksen.
     *
     * @return Kanavatunnus.
     */
    public final synchronized String getId() {
        return this.channelId;
    }

    /**
     * Palauttaa listan <code>Session</code>-olioita jotka ovat tilanneet
     * kanavatunnuksen.
     *
     * @return Lista <code>Session</code>-olioita.
     */
    public final synchronized Set<Session> getCurrentSubscribers() {
        return currentSubscribers;
    }

    /**
     * Palauttaa listan kayttajaId:eita, jotka lasketaan aktiivisiksi kanavalle.
     * Hetkeksi yhteyden katkaissut tai menettänyt henkilo lasketaan
     * aktiiviseksi aikakatkaisuun saakka.
     *
     * @return Lista kayttajaId:eita.
     */
    public final synchronized Set<String> getActiveUserIds() {
        return activeUserIds;
    }

    /**
     * Palauttaa listan kayttajaId:eita, jotka ovat joskus olleet kanavalla.
     * Kaytetaan validoitaessa lokitietojenhakupyyntoa.
     *
     * @return Lista <code>Session</code>-olioita.
     */
    public final synchronized Set<String> getHistoricUserIds() {
        return historicUserIds;
    }

    /**
     * Hakee ammattilaisen kayttajanimen joka kuuluu kanavalle.
     *
     * @return Kanavan ammattilaisen kayttajanimi.
     */
    public final String getAssignedPro() {
        return assignedPro;
    }

    /**
     * Asettaa argumenttina annetun kayttajanimen kanavan ammattilaiseksi.
     *
     * @param username Ammattilaisen kayttajanimi.
     */
    public final void setAssignedPro(final String username) {
        assignedPro = username;
    }

    /**
     * Tarkistaa onko kanava aktiivinen.
     *
     * @return <code>true</code>, jos kanava on aktiivien, <code>false</code>
     * muutoin.
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Asettaa kanavan inaktiiviseksi.
     */
    public void setInactive() {
        this.active = false;
    }

}

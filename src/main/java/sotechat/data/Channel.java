package sotechat.data;

import java.util.HashSet;
import java.util.Set;

/**
 * Kanava-oliot ovat olemassa kanaviin liittyvan tiedon keskittamiseksi.
 * Miten keskeiset jasenyyskasitteet eroavat toisistaan:
 * - Current subscribers: juuri nyt aktiiviset WS yhteydet kanavalle.
 * - Active userIds: oikeus kuunnella ja lahettaa viesteja kanavalle.
 *      HUOM: Yllapidetaan tietoa myos Session-olioissa (jotta O(1) haut).
 * - Historic userIds: koskaan kanavalla olleet.
 */
public class Channel {

    /**
     * Kanavatunnus.
     */
    private String channelId;

    /**
     * Sessiot, joilla on aukioleva <code>WebSocket</code>-yhteys kanavalle.
     */
    private Set<Session> currentSubscribers;

    /**
     * Aktiiviset kayttajatunnukset tarkoittavat niita, joilla on oikeus avata
     * <code>WebSocket</code>-yhteys kanavalle.
     * Tietoa yllapidetaan myos
     * <code>Session</code>-olioissa hakuoperaatioiden nopeuttamiseksi.
     */
    private Set<String> activeUserIds;

    /**
     * Historialliset kayttajatunnukset tarkoittavat niita, jotka ovat joskus
     * olleet kanavalla. Historiallisilla ammattilaiskayttajilla on oikeus
     * hakea kanavan lokitietoja.
     */
    private Set<String> historicUserIds;

    /**
     * Kanavaan mahdollisesti liittyvan ammattilaisen <code>username</code>.
     */
    private String assignedPro;

    /**
     * False, jos kanava on suljettu.
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
     * Sallii parametrina annetun Sessionin osallistua kanavalle.
     * Tarkemmin ilmaistuna: kirjaa <code>Channel</code>-olioon ja
     * <code>Session</code>-olioon oikeuden osallistua kanavalle.
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
     * Asettaa kanavan asiakaskayttajien tilaksi "chat".
     */
    public void setRegUserSessionStatesToChat() {
        for (Session member : getCurrentSubscribers()) {
            /* Hoitajan tilan kuuluu aina olla "pro". */
            if (!member.get("state").equals("pro")) {
                member.set("state", "chat");
            }
        }
    }


    /**
     * Kirjaa uuden tilaajan ylos kanavan tietoihin.
     * Kutsuttava seka tapauksissa, joissa asiakaskayttaja liittyy jonoon
     * eli tilaa polun /toClient/queue/{kanavaId}, etta tapauksessa,
     * jossa jokin kayttaja tilaa varsinaisen kanavan /toClient/chat/{kanavaId}.
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
     * Poistaa parametrina annetun userId:n osallistumisoikeuden kanavalle.
     * Kutsutaan <code>WebSocket</code>-yhteyden ollessa pitkaan katkenneena,
     * seka poistu-nappia painettaessa.
     *
     * @param userId p.
     */
    public final synchronized void removeActiveUserId(final String userId) {
        activeUserIds.remove(userId);
    }

    /**
     * Palauttaa <code>true</code> jos annetulla userId:lla on oikeus
     * osallistua kanavalle.
     *
     * @param userId userId
     * @return <code>true</code> jos oikeus osallistua kanavalle.
     */
    public final synchronized boolean hasActiveUser(final String userId) {
        return activeUserIds.contains(userId);
    }

    /**
     * Palauttaa <code>true</code> jos kayttajaId on joskus ollut kanavalla.
     *
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
     * Palauttaa setin <code>Session</code>-olioista, joilla on
     * aukioleva <code>WebSocket</code>-yhteys kanavalle.
     *
     * @return Setti <code>Session</code>-olioita.
     */
    public final synchronized Set<Session> getCurrentSubscribers() {
        return currentSubscribers;
    }

    /**
     * Palauttaa setin <code>userId</code>:ta, joilla on oikeus
     * avata <code>WebSocket</code>-yhteys kanavalle.
     * Hetkeksi yhteyden katkaissut tai menettänyt henkilo lasketaan
     * aktiiviseksi aikakatkaisuun saakka.
     *
     * @return Setti <code>userId</code>:ta.
     */
    public final synchronized Set<String> getActiveUserIds() {
        return activeUserIds;
    }

    /**
     * Palauttaa setin <code>userId</code>:ta, jotka ovat joskus olleet
     * kanavalla. Kaytetaan validoitaessa lokitietojenhakupyyntoa.
     *
     * @return Setti <code>Session</code>-olioita.
     */
    public final synchronized Set<String> getHistoricUserIds() {
        return historicUserIds;
    }

    /**
     * Palauttaa <code>username</code>:n ammattilaiselle, jolle tama kanava
     * kuuluu, tai tyhjan merkkijonon jos kanava ei viela kuulu kenellekaan.
     *
     * @return username
     */
    public final String getAssignedPro() {
        return assignedPro;
    }

    /**
     * Asettaa argumenttina annetun <code>username</code>:n
     * kanavan ammattilaiseksi.
     *
     * @param username Ammattilaisen kayttajanimi.
     */
    public final void setAssignedPro(final String username) {
        assignedPro = username;
    }

    /**
     * Tarkistaa onko kanava aktiivinen vai suljettu.
     *
     * @return <code>true</code>, jos kanava on aktiivien, <code>false</code>
     * jos kanava on suljettu.
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Sulkee kanavan.
     */
    public void setInactive() {
        this.active = false;
    }

    /**
     * Lisaa parametrina annetun userId:n historiallisen kayttajien listalle.
     * Tarkoitettu kaytettavaksi, kun vanhoja kanavia ladataan muistiin
     * ja halutaan tietaa, kenella on oikeus niiden lokeihin.
     * @param userId userId
     */
    public void addHistoricUserId(final String userId) {
        historicUserIds.add(userId);
    }
}

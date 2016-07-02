package sotechat.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import javax.persistence.OneToMany;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;

import java.util.ArrayList;
import java.util.List;

/**
 * Luokka keskustelun metatietojen tallentamiseen tietokantaan.
 * Tasta luokasta luodaan tietokantaan <code>Conversation</code>-taulu,
 * jonka riveina ovat luokan ilmentymamuuttujat.
 */
@Entity
public class Conversation {

    /**
     * Keskustelun kanavatunnus. Toimii tietokantataulun <code>id</code>:na.
     */
    @Id
    private String channelId;

    /**
     * Keskustelun aikaleima. Ei voi olla arvoltaan <code>null</code>.
     */
    @NotNull
    private String date;

    /**
     * Keskustelun aihealue.
     */
    private String category;

    /**
     * Keskusteluun liittyvat <code>Person</code>-oliot listana. Tietokannassa
     * monesta moneen suhde, jonka avain loytyy <code>Person</code>-taulun
     * <code>conversationsOfPerson</code> rivilta.
     */
    @ManyToMany(mappedBy = "conversationsOfPerson")
    private List<Person> participantsOfConversation;

    /**
     * Keskusteluun liittyvien <code>Message</code>-oliot listana. Tietokannassa
     * yhdesta moneen suhde, jonka avain loytyy <code>Message</code>-taulun
     * <code>conversation</code> rivilta. Hakutapa on <code>EAGER</code>, eli
     * kaikki listalla olevat <code>Message</code>-oliot haetaan heti, kun
     * kyseisen listan omaava <code>Conversation</code>-olio haetaan.
     */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL,
            mappedBy = "conversation")
    private List<Message> messagesOfConversation;

    /**
     * Konstruktori. Alustaa listat keskusteluun liittyville viesteille ja
     * henkiloille.
     */
    public Conversation() {
        this.messagesOfConversation = new ArrayList<>();
        this.participantsOfConversation = new ArrayList<>();
    }

    /**
     * Konstruktori. Asettaa aikaleimaksi argumenttina annetun
     * <code>pDate</code> nimisen muuttujan ja kanavatunnukseksi
     * <code>pChannelId</code> nimisen muuttujan sekä alustaa listat
     * keskusteluun liittyville viesteille ja henkiloille.
     *
     * @param pDate Aikaleima.
     * @param pChannelId Kanavan tunnus.
     */
    public Conversation(final String pChannelId, final String pDate) {
        this.date = pDate;
        this.channelId = pChannelId;
        this.messagesOfConversation = new ArrayList<>();
        this.participantsOfConversation = new ArrayList<>();
    }

    /**
     * Palauttaa viestin aikaleiman.
     *
     * @return Aikaleima.
     */
    public final String getDate() {
        return this.date;
    }

    /**
     * Asettaa viestin aikaleiman argumenttina annetuksi aikaleimaksi.
     *
     * @param pDate Viestin aikaleima.
     */
    public final void setDate(final String pDate) {
        this.date = pDate;
    }

    /**
     * Palauttaa listan keskusteluun liittyvista <code>Person</code>-olioista.
     *
     * @return Lista <code>Person</code>-olioista, jotka on liitetty
     * keskusteluun.
     */
    public final List<Person> getParticipantsOfConversation() {
        return this.participantsOfConversation;
    }

    /**
     * Palauttaa listan keskustelun <code>Message</code>-olioista.
     *
     * @return Lista <code>Message</code>-olioista, jotka on liitetty
     * keskusteluun.
     */
    public final List<Message> getMessagesOfConversation() {
        return this.messagesOfConversation;
    }

    /**
     * Lisaa argumenttina annetun <code>Person</code>-olion keskusteluun
     * liittyvien henkiloiden listaan.
     *
     * @param pPerson <code>Person</code>-olio, joka liitetaam keskusteluun.
     */
    public final void addPersonToConversation(final Person pPerson) {
        participantsOfConversation.add(pPerson);
    }

    /**
     * Lisaa argumenttina annetun <code>Message</code>-olion keskusteluun
     * liittyvien viestien listaan.
     *
     * @param pMessage <code>Message</code>-olio, joka liitetaan keskusteluun.
     */
    public final void addMessageToConversation(final Message pMessage) {
        messagesOfConversation.add(pMessage);
    }

    /**
     * Palauttaa keskustelun aihealueen.
     *
     * @return Keskustelun aihealue.
     */
    public final String getCategory() {
        return category;
    }

    /**
     * Asettaa keskustelun aihealueeksi argumenttina annetun aihealueen.
     *
     * @param pCategory Keskustelulle annettava kategoria
     *                  <code>String</code>-oliona.
     */
    public final void setCategory(final String pCategory) {
        this.category = pCategory;
    }

    /**
     * Palauttaa keskustelun kanavatunnuksen.
     *
     * @return Palauttaa keskustelun kanavatunnuksen
     * <code>String</code>-oliona.
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * Asettaa keskustelun kanavatunnukseksi argumenttina annetun
     * kanavatunnuksen.
     *
     * @param pChannelId Kanavatunnus.
     */
    public void setChannelId(final String pChannelId) {
        this.channelId = pChannelId;
    }
}

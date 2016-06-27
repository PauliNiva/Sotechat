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
 * Luokka yksittaisen keskustelun tietojen tallentamiseen.
 */
@Entity
public class Conversation {

    /** Keskustelun kanavan id. */
    @Id
    private String channelId;

    /** Keskustelun aikaleima. */
    @NotNull
    private String date;

    /** Keskustelun aihealue. */
    private String category;

    /** Keskusteluun liittyvat henkilot. */
    @ManyToMany(mappedBy = "conversationsOfPerson")
    private List<Person> participantsOfConversation;

    /** Keskusteluun liittyvat viestit. */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL,
            mappedBy = "conversation")
    private List<Message> messagesOfConversation;

    /**
     * Konstruktorissa alustetaan keskusteluun liittyvat viestit.
     * ja henkilot
     */
    public Conversation() {
        this.messagesOfConversation = new ArrayList<>();
        this.participantsOfConversation = new ArrayList<>();
    }

    /**
     * Konstruktorissa alustetaan aikaleimaksi parametrina annettu aikaleima
     * seka kanavaid:ksi parametrina annettu kanavaid seka alustetaan
     * keskustelun viestit ja henkilot.
     * @param pDate aikaleima
     * @param pChannelId kanavan id
     */
    public Conversation(final String pChannelId, final String pDate) {
        this.date = pDate;
        this.channelId = pChannelId;
        this.messagesOfConversation = new ArrayList<>();
        this.participantsOfConversation = new ArrayList<>();
    }

    /**
     * Palauttaa viestin aikaleiman.
     * @return aikaleima
     */
    public final String getDate() {
        return this.date;
    }

    /**
     * Asettaa viestin aikaleiman parametrina annettuun aikaleimaan.
     * @param pDate viestin aikaleima
     */
    public final void setDate(final String pDate) {
        this.date = pDate;
    }

    /**
     * Palauttaa listan keskusteluun liittyvista henkiloista.
     * @return lista Person olioista, jotka on liitetty keskusteluun
     */
    public final List<Person> getParticipantsOfConversation() {
        return this.participantsOfConversation;
    }

    /**
     * Palauttaa listan keskustelun viesteista.
     * @return Lista Message olioista, jotka on liitetty keskusteluun
     */
    public final List<Message> getMessagesOfConversation() {
        return this.messagesOfConversation;
    }

    /**
     * Lisaa parametrina annetun henkilon keskusteluun.
     * @param pPerson Person olio, joka halutaan liittaa keskusteluun
     */
    public final void addPersonToConversation(final Person pPerson) {
        participantsOfConversation.add(pPerson);
    }

    /**
     * Lisaa parametrina annetun viestin keskusteluun.
     * @param pMessage Message olio, joka halutaan liittaa keskusteluun
     */
    public final void addMessageToConversation(final Message pMessage) {
        messagesOfConversation.add(pMessage);
    }

    /**
     * Palauttaa keskustelun aihealueen.
     * @return keskustelun aihealue
     */
    public final String getCategory() {
        return category;
    }

    /**
     * Asettaa keskustelun aihealueeksi parametrina annetun aihealueen.
     * @param pCategory Keskustelulle annettava kategoria Stringina.
     */
    public final void setCategory(final String pCategory) {
        this.category = pCategory;
    }

    /**
     * Palauttaa keskustelun kanavaId:n.
     * @return Palauttaa keskustelun kanavaId:n Stringina.
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * Asettaa keskustelun kanavaid:ksi parametrina annetun kanavaid:n.
     * @param pChannelId kanavaid
     */
    public void setChannelId(final String pChannelId) {
        this.channelId = pChannelId;
    }
}

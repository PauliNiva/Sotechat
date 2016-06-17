package sotechat.domain;

import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Luokka yksittaisen keskustelun tietojen tallentamiseen
 */
@Entity
public class Conversation {

    /** kanavan id */
    @Id
    private String channelId;

    /** aikaleima */
    @NotNull
    private String date;

    /** keskustelun aihealue */
    private String category;

    /** keskusteluun liittyvat henkilot */
    @ManyToMany(mappedBy = "conversationsOfPerson")
    private List<Person> participantsOfConversation;

    /** keskusteluun liittyvat viestit */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,
            mappedBy = "conversation")
    private List<Message> messagesOfConversation;

    /**
     * konstruktorissa alustetaan keskusteluun liittyvat viestit
     * ja henkilot
     */
    public Conversation() {
        this.messagesOfConversation = new ArrayList<>();
        this.participantsOfConversation = new ArrayList<>();
    }

    /**
     * Konstruktorissa alustetaan aikaleimaksi parametrina annettu aikaleima
     * seka kanavaid:ksi parametrina annettu kanavaid seka alustetaan
     * keskustelun viestit ja henkilot
     * @param date aikaleima
     * @param channelId kanavan id
     */
    public Conversation(String channelId, String date) {
        this.date = date;
        this.channelId = channelId;
        this.messagesOfConversation = new ArrayList<>();
        this.participantsOfConversation = new ArrayList<>();
    }

    /**
     * palauttaa viestin aikaleiman
     * @return aikaleima
     */
    public final String getDate() {
        return this.date;
    }

    /**
     * asettaa viestin aikaleiman parametrina annettuun aikaleimaan
     * @param pdate viestin aikaleima
     */
    public final void setDate(final String pdate) {
        this.date = pdate;
    }

    /**
     * Palauttaa listan keskusteluun liittyvista henkiloista
     * @return lista Person olioista, jotka on liitetty keskusteluun
     */
    public final List<Person> getParticipantsOfConversation() {
        return this.participantsOfConversation;
    }

    /**
     * Palauttaa listan keskustelun viesteista
     * @return Lista Message olioista, jotka on liitetty keskusteluun
     */
    public final List<Message> getMessagesOfConversation() {
        return this.messagesOfConversation;
    }

    /**
     * Lisaa parametrina annetun henkilon keskusteluun
     * @param pPerson Person olio, joka halutaan liittaa keskusteluun
     */
    public final void addPersonToConversation(final Person pPerson) {
        participantsOfConversation.add(pPerson);
    }

    /**
     * Lisaa parametrina annetun viestin keskusteluun
     * @param pMessage Message olio, joka halutaan liittaa keskusteluun
     */
    public final void addMessageToConversation(final Message pMessage) {
        messagesOfConversation.add(pMessage);
    }

    /**
     * Palauttaa keskustelun aihealueen
     * @return keskustelun aihealue
     */
    public final String getCategory() {
        return category;
    }

    /**
     * Asettaa keskustelun aihealueeksi parametrina annetun aihealueen
     * @param category
     */
    public final void setCategory(String category) {
        this.category = category;
    }

    /**
     * Palauttaa keskustelun kanavaid:n
     * @return
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * Asettaa keskustelun kanavaid:ksi parametrina annetun kanavaid:n
     * @param channelId kanavaid
     */
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}

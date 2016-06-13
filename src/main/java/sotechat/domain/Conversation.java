package sotechat.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.format.annotation.DateTimeFormat;

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
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date date;

    /** keskustelun aihealue */
    private String category;

    /** keskusteluun liittyvat henkilot */
    @ManyToMany(mappedBy = "conversationsOfPerson")
    private List<Person> participantsOfConversation;

    /** keskusteluun liittyvat viestit */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "conversation")
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
    public Conversation(Date date, String channelId) {
        this.date = date;
        this.channelId = channelId;
        this.messagesOfConversation = new ArrayList<>();
        this.participantsOfConversation = new ArrayList<>();
    }

    /**
     * palauttaa viestin aikaleiman
     * @return aikaleima
     */
    public final Date getDate() {
        return this.date;
    }

    /**
     * asettaa viestin aikaleiman parametrina annettuun aikaleimaan
     * @param pdate viestin aikaleima
     */
    public final void setDate(final Date pdate) {
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


    public final void addPersonToConversation(final Person pPerson) {
        participantsOfConversation.add(pPerson);
    }

    public final void addMessageToConversation(final Message pMessage) {
        messagesOfConversation.add(pMessage);
    }

    public final String getCategory() {
        return category;
    }

    public final void setCategory(String category) {
        this.category = category;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}

package sotechat.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Luokka
 */
@Entity
public class Conversation extends AbstractPersistable<Long> {

    @NotNull
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date date;

    private String professional;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "author")
    private List<Message> messagesOfConversation;

    public Conversation() {
        messagesOfConversation = new ArrayList<>();
    }

    public final Date getDate() {
        return this.date;
    }

    public final void setDate(final Date pdate) {
        this.date = pdate;
    }

    public final String getProfessional() {
        return this.professional;
    }

    public final void setProfessional(final String pProfessional) {
        this.professional = pProfessional;
    }

    public final List<Message> getMessagesOfConversation() {
        return this.messagesOfConversation;
    }

    public final void setMessagesOfConversation(
            final List<Message> pMessagesOfConversation) {
        this.messagesOfConversation = pMessagesOfConversation;
    }

    public final void addMessageToConversation(Message pmessage) {
        messagesOfConversation.add(pmessage);
    }
}

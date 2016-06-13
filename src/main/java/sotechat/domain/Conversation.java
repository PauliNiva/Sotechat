package sotechat.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Luokka
 */
@Entity
public class Conversation {
    @Id
    private String channelId;

    @NotNull
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date date;
    private String category;

    @ManyToMany(mappedBy = "conversationsOfPerson")
    private List<Person> participantsOfConversation;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "conversation")
    private List<Message> messagesOfConversation;

    public Conversation() {
        this.messagesOfConversation = new ArrayList<>();
        this.participantsOfConversation = new ArrayList<>();
    }

    public Conversation(Date date, String channelId) {
        this.date = date;
        this.channelId = channelId;
        this.messagesOfConversation = new ArrayList<>();
        this.participantsOfConversation = new ArrayList<>();
    }

    public final Date getDate() {
        return this.date;
    }

    public final void setDate(final Date pdate) {
        this.date = pdate;
    }

    public final List<Person> getParticipantsOfConversation() {
        return this.participantsOfConversation;
    }

    public final List<Message> getMessagesOfConversation() {
        return this.messagesOfConversation;
    }

    public final void setParticipantsOfConversation(
            final List<Person> pParticipantsOfConversation) {
        this.participantsOfConversation = pParticipantsOfConversation;
    }

    public final void setMessagesOfConversation(
            final List<Message> pMessagesOfConversation) {
        this.messagesOfConversation = pMessagesOfConversation;
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

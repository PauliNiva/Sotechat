package sotechat.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Message extends AbstractPersistable<Long> {

    @Temporal(TemporalType.DATE)
    private Date date;

    private String content;
    private String sender;

    @ManyToOne
    private Conversation conversation;

    public Message() {
    }

    public final Date getDate() {
        return this.date;
    }

    public final void setDate(final Date pdate) {
        this.date = pdate;
    }

    public final String getContent() {
        return this.content;
    }

    public final void setContent(final String pcontent) {
        this.content = pcontent;
    }

    public final String getSender() {
        return this.sender;
    }

    public final void setSender(final String psender) {
        this.sender = psender;
    }

    public final Conversation getConversation() {
        return this.conversation;
    }

    public final void setConversation(final Conversation pconversation) {
        this.conversation = pconversation;
    }

}

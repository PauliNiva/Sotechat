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
    private String author;

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

    public final String getContext() {
        return this.content;
    }

    public final void setContext(final String pContent) {
        this.content = pContent;
    }

    public final String getAuthor() {
        return this.author;
    }

    public final void setAuthor(final String pAuthor) {
        this.author = pAuthor;
    }

    public final Conversation getConversation() {
        return this.conversation;
    }

    public final void setConversation(final Conversation pConversation) {
        this.conversation = pConversation;
    }

}

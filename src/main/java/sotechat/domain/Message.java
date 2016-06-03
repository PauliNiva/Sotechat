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

    private String context;
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
        return this.context;
    }

    public final void setContext(final String pcontext) {
        this.context = pcontext;
    }

    public final String getAuthor() {
        return this.author;
    }

    public final void setAuthor(final String pauthor) {
        this.author = pauthor;
    }

    public final Conversation getConversation() {
        return this.conversation;
    }

    public final void setConversation(final Conversation pconversation) {
        this.conversation = pconversation;
    }

}

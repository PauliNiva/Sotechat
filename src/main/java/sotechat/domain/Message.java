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

    private String channelId;

    public Message() {
    }

    public Message(String sender, String content, Date date){
        this.sender = sender;
        this.content = content;
        this.date = date;
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

    public final void setContent(final String pContent) {
        this.content = pContent;
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

    public final void setConversation(final Conversation pConversation) {

        this.conversation = pConversation;
        this.channelId = pConversation.getChannelId();
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}

package sotechat.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * Luokka viestien tallentamiseen.
 */
@Entity
public class Message extends AbstractPersistable<Long> {

    /**
     * Aikaleima muodossa DateTime.toString().
     * */
    private String date;

    /** Viestin sisalto. */
    private String content;

    /** Viestin lahettaja. */
    private String sender;

    /** Keskustelu, johon viesti liittyy. */
    @ManyToOne(fetch = FetchType.LAZY)
    private Conversation conversation;

    /** Keskustelun kanavaid johon viesti liittyy. */
    private String channelId;

    /** Konstruktori. */
    public Message() {
    }

    /** Konstruktori asettaa parametreina annetut lahettajan, viestin sisallon
     * ja aikaleiman.
     * @param pSender lahettajan nimi
     * @param pContent viestin sisalto
     * @param pDate aikaleima
     */
    public Message(final String pSender, final String pContent,
                   final String pDate) {
        this.sender = pSender;
        this.content = pContent;
        this.date = pDate;
    }

    /**
     * Palauttaa viestin aikaleiman.
     * @return viestin aikaleima
     */
    public final String getDate() {
        return this.date;
    }

    /**
     * Asettaa viestin aikaleimaksi parametrina annetun ajan.
     * @param pDate viestin aikaleima
     */
    public final void setDate(final String pDate) {
        this.date = pDate;
    }

    /**
     * Palauttaa viestin sisallon.
     * @return viestin sisalto
     */
    public final String getContent() {
        return this.content;
    }

    /**
     * Asettaa viestin sisalloksi parametrina annetun tekstin.
     * @param pContent viestin sisalto
     */
    public final void setContent(final String pContent) {
        this.content = pContent;
    }

    /**
     * Palauttaa viestin lahettajan.
     * @return viestin lahettaja
     */
    public final String getSender() {
        return this.sender;
    }

    /**
     * Asettaa viestin lahettajaksi parametrina annetun kayttajanimen.
     * @param pSender viestin lahettaja
     */
    public final void setSender(final String pSender) {
        this.sender = pSender;
    }

    /**
     * Palauttaa viestin keskustelun.
     * @return viestiin liitetty Conversation olio
     */
    public final Conversation getConversation() {
        return this.conversation;
    }

    /**
     * Asettaa viestin keskusteluksi parametrina annetun keskustelun.
     * @param pConversation viestiin liittyva keskustelu
     */
    public final void setConversation(final Conversation pConversation) {

        this.conversation = pConversation;
        this.channelId = pConversation.getChannelId();
    }

    /**
     * Palauttaa viestiin liittyvan kanavaid:n.
     * @return keskustelun kanavaid
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * Asettaa keskustelun kanavaid:ksi parametrina annetun id:n.
     * @param pChannelId keskustelun kanavaid
     */
    public void setChannelId(final String pChannelId) {
        this.channelId = pChannelId;
    }
}

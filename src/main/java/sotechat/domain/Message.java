package sotechat.domain;

import javax.persistence.*;

import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * Luokka viestien tallentamiseen
 */
@Entity
public class Message extends AbstractPersistable<Long> {

    /**
     * aikaleima muodossa DateTime.toString()
     * */
    private String date;

    /** viestin sisalto */
    private String content;

    /** viestin lahettaja */
    private String sender;

    /** keskustelu, johon viesti liittyy */
    @ManyToOne(fetch = FetchType.LAZY)
    private Conversation conversation;

    /** keskustelun kanavaid johon viesti liittyy */
    private String channelId;

    /** konstruktori */
    public Message() {
    }

    /** Konstruktori asettaa parametreina annetut lahettajan, viestin sisallon
     * ja aikaleiman.
     * @param sender lahettajan nimi
     * @param content viestin sisalto
     * @param date aikaleima
     */
    public Message(String sender, String content, String date){
        this.sender = sender;
        this.content = content;
        this.date = date;
    }

    /**
     * Palauttaa viestin aikaleiman
     * @return viestin aikaleima
     */
    public final String getDate() {
        return this.date;
    }

    /**
     * Asettaa viestin aikaleimaksi parametrina annetun ajan
     * @param pdate viestin aikaleima
     */
    public final void setDate(final String pdate) {
        this.date = pdate;
    }

    /**
     * Palauttaa viestin sisallon
     * @return viestin sisalto
     */
    public final String getContent() {
        return this.content;
    }

    /**
     * Asettaa viestin sisalloksi parametrina annetun tekstin
     * @param pContent viestin sisalto
     */
    public final void setContent(final String pContent) {
        this.content = pContent;
    }

    /**
     * Palauttaa viestin lahettajan
     * @return viestin lahettaja
     */
    public final String getSender() {
        return this.sender;
    }

    /**
     * Asettaa viestin lahettajaksi parametrina annetun kayttajanimen
     * @param psender viestin lahettaja
     */
    public final void setSender(final String psender) {
        this.sender = psender;
    }

    /**
     * Palauttaa viestin keskustelun
     * @return viestiin liitetty Conversation olio
     */
    public final Conversation getConversation() {
        return this.conversation;
    }

    /**
     * Asettaa viestin keskusteluksi parametrina annetun keskustelun
     * @param pConversation viestiin liittyva keskustelu
     */
    public final void setConversation(final Conversation pConversation) {

        this.conversation = pConversation;
        this.channelId = pConversation.getChannelId();
    }

    /**
     * Palauttaa viestiin liittyvan kanavaid:n
     * @return keskustelun kanavaid
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * Asettaa keskustelun kanavaid:ksi parametrina annetun id:n
     * @param channelId keskustelun kanavaid
     */
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}

package sotechat.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * Luokka viestien tallentamiseen tietokantaan.
 */
@Entity
public class Message extends AbstractPersistable<Long> {

    /**
     * Aikaleima.
     */
    private String date;

    /**
     * Viestin sisalto.
     */
    private String content;

    /**
     * Viestin lahettaja.
     */
    private String sender;

    /**
     * Keskustelu, johon viesti liittyy. Tietokannassa monen suhde yhteen.
     * Hakutapa on <code>EAGER</code>, eli kaikki keskustelu haetaan heti, kun
     * jokin sen viesteistä haetaan.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private Conversation conversation;

    /**
     * Konstruktori.
     */
    public Message() {
    }

    /**
     * Konstruktori asettaa argumentteina annetun lahettajan, viestin sisallon
     * ja aikaleiman.
     *
     * @param pSender Lahettajan nimi.
     * @param pContent Viestin sisalto.
     * @param pDate Aikaleima.
     */
    public Message(final String pSender, final String pContent,
                   final String pDate) {
        this.sender = pSender;
        this.content = pContent;
        this.date = pDate;
    }

    /**
     * Palauttaa viestin aikaleiman.
     *
     * @return Viestin aikaleima.
     */
    public final String getDate() {
        return this.date;
    }

    /**
     * Asettaa viestin aikaleimaksi argumenttina annetun ajan.
     *
     * @param pDate Viestin aikaleima.
     */
    public final void setDate(final String pDate) {
        this.date = pDate;
    }

    /**
     * Palauttaa viestin sisallon.
     *
     * @return Viestin sisalto.
     */
    public final String getContent() {
        return this.content;
    }

    /**
     * Asettaa viestin sisalloksi parametrina annetun tekstin.
     *
     * @param pContent Viestin sisalto.
     */
    public final void setContent(final String pContent) {
        this.content = pContent;
    }

    /**
     * Palauttaa viestin lahettajan.
     *
     * @return Viestin lahettaja.
     */
    public final String getSender() {
        return this.sender;
    }

    /**
     * Asettaa viestin lahettajaksi argumenttina annetun lahettanjan.
     *
     * @param pSender Viestin lahettaja.
     */
    public final void setSender(final String pSender) {
        this.sender = pSender;
    }

    /**
     * Palauttaa keskustelun johon viesti kuuluu.
     *
     * @return Viestiin liitetty <code>Conversation</code>-olio.
     */
    public final Conversation getConversation() {
        return this.conversation;
    }

    /**
     * Asettaa viestin keskusteluksi argumenttina annetun keskustelun.
     *
     * @param pConversation Viestiin liittyva keskustelu.
     */
    public final void setConversation(final Conversation pConversation) {
        this.conversation = pConversation;
    }

}

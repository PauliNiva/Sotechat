package sotechat.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

/**
 * Luokka henkilon tietojen tallentamiseen
 */
@Entity
public class Person {

    /** henkilon id */
    @Id
    public String userId;

    /** henkilon nimimerkki joka nakyy asiakkaille */
    private String username;

    /** nimimerkki jolla henkilo voi kirjautua sisaan jarjestelmaan */
    @Column(unique = true)
    private String loginName;

    /** henkilon salasana */
    private String password;

    /** salasanan suola */
    private String salt;

    /** henkilon keskustelut */
    @ManyToMany
    private List<Conversation> conversationsOfPerson;

    /**
     * Konstruktori alustaa henkilon keskustelut
     */
    public Person() {
        this.conversationsOfPerson = new ArrayList<>();
    }

    /**
     * Konstruktori asettaa kayttajan id:ksi parametrina annetun id:n
     * ja alustaa henkilon keskustelut
     * @param pUserId String kayttajan id
     */
    public Person(String pUserId) {
        this.userId = pUserId;
        this.conversationsOfPerson = new ArrayList<>();
    }

    /**
     * Palauttaa henkilon nimimerkin, joka nakyy asiakkaille
     * @return String nimimerkki, joka nakyy asiakkaille
     */
    public final String getUserName() {
        return username;
    }

    /**
     * Asettaa parametrina annetun nimimerkin kayttajan nimimerkiksi
     * @param pname nimimerkki, joka nakyy asiakkaille
     */
    public final void setUserName(final String pname) {
        this.username = pname;
    }

    /**
     * Palauttaa kirjautumisnimen, jolla henkilo kirjautuu jarjestelmaan sisaan
     * @return kirjautumisnimi
     */
    public final String getLoginName() {
        return loginName;
    }

    /**
     * Asettaa kayttajan kirjautumisnimeksi parametrina annetun nimen
     * @param pLoginName String kirjautumisnimi
     */
    public final void setLoginName(final String pLoginName) {
        this.loginName = pLoginName;
    }

    /**
     * Palauttaa salasanan hajautusarvon
     * @return String kryptattu salasana
     */
    public final String getPassword() {
        return password;
    }

    /**
     * Asettaa salasanaksi parametrina annetun salasanan ja lisaa siihen suolan.
     * Tallentaa salasana muuttujaan salasanasta ja suolasta saadun
     * hajautusarvon.
     * @param pPassword kayttajan salasana
     */
    public final void setPassword(final String pPassword) {
        this.salt = BCrypt.gensalt();
        this.password = BCrypt.hashpw(pPassword, this.salt);
    }

    /**
     * Palauttaa salasanan suolan
     * @return String salasanan suola
     */
    public final String getSalt() {
        return salt;
    }

    /**
     * Palauttaa listan henkilon keskusteluista
     * @return List<Conversation> henkilon keskustelut
     */
    public final List<Conversation> getConversationsOfPerson() {
        return this.conversationsOfPerson;
    }

    /**
     * Liittaa parametrina annetun keskustelun (Conversation olion)
     * henkilon keskusteluihin
     * @param conversation Conversation lisattava keskustelu
     */
    public final void addConversationToPerson(
            final Conversation conversation) {
        this.conversationsOfPerson.add(conversation);
    }

    /**
     * Palauttaa kayttajan id:n
     * @return String henkilon id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Asettaa henkilon id:ksi parametrina annetun id:n
     * @param userId kayttajan id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
}

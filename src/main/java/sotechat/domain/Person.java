package sotechat.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

/**
 * Luokka henkilon tietojen tallentamiseen.
 */
@Entity
public class Person {

    /** Henkilon id . */
    @Id
    private String userId;

    /** Henkilon nimimerkki joka nakyy asiakkaille. */
    @Column(unique = true)
    private String username;

    /** Nimimerkki jolla henkilo voi kirjautua sisaan jarjestelmaan. */
    @Column(unique = true)
    private String loginName;

    /** Hajautusarvo salasanan ja suolan yhdistelmasta. */
    private String authenticationHash;

    /** Salasanan suola. */
    private String salt;

    /** Henkilon keskustelut. */
    @ManyToMany
    private List<Conversation> conversationsOfPerson;

    /** Henkilon rooli. */
    private String role;

    /**
     * Konstruktori alustaa henkilon keskustelut.
     */
    public Person() {
        this.conversationsOfPerson = new ArrayList<>();
    }

    /**
     * Konstruktori asettaa kayttajan id:ksi parametrina annetun id:n
     * ja alustaa henkilon keskustelut.
     * @param pUserId String kayttajan id
     */
    public Person(final String pUserId) {
        this.userId = pUserId;
        this.conversationsOfPerson = new ArrayList<>();
    }

    /**
     * Palauttaa henkilon nimimerkin, joka nakyy asiakkaille.
     * @return String nimimerkki, joka nakyy asiakkaille
     * TODO: dokumentoi mita palauttaa jos ei loydy
     */
    public final String getUserName() {
        return username;
    }

    /**
     * Asettaa parametrina annetun nimimerkin kayttajan nimimerkiksi.
     * @param pName nimimerkki, joka nakyy asiakkaille
     */
    public final void setUserName(final String pName) {
        this.username = pName;
    }

    /**
     * Palauttaa kirjautumisnimen, jolla henkilo kirjautuu jarjestelmaan sisaan.
     * @return kirjautumisnimi TODO: dokumentoi mita palauttaa jos ei loydy
     */
    public final String getLoginName() {
        return loginName;
    }

    /**
     * Asettaa kayttajan kirjautumisnimeksi parametrina annetun nimen.
     * @param pLoginName String kirjautumisnimi
     */
    public final void setLoginName(final String pLoginName) {
        this.loginName = pLoginName;
    }

    /** Palauttaa hajautusarvon selkokielisen salasanan ja suolan yhdistelmästä.
     * @return hajautusarvo Stringinä
     * TODO: dokumentoi mita palauttaa jos ei loydy
     */
    public final String getHashOfPasswordAndSalt() {
        return authenticationHash;
    }

    /** Tallentaa parametrina annetusta selkokielisestä salasanasta
     * hajautusarvon, jonka avulla voidaan autentikoida tulevia
     * kirjautumisia. Hajautusarvo luodaan seuraavasti: aluksi
     * lisätään selkokieliseen salasanaan suola, joka on satunnainen
     * merkkijono. Suola tallennetaan selkokielisenä tietokantaan
     * Person-olion mukana. Salasanan ja suolan yhdistelmästä luodaan
     * hajautusarvo, joka myös tallennetaan Person-olioon. Kun käyttäjä
     * myöhemmin kirjautuu, käyttäjän kirjautuessa antamaan salasanaan
     * lisätään Person-oliosta löytyvä suola, ja tästä yhdistelmästä
     * lasketaan hajautusarvo samalla algoritmillä. Saatua hajautusarvoa
     * vertaillaan Person-oliosta löytyvään hajautusarvoon ja
     * kirjautuminen sallitaan, jos ne ovat samat.
     * @param plainTextPassword selkokielinen salasana
     */
    public final void hashPasswordWithSalt(final String plainTextPassword) {
        this.salt = BCrypt.gensalt();
        this.authenticationHash = BCrypt.hashpw(plainTextPassword, this.salt);
    }

    /**
     * Palauttaa salasanan suolan.
     * @return String salasanan suola
     * TODO: dokumentoi mita palauttaa jos ei loydy
     */
    public final String getSalt() {
        return salt;
    }

    /**
     * Palauttaa listan henkilon keskusteluista.
     * @return List<Conversation> Henkilon keskustelut
     */
    public final List<Conversation> getConversationsOfPerson() {
        return this.conversationsOfPerson;
    }

    /**
     * Liittaa parametrina annetun keskustelun (Conversation olion)
     * henkilon keskusteluihin.
     * @param conversation Conversation lisattava keskustelu
     */
    public final void addConversationToPerson(
            final Conversation conversation) {
        this.conversationsOfPerson.add(conversation);
    }

    /**
     * Poistaa henkiloon liittyvan parametrina annettavan keskustelun.
     * @param conversation Poistettava keskustelu
     */
    public final void removeConversation(
            final Conversation conversation) {
        this.conversationsOfPerson.remove(conversation);
    }

    /**
     * Palauttaa kayttajan id:n.
     * @return String henkilon id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Asettaa henkilon id:ksi parametrina annetun id:n.
     * @param pUserId kayttajan id
     */
    public void setUserId(final String pUserId) {
        this.userId = pUserId;
    }

    /**
     * Palauttaa henkilon roolin.
     * @return String henkilon rooli
     */
    public final String getRole() {
        return this.role;
    }

    /**
     * Asettaa henkilon roolin.
     * @param pRole henkilon rooli merkkijonona.
     */
    public final void setRole(final String pRole) {
        this.role = pRole;
    }
}

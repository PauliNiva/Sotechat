package sotechat.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

/**
 * Luokka ammattikayttajan tallentamiseen tietokantaan.
 */
@Entity
public class Person {

    /**
     * userId, tietokantataulun primary key.
     */
    @Id
    private String userId;

    /**
     * Ammattikayttajan yksilollinen, julkinen nimimerkki,
     * joka voi olla eri kuin loginName.
     */
    @Column(unique = true)
    private String username;

    /**
     * Kirjautumisnimi, jolla henkilo kirjautuu sisaan jarjestelmaan.
     */
    @Column(unique = true)
    private String loginName;

    /**
     * Hajautusarvo salasanan ja suolan yhdistelmasta.
     */
    private String authenticationHash;

    /**
     * Salasanan suola.
     */
    private String salt;

    /**
     * Henkilon keskustelut listana. Tietokannassa monesta moneen suhde.
     */
    @ManyToMany
    private List<Conversation> conversationsOfPerson;

    /**
     * Joko ROLE_ADMIN tai ROLE_USER.
     * Role_USER viittaa ammattikayttajaan (ei asiakaskayttajaan).
     * Role_ADMIN viittaa yllapitajaan, joka voi hallinnoida ammattilaisia.
     */
    private String role;

    /**
     * Konstruktori alustaa listan henkilon keskusteluille.
     */
    public Person() {
        this.conversationsOfPerson = new ArrayList<>();
    }

    /**
     * Konstruktori asettaa kayttajan id:ksi argumenttina annetun id:n
     * ja alustaa listan henkilon keskusteluille.
     *
     * @param pUserId Kayttajan id.
     */
    public Person(final String pUserId) {
        this.userId = pUserId;
        this.conversationsOfPerson = new ArrayList<>();
    }

    /**
     * Palauttaa henkilon nimimerkin, joka nakyy asiakkaille.
     *
     * @return Nimimerkki, joka nakyy asiakkaille.
     */
    public final String getUserName() {
        return username;
    }

    /**
     * Asettaa argumenttina annetun nimimerkin kayttajan nimimerkiksi.
     *
     * @param pName Nimimerkki, joka nakyy asiakkaille.
     */
    public final void setUserName(final String pName) {
        this.username = pName;
    }

    /**
     * Palauttaa kirjautumisnimen, jolla henkilo kirjautuu jarjestelmaan sisaan.
     *
     * @return Kirjautumisnimi.
     */
    public final String getLoginName() {
        return loginName;
    }

    /**
     * Asettaa kayttajan kirjautumisnimeksi parametrina annetun nimen.
     *
     * @param pLoginName Kirjautumisnimi.
     */
    public final void setLoginName(final String pLoginName) {
        this.loginName = pLoginName;
    }

    /**
     * Palauttaa hajautusarvon selkokielisen salasanan ja suolan yhdistelmasta.
     *
     * @return Hajautusarvo merkkijonona.
     */
    public final String getHashOfPasswordAndSalt() {
        return authenticationHash;
    }

    /**
     * Luo argumenttina annetusta selkokielisesta salasanasta
     * hajautusarvon kirjautumisten tunnistautumiseen.
     * <p>
     * Aluksi selkokieliseen salasanaan lisataan suola, joka on satunnainen
     * merkkijono. Suola tallennetaan selkokielisena tietokantaan
     * <code>Person</code>-olion attribuuttina. Salasanan ja suolan
     * yhdistelmasta luodaan hajautusarvo, joka myos tallennetaan
     * <code>Person-olioon</code>.
     *
     * @param plainTextPassword Selkokielinen salasana.
     */
    public final void hashPasswordWithSalt(final String plainTextPassword) {
        this.salt = BCrypt.gensalt();
        this.authenticationHash = BCrypt.hashpw(plainTextPassword, this.salt);
    }

    /**
     * Palauttaa salasanan suolan.
     *
     * @return Salasanan suola merkkijonona.
     */
    public final String getSalt() {
        return salt;
    }

    /**
     * Palauttaa listan henkilon keskusteluista.
     *
     * @return Henkilon keskustelut listana.
     */
    public final List<Conversation> getConversationsOfPerson() {
        return this.conversationsOfPerson;
    }

    /**
     * Liittaa argumenttina annetun <code>Conversation</code>-olion
     * henkilon keskusteluihin.
     *
     * @param conversation Conversation lisattava keskustelu
     */
    public final void addConversationToPerson(
            final Conversation conversation) {
        this.conversationsOfPerson.add(conversation);
    }

    /**
     * Poistaa henkiloon liittyvan argumenttina annettavan keskustelun.
     *
     * @param conversation Poistettava keskustelu.
     */
    public final void removeConversation(
            final Conversation conversation) {
        this.conversationsOfPerson.remove(conversation);
    }

    /**
     * Palauttaa kayttajan id:n.
     *
     * @return Henkilon id.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Asettaa henkilon id:ksi argumenttina annetun id:n.
     *
     * @param pUserId Kayttajan id.
     */
    public void setUserId(final String pUserId) {
        this.userId = pUserId;
    }

    /**
     * Palauttaa henkilon roolin.
     *
     * @return Henkilon rooli merkkijonona.
     */
    public final String getRole() {
        return this.role;
    }

    /**
     * Asettaa henkilon roolin.
     *
     * @param pRole Henkilon rooli.
     */
    public final void setRole(final String pRole) {
        this.role = pRole;
    }
}

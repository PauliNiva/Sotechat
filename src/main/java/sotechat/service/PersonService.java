package sotechat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sotechat.domain.Conversation;
import sotechat.domain.Person;
import sotechat.repo.PersonRepo;

import java.util.List;

/**
 * Luokka Person-olioiden kasittelyyn (CRUD-operaatiot)
 * ja sailyttamiseen tietokannassa.
 */
@Service
public class PersonService {

    /** Person-olioita kasitteleava JPA-repositorio. */
    @Autowired
    private PersonRepo personRepo;

    /**
     * Lisaa tietokantaan uuden Person -olion ja asettaa talle parametrina
     * annetun salasanan.
     * @param person Tietokantaan lisattava Person -olio
     * @param password Henkilon salasana
     * @throws Exception Poikkeus, joka heitetaan, jos tietokantaan tallennus
     * epaonnistuu.
     */
    @Transactional
    public void addPerson(final Person person, final String password)
            throws Exception {
            person.hashPasswordWithSalt(password);
            personRepo.save(person);
    }

    /**
     * Palauttaa listan kaikista tietokannan henkiloista.
     * @return lista Person olioista, jotka on tallennettu tietokantaan
     */
    @Transactional
    public List<Person> findAll() {
        return personRepo.findAll();
    }

    /**
     * Poistaa tietokannasta parametrina annettua id:ta vastaavan henkilon.
     * @param personId henkilon tunnus
     * @throws Exception Poikkeus, joka heitetaan, jos tietokantaan tallennus
     * epaonnistuu.
     */
    @Transactional
    public void delete(final String personId) throws Exception {
        personRepo.delete(personId);
    }

    /**
     * Palauttaa parametrina annettua tunnusta vastaavan Person -olion.
     * @param personId Henkilon kayttajaId.
     * @return Tunnusta vastaava henkilo (Person olio)
     * @throws Exception Poikkeus, joka heitetaan, jos tietokantaan tallennus
     * epaonnistuu.
     */

    @Transactional
    public Person getPerson(final String personId) throws Exception {
        return personRepo.findOne(personId);
    }

    /**
     * Vaihtaa parametrina annettua tunnusta vastaavan henkilon salasanan
     * parametrina annettuun uuteen salasanaan ja tallentaa muutoksen
     * tietokantaan.
     * @param personId Henkilon id
     * @param password Uusi salasana
     * @return true, jos salasanan vaihtaminen onnistui, false jos ei
     */
    @Transactional
    public boolean changePassword(final String personId,
                                  final String password) {
        try {
            Person person = personRepo.findOne(personId);
            person.hashPasswordWithSalt(password);
            personRepo.save(person);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Vaihtaa parametrina annettua tunnusta vastaavan henkilon nimimerkin
     * parametrina annettuun nimeen ja tallentaa muutoksen tietokantaan.
     * @param personId Henkilon id
     * @param newName Uusi nimi
     * @return true, jos nimen vaihtaminen onnnistui, false, jos ei
     */
    @Transactional
    public boolean changeUserName(final String personId, final String newName) {
        try {
            Person person = personRepo.findOne(personId);
            person.setUserName(newName);
            personRepo.save(person);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Palauttaa listan kaikista henkilon keskusteluista, eli listan
     * Conversation oliota, jotka on liitetty parametrina annettua henkilon
     * id:ta vastaavaan Person olioon.
     * @param personId Henkilon id
     * @return Lista henkilon keskusteluista Conversation olioina
     * @throws Exception Poikkeus, joka heitetaan, jos tietokantaan tallennus
     * epaonnistuu.
     */
    @Transactional
    public List<Conversation> personsConversations(final String personId)
            throws Exception {
        return personRepo.findOne(personId).getConversationsOfPerson();
    }

    /**
     * Lisaa keskustelun henkilon keskusteluihin ts. Tallentaa parametrina
     * annetun Conversation -olion parametrina annettua tunnusta vastaavan
     * Person olion listaan.
     * @param personId Henkilon id
     * @param conv Lisattavaa keskustelua vastaava Conversation olio
     */
    @Transactional
    public void addConversation(final String personId,
                                final Conversation conv) {
        Person person = personRepo.findOne(personId);
        person.addConversationToPerson(conv);
        personRepo.save(person);
    }


    /**
     * Poistaa keskustelun henkilon keskusteluista.
     * @param person Person olio
     * @param conv conv
     */
    @Transactional
    public void removeConversation(final Person person,
                                   final Conversation conv) {
        person.removeConversation(conv);
        personRepo.save(person);
    }


}

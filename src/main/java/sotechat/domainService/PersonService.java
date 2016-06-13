package sotechat.domainService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sotechat.domain.Conversation;
import sotechat.domain.Person;
import sotechat.repo.ConversationRepo;
import sotechat.repo.PersonRepo;

import java.util.List;

/**
 * Luokka tietokannassa olevien Person -olioiden talllentamiseen
 * (CRUD -operaatiot)
 * Created by varkoi on 8.6.2016.
 */
@Service
public class PersonService {

    /** repositorio */
    private PersonRepo personRepo;

    /** konstruktoriin injektoidaan repositorio */
    @Autowired
    public PersonService(PersonRepo personRepo){
        this.personRepo = personRepo;
        Person person = new Person("666");
        person.setUsername("hoitaja");
        person.setPassword("salasana");
        this.personRepo.save(person);
    }

    /**
     * Lisaa tietokantaan uuden Person -olion ja asettaa talle parametrina
     * annetun salasanan.
     * @param person Tietokantaan lisattava Person -olio
     * @param password Henkilon salasana
     * @throws Exception
     */
    @Transactional
    public void addPerson(Person person, String password) throws Exception {
            person.setPassword(password);
            personRepo.save(person);
    }

    /**
     * Palauttaa listan kaikista tietokannan henkiloista.
     * @return lista Person olioista, jotka on tallennettu tietokantaan
     */
    public List<Person> findAll(){
        return personRepo.findAll();
    }

    /**
     * Poistaa tietokannasta parametrina annettua id:ta vastaavan henkilon
     * @param personId henkilon tunnus
     * @throws Exception IllegalArgumentException
     */
    @Transactional
    public void delete(String personId) throws Exception {
        personRepo.delete(personId);
    }

    /**
     * Palauttaa parametrina annettua tunnusta vastaavan Person -olion
     * @param personId henkilon tunnus
     * @return tunnusta vastaava henkilo (Person olio)
     * @throws Exception IllegalArgumentException
     */
    public Person getPerson(String personId) throws Exception {
        return personRepo.findOne(personId);
    }

    /**
     * Vaihtaa parametrina annettua tunnusta vastaavan henkilon salasanan
     * parametrina annettuun uuteen salasanaan ja tallentaa muutoksen
     * tietokantaan.
     * @param personId henkilon id
     * @param password uusi salasana
     * @return true, jos salasanan vaihtaminen onnistui, false jos ei
     */
    @Transactional
    public boolean changePassword(String personId, String password) {
        try {
            Person person = personRepo.findOne(personId);
            person.setPassword(password);
            personRepo.save(person);
            return true;
        } catch(Exception e){
            return false;
        }
    }

    /**
     * Vaihtaa parametrina annettua tunnusta vastaavan henkilon nimimerkin
     * parametrina annettuun nimeen ja tallentaa muutoksen tietokantaan.
     * @param personId henkilon id
     * @param newName uusi nimi
     * @return true, jos nimen vaihtaminen onnnistui, false, jos ei
     */
    @Transactional
    public boolean changeScreenName(String personId, String newName) {
        try {
            Person person = personRepo.findOne(personId);
            person.setScreenName(newName);
            personRepo.save(person);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    /**
     * Palauttaa listan kaikista henkilon keskusteluista, eli listan
     * Conversation oliota, jotka on liitetty parametrina annettua henkilon
     * id:ta vastaavaan Person olioon
     * @param personId henkikon id
     * @return lista henkilon keskusteluista Conversation olioina
     * @throws Exception IllegalArgumentException
     */
    public List<Conversation> personsConversations(String personId)
            throws Exception {
        return personRepo.findOne(personId).getConversationsOfPerson();
    }

    /**
     * Lisaa keskustelun henkilon keskusteluihin ts. Tallentaa parametrina
     * annetun Conversation -olion parametrina annettua tunnusta vastaavan
     * Person olion listaan.
     * @param personId henkilon id
     * @param conv lisattavaa keskustelua vastaava Conversation olio
     */
    public void addConversation(String personId, Conversation conv){
        Person person = personRepo.findOne(personId);
        person.addConversationToPerson(conv);
        personRepo.save(person);
    }

}

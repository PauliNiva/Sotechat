package sotechat.service;

import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.util.Base64Utils;

import sotechat.data.ChatLogger;
import sotechat.data.Mapper;
import sotechat.data.SessionRepo;
import sotechat.domain.Person;
import sotechat.repo.PersonRepo;

import javax.transaction.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private PersonRepo personRepo;

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private SessionRepo sessionRepo;

    @Autowired
    private Mapper mapper;

    @Autowired
    private ChatLogger chatLogger;

    @Autowired
    private QueueService queueService;

    @Autowired
    private ValidatorService validatorService;

    //TODO: Selvitä miksi viimeisimmäksi käsitelty person halutaan jättää tähän?
    private Person person;

    /**
     * Lisaa uuden ammattilaisen.
     *
     * @param encodedPersonJson merkkijono muotoa eyJ1c2Vybm...
     *        decoded personJson: {"username": mikko ... }
     *        TODO: JSONissa kuuluisi olla lainausmerkit myos mikon kohdalla
     * @return virheilmoitus Stringina tai tyhja String jos pyynto onnistui.
     */
    @Transactional
    public String addUser(final String encodedPersonJson) {
        /* Validoidaan pyynto. */
        String error = validatorService
                .validateAddUserReq(encodedPersonJson, personRepo);
        if (!error.isEmpty()) {
            /* Palautetaan virheilmoitus. */
            return error;
        }

        /* Pyynto validoitu, tallennetaan tiedot uudesta personista. */
        person = makePersonFrom(encodedPersonJson);
        person.setUserId(mapper.generateNewId());
        try {
            personRepo.save(this.person);
        } catch (Exception databaseException) {
            return "Tietokantavirhe henkilön tallennusta yrittäessä!";
        }
        mapper.mapProUsernameToUserId(person.getUserName(), person.getUserId());

        /* Palautetaan tyhja String merkiksi onnistuneesta pyynnosta. */
        return "";
    }

    @Transactional
    public String listAllPersonsAsJsonList() {
        List<Person> personList = personRepo.findAll();
        List<Person> deprecatedPersonList = new ArrayList<>();
        personList.forEach(p->deprecatedPersonList
                .add(extractInfo(p)));
        Gson gson = new Gson();
        return gson.toJson(deprecatedPersonList);
    }

    private Person extractInfo(final Person pPerson) {
        Person personWithDeprecatedAttributes = new Person();
        personWithDeprecatedAttributes.setLoginName(pPerson.getLoginName());
        personWithDeprecatedAttributes.setUserName(pPerson.getUserName());
        personWithDeprecatedAttributes.setUserId(pPerson.getUserId());
        return personWithDeprecatedAttributes;
    }

    /**
     * Poistaa ammattilaiskäyttäjän annetulla userId:lla.
     *
     * @param userId userId
     * @return virheilmoitus Stringina tai tyhja String jos pyynto onnistui.
     */
    @Transactional
    public String deleteUser(final String userId) {
        Person personToBeDeleted;
        try {
            personToBeDeleted = personRepo.findOne(userId);
        } catch (Exception e) {
            return "Tietokantavirhe hakiessa henkilöä!";
        }

        if (personToBeDeleted == null) {
            return "Käyttäjää ei löydy.";
        }
        if (personToBeDeleted.getRole().equals("ROLE_ADMIN")) {
            return "Ylläpitäjää ei voi poistaa.";
        }
        String username = personToBeDeleted.getUserName();
        mapper.removeMappingForUsername(username);
        try {
            personRepo.delete(userId);
        } catch (Exception databaseException) {
            return "Tietokantavirhe yrittäessä poistaa käyttäjää!";
        }
        sessionRepo.forgetSession(userId);

        /* TODO:
         * broker.sendClosedChannelNoticeToallChannelsOfDeletedUser
         * Merkitysta harvinaisessa tilanteessa, jossa halutaan
         * potkia ulos pro-kayttaja, jolla on aktiivisia kanavia.
         * Silloin haluttaisiin lahettaa kanaville ilmoitus. */

        return "";
    }

    /**
     * Vaihtaa salasanan.
     *
     * @param userId userId kenen salasana vaihdetaan
     * @param encodedPassword haluttu uusi salasana encodattuna
     * @return virheilmoitus Stringina tai tyhja String jos pyynto onnistui.
     */
    @Transactional
    public String changePassword(
            final String userId,
            final String encodedPassword
    ) {
        try {
            String decodedPassword = decode(encodedPassword);
            person = personRepo.findOne(userId);
            if (person == null) {
                return "Käyttäjää ei löydy.";
            }
            person.hashPasswordWithSalt(decodedPassword);
            return "";
        } catch (Exception exception) {
            return "Virhe salasanan kääntämisessä selkokieliseen muotoon"
                    + "tai hakiessa henkilöä tietokannasta!";
        }

    }

    /**
     * Tyhjentaa historian. Tarkoitettu tehtavaksi vain ennen demoamista.
     * Unohtaa aktiiviset sessiot, tyhjentaa asiakasjonon, unohtaa
     * keskustelut muistista, unohtaa keskustelut tietokannasta.
     *
     * @return virheilmoitus Stringina tai tyhja jos ei virhetta
     */
    @Transactional
    public String clearHistory() {
        sessionRepo.forgetAllSessions();
        queueService.clearQueue();
        chatLogger.removeOldMessagesFromMemory(0);
        return databaseService.removeAllConversationsFromDatabase();
    }


    /**
     * Yrittaa luoda Person-olion encoodatusta JSON-stringista.
     * HUOM: oliota ei tallenneta tietokantaan metodin sisalla.
     *
     * @param encodedPersonJson encoodattu person Json Stringina
     * @return Person-olio tai null jos virheellinen syote.
     */
    public static Person makePersonFrom(final String encodedPersonJson) {
        try {
            String decodedPersonJson = decode(encodedPersonJson);
            Gson gson = new Gson();
            Person person = gson.fromJson(decodedPersonJson, Person.class);
            person.hashPasswordWithSalt(person.getHashOfPasswordAndSalt());
            person.setRole("ROLE_USER");
            return person;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Annettuna encoodattu Stringi, palauttaa selkokielisen Stringin.
     * HUOM: Kyseessa ylimaarainen suojaus, kaikki liikenne tulisi
     * silti kuljettaa HTTPS yhteyden sisalla!
     *
     * @param encodedData String encoodattua dataa
     * @return String decoodattua dataa
     * @throws UnsupportedEncodingException jos muotoilu on vaarin
     */
    private static String decode(
            final String encodedData) throws UnsupportedEncodingException {
        return new String(Base64Utils.decodeFromString(encodedData), "UTF-8");
    }

}

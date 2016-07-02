package sotechat.service;

import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.util.Base64Utils;

import sotechat.controller.MessageBroker;
import sotechat.data.ChatLogger;
import sotechat.data.Mapper;
import sotechat.data.Session;
import sotechat.data.SessionRepo;
import sotechat.domain.Person;
import sotechat.repo.PersonRepo;

import javax.transaction.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Palvelu admin-toiminnoille.
 */
@Service
public class AdminService {

    /**
     * Sailo <code>Person</code>-olioille.
     */
    @Autowired
    private PersonRepo personRepo;

    /**
     * Palvelu tietokantatoiminnoille.
     */
    @Autowired
    private DatabaseService databaseService;

    /**
     * Sessioille repo.
     */
    @Autowired
    private SessionRepo sessionRepo;

    /**
     * Mapperi.
     */
    @Autowired
    private Mapper mapper;

    /**
     * Keskustelujen lokitietojen keraaja.
     */
    @Autowired
    private ChatLogger chatLogger;

    /**
     * Jonotuspalvelu.
     */
    @Autowired
    private QueueService queueService;

    /**
     * Palvelu validointitoiminnoille.
     */
    @Autowired
    private ValidatorService validatorService;

    /**
     * Viestinvalittaja.
     */
    @Autowired
    private MessageBroker broker;

    /**
     * Lisaa uuden ammattilaisen.
     *
     * @param encodedPersonJson merkkijono muotoa eyJ1c2Vybm...
     *        decoded personJson: {"username": mikko ... }
     * @return Virheilmoitus Stringina tai tyhja String jos pyynto onnistui.
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
        Person person = makePersonFrom(encodedPersonJson);
        person.setUserId(mapper.generateNewId());
        try {
            personRepo.save(person);
        } catch (Exception databaseException) {
            return "Tietokantavirhe henkilön tallennusta yrittäessä!";
        }
        mapper.mapProUsernameToUserId(person.getUserName(), person.getUserId());

        /* Palautetaan tyhja String merkiksi onnistuneesta pyynnosta. */
        return "";
    }

    /**
     * Listaa kaikki <code>Person</code>-oliot JSON-listana.
     *
     * @return Lista henkiloita JSON:ina.
     */
    @Transactional
    public String listAllPersonsAsJsonList() {
        List<Person> personList = personRepo.findAll();
        List<Person> deprecatedPersonList = new ArrayList<>();
        personList.forEach(p->deprecatedPersonList
                .add(extractInfo(p)));
        Gson gson = new Gson();
        return gson.toJson(deprecatedPersonList);
    }

    /**
     * Hakee argumenttina annettavasta <code>Person</code>-oliosta
     * kirjautumisnimen, kayttajanimen ja kayttajatunnuksen.
     *
     * @param pPerson <code>Person</code>-olio, josta halutaan tietoja.
     * @return Kirjautumisnimi, kayttajanimi ja kayttajatunnus listana.
     */
    private Person extractInfo(final Person pPerson) {
        Person personWithDeprecatedAttributes = new Person();
        personWithDeprecatedAttributes.setLoginName(pPerson.getLoginName());
        personWithDeprecatedAttributes.setUserName(pPerson.getUserName());
        personWithDeprecatedAttributes.setUserId(pPerson.getUserId());
        return personWithDeprecatedAttributes;
    }

    /**
     * Poistaa ammattilaiskayttajan annetulla userId:lla.
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
        Session session = sessionRepo.getSessionFromUserId(userId);
        if (session != null) {
            for (String channelId : session.getChannels()) {
            /* Tiedotetaan poistettavan kayttajan kanaville
             * kanavien sulkemisesta. */
                broker.sendClosedChannelNotice(channelId);
            }
        }
        sessionRepo.forgetSession(userId);

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
    public String changePassword(final String userId,
                                 final String encodedPassword) {
        try {
            String decodedPassword = decode(encodedPassword);
            Person person = personRepo.findOne(userId);
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
            decodedPersonJson = decodedPersonJson.replaceFirst("password",
                    "authenticationHash");
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
    private static String decode(final String encodedData)
            throws UnsupportedEncodingException {
        return new String(Base64Utils.decodeFromString(encodedData), "UTF-8");
    }

}

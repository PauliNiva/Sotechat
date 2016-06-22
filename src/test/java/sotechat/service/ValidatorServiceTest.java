package sotechat.service;

import org.junit.Before;
import org.junit.Test;
import sotechat.data.Channel;
import sotechat.data.Mapper;
import sotechat.data.Session;
import sotechat.data.SessionRepo;
import sotechat.util.MockHttpServletRequest;
import sotechat.util.MockPrincipal;
import sotechat.util.User;
import sotechat.wrappers.MsgToServer;
import static sotechat.util.Asserts.assertSuccess;
import static sotechat.util.Asserts.assertFail;

public class ValidatorServiceTest {

    ValidatorService validator;
    Mapper mapper;
    SessionRepo sessionRepo;
    Channel chanA;
    Channel chanB;
    Channel chanC;
    Channel chanD;
    User clientA;
    User clientB;
    User proA;
    User proB;

    @Before
    public void setUp() {
        /* Testattavan luokan tarvitsemat riippuvuudet. */
        mapper = new Mapper();
        sessionRepo = new SessionRepo(mapper);

        /** Testattava luokka. */
        validator = new ValidatorService(mapper, sessionRepo);

        /* Alustetaan kanavia. */
        chanA = mapper.createChannel();
        chanB = mapper.createChannel();
        chanC = mapper.createChannel();
        chanD = mapper.createChannel();

        /* Alustetaan keskustelijoita. */
        clientA = createRegUser("Henri");
        clientB = createRegUser("Mikko");
        proA = createProUser("ProA");
        proB = createProUser("ProB");

        /* Kanavalla A keskustelee ClientA ja ProA */
        chanA.allowParticipation(clientA.session);
        chanA.allowParticipation(proA.session);
        chanA.addSubscriber(clientA.session);
        chanA.addSubscriber(proA.session);

        /* Kanavalla B keskustelee Mikko ja ProB. */
        chanB.allowParticipation(clientB.session);
        chanB.allowParticipation(proB.session);
        chanB.addSubscriber(clientB.session);
        chanB.addSubscriber(proB.session);

        /* Kanavilla C ja D ei viela ole ketaan. */
    }

    /**
     *  Testataan, etta kelvollinen pyynto aktiivisen
     *  kanavan lokeihin validoituu. Validoitu pyynto
     *  palauttaa tyhjan Stringin, virheellinen virheilmon.
     */
    @Test
    public void logReqValidationActiveChatTest() {
        assertSuccess(validateLogRequest(proA, chanA));
        assertSuccess(validateLogRequest(proB, chanB));
    }

    /**
     *  Testataan, etta lokienhakupyynto validoituu
     *  myos kanaviin, joilta hoitaja on poistunut.
     */
    @Test
    public void logReqValidationHistoricChatTest() {
        leaveChannel(proB, chanB);
        assertSuccess(validateLogRequest(proB, chanB));
    }


    /**
     * Testataan, etteivat hoitajat nae muiden hoitajien lokeja.
     */
    @Test
    public void logReqValidationFailsOtherPeoplesLogsTest() {
        assertFail(validateLogRequest(proA, chanB));
        assertFail(validateLogRequest(proB, chanA));
    }

    /**
     * Historiallisilta kanavilta ei voi hakea toisten lokeja.
     */
    @Test
    public void logReqValidationFailsHistoricOtherPeoplesLogsTest() {
        leaveChannel(proA, chanA);
        assertFail(validateLogRequest(proB, chanA));
        leaveChannel(proB, chanB);
        assertFail(validateLogRequest(proB, chanA));
        assertFail(validateLogRequest(proA, chanB));
    }

    /**
     * Autentikoimattoman kayttajan pyynto hakea lokit pitaisi failata
     * seka historiallisilla etta aktiivisilla kanavilla. Myos kanavilla,
     * joilla kayttaja on, seka niilla joilla se ei ole.
     */
    @Test
    public void logReqValidationFailsUnauthenticatedUsersTest() {
        proA.principal = null;
        assertFail(validateLogRequest(proA, chanA));
        leaveChannel(proA, chanA);
        assertFail(validateLogRequest(proA, chanA));
        assertFail(validateLogRequest(proA, chanB));
    }

    /**
     * Pyynto hakea lokit epaonnistuu ilman kelvollista sessiota.
     */
    @Test
    public void logReqValidationFailsInvalidSessionTest() {
        sessionRepo.forgetSessions();
        assertFail(validateLogRequest(proA, chanA));
    }

    /**
     * Viestin validointi ok normikayttajalle.
     */
    @Test
    public void messageValidationOkForGuestTest() {
        assertSuccess(validateMsg(clientA, chanA));
        assertSuccess(validateMsg(clientB, chanB));
    }

    /**
     * Viestin validointi ok pro-kayttajalle.
     */
    @Test
    public void messageValidationOkForProTest() {
        assertSuccess(validateMsg(proA, chanA));
        assertSuccess(validateMsg(proB, chanB));
    }

    /**
     * Normikayttaja ei voi lahettaa viesteja muiden kanaville.
     */
    @Test
    public void messageValidationFailsForGuestWrongChannelTest() {
        assertFail(validateMsg(clientA, chanB)); // aktiivinen kanava
        assertFail(validateMsg(clientA, chanC)); // epa-aktiivinen kanava
    }

    /**
     * Ammattilaiskayttaja ei voi lahettaa viesteja muiden kanaville.
     */
    @Test
    public void messageValidationFailsForProWrongChannelTest() {
        assertFail(validateMsg(proA, chanB)); // aktiivinen kanava
        assertFail(validateMsg(proA, chanC)); // epa-aktiivinen kanava
    }

    /**
     * Kelvottomalla (esim. vanhentuneella) sessiolla ei voi lahettaa viesteja.
     */
    @Test
    public void messageValidationFailsWithInvalidSessionTest() {
        sessionRepo.forgetSessions();
        assertFail(validateMsg(clientA, chanA));
        assertFail(validateMsg(proA, chanA));
    }

    /**
     * Kelvottomalla userId:lla ei voi lahettaa viesteja.
     */
    @Test
    public void messageValidationFailsWithInvalidUserIdTest() {
        clientA.session.set("userId", "kelvoton");
        assertFail(validateMsg(clientA, chanA));
        proA.session.set("userId", "epakelpo");
        assertFail(validateMsg(proA, chanA));
    }

    /**
     * Ilman autentikaatiota ei voi lahettaa pro-id viesteja.
     */
    @Test
    public void messageValidationFailsWithNoPrincipalTest() {
        proA.principal = null;
        assertFail(validateMsg(proA, chanA));
    }

    /**
     * Autentikaation ja ilmoitetun userId:n pitaa vastata toisiaan.
     */
    @Test
    public void messageValidationFailsWithMismatchedIdsTest() {
        /* Asetetaan proA:n autentikoinniksi proB:n autentikointi. */
        proA.principal = proB.principal;
        assertFail(validateMsg(proA, chanA));
    }

    /**
     * Normikayttajan sallitut subscriptionit.
     */
    @Test
    public void subscriptionValidationOkForGuestTest() {
        chanC.allowParticipation(clientA.session);
        assertSuccess(validateSubscription(clientA, "queue", chanC));
        assertSuccess(validateSubscription(clientA, "chat", chanC));
    }

    /**
     * Normikayttaja ei saa subscribaa muiden kanaville.
     */
    @Test
    public void subscriptionValidationFailForGuestWrongChanTest() {
        assertFail(validateSubscription(clientB, "chat", chanA));
        assertFail(validateSubscription(clientB, "queue", chanA));
    }

    /**
     * Ammattilaiskayttaja saa subscribaa kaikille /queue/ kanaville.
     */
    @Test
    public void subscriptionValidationOkForProAnyQueueChannelTest() {
        assertSuccess(validateSubscription(proB, "queue", chanA));
        assertSuccess(validateSubscription(proB, "queue", chanB));
        assertSuccess(validateSubscription(proB, "queue", chanC));
        assertSuccess(validateSubscription(proB, "queue", chanD));
    }

    private String validateMsg(
            User user,
            Channel channel
    ) {
        String channelId = channel.getId();
        String sessionId = user.session.get("sessionId");
        String content = "Moikka!";
        String userId = user.session.get("userId");
        MockPrincipal principal = user.principal;
        MsgToServer msgToServer = MsgToServer.create(userId, channelId, content);
        return validator.isMessageFraudulent(msgToServer, sessionId, principal);
    }

    private String validateLogRequest(User user, Channel channel) {
        return validator.validateLogRequest(
                user.principal, user.req, channel.getId());
    }

    private void leaveChannel(User user, Channel channel) {
        sessionRepo.leaveChannel(channel.getId(), user.sessionId);
    }

    private String validateSubscription(
            User user,
            String channelType,
            Channel channel
    ) {
        String channelId = channel.getId();
        String sessionId = user.sessionId;
        MockPrincipal principal = user.principal;
        String channelIdWithPath = "/toClient/" + channelType + "/" + channelId;
        String error = validator.validateSubscription(
                principal, sessionId, channelIdWithPath);
        return error;
    }


    private User createRegUser(
            String name
    ) {
        MockPrincipal noPrincipal = null;
        return createUser(name, noPrincipal);
    }

    private User createProUser(
            String name
    ) {
        MockPrincipal principal = new MockPrincipal(name);
        return createUser(name, principal);
    }

    private User createUser(
            String name,
            MockPrincipal principal
    ) {
        if (principal != null) {
            mapper.mapProUsernameToUserId(name, "idFor" + name);
        }
        User user = new User();
        user.principal = principal;
        user.sessionId = "sId" + name;
        user.req = new MockHttpServletRequest(user.sessionId);
        user.session = sessionRepo.updateSession(user.req, user.principal);
        user.userId = user.session.get("userId");

        return user;
    }

}

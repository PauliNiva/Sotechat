package sotechat.service;

import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import sotechat.data.Channel;
import sotechat.data.Mapper;
import sotechat.data.SessionRepo;
import sotechat.util.MockHttpServletRequest;
import sotechat.util.MockPrincipal;
import sotechat.util.User;
import sotechat.wrappers.MsgToServer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    User clientC;
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
        clientC = createRegUser("Pekka");
        proA = createProUser("ProA");
        proB = createProUser("ProB");

        /* Kanavalla A keskustelee ClientA ja ProA */
        chanA.allowParticipation(clientA.session);
        chanA.allowParticipation(proA.session);
        chanA.addSubscriber(clientA.session);
        chanA.addSubscriber(proA.session);
        chanA.setRegUserSessionStatesToChat();

        /* Kanavalla B keskustelee Mikko ja ProB. */
        chanB.allowParticipation(clientB.session);
        chanB.allowParticipation(proB.session);
        chanB.addSubscriber(clientB.session);
        chanB.addSubscriber(proB.session);
        chanB.setRegUserSessionStatesToChat();

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

    /**
     * Ammattilaiskayttaja saa subscribaa omille /chat/ kanavilleen.
     */
    @Test
    public void subscriptionValidationOkForProChatChannelTest() {
        assertSuccess(validateSubscription(proB, "queue", chanB));
    }

    /**
     * Ammattilaiskayttaja ei saa subscribaa muiden /chat/ kanaville.
     */
    @Test
    public void subscriptionValidationFailForProOtherPeoplesChatChannelTest() {
        assertFail(validateSubscription(proB, "chat", chanA));
    }

    /**
     * Subscribe ilman kelvollista sessiosta ei kay.
     */
    @Test
    public void subscriptionValidationFailWithoutProperSessionTest() {
        sessionRepo.forgetSessions();
        assertFail(validateSubscription(proB, "queue", chanB));
        assertFail(validateSubscription(proB, "chat", chanB));
        assertFail(validateSubscription(clientB, "queue", chanB));
        assertFail(validateSubscription(proB, "chat", chanB));
    }

    /**
     * Subscribe pro-sessiolla ilman autentikaatiota ei kay.
     */
    @Test
    public void subscriptionValidationFailProSessionNoAuthTest() {
        proB.principal = null;
        assertFail(validateSubscription(proB, "queue", chanB));
        assertFail(validateSubscription(proB, "chat", chanB));
    }

    /**
     * Ammattilaiskayttaja saa kuunnella jonobroadcast (QBCC) kanavaa.
     */
    @Test
    public void subscriptionValidationOkForProsOnQBCCTest() {
        assertSuccess(validateSubscription(proA, "/toClient/QBCC"));
        assertSuccess(validateSubscription(proB, "/toClient/QBCC"));
    }

    /**
     * Estetaan subscribe muihin kuin erikseen sallittuihin osoitteisiin.
     */
    @Test
    public void subscriptionValidationFailForHackersTest() {
        assertFail(validateSubscription(clientA, "/*"));
        assertFail(validateSubscription(clientA, "/toClient/*"));
        assertFail(validateSubscription(clientA, "/toServer/*"));
        assertFail(validateSubscription(clientA, "/toClient/QBCC/"));
        assertFail(validateSubscription(clientA, "/toClient/QBCC/*"));
        assertFail(validateSubscription(clientA, "/toClient/queue/*"));
        assertFail(validateSubscription(clientA, "/toClient/chat/*"));
        assertFail(validateSubscription(clientA, "/toClient/chat/kanava"));
        assertFail(validateSubscription(clientA, "/toServer/chat/kanava"));
    }

    /**
     * Normikayttaja liittyy jonoon.
     */
    @Test
    public void joinQueueValidationOkTest() {
        assertSuccess(validateJoinQ(clientC));
    }

    /**
     * Ammattikayttaja ei voi liittya jonoon.
     */
    @Test
    public void joinQueueValidationFailForProTest() {
        assertFail(validateJoinQ(proA));
    }

    /**
     * Chatissa oleva kayttaja ei voi liittya jonoon kesken chatin.
     */
    @Test
    public void joinQueueValidationFailForAlreadyChattingUserTest() {
        assertFail(validateJoinQ(clientA));
    }

    /**
     * Jonoon ei voi liittya ammattikayttajalle varatulla nimimerkilla.
     */
    @Test
    public void joinQueueValidationFailWithReservedUsername() {
        clientC.session.set("username", "ProA");
        assertFail(validateJoinQ(clientC));
    }

    /**
     * Samalle kanavalle ei voi liittya nimimerkilla, joka on toisella
     * kanavan kayttajalla (vaikka eri kanavilla voi olla esim kaksi "Anon").
     */
    @Test
    public void joinQueueValidationFailWithTakenUsername() {
        clientC.session.set("username", clientA.session.get("username"));
        clientC.session.addChannel(clientA.session.get("channelId"));
        assertFail(validateJoinQ(clientC));
    }


    /**
     * Kanavalta poistuminen.
     */
    @Test
    public void leaveValidationOkTest() {
        assertSuccess(validateLeave(proA, chanA));
        assertSuccess(validateLeave(clientB, chanB));
    }

    /**
     * Kanavalta poistuminen, jos ei ole kanavalla.
     */
    @Test
    public void leaveValidationFailNotOnChannelTest() {
        assertFail(validateLeave(proB, chanA));
    }

    /**
     * Kanavalta poistuminen, jos sessio on epakelpo.
     */
    @Test
    public void leaveValidationFailWithBadSessionTest() {
        sessionRepo.forgetSessions();
        assertFail(validateLeave(proA, chanA));
    }

    /**
     * Kanavalta poistuminen pro-sessiolla ilman autentikointia.
     */
    @Test
    public void leaveValidationFailWithProSessionNoAuthTest() {
        proA.principal = null;
        assertFail(validateLeave(proA, chanA));
    }

    /**
     * Yksi hoitaja esittaa toista hoitajaa poistumisviestissa.
     */
    @Test
    public void leaveValidationFailWithProMismatchTest() {
        proA.session.set("username", "feikki");
        assertFail(validateLeave(proA, chanA));
    }

    private String validateLeave(User user, Channel channel) {
        boolean accepted = validator.validateLeave(
                user.sessionId, user.principal, channel.getId());
        if (accepted) return "";
        else return "error";
    }


    private String validateJoinQ(User user) {
        JsonObject payload = new JsonObject();
        payload.addProperty("username", user.session.get("username"));
        return validator.validateJoin(user.req, payload, user.principal);
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
        String channelIdWithPath = "/toClient/" + channelType + "/" + channelId;
        return validateSubscription(user, channelIdWithPath);
    }

    private String validateSubscription(User user, String path) {
        Message<?> msg = generateMessage(user, path);
        StompHeaderAccessor wrapper = StompHeaderAccessor.wrap(msg);
        return validator.validateSubscription(wrapper);
    }

    /**
     * Luo Message-olion imitoimaan subscribe-viestia, joka on lahetetty
     * WebSocketilla kayttajalta User polkuun path.
     * @param user
     * @param path
     * @return
     */
    private Message<?> generateMessage(User user, String path) {
        String sessionId = user.sessionId;
        Map<String, List<String>> nativeHeaders = new HashMap<>();
        nativeHeaders.put("id", Collections.singletonList("sub-0"));
        nativeHeaders.put("destination", Collections.singletonList(path));
        Map<String, String> springSessionMap = new HashMap<>();
        springSessionMap.put("SPRING.SESSION.ID", sessionId);
        Message<byte[]> msg = MessageBuilder.withPayload("test".getBytes())
                .setHeader("simpMessageType", SimpMessageType.SUBSCRIBE)
                .setHeader("stompCommand", StompCommand.SUBSCRIBE)
                .setHeader(NativeMessageHeaderAccessor.NATIVE_HEADERS, nativeHeaders)
                .setHeader("simpSessionAttributes", springSessionMap)
                .setHeader("simpHeartbeat", "[J@2b67932")
                .setHeader("simpSubscriptionId", "sub-0")
                .setHeader("simpSessionId", "t049m7e9")
                .setHeader("simpDestination", path)
                .setHeader("simpUser", user.principal)
                .build();
        return msg;
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
        user.session.set("username", name);
        return user;
    }

}

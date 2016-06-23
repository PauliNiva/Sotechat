package sotechat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sotechat.controller.QueueBroadcaster;
import sotechat.data.Session;
import sotechat.data.SessionRepo;

import java.util.Timer;
import java.util.TimerTask;

/**
 *TODO: Hoitajan inaktiivisuuden käsittely.
 **/

/**
 * Luokka, jonka tehtävänä on poistaa inaktiiviset käyttäjät jonosta.
 * Käyttäjiä ei poisteta heti vaan WAIT_TIME_BEFORE_SCANNING_FOR_NONEXISTENT
 * _USERS-muuttujassa määritellyn odotusajan jälkeen.
 */
@Service
public class QueueTimeoutService {

    /**
     * SessionRepo-olio, jonka avulla päästään sessioihin käsiksi niiden
     * id:iden avulla.
     */
    @Autowired
    private SessionRepo sessionRepo;

    /**
     * QueueService-olio, jonka avulla voidaan poistaa inaktiiviset
     * käyttäjät jonosta.
     */
    @Autowired
    private QueueService queueService;

    /**
     * QueueBroadCaster-olio, jonka avulla voidaan lähettää jonon tilanne
     * WebSocketia käyttäville käyttäjille.
     */
    @Autowired
    private QueueBroadcaster queueBroadcaster;

    /**
     * Muuttujaan talletettu odotusaika millisekunteina, jonka jälkeen
     * tarkistetaan käyttäjän session tilanne. Jos sessio on inaktiivinen,
     * se poistetaan jonosta.
     */
    private int waitTimeBeforeScanningForNonexistentUsers
            = 10000;

    private Timer timer;

    /**
     * Konstruktori
     */
    public QueueTimeoutService() {
        this.timer = new Timer();
    }

    /**
     * Metodi, jossa käynnistetään odotus, jonka jälkeen kutsutaan
     * removeInactiveUsersFromQueue-metodia.
     *
     * @param sessionId Annetaan parametrina sessionId, jonka perusteella
     *                  voidaan tarkistaa, onko sessio vielä aktiivinen,
     *                  vai pitääkö se poistaa jonosta.
     */
    public void initiateWaitBeforeScanningForInactiveUsers(
            final String sessionId) {
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                removeInactiveUsersFromQueue(sessionId);
            }
        }, waitTimeBeforeScanningForNonexistentUsers);
    }

    /**
     * Metodi, jossa tarkistetaan onko parametrina annetun sessionId:n omaavan
     * session status aktiivinen vai inaktiivinen. Jos se on inaktiivinen,
     * sessio poistetaan jonosta.
     *
     * @param sessionId SessionId, jonka avulla session status voidaan
     *                  selvittää ConnectionReposta.
     */
    public void removeInactiveUsersFromQueue(
            final String sessionId
    ) {
        if (this.sessionRepo.getSessionFromSessionId(sessionId) == null) {
            return;
        }

        Session userSession = this.sessionRepo
                .getSessionFromSessionId(sessionId);

        if (userSession.get("connectionStatus").equals("disconnected")) {
            System.out.println("Removing user with sessionId "
                    + sessionId + " from queue");
            String channelId = userSession.get("channelId");

            this.sessionRepo.leaveChannel(channelId, sessionId);
            this.queueService.removeFromQueue(channelId);
            this.queueBroadcaster.broadcastQueue();
        }
    }

    public void setWaitTimeBeforeScanningForNonexistentUsers(final int time) {
        this.waitTimeBeforeScanningForNonexistentUsers = time;
    }

    public int getWaitTimeBeforeScanningForNonexistentUsers() {
        return this.waitTimeBeforeScanningForNonexistentUsers;
    }

    public void setTimer(final Timer pTimer) {
        this.timer = pTimer;
    }

}
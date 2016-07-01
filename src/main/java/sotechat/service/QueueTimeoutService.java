package sotechat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sotechat.controller.QueueBroadcaster;
import sotechat.data.Session;
import sotechat.data.SessionRepo;

import java.util.Timer;
import java.util.TimerTask;

/**
 *TODO Hoitajan inaktiivisuuden kasittely.
 **/

/**
 * Tehtavana on poistaa inaktiiviset kayttajat jonosta.
 * Kayttajia ei poisteta heti vaan WAIT_TIME_BEFORE_SCANNING_FOR_NONEXISTENT
 * _USERS-muuttujassa maaritellyn odotusajan jalkeen.
 */
@Service
public class QueueTimeoutService {

    /**
     * SessionRepo-olio.
     */
    @Autowired
    private SessionRepo sessionRepo;

    /**
     * QueueService-olio.
     */
    @Autowired
    private QueueService queueService;

    /**
     * QueueBroadCaster-olio.
     */
    @Autowired
    private QueueBroadcaster queueBroadcaster;

    /**
     * Muuttujaan talletettu odotusaika millisekunteina, jonka jalkeen
     * tarkistetaan kayttajan session tilanne. Jos sessio on inaktiivinen,
     * se poistetaan jonosta. Oletuksena 5 minuuttia.
     */
    private final static int WAIT_TIME_BEFORE_SCANNING_FOR_NONEXISTENT_USERS
            = 1000 * 60 * 5;

    /**
     * Ajastinolio, jonka avulla voidaan ajastaa jonon tyhjennys tapahtumaan
     * tietyn ajan kuluttua.
     */
    private Timer timer;

    /**
     * Konstruktori.
     */
    public QueueTimeoutService() {
        this.timer = new Timer();
    }

    /**
     * Kaynnistetaan odotus, jonka jalkeen kutsutaan
     * removeInactiveUsersFromQueue-metodia.
     *
     * @param sessionId Annetaan parametrina sessionId, jonka perusteella
     *                  voidaan tarkistaa, onko sessio viela aktiivinen,
     *                  vai pitaako se poistaa jonosta.
     */
    public void initiateWaitBeforeScanningForInactiveUsers(
            final String sessionId
    ) {
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                removeInactiveUsersFromQueue(sessionId);
            }
        }, WAIT_TIME_BEFORE_SCANNING_FOR_NONEXISTENT_USERS);
    }

    /**
     * Tarkistetaan onko parametrina annetun sessionId:n omaavan
     * session status aktiivinen vai inaktiivinen. Jos se on inaktiivinen,
     * sessio poistetaan jonosta.
     *
     * @param sessionId SessionId, jonka avulla session status voidaan
     *                  selvittaa ConnectionReposta.
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

    /**
     * Testausta helpottava setteri ajastinoliolle.
     *
     * @param pTimer Parametrina annettava ajastinolio, joka annetaan
     *               QueueTimeoutServicessa oliomuuttujana olevalle ajastimelle
     *               arvoksi.
     */
    public void setTimer(final Timer pTimer) {
        this.timer = pTimer;
    }

}

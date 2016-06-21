package sotechat.connectionEvents;

import org.springframework.beans.factory.annotation.Autowired;
import sotechat.controller.QueueBroadcaster;
import sotechat.data.Session;
import sotechat.data.SessionRepo;
import sotechat.service.QueueService;

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
public class QueueTimeout {

    /**
     * ConnectionRepo-olio, josta päästään käsiksi sessioiden aktiivisuuteen,
     * siis onko käyttäjä esim. sulkenut jo selaimen, jolloin sessio on
     * muuttunut inaktiiviseksi.
     */
    @Autowired
    private ConnectionRepo connectionRepo;

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
    private static final int WAIT_TIME_BEFORE_SCANNING_FOR_NONEXISTENT_USERS
            = 10000;
 //   private final int WAIT_TIME_BEFORE_SCANNING_PRO_ACTIVITY = 2000;

    /**
     * Konstruktori
     */
    public QueueTimeout() {
    }

    /**
     * Metodi, jossa käynnistetään odotus, jonka jälkeen kutsutaan
     * removeInactiveUsersFromQueue-metodia.
     *
     * @param sessionId Annetaan parametrina sessionId, jonka perusteella
     *                  voidaan tarkistaa, onko sessio vielä aktiivinen,
     *                  vai pitääkö se poistaa jonosta.
     */
    public final void initiateWaitBeforeScanningForInactiveUsers(
            final String sessionId) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    removeInactiveUsersFromQueue(sessionId);
                }
            }, WAIT_TIME_BEFORE_SCANNING_FOR_NONEXISTENT_USERS);
    }

   /* public void initiateWaitBeforeScanningForInactiveProfessional(final
    String sessionId) {
        int delay = WAIT_TIME_BEFORE_SCANNING_PRO_ACTIVITY;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                removeProSession(sessionId);
            }
        }, delay);
    } */

    /**
     * Metodi, jossa tarkistetaan onko parametrina annetun sessionId:n omaavan
     * session status aktiivinen vai inaktiivinen. Jos se on inaktiivinen,
     * sessio poistetaan jonosta.
     *
     * @param sessionId SessionId, jonka avulla session status voidaan
     *                  selvittää ConnectionReposta.
     */
    public final void removeInactiveUsersFromQueue(final String sessionId) {
        if (!this.connectionRepo.sessionIsConnected(sessionId)) {

            Session session = this.sessionRepo.getSessionObj(sessionId);

            System.out.println("Removing user with sessionId "
                    + sessionId + " from queue");

            String channelId = session.get("channelId");
            this.sessionRepo.leaveChannel(channelId, sessionId);
            this.sessionRepo.removeSession(sessionId);
            this.queueService.removeFromQueue(channelId);
            this.queueBroadcaster.broadcastQueue();
        }
    }

 /*   public void removeProSession(String sessionId) {
        for (int i = 0; i < 10; i++) {
            System.out.println("Removing pro session");
        }
       // this.sessionRepo.removeSession(sessionId);
    } */
}

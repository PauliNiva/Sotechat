package sotechat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sotechat.controller.MessageBroker;
import sotechat.controller.QueueBroadcaster;
import sotechat.data.Session;
import sotechat.data.SessionRepo;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Poistaa kadonneet kayttajat odotteluajan jalkeen.
 */
@Service
public class TimeoutService {

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
     * Sanomien valitys.
     */
    @Autowired
    private MessageBroker broker;

    /**
     * Muuttujaan talletettu odotusaika millisekunteina, jonka jalkeen
     * tarkistetaan kayttajan session tilanne. Jos sessio on inaktiivinen,
     * se poistetaan jonosta.
     */
    private final static int WAIT_TIME_BEFORE_PROCESSING_DISCONNECT
            = 1000 * 60 * 5; // 5 minuuttia

    /**
     * Ajastin.
     */
    private Timer timer;

    /**
     * Konstruktori.
     */
    public TimeoutService() {
        timer = new Timer();
    }

    /**
     * Kaynnistetaan odotus, jonka jalkeen kutsutaan
     * removeInactiveUserFromQueue-metodia.
     *
     * @param sessionId Annetaan parametrina sessionId, jonka perusteella
     *                  voidaan tarkistaa, onko sessio viela aktiivinen,
     *                  vai pitaako se poistaa jonosta.
     */
    public void waitThenProcessDisconnect(final String sessionId) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                processDisconnect(sessionId);
            }
        }, WAIT_TIME_BEFORE_PROCESSING_DISCONNECT);
    }

    /**
     * Tarkistetaan, onko kayttaja yha poissa, ja poistetaan kayttaja jos on.
     * @param sessionId Poistettavan kayttajan sessionId.
     */
    public void processDisconnect(final String sessionId) {
        Session session = this.sessionRepo.getSessionFromSessionId(sessionId);
        if (session == null) {
            return;
        }
        if (!session.get("connectionStatus").equals("disconnected")) {
            /* Kayttaja onkin palannut. */
            return;
        }
        String userId = session.get("userId");
        for (String channelId : session.getChannels()) {
            disconnectSessionFromChannel(sessionId, channelId);
        }
        sessionRepo.forgetSession(userId);
    }

    /**
     * Katkaisee session kanavalta.
     *
     * @param sessionId Session-tunnus.
     * @param channelId Kanavatunnus.
     */
    private void disconnectSessionFromChannel(final String sessionId,
                                              final String channelId) {
        /* Poistetaan sessio kanavalta. */
        sessionRepo.leaveChannel(channelId, sessionId);

        /* Yritetaan poistaa kanavaa jonosta. */
        if (queueService.removeFromQueue(channelId)) {
            /* Jos kayttaja poistettiin jonosta, tiedotetaan jonon uusi tila. */
            queueBroadcaster.broadcastQueue();
        }

        /* Lahetetaan kanavalle tiedote kanavan sulkeutumisesta. */
        broker.sendClosedChannelNotice(channelId);
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

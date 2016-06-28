package sotechat.controller;

/** Tiedottaa jonon tilanteesta kaikille QBCC subscribanneille. */
public interface QueueBroadcaster {

    /**
     * Metodi, jonka QueueBroadcaster-rajapinnan toteuttavan luokan tulee
     * toteuttaa jonon lahettamiseksi.
     */
    void broadcastQueue();

}

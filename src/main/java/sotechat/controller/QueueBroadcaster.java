package sotechat.controller;

/**
 * Tiedottaa jonon tilanteesta kaikille QBCC tilanneille.
 */
public interface QueueBroadcaster {

    /**
     * Metodi, jonka QueueBroadcaster-rajapinnan toteuttavan luokan tulee
     * toteuttaa jonon lahettamiseksi.
     */
    void broadcastQueue();

}

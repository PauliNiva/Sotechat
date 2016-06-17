package sotechat.controller;

/** Tiedottaa jonon tilanteesta kaikille QBCC subscribanneille. */
public interface QueueBroadcaster {

    void broadcastQueue();

}

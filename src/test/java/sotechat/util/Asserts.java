package sotechat.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class Asserts {


    /** Selkeyttamaan testeja ja vahentamaan toistoa.
     * Assert: annettu virheilmoitus on tyhja. Jos ei ole tyhja, tulosta se.
     * @param error string
     */
    public static void assertSuccess(String error) {
        assertTrue(error, error.isEmpty());
    }

    /**
     * Odotetaan virheilmoituksen olevan epatyhja.
     * @param error string
     */
    public static void assertFail(String error) {
        assertFalse(error, error.isEmpty());
    }
}

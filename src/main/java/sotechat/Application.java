package sotechat;

/**
 * Luokka sovelluksen kaynnistamiseen.
 */
public class Application {

    /**
     * <code>Launcher</code>-olio.
     */
    private static Launcher launcher = new Launcher();

    /**
     * <code>main</code>-metodi, joka ajaa <code>Launcher</code>-olion
     * <code>launch</code>-metodin.
     * @param args Komentoriviargumentit.
     */
    public static void main(final String[] args) {
        launcher.launch(args);
    }

    /**
     * Asettaa sovelluksen <code>Launcher</code>-olioksi argumenttina
     * annettavan <code>Launcher</code>-olion.
     * @param plauncher asetetaan sovelluksen <code>Launcher</code>-olioksi.
     */
    static void setLauncher(final Launcher plauncher) {
        Application.launcher = plauncher;
    }

}

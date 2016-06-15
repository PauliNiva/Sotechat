package sotechat;

public class Application {

    private static Launcher launcher = new Launcher();

    /**
     * Kaynnistaa palvelimen.
     * @param args Ei kayteta argumentteja.
     */
    public static void main(final String[] args) {
        launcher.launch(args);
    }

    static void setLauncher(Launcher plauncher) {
        Application.launcher = plauncher;
    }
}

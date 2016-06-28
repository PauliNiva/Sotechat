package sotechat;

public class Application {

    private static Launcher launcher = new Launcher();

    public static void main(final String[] args) {
        launcher.launch(args);
    }

    static void setLauncher(final Launcher plauncher) {
        Application.launcher = plauncher;
    }
}

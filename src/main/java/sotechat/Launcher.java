package sotechat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Launcher {

    public ApplicationContext launch(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Launcher.class, args);
        return ctx;
    }

}

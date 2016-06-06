package sotechat.util;

import java.security.Principal;

/**
 * Luokka, jonka avulla voidaan testaamisessa simuloida rekisteroityneen
 * kayttajan kirjautumista.
 */
public class TestPrincipal implements Principal {

    private String name;

    public TestPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}

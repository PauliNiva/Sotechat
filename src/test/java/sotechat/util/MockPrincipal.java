package sotechat.util;

import java.security.Principal;

/**
 * Luokka, jonka avulla voidaan testaamisessa simuloida rekisteroityneen
 * kayttajan kirjautumista.
 */
public class MockPrincipal implements Principal {

    private String name;

    public MockPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}

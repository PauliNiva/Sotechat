package sotechat.data;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Random;
import java.security.SecureRandom;
import java.math.BigInteger;

@Component
public class MapperImpl implements Mapper {

    private HashMap<String, String> map; // map.get("491829813") => "Anon"
    private HashMap<String, String> revMap; // revMap.get("hoitaja anne") => "annenId""
    private SecureRandom random;
    private FastGeneratorForRandomStrings fastGen;

    public MapperImpl() {
        this.map = new HashMap<String, String>();
        this.revMap = new HashMap<String, String>();
        this.random = new SecureRandom();
        this.fastGen = new FastGeneratorForRandomStrings(16);

        mapUsernameToId("666", "hoitaja");
    }

    @Override
    public void mapUsernameToId(String id, String username) {
        this.map.put(id, username);
        this.revMap.put(username, id);
    }

    @Override
    public String getUsernameFromId(String id) {
        if (!map.containsKey(id)) {
            return "UNKNOWN_USERNAME";
        }
        return this.map.get(id);
    }

    @Override
    public String getIdFromRegisteredName(String registeredName) {
        if (!revMap.containsKey(registeredName)) {
            return "UNKNOWN_ID";
        }
        return this.revMap.get(registeredName);
    }

    /**
     *
     * @return
     */
    @Override
    public String generateNewId() {
        Random rng = new Random();
        while (true) {
            String userId = getFastRandomString();
            if (!map.containsKey(userId)) {
                return userId;
            }
        }
    }

    public String getSecureRandomString() {
        return "" + new BigInteger(130, random).toString(32);
    }

    public String getFastRandomString() {
        return fastGen.nextString();
    }

    /**
     * Attribution: http://stackoverflow.com/questions/
     * 41107/how-to-generate-a-random-alpha-numeric-string
     */
    private class FastGeneratorForRandomStrings {

        private final char[] symbols;
        private final Random random = new Random();
        private final char[] buf;

        public FastGeneratorForRandomStrings(int length) {
            if (length < 1)
                throw new IllegalArgumentException("length < 1: " + length);
            buf = new char[length];
            StringBuilder tmp = new StringBuilder();
            for (char ch = '0'; ch <= '9'; ++ch)
                tmp.append(ch);
            for (char ch = 'a'; ch <= 'z'; ++ch)
                tmp.append(ch);
            symbols = tmp.toString().toCharArray();
        }

        public String nextString() {
            for (int idx = 0; idx < buf.length; ++idx)
                buf[idx] = symbols[random.nextInt(symbols.length)];
            return new String(buf);
        }
    }
}

package sotechat.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MsgUtil {

    private HashSet<String> halutaanLahettaa;
    private HashMap<String, String> map;

    public MsgUtil() {
        this.halutaanLahettaa = new HashSet<>();
        this.map = new HashMap<>();
    }

    public void add(String key, String value, boolean lahetetaan) {
        if (lahetetaan) halutaanLahettaa.add(key);
        map.put(key, value);
    }

    public String mapToString() {
        String jsonString = "";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            jsonString += "\"" + entry.getKey() + "\"" + ":" + "\"" + entry.getValue() + "\"" + ",";
        }
        jsonString = "{" + jsonString.substring(0, jsonString.length()-1) + "}";
        return jsonString;
    }

    public HashSet<String> getMorkoSet() {
        return halutaanLahettaa;
    }
}

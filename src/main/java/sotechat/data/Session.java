package sotechat.data;


import java.util.HashMap;
import java.util.LinkedHashSet;

public class Session {

    private HashMap<String, String> attributes;

    private LinkedHashSet<String> channels;

    public Session() {
        attributes = new HashMap<>();
        channels = new LinkedHashSet<>();
    }

    public void set(String key, String value) {
        attributes.put(key, value);
    }

    /** Returns attribute value as String, or empty String if not found.
     * @param key key
     * @return value
     */
    public final String get(final String key) {
        String value = attributes.get(key);
        if (value == null) return "";
        return value;
    }

    public final void addChannel(final String channelId) {
        if (get("channelIds").isEmpty()) {
            /** Case: tavallinen kayttaja, jolla vain 1 kanava. */
            set("channelId", channelId);
        } else {
            /** Case: pro-kayttaja, jolla useita kanavia. */
            channels.add(channelId);
            updateChannelsAttribute();
        }
    }

    /** Remove a channel from pro user who may be on multiple channels.
     * @param channelId channelId
     */
    public void removeChannel(final String channelId) {
        channels.remove(channelId);
        updateChannelsAttribute();
    }

    /** Sets "channels" attribute in JSON friendly format.
     * eg. set("channelIds", "["kanava85", "kanava33", "kanava89"]". */
    public final void updateChannelsAttribute() {
        String output = "[";
        for (String channel : channels) {
            /** Lisataan kanavat lainausmerkeilla, peraan pilkku ja vali. */
            output += "\"" + channel + "\", ";
        }
        if (!channels.isEmpty()) {
            /** Jos kanavia oli, poistetaan viimeinen pilkku ja vali. */
            output = output.substring(0, output.length() - 2);
        }
        output += "]";
        set("channelIds", output);
    }

    /** Palauttaa true jos henkilo on kanavalla.
     * @param channelId channelId
     * @return true jos on kanavalla
     */
    public final boolean isOnChannel(final String channelId) {
        return channels.contains(channelId);
    }

}

package sotechat.data;


import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * Session-oliot kuvaamaan aktiivisia sessioita.
 * HUOM: Usea sessioId voidaan liittaa samaan Sessioon.
 */
public class Session {

    /**
     * Attribuutit, kuten "username", "userId", "sessionId".
     * Huom: asiakaskayttajalla on "channelId", pro:lla "channelIds".
     */
    private HashMap<String, String> attributes;

    /**
     * Kanavat, joille sessiolla on oikeus osallistua. Myos Channel-oliossa.
     * */
    private LinkedHashSet<String> channels;

    /**
     * Konstruktori.
     */
    public Session() {
        attributes = new HashMap<>();
        channels = new LinkedHashSet<>();
    }

    /**
     * Setteri attribuuteille. Useimpia attribuutteja voi muokata suoraan
     * taalta, mutta kanavia on tarkoitus kasitella metodien addChannel/
     * removeChannel kautta. sessionId ja userId saattaa olla myos kirjattuna
     * muualle, esim. mapperiin - eli jos niita muokkaa taalta, voi olla
     * etta vanha arvo jaa viela jonnekin muualle. (Todellista kayttotapausta
     * muokkaamiseen ei ole, tama tieto oli relevantti lahinna
     * testien kirjoittamiseen).
     * @param key Attribuutin nimi. Esimerkiksi "username".
     * @param value Attribuutin arvo. Esimerkiksi "Mikko".
     */
    public final void set(
            final String key,
            final String value
    ) {
        attributes.put(key, value);
    }

    /**
     * Palauttoo haetun attribuutin arvon, tai tyhjan Stringin jos ei loydy.
     * @param key Haettavan attribuutin nimi.
     * @return Haettavan attribuutin arvo, tai tyhja String jos ei loydy.
     */
    public final String get(final String key) {
        String value = attributes.get(key);
        if (value == null) {
            return "";
        }
        return value;
    }

    /**
     * Lisaa kanavan ja paivittaa attribuutin channelId/channelIds.
     * @param channelId Lisattavan kanavan channelId.
     */
    public final void addChannel(final String channelId) {
        channels.add(channelId);
        if (get("channelIds").isEmpty()) {
            /* Case: tavallinen kayttaja, jolla vain 1 kanava. */
            set("channelId", channelId);
        } else {
            /* Case: pro-kayttaja, jolla useita kanavia. */
            updateChannelsAttribute();
        }
    }

    /**
     * Poistaa kanavan Session-olion kanavasetista.
     * Pro-kayttajan tapauksessa myos paivittaa "channels" -attribuutin.
     * @param channelId Poistettavan kanavan channelId.
     */
    public void removeChannel(final String channelId) {
        channels.remove(channelId);
        if (isPro()) {
            updateChannelsAttribute();
        }
    }

    /**
     * Asettaa ammattilaiskayttajan "channels" attribuutin
     * JSON-ystavallisessa muotoilussa.
     * Esimerkiksi set("channelIds", "["kanava85", "kanava33", "kanava89"]".
     */
    public final void updateChannelsAttribute() {
        String output = "[";
        for (String channel : channels) {
            /* Lisataan kanavat lainausmerkeilla, peraan pilkku ja vali. */
            output += "\"" + channel + "\", ";
        }
        if (!channels.isEmpty()) {
            /* Jos kanavia oli yli 0, poistetaan viimeinen pilkku ja vali. */
            output = output.substring(0, output.length() - 2);
        }
        output += "]";
        set("channelIds", output);
    }

    /**
     * Palauttaa true jos henkilo on kanavalla.
     * @param channelId channelId
     * @return true jos on kanavalla
     */
    public final boolean hasAccessToChannel(final String channelId) {
        return channels.contains(channelId);
    }

    /**
     * Palauttaa setin kanavia.
     * @return LinkedHashSet kanavia.
     */
    public LinkedHashSet<String> getChannels() {
        return channels;
    }

    /**
     * Liittyyko sessio ammattilaiskayttajaan?
     * @return true jos liittyy
     */
    public final boolean isPro() {
        return this.get("state").equals("pro");
    }

}

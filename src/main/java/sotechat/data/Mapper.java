package sotechat.data;

/** Jotta Springin Dependency Injection toimisi,
 * Mapperilla täytyy olla oma interface.
 */
public interface Mapper {
    void mapUsernameToId(String id, String username);
    String getUsernameFromId(String id);
    String getIdFromRegisteredName(String registeredName);
    String generateNewId();
    boolean isUserIdMapped(String id);
}

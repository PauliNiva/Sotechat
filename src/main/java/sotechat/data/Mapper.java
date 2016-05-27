package sotechat.data;

public interface Mapper {
    void mapUsernameToId(String id, String username);
    String getUsernameFromId(String id);
    String getIdFromRegisteredName(String registeredName);
    String generateNewId();
}

package sotechat.data;

import sotechat.service.DatabaseService;


public interface Mapper {
    Channel getChannel(
            final String channelId
    );

    Channel createChannel();

    void forgetChannel(final String channelId);

    void mapProUsernameToUserId(
            final String username,
            final String userId
    );

    void reserveId(
            final String someId
    );

    void removeMappingForUsername(
            final String username
    );

    boolean isUsernameReserved(
            final String username
    );

    String getIdFromRegisteredName(
            final String username
    );

    String generateNewId();

    String getFastRandomString();

    void setDatabaseService(
            final DatabaseService databaseService
    );
}

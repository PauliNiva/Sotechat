package sotechat.data;

import org.apache.velocity.tools.config.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sotechat.service.DatabaseService;
import sotechat.wrappers.MsgToClient;


import java.util.*;

public interface Mapper {
    public Channel getChannel(
            final String channelId
    );
    public Channel createChannel();
    public void forgetChannel(final String channelId);
    public void mapProUsernameToUserId(
            final String username,
            final String userId
    );
    public void reserveId(
            final String someId
    );
    public void removeMappingForUsername(
            final String username
    );
    public boolean isUsernameReserved(
            final String username
    );
    public String getIdFromRegisteredName(
            final String username
    );
    public String generateNewId();
    public String getFastRandomString();
    public void setDatabaseService(
            final DatabaseService databaseService
    );
}
package sotechat.domain;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class MessageTest {

    private Message message;
    private Conversation conversation;
    private String date;

    @Before
    public void setUp() {
        message = new Message();
        date = new DateTime().toString();
        conversation = new Conversation();
    }

    @Test
    public void setAndGetContextTest() {
        message.setContent("This is a message");
        Assert.assertEquals("This is a message", message.getContent());
    }

    @Test
    public void setAndGetAuthorTest() {
        message.setSender("Shakespeare");
        Assert.assertEquals("Shakespeare", message.getSender());
    }

    @Test
    public void setAndGetConversationTest() {
        conversation.addMessageToConversation(message);
        message.setConversation(conversation);
        Assert.assertEquals(message.getConversation(), conversation);
    }

    @Test
    public void getAndSetDate() {
        message.setDate(date);
        Assert.assertNotNull(message.getDate());
    }

    @Test
    public void getChannelIdTest() {
        message.setChannelId("666");
        Assert.assertEquals("666", message.getChannelId());
    }
}

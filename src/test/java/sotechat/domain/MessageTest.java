package sotechat.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class MessageTest {

    private Message message;
    private Conversation conversation;
    private Date date;

    @Before
    public void setUp() {
        message = new Message();
        date = new Date();
        conversation = new Conversation();
    }

    @Test
    public void setAndGetContextTest() {
        message.setContext("This is a message");
        Assert.assertEquals("This is a message", message.getContext());
    }

    @Test
    public void setAndGetAuthorTest() {
        message.setAuthor("Shakespeare");
        Assert.assertEquals("Shakespeare", message.getAuthor());
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
}

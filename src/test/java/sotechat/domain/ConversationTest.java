package sotechat.domain;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

public class ConversationTest {

    private Conversation conversation;
    private Message message1;
    private Message message2;
    private List<Message> messagesOfConversation;
    private Date date;

    @Before
    public void setUp() {
        conversation = new Conversation();
        message1 = new Message();
        message1.setContext("Context");
        message2 = new Message();
        date = new Date();
    }

    /**
     * TODO päivitä testi
     */
  /*  @Test
    public void getAndSetProfessional() {
        conversation.setProfessional("Iluap");
        Assert.assertEquals("Iluap", conversation.getProfessional());
    }*/

    @Test
    public void addAndGetAndSetMessagesOfConversation() {
        Assert.assertEquals(conversation.getMessagesOfConversation().size(), 0);
        conversation.addMessageToConversation(message1);
        Assert.assertEquals(conversation.getMessagesOfConversation().size(), 1);
        messagesOfConversation = conversation.getMessagesOfConversation();
        message2 = messagesOfConversation.get(0);
        conversation.addMessageToConversation(message2);
        Assert.assertEquals(conversation.getMessagesOfConversation().size(), 2);
        messagesOfConversation.remove(1);
        conversation.setMessagesOfConversation(messagesOfConversation);
        Assert.assertEquals(conversation.getMessagesOfConversation().size(), 1);
    }

    @Test
    public void getAndSetDate() {
        conversation.setDate(date);
        Assert.assertNotNull(conversation.getDate());
    }

}

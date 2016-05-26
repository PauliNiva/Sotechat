
description 'As a user I want to see the messages other people have sent to discussion'

scenario "user can view a message the other party has sent to the discussion", {
    given 'two persons have accessed the chat window'
    when 'the other person sends a message'
    then 'user can view it in the chat window'
}

scenario "user can view all messages the other party has sent in a time order", {
    given 'two persons have accessed the chat window'
    when 'the other person sends multiple messages'
    then 'user can view all the messages in a time order'
}
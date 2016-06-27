angular.module('chatProApp').component('historyConversationComponent', {
    templateUrl: 'proHistories/conversation.html',
    controller: 'historiesConversationController',
    bindings: {
        channel: '@', //Välitetään kanavan ID controllerille
        myname: '@',
        init: '&'
    }
});
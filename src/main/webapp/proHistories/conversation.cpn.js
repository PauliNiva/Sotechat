/**
 * Luodaan komponentti yhden keskustelun viesteistä.
 */
angular.module('chatProApp').component('historyConversationComponent', {
    templateUrl: 'proHistories/conversation.html',
    controller: 'historiesConversationController',
    bindings: {
        channel: '@', 
        myname: '@',
        init: '&'
    }
});
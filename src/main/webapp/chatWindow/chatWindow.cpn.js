/**
 * Luodaan chatWindow templatea kayttaen erilliset komponentit clientille,
 * kuten myos ammattilaiselle.
 */
angular.module('chatApp').component('chatWindowComponent', {
    templateUrl: 'chatWindow/chatWindow.tpl.html',
    controller: 'chatController'
});

angular.module('chatProApp').component('proChatWindowComponent', {
    templateUrl: 'chatWindow/chatWindow.tpl.html',
    controller: 'proChatController',
    bindings: {
        channel: '@',
        chatend: '&'  
    }
});
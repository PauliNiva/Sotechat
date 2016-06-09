/**
 * Luodaan chatWindow templatea käyttäen erilliset komponentit clientille
 * Ja ammattilaiselle
 */
angular.module('chatApp').component('chatWindowComponent', {
    templateUrl: 'chatWindow/chatWindow.tpl.html',
    controller: 'chatController'
});

angular.module('chatApp').component('proChatWindowComponent', {
    templateUrl: 'chatWindow/chatWindow.tpl.html',
    controller: 'proChatController',
    bindings: {
        channel: '@' //Välitetään kanavan ID controllerille
    }
});
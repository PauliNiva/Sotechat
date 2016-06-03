angular.module('chatApp').component('chatWindowComponent', {
    templateUrl: 'chatWindow/chatWindow.tpl.html',
    controller: 'chatController'
});

angular.module('chatApp').component('proChatWindowComponent', {
    templateUrl: 'chatWindow/chatWindow.tpl.html',
    controller: 'proChatController',
    bindings: {
        channel: '@'
    }
});
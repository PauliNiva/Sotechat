var chatApp = angular.module('chatApp', ['luegg.directives']);

chatApp.controller('chatController', function ($scope, ChatService) {
    $scope.messages = [];
    this.chatName = "Esimerkki chat"

    $scope.sendMessage = function () {
        if ($scope.messageForm.$valid) {
            ChatService.send($scope.message);
            $scope.message = "";
        }
    };

    ChatService.receive().then(null, null, function (message) {
        $scope.messages.push(message);
    });

    var init = function () {
        var message = [];
        message.message = "Hei, tervetuloa .. Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh";
        message.time = Date.now();
        message.sender = "Ammattilainen";
        message.I = false;
        $scope.messages.push(message);
    };

    init();
});

chatApp.service("ChatService", function ($q, $timeout) {

    var service = {}, listener = $q.defer(), socket = {
        client: null,
        stomp: null
    }, messageIds = [];

    service.RECONNECT_TIMEOUT = 30000;

    service.receive = function () {
        return listener.promise;
    };

    service.send = function (text) {
        var id = Math.floor(Math.random() * 1000000);
        socket.stomp.send("/toServer/" + service.channelId, {}, JSON.stringify({
            'userId': service.userId,
            'channelId': service.channelId,
            'content': text
        }));
        messageIds.push(id);
    };

    var reconnect = function () {
        $timeout(function () {
            initialize();
        }, this.RECONNECT_TIMEOUT);
    };

    var getMessage = function (data) {
        var parsed = JSON.parse(data);
        var message = [];
        message.message = parsed.content;
        message.time = parsed.timeStamp;
        message.id = Math.floor(Math.random() * 1000000); // <-- mihin tämä on?
        message.sender = parsed.userName;
        //TODO: If author == self then Message.I = true
        message.I = true;
        return message;
    };

    var startListener = function ($scope) {
        socket.stomp.subscribe('/toClient/' + service.channelId, function (data) {
            console.log(JSON.parse(data.body));
            listener.notify(getMessage(data.body));
        });
    };

    var initialize = function () {
        $.get("/join", function (data) {
            service.channelId = data.channelId;
            service.userId = data.userId;
            socket.client = new SockJS('/toServer');
            socket.stomp = Stomp.over(socket.client);
            socket.stomp.connect({}, startListener);
            socket.stomp.onclose = reconnect;
        });
    };
    initialize();
    return service;
});

chatApp.directive('ngEnter', function () {
    return function (scope, element, attrs) {
        element.bind("keydown keypress", function (event) {
            if (event.which === 13 && !event.shiftKey) {
                scope.$apply(function () {
                    scope.$eval(attrs.ngEnter);
                });
                event.preventDefault();
            }
        });
    };
});




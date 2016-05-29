/** Service hoitaa tietoliikenteen
 *   (over STOMP over Websockets over TCP/IP) */
angular.module('chatApp')
    .factory('stompSocket', ['$rootScope', function ($rootScope) {
        var stompClient;
        var socket = {
            init: init,
            connect: connect,
            subscribe: subscribe,
            send: send
        };

        function init(url) {
            stompClient = Stomp.over(new SockJS(url));
        };

         function connect(successCallback, errorCallback) {
            stompClient.connect({}, function (frame) {
                $rootScope.$apply(function () {
                    successCallback(frame);
                });
            }, function (error) {
                $rootScope.$apply(function () {
                    errorCallback(error);
                });
            });
        };

        function subscribe(destination, callback) {
            stompClient.subscribe(destination, function (message) {
                $rootScope.$apply(function () {
                    callback(message);
                });
            });
        };

        function send(destination, headers, object) {
            stompClient.send(destination, headers, object);
        };

        return socket;
    }]);
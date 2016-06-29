/** Service hoitaa tietoliikenteen
 *  (over STOMP over Websockets over TCP/IP) */
angular.module('commonMod')
    .factory('stompSocket', ['$rootScope', function ($rootScope) {
        var stompClient;

        function init(url) {
            stompClient = Stomp.over(new SockJS(url));
        }

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
        }

        function subscribe(destination, callback) {
           return stompClient.subscribe(destination, function (message) {
                $rootScope.$apply(function () {
                    callback(message);
                });
            });
        }
        

        function send(destination, headers, object) {
            stompClient.send(destination, headers, object);
        }

        var socket = {
            init: init,
            connect: connect,
            subscribe: subscribe,
            send: send
        };

        return socket;
    }]);
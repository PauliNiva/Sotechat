angular.module('chatApp')
    .service('connectToServer', ['stompSocket', '$timeout', function (stompSocket, $timeout) {
        var WEBSOCKETURL = '/toServer';
        var status = false;
        var socket = {
            connect: connect,
            subscribe: subscribe,
        };

        function connect(answer) {
            if (!status) {
                stompSocket.init(WEBSOCKETURL);
                stompSocket.connect(function (frame) {
                    status = true;
                    answer();
                }, function (error) {
                    status = false;
                    $timeout(function() {
                        connect(answer);
                    }, 5000);
                });
            } else {
                answer();
            }
        };

        function subscribe(destination, answerFunction) {
            return stompSocket.subscribe(destination, function (response) {
                answerFunction(response);
            });
        }

        return socket;
    }]);
angular.module('chatApp')
    .service('connectToServer', ['stompSocket', function (stompSocket) {
        var status = false;
        var socket = {
            connect: connect,
            subscribe: subscribe,
        };

        function connect(channelID, answer) {
            if (!status) {
            stompSocket.init('/toServer');
            stompSocket.connect(function (frame) {
                status = true;
                answer();
            }, function (error) {
                
                status = false;
            });
        } else  {
                answer();
            }};
        
        function subscribe(destination, answerFunction) {
            return stompSocket.subscribe(destination, function (response) {
                answerFunction(response);
            });
        }
        
        return socket;
    }]);
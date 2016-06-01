angular.module('chatApp')
    .service('connectToServer', ['stompSocket', function (stompSocket) {
        var stompClient;
        var socket = {
            connect: connect
        };

        function connect(channelID, answer) {
            stompSocket.init('/toServer');
            stompSocket.connect(function (frame) {
                stompSocket.subscribe("/toClient/" + channelID, function (response) {
                    answer(response);
                });
            }, function (error) {

            });
        };

        
        return socket;
    }]);
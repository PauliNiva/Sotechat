angular.module('chatApp')
    .controller('proChatController', ['$scope', 'stompSocket', 'connectToServer', 'proStateService',
        function ($scope, stompSocket, connectToServer, proStateService) {
            $scope.pro = true;
            // Taulukko "messages" sisaltaa chat-ikkunassa nakyvat viestit.
            $scope.messages = [];
            var sub;
            // Maaritellaan chatin nimi templateen, talla hetkella kovakoodattu
            $scope.chatName = 'Esimerkki';

            var channel = this.channel;

            /** Funktio lahettaa servicen avulla tekstikentan
             *  sisallon ja lopuksi tyhjentaa tekstikentan. */
            $scope.sendMessage = function () {
                if ($scope.messageForm.$valid) {
                    var destination = "/toServer/chat/" + channel;
                    stompSocket.send(destination, {}, JSON.stringify(
                        {
                            'userId': proStateService.getUserID(),
                            'channelId': channel,
                            'content': $scope.message
                        }));
                    $scope.message = '';
                }
            };

            /** Funktio parsee viestin haluttuun muotoon. */
            var getMessage = function (data) {
                var parsed = JSON.parse(data);
                var message = [];
                message.content = parsed.content;
                message.time = parsed.timeStamp;
                message.sender = parsed.username;
                message.I = message.sender === proStateService.getUsername();
                return message;
            };

            var subscribe = function () {
                sub = connectToServer.subscribe('/toClient/chat/' + channel, function (response) {
                    $scope.messages.push(getMessage(response.body));
                });
            };

            subscribe();
        }]);

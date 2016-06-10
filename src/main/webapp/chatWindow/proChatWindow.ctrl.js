angular.module('chatApp')
    .controller('proChatController', ['$scope', 'stompSocket', 'connectToServer', 'proStateService',
        function ($scope, stompSocket, connectToServer, proStateService) {
            $scope.pro = true;
            // Taulukko "messages" sisaltaa chat-ikkunassa nakyvat viestit.
            $scope.messages = [];
            var sub;
            // Maaritellaan chatin nimi templateen, talla hetkella kovakoodattu
            $scope.chatName = 'Chat';

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
                var message = JSON.parse(data);
                message.I = message.username === proStateService.getUsername();
                if (!message.I){
                    $scope.chatName = message.username;
                }
                return message;
            };

            var subscribe = function () {
                sub = connectToServer.subscribe('/toClient/chat/' + channel, function (response) {
                    if (response.body != '$CLEAR$') {
                        $scope.messages.push(getMessage(response.body));
                    } else  {
                        $scope.messages.length = 0;
                    }
                });
            };

            subscribe();
        }]);

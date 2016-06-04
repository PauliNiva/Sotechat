angular.module('chatApp')
    .controller('proChatController', ['$scope', 'stompSocket', 'connectToServer', 'proStateService',
        function ($scope, stompSocket, connectToServer, proStateService) {
            $scope.pro = true;
            // Taulukko "messages" sisältää chat-ikkunassa näkyvät viestit.
            $scope.messages = [];
            var sub;
            // Määritellään chatin nimi templateen, tällä hetkellä kovakoodattu
            $scope.chatName = 'Esimerkki';

            var channel = this.channel;

            /** Funktio lähettää servicen avulla tekstikentän
             *  sisällön ja lopuksi tyhjentää tekstikentän. */
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

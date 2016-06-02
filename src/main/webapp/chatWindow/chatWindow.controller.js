// Kontrolleri päivittää tietoja molempiin suuntiin:
// - Kun Serviceltä tulee viesti, kontrolleri päivittää selaimessa olevan näkymän.
// - Kun halutaan lähettää viesti, välitetään se Servicelle.
//
angular.module('chatApp')
    .controller('chatController', ['$scope', '$location', 'stompSocket', '$http', 'connectToServer', 'queueService',
        function ($scope, $location, stompSocket, $http, connectToServer, queueService) {
            // Taulukko "messages" sisältää chat-ikkunassa näkyvät viestit.
            $scope.messages = [];
            var sub;
            // Määritellään chatin nimi templateen, tällä hetkellä kovakoodattu
            this.chatName = 'Esimerkki';

            /** Funktio lähettää servicen avulla tekstikentän
             *  sisällön ja lopuksi tyhjentää tekstikentän. */
            $scope.sendMessage = function () {
                if ($scope.messageForm.$valid) {
                    var destination = "/toServer/" + queueService.getChannelID();
                    stompSocket.send(destination, {}, JSON.stringify(
                        {
                            'userId': queueService.getUserID(),
                            'channelId': queueService.getChannelID(),
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
                message.sender = parsed.userName;
                message.I = message.sender === queueService.getUserName();
                return message;
            };

            var subscribe = function () {
                sub = connectToServer.subscribe('/toClient/' + queueService.getChannelID(), function (response) {
                    $scope.messages.push(getMessage(response.body));
                });

            };

            var init = function () {
                connectToServer.connect(queueService.getChannelID(), subscribe);
            };

            queueService.getVariablesFormServer().then(function (response) {
                queueService.setAllVariables(response);
                init();
            });
        }]);
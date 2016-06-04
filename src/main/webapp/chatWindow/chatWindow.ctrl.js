// Kontrolleri päivittää tietoja molempiin suuntiin:
// - Kun Serviceltä tulee viesti, kontrolleri päivittää selaimessa olevan näkymän.
// - Kun halutaan lähettää viesti, välitetään se Servicelle.
//
angular.module('chatApp')
    .controller('chatController', ['$scope', 'stompSocket', 'connectToServer', 'userStateService',
        function ($scope, stompSocket, connectToServer, userStateService) {
            $scope.pro = false;
            // Taulukko "messages" sisältää chat-ikkunassa näkyvät viestit.
            $scope.messages = [];
            var sub;
            // Määritellään chatin nimi templateen, tällä hetkellä kovakoodattu
            $scope.chatName = 'Esimerkki';

            /** Funktio lähettää servicen avulla tekstikentän
             *  sisällön ja lopuksi tyhjentää tekstikentän. */
            $scope.sendMessage = function () {
                if ($scope.messageForm.$valid) {
                    var destination = "/toServer/chat/" + userStateService.getChannelID();
                    stompSocket.send(destination, {}, JSON.stringify(
                        {
                            'userId': userStateService.getUserID(),
                            'channelId': userStateService.getChannelID(),
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
                message.I = message.sender === userStateService.getUsername();
                return message;
            };

            var subscribe = function () {
                sub = connectToServer.subscribe('/toClient/chat/' + userStateService.getChannelID(), function (response) {
                    $scope.messages.push(getMessage(response.body));
                });

            };

            var init = function () {
                connectToServer.connect(subscribe);
            };

            init();
        }]);
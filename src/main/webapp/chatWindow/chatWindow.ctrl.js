/** Kontrolleri paivittaa tietoja molempiin suuntiin:
 * - Kun Servicelta tulee viesti, kontrolleri paivittaa selaimessa olevan nakyman.
 *- Kun halutaan lahettaa viesti, valitetaan se Servicelle.
*/
angular.module('chatApp')
    .controller('chatController', ['$scope', 'stompSocket', 'connectToServer', 'userStateService',
        function ($scope, stompSocket, connectToServer, userStateService) {
            // Taulukko "messages" sisaltaa chat-ikkunassa nakyvat viestit.
            $scope.messages = [];
            var sub;
            // Maaritellaan chatin nimi templateen, talla hetkella kovakoodattu
            $scope.chatName = 'Esimerkki';

            /** Funktio lahettaa servicen avulla tekstikentan
             *  sisallon ja lopuksi tyhjentaa tekstikentan. */
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
                var message = JSON.parse(data);
                message.I = message.username === userStateService.getUsername();
                if (!message.I){
                    $scope.chatName = message.username;
                }
                return message;
            };

            var subscribe = function () {
                sub = connectToServer.subscribe('/toClient/chat/' + userStateService.getChannelID(), function (response) {
                    if (response.body != '$CLEAR$') {
                        $scope.messages.push(getMessage(response.body));
                    } else  {
                        $scope.messages.length = 0;
                    }
                });

            };

            var init = function () {
                connectToServer.connect(subscribe);
            };

            init();
        }]);
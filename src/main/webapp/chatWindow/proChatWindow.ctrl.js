/** Kontrolleri ammattilaisen versio paivittaa tietoja molempiin suuntiin:
 * - Kun Servicelta tulee viesti, kontrolleri paivittaa selaimessa olevan nakyman.
 *- Kun halutaan lahettaa viesti, valitetaan se Servicelle.
 */
angular.module('chatApp')
    .controller('proChatController', ['$scope', 'stompSocket', 'connectToServer', 'proStateService',
        function ($scope, stompSocket, connectToServer, proStateService) {
            $scope.pro = true;
            // Taulukko "messages" sisaltaa chat-ikkunassa nakyvat viestit.
            $scope.messages = [];
            var sub;
            // Maaritellaan chatin nimi templateen, talla hetkella kovakoodattu
            $scope.chatName = '';

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

            /** Funktio muuttaa viestin haluttuun muotoon. 
             *  Lisää sille tiedon, siitä onko viesti käyttäjän
             *  itsensä lähettämä. 
             *  Asettaa chatinNimeen vastapuolen nimimerkin
             */
            var getMessage = function (data) {
                var message = JSON.parse(data);
                message.I = message.username === proStateService.getUsername();
                if (!message.I) {
                    $scope.chatName = message.username;
                }
                return message;
            };

            /** Alustetaan kanava, jolta kuunnellaan tulevat viestit */
            var subscribe = function () {
                sub = connectToServer.subscribe('/toClient/chat/' + channel, function (response) {
                    if (response.body != '$CLEAR$') {
                        $scope.messages.push(getMessage(response.body));
                    } else {
                        $scope.messages.length = 0;
                    }
                });
            };

            /** Alustetaan kanava, kun controlleri ladataan */
            subscribe();
        }]);

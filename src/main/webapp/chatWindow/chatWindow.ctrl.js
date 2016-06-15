/** Kontrolleri paivittaa tietoja molempiin suuntiin:
 * - Kun Servicelta tulee viesti, kontrolleri paivittaa selaimessa olevan nakyman.
 *- Kun halutaan lahettaa viesti, valitetaan se Servicelle.
*/
angular.module('chatApp')
    .controller('chatController', ['$scope', 'stompSocket', 'connectToServer', 'userStateService',
        function ($scope, stompSocket, connectToServer, userStateService) {
            // "messages" sisaltaa chat-ikkunassa nakyvat viestit.
            $scope.messages = [];
            // "messageIds" sisaltaa messageId:t viesteille, jotta samaa
            // viestia ei tulostettaisi montaa kertaa.
            // chat logien broadcastauksessa serveri saattaa lahettaa
            // viesteja, jotka meilla jo on.
            var messageIds = {};
            var sub;
            // Alustetaan ChatName tyhjäksi
            $scope.chatName = '';

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

            /** Funktio muuttaa viestin haluttuun muotoon.
             *  Lisää sille tiedon, siitä onko viesti käyttäjän
             *  itsensä lähettämä.
             *  Asettaa chatinNimeen vastapuolen nimimerkin
             */
            var getMessage = function (data) {
                var message = JSON.parse(data);
                message.I = message.username === userStateService.getUsername();
                if (!message.I){
                    $scope.chatName = message.username;
                }
                return message;
            };

            /** Alustetaan kanava, jolta kuunnellaan tulevat viestit */
            var subscribe = function () {
                sub = connectToServer.subscribe('/toClient/chat/' + userStateService.getChannelID(), function (response) {

                    //TODO: Testaa ettei allaoleva hakkerointi toimi
                    //sub = connectToServer.subscribe('/toClient/chat/*', function (response) {

                    // Lisataan viesti, jos sita ei ole jo entuudestaan.
                    // Chat Logien broadcastauksen yhteydessa serveri saattaa
                    // lahettaa meille viesteja, jotka meilla jo on.
                    var message = getMessage(response.body);
                    if (!messageIds[message.messageId]) {
                        messageIds[message.messageId] = true;
                        $scope.messages.push(getMessage(response.body));
                    }
                });

            };

            /** Varmistetaan serveriltä että ollaan yhteydessä  */
            var init = function () {
                connectToServer.connect(subscribe);
            };
            /** Alustetaan yhteys kun controller ladataan */
            init();
        }]);
/** 
 * Kontrolleri paivittaa tietoja molempiin suuntiin:
 * - Kun Servicelta tulee viesti, kontrolleri paivittaa selaimessa olevan nakyman.
 * - Kun halutaan lahettaa viesti, valitetaan se Servicelle.
 */
angular.module('chatApp')
    .controller('chatController', ['$scope', '$uibModal', 'stompSocket', 'connectToServer', 'userStateService',
        function ($scope, $uibModal, stompSocket, connectToServer, userStateService) {
            // "messages" sisaltaa chat-ikkunassa nakyvat viestit.
            $scope.messages = [];
            // "messageIds" sisaltaa messageId:t viesteille, jotta samaa
            // viestia ei tulostettaisi montaa kertaa.
            // chat logien broadcastauksessa serveri saattaa lahettaa
            // viesteja, jotka meilla jo on.
            var messageIds = {};
            var sub;
            $scope.chatText = 'Tervetuloa!';

            /**
             * Kayttajan sulkiessa keskustelun kysytaan varmistus,
             * jonka jalkeen kutsutaan chatin sulkemista.
             */
            $scope.userLeave = function() {
                var modalInstance = $uibModal.open({
                    animation: true,
                    templateUrl: 'common/areUSureModal.tpl.html',
                    controller: 'AreUSureModalController'
                });
                modalInstance.result.then(closeChat);
            };

            /**
             * Functio muokkaa kayttoliittyman suljetuksi, kun keskustelu suljetaan.
             */
            var closeChat = function() {
                userStateService.leaveChat();
                sub.unsubscribe();
                $scope.chatText = 'Keskustelu on päättynyt! Historia katoaa sivulta poistuttaessa!'
                $scope.chatClosed = true;
            };

            /** 
			 * Funktio lahettaa servicen avulla tekstikentan
             * sisallon ja lopuksi tyhjentaa tekstikentan. 
			 */
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
             *  Lisaa sille tiedon, siitä onko viesti kayttajan
             *  itsensa lahettama.
             *  Asettaa chatinNimeen vastapuolen nimimerkin
             */
            var getMessage = function (data) {
                var message = JSON.parse(data);
                message.I = message.username === userStateService.getUsername();
                if (!angular.isUndefined(message.username)) {
                    if (!message.I) {
                        $scope.chatText = 'Keskustelu käyttäjän ' + message.username + ' kanssa';
                    }
                }
                return message;
            };

            /** Alustaa kanavan, jolta kuunnellaan tulevat viestit */
            var subscribe = function () {
                sub = connectToServer.subscribe('/toClient/chat/' + userStateService.getChannelID(), function (response) {
                    // Lisataan viesti, jos sita ei ole jo entuudestaan.
                    // Chat Logien broadcastauksen yhteydessa serveri saattaa
                    // lahettaa meille viesteja, jotka meilla jo on.
                    var message = getMessage(response.body);
                    if (message.messageId && !messageIds[message.messageId]) {
                        messageIds[message.messageId] = true;
                        $scope.messages.push(getMessage(response.body));
                    } else if (message.notice === "chat closed") {
                        closeChat();
                    }
                });

            };

            /** Varmistaa serverilta että ollaan yhteydessa  */
            var init = function () {
                connectToServer.connect(subscribe);
            };
            /** Alustaa yhteys kun controller ladataan */
            init();
        }]);
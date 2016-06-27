/** Kontrolleri ammattilaisen versio paivittaa tietoja molempiin suuntiin:
 * - Kun Servicelta tulee viesti, kontrolleri paivittaa selaimessa olevan nakyman.
 *- Kun halutaan lahettaa viesti, valitetaan se Servicelle.
 */
angular.module('chatProApp')
    .controller('proChatController', ['$scope', '$uibModal', 'stompSocket', 'connectToServer', 'proStateService',
        function ($scope, $uibModal, stompSocket, connectToServer, proStateService) {
            $scope.pro = true;
            // Taulukko "messages" sisaltaa chat-ikkunassa nakyvat viestit.
            $scope.messages = [];
            // "messageIds" sisaltaa messageId:t viesteille, jotta samaa
            // viestia ei tulostettaisi montaa kertaa.
            // chat logien broadcastauksessa serveri saattaa lahettaa
            // viesteja, jotka meilla jo on.
            var messageIds = {};
            var sub;
            // Maaritellaan chatin nimi templateen, talla hetkella kovakoodattu
            $scope.chatText = '';

            var channel = this.channel;
            var endChat = this.chatend;

            $scope.$on('unSubscribeChat', function (event, args) {
                if (args.channelID === channel) {
                    sub.unsubscribe();
                }
            });

            $scope.userLeave = function() {
                var modalInstance = $uibModal.open({
                    animation: true,
                    templateUrl: 'common/areUSureModal.tpl.html',
                    controller: 'AreUSureModalController'
                });

                modalInstance.result.then(closeChat);
            };

            var closeChat = function() {
                sub.unsubscribe();
                $scope.chatClosed = true;
                endChat();
            };
            
            $scope.closeConversation = function() {
                endChat();
            };

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
                if (!angular.isUndefined(message.username)) {
                    message.I = message.username === proStateService.getUsername();
                    if (!message.I) {
                        $scope.chatText = 'Keskustelu käyttäjän ' + message.username + ' kanssa';
                    }
                }
                return message;
            };

            var userLeaves = function() {
                sub.unsubscribe();
                $scope.chatText = 'Vastapuoli on lopettanu keskustelun';
                $scope.chatClosed = true;
                proStateService.leaveChannel(channel);
            };

            /** Alustetaan kanava, jolta kuunnellaan tulevat viestit */
            var subscribe = function () {
                sub = connectToServer.subscribe('/toClient/chat/' + channel, function (response) {
                    var message = getMessage(response.body);
                    if (message.messageId && !messageIds[message.messageId]) {
                        messageIds[message.messageId] = true;
                        $scope.messages.push(getMessage(response.body));
                    } else if (message.notice == "chat closed") {
                        userLeaves();
                    }
                });
            };

            /** Alustetaan kanava, kun controlleri ladataan */
            if (angular.isUndefined(sub)){
                subscribe();
            }

        }]);

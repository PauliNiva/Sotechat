/** Controlelri huolehtii ammattilaisen näkymän välilehtien hallinnasta
 * Seka ilmoittaa "lapsilleen" yhteyden muodostumisesta serveriin
 */
angular.module('chatProApp')
    .controller('proCPController', ['$scope', '$timeout', '$http', '$uibModal', 'connectToServer', 'proStateService', 'heartBeatService',
        function ($scope, $timeout, $http, $uibModal, connectToServer, proStateService) {
            /** Alustetaan muuttuja */
            var tabCount = 0;
            $scope.pro = true;
            $scope.chats = [];
            $scope.activeChatTab = tabCount;
            $scope.proView = 'proControlPanel/userHandlingArea.tpl.html';
            /** onko ammattilainen paikalla */
            $scope.present = {};


            $scope.showHistory = function () {
                $scope.proView = 'proHistories/chatHistory.html';
                angular.forEach($scope.chats, function(key) {
                    $scope.$broadcast('unSubscribeChat', {'channelID': key.channel});
                });
            };

            $scope.backToPanel = function () {
                $scope.proView = 'proControlPanel/userHandlingArea.tpl.html';
            };

            /** Ilmoitetaan jono controllerille että yhteys serveriin on muodostetu */
            var initQueue = function () {
                $scope.$broadcast('connectedToQueue');
            };
            
            $scope.endChat = function(channelID) {
                proStateService.leaveChannel(channelID);
                removeTab(channelID);
            };

            /** Lisää uuden chat välilehdin annetulla kanavaID:nä */
            $scope.addChatTab = function (channelID) {
                proStateService.addChannel(channelID);
                $scope.chats.push({index: tabCount, title: 'Chat' + tabCount, channel: channelID, status: 'online'});
                $scope.activeChatTab = tabCount;
                tabCount++;
            };

            $scope.changeTab = function (index) {
                $scope.activeChatTab = index;
            };

            $scope.tabIsSelected = function (index) {
                return index === $scope.activeChatTab;
            };

            var removeTab = function (channelID) {
                var chatTabIndex = arrayObjectIndexOf($scope.chats, channelID, 'channel');
                if (chatTabIndex > -1) {
                    $scope.chats.splice(chatTabIndex, 1);
                }
                if ($scope.chats.length > 0) {
                    $scope.activeChatTab = $scope.chats[0].index;
                }
            };

            var arrayObjectIndexOf = function (myArray, searchTerm, property) {
                for (var i = 0, len = myArray.length; i < len; i++) {
                    if (myArray[i][property] === searchTerm) return i;
                }
                return -1;
            };


            /** Avaa kaikki amamttilaisen avoimet välilehdet */
            var updateChannels = function () {
                $scope.chats = [];
                tabCount = 0;
                $scope.activeChatTab = tabCount;
                angular.forEach(proStateService.getChannelIDs(), function (key) {
                    $scope.chats.push({index: tabCount, title: 'Chat' + tabCount, channel: key, status: 'online'});
                    tabCount++;
                });
            };

            /** Kun yhteys serveriin on muodostettu alustetaan siitä riippuvat */
            var answer = function () {
                initQueue();
                $scope.username = proStateService.getUsername();
                $scope.present.yesNo = proStateService.getOnline();
                updateChannels();
            };

            /** Päivittää ammattilaisen tiedot serveriltä
             *  Ja aloittaa alustuksen haun valmistuttua
             */
            $scope.updateProStatus = function () {
                proStateService.getVariablesFormServer().then(function (response) {
                    proStateService.setAllVariables(response);
                    connectToServer.connect(answer);
                });
            };

            $scope.changeStatus = function() {
               if ($scope.present.yesNo) {
                   proStateService.setStatusOnline();
               } else {
                   proStateService.setStatusOffline();
               }
            };

            /** Pyytää alustusta kun kontrolleri ladataan */
            $scope.updateProStatus();
        }]);
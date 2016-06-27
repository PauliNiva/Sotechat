angular.module('chatProApp')
        .controller('proHistoriesController', ['$scope', 'proStateService','proHistoryService',
            function($scope, proStateService, proHistoryService) {

                /** kuinka paljon keskusteluja naytetaan */
                $scope.quantity = -10;
                /** kuinka paljon viesteja naytetaan */
                $scope.messageQuantity = -10;
                /** onko keskusteluja jaljella naytettavaksi */
                $scope.left = true;
                /** onko viesteja jaljella naytettavaksi */
                $scope.messagesLeft = true;
                /** naytettavat viestit */
                $scope.messages = [];
                /** keskustelujen tiedot */
                $scope.Conversations = [];
                /** onko tietokannassa keskusteluja vai ei */
                $scope.empty = false;
                /** keskustelunakyman osoite */
                $scope.view = 'proHistories/conversation.html';
                /** naytetaanko keskustelunakyma */
                $scope.showConv = false;
                /** henkilon kayttajanimi */
                $scope.myUsername = proStateService.getUsername();

                $scope.openHistoryOf = function(channelID) {
                    $scope.$broadcast ('openHistory' + channelID);
                };

                /** hakee henkilon id:n perusteella keskustelujen tiedot */
                var showHistory = function () {
                    proHistoryService.getHistory().then(function(response) {
                        var data = response.data;

                        if(response == null){
                            $scope.Conversations = [];
                        } else {
                            $scope.Conversations = data;
                        }
                        if ($scope.Conversations.length > 0) {
                            $scope.empty = false;
                        } else {
                            $scope.empty = true;
                        }
                    })
                };

                /** haetaan naytettavat viestit ja naytetaan ne */
                $scope.showConversation = function (channelId) {
                    proHistoryService.getMessages(channelId).then(function(response){
                       var msghistory = response.data;
                        if (msghistory.length > 0) {
                            $scope.messagesLeft = true;
                            $scope.messages = msghistory;
                            $scope.startTime = msghistory[0].timeStamp; 
                        }else {
                            $scope.messagesLeft = false;
                        }
                        $scope.showConv = true;
                    });
                };

                /** lisataan naytettavien keskustelujen maaraa */
                $scope.addQuantity = function () {
                    if ((-$scope.quantity) < $scope.Conversations.length) {
                        var diff = $scope.Conversations.length - (-$scope.quantity);
                        if(diff < 10) {
                            $scope.quantity -= diff;
                        } else {
                            $scope.quantity -= 10;
                        }
                    } else if ($scope.left == false) {
                        $scope.left = true;
                    } else {
                        $scope.left = false;
                    }
                };

                /** naytettavien keskustelujen resetointi */
                var resetQuantity = function () {
                    $scope.quantity = -10;
                    $scope.left = true;
                };

                /** lisataan naytettavien viestien maaraa */
                $scope.addMessageQuantity = function () {
                    if ((-$scope.messageQuantity) < $scope.messages.length) {
                        var diff = $scope.Conversations.length - (-$scope.messageQuantity);
                        if(diff < 10) {
                            $scope.messageQuantity -= diff;
                        } else {
                            $scope.messageQuantity -= 10;
                        }
                    } else if ($scope.messagesLeft == false ){
                        $scope.messagesLeft = true;
                    } else {
                        $scope.messagesLeft = false;
                    }
                };

                /** resetoidaan naytettavien viestien maara */
                var resetMessageQuantity = function () {
                    $scope.messageQuantity = -10;
                    $scope.messagesLeft = true;
                };

                /** siirrytaan takaisin keskustelut -sivulle */
                $scope.backToConversations = function () {
                    resetQuantity();
                    resetMessageQuantity();
                    $scope.showConv = false;
                };
                
                /** alustetaan keskustelut kun kontrolleri ladataan */
                showHistory();

            }]);
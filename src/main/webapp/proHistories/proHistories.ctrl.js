/**
 * Created by varkoi on 17.6.2016.
 */

angular.module('chatProApp')
        .controller('proHistoriesController', ['$scope', 'proStateService','proHistoryService',
            function($scope, proStateService, proHistoryService) {

                /** kuinka paljon keskusteluja naytetaan */
                $scope.quantity = -10;
                /** kuinka paljon viesteja naytetaan */
                $scope.messageQuantity = -25;
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



                /** hakee henkilon id:n perusteella keskustelujen tiedot */
                var showHistory = function () {
                    var userId = proStateService.getUserID();
                    $scope.Conversations = proHistoryService.getHistory(userId);
                    if($scope.Conversations.empty){
                        $scope.Conversations = [];
                    }
                    if ($scope.Conversations.length > 0) {
                        $scope.empty = false;
                    } else {
                        $scope.empty = true;
                    }
                };

                /** lisataan naytettavien keskustelujen maaraa */
                $scope.addQuantity = function () {
                    if ((-$scope.quantity) < $scope.Conversations.length) {
                        $scope.quantity -= 10;
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
                var addMessageQuantity = function () {
                    if ((-$scope.messageQuantity) < $scope.messages.length) {
                        $scope.messageQuantity -= 10;
                    } else {
                        $scope.messagesLeft = false;
                    }
                };

                /** resetoidaan naytettavien viestien maara */
                var resetMessageQuantity = function () {
                    $scope.messageQuantity = -25;
                    $scope.messagesLeft = true;
                };

                /** haetaan naytettavat viestit ja siirrytaan ne nayttavalle sivulle */
                var showConversation = function (channelId) {
                    $scope.messages = proHistoryService.getMessages(channelId);
                    if ($scope.messages.length > 0) {
                        $scope.messagesLeft = true;
                    }else {
                        $scope.messagesLeft = false;
                    }
                    return proHistoryService.getConversationPage();
                };

                /** siirrytaan takaisin keskustelut -sivulle */
                $scope.backToConversations = function () {
                    resetQuantity();
                    resetMessageQuantity();
                    $scope.left = true;
                    $scope.messagesLeft = true;
                    return proHistoryService.getHistoryPage();
                };
                
                /** alustetaan keskustelut kun kontrolleri adataan */
                showHistory();

            }]);
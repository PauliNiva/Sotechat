angular.module('chatProApp')
    .controller('historiesConversationController', ['$scope', 'proStateService','proHistoryService',
        function($scope, proStateService, proHistoryService) {
            /** kuinka paljon viesteja naytetaan */
            $scope.messageQuantity = -10;
            /** onko keskusteluja jaljella naytettavaksi */
            $scope.left = true;
            /** onko viesteja jaljella naytettavaksi */
            $scope.messagesLeft = true;

            $scope.showLeft = false;
            /** naytettavat viestit */
            $scope.messages = [];
            /** onko tietokannassa keskusteluja vai ei */
            $scope.empty = false;

            var channelId = this.channel;
            var myname = this.myname;

            /** haetaan naytettavat viestit ja siirrytaan ne nayttavalle sivulle */
            function init() {
                proHistoryService.getMessages(channelId).then(function(response){
                    var msghistory = response.data;
                    if (msghistory.length > 0) {
                        angular.forEach(msghistory, function (key) {
                            key.I = key.username === myname;
                        });
                        $scope.messagesLeft = true;
                        $scope.messages = msghistory;
                        $scope.startTime = msghistory[0].timeStamp;
                    }else {
                        $scope.messagesLeft = false;
                    }
                    $scope.showConv = true;
                });
            };

            /** lisataan naytettavien viestien maaraa */
            $scope.addMessageQuantity = function () {
                if ((-$scope.messageQuantity) < $scope.messages.length) {
                    $scope.messageQuantity -= 10;
                } else if ($scope.messagesLeft == false ){
                    $scope.messagesLeft = true;
                } else {
                    $scope.messagesLeft = false;
                    $scope.showLeft = true;
                }
            };

            $scope.showLess = function () {
                if($scope.messageQuantity<=-10){
                    $scope.messageQuantity += 10;
                    $scope.showLeft = false
                } else {
                    $scope.messagesLeft = true;
                }
            }

            /** resetoidaan naytettavien viestien maara */
            var resetMessageQuantity = function () {
                $scope.messageQuantity = -10;
                $scope.messagesLeft = true;
            };
            init();

        }]);
angular.module('chatProApp')
    .controller('historiesConversationController', ['$scope', 'proStateService','proHistoryService',
        function($scope, proStateService, proHistoryService) {
            /** kuinka paljon viesteja naytetaan */
            $scope.messageQuantity = -7;
            /** onko keskusteluja jaljella naytettavaksi */
            $scope.left = true;
            /** onko viesteja jaljella naytettavaksi */
            $scope.messagesLeft = true;
            /** Naytetaanko ei enempaa viesteja viesti */
            $scope.showLeft = false;
            /** Naytetaanko nayta vahemman nappi */
            $scope.less = false;
            /** naytettavat viestit */
            $scope.messages = [];
            /** onko tietokannassa keskusteluja vai ei */
            $scope.empty = false;
            $scope.loaded = false;

            var channelId = this.channel;
            var myname = this.myname;

            var extra = 0;

            $scope.$on('openHistory' + channelId, function(e) {
                resetMessageQuantity();
                init();
            });

            /** haetaan naytettavat viestit ja siirrytaan ne nayttavalle sivulle */
            function init() {
                if(!$scope.messages.length) {
                    proHistoryService.getMessages(channelId).then(function (response) {
                        var msghistory = response.data;
                        if (msghistory.length > 0) {
                            angular.forEach(msghistory, function (key) {
                                key.I = key.username === myname;
                            });
                            $scope.messagesLeft = true;
                            $scope.messages = msghistory;
                            $scope.startTime = msghistory[0].timeStamp;
                        } else {
                            $scope.messagesLeft = false;
                            $scope.empty = true;
                        }
                        $scope.showConv = true;
                        $scope.loaded = true;
                    });
                }
            };

            /** lisataan naytettavien viestien maaraa */
            $scope.addMessageQuantity = function () {
                if ((-$scope.messageQuantity) < $scope.messages.length) {
                    var diff = $scope.messages.length - (-$scope.messageQuantity);
                    if (diff < 7){
                        $scope.messageQuantity -= diff;
                        extra = diff;
                    } else {
                        $scope.messageQuantity -= 7;
                        extra = 0;
                    }
                } else if ($scope.messagesLeft == false ){
                    $scope.messagesLeft = true;
                } else {
                    $scope.messagesLeft = false;
                    $scope.showLeft = true;
                    $scope.less =true;
                }
            };

            $scope.showLess = function () {
                var diff = $scope.messages.length - (-$scope.messageQuantity);
                if ($scope.messageQuantity<-7) {
                    if($scope.messageQuantity%7==0) {
                        $scope.messageQuantity += 7;
                    }else{
                        $scope.messageQuantity += extra;
                    }
                    $scope.showLeft = false
                    if ($scope.messageQuantity < $scope.messages.length) {
                        $scope.messagesLeft = true;
                    }
                } else {
                    $scope.messagesLeft = true;
                    $scope.less = false;
                    $scope.showLeft = false;
                }
            }

            /** resetoidaan naytettavien viestien maara */
            var resetMessageQuantity = function () {
                $scope.messageQuantity = -7;
                $scope.messagesLeft = true;
                $scope.less = false;
                $scope.showLeft = false; 
            };

        }]);
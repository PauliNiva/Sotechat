angular.module('chatApp')
    .controller('userInPoolCtrl', ['$http', '$scope', '$location', 'queueService', 'connectToServer', '$timeout',
        function ($http, $scope, $location, queueService, connectToServer, $timeout) {

            $timeout(function () {
                queueService.setUserState('chat');
                $scope.updateState();
            }, 10000);  // test only

            var subscribeToQueue;
            var QUEUEADDRESS = '/toClient/';

            var onMessage = function (response) {
                var parsed = JSON.parse(response.body);
                if (parsed.content === 'etene') {
                    subscribeToQueue.unsubscribe();
                    $scope.updateState();
                }
            };

            var onConnection = function () {
                subscribeToQueue = connectToServer.subscribe(QUEUEADDRESS + queueService.getChannelID(), onMessage);
            };

            var init = function () {
                connectToServer.connect(queueService.getChannelID(), onConnection);
            };

            init();


        }]);
angular.module('chatApp')
    .controller('userInQueueCtrl', ['$http', '$scope', 'userStateService', 'connectToServer', '$timeout',
        function ($http, $scope, userStateService, connectToServer, $timeout) {
            var QUEUEADDRESS = '/toClient/queue/';
            var subscribeToQueue;
            $timeout(function () {
                userStateService.setUserState('chat');
                subscribeToQueue.unsubscribe();
                $scope.updateState();
            }, 3000);  // test only

            var onMessage = function (response) {
                var parsed = JSON.parse(response.body);
                if (parsed.content === 'etene') {
                    subscribeToQueue.unsubscribe();
                    $scope.updateState();
                }
            };

            var onConnection = function () {
                subscribeToQueue = connectToServer.subscribe(QUEUEADDRESS + userStateService.getChannelID(), onMessage);
            };

            var init = function () {
                connectToServer.connect(onConnection);
            };

            init();

        }]);
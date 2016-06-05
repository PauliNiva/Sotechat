angular.module('chatApp')
    .controller('pronQueueCtrl', ['$scope', 'userStateService',
        function ($scope, userStateService) {
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
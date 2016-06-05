angular.module('chatApp')
    .controller('userInQueueCtrl', ['$http', '$scope', 'userStateService', 'connectToServer', '$timeout',
        function ($http, $scope, userStateService, connectToServer, $timeout) {
            var QUEUEADDRESS = '/toClient/queue/';
            var subscribeToQueue;


            var onMessage = function (response) {
                var parsed = JSON.parse(response.body);
                    subscribeToQueue.unsubscribe();
                    $scope.updateState();
            };

            var onConnection = function () {
                subscribeToQueue = connectToServer.subscribe(QUEUEADDRESS + userStateService.getChannelID(), onMessage);
            };

            var init = function () {
                connectToServer.connect(onConnection);
            };

            init();

        }]);
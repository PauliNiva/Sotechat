angular.module('chatApp')
    .controller('proQueueCtrl', ['$scope',
        function ($scope) {
            var QUEUEADDRESS = '/toClient/queue/';
            var subscribeToQueue;

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



        }]);
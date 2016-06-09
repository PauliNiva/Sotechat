angular.module('chatApp')
    .controller('proQueueCtrl', ['$scope', 'queueProService', 'stompSocket', 'connectToServer', 'proStateService',
        function ($scope, queueProService, stompSocket, connectToServer, proStateService) {
            var QUEUEADDRESS = '/toClient/';
            var CLIENTQUEUE = '/toServer/queue/';
            $scope.queue = queueProService.queue;
            $scope.queueStatus = $scope.inQueue === 0;
            $scope.inQueue = 0;

            $scope.$on('connectedToQueue', function (e) {
                connectToServer.subscribe(QUEUEADDRESS + proStateService.getQueueBroadcastChannel(), queue);
            });

            $scope.$watch(function () {
                return queueProService.queue.length;
            }, function (lenght) {
                $scope.inQueue = lenght;
                $scope.queueStatus = $scope.inQueue === 0;
            }, true);

            $scope.nextFromQueue = function () {
                var firstChannelID = queueProService.getFirstChannelID();
                if (firstChannelID != null) {
                    var checkIsPopOk = connectToServer.subscribe(CLIENTQUEUE + firstChannelID, function (response) {
                        console.log(response.body);
                        if (response.body.content === 'channel activated.') {
                           // queueProService.removeFirstFromQueue(firstChannelID);
                            stompSocket.send('/toServer/queue/' + firstChannelID, {}, '');
                            $scope.addChatTab(firstChannelID);
                        }
                        checkIsPopOk.unsubscribe();
                    });
                }
            };

            var queue = function (response) {
                queueProService.clear();
                angular.forEach(JSON.parse(response.body).jono, function (key) {
                    queueProService.addToQueue(key);
                });
            };
        }]);
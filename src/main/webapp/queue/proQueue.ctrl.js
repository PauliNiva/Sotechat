angular.module('chatApp')
    .controller('proQueueCtrl', ['$scope', 'queueProService', 'stompSocket', 'connectToServer', 'proStateService',
        function ($scope, queueProService, stompSocket, connectToServer, proStateService) {
            var QUEUEADDRESS = '/toClient/';
            var CLIENTQUEUE = '/toClient/queue/';
            $scope.queue = queueProService.queue;
            $scope.categorys = queueProService.categorys;
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
                $scope.pickFromQueue(firstChannelID);
            };
            
            $scope.pickFromQueue = function (channelID) {
                console.log("moi" + channelID);
                var checkChannelID = queueProService.checkChannelID(channelID);
                console.log( checkChannelID);
                if (checkChannelID != null) {
                    var checkIsPopOk = connectToServer.subscribe(CLIENTQUEUE + checkChannelID, function (response) {
                        if (JSON.parse(response.body).content === 'channel activated.') {
                            $scope.addChatTab(checkChannelID);
                        }
                        checkIsPopOk.unsubscribe();
                    });
                    stompSocket.send('/toServer/queue/' + checkChannelID, {}, '');
                }
            };

            var queue = function (response) {
                queueProService.clear();
                angular.forEach(JSON.parse(response.body).jono, function (key) {
                    queueProService.addToQueue(key);
                });
            };

        }]);
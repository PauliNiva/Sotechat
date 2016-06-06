angular.module('chatApp')
    .controller('proQueueCtrl', ['$scope', 'connectToServer','stompSocket',
        function ($scope, connectToServer,stompSocket) {

            $scope.nextFromQueue = function() {
                stompSocket.send('/toServer/queue/' + $scope.queue[0].channelID, {}, '');
                $scope.getChannelsFromServer();
            };
        }]);
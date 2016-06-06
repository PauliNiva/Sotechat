angular.module('chatApp')
    .controller('proQueueCtrl', ['$scope', 'queueProService','stompSocket',
        function ($scope, queueProService,stompSocket) {

            $scope.nextFromQueue = function() {
                var channeID = $scope.queue[0].channelID;
                stompSocket.send('/toServer/queue/' + channeID, {}, '');
                queueProService.removeFirstFromQueue();
                $scope.addChatTab(channeID);
            };
        }]);
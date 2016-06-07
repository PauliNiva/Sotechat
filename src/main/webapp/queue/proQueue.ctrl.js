angular.module('chatApp')
    .controller('proQueueCtrl', ['$scope', 'queueProService','stompSocket',
        function ($scope, queueProService,stompSocket) {

            $scope.nextFromQueue = function() {
                var channelID = queueProService.removeFirstFromQueue().channelID;
                if (channelID != null) {
                    stompSocket.send('/toServer/queue/' + channelID, {}, '');
                    $scope.addChatTab(channelID);
                }
            };
        }]);
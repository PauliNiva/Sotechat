angular.module('chatApp')
    .controller('userToPoolCtrl', ['$http', '$scope', '$location','queueService', function($http, $scope, $location, queueService) {
        var QUEUEJOINURL = '/queueJoin';
        $scope.joinQueue = function() {
            var successJoinQueue = function(response) {
                queueService.setUserName(response.data.userName);
                queueService.setChannelID(response.data.channelId);
                queueService.setUserID(response.data.userId);
                $location.path('/inQueue');
            };

            var errorJoinQueue = function(response) {

            };
             
            // $http.post(QUEUEJOINURL, {'userName' : $scope.userName, 'startMessage' : $scope.startMessage})
            //   .then(successJoinQueue, errorJoinQueue);

           // $http.get("/join").then(successJoinQueue);
            queueService.getVariablesFormServer().then(function(response) {
                queueService.setAllVariables(response);
                $location.path('/inQueue')
            });
            

           
        };

    }]);
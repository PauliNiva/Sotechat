angular.module('chatApp')
    .controller('userToPoolCtrl', ['$http', '$scope', '$location', function($http, $scope, $location) {
        var QUEUEJOINURL = '/queueJoin';
        $scope.joinQueue = function() {
            var successJoinQueue = function(response) {
                $scope.userName = response.data.userName;
                $scope.channelId = response.data.channelId;
                $scope.userId = response.data.userId;
                $location.path('/inQueue');
            };

            var errorJoinQueue = function(response) {

            };
             
            // $http.post(QUEUEJOINURL, {'userName' : $scope.userName, 'startMessage' : $scope.startMessage})
            //   .then(successJoinQueue, errorJoinQueue);
            $http.get("/join").then(successJoinQueue);
           
        };

    }]);
angular.module('chatApp')
    .controller('userToPoolCtrl', ['$http', '$scope', '$location', function($http, $scope, $location) {
        var QUEUEJOINURL = '/queueJoin';
        $scope.joinQueue = function() {
            var successJoinQueue = function(response) {
                
            };

            var errorJoinQueue = function(response) {

            };
             
            // $http.post(QUEUEJOINURL, {'userName' : $scope.userName, 'startMessage' : $scope.startMessage})
            //   .then(successJoinQueue, errorJoinQueue);
            $location.path('/inQueue');
        };

    }]);
angular.module('chatApp')
    .controller('userToPoolCtrl', ['$http', '$scope', '$location','queueService', function($http, $scope, $location, queueService) {

        queueService.refreshState();

        var JOINPOOLURL = '/joinPool';
        $scope.joinQueue = function() {
            var successJoinQueue = function(response) {
                queueService.refreshState();
            };

            var errorJoinQueue = function(response) {
                console.log("Error joining pool. Duplicate username?");
            };

            $http.post(JOINPOOLURL, {'username' : $scope.userName, 'startMessage' : $scope.startMessage})
               .then(successJoinQueue, errorJoinQueue);

        };

    }]);
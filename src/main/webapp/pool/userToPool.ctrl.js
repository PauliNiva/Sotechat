angular.module('chatApp')
    .controller('userToPoolCtrl', ['$http', '$scope', '$location','queueService', function($http, $scope, $location, queueService) {
        var JOINPOOLURL = '/joinPool';
        $scope.joinQueue = function() {
            var successJoinQueue = function(response) {
                queueService.getVariablesFormServer().then(function (response) {
                    queueService.setAllVariables(response);
                    $scope.updateState();
                });
            };

            var errorJoinQueue = function(response) {
                console.log("Error joining pool. Duplicate username?");
            };

            $http.post(JOINPOOLURL, {'username' : $scope.userName, 'startMessage' : $scope.startMessage})
               .then(successJoinQueue, errorJoinQueue);
        };

    }]);
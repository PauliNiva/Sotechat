angular.module('chatApp')
    .controller('userToQueueCtrl', ['$http', '$scope', 'userStateService', function ($http, $scope, userStateService) {
        var JOINPOOLURL = '/joinPool';
        $scope.joinQueue = function () {
            var successJoinQueue = function (response) {
                userStateService.getVariablesFormServer().then(function (response) {
                    userStateService.setAllVariables(response);
                    $scope.updateState();
                });
            };

            var errorJoinQueue = function (response) {
                console.log("Error joining pool. Duplicate username?");
            };

            $http.post(JOINPOOLURL, {'username': $scope.username, 'startMessage': $scope.startMessage})
                .then(successJoinQueue, errorJoinQueue);
        };
    }]);
angular.module('chatApp')
    .controller('userCtrl', ['$scope', 'userStateService', 'heartBeatService',
        function ($scope, userStateService) {
            $scope.pro = false;
            userStateService.getVariablesFormServer().then(function (response) {
                userStateService.setAllVariables(response);
                $scope.state = userStateService.getUserState();
            });

            $scope.updateState = function () {
                userStateService.getVariablesFormServer().then(function (response) {
                    userStateService.setAllVariables(response);
                    $scope.state = userStateService.getUserState();
                });
            };
        }]);
        
angular.module('chatApp')
    .controller('userCtrl', ['$scope', 'queueService',
        function ($scope, queueService) {
            $scope.pro = false;
            queueService.getVariablesFormServer().then(function (response) {
                queueService.setAllVariables(response);
                $scope.state = queueService.getUserState();
            });

            $scope.updateState = function() {
                $scope.state = queueService.getUserState();
            };
        }]);
        
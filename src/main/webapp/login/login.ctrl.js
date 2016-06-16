angular.module('chatProApp')
    .controller('loginController', ['$scope', 'auth',
        function($scope, auth) {
            var self = this;
            self.credentials = {};

            self.login = function() {
                auth.authenticate(self.credentials, $scope.$parent.login)
            };

        }]);
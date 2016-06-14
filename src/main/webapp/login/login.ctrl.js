angular.module('chatProApp')
    .controller('loginController', ['$scope', 'auth',
        function($scope, auth) {
            var self = this;
            self.credentials = {};
            self.authenticated = function() {
                return auth.authenticated;
            }

            self.login = function() {
                auth.authenticate(self.credentials, $scope.$parent.login)
            };

            self.logout = auth.clear;
        }]);
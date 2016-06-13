angular.module('chatApp', [])
    .controller('navigation',
        function($rootScope, $http, $location) {
            var self = this

            var authenticate = function(credentials, callback) {

                var headers = credentials ? {authorization : "Basic "
                + btoa(credentials.username + ":" + credentials.password)
                } : {};

                $http.get('user', {headers : headers}).then(function(response) {
                    if (response.data.name) {
                        $rootScope.authenticated = true;
                    } else {
                        $rootScope.authenticated = false;
                    }
                    callback && callback();
                }, function() {
                    $rootScope.authenticated = false;
                    callback && callback();
                });

            }

            authenticate();
            self.credentials = {};
            self.login = function() {
                authenticate(self.credentials, function() {
                    if ($rootScope.authenticated) {
                        $location.path("/pro");
                        self.error = false;
                    } else {
                        $location.path("/login");
                        self.error = true;
                    }
                });
            };
        });
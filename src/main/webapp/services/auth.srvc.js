angular.module('chatProApp')
    .factory('auth', function ($rootScope, $http, $location) {
        var authenticated = false;

        function authenticate(credentials, callback) {
            var headers = credentials && credentials.username ? {
                authorization: "Basic "
                + btoa(credentials.username + ":"
                    + credentials.password)
            } : {};

            $http.get('auth', {
                headers: headers
            }).then(function (response) {
                if (response.data.name) {
                    authenticated = true;
                } else {
                    authenticated = false;
                }
                callback && callback(authenticated);
            }, function () {
                authenticated = false;
                callback && callback(false);
            });

        }

        function checkStatusServer(headers) {
            $http.get('auth', {
                headers: headers
            }).then(function (response) {
                if (response.data.name) {
                    authenticated = true;
                } else {
                    authenticated = false;
                }
                callback && callback(authenticated);
            }, function () {
                authenticated = false;
                callback && callback(false);
            });

        }

        function clear() {
            auth.authenticated = false;
            $http.post('/logout', {});
        }

        function init() {
        }

        function getAuthStatus() {
            return authenticated;
        }

        var auth = {
            authenticate: authenticate,
            checkStatusServer:checkStatusServer,
            getAuthStatus: getAuthStatus,
            clear: clear
        };

        return auth;
    });
/**
 * Lahettaa palvelimelle kirjautumisen ja tarkistaa sen tilan palvelimelta.
 */
angular.module('chatProApp')
    .factory('auth', function ($rootScope, $http) {
        var authenticated = false;
        var role;

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
                    role = response.data.authorities[0].authority;
                } else {
                    authenticated = false;
                }
                callback && callback(authenticated);
            }, function () {
                authenticated = false;
                callback && callback(false);
            });
        }

        function clear(callback) {
            auth.authenticated = false;
            role = null;
            $http.post('/logout', {}).then(callback);
        }

        function getAuthStatus() {
            return authenticated;
        }

        function getRole() {
            return role;
        }

        var auth = {
            authenticate: authenticate,
            getAuthStatus: getAuthStatus,
            getRole: getRole,
            clear: clear
        };

        return auth;
    });
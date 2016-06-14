angular.module('auth', [])
    .factory('auth', function($rootScope, $http, $location) {

        enter = function() {
            if ($location.path() != auth.loginPath) {
                auth.path = $location.path();
                if (!auth.authenticated) {
                    $location.path(auth.loginPath);
                }
            }
        }
        var auth = {
            authenticated : false,
            loginPath : '/login',
            logoutPath : '/logout',
            homePath : '/',
            
            authenticate : function(credentials, callback) {
                var headers = credentials && credentials.username ? {
                    authorization : "Basic "
                    + btoa(credentials.username + ":"
                        + credentials.password)
                } : {};

                $http.get('auth', {
                    headers : headers
                }).then(function(response) {
                    if (response.data.name) {
                        auth.authenticated = true;
                    } else {
                        auth.authenticated = false;
                    }
                    $location.path(auth.homePath);
                    callback && callback(auth.authenticated);
                }, function() {
                    auth.authenticated = false;
                    callback && callback(false);
                });

            },

            clear : function() {
                auth.authenticated = false;
                $location.path(auth.loginPath);
                $http.post(auth.logoutPath, {});
            },

            init : function(homePath, loginPath, logoutPath) {
               // auth.homePath = homePath;
                auth.loginPath = loginPath;
                auth.logoutPath = logoutPath;

                auth.authenticate({}, function(authenticated) {
                    if (authenticated) {
                        $location.path(auth.path);
                    }
                })

                $rootScope.$on('$routeChangeStart', function() {
                    enter();
                });
            }

        };

        return auth;
    });
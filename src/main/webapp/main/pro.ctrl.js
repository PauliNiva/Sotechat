/** Controlleri huolehtii käyttäjän tilan pyyntämisestä
 *  sivulle tulon yhteydessä, sekä sitä pyytäessä
 */
angular.module('chatProApp')
    .controller('proCtrl', ['$scope', 'auth',
        function ($scope, auth) {
            $scope.login = function(authenticated) {
                if (authenticated) {
                    $scope.authStatus = 'proControlPanel/controlPanel.tpl.html';
                    console.log("Login succeeded");
                    $scope.error = false;
                } else {
                    console.log("Login failed");
                    $scope.error = true;
                }
            };
            
            $scope.clear = function() {
                auth.clear();
            };

            var init  = function(authenticated) {
                $scope.login(authenticated);
                if (auth.getAuthStatus() !== false) {
                    $scope.authStatus = 'proControlPanel/controlPanel.tpl.html';
                } else {
                    $scope.error = false;
                    $scope.authStatus = 'login/login.tpl.html';
                }
            };
            auth.authenticate([], init);
        }]);
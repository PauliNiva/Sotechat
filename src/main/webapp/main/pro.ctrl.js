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
                    self.error = false;
                } else {
                    console.log("Login failed");
                    self.error = true;
                }
            };

            var init  = function(authenticated) {
                $scope.login(authenticated);
                if (auth.getAuthStatus() !== false) {
                    $scope.authStatus = 'proControlPanel/controlPanel.tpl.html';
                } else {
                    $scope.authStatus = 'login/login.tpl.html';
                }
            };
            auth.authenticate([], init);
        }]);
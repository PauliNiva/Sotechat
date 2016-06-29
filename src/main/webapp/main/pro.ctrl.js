/** Kontrolleri huolehtii ammattilaisen kirjautumistilan tarkastelusta.
 * Ja oikean näkymän näyttämisestä.
 */
angular.module('chatProApp')
    .controller('proCtrl', ['$scope', 'auth',
        function ($scope, auth) {
            var CPTEMPLATE = 'proControlPanel/controlPanel.tpl.html';
            var ADMINTEMPLATE = 'admin/adminCP.tpl.html'
            var LOGINTEMPLATE = 'login/login.tpl.html';

            /**
             * Näyttää näkymän riippuen onko ammattilainen kirjautunut vai ei.
             * @param authenticated Tieto siitä onko käyttäjä kirjautunut.
             */
            $scope.login = function(authenticated) {
                if (authenticated) {
                    if (auth.getRole() === 'ROLE_ADMIN') {
                        $scope.authStatus = ADMINTEMPLATE;
                    } else {
                        $scope.authStatus = CPTEMPLATE;
                    }
                    $scope.error = false;
                } else {
                    $scope.error = true;
                }
            };

            /**
             * Uloskirjauttaa käyttäjän
             */
            $scope.logout = function() {
                auth.clear(function() {
                    auth.authenticate([], init);
                });
            };

            /**
             * Tarkastetaan kirjautumistiedot sivun ladatessa, mutta ei näytetä
             * virheviestehjä
             */
            var init  = function() {
                if (auth.getAuthStatus() !== false) {
                    if (auth.getRole() === 'ROLE_ADMIN') {
                        $scope.authStatus = ADMINTEMPLATE;
                    } else {
                        $scope.authStatus = CPTEMPLATE;
                    }
                } else {
                    $scope.error = false;
                    $scope.authStatus = LOGINTEMPLATE;
                }
            };
            auth.authenticate([], init);
        }]);
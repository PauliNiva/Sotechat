/** 
 * Kontrolleri huolehtii ammattilaisen kirjautumistilan tarkastelusta.
 * Ja oikean nakyman nayttamisesta.
 */
angular.module('chatProApp')
    .controller('proCtrl', ['$scope', 'auth', 'proStateService',
        function ($scope, auth, proStateService) {
            var CPTEMPLATE = 'proControlPanel/controlPanel.tpl.html';
            var ADMINTEMPLATE = 'admin/adminCP.tpl.html'
            var LOGINTEMPLATE = 'login/login.tpl.html';

            /**
             * Nayttaa nakyman riippuen onko ammattilainen kirjautunut vai ei.
             * @param authenticated Tieto siitä onko kayttaja kirjautunut.
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
             * Uloskirjauttaa kayttajan.
             */
            $scope.logout = function() {
                proStateService.setStatusOffline().then(function() {
                    auth.clear(function() {
                    auth.authenticate([], init);
                });
                });

            };

            /**
             * Tarkastaa kirjautumistiedot sivun ladatessa, mutta ei nayteta
             * virheviesteja.
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
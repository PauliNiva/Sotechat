/** Controlleri huolehtii käyttäjän tilan pyyntämisestä
 *  sivulle tulon yhteydessä, sekä sitä pyytäessä
 */
angular.module('chatProApp')
    .controller('proCtrl', ['$scope',
        function ($scope) {
            $scope.authStatus = 'login/login.tpl.html';
        }]);

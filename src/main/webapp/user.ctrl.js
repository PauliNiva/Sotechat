/** Controlleri huolehtii käyttäjän tilan pyyntämisestä
 *  sivulle tulon yhteydessä, sekä sitä pyytäessä
 */
angular.module('chatApp')
    .controller('userCtrl', ['$scope', 'userStateService', 'heartBeatService',
        function ($scope, userStateService) {
            $scope.pro = false;
            
            /** Tekee käyttäjän tilan päivityspyynnöt tarvittaessa */
            $scope.updateState = function () {
                userStateService.getVariablesFormServer().then(function (response) {
                    userStateService.setAllVariables(response);
                    $scope.state = userStateService.getUserState();
                });
            };
            /** Kun controller ladataan päivitetään käyttäjän tila */
            $scope.updateState();
        }]);
        
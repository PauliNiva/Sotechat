/** 
 * Kontrolleri huolehtii kayttajan tilan pyytamisesta
 * sivulle tulon yhteydessa, sekä sitä kutsuessa.
 */
angular.module('chatApp')
    .controller('userCtrl', ['$scope','$http', 'userStateService', 'heartBeatService',
        function ($scope, $http, userStateService) {
            $scope.pro = false;
            
            /** Tekee kayttajan tilan paivityspyynnot tarvittaessa */
            $scope.updateState = function () {
                userStateService.getVariablesFormServer().then(function (response) {
                    userStateService.setAllVariables(response);
                    $scope.state = userStateService.getUserState();
                });
            };
            
           /** 
			* Kun kontrolleri ladataan paivitetaan kayttajan tila 
			*/
            $scope.updateState();
        }]);
        
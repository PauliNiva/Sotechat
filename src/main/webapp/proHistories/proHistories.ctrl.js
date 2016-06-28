angular.module('chatProApp')
        .controller('proHistoriesController', ['$scope', 'proStateService','proHistoryService',
            function($scope, proStateService, proHistoryService) {
                /** kuinka paljon keskusteluja naytetaan */
                $scope.quantity = 10;
                /** onko keskusteluja jaljella naytettavaksi */
                $scope.left = true;
                /** keskustelujen tiedot */
                $scope.Conversations = [];
                /** onko tietokannassa keskusteluja vai ei */
                $scope.empty = false;
                /** henkilon kayttajanimi */
                $scope.myUsername = proStateService.getUsername();

                /** Lähetetään käsky ladata keskusetlun historia */
                $scope.openHistoryOf = function(channelID) {
                    $scope.$broadcast ('openHistory' + channelID);
                };

                /** hakee henkilon id:n perusteella keskustelujen tiedot. */
                var showHistory = function () {
                    proHistoryService.getHistory().then(function(response) {
                        var data = response.data;
                        if(response == null){
                            $scope.Conversations = [];
                        } else {
                            $scope.Conversations = data;
                        }
                        $scope.empty = $scope.Conversations.length <= 0;
                    })
                };
                
                /** lisataan naytettavien keskustelujen maaraa. */
                $scope.addQuantity = function () {
                    $scope.quantity += 10;
                    if ($scope.quantity  >= $scope.Conversations.length) {
                        $scope.left = false;
                    }
                };
                
                
                /** alustetaan keskustelut kun kontrolleri ladataan */
                showHistory();

            }]);
/** Controlleri hallitsee ja seuraa käyttäjän  
 *  tilaa, kun käyttäjä odottaa jonossa
 **/
angular.module('chatApp')
    .controller('userInQueueCtrl', ['$scope','$interval', 'userStateService', 'connectToServer',
        function ($scope, $interval, userStateService, connectToServer) {
            /** Serverin mappaukset */
            var QUEUEADDRESS = '/toClient/queue/';
            /** Muuttuja johon tallennetaan yhteys kanavaan */
            var subscribeToQueue;

            $scope.color = 'blueBg';

            $interval(function() {
                if ($scope.color === 'whiteBg') {
                    $scope.color = 'blueBg';
                } else {
                    $scope.color = 'whiteBg';
                }
            }, 3000);

            /** Viestin saapuessa jonotuskanavalle 
             *  lopetetaan kanavan kuuntelu ja
             *  pyydetään käyttäjän tila päivitystä
             */
            var onMessage = function () {
                    subscribeToQueue.unsubscribe();
                    $scope.updateState();
            };

            /** Yhteyden muodostuttua serveriin yhdistetään jonon kanavaan */
            var onConnection = function () {
                subscribeToQueue = connectToServer.subscribe(QUEUEADDRESS + userStateService.getChannelID(), onMessage);
            };
            
            /** Yhdistetään serveriin*/
            connectToServer.connect(onConnection);
        }]);
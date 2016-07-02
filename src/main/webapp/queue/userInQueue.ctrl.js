/** 
 * Kontrolleri hallitsee ja seuraa kayttajan  
 *  tilaa, kun kayttaja odottaa jonossa.
 */
angular.module('chatApp')
    .controller('userInQueueCtrl', ['$scope','$interval', 'userStateService', 'connectToServer',
        function ($scope, $interval, userStateService, connectToServer) {
            var QUEUEADDRESS = '/toClient/queue/';
            /** Muuttuja johon tallennetaan yhteyskanavaan */
            var subscribeToQueue;
            $scope.color = 'blueBg';
            var iBackGround = $interval(function() {
                if ($scope.color === 'whiteBg') {
                    $scope.color = 'blueBg';
                } else {
                    $scope.color = 'whiteBg';
                }
            }, 3000);

            /** 
             * Viestin saapuessa jonotuskanavalle 
             *  lopetetaan kanavan kuuntelu ja
             *  pyydetään kayttajan tila paivitysta.
             */
            var onMessage = function () {
                    subscribeToQueue.unsubscribe();
                    $interval.cancel(iBackGround);
                    $scope.updateState();
            };

            /** 
             * Yhteyden muodostuttua serveriin yhdistetaan jonon kanavaan.
             */
            var onConnection = function () {
                subscribeToQueue = connectToServer.subscribe(QUEUEADDRESS + userStateService.getChannelID(), onMessage);
            };
            
            /** 
             * Yhdistetaan serveriin.
             */
            connectToServer.connect(onConnection);
        }]);
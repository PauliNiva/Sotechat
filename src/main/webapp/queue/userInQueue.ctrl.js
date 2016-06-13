/** Controlleri hallitsee ja seuraa käyttäjän  
 *  tilaa, kun käyttäjä odottaa jonossa
 **/
angular.module('chatApp')
    .controller('userInQueueCtrl', ['$scope', 'userStateService', 'connectToServer',
        function ($scope, userStateService, connectToServer) {
            /** Serverin mappaukset */
            var QUEUEADDRESS = '/toClient/queue/';
            /** Muuttuja johon tallennetaan yhteys kanavaan */
            var subscribeToQueue;

            /** Viestin saapuessa jonotuskanavalle 
             *  lopetetaan kanavan kuuntelu ja
             *  pyydetään käyttäjän tila päivitystä
             */
            var onMessage = function (response) {
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
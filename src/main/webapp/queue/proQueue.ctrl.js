/** Controlleri huolehtii ammattilaisen jonon tilasta 
 *  sekä siihen liittyvistä tapahtumista
 */
angular.module('chatProApp')
    .controller('proQueueCtrl', ['$scope', 'queueProService', 'stompSocket', 'connectToServer', 'proStateService',
        function ($scope, queueProService, stompSocket, connectToServer, proStateService) {
            /** Serverin mappaukset */
            var QUEUEADDRESS = '/toClient/';
            var CLIENTQUEUE = '/toClient/queue/';
            /** Jonoon liittyvien muuttujien ja tilatietojen alustus & esittely */
            $scope.queue = queueProService.queue;
            $scope.categories = queueProService.categories;
            $scope.selectedCategory = '';
            $scope.queueStatus = $scope.inQueue === 0;
            $scope.inQueue = 0;

            /** Odottaa parent conrollerilta serverin yhdistys eventtiä ja toimii sen saapuessa */
            $scope.$on('connectedToQueue', function (e) {
                connectToServer.subscribe(QUEUEADDRESS + proStateService.getQueueBroadcastChannel(), queue);
            });

            /** Tarkkailee jonon tilaa ja reagoi 
             * sen muuttuessa päivittämällä pituutta
             * controllerin vastaaviin muuttujiin.
             */
            $scope.$watch(function () {
                return queueProService.queue.length;
            }, function (lenght) {
                $scope.inQueue = lenght;
                $scope.queueStatus = $scope.inQueue === 0;
            }, true);

            /** Pyytää jonon elementtien vaihtoa kun käyttäjä vaihtaa valintaa */
            $scope.selectedItemChanged = function () {
                $scope.queue = queueProService.makeQueueByCategory($scope.selectedCategory);
            };

            /** Pyytää jonon ensimmäisen nostoa jonosta */
            $scope.nextFromQueue = function () {
                if ($scope.queue.length > 0 ) {
                    $scope.pickFromQueue($scope.queue[0].channelID);
                }
            };

            /** Nostaa annetun kanavaID:n perusteella jonosta asikkaan käsiteltäväksi */
            $scope.pickFromQueue = function (channelID) {
                var checkChannelID = queueProService.checkChannelID(channelID);
                if (checkChannelID != null) {
                    var checkIsPopOk = connectToServer.subscribe(CLIENTQUEUE + checkChannelID, function (response) {
                        if (JSON.parse(response.body).channelAssignedTo === proStateService.getUsername()) {
                            $scope.addChatTab(checkChannelID);
                        }
                        checkIsPopOk.unsubscribe();
                    });
                    stompSocket.send('/toServer/queue/' + checkChannelID, {}, '');
                }
            };

            /** Yhteyden muodustettua alustetaan jono 
             * ja päivitetään sen tila oikeaksi aina kun tila päivitys tulee 
             */
            var queue = function (response) {
                queueProService.clear();
                angular.forEach(JSON.parse(response.body).jono, function (key) {
                    queueProService.addToQueue(key);
                });
                $scope.selectedItemChanged();
            };

        }]);
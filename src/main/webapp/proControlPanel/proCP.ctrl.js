angular.module('chatApp')
    .controller('proCPController', ['$scope', 'connectToServer', 'proStateService', 'queueProService',
        function ($scope, connectToServer, proStateService, queueProService) {
            var QUEUEADDRESS = '/toClient/';
            var tabCount = 0;
            $scope.pro = true;
            $scope.inQueue = 0;
            $scope.chats = [];
            $scope.queue = queueProService.queue;
            $scope.queueStatus = $scope.inQueue === 0;


            $scope.$watch(function () {
                return queueProService.queue.length;
            }, function (lenght) {
                $scope.inQueue = lenght;
                $scope.queueStatus = $scope.inQueue === 0;
            }, true);

            var queue = function (response) {
                queueProService.clear();
                angular.forEach(JSON.parse(response.body).jono, function (key) {
                    console.log(key);
                    queueProService.addToQueue(key);
                    console.log(queueProService.queue);
                });
            };

            $scope.addChatTab = function (channelID) {
                $scope.chats.push({title: 'Chat' + tabCount++, channel: channelID});
            };

            var updateChannels = function () {
                tabCount = 1;
                $scope.chats = [];
                angular.forEach(proStateService.getChannelIDs(), function (key) {
                    $scope.chats.push({title: 'Chat' + tabCount, channel: key});
                    tabCount++;
                });
            };

            var answer = function () {

                connectToServer.subscribe(QUEUEADDRESS + proStateService.getQueueBroadcastChannel(), queue);
                $scope.username = proStateService.getUsername();
                updateChannels();
            };

            var init = function () {
                connectToServer.connect(answer);
            };


            proStateService.getVariablesFormServer().then(function (response) {
                proStateService.setAllVariables(response);
                init();
            });

        }]);
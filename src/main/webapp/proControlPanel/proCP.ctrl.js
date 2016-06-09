angular.module('chatApp')
    .controller('proCPController', ['$scope', 'connectToServer', 'proStateService', 'queueProService', 'heartBeatService',
        function ($scope, connectToServer, proStateService, queueProService) {
            var tabCount = 0;
            $scope.pro = true;
            $scope.chats = [];

            var initQueue = function () {
                $scope.$broadcast('connectedToQueue');
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
                initQueue();
                $scope.username = proStateService.getUsername();
                updateChannels();
            };

            var init = function () {
                connectToServer.connect(answer);
            };

            $scope.updateProStatus = function() {
                proStateService.getVariablesFormServer().then(function (response) {
                    proStateService.setAllVariables(response);
                    init();
                });
            };
            
            $scope.updateProStatus();
        }]);
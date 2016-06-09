angular.module('chatApp')
    .controller('proCPController', ['$scope','$timeout', 'connectToServer', 'proStateService', 'queueProService', 'heartBeatService',
        function ($scope, $timeout, connectToServer, proStateService, queueProService) {
            var tabCount = 0;
            $scope.pro = true;
            $scope.chats = [];
            $scope.active=tabCount+1;

            var initQueue = function () {
                $scope.$broadcast('connectedToQueue');
            };

            $scope.addChatTab = function (channelID) {
                $scope.chats.push({title: 'Chat' + tabCount++, channel: channelID});
                $timeout(function(){
                    $scope.active=tabCount-1;
                });
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
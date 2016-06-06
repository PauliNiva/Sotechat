angular.module('chatApp')
    .controller('proCPController', ['$scope', 'connectToServer', 'proStateService','queueProService',
        function ($scope, connectToServer, proStateService, queueProService) {
        var QUEUEADDRESS = '/toClient/';
        var tabCount = 0;
        $scope.pro = true;

        $scope.chats = [];
        $scope.queue = queueProService.queue;
        $scope.queueStatus = function () {$scope.queue.length === 0};
        $scope.inQueue = $scope.queue.length;

        var queue = function(response) {
            angular.forEach(JSON.parse(response.body).jono,function(key) {
                queueProService.addToQueue(key);
            });
        };

        $scope.addChatTab = function(channelID) {
            $scope.chats.push({title: 'Chat' + tabCount++, channel: channelID});
        };

        var updateChannels = function() {
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
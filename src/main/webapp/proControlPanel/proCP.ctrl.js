angular.module('chatApp')
    .controller('proCPController', ['$scope', 'connectToServer', 'proStateService', function ($scope, connectToServer, proStateService) {
        var QUEUEADDRESS = '/toClient/';
        $scope.pro = true;

        $scope.chats = [];
        $scope.queue = [];
        $scope.queueStatus = function () {$scope.queue.length === 0};
        $scope.inQueue = $scope.queue.length;

        var queue = function(response) {
            $scope.queue = [];
            angular.forEach(JSON.parse(response.body).jono,function(key) {
                var queueObject = [];
                queueObject.username = key.username;
                queueObject.channelID = key.channelId;
                queueObject.category = key.category;
                $scope.queue.push(queueObject);
            });
        };

        $scope.getChannelsFromServer = function() {
            proStateService.getVariablesFormServer().then(function (response) {
                proStateService.setAllVariables(response);
                updateChannels();
            });
        };

        var updateChannels = function() {
            var i = 1;
            $scope.chats = [];
            angular.forEach(proStateService.getChannelIDs(), function (key) {
                $scope.chats.push({title: 'Chat' + i, channel: key});
                i++;
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
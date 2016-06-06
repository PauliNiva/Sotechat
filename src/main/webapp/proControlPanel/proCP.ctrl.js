angular.module('chatApp')
    .controller('proCPController', ['$scope', 'connectToServer', 'proStateService', function ($scope, connectToServer, proStateService) {
        var QUEUEADDRESS = '/toClient/';
        $scope.pro = true;

        $scope.chats = [];
        $scope.queue = [];

        var queue = function(response) {
            $scope.queue = [];
            angular.forEach(JSON.parse(response.body).jono,function(key) {
                var queueObject = [];
                queueObject.username = key.username;
                queueObject.channelID = key.channelId;
                queueObject.category = key.category;
                $scope.queue.push(queueObject);
            });
            console.log($scope.queue)
        };

        var answer = function () {
            connectToServer.subscribe(QUEUEADDRESS + proStateService.getQueueBroadcastChannel(), queue);
            $scope.username = proStateService.getUsername();
            var i = 1;
            angular.forEach(proStateService.getChannelIDs(), function (key) {
                $scope.chats.push({title: 'Chat' + i, channel: key});
                i++;
            });
        };

        var init = function () {
            connectToServer.connect(answer);
        };


        proStateService.getVariablesFormServer().then(function (response) {
            proStateService.setAllVariables(response);
            init();
        });
    }]);
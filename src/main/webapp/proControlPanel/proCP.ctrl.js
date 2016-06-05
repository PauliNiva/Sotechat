angular.module('chatApp')
    .controller('proCPController', ['$scope', 'connectToServer', 'proStateService', function ($scope, connectToServer, proStateService) {
        var QUEUEADDRESS = '/toClient/';
        $scope.pro = true;

        $scope.chats = [];

        var queue = function(response) {
            console.log(response);
        };

        var answer = function () {
            connectToServer.subscribe(QUEUEADDRESS + proStateService.getQueueBroadcastChannel(), queue);
            $scope.username = proStateService.getUsername();
            var i = 1;
            console.log(proStateService.getChannelIDs());
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
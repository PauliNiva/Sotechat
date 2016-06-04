angular.module('chatApp')
    .controller('proCPController', ['$scope', 'connectToServer', 'proStateService', function ($scope, connectToServer, proStateService) {
        $scope.pro = true;

        $scope.chats = [];

        var answer = function () {
            connectToServer.subscribe(proStateService.getQueueBroadcastChannel());
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
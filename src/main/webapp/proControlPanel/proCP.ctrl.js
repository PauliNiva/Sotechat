angular.module('chatApp')
    .controller('proCPController', ['$scope', 'connectToServer', 'proStateService', function ($scope, connectToServer, proStateService) {
        $scope.pro = true;

        $scope.chats = [];

        var answer = function () {
            var i = 1;
            angular.forEach(JSON.parse(proStateService.getChannelIDs()), function (key) {
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
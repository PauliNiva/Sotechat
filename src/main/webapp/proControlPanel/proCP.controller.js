angular.module('chatApp')
    .controller('proCPController', ['$scope', 'connectToServer', 'queueService', function ($scope, connectToServer, queueService) {
        $scope.pro = true;
        
        $scope.chats = [];

        var answer = function () {
            $scope.chats.push({title:'Chat1'});
            $scope.chats.push({title:'Chat2', disabled: true });
        };

        var init = function () {
            connectToServer.connect(queueService.getChannelID(), answer);

        };


        queueService.getVariablesFormServer().then(function (response) {
            queueService.setAllVariables(response);
            init();
        });


    }]);
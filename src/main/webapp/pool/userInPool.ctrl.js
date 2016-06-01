angular.module('chatApp')
    .controller('userInPoolCtrl', ['$http', '$scope', '$location','queueService', 'connectToServer', function($http, $scope, $location, queueService, connectToServer) {
        var channelID;
        var userID;
        var userName

        var onMessage = function(response) {
            console.log(response);
            var parsed = JSON.parse(response.body);
            if (parsed.content === 'etene') {
                $location.path('/chat');
            }

        };
        
        var init = function () {
            connectToServer.connect(queueService.getChannelID(), onMessage);
        }


        var getVariables = function() {
            userName = queueService.getUserName();
            channelID = queueService.getChannelID();
            userID = queueService.getUserID();

            init();
        };



        queueService.getVariablesFormServer().then(function(response) {
            queueService.setAllVariables(response);
            init();
        });

    }]);
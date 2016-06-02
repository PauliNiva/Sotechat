angular.module('chatApp')
    .controller('userInPoolCtrl', ['$http', '$scope', '$location','queueService', 'connectToServer', 
        function($http, $scope, $location, queueService, connectToServer) {

            queueService.refreshState();
            
            var subscribeToQueue;
            var QUEUEADDRESS = '/toClient/';
    
            var onMessage = function(response) {
                var parsed = JSON.parse(response.body);
                if (parsed.content === 'etene') {
                    subscribeToQueue.unsubscribe();
                    $location.path('/chat');
                }
            };
    
            var onConnection = function () {
                subscribeToQueue = connectToServer.subscribe(QUEUEADDRESS + queueService.getChannelID(), onMessage);
            };
            
            var init = function () {
                connectToServer.connect(queueService.getChannelID(), onConnection);
            };
            
            queueService.getVariablesFormServer().then(function(response) {
                queueService.setAllVariables(response);
                init();
            });

    }]);
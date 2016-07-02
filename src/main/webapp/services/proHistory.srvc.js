/**
 * Hakee palvelimelta keskusteluhistoriat ja sen viestit.
 */
angular.module('chatProApp')
    .factory('proHistoryService', ['$http', function ($http) {
        
        function getHistory(){
            return $http.get('/listMyConversations/');
        }

        function getMessages(channelId){
            return $http.get('/getLogs/' + channelId);
        }
        
        var history = {
            getHistory: getHistory,
            getMessages: getMessages
        };
        
        return history;
    }]);
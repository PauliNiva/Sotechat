/**
 * Created by Asus on 17.6.2016.
 */

angular.module('chatProApp')
    .factory('proHistoryService', ['$http', function ($http) {
        
        function getHistory(){
            return $http.get('/history');
        }

        function getMessages(channelId){
            return $http.get('/messages/' + channelId);
        }
        
        var history = {
            getHistory: getHistory,
            getMessages: getMessages
        };
        
        return history;
    }]);
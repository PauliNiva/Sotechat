/**
 * Created by Asus on 17.6.2016.
 */

angular.module('chatProApp')
    .factory('proHistoryService', ['$http', function ($http) {
        
        var PROHISTORYURL = '/proHistory';
        
        function getHistoryPage(){
            return $http.get(PROHISTORYURL);
        };

        function getConversationPage(){
            return $http.get(PROHISTORYURL + '/Conversation');
        }

        function getHistory(userId){
            return $http.get('/history/' + '666');
        }

        function getMessages(channelId){
            return $http.get('/messages/' + channelId);
        }
        
        var history = {
            getHistoryPage: getHistoryPage,
            getConversationPage: getConversationPage,
            getHistory: getHistory,
            getMessages: getMessages
        };
        
        return history;
    }]);
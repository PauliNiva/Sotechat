/**
 * Created by Asus on 17.6.2016.
 */

angular.module('chatProApp')
    .factory('proHistoryService', ['http', function ($http) {
        
        var PROHISTORYURL = '/proHistory';
        
        function getHistoryPage(){
            return $http.get(PROHISTORYURL);
        };

        function getConversation(){
            return $http.get(PROHISTORYURL + '/Conversation');
        }

        function getHistory(userId){

        }

        function getMessages(channelId){
            return $http.get('/messages/' + channelId);
        }
        
        var history = {
            getHistoryPage: getHistoryPage,
            getConversation: getConversation,
            getHistory: getHistory,
            getMessages: getMessages
        };
        
        return history;
    }]);
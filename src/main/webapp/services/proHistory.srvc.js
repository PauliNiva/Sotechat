/**
 * Created by Asus on 17.6.2016.
 */

angular.module('chatProApp')
    .factory('proHistoryService', ['http', function ($http) {
        
        var PROHISTORYURL = '/proHistory';
        
        function getHistoryPage(){
            return $http.get(PROHISTORYURL);
        };

        function getConversation(channelId){
            return $http.get(PROHISTORYURL + '/Conversation');
        }

        function getHistory(){

        }

        function getMessages(channelId){

        }
        
        var history = {
            getHistory: getHistory,
            getConversation: getConversation
        };
        
        return history;
    }]);
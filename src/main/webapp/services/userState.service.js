angular.module('chatApp')
    .factory('queueService', ['$http', '$location', function ($http, $location) {
        var channelID;
        var userName;
        var userID;
        var userState;
        var queue = {
            getVariablesFormServer: getVariablesFormServer,
            setAllVariables: setAllVariables,
            setChannelID : setChannelID,
            setUserName : setUserName,
            setUserID : setUserID,
            setUserState : setUserState,
            getUserState: getUserState,
            getChannelID : getChannelID,
            getUserName : getUserName,
            getUserID : getUserID,
            refreshState : refreshState
        };

        function setChannelID(value) {
            channelID = value;
        };

        function setUserName(value) {
            userName = value;
        };

        function setUserState(value) {
            userState = value;
        };

        function setUserID(value) {
            userID = value;
        };

        function getChannelID() {
            return channelID;
        };

        function getUserName() {
            return userName;
        };

        function getUserID() {
            return userID;
        };

        function getUserState() {
            return userState;
        };

        function getVariablesFormServer() {
          return $http.get("/state");
        };

        function setAllVariables(response) {
            setUserName(response.data.userName);
            setChannelID(response.data.channelId);
            setUserID(response.data.userId);
            setUserState(response.data.state);
        };

        function refreshState() {
            getVariablesFormServer().then(function(response) {
                setAllVariables(response);
                if (getUserState() == "chat") $location.path('/chat');
                if (getUserState() == "pool") $location.path('/inQueue');
                else $location.path('/');
            });
        }
        
        return queue;
    }]);

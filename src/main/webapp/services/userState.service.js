angular.module('chatApp')
    .factory('queueService', ['$http', function ($http) {
        var channelID;
        var userName;
        var userID;
        var userState;

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
            if (userState === 'pool') {
                return 'pool/userInPool.tpl.html'
            } else if (userState === 'chat') {
                return 'chatWindow/userInChat.tpl.html'
            } else {
                return 'pool/userToPool.tpl.html';
            }
        };

        function getVariablesFormServer() {
          return $http.get("/userState");
        };

        function setAllVariables(response) {
            setUserName(response.data.userName);
            setChannelID(response.data.channelId);
            setUserID(response.data.userId);
            setUserState(response.data.state);
        };

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
            getUserID : getUserID
        };
        
        return queue;
    }]);

angular.module('chatApp')
    .factory('queueService', ['$http', function ($http) {
        var channelIDs = [];
        var userName;
        var userID;
        var userState;
        var queue = {
            getVariablesFormServer: getVariablesFormServer,
            setAllVariables: setAllVariables,
            setUserName : setUserName,
            setUserID : setUserID,
            getChannelIDs : getChannelIDs,
            getUserName : getUserName,
            getUserID : getUserID
        };

        function addChannelID(value) {
            channelIDs.push(value);
        };

        function setUserName(value) {
            userName = value;
        };


        function setUserID(value) {
            userID = value;
        };

        function getChannelIDs() {
            return channelIDs;
        };

        function getUserName() {
            return userName;
        };

        function getUserID() {
            return userID;
        };

        function addAllChannels(values) {
            angular.forEach(values, function(value, key) {
                addChannelID(value);
            });
        };

        function getVariablesFormServer() {
            return $http.get("/proState");
        };

        function setAllVariables(response) {
            setUserName(response.data.userName);
            addAllChannels(response.data.channelIds);
            setUserID(response.data.userId);
            setUserState(response.data.state);
        };

        return queue;
    }]);

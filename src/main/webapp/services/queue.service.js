angular.module('chatApp', [])
    .service('queueService', function () {
        var channelID;
        var userName;
        var userID;
        var queue = {
            setChannelID : setChannelID,
            setUserName : setUserName,
            setUserID : setUserID,
            getChannelID : getChannelID,
            getUserName : getUserName,
            getUserID : getUserID
        };

        function setChannelID(value) {
            channelID = value;
        };

        function setUserName(value) {
            userName = value;
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

        return queue;
    });

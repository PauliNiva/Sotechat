angular.module('chatApp')
    .factory('proStateService', ['$http', function ($http) {
        var PROSTATEURL = '/proState';
        var channelIDs = [];
        var username;
        var userID;
        var online;
        var qbcc;

        function addChannelID(value) {
            channelIDs.push(value);
        };

        function setUsername(value) {
            username = value;
        };

        function setUserID(value) {
            userID = value;
        };

        function setOnline(value) {
            online = value;
        };

        function setQueueBroadcastChannel(value) {
            qbcc = '/' + value;
        };

        function getOnline() {
            return online;
        };

        function getQueueBroadcastChannel() {
            return qbcc;
        };

        function getChannelIDs() {
            return channelIDs;
        };

        function getUsername() {
            return username;
        };

        function getUserID() {
            return userID;
        };

        function addAllChannels(values) {
            channelIDs = JSON.parse(values);
        };

        function getVariablesFormServer() {
            return $http.get(PROSTATEURL);
        };

        function setAllVariables(response) {
            setUsername(response.data.username);
            addAllChannels(response.data.channelIds);
            setUserID(response.data.userId);
            setQueueBroadcastChannel(response.data.qbcc);
            setOnline(response.data.online);
        };

        var pro = {
            getVariablesFormServer: getVariablesFormServer,
            setAllVariables: setAllVariables,
            getQueueBroadcastChannel: getQueueBroadcastChannel,
            getChannelIDs: getChannelIDs,
            getUsername: getUsername,
            getUserID: getUserID,
            getOnline: getOnline
        };

        return pro;
    }]);

/** Palvelu sailoo ammattilaisen tilatiedot,
 *  seka kanavat missa ammattilainen on keskustelemassa.
 */
angular.module('chatProApp')
    .factory('proStateService', ['$http', function ($http) {
        var PROSTATEURL = '/proState';
        var channelIDs = [];
        var username;
        var userID;
        var online;
        /**
         * Jonokanava
         */
        var qbcc;

        /** 
         * Getterit ja setterit 
         */
        function setUsername(value) {
            username = value;
        }

        function setUserID(value) {
            userID = value;
        }

        function setOnline(value) {
            online = value;
        }

        function setQueueBroadcastChannel(value) {
            qbcc = value;
        }

        function getOnline() {
            return online;
        }

        function getQueueBroadcastChannel() {
            return qbcc;
        }

        function getChannelIDs() {
            return channelIDs;
        }

        function getUsername() {
            return username;
        }

        function getUserID() {
            return userID;
        }

        /**
         * Asettaa ammattilaisen avonaiset kanavat halutuiksi.
         * @param values Lista avonaisista kanavista.
         */
        function addAllChannels(values) {
            channelIDs = JSON.parse(values);
        }

        /**
         * Hakee palvelimelta ammattilaisen tilatiedot.
         * @returns {HttpPromise}
         */
        function getVariablesFormServer() {
            return $http.get(PROSTATEURL);
        }

        /**
         * Alustaa tilatiedot annettun vastauksen mukaan.
         * @param response HTTP vastaus joka sisältaa tilatiedot.
         */
        function setAllVariables(response) {
            setUsername(response.data.username);
            addAllChannels(response.data.channelIds);
            setUserID(response.data.userId);
            setQueueBroadcastChannel(response.data.qbcc);
            setOnline(response.data.online);

            online = response.data.online === 'true';
        }
        
        function leaveChannel(channelID) {
            $http.post("/leave/" + channelID, {});
            var index = arrayIndexOf(channelIDs, channelID, 'channel');
            if (index > -1) {
                channelIDs.splice(index, 1);
            }
        }

        function addChannel(channelID) {
            channelIDs.push(channelID);
        }
        
        function setStatusOnline() {
            online = true;
            return $http.post('/setStatus/?online=true', {});
        }

        function setStatusOffline() {
            online = false;
            return $http.post('/setStatus/?online=false', {});
        }

        var arrayIndexOf = function (myArray, searchTerm) {
            for (var i = 0, len = myArray.length; i < len; i++) {
                if (myArray[i] === searchTerm) return i;
            }
            return -1;
        };

        var pro = {
            getVariablesFormServer: getVariablesFormServer,
            setAllVariables: setAllVariables,
            getQueueBroadcastChannel: getQueueBroadcastChannel,
            getChannelIDs: getChannelIDs,
            getUsername: getUsername,
            getUserID: getUserID,
            getOnline: getOnline,
            leaveChannel: leaveChannel,
            addChannel: addChannel,
            setStatusOnline: setStatusOnline,
            setStatusOffline: setStatusOffline
        };

        return pro;
    }]);

/** Palvelu huolehtii käyttäjän tilan ylläpitämiestä
 *  kontrolleri vaihdosten yli
 */
angular.module('chatApp')
    .factory('userStateService', ['$http', function ($http) {
        /** Esitellään parametrit */
        var channelID;
        var username;
        var userID;
        var userState;

        /** Getterit ja setterit */
        function setChannelID(value) {
            channelID = value;
        }

        function setUsername(value) {
            username = value;
        }

        function setUserState(value) {
            userState = value;
        }

        function setUserID(value) {
            userID = value;
        }

        function getChannelID() {
            return channelID;
        }

        function getUsername() {
            return username;
        }

        function getUserID() {
            return userID;
        }

        function getState() {
            return userState;
        }

        /** Palauttaa käyttäjän tilaavastaavan templaten osoitteen */
        function getUserState() {
            if (userState === 'queue') {
                return 'queue/userInQueue.tpl.html'
            } else if (userState === 'chat') {
                return 'chatWindow/userInChat.tpl.html'
            } else if (userState === 'pro') {
                return 'staticErrorPages/sameBrowserError.html'
            } else if (userState === 'closed') {
                return 'common/chatClosed.tpl.html'
            } else {
                return 'queue/userToQueue.tpl.html';
            }
        }
        
        /** Lähettää poistumis ilmoituksen serverille */
        function leaveChat() {
            $http.post("/leave/" + getChannelID(), {});
        }

        /** Hakee get-pyynnöllä palvelimelta käyttäjän tiedot */
        function getVariablesFormServer() {
            return $http.get("/userState");
        }

        /** asettaa vastauksessa tullet parametrit palveluun*/
        function setAllVariables(response) {
            setUsername(response.data.username);
            setChannelID(response.data.channelId);
            setUserID(response.data.userId);
            setUserState(response.data.state);
        }

        var queue = {
            getVariablesFormServer: getVariablesFormServer,
            setAllVariables: setAllVariables,
            setUserState: setUserState,
            getUserState: getUserState,
            getChannelID: getChannelID,
            getUsername: getUsername,
            getUserID: getUserID,
            leaveChat: leaveChat,
            getState: getState
        };

        return queue;
    }]);

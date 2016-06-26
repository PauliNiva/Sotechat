/** Palvelu huolehtii websocket yhteyden muodostus pyynnöstä
 *  palvelimeen ja pitää yhteyden elossa 
 *  kontrollerien välillä liikkuessa
 */
angular.module('commonMod')
    .service('connectToServer', ['stompSocket', '$timeout', function (stompSocket, $timeout) {
        /** Serverin mappaukset */
        var WEBSOCKETURL = '/toServer';
        /** Yhteyden tila */
        var connectionStatus = false;
        
        /** Yhteyden muodostamis pyyntö 
         *  Parametrina functio jota kutsutaan 
         *  kun yhteys muodostettu
         */
        function connect(answer) {
            if (!connectionStatus) {
                stompSocket.init(WEBSOCKETURL);
                stompSocket.connect(function (frame) {
                    connectionStatus = true;
                    answer();
                }, function (error) {
                    connectionStatus = false; //TODO: RECONNECT
                   // $timeout(function() {
                   //     connect(answer);
                   // }, 10000);
                });
            } else {
                answer();
            }
        }

        /** Funtio jolta voidaan pyytää kanavan tilaamista
         *  Parametrina kavan osoite sekä functio jota kutsutaan
         *  kun viestejä saapuu kanavalta
         */
        function subscribe(destination, answerFunction) {
            return stompSocket.subscribe(destination, function (response) {
                answerFunction(response);
            });
        }

        var socket = {
            connect: connect,
            subscribe: subscribe,
        };

        return socket;
    }]);
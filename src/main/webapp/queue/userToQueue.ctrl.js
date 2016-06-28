/** Controlleri hallitsee käyttäjän liittämistä jonoon, 
 *  kun käyttäjä on valinnut nimimerkin ja kirjoittanut aloitusviestin
 */
angular.module('chatApp')
    .controller('userToQueueCtrl', ['$http', '$scope', 'userStateService', function ($http, $scope, userStateService) {
        /** Serverin mappaukset */
        var JOINPOOLURL = '/joinPool';

        /** Kun käyttäjä painaa jonoon liittymispainiketta 
         *  Suoritetaan post pyyntö serverille ja toimitaan
         *  sen vastauksen mukaan.
         */
        $scope.joinQueue = function () {
            /** Onnistunut post pyyntö
             *  Käyttäjä pyytää tilapäivitystä ja toimii sen mukaan
             */
            var handleResponse = function (response) {
                if (response.data.content == "OK, please request new state now.") {
                    userStateService.getVariablesFormServer().then(function (response) {
                        userStateService.setAllVariables(response);
                        $scope.updateState();
                    });
                } else {
                    errorJoinQueue(response);
                }

            };
                /** Epäonnistunut post pyyntö
                 *  Alustetaan virheilmoitukset
                 */
            var errorJoinQueue = function (response) {
                    var err = "Tuntematon virhe";
                    if (response.data.content == "Denied join pool request for professional.") {
                        // Tässä casessa userState vaihtuu "pro":ksi ja kayttajan nakyma
                        // heitetaan staattiselle virhesivulle. Siksi tyhja blokki.
                    } else {
                        if (response.data.content == "Denied join pool request due to reserved username.") {
                            err = "Kayttajanimi on varattu. Kokeile toista nimea.";
                        } else if (response.data.content == "Denied join pool request. Username already on channel.") {
                            err = "Kanavalla on jo kayttaja samalla kayttajanimella. Kokeile toista nimea.";
                        } else if (response.data.content == "Denied join, no professionals available.") {
                            err = "Ammattilaisia ei ole juuri nyt paikalla.";
                        }
                        alert(err);
                    }
            }

            /** Suoritetaan post pyyntö palvelimelle
             *  Viesti sisältää halutun käyttäjänimen 
             *  Sekä aloutusviestin
             */
            $http.post(JOINPOOLURL, {'username': $scope.username, 'startMessage': $scope.startMessage})
                .then(handleResponse, errorJoinQueue);
        };
    }]);
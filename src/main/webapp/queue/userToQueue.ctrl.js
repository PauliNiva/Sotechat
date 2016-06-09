angular.module('chatApp')
    .controller('userToQueueCtrl', ['$http', '$scope', 'userStateService', function ($http, $scope, userStateService) {
        var JOINPOOLURL = '/joinPool';

        $scope.joinQueue = function () {
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

            var errorJoinQueue = function (response) {
                var err = "Tuntematon virhe";
                console.log("RESPONSE: " + response);
                if (response.data.content == "Denied join pool request due to reserved username.") {
                    err = "Kayttajanimi on varattu. Kokeile toista nimea.";
                } else if (response.data.content == "Denied join pool request. Username already on channel.") {
                    err = "Kanavalla on jo kayttaja samalla kayttajanimella. Kokeile toista nimea.";
                } else if (response.data.content == "Denied join pool request for professional.") {
                    // Tässä casessa userState vaihtuu "pro":ksi ja kayttajan nakyma
                    // heitetaan staattiselle virhesivulle. Siksi tyhja blokki.
                }
            }

            $http.post(JOINPOOLURL, {'username': $scope.username, 'startMessage': $scope.startMessage})
                .then(handleResponse, errorJoinQueue);
        };
    }]);
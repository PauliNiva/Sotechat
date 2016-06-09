angular.module('chatApp')
    .controller('userToQueueCtrl', ['$http', '$scope', 'userStateService', function ($http, $scope, userStateService) {
        var JOINPOOLURL = '/joinPool';
        $scope.joinQueue = function () {
            var successJoinQueue = function (response) {
                userStateService.getVariablesFormServer().then(function (response) {
                    userStateService.setAllVariables(response);
                    $scope.updateState();
                });
            };

            var errorJoinQueue = function (response) {
                var err = "Tuntematon virhe";
                if (response.data.content == "Denied join pool request due to reserved username.") {
                    err = "Kayttajanimi on varattu. Kokeile toista nimea.";
                } else if (response.data.content == "Denied join pool request for professional.") {
                    err = "Olet jo kirjautunut hoitajana. Jarjestelmamme ei voi tunnistaa sinua" +
                        "samaan aikaan seka hoitajaksi etta asiakkaaksi. Jos haluat testata" +
                        "chatin toimintaa, kokeile avata asiakasikkuna eri selaimessa tai" +
                        "selaimen incognito-tilassa. Siten jarjestelmamme nakisi 2 eri kayttajaa."
                }
                alert(err);
                console.log(err);
            };

            $http.post(JOINPOOLURL, {'username': $scope.username, 'startMessage': $scope.startMessage})
                .then(successJoinQueue, errorJoinQueue);
        };
    }]);
/**
 * Kontrolleri yllapitajan hallintapaneelin toimintoja varten.
 */
angular.module('chatProApp')
    .controller('adminController',
        ['$scope', '$http', '$uibModal', 'base64', 'adminService',
            function ($scope, $http, $uibModal, base64, adminService) {
                $scope.users = [];
                $scope.alerts = [];
                $scope.resetPsw = '';
                $scope.editPassword = '';
                $scope.adminView = 'admin/userHandling.tpl.html';
                $scope.newUser = {};

                /**
                 * Valitsee, parametrina annetun vastauksen perusteella ilmoituksen.
                 * @param response HTTP vastaus serverilta.
                 */
                var success = function (response) {
                    if (response.data.status === 'OK') {
                        $scope.alerts.push({
                            type: 'success',
                            dismiss: 4000,
                            msg: 'Toiminto onnistui!'
                        });
                        getUsers();
                    } else {
                        $scope.alerts.push({
                            type: 'danger',
                            msg: 'Toiminto ei onnistunut! ' + response.data.error
                        });
                    }
                };

                /**
                 * Shows AreUSure modal.
                 * @param sureFunction Function to run when sure.
                 */
                var makeSure = function(sureFunction) {
                    var modalInstance = $uibModal.open({
                        animation: true,
                        templateUrl: 'common/areUSureModal.tpl.html',
                        controller: 'AreUSureModalController'
                    });
                    modalInstance.result.then(sureFunction);
                };

                /**
                 * Vaihtaa nakyman asetuksiin.
                 */
                $scope.toSettings = function () {
                    $scope.adminView = 'admin/settings.tpl.html';
                };

                /**
                 * Vaihtaa nakyman asetusnakymaan.
                 */
                $scope.toUsers = function () {
                    $scope.adminView = 'admin/userHandling.tpl.html';
                };

                /**
                 * Valittaa palvelulle uudenkayttajan lisays pyynnon.
                 * Seka function, joka suoritetaan kun vastaus saapuu.
                 */
                $scope.createNewUser = function () {
                    adminService.createUser(base64.encode(JSON.stringify($scope.newUser)),
                        function (response) {
                        if (response.data.status === 'OK') {
                            $scope.newUserBoolean = false;
                            $scope.newUser = {};
                        }
                        success(response);
                    })
                };

                /**
                 * Kiinnittaa kayttajanID:n, jonka salasanaa muokataan.
                 * @param userID ID, jota muokataan.
                 */
                $scope.rpsw = function (userID) {
                    $scope.resetPsw = userID;
                };

                /**
                 * Valittaa palvelulle salasanan vaihto pyynnon.
                 * @param userID ID, jonka salasanaa muokataan.
                 * @param newPsw Salasana, joka laitetaan tilalle.
                 */
                $scope.doResetPsw = function (userID, newPsw) {
                    adminService.resetPassword(userID, base64.encode(newPsw), success);
                    $scope.resetPsw = '';
                    $scope.editPassword = '';
                };

                /**
                 * Valittaa kayttajanpoistamispyynnon.
                 * @param userID Kayttaja, jota ollaan poistamassa.
                 */
                $scope.removeUser = function (userID) {
                    makeSure(function() {
                        adminService.delUser(userID, success);
                    });
                };

                /**
                 * Aloittaa uuden kayttajan luomisen.
                 */
                $scope.newUserBoolTrue = function () {
                    $scope.newUserBoolean = true;
                };

                /**
                 * Peruuttaa kayttajan luomisen tai salasanan nollaamisen.
                 */
                $scope.cancelNewOrReset = function () {
                    $scope.newUserBoolean = false;
                    $scope.resetPsw = '';
                };

                /**
                 * Valitta palvelimen tilan resetointi pyynnon.
                 */
                $scope.resetServer = function () {
                    makeSure(function() {
                        adminService.resetDatabase(success);
                    });
                };

                /**
                 * Sulkee ilmoituksen.
                 * @param index Suljettavan ilmoituksen indeksi.
                 */
                $scope.closeAlert = function (index) {
                    $scope.alerts.splice(index, 1);
                };

                /**
                 * Pyytaa kayttajien hakua palvelulta ja lisää ne taulukkoon.
                 */
                var getUsers = function () {
                    $scope.users = [];
                    adminService.getUsers(function (response) {
                        angular.forEach(response.data, function (key) {
                            $scope.users.push(key);
                        });
                    });
                };
                
                /** Hakee kayttajat kun kontrolleri ladataan. */
                getUsers();
            }]);
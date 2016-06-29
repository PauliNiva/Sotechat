/**
 * Kontrolleri varmistus modalille.
 */
angular.module('commonMod')
    .controller('AreUSureModalController', function ($scope, $uibModalInstance) {
        /**
         * Käytäjä hyväksyy
         */
        $scope.ok = function () {
            $uibModalInstance.close(true);
        };

        /**
         * Käyttäjä hylkää
         */
        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    });
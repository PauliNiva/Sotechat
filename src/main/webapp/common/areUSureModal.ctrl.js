/**
 * Kontrolleri varmistus modalille.
 */
angular.module('commonMod')
    .controller('AreUSureModalController', function ($scope, $uibModalInstance) {
        /**
         * Kaytaja hyvaksyy.
         */
        $scope.ok = function () {
            $uibModalInstance.close(true);
        };

        /**
         * Kayttaja hylkaa.
         */
        $scope.cancel = function () {
            $uibModalInstance.dismiss('cancel');
        };
    });
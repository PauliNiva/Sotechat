angular.module('commonMod')
    .controller('AreUSureModalController', function ($scope, $uibModalInstance) {
    $scope.ok = function () {
        $uibModalInstance.close(true);
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
});
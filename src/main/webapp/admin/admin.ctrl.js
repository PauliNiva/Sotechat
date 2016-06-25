angular.module('chatProApp')
    .controller('adminController', ['$scope', '$http', '$uibModal', 'adminService',
        function ($scope, $http, $uibModal, adminService) {
            $scope.users = [];
            $scope.alerts = [];
            $scope.resetPsw = '';
            $scope.editPassword = '';

            var success = function(response) {
                if (response.data.status == 'OK') {
                    $scope.alerts.push({ type: 'success',dismiss: 4000,  msg: 'Toiminto onnistui!' })
                    getUsers();
                } else {
                    $scope.alerts.push({ type: 'danger', msg: 'Toiminto ei onnistunut! ' + response.data.error })
                }
            };
            
            $scope.createNewUser = function () {
                var user = '{"username": '+ $scope.newUserUsername + ', "loginName": '
                    + $scope.newUserLoginName +', "password": '
                    + $scope.newUserPassword+ '}';
                adminService.createUser(btoa(user), function(response) {
                    if (response.data.status != 'OK') {
                        $scope.newUserBoolean = false;
                        $scope.newUserUsername = '';
                        $scope.newUserPassword = '';
                        $scope.newUserLoginName = '';
                        success();
                    }
                })
            };

            $scope.rpsw = function(userID) {
                $scope.resetPsw = userID;
            };

            $scope.doResetPsw = function(userID, newPsw) {
                adminService.resetPassword(userID, btoa(newPsw), success);
                $scope.resetPsw = '';
                $scope.editPassword = '';
            };

            $scope.removeUser = function(userID) {
                var modalInstance = $uibModal.open({
                    animation: true,
                    templateUrl: 'common/areUSureModal.tpl.html',
                    controller: 'AreUSureModalController'
                });

                modalInstance.result.then(function (result) {
                    adminService.delUser(userID, success);
                });
            };

            $scope.cancelEditOrReset = function() {
                $scope.resetPsw = '';
            };

            $scope.closeAlert = function(index) {
                $scope.alerts.splice(index, 1);
            };
            
            var getUsers = function() {
                $scope.users = [];
                adminService.getUsers(function(response) {
                    angular.forEach(response.data, function (key) {
                        $scope.users.push(key);
                    });
                });
            };

            getUsers();
        }]);
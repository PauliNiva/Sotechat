angular.module('chatProApp')
    .factory('adminService', ['$http', function ($http) {

        function getUsers(callback) {
            $http.get('/getusers').then(callback);
        }

        function createUser(user , callback) {
            $http.post('/newuser', user).then(callback);
        }

        function delUser(userID, callback) {
            $http.delete('/delete/' + userID, {}).then(callback);
        }

        function resetPassword(userID, newPassword, callback) {
            $http.post('/resetpassword/' + userID, newPassword).then(callback);
        }

        function resetDatabase(callback) {
            $http.delete('/tuhoaHistoria', {}).then(callback);
        }

        var admin = {
            getUsers: getUsers,
            createUser: createUser,
            delUser: delUser,
            resetPassword: resetPassword,
            resetDatabase: resetDatabase
        };

        return admin;
    }]);
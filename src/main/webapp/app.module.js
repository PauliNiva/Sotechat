angular.module('chatApp', ['luegg.directives', 'ngRoute']);

angular.module('chatApp').config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
    $locationProvider.html5Mode(true);
    $routeProvider.when('/', {
        templateUrl: 'pool/userToPool.tpl.html', controller: 'userToPoolCtrl'
    }).when('/inQueue', {
        templateUrl: 'pool/userInPool.tpl.html', controller: 'userInPoolCtrl'
    }).when('/chat', {
        templateUrl: 'chatWindow/userInChat.tpl.html'
    }).otherwise({
        redirectTo: '/'
    });
}]);


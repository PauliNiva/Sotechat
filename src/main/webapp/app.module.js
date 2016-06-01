angular.module('chatApp', ['luegg.directives', 'ngRoute']);

angular.module('chatApp').config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
    $locationProvider.html5Mode(true);
    $routeProvider.when('/', {
        templateUrl: 'pool/userToPool.tpl.html', controller: 'userToPoolCtrl'
    }).when('/inQueue', {
        templateUrl: 'chatWindow/userInChat.tpl.html', controller: 'chatController'
    }).otherwise({
        redirectTo: '/'
    });
}]);


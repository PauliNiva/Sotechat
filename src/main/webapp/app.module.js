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

angular.module('chatApp').run(['$location', 'queueService', function ($location, queueService) {
    /**var state = queueService.getUserState();
    if (state === 'queue') {
        $location.path('/inQueue');
    } else if (state === 'chat') {
        $location.path('/chat');
    } else {
        $location.path('/');
    }*/
}]);


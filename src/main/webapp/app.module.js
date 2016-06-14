/** Alustataan angular moduuli ja liitetään siihen ulkopuoliset riippuvuus kirjastot */
angular.module('commonMod', ['luegg.directives', 'focus-if', 'ui.bootstrap', 'ngAnimate']);

angular.module('chatApp', ['luegg.directives', 'focus-if', 'ui.bootstrap', 'ngAnimate', 'commonMod']);

angular.module('chatProApp', ['luegg.directives', 'focus-if', 'ui.bootstrap', 'ngAnimate', 'commonMod', 'ngRoute', 'auth'])
    .config(
    function($routeProvider, $httpProvider, $locationProvider) {
        $locationProvider.html5Mode(true);
        $routeProvider.when('/', {
            templateUrl : 'proCP.html',
        }).when('/login', {
            templateUrl : 'login/login.tpl.html',
            controller : 'loginController',
            controllerAs : 'login'
        }).otherwise('/login');

        // $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';

    }).run(function(auth) {

    // Initialize auth module with the home page and login/logout path
    // respectively
    auth.init('/', '/login', '/logout');

});



/** Alustataan angular moduuli ja liitetään siihen ulkopuoliset riippuvuus kirjastot */
angular.module('commonMod', ['luegg.directives', 'focus-if', 'ui.bootstrap', 'ngAnimate']);

angular.module('chatApp', ['luegg.directives', 'focus-if', 'ui.bootstrap', 'ngAnimate', 'commonMod']);

angular.module('chatProApp', ['luegg.directives', 'focus-if', 'ui.bootstrap', 'ngAnimate', 'commonMod']).config(function($httpProvider) {
    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
});



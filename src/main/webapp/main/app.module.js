/** Alustataan angular moduuli ja liitetään siihen ulkopuoliset riippuvuus kirjastot */
angular.module('commonMod', ['luegg.directives', 'focus-if', 'ui.bootstrap', 'ngAnimate']);

angular.module('chatApp', ['luegg.directives', 'focus-if', 'ui.bootstrap', 'ngAnimate', 'commonMod']);


angular.module('chatProApp', ['luegg.directives', 'focus-if', 'ui.bootstrap', 'ngAnimate', 'commonMod','uiSwitch', 'ab-base64']).config(function($httpProvider) {
    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
});

// window.onbeforeunload = function (event) {
//     var message = 'Haluatko varmasti poistua?';
//     if (typeof event == 'undefined') {
//         event = window.event;
//     }
//     if (event) {
//         event.returnValue = message;
//     }
//     return message;
// };




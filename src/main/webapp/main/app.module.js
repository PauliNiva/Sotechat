/** Alustataan angular moduulit ja liitetään siihen ulkopuoliset riippuvuus kirjastot */

/** Moduuli yhteisille komponenteille */
angular.module('commonMod', ['luegg.directives', 'focus-if', 'ui.bootstrap', 'ngAnimate']);

/** Moduuli käyttäjän komponenteille */
angular.module('chatApp', ['luegg.directives', 'focus-if', 'ui.bootstrap', 'ngAnimate', 'commonMod']);

/** Moduuli ammattilaisen komponenteille */
angular.module('chatProApp', ['luegg.directives', 'focus-if', 'ui.bootstrap', 'ngAnimate', 'commonMod','uiSwitch', 'ab-base64']).config(function($httpProvider) {
    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
});





/**
 * Direktiivi havaitsee "ENTER"-painikkeen painallukset kentässä estää normaalintoiminnan
 * Jos painetaan SHIFT+ENTER tehdään normaali ENTER Toiminto
 * Käytetään estämään textarea kentän normaali rivin vaihto.
 */
angular.module('chatApp')
    .directive('ngEnter', function () {
    return function (scope, element, attrs) {
        element.bind("keydown keypress", function (event) {
            if (event.which === 13 && !event.shiftKey) {
                scope.$apply(function () {
                    scope.$eval(attrs.ngEnter);
                });
                event.preventDefault();
            }
        });
    };
});
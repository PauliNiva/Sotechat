/**
 * Direktiivi havaitsee "ENTER"-painikkeen painallukset kentassa ja estaa normaalintoiminnan.
 * Jos painetaan SHIFT+ENTER tehdaan normaali ENTER Toiminto.
 * Kaytetaan estamaan textarea kentan normaali rivin vaihto.
 */
angular.module('commonMod')
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
/**
 * Kontrolleri keskustelun kiinni olemis näkymälle.
 */
angular.module('chatApp')
    .controller('chatClosedController', function ($scope, $interval, userStateService) {
        $scope.color = 'redBg';
        var iBackGround;
        var iCheckStatus;
        var stop = function() {
            $interval.cancel(iBackGround);
            $interval.cancel(iCheckStatus);
        };
        var init = function() {
            /**
             * Vaihtelee taustan väriä 4 s välein.
             */
            iCheckStatus = $interval(function() {
                if ($scope.color === 'whiteBg') {
                    $scope.color = 'redBg';
                } else {
                    $scope.color = 'whiteBg';
                }
            }, 4000);
            /**
             * Tarkistaa chatin tilan 20s välein, kunnes se ei ole enää suljettu.
             */
            iBackGround = $interval(function() {
                $scope.updateState();
                if (userStateService.getState() !== 'closed') {
                    stop();
                }
            }, 20000);
        };
        init();
    });
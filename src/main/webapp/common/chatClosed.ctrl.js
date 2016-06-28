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
            iCheckStatus = $interval(function() {
                if ($scope.color === 'whiteBg') {
                    $scope.color = 'redBg';
                } else {
                    $scope.color = 'whiteBg';
                }
            }, 4000);

            iBackGround = $interval(function() {
                $scope.updateState();
                if (userStateService.getState() !== 'closed') {
                    stop();
                }
            }, 10000);
        };
        init();

    });
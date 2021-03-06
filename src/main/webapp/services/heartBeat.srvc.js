/**
 * The purpose of this module is to keep the HTTP Session alive
 * as long as the browser is open (even when data is only transmitted on WS).
 */
angular.module('commonMod')
    .factory('heartBeatService', ['$http', '$interval', function ($http, $interval) {
        var HEARTBEATURL = '/toServer/heartBeat/';
        var msFreq = 1000000; // 16min

        var postHeartBeat = function () {
            $http.post(HEARTBEATURL, {'heartbeat': 'client alive'});
        };

        $interval(postHeartBeat, msFreq);

        var heartBeat = {};
        return heartBeat;
    }]);
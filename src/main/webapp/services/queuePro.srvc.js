angular.module('chatApp')
    .factory('queueProService', [function () {
        var queue = [];
        var length = 0;

        var getFirstChannelID = function () {
            if (queue.length > 0) {
                return queue[0].channelID;
            }
            return null;
        }

        var addToQueue = function (key) {
            var queueObject = [];
            queueObject.username = key.username;
            queueObject.channelID = key.channelId;
            queueObject.category = key.category;
            queue.push(queueObject);
            length++;
        };

        var getLength = function () {
            return length;
        };

        var clear = function () {
            queue.length = 0;
            length = 0;
        };

        var queueService = {
            getFirstChannelID: getFirstChannelID,
            addToQueue: addToQueue,
            clear: clear,
            queue: queue,
            getLength: getLength
        };
        return queueService;
    }]);
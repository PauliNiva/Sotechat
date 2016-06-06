angular.module('chatApp')
    .factory('queueProService', ['stompSocket', function (stompSocket) {
        var queue = [];
        var length = 0;

        var removeFirstFromQueue = function(){
            if (queue.length > 0) {
                var first = queue[0];
                queue.splice(0,1);
                length--;
                return first;
            }
        };

        var addToQueue = function(key) {
            var queueObject = [];
            queueObject.username = key.username;
            queueObject.channelID = key.channelId;
            queueObject.category = key.category;
            queue.push(queueObject);
            length++;
        };

        var getLength = function() {
            return length;
        };

        var queueService = {
            removeFirstFromQueue:removeFirstFromQueue,
            addToQueue:addToQueue,
            queue:queue,
            getLength:getLength
        };
        return queueService;
    }]);
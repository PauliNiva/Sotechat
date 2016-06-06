angular.module('chatApp')
    .service('queueProService', ['stompSocket', function (stompSocket) {
        var queue = [];

        var removeFirstFromQueue = function(){
            if (queue.length > 0) {
                var first = queue[0];
                queue.splice(0,1);
                return first;
            }
        };

        var addToQueue = function(key) {
            var queueObject = [];
            queueObject.username = key.username;
            queueObject.channelID = key.channelId;
            queueObject.category = key.category;
            queue.push(queueObject);
        };

        var getQueue = function() {
            return queue;
        };

        var queueService = {
            removeFirstFromQueue:removeFirstFromQueue,
            addToQueue:addToQueue,
            queue:queue
        };
        return queueService;
    }]);
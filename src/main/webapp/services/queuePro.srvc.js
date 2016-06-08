angular.module('chatApp')
    .factory('queueProService', ['stompSocket', function (stompSocket) {
        var queue = [];
        var length = 0;

        var removeFirstFromQueue = function(){
            if (queue.length > 0) {
                var first = queue[0];
                queue.splice(0,1);
                console.log(queue);
                length--;
                return first;
            }
            return null;
        };

        var addToQueue = function(key) {
            var queueObject = [];
            queueObject.username = key.username;
            queueObject.channelID = key.channelId;
            queueObject.category = key.category;
            queue.push(queueObject);
            console.log("moi2");
            length++;
        };

        var getLength = function() {
            return length;
        };

        var clear = function () {
            queue.length = 0;
            console.log("moi1");
            length = 0;
        };

        var queueService = {
            removeFirstFromQueue:removeFirstFromQueue,
            addToQueue:addToQueue,
            clear:clear,
            queue:queue,
            getLength:getLength
        };
        return queueService;
    }]);
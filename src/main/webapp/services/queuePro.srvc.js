angular.module('chatApp')
    .factory('queueProService', [function () {
        var queue = [];
        var categorys = [];
        categorys.push('Kaikki');
        var length = 0;

        var getFirstChannelID = function () {
            if (queue.length > 0) {
                return queue[0].channelID;
            }
            return null;
        }
        
        var checkChannelID = function (channelID) {
            for(var i=0; i < queue.length; i++) {
                console.log(queue[i].channelID);
                console.log(channelID);
                console.log(queue[i].channelID === channelID);
                if(queue[i].channelID === channelID) return queue[i].channelID;
            }
            return null;
        };

        var addCategory = function(category) {
            var boolean = true;
            for(var i=0; i < categorys.length; i++) {
                if(categorys[i] === category) boolean=  false;
            }
            if (boolean) {
                categorys.push(category);
            }
        }

        var addToQueue = function (key) {
            var queueObject = [];
            queueObject.username = key.username;
            queueObject.channelID = key.channelId;
            queueObject.category = key.category;
            addCategory(queueObject.category);
            queue.push(queueObject);
            length++;
        };

        var getLength = function () {
            return length;
        };

        var clear = function () {
            queue.length = 0;
            categorys.length = 0;
            categorys.push('Kaikki');
            console.log(categorys);
            length = 0;
        };

        var queueService = {
            getFirstChannelID: getFirstChannelID,
            checkChannelID:checkChannelID,
            addToQueue: addToQueue,
            clear: clear,
            queue: queue,
            categorys:categorys,
            getLength: getLength
        };
        return queueService;
    }]);
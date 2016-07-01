/** 
 * Palvelu huolehtii ammattilaiselle nakyvan jonon yllapitamisesta,
 * seka sailomisesta.
 */
angular.module('chatProApp')
    .factory('queueProService', [function () {
        /** Alustetaan taulukot jonolle ja kategorioille */
        var queue = [];
        var categories = [];
        var length = 0;

        /**
         * Palauttaa jonon ensimmaisen kanavanId:n.
         * Jos jono tyhja niin palauttaa null.
         * @returns {*}
         */
        var getFirstChannelID = function () {
            if (queue.length > 0) {
                return queue[0].channelID;
            }
            return null;
        };

        /**
         * Hakee jonosta haluttua kategoriaa vastaavat alkiot.
         * @param category kategoria joka halutaan palautettavaksi.
         * @returns {Array} Alkiot jotka kuuluvat kategoriaan.
         * Jos kategoria tyhja niin palautetaan koko jono.
         */
        var makeQueueByCategory = function (category) {
            if (category === "") return queue;
            var subQueue = [];
            for (var i = 0; i < queue.length; i++) {
                if (queue[i].category === category) {
                    subQueue.push(queue[i]);
                }
            }
            return subQueue;
        };

        /**
         * Tarkastaa onko annettu kanavaID jonossa.
         * @param channelID KanavaID jonka tilanne tahdotaan tarkastaa.
         * @returns {*} palautetaan sama kanavaID jos löydetty muuten null.
         */
        var checkChannelID = function (channelID) {
            for (var i = 0; i < queue.length; i++) {
                if (queue[i].channelID === channelID) {
                    return queue[i].channelID;
                }
            }
            return null;
        };

        /**
         * Lisataan kategoria taulukkoon, jos se ei jo ennestaan sisalla sita.
         * @param category Kategoriannimi joka tahdotaan lisata.
         */
        var addCategory = function (category) {
            var boolean = true;
            for (var i = 0; i < categories.length; i++) {
                if (categories[i] === category) {
                    boolean = false;
                }
            }
            if (boolean) {
                categories.push(category);
            }
        };

        /**
         * Lisaa jonotaulukkoon annetun jono objectin,
         * kun se on alustettu.
         * @param object Lisattava jono objekti.
         */
        var addToQueue = function (object) {
            var queueObject = [];
            queueObject.username = object.username;
            queueObject.channelID = object.channelId;
            queueObject.category = object.category;
            addCategory(queueObject.category);
            queue.push(queueObject);
            length++;
        };

        /**
         * Palauttaa jonon pituuden.
         * @returns {number} jonon pituus
         */
        var getLength = function () {
            return length;
        };

        /** 
         * Tyhjentaa jonon ja kategoriat kokonaan.
         */
        var clear = function () {
            queue.length = 0;
            categories.length = 0;
            length = 0;
        };

        var queueService = {
            getFirstChannelID: getFirstChannelID,
            checkChannelID: checkChannelID,
            addToQueue: addToQueue,
            makeQueueByCategory: makeQueueByCategory,
            clear: clear,
            queue: queue,
            categories: categories,
            getLength: getLength
        };

        return queueService;
    }]);
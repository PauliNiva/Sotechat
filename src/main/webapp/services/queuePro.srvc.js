/** Palvelu huolehtii ammattilaiselle näkyvän jonon ylläpitämisestä
 *  Sekä säilömisestä
 */
angular.module('chatProApp')
    .factory('queueProService', [function () {
        /** Alustetaan taulukot jonolle ja kategorioille */
        var queue = [];
        var categories = [];
        var length = 0;

        /**
         * Palauttaa jonon ensimmäisen kanavanId:n
         * Jos jono tyhjä niin palauttaa null
         * @returns {*}
         */
        var getFirstChannelID = function () {
            if (queue.length > 0) {
                return queue[0].channelID;
            }
            return null;
        };

        /**
         * Hakee jonosta haluttua kategoriaa vastaavat alkiot
         * @param category kategoria joka halutaan palautettavaksi
         * @returns {Array} Alkiot jotka kuuluvat kategoriaan.
         * Jos kategoria tyhjä niin palautetaan koko jono
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
         * Tarkastaa onko annettu kanavaID jonossa
         * @param channelID KanavaID jonka tilanne tahdotaan tarkastaa
         * @returns {*} palautetaan sama kanavaID jos löydetty muuten null
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
         * Lisätään kategoria taulukkoon, jos se ei jo ennestään sisällä sitä
         * @param category kategorian nimi joka tahdotaan lisätä
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
         * Lisää jono taulukkoon annetun jono objectin
         * kun se on alustettu
         * @param object lisättävä jono objekti
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
         * Palauttaa jonon pituuden
         * @returns {number} jonon pituus
         */
        var getLength = function () {
            return length;
        };

        /** Tyhjentää jonon ja kategoriat kokonaan */
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
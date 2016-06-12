describe('queueProService tests', function () {
    var proStateService;
    beforeEach(module('chatApp'));
    beforeEach(function () {

        inject(function (_$httpBackend_, _queueProService_) {
            queueProService = _queueProService_;
        });

    });

    it('should have functions and arrays', function () {
        expect(angular.isFunction(queueProService.getFirstChannelID)).toBe(true);
        expect(angular.isFunction(queueProService.checkChannelID)).toBe(true);
        expect(angular.isFunction(queueProService.addToQueue)).toBe(true);
        expect(angular.isFunction(queueProService.makeQueueByCategory)).toBe(true);
        expect(angular.isFunction(queueProService.clear)).toBe(true);
        expect(angular.isFunction(queueProService.getLength)).toBe(true);

        expect(angular.isArray(queueProService.queue)).toBe(true);
        expect(angular.isArray(queueProService.categories)).toBe(true);
    });


    it('init: arrays empty', function () {
        expect(queueProService.queue.length).toEqual(0);
        expect(queueProService.categories.length).toEqual(0);
        expect(queueProService.getLength()).toEqual(0);
    });

    it('addQueue works', function () {
        var testi = [];
        testi.channelId ='testi';
        testi.username = 'nimi';
        testi.category = 'kate';

        queueProService.addToQueue(testi);
        
        expect(queueProService.queue.length).toEqual(1);
        expect(queueProService.categories.length).toEqual(1);
        expect(queueProService.getLength()).toEqual(1);
    });

    it('getFirstChannelID returns null if queue empty', function () {
        expect(queueProService.getFirstChannelID()).toEqual(null);
    });

    it('getFirstChannelID returns firstID', function () {
        var testi = [];
        testi.channelId ='testi';
        testi.username = 'nimi';
        testi.category = 'kate';
        queueProService.addToQueue(testi);
        expect(queueProService.getFirstChannelID()).toEqual('testi');
        testi.channelId ='testi1';
        testi.username = 'nimi';
        testi.category = 'kate';
        queueProService.addToQueue(testi);
        expect(queueProService.getFirstChannelID()).toEqual('testi');
    });

    it('checkChannelID returns null if not in array', function () {
        expect(queueProService.checkChannelID('moi')).toEqual(null);
    });

    it('checkChannelID returns channelid if contains', function () {
        var testi = [];
        testi.channelId ='testi';
        testi.username = 'nimi';
        testi.category = 'kate';
        queueProService.addToQueue(testi);
        expect(queueProService.checkChannelID('testi')).toEqual('testi');
        testi.channelId ='testi1';
        testi.username = 'nimi';
        testi.category = 'kate';
        queueProService.addToQueue(testi);
        expect(queueProService.checkChannelID('testi1')).toEqual('testi1');
        expect(queueProService.checkChannelID('ewerw')).toEqual(null);
    });

    it('makeQueueByCategory returns empty array if queue empty', function () {
        expect(queueProService.makeQueueByCategory('kate')).toEqual([]);
        expect(queueProService.makeQueueByCategory('')).toEqual([]);
    });

    it('makeQueueByCategory returns category array', function () {
        var testi = [];
        testi.channelId ='testi';
        testi.username = 'nimi';
        testi.category = 'kate';
        queueProService.addToQueue(testi);
        expect(queueProService.makeQueueByCategory('kate').length).toEqual(1);
        expect(queueProService.makeQueueByCategory('kate1').length).toEqual(0);
        expect(queueProService.makeQueueByCategory('').length).toEqual(1);
        testi.channelId ='testi1';
        testi.username = 'nimi';
        testi.category = 'kate1';
        queueProService.addToQueue(testi);
        expect(queueProService.makeQueueByCategory('kate').length).toEqual(1);
        expect(queueProService.makeQueueByCategory('kate1').length).toEqual(1);
        expect(queueProService.makeQueueByCategory('').length).toEqual(2);
    });

    it('clear queue works', function () {
        var testi = [];
        testi.channelId ='testi';
        testi.username = 'nimi';
        testi.category = 'kate';
        queueProService.addToQueue(testi);
        expect(queueProService.queue.length).toEqual(1);
        expect(queueProService.categories.length).toEqual(1);
        expect(queueProService.getLength()).toEqual(1);
        queueProService.clear();
        expect(queueProService.queue.length).toEqual(0);
        expect(queueProService.categories.length).toEqual(0);
        expect(queueProService.getLength()).toEqual(0);
    });


});
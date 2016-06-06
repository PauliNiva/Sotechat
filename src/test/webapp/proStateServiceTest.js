describe('proStateService tests', function () {
    var proStateService;
    beforeEach(module('chatApp'));
    beforeEach(function () {

        inject(function (_$httpBackend_, _proStateService_) {
            proStateService = _proStateService_;
            $httpBackend = _$httpBackend_;
        });

    });

    it('should have functions', function () {
        expect(angular.isFunction(proStateService.getUserID)).toBe(true);
        expect(angular.isFunction(proStateService.getChannelIDs)).toBe(true);
        expect(angular.isFunction(proStateService.getUsername)).toBe(true);
        expect(angular.isFunction(proStateService.getOnline)).toBe(true);
        expect(angular.isFunction(proStateService.getVariablesFormServer)).toBe(true);
        expect(angular.isFunction(proStateService.setAllVariables)).toBe(true);
        expect(angular.isFunction(proStateService.getQueueBroadcastChannel)).toBe(true);
    });
    

    it('ProState setters & getters test', function () {
        var status = {'username': 'uu', 'channelIds': '["id", "id2"]', 'userId': 'uid', 'online': 'online'};
        $httpBackend.expectGET('/proState').respond(status);
        proStateService.getVariablesFormServer().then(function(response) {
            proStateService.setAllVariables(response);
        });
        $httpBackend.flush();
        expect(proStateService.getOnline()).toEqual('online');
        expect(proStateService.getChannelIDs()).toEqual(["id", "id2"]);
        expect(proStateService.getUsername()).toEqual('uu');
        expect(proStateService.getUserID()).toEqual('uid');
    });


});
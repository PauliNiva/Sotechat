describe('userStateService tests', function () {
    var userStateService;
    beforeEach(module('chatApp'));
    beforeEach(function () {

        inject(function (_$httpBackend_, _userStateService_) {
            userStateService = _userStateService_;
            $httpBackend = _$httpBackend_;
        });

    });

    it('should have functions', function () {
        expect(angular.isFunction(userStateService.getUserID)).toBe(true);
        expect(angular.isFunction(userStateService.getChannelID)).toBe(true);
        expect(angular.isFunction(userStateService.getUsername)).toBe(true);
        expect(angular.isFunction(userStateService.getUserState)).toBe(true);
        expect(angular.isFunction(userStateService.getVariablesFormServer)).toBe(true);
        expect(angular.isFunction(userStateService.setAllVariables)).toBe(true);
    });

    it('UserState test', function () {
        userStateService.setUserState('random');
        expect(userStateService.getUserState()).toEqual('queue/userToQueue.tpl.html');
        userStateService.setUserState('queue');
        expect(userStateService.getUserState()).toEqual('queue/userInQueue.tpl.html');
        userStateService.setUserState('chat');
        expect(userStateService.getUserState()).toEqual('chatWindow/userInChat.tpl.html');
    });

    it('UserState setters & getters', function () {
        var status = {'username': 'uu', 'channelId': 'id', 'userId': 'uid', 'state': 'queue'};
        $httpBackend.expectGET('/userState').respond(status);
        userStateService.getVariablesFormServer().then(function(response) {
            userStateService.setAllVariables(response);
        });
        $httpBackend.flush();
        expect(userStateService.getUserState()).toEqual('queue/userInQueue.tpl.html');
        expect(userStateService.getChannelID()).toEqual('id');
        expect(userStateService.getUsername()).toEqual('uu');
        expect(userStateService.getUserID()).toEqual('uid');
    });
    

});
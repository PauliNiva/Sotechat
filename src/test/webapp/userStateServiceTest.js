describe('userStateService tests', function () {
    var userStateService;
    beforeEach(module('chatApp'));
    beforeEach(function () {

        inject(function (_userStateService_) {
            userStateService = _userStateService_;
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


});
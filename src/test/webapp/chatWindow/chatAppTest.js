"use strict";
describe('ChatController', function() {
    var $scope, ctrl;
    var mockService;
    var data = '\{"userName":"Anon","channelId":"0","timeStamp":"2016-05-24T06:58:54.794Z","content":"Kirjoitan jotain"\}';

    beforeEach(function() {
        mockService = jasmine.createSpyObj('stompSocket', ['init', 'connect']);
        module('chatApp');
        inject(function ($rootScope, $controller) {
            $scope = $rootScope.$new();
            ctrl = $controller('chatController', {
                $scope: $scope,
                stompSocket: mockService
            });
        });
    });

    it('Messages empty', function() {
        $scope.message = '';
        var testi = $scope.getMessage('\{"message":"testi"}');
        expect($scope.message).toEqual("");
    });

    it("testi", function() {
       var message =  $scope.getMessage(data);
        expect(message.sender).toBe("Anon")
    });
});


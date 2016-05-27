"use strict";
describe("ChatController", function() {
    var scope, ctrl, httpBackend, form, element;
    var stompSocket;
    var data = '\{"userName":"Anon","channelId":"0","timeStamp":"2016-05-24T06:58:54.794Z","content":"Kirjoitan jotain"\}';

    beforeEach(function() {
        stompSocket = jasmine.createSpyObj('stompSocket', ['joinToChat', 'connect', 'send']);
        module('chatApp');
        inject(function ($rootScope, $controller, $compile, $httpBackend, $http) {
             element = angular.element(
                '<form name="messageForm">' +
                '<textarea ng-model="message" ng-enter="sendMessage();" required name="messageArea"></textarea>' +
                '</form>'
            );
            stompSocket = {
                send: function() {}
            };

            spyOn(stompSocket, 'send').and.returnValue('true');
            scope = $rootScope.$new();
            httpBackend = $httpBackend;
            httpBackend.when("GET", "/join").respond([{}, {}, {}]);

            ctrl = $controller('chatController', {
                $scope: scope,
                $http: $http,
                stompSocket: stompSocket
            });
            $compile(element)(scope);
            scope.$digest();
        });
    });

    it('Messages empty at te begining', function() {
        expect(scope.messages).toEqual([]);
    });

    it("Can't sent empty message", function() {
        scope.message = "";
        scope.sendMessage();
        expect(stompSocket.send).not.toHaveBeenCalled();
    });

    it("Can sent message", function() {
        angular.element(element).val('Testi').trigger('input');
        scope.$apply();
        scope.sendMessage();
        expect(stompSocket.send).toHaveBeenCalled();
    });
});


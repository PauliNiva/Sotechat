"use strict";
describe("ProChatController", function() {
    var scope, ctrl, httpBackend, form, element;
    var stompSocket;
    var connectToServer;

    beforeEach(function() {
        stompSocket = jasmine.createSpyObj('stompSocket', ['subscribe', 'connect', 'send']);
        connectToServer = jasmine.createSpyObj('connectToServer', ['subscribe', 'connect']);
        module('chatProApp');
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

            ctrl = $controller('proChatController', {
                $scope: scope,
                $http: $http,
                stompSocket: stompSocket,
                connectToServer:connectToServer
            });
            $compile(element)(scope);
            scope.$digest();
        });
    });

    it('Messages empty at te begining', function() {
        expect(scope.messages).toEqual([]);
    });

    it('prochatController pro varianle is true', function() {
        expect(scope.pro).toEqual(true);
    });

    it("Can't sent empty message", function() {
        scope.message = "";
        scope.sendMessage();
        expect(stompSocket.send).not.toHaveBeenCalled();
    });

    it("Can sent message", function() {
        scope.message = "testi";
        scope.$apply();
        scope.sendMessage();
        expect(stompSocket.send).toHaveBeenCalled();
    });
});


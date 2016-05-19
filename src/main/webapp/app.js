var app = angular.module('chatApp', []);

app.controller('chat', function($scope) {
    $scope.messages = [];

    $scope.addMessage = function() {
        var message = [];
        message.message = $scope.message;
        message.time = Date.now();
        message.id = Math.floor(Math.random() * 1000000);
        message.sender = "Minä";
        message.I = true;
        $scope.messages.push(message);
        $scope.message = "";
    };
    var init =  function(){
        var message = [];
        message.message = "Hei, tervetuloa .. Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh";
        message.time = Date.now();
        message.id = Math.floor(Math.random() * 1000000);
        message.sender = "Ammattilainen";
        message.I = false;
        $scope.messages.push(message);
    };
    init();
});

app.directive('ngEnter', function() {
    return function(scope, element, attrs) {
        element.bind("keydown keypress", function(event) {
            if(event.which === 13 && !event.shiftKey) {
                scope.$apply(function(){
                    scope.$eval(attrs.ngEnter);
                });
                event.preventDefault();
            }
        });
    };
});




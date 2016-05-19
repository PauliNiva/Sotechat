var app = angular.module('chatApp', []);
var stompClient = null;




app.service('connect', function($q) {
    var service = {}, listener = $q.defer(), socket = {
        client: null,
        stomp: null
    }, messageIds = [];
var dataVariable;
var connect = function(data, $scope) {
    dataVariable = data;
    var socket = new SockJS('/toServer');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/toClient/'+data.channelId, function(greeting){
            console.log(JSON.parse(greeting.body));
            listener.notify(JSON.parse(greeting.body));
        });
    });
}
     var receive =  function(){
         return listener.promise;
    }

    function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

var sendConversation = function(text) {
    stompClient.send("/toServer/"+dataVariable.channelId, {}, JSON.stringify({ 'userId': dataVariable.userId, 'channelId': dataVariable.channelId, 'content': text }));
}

function showGreeting(message) {
    var response = document.getElementById('response');
    var p = document.createElement('p');
    p.style.wordWrap = 'break-word';
    p.appendChild(document.createTextNode(message));
    response.appendChild(p);
}
    return service;
});
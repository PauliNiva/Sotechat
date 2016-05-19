var stompClient = null;

function setConnected(connected) {
  //  document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
  //  document.getElementById('response').innerHTML = '';
}

var dataVariable;

function connect(data, $scope) {
    dataVariable = data;
    var socket = new SockJS('/toServer');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/toClient/'+data.channelId, function(greeting){
            console.log(JSON.parse(greeting.body));
            $scope.message.push(JSON.parse(greeting.body).content);
        });
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendConversation(text) {
    stompClient.send("/toServer/"+dataVariable.channelId, {}, JSON.stringify({ 'userId': dataVariable.userId, 'channelId': dataVariable.channelId, 'content': text }));
}

function showGreeting(message) {
    var response = document.getElementById('response');
    var p = document.createElement('p');
    p.style.wordWrap = 'break-word';
    p.appendChild(document.createTextNode(message));
    response.appendChild(p);
}
// Kontrolleri päivittää tietoja molempiin suuntiin:
// - Kun Serviceltä tulee viesti, kontrolleri päivittää selaimessa olevan näkymän.
// - Kun halutaan lähettää viesti, välitetään se Servicelle.
// TODO: Kuvaile $scope
angular.module('chatApp.controllers', ['luegg.directives'])
    .controller('chatController', ['$scope', '$location', '$interval', 'stompSocket', '$http', function ($scope, $location, $interval, stompSocket, $http) {
        // Taulukko "messages" sisältää chat-ikkunassa näkyvät viestit.
        $scope.messages = [];
        // Muuttujat joihin tallennetaan channelId ja user id
        var channelId;
        var userId;
        // Määritellään chatin nimi templateen, tällä hetkellä kovakoodattu
        this.chatName = 'Esimerkki';

        /** Funktio lähettää servicen avulla tekstikentän
         *  sisällön ja lopuksi tyhjentää tekstikentän. */
        $scope.sendMessage = function () {
            if ($scope.messageForm.$valid) {
                var destination = "/toServer/" + channelId;
                stompSocket.send(destination, {}, JSON.stringify(
                    {
                        'userId': userId,
                        'channelId': channelId,
                        'content': $scope.message
                    }));
                $scope.message = '';
            }
        };
        
        /** Funktio parsee viestin haluttuun muotoon. */
        var getMessage = function (data) {
            var parsed = JSON.parse(data);
            console.log(parsed);
            var message = [];
            message.message = parsed.content;
            message.time = parsed.timeStamp;
            message.sender = parsed.userName;
            /** TODO: If author == self then Message.I = true
             *       Vaikuttaa viestien asemointiin vasen/oikea. */
            message.I = true;
            return message;
        }
        

        /** Kun tämä JS ladataan, tehdään GET-pyyntö polkuun /join.
         *  Näin kerrotaan palvelimelle, että haluamme chattiin. */
        var joinToChat = function () {
            $http.get("/join").then(function(response) {
                username = response.data.userName;
                channelId = response.data.channelId;
                userId = response.data.userId;
                console.log(username + " " + channelId + " " + userId);
                initStompClient();
            })
        };


        var initStompClient = function () {
            stompSocket.init('/toServer');
            stompSocket.connect(function (frame) {
                stompSocket.subscribe("/toClient/" + channelId, function (message) {
                    $scope.messages.push(getMessage(message.body));
                });
            /*    stompSocket.subscribe("/topic/", function(message) {
                    console.log(JSON.parse(message.body));
                });*/
            }, function (error) {
                initStompClient();
            });
        };
        joinToChat();
    }]);
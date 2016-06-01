// Kontrolleri päivittää tietoja molempiin suuntiin:
// - Kun Serviceltä tulee viesti, kontrolleri päivittää selaimessa olevan näkymän.
// - Kun halutaan lähettää viesti, välitetään se Servicelle.
//
angular.module('chatApp')
    .controller('chatController', ['$scope', '$location', '$interval', 'stompSocket', '$http', 'queueService',
        function ($scope, $location, $interval, stompSocket, $http, queueService) {
            // Taulukko "messages" sisältää chat-ikkunassa näkyvät viestit.
            $scope.messages = [];
            // Muuttujat joihin tallennetaan channelId ja user id
            var channelID;
            var userID;
            var userName;
            // Määritellään chatin nimi templateen, tällä hetkellä kovakoodattu
            this.chatName = 'Esimerkki';

            /** Funktio lähettää servicen avulla tekstikentän
             *  sisällön ja lopuksi tyhjentää tekstikentän. */
            $scope.sendMessage = function () {
                if ($scope.messageForm.$valid) {
                    var destination = "/toServer/" + channelID;

                    stompSocket.send(destination, {}, JSON.stringify(
                        {
                            'userId': userID,
                            'channelId': channelID,
                            'content': $scope.message
                        }));
                    $scope.message = '';
                }
            };

            /** Funktio parsee viestin haluttuun muotoon. */
            var getMessage = function (data) {
                var parsed = JSON.parse(data);
                var message = [];
                message.content = parsed.content;
                message.time = parsed.timeStamp;
                message.sender = parsed.userName;
                message.I = message.sender === userName;
                return message;
            };

            var initStompClient = function () {
                stompSocket.init('/toServer');
                stompSocket.connect(function (frame) {
                    console.log(channelID);
                    stompSocket.subscribe("/toClient/" + "dev", function (response) {
                        $scope.messages.push(getMessage(response.body));
                    });
                }, function (error) {
                    initStompClient();
                });
            };

            /** Kun tämä JS ladataan, tehdään GET-pyyntö polkuun /join.
             *  Näin kerrotaan palvelimelle, että haluamme chattiin. */
            var joinToChat = function () {
                $http.get("/join").then(function (response) {
                    userName = response.data.userName;
                    channelId = response.data.channelId;
                    userId = response.data.userId;
                    initStompClient();
                })
            };

            var getVariables = function() {
                userName = queueService.getUserName();
                channelID = queueService.getChannelID();
                userID = queueService.getUserID();
                initStompClient();
            };

            getVariables();
        }]);
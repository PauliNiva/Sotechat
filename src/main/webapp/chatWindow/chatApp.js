// Tiedostossa clientin puolen toiminnallisuus.
// Toteutettu AngularJS-appina, jolla 2 komponenttia:
// Service ja Controller

var chatApp = angular.module('chatApp', ['luegg.directives']);
// Mitä "luegg.directives" tekee?

// Kontrolleri päivittää tietoja molempiin suuntiin:
// - Kun Serviceltä tulee viesti, kontrolleri päivittää selaimessa olevan näkymän.
// - Kun halutaan lähettää viesti, välitetään se Servicelle.
// TODO: Kuvaile $scope
chatApp.controller('chatController', function ($scope, ChatService) {
    // Taulukko "messages" sisältää chat-ikkunassa näkyvät viestit.
    $scope.messages = [];
    // Muuttujan chatName teksti näytetään sivulla tyylitettynä.
    this.chatName = "Esimerkki chat"

    /** Funktio antaa servicelle lähetettäväksi tekstikentän
     *  sisällön ja lopuksi tyhjentää tekstikentän. */
    $scope.sendMessage = function () {
        if ($scope.messageForm.$valid) {
            ChatService.send($scope.message);
            $scope.message = "";
        }
    };

    /**
     * Viestejä vastaanottaessa pushataan uusi viesti taulukkoon
     * messages, joka järjestetään timeStamppien mukaisesti.
     * TODO: Nykyinen tapa vastaanottaa viestejä on hyvin bloatti.
     * Miten viestien vastaanottaminen toimii?
     *      -> Client avaa sivun index.html, joka ajaa tämän JS tiedoston
     *      -> JS avaa listenerin $q.defer() (??)
     *      -> JS tekee GET-pyynnön polkuun /join
     *      -> Palvelin vastaa mm. kanavaID:llä
     *      -> Client subscribaa annettuun kanavaID:hen
     *          (STOMP over Websocket over TCP)
     *      -> Kun kanavalle tulee viestejä, kutsutaan listenerin notify:ta
     *      -> ???
     *      -> Alla oleva metodi maagisesti ajetaan oikealla parametrillä.
     * Vastaanottomekanismi sama kuin g00glen00bin tutoriaalissa.
     * Salman Khanin projektissa on yksinkertaisempi mekanismi,
     * mutta se on saavutettu tekemällä yksi iso komponentti.
     * Tällä komponenttijaolla yksinkertaisempi tapa ei toiminut
     * muuttujien näkyvyyden vuoksi.
     */
    ChatService.receive().then(null, null, function (message) {
        // TODO: Selvitä nullien merkitys.
        $scope.messages.push(message);
    });

    /** Init vain heittää alla näkyvän viestin chattiin. */
    var init = function () {
        var message = [];
        message.message = "Hei, tervetuloa .. Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh";
        message.time = Date.now();
        message.sender = "Ammattilainen";
        message.I = false;
        $scope.messages.push(message);
    };

    init();
});


/** Service hoitaa tietoliikenteen
 *   (yhdistämisen ja viestien välittämisen JSONeina)
 *   (over STOMP over Websockets over TCP/IP) */
chatApp.service("ChatService", function ($q, $timeout) {

    var service = {}, listener = $q.defer(), socket = {
        client: null,
        stomp: null
    }, messageIds = [];

    service.RECONNECT_TIMEOUT = 30000;

    // Viestien vastaanottomekanismi kuvailtu controllerissa.
    service.receive = function () {
        return listener.promise;
    };

    /** Kontrolleri kutsuu tätä halutessaan lähettää viestin. */
    service.send = function (text) {
        var id = Math.floor(Math.random() * 1000000);
        socket.stomp.send("/toServer/" + service.channelId, {}, JSON.stringify({
            'userId': service.userId,
            'channelId': service.channelId,
            'content': text
        }));
        messageIds.push(id);
    };

    /** Reconnect ei testattu. */
    var reconnect = function () {
        $timeout(function () {
            initialize();
        }, this.RECONNECT_TIMEOUT);
    };

    /** Funktio parsee viestin haluttuun muotoon. */
    var getMessage = function (data) {
        var parsed = JSON.parse(data);
        var message = [];
        message.message = parsed.content;
        message.time = parsed.timeStamp;
        /** Mitä varten message.id on? */
        message.id = Math.floor(Math.random() * 1000000);
        message.sender = parsed.userName;
        /** TODO: If author == self then Message.I = true
          *       Vaikuttaa viestien asemointiin vasen/oikea. */
        message.I = true;
        return message;
    };

    /** Tämä funktio ajetaan vain chattiin liittymisen yhteydessä. */
    var startListener = function ($scope) {
        /** Subscribetään kanavalle, jonka ID saatiin palvelimelta. */
        socket.stomp.subscribe('/toClient/' + service.channelId, function (data) {
            /** Tämä anonyymi funktio ajetaan aina, kun clientille saapuu viesti. */
            console.log(JSON.parse(data.body));
            listener.notify(getMessage(data.body));
        });
    };

    /** Kun tämä JS ladataan, tehdään GET-pyyntö polkuun /join.
     *  Näin kerrotaan palvelimelle, että haluamme chattiin. */
    var initialize = function () {
        $.get("/join", function (data) {
            /** Asynkroninen funktiokutsu = selain ei jäädy odotellessa vastausta */
            service.channelId = data.channelId;
            service.userId = data.userId;
            socket.client = new SockJS('/toServer');
            socket.stomp = Stomp.over(socket.client);
            socket.stomp.connect({}, startListener);
            socket.stomp.onclose = reconnect;
        });
        /** Jos tähän kirjoittaa koodia, se ajetaan ennen ylläolevaa blokkia. */
    };
    initialize();
    // miksi return service?
    return service;
});

// Tekstin syöttö ja lähettäminen entterillä. TODO: tarkempi kuvaus.
chatApp.directive('ngEnter', function () {
    return function (scope, element, attrs) {
        element.bind("keydown keypress", function (event) {
            if (event.which === 13 && !event.shiftKey) {
                scope.$apply(function () {
                    scope.$eval(attrs.ngEnter);
                });
                event.preventDefault();
            }
        });
    };
});




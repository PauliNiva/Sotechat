$(document).ready(function() {
    disconnect();
    $.get("/join", function( data ) {
        connect(data);
    });
});
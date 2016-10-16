$(function() {
    $.ajax({
        url: '/search-definitions/index',
        type: 'GET',
        dataType: 'json'
    }).done(function(response) {
        var $searchResult = $('#searchDefinitions');

        for (var i=0; i<response.length; i++) {
            var def = response[i];
            $searchResult.append('<li><a href="/page/search/' + def.id + '">' + def.name + '</a></li>');
        }
    });
});

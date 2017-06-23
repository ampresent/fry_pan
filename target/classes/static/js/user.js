$('#follow-btn').on('click', function() {
    $.ajax({
        type :  "GET",
        data : $.param([{name:'username', value:$("meta[name='username']").attr('context')}, {name:'_csrf', value:$("meta[name='_csrf']").attr('context')}]),
        url : '/user/follow',
        success : function() {$(this).children().toggleClass('glyphicon-ok')},
        error: function(xhr) { console.log(xhr.responseText); }
    })
})
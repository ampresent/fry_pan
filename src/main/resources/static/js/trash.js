//$('.list-group-item').on('click', function() {
//    var $this = $(this);
//    $this.parent().find('.active').removeClass('active')
//    $this.toggleClass('active');
//})
//
//$('.panel-item').on('click', function() {
//    var $this = $(this);
//    $this.toggleClass('active')
//})
//
function restore_garbages(ids) {
    if (ids.length > 0) {
        var req = "ids=" + ids.join("&ids=");
        $.ajax({
            type: "POST",
            beforeSend: function(request) {
                request.setRequestHeader("X-CSRF-TOKEN", $("meta[name='_csrf']").attr("content"));
            },
            url : "/trash/restore",
            data: req,
            success: function() {
                window.location.reload();
            },
            error: function(xhr) { console.log(xhr.responseText); }
        })
    }
}
//
//$('#clear-tasks').on('click', function() {
//    $.ajax({
//        type: "GET",
//        beforeSend: function(request) {
//            request.setRequestHeader("X-CSRF-TOKEN", $("meta[name='_csrf']").attr("content"));
//        },
//        url : "/offline/clear",
//        data: null,
//        success: function() {
//            window.location.reload();
//        },
//        error: function(xhr) { console.log(xhr.responseText); }
//    })
//})

$('#restore-btn').on('click', function() {
    var ids;
    ids = $('.panel-item.active').find('.garbage-id').map(function(){
        return $.trim($(this).text());
    }).get();
    restore_garbages(ids);
})

$('.restore-single').on('click', function() {
    id = $(this).parent().siblings('.garbage-id').text();
    restore_garbages([id]);
})

//$('#share-btn').on('click', function() {
//    var selected = $('.file-entry.active')
//    if (selected.length > 0) {
//        var path = getPath() + selected.text()
//        data = $.param([{name:'path', value:path}, {name:'_csrf', value:$("meta[name='_csrf']").attr("content")}])
//        $.ajax({
//            type : "POST",
//            url : "/share/create",
//            data : data,
//            success : function() {
//                window.location.href = "/share";
//            },
//            error: function(xhr) { console.log(xhr.responseText); }
//        })
//    }
//})
//
//$('#delete-btn').on('click', function() {
//    var selected = $('.file-entry.active')
//    if (selected.length > 0) {
//        var path = getPath() + selected.text()
//        data = $.param([{name:'path', value:path}, {name:'_csrf', value:$("meta[name='_csrf']").attr("content")}])
//        $.ajax({
//            type : "POST",
//            url : "/file/delete",
//            data : data,
//            success : function() {
//                window.location.reload()
//            },
//            error: function(xhr) { console.log(xhr.responseText); }
//        })
//    }
//})
//
//function remove_shares(ids) {
//    if (ids.length > 0) {
//        $.ajax({
//            type : "POST",
//            data : $.param([{name:"ids", value:[ids]}, {name:'_csrf', value:$("meta[name='_csrf']").attr("content")}]),
//            url : "/share/delete",
//            success : function() {
//                window.location.reload()
//            },
//            error : function(xhr) {console.log(xhr.responseText);}
//        })
//    }
//}
$('#clear-btn').on('click', function() {
    $.ajax({
        type: "GET",
        beforeSend: function(request) {
            request.setRequestHeader("X-CSRF-TOKEN", $("meta[name='_csrf']").attr("content"));
        },
        url : "/trash/clear",
        data: null,
        success: function() {
            window.location.reload();
        },
        error: function(xhr) { console.log(xhr.responseText); }
    })
})
//
//$('#remove-shares').on('click', function() {
//    var ids;
//    ids = $('.panel-item.active').find('.share-id').map(function(){
//        return $.trim($(this).text());
//    }).get();
//    remove_shares(ids);
//})
//
//$('.cancel-share').on('click', function() {
//    id = $(this).parent().siblings(".share-id").text()
//    remove_shares([id])
//})
//
//function getPath() {
//    var name = "path";
//    var file = decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search) || [null, ''])[1].replace(/\+/g, '%20')) || null;
//    if (file == null) { file = "/"; }
//    else { file = file.replace(/\/*$/, "") + "/" }
//    return file;
//}
//
//$('.file-entry').on('click', function() {
//	$('.file-entry').find('img').remove()
//    var file = getPath() + $(this).text();
//    $(this).prepend('<img src="/thumb/get?file='+file+'" height="48"/>');
//})
//
//$('.dir-entry').on('click', function() {
//	$('.file-entry').find('img').remove()
//})
//
//$('.file-entry').dblclick(function() {
//    var file = getPath();
//    window.location = "/file/access?file=" + file + $(this).text();
//})
//
//$('.dir-entry').dblclick(function() {
//    var path = getPath()
//    window.location = "/disk/access?path=" + path + $(this).text();
//})
//
//$('.backbtn').on("click", function() {
//    window.location = "/disk/access?path=" + getPath().replace(/[^/]*\/*$/, "");
//})


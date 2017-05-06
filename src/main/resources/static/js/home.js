$('.popover-btn').popover({
    html: true,
    title: function () {
        return $(this).next().html();
    },
    content: function () {
        return $(this).next().next().html();
    }
}).on('shown.bs.popover', function () {
    var $this = $(this)
    $(this).next().find('.close-btn').on('click', function () {
        $this.popover('hide')
    })
    $('#create-new-folder').on('click', function() {
        $.ajax({
            type: 'POST',
            url: "/folder/create",
            data: $('#mkdir-form').serialize(),
            success: function(data)
            {
                window.location.reload();
            }
        });
        return false;
    });
    $('#start-offline').on('click', function() {
        $.ajax({
            type: 'POST',
            url: "/offline/download",
            data: $('#offline-form').serialize(),
            success: function(data)
            {
                window.location.reload();
            }
        });
        return false;
    });
});
$('#upload-file').on('change', function() {
    var formData = new FormData($( "#upload-form" )[0]);
    $.ajax({
        type: 'POST',
        url: "/file/upload",
        contentType: false,
        processData: false,
        data: formData, //$('#upload-form').serialize(), //formData,
        success: function(data) { window.location.reload(); },
        error: function(xhr) { console.log(xhr.responseText); }
    });
    return false;
});
$(".popover-btn").on("click", function () {
    if ($(this).siblings(".popover").length) return;
    $(this).popover("toggle")
})

$('.list-group-item').on('click', function() {
    var $this = $(this);
    $this.parent().find('.active').removeClass('active')
    $this.toggleClass('active');
})

$('.panel-item').on('click', function() {
    var $this = $(this);
    $this.toggleClass('active')
})

$('#remove-tasks').on('click', function() {
    var ids;
    ids = $('.panel-item.active').find('.offline-id').map(function(){
        return $.trim($(this).text());
    }).get();
    if (ids.length > 0) {
        var req = "ids=" + ids.join("&ids=");
        $.ajax({
            type: "POST",
            beforeSend: function(request) {
                request.setRequestHeader("X-CSRF-TOKEN", $("#input-csrf").val());
            },
            url : "/offline/pop",
            data: req,
            success: function() {
                window.location.reload();
            },
            error: function(xhr) { console.log(xhr.responseText); }
        })
    }
})

function getURLParameter(name) {
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search) || [null, ''])[1].replace(/\+/g, '%20')) || null;
}

$('.file-entry').dblclick(function() {
    var file = getURLParameter("path")
    if (file == null) {
        window.location = "/file/access?file=/" + $(this).text();
    } else {
        window.location = "/file/access?file=" + file.replace(/\/*$/, "") + "/" + $(this).text();
    }
})

$('.dir-entry').dblclick(function() {
    var path = getURLParameter("path")
    if (path == null) {
        window.location = "/disk/access?path=/" + $(this).text();
    } else {
        window.location = "/disk/access?path=" + path.replace(/\/*$/, "") + "/" + $(this).text();
    }
})

$('.backbtn').on("click", function() {
    window.location = "/disk/access?path=" + getURLParameter("path").replace(/[^/]*\/*$/, "");
})


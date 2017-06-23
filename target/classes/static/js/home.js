
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


    $('.secure-file').on('change', function() {
        key = localStorage.getItem('secure-key')
        if (key == null) {
            window.location.reload();
            return false;
        }
        plain = $("meta[name='val-plain']").attr("content");
        enc = $("meta[name='val-enc']").attr("content");
        if (plain == null || enc == null) {
            secupload(this.files[0], sjcl.codec.base64.fromBits(sjcl.random.randomWords(32)))
        }
        localStorage.setItem("secure-key", key);
        return false;
    });

    $('#re-secure-key').on('input', function(){
        key = $('#secure-key').val()
        rekey = this.value
        if (key == rekey) {
            plain = $("meta[name='val-plain']").attr("content");
            enc = $("meta[name='val-enc']").attr("content");
            if (plain != null && enc != null) {
                try {
                    if (sjcl.decrypt(key, enc) == plain) {
                        check = $(this).parent().parent().prepend('<i class="glyphicon glyphicon-check" style="font-size: 200%; display: none;"> Validated</i>')
                        $(this).parent().siblings('i').fadeIn(500)
                        $(this).parent().fadeOut(500)
                        localStorage.setItem("secure-key", key)
                    }
                } catch (e) {
                }
            } else {
                localStorage.setItem("secure-key", key)
            }
        }
    })
});
$('#upload-file').on('change', function() {
    var formData = new FormData($( "#upload-form" )[0]);
    var reader = new FileReader();
    reader.addEventListener(
        'load',
         function () {
            var hash = md5(this.result);
            formData.append("hash", hash);
            $.ajax({
                type: 'POST',
                url: "/file/upload",
                contentType: false,
                processData: false,
                data: formData, //$('#upload-form').serialize(), //formData,
                success: function(data) { window.location.reload(); },
                error: function(xhr) { console.log(xhr.responseText); }
            });
         }
    )
    reader.readAsBinaryString(this.files[0]);
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

function remove_task(ids) {
    if (ids.length > 0) {
        var req = "ids=" + ids.join("&ids=");
        $.ajax({
            type: "POST",
            beforeSend: function(request) {
                request.setRequestHeader("X-CSRF-TOKEN", $("meta[name='_csrf']").attr("content"));
            },
            url : "/offline/pop",
            data: req,
            success: function() {
                window.location.reload();
            },
            error: function(xhr) { console.log(xhr.responseText); }
        })
    }
}

$('#clear-tasks').on('click', function() {
    $.ajax({
        type: "GET",
        beforeSend: function(request) {
            request.setRequestHeader("X-CSRF-TOKEN", $("meta[name='_csrf']").attr("content"));
        },
        url : "/offline/clear",
        data: null,
        success: function() {
            window.location.reload();
        },
        error: function(xhr) { console.log(xhr.responseText); }
    })
})

$('#remove-tasks').on('click', function() {
    var ids;
    ids = $('.panel-item.active').find('.offline-id').map(function(){
        return $.trim($(this).text());
    }).get();
    remove_task(ids);
})

$('.cancel-offline').on('click', function() {
    id = $(this).parent().siblings('.offline-id').text();
    remove_tasks([id]);
})

$('#share-btn').on('click', function() {
    var selected = $('.file-entry.active')
    if (selected.length > 0) {
        var path = getPath() + selected.text()
        data = $.param([{name:'path', value:path}, {name:'_csrf', value:$("meta[name='_csrf']").attr("content")}])
        $.ajax({
            type : "POST",
            url : "/share/create",
            data : data,
            success : function() {
                window.location.href = "/share";
            },
            error: function(xhr) { console.log(xhr.responseText); }
        })
    }
})

$('#delete-btn').on('click', function() {
    var selected = $('.file-entry.active')
    if (selected.length > 0) {
        var path = getPath() + selected.text()
        data = $.param([{name:'path', value:path}, {name:'_csrf', value:$("meta[name='_csrf']").attr("content")}])
        $.ajax({
            type : "POST",
            url : "/file/delete",
            data : data,
            success : function() {
                window.location.reload()
            },
            error: function(xhr) { console.log(xhr.responseText); }
        })
    }
})

function remove_shares(ids) {
    if (ids.length > 0) {
        $.ajax({
            type : "POST",
            data : $.param([{name:"ids", value:[ids]}, {name:'_csrf', value:$("meta[name='_csrf']").attr("content")}]),
            url : "/share/delete",
            success : function() {
                window.location.reload()
            },
            error : function(xhr) {console.log(xhr.responseText);}
        })
    }
}
$('#clear-shares').on('click', function() {
    $.ajax({
        type: "GET",
        beforeSend: function(request) {
            request.setRequestHeader("X-CSRF-TOKEN", $("meta[name='_csrf']").attr("content"));
        },
        url : "/share/clear",
        data: null,
        success: function() {
            window.location.reload();
        },
        error: function(xhr) { console.log(xhr.responseText); }
    })
})

$('#remove-shares').on('click', function() {
    var ids;
    ids = $('.panel-item.active').find('.share-id').map(function(){
        return $.trim($(this).text());
    }).get();
    remove_shares(ids);
})

$('.cancel-share').on('click', function() {
    id = $(this).parent().siblings(".share-id").text()
    remove_shares([id])
})

function getPath() {
    var name = "path";
    var file = decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search) || [null, ''])[1].replace(/\+/g, '%20')) || null;
    if (file == null) { file = "/"; }
    else { file = file.replace(/\/*$/, "") + "/" }
    return file;
}

$('.file-entry').on('click', function() {
	$('.file-entry').find('img').remove()
    var file = getPath() + $(this).text();
    $(this).prepend('<img src="/thumb/get?file='+file+'" height="48"/>');
})

$('.dir-entry').on('click', function() {
	$('.file-entry').find('img').remove()
})

$('.file-entry').dblclick(function() {
    var file = getPath();
    window.location = "/file/access?file=" + file + $(this).text();
})

$('.dir-entry').dblclick(function() {
    var path = getPath()
    window.location = "/disk/access?path=" + path + $(this).text();
})

$('.backbtn').on("click", function() {
    window.location = "/disk/access?path=" + getPath().replace(/[^/]*\/*$/, "");
})


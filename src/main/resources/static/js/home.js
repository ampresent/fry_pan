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


function getURLParameter(name) {
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search) || [null, ''])[1].replace(/\+/g, '%20')) || null;
}
$('.file-entry').dblclick(function() {
    window.location = "/file/access?file=" + getURLParameter("file").replace(/\/*$/, "") + "/" + $(this).text();
})

$('.dir-entry').dblclick(function() {
    window.location = "/disk/access?path=" + getURLParameter("path").replace(/\/*$/, "")  + "/" + $(this).text();
})

$('.backbtn').on("click", function() {
    window.location = "/disk/access?path=" + getURLParameter("path").replace(/[^/]*\/*$/, "");
})

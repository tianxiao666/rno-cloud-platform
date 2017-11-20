
/**
 * 定义全局对象
 */
var bs = $.extend({}, bs);

/**
 * 添加tab页
 * @param addTabs({ id: $(this).attr("id"), title: $(this).attr('title'), close: true, url:''});
 */
bs.addTabs = function (options) {
    var id = "tab_" + options.id;
    var active_flag = false;
    if ($("#" + id)) {
        active_flag = $("#" + id).hasClass('active');
    }
    $(".active").removeClass("active");

    //如果TAB不存在，创建一个新的TAB
    if (!$("#" + id)[0]) {
        //固定TAB中IFRAME高度
        var mainHeight = $(document.body).height() - 200;

        //创建新TAB的title
        var title = '<li id="tab_' + id + '"><a href="#' + id
            + '" style="margin-top:-10px" role="tab" data-toggle="tab">' + options.title;

        //是否允许关闭
        if (options.close) {
            title += ' <i class="glyphicon glyphicon-remove" tabclose="' + id + '"></i>';
        }
        title += '</a></li>';

        //是否指定TAB内容
        var content;
        if (options.content) {
            content = '<div role="tabpanel" class="tab-pane" id="' + id + '" style="height:100%">' + options.content
                + '</div>';
        } else {//没有内容，使用IFRAME打开链接
            content = '<div role="tabpanel" class="tab-pane active" id="' + id + '" style="height:100%">' +
                '<iframe id="iframe_' + id + '" src="' + options.url +
                '" width="100%" seamless height="100%" name="iframeIn" frameborder="no" border="0" marginwidth="0" ' +
                'style="padding-bottom: 60px" marginheight="0" scrolling="auto" allowtransparency="yes">' +
                '</iframe></div>';
        }
        //加入TABS
        $(".nav-tabs").append(title);
        $(".tab-content").append(content);
    } else {
        if (active_flag) {
            $("#iframe_" + id).attr('src', $("#iframe_" + id).attr('src'));
        }
    }
    //激活TAB
    $("#tab_" + id).addClass('active');
    $("#" + id).addClass("active");
    var defaultHeight = "630px";
    setPageIframeHeight(defaultHeight, id);
}

//关闭tab页
var closeTab = function (id) {
    //如果关闭的是当前激活的TAB，激活他的前一个TAB
    if ($("li.active").attr('id') == "tab_" + id) {
        $("#tab_" + id).prev().addClass('active');
        $("#" + id).prev().addClass('active');
        var defaultHeight = "630px";
        setPageIframeHeight(defaultHeight, id);
    }
    //关闭TAB
    $("#tab_" + id).remove();
    $("#" + id).remove();
};

//页面tab的关闭事件监听
$(function () {
    var mainHeight = $(document.body).height() - 300;
    $('.main-left,.main-right').height(mainHeight);
    $("[addtabs]").click(function () {
        this.addTabs({id: $(this).attr("id"), title: $(this).attr('title'), close: true});
        var defaultHeight = "630px";
        setPageIframeHeight(defaultHeight, id);
    });

    $(".nav-tabs").on("click", "[tabclose]", function (e) {
        closeTab($(this).attr("tabclose"));
    });
});

function setPageIframeHeight(defaultHeight, workZoneId) {
    var zoneFrame = window.top.document.getElementById("workZoneFrame" + workZoneId);
    var topmaindiv = window.top.document.getElementsByTagName("iframe");
    setInterval(function () {
        var docH = document.documentElement.clientHeight;
        var headH = $("header").height();

        var footerH = $("#footermaindiv").height();
        var contentH = docH - headH - footerH - 64;
        $("#maindiv").css("height", contentH);
        $(".tab-pane").css("height", contentH - 4 + "px");
        $(".tab-pane.active").css("height", contentH - 4 + "px");

        var worzoneSiteFrameDiv = $(window.frames["iframeIn"]).find("#workZonesiteFrame_");

        worzoneSiteFrameDiv.find("#" + workZoneId).css("height", defaultHeight);
    }, 200)
}

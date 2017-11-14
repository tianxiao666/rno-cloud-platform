// 设置HTML5本地存储中 SelectedMenu 的值为空
localStorage['SelectedMenuId'] = "";
localStorage['SelectedMenuFlag'] = "";
localStorage['SelectedMenuRank'] = "";

$(function () {
    //幻灯片
    $(window).load(function () {
        $(".login-slide").flexslider();
    });

     //弹出Email后缀名列表
    $(".email-text").click(function () {
        $(".email-list").toggle();
    });
    $(".email-extensions").mouseleave(function () {
        $(".email-list").hide();
    });

    var $emailList = $(".email-list li");
    //显示Email后缀名
    $emailList.click(function () {
        $(".email-name").text($(this).text());
        $(".email-list").hide();
    });

    //鼠标经过改变颜色
    $emailList.hover(function () {
        $(this).addClass("hover")
    }, function () {
        $(this).removeClass("hover")
    });
});

// 设置HTML5本地存储中 SelectedMenu 的值为空
localStorage['SelectedMenuId'] = "";
localStorage['SelectedMenuFlag'] = "";
localStorage['SelectedMenuRank'] = "";

$(function () {
    //幻灯片
    $(window).load(function () {
        $(".login_slide").flexslider();
    });

     //弹出Email后缀名列表
    $(".email_text").click(function () {
        $(".email_list").toggle();
    });
    $(".email_extensions").mouseleave(function () {
        $(".email_list").hide();
    });

    var $email_list = $(".email_list li");
    //显示Email后缀名
    $email_list.click(function () {
        $(".email_name").text($(this).text());
        $(".email_list").hide();
    });

    //鼠标经过改变颜色
    $email_list.hover(function () {
        $(this).addClass("hover")
    }, function () {
        $(this).removeClass("hover")
    });
});

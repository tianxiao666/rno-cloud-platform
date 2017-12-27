// 设置 HTML5 本地存储中菜单项选择的值为空，这样从登录页进系统就会显示默认首页
localStorage['SelectedMenuId'] = "";
localStorage['SelectedMenuFlag'] = "";
localStorage['SelectedMenuRank'] = "";

$(function () {
    // 初始化系统标题、Logo与软件版本
    $.ajax({
        url: "api/app-info",
        dataType: "json",
        async: false,
        success: function (data) {
            document.title = data.name;
            $(".logo").html("<img src='images/" + data.logo + "'>");
            $("#software-version").html(data.name + " " + data.version);
        }
    });

    // 幻灯片
    $(window).load(function () {
        $(".flexslider").flexslider();
    });
});

// 设置 HTML5 本地存储中菜单项选择的值为空，这样从登录页进系统就会显示默认首页
localStorage['SelectedMenuId'] = "";
localStorage['SelectedMenuFlag'] = "";
localStorage['SelectedMenuRank'] = "";

$(function () {
    // 幻灯片
    $(window).load(function () {
        $(".flexslider").flexslider();
    });
});

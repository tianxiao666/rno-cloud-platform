$(document).ready(function () {
    //初始化门户主体高度
    windowsResize();

    // 初始化系统标题、Logo与软件版本
    $.ajax({
        url: "api/app-info",
        dataType: "json",
        async: false,
        success: function (data) {
            document.title = data.name;
            $("#system-name").html(data.name);
        }
    });

    //初始化菜单
    $.ajax({
        url: "/api/query-menus",
        dataType: "json",
        async: false,
        success: function (data) {
            renderMenu(data.menus);
        }
    });

    $("a[id^='item']").click(function () {
        bs.addTabs({
            id: $(this).attr('id'),
            title: $(this).text().trim(),
            close: true,
            url: $(this).siblings("input").val()
        });
    });
});

//浏览器大小变化时修改门户主体高度
$(window).resize(function () {
    if (window.timerLayout)
        clearTimeout(window.timerLayout);
    window.timerLayout = setTimeout(windowsResize, 100);
});

//获取门户主体高度
function windowsResize() {
    var docH = document.documentElement.clientHeight;
    $("#maindiv").css("height", docH - 100 - 42);
}

// 渲染菜单，暂时只支持两级
function renderMenu(menu) {

    console.log(menu);

    var count = 1;
    var menuHtml = [];
    $.each(menu, function (index) {
        var firstLevelMenu = menu[index];
        if (count === 1) {
            menuHtml.push("<li class='active'>");
        } else {
            menuHtml.push("<li>");
        }
        menuHtml.push("<a href='#' class='dropdown-collapse'>");
        menuHtml.push("<i class='fa fa-folder fa-fw'></i>");
        menuHtml.push("<span class='side-menu-title'>" + firstLevelMenu.name +"</span>");
        menuHtml.push("<span class='fa arrow'></span></a>");

        if (count === 1) {
            menuHtml.push("<ul class='nav nav-second-level collapse in'>");
        } else {
            menuHtml.push("<ul class='nav nav-second-level'>");
        }

        var secondMenu = firstLevelMenu.children;
        $.each(secondMenu, function (secondIndex) {
            var secondLevelMenu = secondMenu[secondIndex];
            menuHtml.push("<li>");
            menuHtml.push("<a id='item"  + (count ++) + "'>");
            menuHtml.push("<i class='fa fa-arrow-circle-o-right fa-fw'></i>");
            menuHtml.push("<span>" + secondLevelMenu.name + "</span></a>");
            menuHtml.push("<input type='hidden' value='" + secondLevelMenu.url + "'></li>");
        });
        menuHtml.push("</ul></li>");
    });
    $("#sidebar").html(menuHtml.join(""));
}

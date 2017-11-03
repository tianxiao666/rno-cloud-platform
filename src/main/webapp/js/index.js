//保存被选择的菜单
var firstLevelMenuSelected = "";
var secondLevelMenuSelected = "";
var thirdLevelMenuSelected = "";

$(function () {
    $("#provinceMenu").change(function () {
        var provinceId = parseInt($(this).find("option:checked").val());
        $.getJSON("data/area.json", function (data) {
            renderArea(data, provinceId, "cityMenu")
        })
    });

    //初始化区域
    $.ajax({
        url: "data/area.json",
        dataType: "json",
        async: false,
        success: function (data) {
            renderArea(data, 0, "provinceMenu");
            $("#provinceMenu").change();
        }
    });

    //初始化内容显示区域高度
    var clientHeight = document.documentElement.clientHeight;
    $("#mainDiv").css("height", clientHeight - 95 - 40);

    onLoadOpenHomeTab();

    var $navMenu = $("#navMenu");
    //鼠标悬停在一级菜单时，显示二级菜单，当鼠标离开时，收起二级菜单
    $navMenu.find(">li").hover(function () {
        $(this).children("ul").css("display", "block");
        if (firstLevelMenuSelected !== $(this).attr("id")) {
            $(this).addClass("firstLevelMenuOn");
        }
    }, function () {
        $(this).children("ul").css("display", "none");
        if (firstLevelMenuSelected !== $(this).attr("id")) {
            $(this).removeClass("firstLevelMenuOn");
        }
    });

    //鼠标悬停在二级菜单时，显示三级菜单，当鼠标离开时，收起三级菜单
    $navMenu.find(">li>ul>li").hover(function () {
        $(this).children("ul").css({
            "display": "block",
            "float": "left",
            "margin-top": "-26px",
            "margin-left": "125px",
            "width": "125px",
            "position": "absolute"
        });
        if (secondLevelMenuSelected !== $(this).attr("id")) {
            $(this).addClass("secondLevelMenuOn");
        }
    }, function () {
        $(this).children("ul").css("display", "none");
        if (secondLevelMenuSelected !== $(this).attr("id")) {
            $(this).removeClass("secondLevelMenuOn");
        }
    });

    //鼠标悬停在三级菜单时改变菜单的样式，提高用户体验
    $navMenu.find(">li>ul>li>ul>li").hover(function () {
        if ($(this).attr("id") !== thirdLevelMenuSelected) {
            $(this).addClass("thirdLevelMenuOn");
        }
    }, function () {
        if ($(this).attr("id") !== thirdLevelMenuSelected) {
            $(this).removeClass("thirdLevelMenuOn");
        }
    });

    var i = 0;
    //鼠标点击二级菜单，窗口加载相应的模块
    $navMenu.find(">li>ul>li").click(function () {
        var cl = "secondLevelMenu" + " " + "secondLevelMenuOn";
        var cl1 = $(this).attr("class");
        var cl2 = "secondLevelMenu" + " " + "secondLevelMenuSelected";

        if (cl1 === cl || cl1 === cl2 || i === 0) {
            i++;

            var href = $(this).children("input").val();

            $("#mainIFrame").attr("src", href);

            //删除原来选中的菜单的选中样式
            if (localStorage['selectedMenuRank'] == 2) {
                $("li[class$='thirdLevelMenuSelected']").removeClass("thirdLevelMenuSelected");
            }
            $("li[class$='secondLevelMenuSelected']").removeClass("secondLevelMenuSelected");
            $("li[class$='firstLevelMenuSelected']").removeClass("firstLevelMenuSelected");

            //在选中的菜单添加选中样式
            $(this).removeClass("secondLevelMenuOn");
            $(this).addClass("secondLevelMenuSelected");

            $(this).parent().parent().removeClass("firstLevelMenuOn");
            $(this).parent().parent().addClass("firstLevelMenuSelected");

            //保存最后被选中的菜单
            secondLevelMenuSelected = $(this).attr("id");

            firstLevelMenuSelected = $(this).parent().parent().attr("id");

            saveMenuId(2, secondLevelMenuSelected);
        }
    });

    //鼠标点三级级菜单，窗口加载相应的模块
    $navMenu.find(">li>ul>li>ul>li").click(function () {

        var href = $(this).children("input").val();

        $("#mainIFrame").attr("src", href);

        //删除原来选中的菜单的选中样式
        $("li[class$='thirdLevelMenuSelected']").removeClass("thirdLevelMenuSelected");
        $("li[class$='secondLevelMenuSelected']").removeClass("secondLevelMenuSelected");
        $("li[class$='firstLevelMenuSelected']").removeClass("firstLevelMenuSelected");

        //在选中的菜单添加选中样式

        $(this).removeClass("thirdLevelMenuOn");
        $(this).addClass("thirdLevelMenuSelected");

        $(this).parent().parent().removeClass("secondLevelMenuOn");
        $(this).parent().parent().addClass("secondLevelMenuSelected");

        $(this).parent().parent().parent().parent().removeClass("firstLevelMenuOn");
        $(this).parent().parent().parent().parent().addClass("firstLevelMenuSelected");

        //保存最后被选中的菜单
        thirdLevelMenuSelected = $(this).attr("id");

        secondLevelMenuSelected = $(this).parent().parent().attr("id");

        firstLevelMenuSelected = $(this).parent().parent().parent().parent().attr("id");

        saveMenuId(3, thirdLevelMenuSelected);
    });

    //保存设置信息事件
    $("#saveUserConfigBtn").click(function () {
        saveUserConfig();
    });

    //时间显示
    nowTime(document.getElementById('nowTime'));

    function saveMenuId(rank, menuId) {
        if (localStorage['selectedMenuRank'] === "3") {
            menuId = localStorage['SelectedMenuId'];
            localStorage['selectedMenuFlag'] = 1;
        } else {
            localStorage['selectedMenuFlag'] = 0;
        }
        $("#SelectedMenuId").val(menuId);
        localStorage['SelectedMenuId'] = menuId;
        localStorage['selectedMenuRank'] = rank;
    }
});

// 动态显示客户端时间
// ev:显示时间的元素 type:时间显示模式.若传入12则为12小时制,不传入则为24小时制
function nowTime(ev, type) {
    // 年月日时分秒
    var Y, M, D, W, H, I, S;

    // 月日时分秒为单位时前面补零
    function fillZero(v) {
        if (v < 10) {
            v = '0' + v;
        }
        return v;
    }

    (function () {
        var d = new Date();
        var Week = ['星期天', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
        Y = d.getFullYear();
        M = fillZero(d.getMonth() + 1);
        D = fillZero(d.getDate());
        W = Week[d.getDay()];
        H = fillZero(d.getHours());
        I = fillZero(d.getMinutes());
        S = fillZero(d.getSeconds());
        // 12小时制显示模式
        if (type && type === 12) {
            // 若要显示更多时间类型诸如中午凌晨可在下面添加判断
            if (H <= 12) {
                H = '上午&nbsp;' + H;
            } else if (H > 12 && H < 24) {
                H -= 12;
                H = '下午&nbsp;' + fillZero(H);
            } else if (H === 24) {
                H = '下午&nbsp;00';
            }
        }
        ev.innerHTML = Y + '年' + M + '月' + D + '日 ' + ' ' + W + '&nbsp;' + H
            + ':' + I + ':' + S;
        // 每秒更新时间
        setTimeout(arguments.callee, 1000);
    })();
}

//保存用户配置
function saveUserConfig() {
    var cityId = $("#cityMenu").val();
    $.ajax({
        url: "saveUserConfigAction",
        type: "POST",
        data: {
            'cityId': cityId
        },
        dataType: 'json',
        success: function () {
        }
    });
}

// 初始打开系统或刷新页面后显示的功能
function onLoadOpenHomeTab() {
    // 从HTML5本地存储中取值，用于在刷新页面后仍能打开之前的功能
    var selectedMenuId = localStorage['SelectedMenuId'];

    var $selectedMenuId;

    if (selectedMenuId === "") {
        $selectedMenuId = $("#navMenu").find(">li>ul>li").eq(0);
    } else {
        $selectedMenuId = $("#" + selectedMenuId);
    }
    if (localStorage['selectedMenuFlag'] === "0") {
        $selectedMenuId.parent().parent().addClass("firstLevelMenuSelected");
        $selectedMenuId.addClass("secondLevelMenuSelected");
    } else {
        $selectedMenuId.parent().parent().parent().parent().addClass("firstLevelMenuSelected");
        $selectedMenuId.parent().parent().addClass("secondLevelMenuSelected");
        $selectedMenuId.addClass("thirdLevelMenuSelected");
    }
    var href = $selectedMenuId.children("input").val();
    $("#mainIFrame").attr("src", href);
}

// 渲染菜单
function renderMenu(data) {
    var menu = data.menu;
    var menuHtml = [];
    $.each(menu, function (index) {
        var firstLevelMenu = menu[index];
        menuHtml.push("<li id='" + firstLevelMenu.title + "' class='firstLevelMenu'><div>"
            + firstLevelMenu.title + "</div><ul>");

        var secondMenu = firstLevelMenu.sub;
        $.each(secondMenu, function (secondIndex) {
            var secondLevelMenu = secondMenu[secondIndex];
            menuHtml.push("<li id='" + secondLevelMenu.title + "' class='secondLevelMenu'>" + secondLevelMenu.title);
            if (secondLevelMenu.sub === undefined) {
                menuHtml.push("<input type='hidden' value='" + secondLevelMenu.url + "'></li>");
            } else {
                menuHtml.push("<div style='float:right;'><span class='arrow'></span></div><ul>");

                var thirdMenu = secondLevelMenu.sub;
                $.each(thirdMenu, function (thirdIndex) {
                    var thirdLevelMenu = thirdMenu[thirdIndex];
                    menuHtml.push("<li id='" + thirdLevelMenu.title + "' class='thirdLevelMenu'>" + thirdLevelMenu.title);
                    menuHtml.push("<input type='hidden' value='" + thirdLevelMenu.url + "'></li>");
                });
                menuHtml.push("</ul>");
            }

        });
        menuHtml.push("</ul>");
    });
    $("#navMenu").html(menuHtml.join(""));
}

// 渲染区域
function renderArea(data, parentId, areaMenu) {
    var arr = data.filter(function (v) {
        return v.parentId === parentId;
    });
    if (arr.length > 0) {
        var areaHtml = [];
        $.each(arr, function (index) {
            var area = arr[index];
            areaHtml.push("<option value='" + area.id + "'>" + area.name + "</option>");
        });
        $("#" + areaMenu).html(areaHtml.join(""));
    } else {
        console.log("父ID为" + parentId + "时未找到任何下级区域。");
    }
}

//内部数据检查
var internalCheck = [{
    "code": "powerCheck",
    "name": "功率检查"
}, {
    "code": "freqHopCheck",
    "name": "跳频检查"
}, {
    "code": "nccperm",
    "name": "NCCPERM检查"
}, {
    "code": "meaFreqMultidefined",
    "name": "测量频点多定义"
}, {
    "code": "meaFreqMomit",
    "name": "测量频点漏定义"
}, {
    "code": "baNumCheck",
    "name": "BA表个数检查"
}, {
    "code": "sameFreqBsicCheck",
    "name": "同频同bsic检查"
}, {
    "code": "talimMaxTa",
    "name": "TALIM_MAXTA检查"
}];
//邻区检查
var ncellCheck = [{
    "code": "ncellMomit",
    "name": "本站邻区漏定义"
}, {
    "code": "unidirNcell",
    "name": "单向邻区"
}, {
    "code": "ncellNumCheck",
    "name": "邻区过多过少检查"
}, {
    "code": "sameNcellFreqCheck",
    "name": "同邻频检查"
}
    /*,{
        "code" : "ncellDataCheck",
        "name" : "邻区数据检查"
    }*/
];

//缓存数据
var powerCheckResult = new Array();
var freqHopCheckResult = new Array();
var nccpermResult = new Array();
var meaFreqMultidefinedResult = new Array();
var meaFreqMomitResult = new Array();
var baNumCheckResult = new Array();
var sameFreqBsicCheckResult = new Array();
var talimMaxTaResult = new Array();
var ncellMomitResult = new Array();
var unidirNcellResult = new Array();
var ncellNumCheckResult = new Array();
var sameNcellFreqCheckResult = new Array();
var ncellDataCheckResult = new Array();

var bsc,bscStr,date1,cityId,isCheckMaxChgr ,isCheckBaNum ,maxNum,minNum,isCheckCoBsic,distance,isCheckNcellNum,ncell_maxNum,ncell_minNum;

$(function () {

    // 执行 laydate 实例 
    var dateBeg = new Date();
    dateBeg.setFullYear(2014, 11, 5);
    laydate.render({elem: '#uploadDate', value: dateBeg});
    // 初始化区域联动
    initAreaSelectors({selectors: ["province-menu", "city-menu"]});

    //加载参数树形菜单
    loadParamCheckMenu();

    //加载BSC
    getAllBsc();

    $("#settingsDiv").draggable();
    $("#selectBscDiv").draggable();

    //查询点击事件
    $("#checkBtn").click(function(){
        //显示需要检查的tab
        checkData('show');
    });

    //查询设置按钮点击事件
    $("#settingsBtn").click(function () {
        $("#settingsDiv").show();
    });

    //数据导出点击事件
    $("#exportBtn").on('click', function() {
        if (checkData('export') === false) {
            return false;
        } else {
            $("#info").css("background", "yellow");
            showInfoInAndOut("info", "正在导出文件，请耐心等待.....");
            //获取所选择的菜单项
            var item = "";
            $('input:checkbox[name="twoChk"]:checked').each(function () {
                item += $(this).val() + ",";
            });
            item = item.substr(0, item.length - 1);
            var items = eval("'" + item + "'");
            //给form赋值
            $("#bsc").val(bscStr);
            $("#date1").val(date1);
            $("#checkType").val(checkType);
            $("#cityId").val(cityId);
            $("#items").val(items);
            $("#checkMaxChgr").val(isCheckMaxChgr);
            $("#checkBaNum").val(isCheckBaNum);
            $("#maxNums").val(maxNum);
            $("#minNums").val(minNum);
            $("#checkCoBsic").val(isCheckCoBsic);
            $("#distances").val(distance);
            $("#checkNcellNum").val(isCheckNcellNum);
            $("#ncellMaxNum").val(ncell_maxNum);
            $("#ncellMinNum").val(ncell_minNum);
            return true;
        }
    });

    //控制tab移动
    $(".btnRight").click(function () {
        var i = $("#frame").scrollLeft();
        $("#frame").scrollLeft(i + 50);
    });
    $(".btnLeft").click(function () {
        var i = $("#frame").scrollLeft();
        $("#frame").scrollLeft(i - 50);
    });

    var tdWidth = $("#contentTab tr:eq(0) td:nth-child(2)").width();
    $("#frame").css({
        'width': (tdWidth - 1151) + "px"
    });

});

function loadParamCheckMenu() {
    $("#paramCheckMenu").html("");
    var html = "";
    //内部数据检查
    html += "<li><input type='checkbox' name='oneChk'><span>内部数据检查</span><ul>";
    for (var i = 0; i < internalCheck.length; i++) {
        html += "<li><input type='checkbox' name='twoChk' value='" + internalCheck[i]['code']
            + "'><span id='" + internalCheck[i]['code'] + "'>" + internalCheck[i]['name'] + "</span></li>";
    }
    html += "</ul></li>";
    //邻区检查
    html += "<li><input type='checkbox' name='oneChk'><span>邻区检查</span><ul>";
    for (var i = 0; i < ncellCheck.length; i++) {
        html += "<li><input type='checkbox' name='twoChk' value='" + ncellCheck[i]['code']
            + "'><span id='" + ncellCheck[i]['code'] + "'>" + ncellCheck[i]['name'] + "</span></li>";
    }
    html += "</ul></li>";
    //加载到页面
    $("#paramCheckMenu").html(html);
    //加入树形菜单样式
    $("#paramCheckMenu").treeview();
    //加入关联选择事件
    $("input[name='oneChk']").click(function () {
        var checkedValue = this.checked;
        $(this).parent("li").find("input[name='twoChk']").attr("checked", checkedValue);
    });
}

function getAllBsc() {
    $("#defaultBsc").html("");
    $("#selectedBsc").html("");
    var cityId = $("#city-menu").val()
    $.ajax({
        url: '/api/gsm-param-check/check-bsc-by-cityId',
        data: {
            'cityId': cityId
        },
        type: 'get',
        dataType: 'text',
        success: function (raw) {
            if (raw === '') {
                $("#info").css("background", "yellow");
                showInfoInAndOut("info", "该城市匹配不到BSC");
            } else {
                var data = eval("(" + raw + ")");
                var html = "";
                for (var i = 0; i < data.length; i++) {
                    html += "<option value='" + data[i]['id'] + "'>" + data[i]['bsc'] + "</option>";
                }
                $("#defaultBsc").html(html);
            }
        }, error: function (err) {
            $("#info").css("background", "red");
            showInfoInAndOut("info", "后台程序错误！");
        }
    });
}

// 确认选择的BSC
function sumBsc() {
    var bscStr = "";
    for (var i = 0; i < document.getElementById("selectedBsc").options.length; i++) {
        bscStr += document.getElementById("selectedBsc").options[i].text + ",";
    }
    bscStr = bscStr.substring(0, bscStr.length - 1);
    //console.log(bscStr);
    $("#bscStr").val("");
    $("#bscStr").val(bscStr);
    $("#selectBscDiv").hide();
}

//BSC选择器
function PutRightOneClk() {
    if (document.getElementById("defaultBsc").options.selectedIndex == -1) {
        return false;
    }
    while (document.getElementById("defaultBsc").options.selectedIndex > -1) {
        var id = document.getElementById("defaultBsc").options.selectedIndex
        var varitem = new Option(document.getElementById("defaultBsc").options[id].text, document.getElementById("defaultBsc").options[id].value);
        document.getElementById("selectedBsc").options.add(varitem);
        document.getElementById("defaultBsc").options.remove(id);
    }
}

function PutRightAllClk() {
    if (document.getElementById("defaultBsc").options.length == 0) {
        return false;
    }
    document.getElementById("selectedBsc").options.length = 0;
    for (var i = 0; i < document.getElementById("defaultBsc").options.length; i++) {
        var varitem = new Option(document.getElementById("defaultBsc").options[i].text, document.getElementById("defaultBsc").options[i].value);
        document.getElementById("selectedBsc").options.add(varitem);
    }
    document.getElementById("defaultBsc").options.length = 0;
}

function PutLeftOneClk() {
    if (document.getElementById("selectedBsc").options.selectedIndex == -1) {
        return false;
    }
    while (document.getElementById("selectedBsc").options.selectedIndex > -1) {
        var id = document.getElementById("selectedBsc").options.selectedIndex
        var varitem = new Option(document.getElementById("selectedBsc").options[id].text, document.getElementById("selectedBsc").options[id].value);
        document.getElementById("defaultBsc").options.add(varitem);
        document.getElementById("selectedBsc").options.remove(id);
    }
}

function PutLeftAllClk() {
    if (document.getElementById("selectedBsc").options.length == 0) {
        return false;
    }
    for (var i = 0; i < document.getElementById("selectedBsc").options.length; i++) {
        var varitem = new Option(document.getElementById("selectedBsc").options[i].text, document.getElementById("selectedBsc").options[i].value);
        document.getElementById("defaultBsc").options.add(varitem);
    }
    document.getElementById("selectedBsc").options.length = 0;
}

function checkData(flag) {

    //获取所选择的菜单项
    var items = [];
    var one;
    $('input:checkbox[name="twoChk"]:checked').each(function () {
        one = {
            'code': $(this).val(),
            'name': $("span#" + $(this).val()).text()
        };
        items.push(one);
    });
    //console.log(items);
    if (items.length === 0) {
        alert("请选择要查询的项目！");
        return false;
    }

    //获取bsc
    bsc = '';
    for (var i = 0; i < document.getElementById("selectedBsc").options.length; i++) {
        bsc += document.getElementById("selectedBsc").options[i].value;
        if (i < document.getElementById("selectedBsc").options.length - 1) {
            bsc += ',';
        }
    }
    bscStr = eval("'" + bsc + "'");
    if (bscStr == "") {
        alert("请选择BSC！");
        $("#paramCheckTab").html("");
        $("#paramCheckContent").html("");
        return false;
    }
    //获取日期
    date1 = $("#uploadDate").val();
    if (date1 == "") {
        alert("请选择日期！");
        $("#paramCheckTab").html("");
        $("#paramCheckContent").html("");
        return false;
    }
    //获取cityId
    cityId = $("#city-menu").val();
    //获取设置
    var numTest = /^([0-9]+)$/; //检查数字

    isCheckMaxChgr = false;
    if ($("#isCheckMaxChgr").is(':checked')) {
        isCheckMaxChgr = true;
    }
    isCheckBaNum = false;
    if ($("#isCheckBaNum").is(':checked')) {
        isCheckBaNum = true;
    }
    maxNum = $("#MAXNUM").val();
    minNum = $("#MINNUM").val();
    if (isCheckBaNum) {
        if (maxNum == null || minNum == null || !numTest.test(maxNum)
            || !numTest.test(minNum) || Number(minNum) > Number(maxNum)) {
            alert("BA表个数检查的查询设置不符合要求,请重新设置");
            $("#paramCheckTab").html("");
            $("#paramCheckContent").html("");
            return false;
        }
    }

    isCheckCoBsic = false;
    if ($("#isCheckCoBsic").is(':checked')) {
        isCheckCoBsic = true;
    }
    distance = $("#DISTANCE").val();
    if (isCheckCoBsic) {
        if (distance == null || !numTest.test(distance)) {
            alert("同频同bsic检查的查询设置不符合要求,请重新设置");
            $("#paramCheckTab").html("");
            $("#paramCheckContent").html("");
            return false;
        }
    }

    isCheckNcellNum = false;
    if ($("#isCheckNcellNum").is(':checked')) {
        isCheckNcellNum = true;
    }
    ncell_maxNum = $("#NCELL_MAXNUM").val();
    ncell_minNum = $("#NCELL_MINNUM").val();
    if (isCheckNcellNum) {
        if (ncell_minNum == null || ncell_minNum == null || !numTest.test(ncell_maxNum)
            || !numTest.test(ncell_maxNum)) {
            alert("邻区过多过少检查的查询设置不符合要求,请重新设置");
            $("#paramCheckTab").html("");
            $("#paramCheckContent").html("");
            return false;
        }
    }

    if (flag === 'show') {
        $("#paramCheckTab").html("");
        $("#paramCheckContent").html("");
        //组建tab的html脚本
        var tabTitle = "<li id='" + items[0]['code'] + "' class='selected'>" + items[0]['name'] + "</li>";
        var tabContent = "<div id='div_tab_0' style='display:block; overflow:auto;height:600px;'>"
            + "<table id='" + items[0]['code'] + "Table' style='width:99%;'></table></div>";
        for (var i = 1; i < items.length; i++) {
            tabTitle += "<li id='" + items[i]['code'] + "' >" + items[i]['name'] + "</li>";
            tabContent += "<div id='div_tab_" + i + "' style='display:none; overflow:auto;height:600px;'>"
                + "<table id='" + items[i]['code'] + "Table' style='width:99%;'></table></div>";
        }
        $("#paramCheckTab").html(tabTitle);
        $("#paramCheckContent").html(tabContent);
        //加入点击切换tab事件
        tab("div_tab", "li", "onclick");
        //为所有li项加入加载数据事件
        $("#paramCheckTab li").click(function () {
            loadResultForTab($(this).attr("id"), 'show');
        });
        //为第一个tab的table加载数据
        loadResultForTab(items[0]['code'], 'show');
    } else {
        return true;
    }

}

function loadResultForTab(checkType) {
    if ($("#" + checkType + "Table:has(tr)").length > 0) {
        return;
    }
    $(".loading").show();
    $.ajax({
        url: '/api/gsm-param-check/check-param',
        data: {
            'bscStr': bscStr,
            'date1': date1,
            'checkType': checkType,
            'cityId': cityId,
            'items': 'showData',
            'checkMaxChgr': isCheckMaxChgr,
            'checkBaNum': isCheckBaNum,
            'maxNum': maxNum,
            'minNum': minNum,
            'checkCoBsic': isCheckCoBsic,
            'distance': distance,
            'checkNcellNum': isCheckNcellNum,
            'ncellMaxNum': ncell_maxNum,
            'ncellMinNum': ncell_minNum
        },
        type: 'get',
        dataType: 'text',
        success: function (raw) {
            $(".loading").css("display", "none");
            var data = eval("(" + raw + ")");
            if (data.length > 0) {
                var data = eval("(" + raw + ")");
                var thHtml = "";
                //标题
                thHtml = buildTh(checkType);
                $("#" + checkType + "Table").append(thHtml);
                //缓存数据
                saveCache(checkType, data);
                //tr，初始加载200条信息
                appendTr(checkType, 0, 200);
            } else {
                $("#info").css("background", "red");
                showInfoInAndOut('info', '没有符合条件的数据');
                $("#" + checkType + "Table").html("");
            }
        }, error: function (err) {
            $(".loading").css("display", "none");
            $("#info").css("background", "red");
            showInfoInAndOut("info", "后台程序错误！");
        }
    });

}

//提示信息
function showInfoInAndOut(div, info) {
    var divSet = $("#" + div);
    divSet.html(info);
    divSet.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

function buildTh(checkType) {
    var th = "";
    //功率检查
    if (checkType == 'powerCheck') {
        th += "<tr><th></th><th>BSC</th><th>CELL</th><th>BSPWRB</th><th>BSPWRT</th><th>指令</th></tr>";
    }
    //跳频检查
    else if (checkType == 'freqHopCheck') {
        th += "<tr><th></th><th>BSC</th><th>CELL</th><th>CHGR</th><th>HOP</th><th>频点数</th><th>频点列表</th><th>指令</th></tr>";
    }
    //NCCPERM检查
    else if (checkType == 'nccperm') {
        th += "<tr><th></th><th>BSC</th><th>CELL</th><th>NCCPERM</th><th>缺失的NCC</th><th>指令</th></tr>";
    }
    //测量频点多定义
    else if (checkType == 'meaFreqMultidefined') {
        th += "<tr><th></th><th>BSC</th><th>CELL</th><th>LISTTYPE</th><th>多定义的频点</th><th>指令</th></tr>";
    }
    //测量频点漏定义
    else if (checkType == 'meaFreqMomit') {
        th += "<tr><th></th><th>BSC</th><th>CELL</th><th>LISTTYPE</th><th>漏定义的频点</th><th>指令</th></tr>";
    }
    //BA表个数检查
    else if (checkType == 'baNumCheck') {
        th += "<tr><th></th><th>BSC</th><th>CELL</th><th>LISTTYPE</th><th>NUM</th></tr>";
    }
    //同频同bsic检查
    else if (checkType == 'sameFreqBsicCheck') {
        th += "<tr><th></th><th>BSIC</th><th>BCCHNO</th><th>BSC1</th><th>CELL1</th><th>CELL1_NAME</th>"
            + "<th>BSC2</th><th>CELL2</th><th>CELL2_NAME</th><th>DISTANCE(M)</th><th>指令</th></tr>";
    }
    //TALIM_MAXTA检查
    else if (checkType == 'talimMaxTa') {
        th += "<tr><th></th><th>CREATE_TIME</th><th>BSC</th><th>CELL</th><th>TALIM</th><th>MAXTA</th></tr>";
    }

    //本站邻区漏定义
    else if (checkType == 'ncellMomit') {
        th += "<tr><th></th><th>BSC</th><th>CELL</th><th>CELLR</th><th>CELLR_BSC</th><th>CS</th><th>指令</th></tr>";
    }
    //单向邻区检查
    else if (checkType == 'unidirNcell') {
        th += "<tr><th></th><th>BSC</th><th>CELL</th><th>CELLR</th><th>DIR</th><th>CELLR_BSC</th><th>同BSC</th><th>指令</th></tr>";
    }
    //邻区过多过少检查
    else if (checkType == 'ncellNumCheck') {
        th += "<tr><th></th><th>BSC</th><th>CELL</th><th>邻区数量</th><th>邻区信息</th></tr>";
    }
    //同邻频检查
    else if (checkType == 'sameNcellFreqCheck') {
        th += "<tr><th></th><th>BSC</th><th>CELL</th><th>CELLR</th><th>cell_bcch</th><th>cellr_bcch</th><th>cell_问题频点</th><th>cellr_问题频点</th>"
            + "<th>DIR</th><th>CS</th><th>DISTANCE</th><th>备注</th></tr>";
    }
    //邻区数据检查
    else if (checkType == 'ncellDataCheck') {
        th += "<tr><th></th><th>NAME</th><th>BSC</th><th>CELL</th><th>VALUE</th><th>BSCR</th><th>CELLR</th><th>VALUER</th></tr>";
    }

    return th;
}

function saveCache(checkType, data) {
    //功率检查
    if (checkType == 'powerCheck') {
        powerCheckResult = data;
    }
    //跳频检查
    else if (checkType == 'freqHopCheck') {
        freqHopCheckResult = data;
    }
    //NCCPERM检查
    else if (checkType == 'nccperm') {
        nccpermResult = data;
    }
    //测量频点多定义
    else if (checkType == 'meaFreqMultidefined') {
        meaFreqMultidefinedResult = data;
    }
    //测量频点漏定义
    else if (checkType == 'meaFreqMomit') {
        meaFreqMomitResult = data;
    }
    //BA表个数检查
    else if (checkType == 'baNumCheck') {
        baNumCheckResult = data;
    }
    //同频同bsic检查
    else if (checkType == 'sameFreqBsicCheck') {
        sameFreqBsicCheckResult = data;
    }
    //TALIM_MAXTA检查
    else if (checkType == 'talimMaxTa') {
        talimMaxTaResult = data;
    }

    //本站邻区漏定义
    else if (checkType == 'ncellMomit') {
        ncellMomitResult = data;
    }
    //单向邻区检查
    else if (checkType == 'unidirNcell') {
        unidirNcellResult = data;
    }
    //邻区过多过少检查
    else if (checkType == 'ncellNumCheck') {
        ncellNumCheckResult = data;
    }
    //同邻频检查
    else if (checkType == 'sameNcellFreqCheck') {
        sameNcellFreqCheckResult = data;
    }
    //邻区数据检查
    else if (checkType == 'ncellDataCheck') {
        ncellDataCheckResult = data;
    }
}

function appendTr(checkType, start, end) {
    var tr = "";
    //功率检查
    if (checkType == "powerCheck") {
        if (end >= powerCheckResult.length) {
            for (var i = start; i < powerCheckResult.length; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(powerCheckResult[i]['ENGNAME'], '') + "</td><td>"
                    + getValidValue(powerCheckResult[i]['CELL'], '') + "</td><td>"
                    + getValidValue(powerCheckResult[i]['BSPWRB'], '') + "</td><td>"
                    + getValidValue(powerCheckResult[i]['BSPWRT'], '') + "</td><td>"
                    + getValidValue(powerCheckResult[i]['COMMAND'], '') + "</td></tr>";
            }
        } else {
            for (var i = start; i < end; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(powerCheckResult[i]['ENGNAME'], '') + "</td><td>"
                    + getValidValue(powerCheckResult[i]['CELL'], '') + "</td><td>"
                    + getValidValue(powerCheckResult[i]['BSPWRB'], '') + "</td><td>"
                    + getValidValue(powerCheckResult[i]['BSPWRT'], '') + "</td><td>"
                    + getValidValue(powerCheckResult[i]['COMMAND'], '') + "</td></tr>";
            }
            tr += "<tr id='" + checkType + "LoadMore'>" +
                "<td colspan='3' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + powerCheckResult.length + ")'>显示全部</td>" +
                "<td colspan='3' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + (end + 200) + ")'>加载更多</td></tr>";
        }
    }
    //跳频检查
    else if (checkType == 'freqHopCheck') {
        if (end >= freqHopCheckResult.length) {
            for (var i = start; i < freqHopCheckResult.length; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(freqHopCheckResult[i]['BSC'], '') + "</td><td>"
                    + getValidValue(freqHopCheckResult[i]['CELL'], '') + "</td><td>"
                    + getValidValue(freqHopCheckResult[i]['CH_GROUP'], '') + "</td><td>"
                    + getValidValue(freqHopCheckResult[i]['HOP'], '') + "</td><td>"
                    + getValidValue(freqHopCheckResult[i]['DCHNO'], '') + "</td><td>"
                    + getValidValue(freqHopCheckResult[i]['DCH'], '') + "</td><td>"
                    + getValidValue(freqHopCheckResult[i]['COMMAND'], '') + "</td></tr>";
            }
        } else {
            for (var i = start; i < end; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(freqHopCheckResult[i]['BSC'], '') + "</td><td>"
                    + getValidValue(freqHopCheckResult[i]['CELL'], '') + "</td><td>"
                    + getValidValue(freqHopCheckResult[i]['CH_GROUP'], '') + "</td><td>"
                    + getValidValue(freqHopCheckResult[i]['HOP'], '') + "</td><td>"
                    + getValidValue(freqHopCheckResult[i]['DCHNO'], '') + "</td><td>"
                    + getValidValue(freqHopCheckResult[i]['DCH'], '') + "</td><td>"
                    + getValidValue(freqHopCheckResult[i]['COMMAND'], '') + "</td></tr>";
            }
            tr += "<tr id='" + checkType + "LoadMore'>" +
                "<td colspan='4' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + freqHopCheckResult.length + ")'>显示全部</td>" +
                "<td colspan='4' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + (end + 200) + ")'>加载更多</td></tr>";
        }
    }
    //NCCPERM检查
    else if (checkType == 'nccperm') {
        if (end >= nccpermResult.length) {
            for (var i = start; i < nccpermResult.length; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(nccpermResult[i]['BSC'], '') + "</td><td>"
                    + getValidValue(nccpermResult[i]['CELL'], '') + "</td><td>"
                    + getValidValue(nccpermResult[i]['NCCPERM'], '') + "</td><td>"
                    + getValidValue(nccpermResult[i]['LEAK_NCC'], '') + "</td><td>"
                    + getValidValue(nccpermResult[i]['COMMAND'], '') + "</td></tr>";
            }
        } else {
            for (var i = start; i < end; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(nccpermResult[i]['BSC'], '') + "</td><td>"
                    + getValidValue(nccpermResult[i]['CELL'], '') + "</td><td>"
                    + getValidValue(nccpermResult[i]['NCCPERM'], '') + "</td><td>"
                    + getValidValue(nccpermResult[i]['LEAK_NCC'], '') + "</td><td>"
                    + getValidValue(nccpermResult[i]['COMMAND'], '') + "</td></tr>";
            }
            tr += "<tr id='" + checkType + "LoadMore'>" +
                "<td colspan='3' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + nccpermResult.length + ")'>显示全部</td>" +
                "<td colspan='3' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + (end + 200) + ")'>加载更多</td></tr>";
        }
    }
    //测量频点多定义
    else if (checkType == 'meaFreqMultidefined') {
        if (end >= meaFreqMultidefinedResult.length) {
            for (var i = start; i < meaFreqMultidefinedResult.length; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(meaFreqMultidefinedResult[i]['BSC'], '') + "</td><td>"
                    + getValidValue(meaFreqMultidefinedResult[i]['CELL'], '') + "</td><td>"
                    + getValidValue(meaFreqMultidefinedResult[i]['LISTTYPE'], '') + "</td><td>"
                    + getValidValue(meaFreqMultidefinedResult[i]['OVER_DEFINE'], '') + "</td><td>"
                    + getValidValue(meaFreqMultidefinedResult[i]['COMMAND'], '') + "</td></tr>";
            }
        } else {
            for (var i = start; i < end; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(meaFreqMultidefinedResult[i]['BSC'], '') + "</td><td>"
                    + getValidValue(meaFreqMultidefinedResult[i]['CELL'], '') + "</td><td>"
                    + getValidValue(meaFreqMultidefinedResult[i]['LISTTYPE'], '') + "</td><td>"
                    + getValidValue(meaFreqMultidefinedResult[i]['OVER_DEFINE'], '') + "</td><td>"
                    + getValidValue(meaFreqMultidefinedResult[i]['COMMAND'], '') + "</td></tr>";
            }
            tr += "<tr id='" + checkType + "LoadMore'>" +
                "<td colspan='3' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + meaFreqMultidefinedResult.length + ")'>显示全部</td>" +
                "<td colspan='3' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + (end + 200) + ")'>加载更多</td></tr>";
        }
    }
    //测量频点漏定义
    else if (checkType == 'meaFreqMomit') {
        if (end >= meaFreqMomitResult.length) {
            for (var i = start; i < meaFreqMomitResult.length; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(meaFreqMomitResult[i]['BSC'], '') + "</td><td>"
                    + getValidValue(meaFreqMomitResult[i]['CELL'], '') + "</td><td>"
                    + getValidValue(meaFreqMomitResult[i]['LISTTYPE'], '') + "</td><td>"
                    + getValidValue(meaFreqMomitResult[i]['LEAK_DEFINE'], '') + "</td><td>"
                    + getValidValue(meaFreqMomitResult[i]['COMMAND'], '') + "</td></tr>";
            }
        } else {
            for (var i = start; i < end; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(meaFreqMomitResult[i]['BSC'], '') + "</td><td>"
                    + getValidValue(meaFreqMomitResult[i]['CELL'], '') + "</td><td>"
                    + getValidValue(meaFreqMomitResult[i]['LISTTYPE'], '') + "</td><td>"
                    + getValidValue(meaFreqMomitResult[i]['LEAK_DEFINE'], '') + "</td><td>"
                    + getValidValue(meaFreqMomitResult[i]['COMMAND'], '') + "</td></tr>";
            }
            tr += "<tr id='" + checkType + "LoadMore'>" +
                "<td colspan='3' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + meaFreqMomitResult.length + ")'>显示全部</td>" +
                "<td colspan='3' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + (end + 200) + ")'>加载更多</td></tr>";
        }
    }
    //BA表个数检查
    else if (checkType == 'baNumCheck') {
        if (end >= baNumCheckResult.length) {
            for (var i = start; i < baNumCheckResult.length; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(baNumCheckResult[i]['BSC'], '') + "</td><td>"
                    + getValidValue(baNumCheckResult[i]['CELL'], '') + "</td><td>"
                    + getValidValue(baNumCheckResult[i]['LISTTYPE'], '') + "</td><td>"
                    + getValidValue(baNumCheckResult[i]['NUM'], '') + "</td></tr>";
            }
        } else {
            for (var i = start; i < end; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(baNumCheckResult[i]['BSC'], '') + "</td><td>"
                    + getValidValue(baNumCheckResult[i]['CELL'], '') + "</td><td>"
                    + getValidValue(baNumCheckResult[i]['LISTTYPE'], '') + "</td><td>"
                    + getValidValue(baNumCheckResult[i]['NUM'], '') + "</td></tr>";
            }
            tr += "<tr id='" + checkType + "LoadMore'>" +
                "<td colspan='3' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + baNumCheckResult.length + ")'>显示全部</td>" +
                "<td colspan='2' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + (end + 200) + ")'>加载更多</td></tr>";
        }
    }
    //同频同bsic检查
    else if (checkType == 'sameFreqBsicCheck') {
        if (end >= sameFreqBsicCheckResult.length) {
            for (var i = start; i < sameFreqBsicCheckResult.length; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(sameFreqBsicCheckResult[i]['BSIC'], '') + "</td><td>"
                    + getValidValue(sameFreqBsicCheckResult[i]['BCCH'], '') + "</td><td>"
                    + getValidValue(sameFreqBsicCheckResult[i]['BSC1'], '') + "</td><td>"
                    + getValidValue(sameFreqBsicCheckResult[i]['CELL1'], '') + "</td><td>"
                    + getValidValue(sameFreqBsicCheckResult[i]['CELL1_NAME'], '') + "</td><td>"
                    + getValidValue(sameFreqBsicCheckResult[i]['BSC2'], '') + "</td><td>"
                    + getValidValue(sameFreqBsicCheckResult[i]['CELL2'], '') + "</td><td>"
                    + getValidValue(sameFreqBsicCheckResult[i]['CELL2_NAME'], '') + "</td><td>"
                    + getValidValue(sameFreqBsicCheckResult[i]['DISTANCE'], '') + "</td><td>"
                    + getValidValue(sameFreqBsicCheckResult[i]['MML'], '') + "</td></tr>";
            }
        } else {
            for (var i = start; i < end; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(sameFreqBsicCheckResult[i]['BSIC'], '') + "</td><td>"
                    + getValidValue(sameFreqBsicCheckResult[i]['BCCH'], '') + "</td><td>"
                    + getValidValue(sameFreqBsicCheckResult[i]['BSC1'], '') + "</td><td>"
                    + getValidValue(sameFreqBsicCheckResult[i]['CELL1'], '') + "</td><td>"
                    + getValidValue(sameFreqBsicCheckResult[i]['CELL1_NAME'], '') + "</td><td>"
                    + getValidValue(sameFreqBsicCheckResult[i]['BSC2'], '') + "</td><td>"
                    + getValidValue(sameFreqBsicCheckResult[i]['CELL2'], '') + "</td><td>"
                    + getValidValue(sameFreqBsicCheckResult[i]['CELL2_NAME'], '') + "</td><td>"
                    + getValidValue(sameFreqBsicCheckResult[i]['DISTANCE'], '') + "</td><td>"
                    + getValidValue(sameFreqBsicCheckResult[i]['MML'], '') + "</td></tr>";
            }
            tr += "<tr id='" + checkType + "LoadMore'>" +
                "<td colspan='6' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + sameFreqBsicCheckResult.length + ")'>显示全部</td>" +
                "<td colspan='5' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + (end + 200) + ")'>加载更多</td></tr>";
        }
    }
    //TALIM_MAXTA检查
    else if (checkType == 'talimMaxTa') {
        if (end >= talimMaxTaResult.length) {
            for (var i = start; i < baNumCheckResult.length; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(talimMaxTaResult[i]['CREATE_TIME'], '') + "</td><td>"
                    + getValidValue(talimMaxTaResult[i]['BSC'], '') + "</td><td>"
                    + getValidValue(talimMaxTaResult[i]['CELL'], '') + "</td><td>"
                    + getValidValue(talimMaxTaResult[i]['TALIM'], '') + "</td><td>"
                    + getValidValue(talimMaxTaResult[i]['MAXTA'], '') + "</td></tr>";
            }
        } else {
            for (var i = start; i < end; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(talimMaxTaResult[i]['CREATE_TIME'], '') + "</td><td>"
                    + getValidValue(talimMaxTaResult[i]['BSC'], '') + "</td><td>"
                    + getValidValue(talimMaxTaResult[i]['CELL'], '') + "</td><td>"
                    + getValidValue(talimMaxTaResult[i]['TALIM'], '') + "</td><td>"
                    + getValidValue(talimMaxTaResult[i]['MAXTA'], '') + "</td></tr>";
            }
            tr += "<tr id='" + checkType + "LoadMore'>" +
                "<td colspan='3' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + talimMaxTaResult.length + ")'>显示全部</td>" +
                "<td colspan='3' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + (end + 200) + ")'>加载更多</td></tr>";
        }
    }

    //本站邻区漏定义
    else if (checkType == 'ncellMomit') {
        if (end >= ncellMomitResult.length) {
            for (var i = start; i < ncellMomitResult.length; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(ncellMomitResult[i]['BSC'], '') + "</td><td>"
                    + getValidValue(ncellMomitResult[i]['CELL'], '') + "</td><td>"
                    + getValidValue(ncellMomitResult[i]['CELLR'], '') + "</td><td>"
                    + getValidValue(ncellMomitResult[i]['CELLR_BSC'], '') + "</td><td>"
                    + getValidValue(ncellMomitResult[i]['CS'], '') + "</td><td>"
                    + getValidValue(ncellMomitResult[i]['MML'], '') + "</td></tr>";
            }
        } else {
            for (var i = start; i < end; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(ncellMomitResult[i]['BSC'], '') + "</td><td>"
                    + getValidValue(ncellMomitResult[i]['CELL'], '') + "</td><td>"
                    + getValidValue(ncellMomitResult[i]['CELLR'], '') + "</td><td>"
                    + getValidValue(ncellMomitResult[i]['CELLR_BSC'], '') + "</td><td>"
                    + getValidValue(ncellMomitResult[i]['CS'], '') + "</td><td>"
                    + getValidValue(ncellMomitResult[i]['MML'], '') + "</td></tr>";
            }
            tr += "<tr id='" + checkType + "LoadMore'>" +
                "<td colspan='4' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + ncellMomitResult.length + ")'>显示全部</td>" +
                "<td colspan='3' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + (end + 200) + ")'>加载更多</td></tr>";
        }
    }
    //单向邻区检查
    else if (checkType == 'unidirNcell') {
        if (end >= unidirNcellResult.length) {
            for (var i = start; i < unidirNcellResult.length; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(unidirNcellResult[i]['BSC'], '') + "</td><td>"
                    + getValidValue(unidirNcellResult[i]['CELL'], '') + "</td><td>"
                    + getValidValue(unidirNcellResult[i]['CELLR'], '') + "</td><td>"
                    + getValidValue(unidirNcellResult[i]['DIR'], '') + "</td><td>"
                    + getValidValue(unidirNcellResult[i]['CELLR_BSC'], '') + "</td><td>"
                    + getValidValue(unidirNcellResult[i]['IS_SAME_BSC'], '') + "</td><td>"
                    + getValidValue(unidirNcellResult[i]['COMMAND'], '') + "</td></tr>";
            }
        } else {
            for (var i = start; i < end; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(unidirNcellResult[i]['BSC'], '') + "</td><td>"
                    + getValidValue(unidirNcellResult[i]['CELL'], '') + "</td><td>"
                    + getValidValue(unidirNcellResult[i]['CELLR'], '') + "</td><td>"
                    + getValidValue(unidirNcellResult[i]['DIR'], '') + "</td><td>"
                    + getValidValue(unidirNcellResult[i]['CELLR_BSC'], '') + "</td><td>"
                    + getValidValue(unidirNcellResult[i]['IS_SAME_BSC'], '') + "</td><td>"
                    + getValidValue(unidirNcellResult[i]['COMMAND'], '') + "</td></tr>";
            }
            tr += "<tr id='" + checkType + "LoadMore'>" +
                "<td colspan='4' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + unidirNcellResult.length + ")'>显示全部</td>" +
                "<td colspan='4' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + (end + 200) + ")'>加载更多</td></tr>";
        }
    }
    //邻区过多过少检查
    else if (checkType == 'ncellNumCheck') {
        if (end >= ncellNumCheckResult.length) {
            for (var i = start; i < ncellNumCheckResult.length; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(ncellNumCheckResult[i]['BSC'], '') + "</td><td>"
                    + getValidValue(ncellNumCheckResult[i]['CELL'], '') + "</td><td>"
                    + getValidValue(ncellNumCheckResult[i]['N_CELL_NUM'], '') + "</td><td>"
                    + getValidValue(ncellNumCheckResult[i]['N_CELLS'], '') + "</td></tr>";
            }
        } else {
            for (var i = start; i < end; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(ncellNumCheckResult[i]['BSC'], '') + "</td><td>"
                    + getValidValue(ncellNumCheckResult[i]['CELL'], '') + "</td><td>"
                    + getValidValue(ncellNumCheckResult[i]['N_CELL_NUM'], '') + "</td><td>"
                    + getValidValue(ncellNumCheckResult[i]['N_CELLS'], '') + "</td></tr>";
            }
            tr += "<tr id='" + checkType + "LoadMore'>" +
                "<td colspan='3' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + ncellNumCheckResult.length + ")'>显示全部</td>" +
                "<td colspan='2' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + (end + 200) + ")'>加载更多</td></tr>";
        }
    }
    //同邻频检查
    else if (checkType == 'sameNcellFreqCheck') {
        if (end >= sameNcellFreqCheckResult.length) {
            for (var i = start; i < sameNcellFreqCheckResult.length; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(sameNcellFreqCheckResult[i]['BSC'], '') + "</td><td>"
                    + getValidValue(sameNcellFreqCheckResult[i]['CELL'], '') + "</td><td>"
                    + getValidValue(sameNcellFreqCheckResult[i]['CELLR'], '') + "</td><td>"
                    + getValidValue(sameNcellFreqCheckResult[i]['CELL_BCCH'], '') + "</td><td>"
                    + getValidValue(sameNcellFreqCheckResult[i]['CELLR_BCCH'], '') + "</td><td>"
                    + getValidValue(sameNcellFreqCheckResult[i]['CELL_FREQ'], '') + "</td><td>"
                    + getValidValue(sameNcellFreqCheckResult[i]['CELLR_FREQ'], '') + "</td><td>"
                    + getValidValue(sameNcellFreqCheckResult[i]['DIR'], '') + "</td><td>"
                    + getValidValue(sameNcellFreqCheckResult[i]['CS'], '') + "</td><td>"
                    + getValidValue(sameNcellFreqCheckResult[i]['DISTANCE'], '') + "</td><td>"
                    + getValidValue(sameNcellFreqCheckResult[i]['COMMENT'], '') + "</td></tr>";
            }
        } else {
            for (var i = start; i < end; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(sameNcellFreqCheckResult[i]['BSC'], '') + "</td><td>"
                    + getValidValue(sameNcellFreqCheckResult[i]['CELL'], '') + "</td><td>"
                    + getValidValue(sameNcellFreqCheckResult[i]['CELLR'], '') + "</td><td>"
                    + getValidValue(sameNcellFreqCheckResult[i]['CELL_BCCH'], '') + "</td><td>"
                    + getValidValue(sameNcellFreqCheckResult[i]['CELLR_BCCH'], '') + "</td><td>"
                    + getValidValue(sameNcellFreqCheckResult[i]['CELL_FREQ'], '') + "</td><td>"
                    + getValidValue(sameNcellFreqCheckResult[i]['CELLR_FREQ'], '') + "</td><td>"
                    + getValidValue(sameNcellFreqCheckResult[i]['DIR'], '') + "</td><td>"
                    + getValidValue(sameNcellFreqCheckResult[i]['CS'], '') + "</td><td>"
                    + getValidValue(sameNcellFreqCheckResult[i]['DISTANCE'], '') + "</td><td>"
                    + getValidValue(sameNcellFreqCheckResult[i]['COMMENT'], '') + "</td></tr>";
            }
            tr += "<tr id='" + checkType + "LoadMore'>" +
                "<td colspan='6' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + sameNcellFreqCheckResult.length + ")'>显示全部</td>" +
                "<td colspan='6' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + (end + 200) + ")'>加载更多</td></tr>";
        }
    }
    //邻区数据检查
    else if (checkType == 'ncellDataCheck') {
        if (end >= ncellDataCheckResult.length) {
            for (var i = start; i < ncellDataCheckResult.length; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(ncellDataCheckResult[i]['NAME'], '') + "</td><td>"
                    + getValidValue(ncellDataCheckResult[i]['BSC'], '') + "</td><td>"
                    + getValidValue(ncellDataCheckResult[i]['CELL'], '') + "</td><td>"
                    + getValidValue(ncellDataCheckResult[i]['VALUE'], '') + "</td><td>"
                    + getValidValue(ncellDataCheckResult[i]['BSCR'], '') + "</td><td>"
                    + getValidValue(ncellDataCheckResult[i]['CELLR'], '') + "</td><td>"
                    + getValidValue(ncellDataCheckResult[i]['VALUER'], '') + "</td></tr>";
            }
        } else {
            for (var i = start; i < end; i++) {
                tr += "<tr><td>" + (i + 1) + "</td><td>" + getValidValue(ncellDataCheckResult[i]['NAME'], '') + "</td><td>"
                    + getValidValue(ncellDataCheckResult[i]['BSC'], '') + "</td><td>"
                    + getValidValue(ncellDataCheckResult[i]['CELL'], '') + "</td><td>"
                    + getValidValue(ncellDataCheckResult[i]['VALUE'], '') + "</td><td>"
                    + getValidValue(ncellDataCheckResult[i]['BSCR'], '') + "</td><td>"
                    + getValidValue(ncellDataCheckResult[i]['CELLR'], '') + "</td><td>"
                    + getValidValue(ncellDataCheckResult[i]['VALUER'], '') + "</td></tr>";
            }
            tr += "<tr id='" + checkType + "LoadMore'>" +
                "<td colspan='4' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + ncellDataCheckResult.length + ")'>显示全部</td>" +
                "<td colspan='4' style='text-align: center;cursor:pointer;background: #bdd3ef;border:2px solid #eee ' " +
                " onclick='appendTr(\"" + checkType + "\"," + end + "," + (end + 200) + ")'>加载更多</td></tr>";
        }
    }

    //移除旧的加载按钮tr
    $("#" + checkType + "LoadMore").remove();
    //加载更多数据到table
    $("#" + checkType + "Table").append(tr);
}
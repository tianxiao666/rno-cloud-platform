var CellDetailCode = [{
    'code': 'cellType',
    'name': '基站类型'
}, {
    'code': 'pci',
    'name': 'PCI'
}, {
    'code': 'longitude',
    'name': '小区经度'
}, {
    'code': 'latitude',
    'name': '小区纬度'
}, {
    'code': 'antennaHeight',
    'name': '天线挂高'
}, {
    'code': 'azimuth',
    'name': '方向角'
}, {
    'code': 'openStationRate',
    'name': '开站配比'
}, {
    'code': 'freqBandType',
    'name': '频带类型'
}, {
    'code': 'downTile',
    'name': '下倾角'
}, {
    'code': 'subFrame',
    'name': '子帧分配'
}, {
    'code': 'coverType',
    'name': '覆盖类型'
}, {
    'code': 'mdowntilt',
    'name': '机械下倾角'
}, {
    'code': 'specialSubFrame',
    'name': '特殊子帧'
}, {
    'code': 'referenceSignalPower',
    'name': '参考信号功率'
}, {
    'code': 'edowntilt',
    'name': '电子下倾角'
}, {
    'code': 'isVipStation',
    'name': '是否VIP站点'
}, {
    'code': 'tac',
    'name': 'TAC'
}, {
    'code': 'rrcCount',
    'name': 'RRC数量'
}, {
    'code': 'tal',
    'name': 'TAL'
}, {
    'code': 'pdcch',
    'name': 'PDCCH'
}, {
    'code': 'pusch',
    'name': 'PUSCH'
}, {
    'code': 'cellCoverRadii',
    'name': '小区覆盖半径'
}, {
    'code': 'antennaModel',
    'name': '天线型号'
}, {
    'code': 'startServiceDate',
    'name': '开通日期'
}, {
    'code': 'bandwidth',
    'name': '带宽'
}, {
    'code': 'isAntennaClosured',
    'name': '天线是否合路'
}, {
    'code': 'buildType',
    'name': '建设类型'
}];

var CellAdviceCode = [{
    'code': 'referenceSignalAdjustment',
    'name': '参考信号调整'
}, {
    'code': 'antennaaCoverRangeAdjustment',
    'name': '天线覆盖范围调整'
}, {
    'code': 'cellRechoosePiorityAdjustment',
    'name': '小区重选优先级调整'
}, {
    'code': 'switchParanoidAndDelayAdjustment',
    'name': '切换偏执调整、切换迟滞、偏移、时延调整'
}, {
    'code': 'switchStrategyThresholdAdjustment',
    'name': '切换策略A1/A2，A3/A4门限调整'
}, {
    'code': 'cellRechooseDelay',
    'name': '小区重选迟滞'
}, {
    'code': 'interFrequencyFrequencyOffset',
    'name': '频间频率偏移'
}, {
    'code': 'loadBalanceAdjustment',
    'name': '负载均衡算法调整'
}];

$(function () {
    $("#tabs").tabs();

    // 设置导航标题
    setNavTitle("navTitle");

    $("#lteCellAdviceDiv").draggable();
    $("#lteCellDetailDiv").draggable();
    initAreaSelectors({selectors: ["province", "city", "district"]});
    initAreaSelectors({selectors: ["provincemenu2", "citymenu2", "districtmenu2"]});

    //AJAX 查询导入记录
    $("#capicity-query-form").ajaxForm({
        url: "/api/lte-capicity-optimization/query-info",
        success: function (data) {
            showCellRecord(data, 0);
        }
    });
    $("#capicityOptimizationAdviceDialog").bind("click", function (e) {
        var target = $(e.target);
        if (target.closest($("#lteCellAdviceDiv")).length == 0) {
            adviceDialogClose();
        }
    });
    $("#capicityOptimizationDetailDialog").bind("click", function (e) {
        var target = $(e.target);
        if (target.closest($("#lteCellDetailDiv")).length == 0) {
            detailDialogClose();
        }
    });
});

/**
 * 设置导航标题
 * @param navTitleId 放置导航标题元素的ID
 */
function setNavTitle(navTitleId) {
    var navTitle = "";
    var param = location.search.split("nav=");
    if (param.length > 1) {
        navTitle = decodeURI(param[1]);
    }
    if (navTitle !== "") {
        $("#" + navTitleId).html("当前位置：" + navTitle);
    }
}

var lastFormContent = [];

function beforeSubmitForm() {
    if (lastFormContent.length != 0) {
        if ($("#province").val() == lastFormContent[0] && $("#city").val() == lastFormContent[1] && $("#district").val() == lastFormContent[2]) {
            if ($("#onlyShowHighPressCells").is(':checked')) {
                showCellRecord(dataOfHighStressList, 0);
            } else {
                showCellRecord(dataFullFromServer, 0);
            }
        } else {
            if ($("#onlyShowHighPressCells").is(':checked')) {
                $.ajax({
                    type: "POST",
                    data: $("#capicity-query-form").formSerialize(),
                    url: "/api/lte-capicity-optimization/query-info",
                    success: function (data) {
                        dataOfHighStressList = [];
                        dataFullFromServer = null;
                        selectHighStressData(data, 0);
                        lastFormContent[0] = $("#province").val();
                        lastFormContent[1] = $("#city").val();
                        lastFormContent[2] = $("#district").val();
                        showCellRecord(dataOfHighStressList, 0);
                    },
                    error: function (err) {
                        console.log("error")
                    }
                });
            } else {
                dataOfHighStressList = [];
                dataFullFromServer = null;
                lastFormContent[0] = $("#province").val();
                lastFormContent[1] = $("#city").val();
                lastFormContent[2] = $("#district").val();
                // $("#capicity-query-form").submit();
                $.ajax({
                    type: "POST",
                    data: $("#capicity-query-form").formSerialize(),
                    url: "/api/lte-capicity-optimization/query-info",
                    success: function (data) {
                        dataOfHighStressList = [];
                        dataFullFromServer = null;
                        selectHighStressData(data, 0);
                        lastFormContent[0] = $("#province").val();
                        lastFormContent[1] = $("#city").val();
                        lastFormContent[2] = $("#district").val();
                        showCellRecord(dataFullFromServer, 0);
                    },
                    error: function (err) {
                        console.log("error")
                    }
                });
            }
        }
    } else {
        if (!$("#onlyShowHighPressCells").is(':checked')) {
            dataOfHighStressList = [];
            dataFullFromServer = null;
            lastFormContent[0] = $("#province").val();
            lastFormContent[1] = $("#city").val();
            lastFormContent[2] = $("#district").val();
            $.ajax({
                type: "POST",
                data: $("#capicity-query-form").formSerialize(),
                url: "/api/lte-capicity-optimization/query-info",
                success: function (data) {
                    dataOfHighStressList = [];
                    dataFullFromServer = null;
                    selectHighStressData(data, 0);
                    lastFormContent[0] = $("#province").val();
                    lastFormContent[1] = $("#city").val();
                    lastFormContent[2] = $("#district").val();
                    showCellRecord(dataFullFromServer, 0);
                },
                error: function (err) {
                    console.log("error")
                }
            });
        } else {
            $.ajax({
                type: "POST",
                data: $("#capicity-query-form").formSerialize(),
                url: "/api/lte-capicity-optimization/query-info",
                success: function (data) {
                    dataOfHighStressList = [];
                    dataFullFromServer = null;
                    selectHighStressData(data, 0);
                    lastFormContent[0] = $("#province").val();
                    lastFormContent[1] = $("#city").val();
                    lastFormContent[2] = $("#district").val();
                    showCellRecord(dataOfHighStressList, 0);
                },
                error: function (err) {
                    console.log("error")
                }
            });
        }
    }
}

var dataFullFromServer;
var dataOfHighStressList = [];

function selectHighStressData(dataFromServer, fromCache) {
    for (var i = 0; i < dataFromServer.length; i++) {
        if (dataFromServer[i]["advice"] == 1 && fromCache == 0) {
            dataOfHighStressList.push(dataFromServer[i]);
        }
    }
    // fromCache = 0;
    dataFullFromServer = dataFromServer;
}

function showCellRecord(dataList, firstRowIndex) {
    myDataTable = $('#showResultTable')
        .css("line-height", "12px")
        .DataTable({
            "data": dataList,
            "columns": [
                {"data": "cellName"},
                {"data": "onHighStress"},
                {"data": "rrc"},
                {"data": "upRate"},
                {"data": "downRate"},
                {"data": "upFlow"},
                {"data": "downFlow"},
                {"data": "advice"}
            ],
            "columnDefs": [{
                "render": function (data, type, row) {
                    switch (row['advice']) {
                        case "0":
                            return "<a onclick='showCellDetail(" + "\"" + row["cellId"] + "\"" + ")'>查看详情</a>";
                        case "1":
                            return "<a onclick='showCellDetail(" + "\"" + row["cellId"] + "\"" + ")'>查看详情</a> " + '&nbsp;' + "<a style='color: red' onclick='showAdvice(" + "\"" + row["cellId"] + "\"" + ")'>解决方案</a>";
                    }
                },
                "targets": -1,
                "data": null
            }],
            "lengthChange": false,
            "ordering": true,
            "searching": false,
            "destroy": true,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            },
            "iDisplayStart": firstRowIndex
        });
}


function showCellDetail(cId) {
    var cell = null;
    for (var ind = 0; ind < dataFullFromServer.length; ind++) {
        if (dataFullFromServer[ind].cellId === cId) {
            cell = dataFullFromServer[ind];
        }
    }
    if (cell == null) {
        alert("出错");
        return;
    }
    // 设置值
    var lteCellDetailDiv = $("#lteCellDetailDiv");
    lteCellDetailDiv.find("span#detailCellId").html(
        cell['cellId']);
    lteCellDetailDiv.find("span#detailCellName").html(
        cell['cellName']);
    var html = '';
    var size = CellDetailCode.length;
    var onekey;
    for (var i = 0; i < size; i++) {
        if (i % 3 === 0) {
            // 换行
            if (i > 0) {
                html += "</tr>";
            }
            html += "<tr>";
        }
        onekey = CellDetailCode[i];
        var value = cell[onekey['code']] === undefined || cell[onekey['code']] === null ? ' ' : cell[onekey['code']];
        html += "<td class='menuTd'>" + onekey['name']
            + " : " + value + "</td>";
    }
    if (size % 3 > 0) {
        for (var j = 0; i < j - size % 3; j++) {
            html += "<td class='menuTd'></td>";
        }
    }
    html += "</tr>";
    $("#viewCellDetailTable").html(html);
    $("#capicityOptimizationDetailDialog").css({"display": "block", "opacity": "1"});
    $("#lteCellDetailDiv").animate({"marginTop": "-128px", "opacity": "1"}, 300);
}

function detailDialogClose() {
    $("#lteCellDetailDiv").animate({"marginTop": "-188px", "opacity": "0"}, 300, function () {
        $("#lteCellDetailDiv").css({"marginTop": "00px"});
        $("#capicityOptimizationDetailDialog").animate({"opacity": "0"}, 300, function () {
            $("#capicityOptimizationDetailDialog").hide();
        });
    });
}

function showAdvice(cId, data) {
    var cell = null;
    for (var ind = 0; ind < dataFullFromServer.length; ind++) {
        if (dataFullFromServer[ind].cellId === cId) {
            cell = dataFullFromServer[ind];
        }
    }
    if (cell == null) {
        alert("出错");
        return;
    }
    // 设置值
    var lteCellDetailDiv = $("#lteCellAdviceDiv");
    lteCellDetailDiv.find("span#adviceCellId").html(
        cell['cellId']);
    lteCellDetailDiv.find("span#adviceCellName").html(
        cell['cellName']);
    var html = '';
    var size = CellAdviceCode.length;
    var onekey;
    for (var i = 0; i < size; i++) {
        onekey = CellAdviceCode[i];
        if (cell[onekey['code']] == "1") {
            html += "<tr>" +
                "<td class='menuTd' style =" + "width: 238px;height: 31px;" + ">" + onekey['name'] + "</td>" +
                "<td class='menuTd' style =" + "width: 49px;height: 31px;" + "><button name=\"" + CellAdviceCode[i].code + "\" onclick='setAlreadyHandle(this, " + cId + ")'>操作</button></td>" +
                "</tr>";
        }
    }
    $("#viewCellAdviceTable").html(html);
    $("#capicityOptimizationAdviceDialog").css({"display": "block", "opacity": "1"});
    $("#lteCellAdviceDiv").animate({"marginTop": "-128px", "opacity": "1"}, 300);
}

function adviceDialogClose() {
    $("#lteCellAdviceDiv").animate({"marginTop": "-188px", "opacity": "0"}, 300, function () {
        $("#lteCellAdviceDiv").css({"marginTop": "00px"});
        $("#capicityOptimizationAdviceDialog").animate({"opacity": "0"}, 300, function () {
            $("#capicityOptimizationAdviceDialog").hide();
        });
    });
}

function setAlreadyHandle(dis, cId) {
    dis.innerText = "已操作";
    for (var ind = 0; ind < dataFullFromServer.length; ind++) {
        if (dataFullFromServer[ind].cellId == cId) {

            dataFullFromServer[ind].downRate = Math.floor(dataFullFromServer[ind].downRate * 0.95) < 10 ? 10 : Math.floor(dataFullFromServer[ind].downRate * 0.95);
            dataFullFromServer[ind].upRate = Math.floor(dataFullFromServer[ind].upRate * 0.95) < 10 ? 10 : Math.floor(dataFullFromServer[ind].upRate * 0.95);
            dataFullFromServer[ind].downFlow = Math.floor(dataFullFromServer[ind].downFlow * 0.95) < 1 ? 1 : Math.floor(dataFullFromServer[ind].downFlow * 0.95);
            dataFullFromServer[ind].upFlow = Math.floor(dataFullFromServer[ind].upFlow * 0.95) < 1 ? 1 : Math.floor(dataFullFromServer[ind].upFlow * 0.95);

            dataFullFromServer[ind][dis.name] = "0";
            var advices = 0;
            for (var i = 0; i < CellAdviceCode.length; i++) {
                if (dataFullFromServer[ind][CellAdviceCode[i].code] > 0) {
                    advices++;
                }
            }
            if (advices <= 0) {
                dataFullFromServer[ind].advice = "0";
                dataFullFromServer[ind].onHighStress = "0";
            }
        }
    }
    for (var inde = 0; inde < dataOfHighStressList.length; inde++) {
        if (dataOfHighStressList[inde].cellId == cId) {
            dataOfHighStressList[inde].downRate = Math.floor(dataOfHighStressList[inde].downRate * 0.95) < 10 ? 10 : Math.floor(dataOfHighStressList[inde].downRate * 0.95);
            dataOfHighStressList[inde].upRate = Math.floor(dataOfHighStressList[inde].upRate * 0.95) < 10 ? 10 : Math.floor(dataOfHighStressList[inde].upRate * 0.95);
            dataOfHighStressList[inde].downFlow = Math.round(dataOfHighStressList[inde].downFlow * 0.95) < 1 ? 1 : Math.round(dataOfHighStressList[inde].downFlow * 0.95);
            dataOfHighStressList[inde].upFlow = Math.round(dataOfHighStressList[inde].upFlow * 0.95) < 1 ? 1 : Math.round(dataOfHighStressList[inde].upFlow * 0.95);

            dataOfHighStressList[inde][dis.name] = "0";
            $(dis).attr("disabled", true);
            var advices = 0;
            for (var i = 0; i < CellAdviceCode.length; i++) {
                if (dataOfHighStressList[inde][CellAdviceCode[i].code] > 0) {
                    advices++;
                }
            }
            if (advices <= 0) {
                dataOfHighStressList[inde].advice = "0";
                dataOfHighStressList[inde].onHighStress = "0";
                dataOfHighStressList.splice(inde, 1);
            }
        }
    }
    var nowPageIndex = myDataTable.page.info().page;
    var pageLength = myDataTable.page.info().length;
    if ($("#onlyShowHighPressCells").is(':checked')) {
        showCellRecord(dataOfHighStressList, nowPageIndex*pageLength);
    } else {
        showCellRecord(dataFullFromServer, nowPageIndex*pageLength);
    }
}
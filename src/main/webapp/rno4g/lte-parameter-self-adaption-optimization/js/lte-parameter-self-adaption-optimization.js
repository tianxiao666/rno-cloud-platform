var CellOperationCode = [{
    'code': 'cellPriority',
    'name': '小区重选优先级已从7调整为5'
}, {
    'code': 'cellChangeSwitchDifficulty',
    'name': '小区切换难易度已从一级调整到三级'
}, {
    'code': 'decreaseOrIncreaseHighStressCellRechooseDelay',
    'name': '降低或提高高负荷小区的重选迟滞'
}, {
    'code': 'decreaseHighStressCellFrequencyFrequencyOffset',
    'name': '降低高负荷小区频间频率偏移'
}];

$(function () {
    $("#tabs").tabs();

    // 设置导航标题
    setNavTitle("navTitle");

    $("#lteOperationDiv").draggable();
    initAreaSelectors({selectors: ["province", "city", "district"]});
    initAreaSelectors({selectors: ["provincemenu2", "citymenu2", "districtmenu2"]});

    $("#parameterSelfAdaptionOptimizationDialog").bind("click", function (e) {
        var target = $(e.target);
        if (target.closest($("#lteOperationDiv")).length == 0) {
            operationDialogClose();
        }
    });
});

function queryCellsNeedsToBeHandle() {
    var dataString = "province=" + $("#province").val() + "&city=" + $("#city").val() + "&district=" + $("#district").val() + "&cellName=" + ($("#cellName").val()).toString().trim();
    $.ajax({
        type: "POST",
        data: dataString,
        url: "/api/lte-parameter-self-adaption-optimization//query-cell-info",
        success: showCellRecord
    });
}

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

dataFullFromServer = null;

function showCellRecord(dataList) {
    dataFullFromServer = dataList;
    $('#queryImportTab').DataTable().clear();
    $('#queryImportTab')
        .css("line-height", "12px")
        .DataTable({
            "data": dataList,
            "columns": [
                {"data": "cellName"},
                {"data": "radioAccessRate"},
                {"data": "erabSetUpSuccessRate"},
                {"data": "rrcConnectionSetUpSuccessRate"},
                {"data": "radioDropRate"},
                {"data": "radioDropCount"},
                {"data": "erabDropRate"},
                {"data": "switchRequestCount"},
                {"data": "switchSuccessCount"},
                {"data": "switchSuccessRate"},
                {"data": "operation"}
            ],
            "columnDefs": [{
                "render": function (data, type, row) {
                    switch (row['operation']) {
                        case "1":
                            return "<a onclick='showCellOperation(" + "\"" + row["cellId"] + "\"" + ")'>参数自调整</a>";
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
            }
        });
}

function showCellOperation(cId) {
    var cell = null;
    for (var ind = 0; ind < dataFullFromServer.length; ind++) {
        if (dataFullFromServer[ind].cellId === cId) {
            cell = dataFullFromServer[ind];
        }
    }
    if (cell == null) {
        alert("出错，未找到小区");
        return;
    }
    var html = '';
    var size = CellOperationCode.length;
    var onekey;
    var lengthOfContent = 0;
    html += "<tr>" +
        "<td class='menuTd'  style =" + "\"width:100%;height: 31px;display: block;position: relative;\"" + ">参数自调整正在启动:</td>" +
        "</tr>";
    var orderNumber = 0;
    for (var i = 0; i < size; i++) {
        onekey = CellOperationCode[i];
        if (cell[onekey['code']]) {
            if (cell[onekey['code']].length > 0) {
                lengthOfContent += 1;
                orderNumber += 1;
                html += "<tr>" +
                    "<td class='menuTd' id=" + 'leftSlide' + lengthOfContent + " style =" + "\"background: url(images/panel_title.png) 0 0 repeat scroll transparent;left :100%;width: 238px;height: 0px;display: none;position: relative;\"" + ">" + orderNumber + "，" + cell[onekey['code']] + "</td>" +
                    "</tr>";
            }
        }
    }
    lengthOfContent += 1;
    html += "<tr>" +
        "<td class='menuTd' id=" + 'leftSlide' + lengthOfContent + " style =" + "\"left :100%;width: 100%; height: 0px;display: none;position: relative;\"" + "><input value='完成' type='button' style='width: 100%;' onclick='operationDialogClose()'></td>" +
        "</tr>";

    $("#viewCellDetailTable").html(html);
    $("#parameterSelfAdaptionOptimizationDialog").css({"display": "block", "opacity": "1"});
    $("#lteOperationDiv").animate({"marginTop": "-128px", "opacity": "1"}, 300);

    for (var i = 1; i <= lengthOfContent; i++) {
        var shouldCloseDialog = 0;
        if ((i + 1) > lengthOfContent) {
            shouldCloseDialog += 1;
        }
        leftSlideTimeout(i, shouldCloseDialog);
    }
}

function leftSlideTimeout(i, shouldCloseDialog) {
    setTimeout(function () {
        $("#leftSlide" + (i)).css({"display": "block", "width": "100%", "height": "31px"});
        $("#leftSlide" + (i)).animate({left: "0%"}, 500, function () {
            if (shouldCloseDialog == 1) {
                // detailDialogClose();
            }
        });
    }, 500 * i);
}

function operationDialogClose() {
    $("#lteOperationDiv").animate({"marginTop": "-188px", "opacity": "0"}, 300, function () {
        $("#lteOperationDiv").css({"marginTop": "00px"});
        $("#parameterSelfAdaptionOptimizationDialog").animate({"opacity": "0"}, 300, function () {
            $("#parameterSelfAdaptionOptimizationDialog").hide();
        });
    });
}
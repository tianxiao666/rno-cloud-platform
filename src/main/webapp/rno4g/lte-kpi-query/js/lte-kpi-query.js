var optionsLength;//获取指标列的总长度
var columnArr = []; //指标列数组
var firstTime = true;
var defIndexLabelStr = "rrc_ConnEstabSucc,erab_EstabSucc,wireConn,erab_Drop_CellLevel,switchSucc";
var defIndexNameStr = "RRC连接建立成功率,E-RAB建立成功率,无线接通率,E-RAB掉线率(小区级),切换成功率";
var columnStr = "meabegTime,meaendTime,cellName";
var columnList = [];

$(function () {

    $(".draggable").draggable();
    $("#trigger").css("display", "none");

    getRno4GStsIndex();

    // getAllCellByAreaId
    //执行 laydate 实例 
    laydate.render({elem: '#beginTime', value: new Date("2015-09-01 00:00:00"), type: "datetime"});
    laydate.render({elem: '#endTime', value: new Date("2015-09-30 23:59:59"), type: "datetime"});

    // 初始化区域联动
    initAreaSelectors({selectors: ["provinceId", "cityId"]});

    $("#queryDataBtn").click(function () {
        var cells = $("#cellStr").val();
        if (cells === null || cells.length === 0 || cells === "") {
            showInfoInAndOut('info', '请输入要查看的小区');
            return false;
        }
        var indexStr = $("#indexHiddenStr").val();
        if (indexStr.length === 0) {
            $("#indexHiddenNameStr").val(defIndexNameStr);
            $("#indexHiddenStr").val(defIndexLabelStr);
        }

        // 如果未选择指标，则显示默认指标
        if (columnArr.length === 0) {
            columnStr = "meabegTime,meaendTime,cellName";
            columnStr = columnStr + ',' + defIndexLabelStr;
            var indexLabelList = defIndexLabelStr.split(",");
            var indexNameList = defIndexNameStr.split(",");
            var tableStr = "";
            for (var i = 0; i < indexLabelList.length; i++) {
                tableStr += "<th id='" + indexLabelList[i] + "'>" + indexNameList[i] + "</th> ";
            }
            $("#queryResultDT tbody").remove();
            $("#queryResultDT thead tr th:gt(2)").remove();
            $("#queryResultDT thead tr").append(tableStr);
        }
        $(".loading").show();
    });

    // AJAX 查询话务性能指标
    $("#queryForm").ajaxForm({
        url: "/api/lte-kpi-query/query-result",
        success: showQueryResult
    });

    $("#importBtn").click(function () {
        var indexStr = $("#indexHiddenStr").val();
        if (indexStr.length === 0) {
            $("#indexHiddenNameStr").val(defIndexNameStr);
            $("#indexHiddenStr").val(defIndexLabelStr);
        }

        var cells = $("#cellStr").val();
        if (cells === null || cells.length === 0 || cells === "") {
            showInfoInAndOut('info', '请先输入小区查询');
            return false;
        }
        initDownloadAttachParams();

        // 导出数据
        $("#downloadFileForm").submit();
    });
});

//显示查询结果
function showQueryResult(data) {
    $(".loading").css("display", "none");
    if (data === null || data === undefined || data == "") {
        showInfoInAndOut('info', '没有符合条件的记录');
    }else {
        firstTime = false;
    }
    // alert(data.length);
    columnList = [];
    initColumnList();

    $('#queryResultDT').css("line-height", "12px")
        .DataTable({
            "data": data,
            "columns": columnList,
            "lengthChange": false,
            "ordering": true,
            "searching": false,
            "destroy": true,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        });
}

//初始化导出数据时上传参数
function initDownloadAttachParams() {
    //清空并保存
    $("#cellNameStrForDownload").val("");
    $("#cellNameStrForDownload").val($("#cellStr").val());
    $("#indexStrForDownload").val("");
    $("#indexStrForDownload").val($("#indexHiddenStr").val());
    $("#indexNameStrForDownload").val("");
    $("#indexNameStrForDownload").val($("#indexHiddenNameStr").val());
    $("#cityIdForDownload").val("");
    $("#cityIdForDownload").val($("#cityId").val());
    $("#begTimeForDownload").val("");
    $("#begTimeForDownload").val($("#beginTime").val());
    $("#endTimeForDownload").val("");
    $("#endTimeForDownload").val($("#endTime").val());
}

/**
 * 获取话统指标项
 */
function getRno4GStsIndex() {
    $(".loading").show();
    $.ajax({
        url: '/api/lte-kpi-query/load-index',
        dataType: 'text',
        type: 'get',
        success: function (data) {
            try {
                var data1 = JSON.parse(data);
                var html = '';
                optionsLength = data1.length;
                for (var i = 0; i < optionsLength; i++) {
                    html += "<option id ='index_option_" + i + "' value='" + data1[i].indexRealName.trim() + "'>" + data1[i].indexDisplayName.trim() + "</option>";
                }
                $("#defaultIndex").html(html);
            } catch (err) {
            }
        },
        complete: function () {
            $(".loading").css("display", "none");
        }
    });
}

//初始化话统指标项选择页面
function initIndexDiv() {
    var defaultDomId = "defaultIndex";
    var selectId = "selectedIndex";
    var arrT = defIndexLabelStr.split(",");
    if (document.getElementById(selectId).options.length === 0) {
        for (var j = 0; j < arrT.length; j++) {
            for (var i = 0; i < document.getElementById(defaultDomId).options.length; i++) {
                if (document.getElementById(defaultDomId).options[i].value.trim() === arrT[j].trim()) {
                    var varitem = new Option(document.getElementById(defaultDomId).options[i].text, document.getElementById(defaultDomId).options[i].value);
                    var domId = document.getElementById(defaultDomId).options[i].id;
                    if (domId !== null) {
                        varitem.setAttribute("id", domId);
                    }
                    var index = 0;
                    if (document.getElementById(selectId).options.length > 0) {
                        index = document.getElementById(selectId).options.length;
                        for (var k = 0; k < document.getElementById(selectId).options.length; k++) {
                            if (Number(domId.split("_")[2]) < Number(document.getElementById(selectId).options[k].id.split("_")[2])) {
                                index = k;
                                break;
                            }
                        }
                    }
                    document.getElementById(selectId).options.add(varitem, index);
                    document.getElementById(defaultDomId).options.remove(i);
                    break;
                }
            }
        }
    }
}

//BSC选择器
/**
 * @return {boolean}
 */
function PutRightOneClk(defaultDomId, selectId) {
    if (document.getElementById(defaultDomId).options.selectedIndex === -1) {
        return false;
    }
    while (document.getElementById(defaultDomId).options.selectedIndex > -1) {
        var id = document.getElementById(defaultDomId).options.selectedIndex;
        var varitem = new Option(document.getElementById(defaultDomId).options[id].text, document.getElementById(defaultDomId).options[id].value);
        var domId = document.getElementById(defaultDomId).options[id].id;
        if (domId !== null) {
            varitem.setAttribute("id", domId);
        }
        var index = 0;
        if (document.getElementById(selectId).options.length > 0) {
            index = document.getElementById(selectId).options.length;
            for (var i = 0; i < document.getElementById(selectId).options.length; i++) {
                if (Number(domId.split("_")[2]) < Number(document.getElementById(selectId).options[i].id.split("_")[2])) {
                    index = i;
                    break;
                }
            }
        }
        document.getElementById(selectId).options.add(varitem, index);
        document.getElementById(defaultDomId).options.remove(id);
    }
}

/**
 * @return {boolean}
 */
function PutRightAllClk(defaultDomId, selectId) {
    if (document.getElementById(defaultDomId).options.length === 0) {
        return false;
    }
    for (var i = 0; i < document.getElementById(defaultDomId).options.length; i++) {
        var varitem = new Option(document.getElementById(defaultDomId).options[i].text, document.getElementById(defaultDomId).options[i].value);
        var domId = document.getElementById(defaultDomId).options[i].id;
        if (domId !== null) {
            varitem.setAttribute("id", domId);
        }
        var index = 0;
        if (document.getElementById(selectId).options.length > 0) {
            index = document.getElementById(selectId).options.length;
            for (var j = 0; j < document.getElementById(selectId).options.length; j++) {
                if (Number(domId.split("_")[2]) < Number(document.getElementById(selectId).options[j].id.split("_")[2])) {
                    index = j;
                    break;
                }
            }
        }
        document.getElementById(selectId).options.add(varitem, index);
    }
    document.getElementById(defaultDomId).options.length = 0;
}

/**
 * @return {boolean}
 */
function PutLeftOneClk(defaultDomId, selectId) {
    if (document.getElementById(selectId).options.selectedIndex === -1) {
        return false;
    }
    while (document.getElementById(selectId).options.selectedIndex > -1) {
        var id = document.getElementById(selectId).options.selectedIndex;
        var varitem = new Option(document.getElementById(selectId).options[id].text, document.getElementById(selectId).options[id].value);
        var domId = document.getElementById(selectId).options[id].id;
        if (domId !== null) {
            varitem.setAttribute("id", domId);
        }
        var index = 0;
        if (document.getElementById(defaultDomId).options.length > 0) {
            index = document.getElementById(defaultDomId).options.length;
            for (var i = 0; i < document.getElementById(defaultDomId).options.length; i++) {
                if (Number(domId.split("_")[2]) < Number(document.getElementById(defaultDomId).options[i].id.split("_")[2])) {
                    index = i;
                    break;
                }
            }
        }
        document.getElementById(defaultDomId).options.add(varitem, index);
        document.getElementById(selectId).options.remove(id);
    }
}

/**
 * @return {boolean}
 */
function PutLeftAllClk(defaultDomId, selectId) {
    if (document.getElementById(selectId).options.length === 0) {
        return false;
    }
    for (var i = 0; i < document.getElementById(selectId).options.length; i++) {
        var varitem = new Option(document.getElementById(selectId).options[i].text, document.getElementById(selectId).options[i].value);
        var domId = document.getElementById(selectId).options[i].id;
        if (domId !== null) {
            varitem.setAttribute("id", domId);
        }
        var index = 0;
        if (document.getElementById(defaultDomId).options.length > 0) {
            index = document.getElementById(defaultDomId).options.length;
            for (var j = 0; j < document.getElementById(defaultDomId).options.length; j++) {
                if (Number(domId.split("_")[2]) < Number(document.getElementById(defaultDomId).options[j].id.split("_")[2])) {
                    index = j;
                    break;
                }
            }
        }
        document.getElementById(defaultDomId).options.add(varitem, index);
    }
    document.getElementById(selectId).options.length = 0;
}

/**
 * 确认选择的Index
 */
function sumIndex() {
    columnStr = "meabegTime,meaendTime,cellName";
    if (!firstTime) {
        if (!confirm("改变指标项将清空表格中数据？")) {
            return;
        }
    }
    var tableStr = "";
    var indexLabelStr = "";
    var indexNameStr = "";
    columnArr.length = 0;
    for (var i = 0; i < document.getElementById("selectedIndex").options.length; i++) {
        tableStr += "<th id='" + document.getElementById("selectedIndex").options[i].value + "'>" + document.getElementById("selectedIndex").options[i].text + "</th> ";
        columnArr.push(document.getElementById("selectedIndex").options[i].value);
        indexLabelStr += document.getElementById("selectedIndex").options[i].value + ",";
        indexNameStr += document.getElementById("selectedIndex").options[i].text + ",";
    }
    indexLabelStr = indexLabelStr.substring(0, indexLabelStr.length - 1);
    indexNameStr = indexNameStr.substring(0, indexNameStr.length - 1);
    $('#queryResultDT').DataTable().clear();
    $('#queryResultDT').DataTable().destroy();
    $("#queryResultDT tbody").remove();
    $("#queryResultDT thead tr th:gt(2)").remove();
    $("#queryResultDT thead tr").append(tableStr);
    $("#queryResultDT thead tr th").css("width", "auto");
    $('#queryResultDT').css("line-height", "30px");
    $("#indexHiddenStr").val("");
    $("#indexHiddenStr").val(indexLabelStr);
    $("#indexHiddenNameStr").val("");
    $("#indexHiddenNameStr").val(indexNameStr);
    $("#selectIndexDiv").hide();

    if (indexLabelStr !== "") {
        columnStr = columnStr + ',' + indexLabelStr;
    }
}

//初始化表头
function initColumnList() {
    var colArr = columnStr.split(",");
    for (var i = 0; i < colArr.length; i++) {
        var obj = {};
        obj["data"] = colArr[i];
        columnList.push(obj);
    }
}

/**
 * 确认输入的cell
 */
function sumCell() {
    var cellNameStr = document.getElementById("cellNameStr").value;
    cellNameStr = cellNameStr.replace(/，/ig, ',');
    var cellArr = cellNameStr.split(",");
    var len = cellArr.length;
    if (len > 1000) {
        showInfoInAndOut('info', '输入的小区不能超过1000个！');
        return;
    }
    $("#cellStr").val("");
    $("#cellStr").val(cellNameStr);
    $("#cellHiddenNameStr").val("");
    $("#cellHiddenNameStr").val(cellNameStr);
    $("#selectCellDiv").hide();
    document.getElementById("cellNameStr").value = '';
}

function showInfoInAndOut(div, info) {
    var divSet = $("#" + div);
    divSet.html(info);
    divSet.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}
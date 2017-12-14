$(function () {

    $("#progressbar").progressbar({
        value: 0
    });
    $("#tabs").tabs();

    $("#cell_dateWinDiv").draggable();
    $("#cell_bscWinDiv").draggable();
    $("#cell_cellWinDiv").draggable();
    $("#cell_paramWinDiv").draggable();
    $("#channel_dateWinDiv").draggable();
    $("#channel_bscWinDiv").draggable();
    $("#channel_cellWinDiv").draggable();
    $("#channel_paramWinDiv").draggable();
    $("#ncell_dateWinDiv").draggable();
    $("#ncell_bscWinDiv").draggable();
    $("#ncell_cellWinDiv").draggable();
    $("#ncell_paramWinDiv").draggable();
    $("#ncell_ncellWinDiv").draggable();

    // 初始化区域联动
    initAreaSelectors({selectors: ["province-menu-1", "city-menu-1"]});
    initAreaSelectors({selectors: ["province-menu-2", "city-menu-2"]});
    initAreaSelectors({selectors: ["province-menu-3", "city-menu-3"]});

});

function getParamSelect(flag) {
    //清空原有参数值
    clearParamSelect(flag);
    var cityId;
    if (flag === "channel") {
        cityId = $("#city-menu-2").find("option:selected").val();
        uploadParamValue(cityId, "channel");
    } else if (flag === "ncell") {
        cityId = $("#city-menu-3").find("option:selected").val();
        uploadParamValue(cityId, "ncell");
    } else if (flag === "cell") {
        cityId = $("#city-menu-1").find("option:selected").val();
        uploadParamValue(cityId, "cell");
    }
}

function uploadParamValue(cityId, type) {
    var dataType;
    if (type === "cell") {
        dataType = "CELLDATA";
    } else if (type === "channel") {
        dataType = "CHANNELDATA";
    } else if (type === "ncell") {
        dataType = "NCELLDATA";
    }
    $.ajax({
        url: "/api/gsm-param-query/query-param-by-cityId",
        type: "get",
        data: {
            "cityId": cityId,
            "dataType": dataType,
        },
        dataType: "text",
        success: function (data) {
            var listInfo = {};
            listInfo = eval("(" + data + ")");
            var bscId, bscName, dateStr;
            for (var key in listInfo) {
                if ("bscInfo" === key) {
                    for (var bscIndex = 0; bscIndex < listInfo[key].length; bscIndex++) {
                        bscId = listInfo[key][bscIndex]["ID"];
                        bscName = listInfo[key][bscIndex]["BSC"];
                        if (type === "cell") {
                            $("#cell_targetBsc").append("<option value='" + bscId + "'>" + bscName + "</option>");
                            $("#cell_waitforselBsc").append("<option value='" + bscId + "'>" + bscName + "</option>");
                        }
                        if (type === "channel") {
                            $("#channel_targetBsc").append("<option value='" + bscId + "'>" + bscName + "</option>");
                            $("#channel_waitforselBsc").append("<option value='" + bscId + "'>" + bscName + "</option>");
                        }
                        if (type === "ncell") {
                            $("#ncell_targetBsc").append("<option value='" + bscId + "'>" + bscName + "</option>");
                            $("#ncell_waitforselBsc").append("<option value='" + bscId + "'>" + bscName + "</option>");
                        }
                    }
                }
                if ("dateInfo" === key) {
                    for (var dateIndex = 0; dateIndex < listInfo[key].length; dateIndex++) {
                        dateStr = listInfo[key][dateIndex]["MEA_DATE"];
                        if (type === "cell") {
                            $("#cell_targetDate").append("<option>" + dateStr + "</option>");
                            $("#cell_waitforselDate").append("<option>" + dateStr + "</option>");
                        }
                        if (type === "channel") {
                            $("#channel_targetDate").append("<option>" + dateStr + "</option>");
                            $("#channel_waitforselDate").append("<option>" + dateStr + "</option>");
                        }
                        if (type === "ncell") {
                            $("#ncell_targetDate").append("<option>" + dateStr + "</option>");
                            $("#ncell_waitforselDate").append("<option>" + dateStr + "</option>");
                        }
                    }
                }
            }
        }, error: function (err) {
            $(".loading").css("display", "none");
            $("#info").css("background", "red");
            showInfoInAndOut("info", "后台程序错误！");
        }
    });
}

function clearParamSelect(flag) {
    if (flag === "cell" || flag === "channel") {
        removeParamValue(flag);
    } else if (flag === "ncell") {
        removeParamValue(flag);
        $("#ncell_ncellInput").html("");
        $("#ncellForNcell").val("");
        $("#ncell_targetNcell").html("<option>ALL</option>");
    }
}

function removeParamValue(flag) {
    $("#" + flag + "_targetDate option").each(function () {
        if ("ALL" !== $(this).text()) {
            $(this).remove();
        }
    });
    $("#" + flag + "_waitforselDate option").each(function () {
        if ("ALL" !== $(this).text()) {
            $(this).remove();
        }
    });
    $("#" + flag + "_targetBsc option").each(function () {
        if ("ALL" !== $(this).text()) {
            $(this).remove();
        }
    });
    $("#" + flag + "_waitforselBsc option").each(function () {
        if ("ALL" !== $(this).text()) {
            $(this).remove();
        }
    });
    $("#" + flag + "_selectedDate").html("");
    $("#" + flag + "_select").html("");
    $("#" + flag + "_cellInput").val("");
    $("#" + flag + "_selectedParam").html("");
    $("#" + flag + "Bsc").val("");
    $("#" + flag + "ForCell").val("");
    $("#" + flag + "Date").val("");
    $("#" + flag + "Param").val("");
    $("#" + flag + "_targetCell").html("<option>ALL</option>");
}

//弹出框选择器
function PutRightOneClk(defaul, select) {
    if (document.getElementById(defaul).options.selectedIndex === -1) {
        return false;
    }
    while (document.getElementById(defaul).options.selectedIndex > -1) {
        var id = document.getElementById(defaul).options.selectedIndex
        var varitem = new Option(document.getElementById(defaul).options[id].text, document.getElementById(defaul).options[id].value);
        document.getElementById(select).options.add(varitem);
        document.getElementById(defaul).options.remove(id);
    }
}

function PutRightAllClk(defaul, select) {
    if (document.getElementById(defaul).options.length === 0) {
        return false;
    }
    for (var i = 0; i < document.getElementById(defaul).options.length; i++) {
        var varitem = new Option(document.getElementById(defaul).options[i].text, document.getElementById(defaul).options[i].value);
        document.getElementById(select).options.add(varitem);
    }
    document.getElementById(defaul).options.length = 0;
}

function PutLeftOneClk(defaul, select) {
    if (document.getElementById(select).options.selectedIndex === -1) {
        return false;
    }
    while (document.getElementById(select).options.selectedIndex > -1) {
        var id = document.getElementById(select).options.selectedIndex
        var varitem = new Option(document.getElementById(select).options[id].text, document.getElementById(select).options[id].value);
        document.getElementById(defaul).options.add(varitem);
        document.getElementById(select).options.remove(id);
    }
}

function PutLeftAllClk(defaul, select) {
    if (document.getElementById(select).options.length === 0) {
        return false;
    }
    for (var i = 0; i < document.getElementById(select).options.length; i++) {
        var varitem = new Option(document.getElementById(select).options[i].text, document.getElementById(select).options[i].value);
        document.getElementById(defaul).options.add(varitem);
    }
    document.getElementById(select).options.length = 0;
}

function ensure(select, selectOri, div) {
    var bscStr = "";
    if (document.getElementById(select).options.length === 0) {
        $("#info").css("background", "red");
        showInfoInAndOut("info", "请选择参数！");
        return false;
    }
    for (var i = 0; i < document.getElementById(select).options.length; i++) {
        bscStr += document.getElementById(select).options[i].text + ",";
    }
    bscStr = bscStr.substring(0, bscStr.length - 1);
    $("#" + selectOri + " option[style='display:none']").remove();
    $("#" + selectOri).prepend("<option selected='selected' style='display:none'>" + bscStr + "</option>");
    $("#" + div).hide();
}

function cancle(div) {
    $("#" + div).hide();
}

function cellEnsure(id, selectOri) {
    var text = $("#" + id).val();
    $("#" + selectOri + " option[style='display:none']").remove();
    $("#" + selectOri).prepend("<option selected='selected' style='display:none'>" + text + "</option>");
}

//提示信息
function showInfoInAndOut(div, info) {
    var divSet = $("#" + div);
    divSet.html(info);
    divSet.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}
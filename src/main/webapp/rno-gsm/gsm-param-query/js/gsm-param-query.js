$(function () {

    $("#progressbar").progressbar({
        value: 0
    });
    $("#tabs").tabs();

    $(".dialog2").draggable();

    // 初始化区域联动
    initAreaSelectors({selectors: ["province-menu-cell", "city-menu-cell"]});
    initAreaSelectors({selectors: ["province-menu-channel", "city-menu-channel"]});
    initAreaSelectors({selectors: ["province-menu-ncell", "city-menu-ncell"]});

    //数据导出点击事件
    $("#exportCellBtn").on('click', function () {
        var param = $("#cell_targetParam").find("option:selected").val();
        if (param === "ALL") {
            param = "";
            for (var i = 0; i < cellParam.length; i++) {
                param += cellParam[i];
                if (i < cellParam.length - 1) {
                    param += ",";
                }
            }
        }
        $("#cellParam").val(param);
        $("#cellBsc").val($("#cell_targetBsc").val());
        $("#cellDate").val($("#cell_targetDate").val());
        $("#cellForCell").val($("#cell_targetCell").val());
        $("#info").css("background", "yellow");
        showInfoInAndOut("info", "正在导出文件，请耐心等待.....");
        return true;
    });
    $("#exportChannelBtn").on('click', function () {
        var param = $("#channel_targetParam").find("option:selected").val();
        if (param === "ALL") {
            param = "";
            for (var i = 0; i < channelParam.length; i++) {
                param += channelParam[i];
                if (i < channelParam.length - 1) {
                    param += ",";
                }
            }
        }
        $("#channelParam").val(param);
        $("#channelBsc").val($("#channel_targetBsc").val());
        $("#channelDate").val($("#channel_targetDate").val());
        $("#channelForCell").val($("#channel_targetCell").val());
        $("#info").css("background", "yellow");
        showInfoInAndOut("info", "正在导出文件，请耐心等待.....");
        return true;
    });
    $("#exportNcellBtn").on('click', function () {
        var param = $("#ncell_targetParam").find("option:selected").val();
        if (param === "ALL") {
            param = "";
            for (var i = 0; i < ncellParam.length; i++) {
                param += ncellParam[i];
                if (i < ncellParam.length - 1) {
                    param += ",";
                }
            }
        }
        $("#ncellParam").val(param);
        $("#ncellBsc").val($("#ncell_targetBsc").val());
        $("#ncellDate").val($("#ncell_targetDate").val());
        $("#ncellForCell").val($("#ncell_targetCell").val());
        $("#ncellForNcell").val($("#ncell_targetNcell").val());
        $("#info").css("background", "yellow");
        showInfoInAndOut("info", "正在导出文件，请耐心等待.....");
        return true;
    });

});

function getParamSelect(flag) {
    //清空原有参数值
    clearParamSelect(flag);
    var cityId;
    if (flag === "channel") {
        cityId = $("#city-menu-channel").find("option:selected").val();
        uploadParamValue(cityId, "channel");
    } else if (flag === "ncell") {
        cityId = $("#city-menu-ncell").find("option:selected").val();
        uploadParamValue(cityId, "ncell");
    } else if (flag === "cell") {
        cityId = $("#city-menu-cell").find("option:selected").val();
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
        url: "../../api/gsm-param-query/query-param-by-cityId",
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
                //bsc参数
                if ("bscInfo" === key) {
                    for (var bscIndex = 0; bscIndex < listInfo[key].length; bscIndex++) {
                        bscId = listInfo[key][bscIndex]["ID"];
                        bscName = listInfo[key][bscIndex]["BSC"];
                        if (type === "cell") {
                            $("#cell_targetBsc").append("<option value='" + bscId + "'>" +
                                bscName + "</option>");
                            $("#cell_waitforselBsc").append("<option value='" + bscId + "'>" +
                                bscName + "</option>");
                        }
                        if (type === "channel") {
                            $("#channel_targetBsc").append("<option value='" + bscId + "'>" +
                                bscName + "</option>");
                            $("#channel_waitforselBsc").append("<option value='" + bscId + "'>" +
                                bscName + "</option>");
                        }
                        if (type === "ncell") {
                            $("#ncell_targetBsc").append("<option value='" + bscId + "'>" +
                                bscName + "</option>");
                            $("#ncell_waitforselBsc").append("<option value='" + bscId + "'>" +
                                bscName + "</option>");
                        }
                    }
                }
                //date参数
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
            //cell参数
            if (type === "cell") {
                for (var i = 0; i < cellParam.length; i++) {
                    $("#cell_targetParam").append("<option >" + cellParam[i] + "</option>");
                    $("#cell_waitforselParam").append("<option>" + cellParam[i] + "</option>");
                }
            }
            if (type === "ncell") {
                for (var j = 0; j < ncellParam.length; j++) {
                    $("#ncell_targetParam").append("<option>" + ncellParam[j] + "</option>");
                    $("#ncell_waitforselParam").append("<option>" + ncellParam[j] + "</option>");
                }
            }
            if (type === "channel") {
                for (var k = 0; k < channelParam.length; k++) {
                    $("#channel_targetParam").append("<option>" + channelParam[k] + "</option>");
                    $("#channel_waitforselParam").append("<option>" + channelParam[k] + "</option>");
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
        //删除ncell
        $("#" + flag + "_targetNcell option").each(function () {
            if ("ALL" !== $(this).text()) {
                $(this).remove();
            }
        });
        $("#" + flag + "_ncellInput").val("");
    }
}

function removeParamValue(flag) {
    //删除日期
    $("#" + flag + "_targetDate option").each(function () {
        if ("ALL" !== $(this).text()) {
            $(this).remove();
        }
    });
    $("#" + flag + "_waitforselDate option").each(function () {
        $(this).remove();
    });
    $("#" + flag + "_selectedDate option").each(function () {
        $(this).remove();
    });
    //删除bsc
    $("#" + flag + "_targetBsc option").each(function () {
        if ("ALL" !== $(this).text()) {
            $(this).remove();
        }
    });
    $("#" + flag + "_waitforselBsc option").each(function () {
        $(this).remove();
    });
    $("#" + flag + "_selectedBsc option").each(function () {
        $(this).remove();
    });
    //删除param
    $("#" + flag + "_targetParam option").each(function () {
        if ("ALL" !== $(this).text()) {
            $(this).remove();
        }
    });
    $("#" + flag + "_waitforselParam option").each(function () {
        $(this).remove();
    });
    $("#" + flag + "_selectedParam option").each(function () {
        $(this).remove();
    });
    //删除cell
    $("#" + flag + "_targetCell option").each(function () {
        if ("ALL" !== $(this).text()) {
            $(this).remove();
        }
    });
    $("#" + flag + "_cellInput").val("");
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

function ensure(select, selectOri, div, type) {
    //type标志只为cell下的CELl参数用
    var bscStr = "";
    var valueStr = "";
    if (document.getElementById(select).options.length === 0) {
        $("#info").css("background", "red");
        showInfoInAndOut("info", "请选择参数！");
        return false;
    }
    for (var i = 0; i < document.getElementById(select).options.length; i++) {
        bscStr += document.getElementById(select).options[i].text;
        if (i < document.getElementById(select).options.length - 1) {
            bscStr += ",";
        }
        if (type === "bscParam") {
            valueStr += document.getElementById(select).options[i].value;
            if (i < document.getElementById(select).options.length - 1) {
                valueStr += ",";
            }
        } else {
            valueStr = bscStr;
        }
    }
    $("#" + selectOri + " option[style='display:none']").remove();
    $("#" + selectOri).prepend("<option selected='selected' style='display:none' value='" + valueStr + "'>" + bscStr + "</option>");
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

//datatable是否有data的标记
var cellFlag = false;
var ncellFlag = false;
var channelFlag = false;
var typeFlag;

function searchRecord(type) {
    var param = $("#" + type + "_targetParam").find("option:selected").val();
    if (param === "ALL") {
        param = "";
        if(type === "cell") {
            for (var i = 0; i < cellParam.length; i++) {
                param += cellParam[i];
                if (i < cellParam.length - 1) {
                    param += ",";
                }
            }
        }else if(type === "ncell") {
            for (var j = 0; j < ncellParam.length; j++) {
                param += ncellParam[j];
                if (j < ncellParam.length - 1) {
                    param += ",";
                }
            }
        }else if(type === "channel") {
            for (var k = 0; k < channelParam.length; k++) {
                param += channelParam[k];
                if (k < channelParam.length - 1) {
                    param += ",";
                }
            }
        }
    }
    var bsc = $("#" + type + "_targetBsc").val();
    var date = $("#" + type + "_targetDate").val();
    var cell = $("#" + type + "_targetCell").val().trim();
    var cityid = $("#" + "city-menu-" + type).find("option:selected").val();
    if(type === "cell") {
        typeFlag = cellFlag;
    }else if(type === "channel"){
        typeFlag = channelFlag;
    }else if(type === "ncell") {
        typeFlag = ncellFlag;
    }
    if(typeFlag === true) {
        $("#" + type + "ListTab").DataTable().clear();
        $("#" + type + "ListTab").DataTable().destroy();
        $("#" + type + "Table").html("");
    }
    $(".loading").show();
    if (type === "cell" || type === "channel") {
        $.ajax({
            url: "../../api/gsm-param-query/query-cell-param",
            data: {
                "cellParam": param,
                "cellBsc": bsc,
                "cellDate": date,
                "cellForCell": cell,
                "cityId": cityid,
                "type" : type
            },
            type: "post",
            dataType: "text",
            success: function (raw) {
                $(".loading").css("display", "none");
                data = eval("(" + raw + ")");
                if (data.length === 0) {
                    if(type === "cell") {
                        cellFlag = false;
                    }else {
                        channelFlag = false;
                    }
                    $("#info").css("background", "red");
                    showInfoInAndOut("info", "没有符合条件的数据");
                } else {
                    if(type === "cell") {
                        cellFlag = true;
                    }else {
                        channelFlag = true;
                    }
                    var th = "";
                    var cloumn = "";
                    if(type === "cell") {
                        th = "<th>日期</th>" + "<th>MSC</th>" + "<th>BSC</th>" + "<th>CELL</th>";
                        cloumn = "[{'data': 'MEA_DATE'},{'data': 'MSC'}," +
                            "{'data': 'BSC'},{'data': 'CELL'},";
                    }else {
                        th = "<th>日期</th>" + "<th>MSC</th>" + "<th>BSC</th>" + "<th>CELL</th>"+
                            "<th>CH_GROUP</th>"+ "<th>CHGR_STATE</th>"+ "<th>CHGR_TG</th>";
                        cloumn = "[{'data': 'MEA_DATE'},{'data': 'MSC'}," +
                            "{'data': 'BSC'},{'data': 'CELL'},{'data': 'CH_GROUP'},{'data': 'CHGR_STATE'}," +
                            "{'data': 'CHGR_TG'},";
                    }
                    for (var p of param.split(",")) {
                        th += "<th>" + p + "</th>";
                        cloumn += "{'data': '" + p + "'},";
                    }
                    $("#" + type + "Table").append("<tr>" + th + "</tr>");
                    cloumn += "]";
                    var cloumnData = eval("(" + cloumn + ")");
                    $("#" + type + "ListTab").DataTable().clear();
                    $("#" + type + "ListTab").css("line-height", "12px")
                        .DataTable({
                            "data": data,
                            "columns": cloumnData,
                            "lengthChange": false,
                            "ordering": true,
                            "searching": false,
                            "destroy": true,
                            "language": {
                                url: "../../lib/datatables/1.10.16/i18n/Chinese.json"
                            }
                        });
                }
            }, error: function (err) {
                if(type === "cell") {
                    cellFlag = false;
                }else {
                    channelFlag = false;
                }
                $(".loading").css("display", "none");
                $("#info").css("background", "red");
                showInfoInAndOut("info", "后台程序错误！");
            }
        });
    }else if(type === "ncell") {
        var ncell = $("#ncell_targetNcell").val().trim();
        $.ajax({
            url: "../../api/gsm-param-query/query-cell-param",
            data: {
                "cellParam": param,
                "cellBsc": bsc,
                "cellDate": date,
                "cellForCell": cell,
                "cellForNcell": ncell,
                "cityId": cityid,
                "type": type
            },
            type: "post",
            dataType: "text",
            success: function (raw) {
                $(".loading").css("display", "none");
                var data = eval("(" + raw + ")");
                if (data.length === 0) {
                    ncellFlag = false;
                    $("#info").css("background", "red");
                    showInfoInAndOut("info", "没有符合条件的数据");
                } else {
                    ncellFlag = true;
                    var th = "<th>日期</th>" + "<th>MSC</th>" + "<th>BSC</th>" + "<th>CELL</th>" +
                        "<th>N_BSC</th>" + "<th>N_CELL</th>" ;
                    var cloumn = "[{'data': 'MEA_DATE'},{'data': 'MSC'}," +
                        "{'data': 'BSC'},{'data': 'CELL'},{'data': 'N_BSC'},{'data': 'N_CELL'}," ;
                    for (var p of param.split(",")) {
                        th += "<th>" + p + "</th>";
                        cloumn += "{'data': '" + p + "'},";
                    }
                    $("#ncellTable").append("<tr>" + th + "</tr>");
                    cloumn += "]";
                    var cloumnData = eval("(" + cloumn + ")");
                    $("#ncellListTab").DataTable().clear();
                    $("#ncellListTab").css("line-height", "12px")
                        .DataTable({
                            "data": data,
                            "columns": cloumnData,
                            "lengthChange": false,
                            "ordering": true,
                            "searching": false,
                            "destroy": true,
                            "language": {
                                url: "../../lib/datatables/1.10.16/i18n/Chinese.json"
                            }
                        });
                }
            }, error: function (err) {
                ncellFlag = false
                $(".loading").css("display", "none");
                $("#info").css("background", "red");
                showInfoInAndOut("info", "后台程序错误！");
            }
        });
    }
}


var cellParam = ["ACCMIN", "ACC_16", "ACSTATE", "ACTIVE_32", "AGBLK", "ALLOCPREF", "AMRPCSTATE", "ATT", "AW", "BCC", "BCCHDTCB", "BCCHDTCBHYST", "BCCHLOSS", "BCCHLOSSHYST", "BCCHNO", "BCCHREUSE", "BCCHTYPE", "BPDCHBR", "BSPWR", "BSPWRB", "BSPWRMIN", "BSPWRT", "BSRXMIN", "BSRXSUFF", "BSTXPWR", "BTSPS", "BTSPSHYST", "CB", "CBCHD", "CBQ", "CCHPWR", "CELLQ", "CELL_STATE", "CELL_TYPE", "CHAP", "CHCSDL", "CI", "CLSACC", "CLSLEVEL", "CLSRAMP", "CLS_STATUS", "CMDR", "CODECREST", "CRH", "CRO", "CSPSALLOC", "CSPSPRIO", "C_SYS_TYPE", "DCHNO_128", "DHA", "DMPR", "DMQB", "DMQBAMR_15", "DMQBNAMR_15", "DMQG", "DMQGAMR_15", "DMQGNAMR_15", "DMSUPP", "DMTFAMR", "DMTFNAMR", "DMTHAMR", "DMTHNAMR", "DRX", "DTCB", "DTCBHYST", "DTHAMR_15", "DTHNAMR_15", "DTMSTATE", "DTXD", "DTXU", "DYNBTSPWR_STATE", "DYNMSPWR_STATE", "DYNVGCH", "ECSC", "EFACTOR", "EITEXCLUDED", "EPDCHBR", "EXTPEN", "FASTMSREG", "FBOFFS", "FBVGCHALLOC", "FDDMRR", "FDDQMIN", "FDDQMINOFF", "FDDQOFF", "FDDREPTHR2", "FDDRSCPMIN", "FERLEN", "FLEXHIGHGPRS", "FNOFFSET", "FPDCH", "GAMMA", "GHPRIO", "GLPRIOTHR", "GPDCHBR", "GPRSPRIO", "GPRSSUP", "HCSIN", "HCSOUT", "HOCLSACC", "HPBSTATE", "HYSTSEP", "IAN", "ICMSTATE", "IDLE_32", "IHO", "INTAVE", "IRC", "ISHOLEV", "LA", "LAC", "LAYER", "LAYERHYST", "LAYERTHR", "LCOMPDL", "LCOMPUL", "LIMIT1", "LIMIT2", "LIMIT3", "LIMIT4", "LOL", "LOLHYST", "MAXIHO", "MAXISHO", "MAXLAPDM", "MAXRET", "MAXSBLK", "MAXSMSG", "MAXTA", "MBCR", "MC", "MCC", "MFRMS", "MINREQTCH", "MISSNM", "MNC", "MPDCH", "MSRXMIN", "MSRXSUFF", "MSTXPWR", "NCC", "NCCPERM_8", "NDIST", "NECI", "NNCELLS", "OPTMSCLASS", "PDCHPREEMPT", "PHCSTHR", "PLAYER", "PRACHBLK", "PRIMPLIM", "PSKONBCCH", "PSSBQ", "PSSHF", "PSSTA", "PSSTEMP", "PT", "PTIMBQ", "PTIMHF", "PTIMTA", "PTIMTEMP", "QCOMPDL", "QCOMPUL", "QDESDL", "QDESDLAFR", "QDESDLAHR", "QDESDLAWB", "QDESUL", "QDESULAFR", "QDESULAHR", "QDESULAWB", "QEVALSD", "QEVALSI", "QLENGTH", "QLENSD", "QLENSI", "QLIMDL", "QLIMDLAFR", "QLIMDLAWB", "QLIMUL", "QLIMULAFR", "QLIMULAWB", "QOFFSETDL", "QOFFSETDLAFR", "QOFFSETDLAWB", "QOFFSETUL", "QOFFSETULAFR", "QOFFSETULAWB", "QSC", "QSCI", "QSI", "REPPERNCH", "RESLIMIT", "RHYST", "RLINKT", "RLINKTAFR", "RLINKTAHR", "RLINKTAWB", "RLINKUP", "RLINKUPAFR", "RLINKUPAHR", "RLINKUPAWB", "RTTI", "SCALLOC", "SCHO", "SCLD", "SCLDLOL", "SCLDLUL", "SCLDSC", "SLEVEL", "SPDCH", "SPRIO", "SSDESDL", "SSDESDLAFR", "SSDESDLAHR", "SSDESDLAWB", "SSDESUL", "SSDESULAFR", "SSDESULAHR", "SSDESULAWB", "SSEVALSD", "SSEVALSI", "SSLENDL", "SSLENSD", "SSLENSI", "SSLENUL", "SSOFFSETDL", "SSOFFSETDLAFR", "SSOFFSETDLAWB", "SSOFFSETUL", "SSOFFSETULAFR", "SSOFFSETULAWB", "SSRAMPSD", "SSRAMPSI", "STIME", "STREAMSUP", "T3212", "TALIM", "TAOL", "TAOLHYST", "TBFDLLIM", "TBFULLIM", "TIHO", "TMAXIHO", "TRAFBLK", "TX", "VGCHALLOC", "XRANGE", "AFLP", "AFRCSCC", "AHRCSCC", "AIRCT", "ANTENNA_GAIN", "ANTENNA_TILT", "ANTENNA_TYPE", "AQPSKONBCCH", "ASSV", "ATHABIS", "ATHABISPR", "AWBCSCC", "BSRPWRHO", "BSRPWROFFSET", "CELL_DIR", "CHMAX", "CLTHV", "CO", "COVERAGEE_8", "CP_BCC_TSC", "DAMRCRABIS", "DAMRCRABISPR", "DCDLACT", "DHASS", "DHASSTHRASS", "DHASSTHRHO", "DHPR", "DISPLAY_TAG", "DLPCE", "DLPCE2A", "DLPCG", "DTHAMRWB_15", "DTXFUL", "E2AFACTOR", "E2AINITMCS", "E2ALQC", "E2APDCHBR", "EA", "EARFCNIDLE_8", "EC_16", "EFRCSCC", "EFTASTATE", "EHPRIOTHR_8", "EINITMCS", "ELPRIOTHR_8", "ENV_CHAR", "EQRXLEVMIN_8", "ERATPRIO_8", "FASTRET3G", "FASTRETLTE", "FDDARFCNIDLE_32", "FSLOTS", "FULLAQPSK", "GMEASTHR", "GRATPRIO_0", "GTRES", "HEIGHT", "HRCSCC", "INITDLPCE", "INITDLPCE2A", "INITDLPCG", "LATITUDE", "LONGITUDE", "MAX_ALTITUDE", "MAX_CELL_RADIUS", "MINCHBW_8", "MIN_ALTITUDE", "MSRPWRHO", "MSRPWROFFSET", "NCPROF_16", "NCRPT", "NCS", "NCSTAT", "NUMINT", "OL_BSPWRMIN", "OL_BSPWRT", "OL_BSTXPWR", "OL_CELL", "OL_DTXFUL", "OL_IHO", "OL_LCOMPDL", "OL_LCOMPUL", "OL_MAXIHO", "OL_MSTXPWR", "OL_NUMINT", "OL_QCOMPDL", "OL_QCOMPUL", "OL_QDESDL", "OL_QDESDLAFR", "OL_QDESDLAHR", "OL_QDESDLAWB", "OL_QDESUL", "OL_QDESULAFR", "OL_QDESULAHR", "OL_QDESULAWB", "OL_QLENDL", "OL_QLENUL", "OL_QLIMDL", "OL_QLIMDLAFR", "OL_QLIMDLAWB", "OL_QLIMUL", "OL_QLIMULAFR", "OL_QLIMULAWB", "OL_QOFFSETDL", "OL_QOFFSETDLAFR", "OL_QOFFSETDLAWB", "OL_QOFFSETUL", "OL_QOFFSETULAFR", "OL_QOFFSETULAWB", "OL_REGINTDL", "OL_REGINTUL", "OL_SDCCHREG", "OL_SLCA_BCCHACL", "OL_SLCA_BCCHLVA", "OL_SLCA_CBCHACL", "OL_SLCA_CBCHLVA", "OL_SLCA_SDCCHACL", "OL_SLCA_SDCCHLVA", "OL_SLCA_STATE", "OL_SLCA_TCHAMRFRACL", "OL_SLCA_TCHAMRFRLVA", "OL_SLCA_TCHAMRHRACL", "OL_SLCA_TCHAMRHRLVA", "OL_SLCA_TCHAMRWBACL", "OL_SLCA_TCHAMRWBLVA", "OL_SLCA_TCHBPCACL", "OL_SLCA_TCHBPCLVA", "OL_SLCA_TCHEFRACL", "OL_SLCA_TCHEFRLVA", "OL_SLCA_TCHFRACL", "OL_SLCA_TCHFRLVA", "OL_SLCA_TCHHRACL", "OL_SLCA_TCHHRLVA", "OL_SSDESDL", "OL_SSDESDLAFR", "OL_SSDESDLAHR", "OL_SSDESDLAWB", "OL_SSDESUL", "OL_SSDESULAFR", "OL_SSDESULAHR", "OL_SSDESULAWB", "OL_SSLENDL", "OL_SSLENUL", "OL_SSOFFSETDL", "OL_SSOFFSETDLAFR", "OL_SSOFFSETDLAWB", "OL_SSOFFSETUL", "OL_SSOFFSETULAFR", "OL_SSOFFSETULAWB", "OL_TIHO", "OL_TMAXIHO", "OL_TSC", "OL_USERDATA", "OWNBCCHINACT", "OWNBCCHINIDLE", "PCIDG_8", "PCIDP_80", "PCID_8", "PDCHALLOC", "PLMNNAME", "PRIOCR", "QBAFRV", "QBAHRV", "QBAWBV", "QBNAV", "QDESDLOV", "QDESULOV", "QLENDL", "QLENUL", "RAC", "REGINTDL", "REGINTUL", "RIMNACC", "RME", "RO", "RTTIINITMCS", "SCLDLL", "SCLDUL", "SDCCHOL", "SDCCHREG", "SDCCHUL", "SECTOR_ANGLE", "SIMSG1", "SIMSG7", "SIMSG8", "SLCA_BCCHACL", "SLCA_BCCHLVA", "SLCA_BTSPSSDCCHACL", "SLCA_BTSPSSDCCHLVA", "SLCA_BTSPSTCHACL", "SLCA_BTSPSTCHBPCACL", "SLCA_BTSPSTCHBPCLVA", "SLCA_BTSPSTCHLVA", "SLCA_CBCHACL", "SLCA_CBCHLVA", "SLCA_SDCCHACL", "SLCA_SDCCHLVA", "SLCA_STATE", "SLCA_TCHAMRFRACL", "SLCA_TCHAMRFRLVA", "SLCA_TCHAMRHRACL", "SLCA_TCHAMRHRLVA", "SLCA_TCHAMRWBACL", "SLCA_TCHAMRWBLVA", "SLCA_TCHBPCACL", "SLCA_TCHBPCLVA", "SLCA_TCHEFRACL", "SLCA_TCHEFRLVA", "SLCA_TCHFRACL", "SLCA_TCHFRLVA", "SLCA_TCHHRACL", "SLCA_TCHHRLVA", "SLOW", "SPERIOD", "SSTHRASSV", "SSTHRV", "SS_SDCCH_STATE", "SS_TCH_STATE", "TCHFROL", "TCHFRUL", "TCHHROL", "TCHHRUL", "THRAV", "THRDV", "THRVP", "TILT", "TSC", "TSCSET1PAIRS", "TSS", "TXTYPE", "UHPRIOTHR_32", "ULPRIOTHR_32", "UMFI_ACTIVE_64", "UMFI_IDLE_64", "UQRXLEVMIN_32", "URATPRIO_32", "USERDATA", "VAMOSCELLSTATE", "VCSCC", "VHOSUCCESS"];
var ncellParam = ["AWOFFSET", "BQOFFSET", "BQOFFSETAFR", "BQOFFSETAWB", "CAND", "CS", "GPRSVALID", "HIHYST", "KHYST", "KOFFSET", "LHYST", "LOFFSET", "LOHYST", "OFFSET", "PROFFSET", "TRHYST", "TROFFSET", "USERDATA", "AWOFFSET", "BQOFFSET", "BQOFFSETAFR", "BQOFFSETAWB", "CAND", "CS", "GPRSVALID", "HIHYST", "KHYST", "KOFFSET", "LHYST", "LOFFSET", "LOHYST", "OFFSET", "PROFFSET", "TRHYST", "TROFFSET", "USERDATA", "AWOFFSET", "BQOFFSET", "BQOFFSETAFR", "BQOFFSETAWB", "CAND", "CS", "GPRSVALID", "HIHYST", "KHYST", "KOFFSET", "LHYST", "LOFFSET", "LOHYST", "OFFSET", "PROFFSET", "TRHYST", "TROFFSET", "USERDATA"];
var channelParam = ["BAND", "BCCD", "CBCH", "CCCH", "DCHNO_32", "EACPREF", "ETCHTN_8", "EXCHGR", "HOP", "HOPTYPE", "HSN", "MAIO_16", "NUMREQBPC", "NUMREQCS3CS4BPC", "NUMREQE2ABPC", "NUMREQEGPRSBPC", "ODPDCHLIMIT", "SAS", "SCTYPE", "SDCCH", "TN7BCCH", "TNBCCH", "TN_8", "TSC", "USERDATA", "BAND", "BCCD", "CBCH", "CCCH", "DCHNO_32", "EACPREF", "ETCHTN_8", "EXCHGR", "HOP", "HOPTYPE", "HSN", "MAIO_16", "NUMREQBPC", "NUMREQCS3CS4BPC", "NUMREQE2ABPC", "NUMREQEGPRSBPC", "ODPDCHLIMIT", "SAS", "SCTYPE", "SDCCH", "TN7BCCH", "TNBCCH", "TN_8", "TSC", "USERDATA", "BAND", "BCCD", "CBCH", "CCCH", "DCHNO_32", "EACPREF", "ETCHTN_8", "EXCHGR", "HOP", "HOPTYPE", "HSN", "MAIO_16", "NUMREQBPC", "NUMREQCS3CS4BPC", "NUMREQE2ABPC", "NUMREQEGPRSBPC", "ODPDCHLIMIT", "SAS", "SCTYPE", "SDCCH", "TN7BCCH", "TNBCCH", "TN_8", "TSC", "USERDATA"];
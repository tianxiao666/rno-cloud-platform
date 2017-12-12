var cellParams = ["BCCH", "BSIC", "ATT", "ACC", "CB", "CBQ", "CCHPWR", "MAXRET", "NCCPERM", "T3212", "QCOMPDL", "PTIMTEMP",
    "QLENSD", "TO", "TX", "QDESDL", "MISSNM", "SSDESDLAFR", "SSDESDLAHR", "QEVALSD", "QEVALSI", "SSEVALSD", "PTIMHF", "PTIMTA",
    "SSEVALSI", "SCHO", "SSRAMPSD", "SSRAMPSI", "RLINKUP", "CELLQ", "RLINKT", "AW", "RLINKTAFR", "PSSTEMP", "PTIMBQ", "RLINKTAHR",
    "NECI", "DHA", "CLS_STATUS", "CLSLEVEL", "SCLD", "SCLDSC", "CLSACC", "FNOFFSET", "BSRXMIN", "QLENSI", "HOCLSACC", "ACTIVEMBCCHNO", "IDLEMBCCHNO",
    "DMPR", "SSDESULAFR", "SSDESULAHR", "ACSTATE", "QCOMPUL", "QDESUL", "BSPWR", "QLIMDL", "SLEVEL", "LIMIT1", "STIME",
    "XRANGE", "LIMIT2", "BTSPS", "LIMIT3", "LIMIT4", "BTSPSHYST", "BSTXPWR", "QLIMUL", "AGBLK", "BCCHTYPE", "ECSC", "DTXD", "RLINKUPAFR",
    "RLINKUPAHR", "DTXU", "C_SYS_TYPE", "MBCR", "CRO", "SSDESDL", "IRC", "HCSIN", "CHCSDL", "HCSOUT", "LA", "CSPSALLOC", "CSPSPRIO", "GPRSSUP",
    "MPDCH", "LAYER", "SSDESUL", "PSKONBCCH", "SCALLOC", "BSPWRB", "BSPWRT", "MSTXPWR", "CHAP", "DTMSTATE", "PRIMPLIM", "MSRXMIN",
    "ACCMIN", "IHO", "MAXIHO", "TIHO", "TMAXIHO", "SSOFFSETUL", "SSOFFSETDL", "QOFFSETUL", "QOFFSETDL", "SSOFFSETULAFR", "PT", "SSLENSD",
    "SSOFFSETDLAFR", "QOFFSETULAFR", "QOFFSETDLAFR", "CLSRAMP", "MAXTA", "LAYERTHR", "LAYERHYST", "BSRXSUFF", "MSRXSUFF", "DYNMSPWR_STATE",
    "SSLENSI", "QLIMULAFR", "QLIMDLAFR", "TALIM", "FLEXHIGHGPRS", "PDCHPREEMPT", "TBFDLLIM", "TBFULLIM", "BSPWRMIN", "ISHOLEV", "LAC", "CRH",
    "FPDCH", "DYNBTSPWR_STATE", "FASTMSREG", "GAMMA", "GPRSPRIO", "HYSTSEP", "LCOMPDL", "LCOMPUL", "MFRMS", "CI", "PSSBQ", "PSSHF", "PSSTA"];

var chgParams = ["CHGR_STATE", "CHGR_TG", "BAND", "BCCD", "CBCH", "CCCH", "EACPREF", "DCHNO_32", "ETCHTN", "EXCHGR", "HOP", "HOPTYPE", "HSN", "MAIO",
    "NUMREQBPC", "NUMREQCS3CS4BPC", "NUMREQE2ABPC", "NUMREQEGPRSBPC", "ODPDCHLIMIT"];

var neightbourParams = ["AWOFFSET", "BQOFFSET", "BQOFFSETAFR", "BQOFFSETAWB", "CAND", "CS", "GPRSVALID", "HIHYST", "KHYST", "KOFFSET",
    "LHYST", "OFFSET", "LOHYST", "PROFFSET", "TRHYST", "TROFFSET", "USERDATA"];
$(function () {
    // 初始化区域联动
    initAreaSelectors({selectors: ["province-id", "city-id"]});

    // 执行 laydate 实例 
    var dateBeg = new Date();
    dateBeg.setFullYear(2014,11,1);
    laydate.render({elem: '#begUploadDate', value: dateBeg});
    dateBeg.setFullYear(2014,11,5);
    laydate.render({elem: '#endUploadDate', value: dateBeg});

    //加载bsc树形菜单
    getAllBscCell();
    //加载参数类型
    changeParam();

    //参数对比点击事件
    $("#compareBtn").click(function () {
        paramCompare('compare');
    });

    //导出
    $("#exportBtn").click(function () {
        paramCompare('export');
    });

});

function getAllBscCell() {
    $("#bscTree").html("");
    var cityId = $("#city-id").val();
    $.ajax({
        url : '/api/gsm-param-check/check-bsc-by-cityId',
        data : {
            'cityId' : cityId
        },
        type : 'get',
        dataType : 'text',
        success : function(raw) {
            if (raw === '') {
                $("#info").css("background", "yellow");
                showInfoInAndOut("info", "该城市匹配不到BSC");
            }else {
                var data = eval("("+raw+")");
                var html = "";
                html += "<li><input name='bscChk'  type='checkbox'/><span>全选</span><ul>";
                for (var i = 0; i < data.length; i++) {
                    html += "<li><input name='bsc' value='" + data[i]['id'] + "' type='checkbox'/> <span>" + data[i]['bsc'] + "</span></li>";
                }
                html += "</ul></li>";
                $("#allBsc").html(html);
                $("#allBsc").treeview();
                //加入关联选择事件
                $("input[name='bscChk']").click(function () {
                    var checkedValue = this.checked;
                    $(this).parent("li").find("input[name='bsc']").attr("checked", checkedValue);
                });
            }
        }, error: function (err) {
            $("#info").css("background", "red");
            showInfoInAndOut("info", "后台程序错误！");
        }
    });
}

function changeParam() {
    $("#paramCheckBox").html("");

    var paramType = $('input[name="paramType"]:checked').val();
    var paramHtml;
    if (paramType === "cell") {
        paramHtml = strToHtmlByParams(cellParams);
    } else if (paramType === "channel") {
        paramHtml = strToHtmlByParams(chgParams);
    } else if (paramType === "neighbour") {
        paramHtml = strToHtmlByParams(neightbourParams);
    }

    $("#paramCheckBox").html(paramHtml);
    //console.log(paramHtml);

    //绑定树形菜单样式
    $("#paramCheckBox").treeview({
        collapsed: true
    });

    //加入关联选择事件
    $("input[name='pmChk']").click(function () {
        var checkedValue = this.checked;
        $(this).parent("li").find("input[name='pmChk']").attr("checked", checkedValue);
    });
    $("input[name='pmChk']").click(function () {
        var checkedValue = this.checked;
        $(this).parent("li").find("input[name='params']").attr("checked", checkedValue);
    });
}

function paramCompare(flag) {
    //获取城市id
    var cityId = $("#city-id").val();
    //获取参数类型
    var paramType = $('input[name="paramType"]:checked').val();
    //获取所选择的参数
    var paramStr = "";
    $('input:checkbox[name="params"]:checked').each(function () {
        paramStr += $(this).val() + ",";
    });
    paramStr = paramStr.substring(0, paramStr.length - 1);
    //获取所选择的BSC
    var bscStr = "";
    $('input:checkbox[name="bsc"]:checked').each(function () {
        bscStr += $(this).val() + ",";
    });
    bscStr = bscStr.substring(0, bscStr.length - 1);
    //获取第一个日期
    var date1 = $("#begUploadDate").val();
    //获取第二个日期
    var date2 = $("#endUploadDate").val();

    //验证
    if (paramType == undefined || paramType == "") {
        alert("请选择参数类型！");
        return;
    }
    if (paramStr == undefined || paramStr == "") {
        alert("请选择需要对比的参数！");
        return;
    }
    if (bscStr == undefined || bscStr == "") {
        alert("请选择需要对比的BSC！");
        return;
    }
    if (date1 == date2) {
        alert("选择比较的两个日期是同一天！");
        return;
    }
    if(flag==='compare') {
        //清空
        $("#paramDiffTable").html("");
        $("#paramDiffDetailTable").html("");
        $("#paramName").html("");
        //禁止重复点击
        $(this).attr("disabled", "disabled");
        $(".loading").show();
        $.ajax({
            url: '/api/gsm-param-change/change-param',
            data: {
                'cityId': cityId,
                'paramType': paramType,
                'paramStr': paramStr,
                'bscStr': bscStr,
                'date1': date1,
                'date2': date2
            },
            type: 'get',
            dataType: 'text',
            success: function (raw) {
                $(".loading").css("display", "none");
                var data = eval("(" + raw + ")");
                var flag;
                if(data.length === 1) {
                    flag = data[0]['dateMessage'];
                }
                if (flag) {
                    $("#info").css("background", "yellow");
                    showInfoInAndOut("info", flag);
                } else {
                    if (data.length === 0) {
                        $("#info").css("background", "yellow");
                        showInfoInAndOut("info", "没有找到相应的数据");
                    }else {
                        var paramTitle = paramStr.split(",");
                        var html = "<tr><th>BSC</th>";
                        for (var i = 0; i < paramTitle.length; i++) {
                            html += "<th>" + paramTitle[i] + "</th>";
                        }
                        html += "</tr>";
                        var title;
                        var one;
                        for (var i = 0; i < data.length; i++) {
                            html += "<tr>";
                            html += "<td>" + data[i]['BSC_ENGNAME'] + "</td>";
                            for (var j = 0; j < paramTitle.length; j++) {
                                title = paramTitle[j];
                                if (Number(data[i][title]) > 0) {
                                    html += "<td style='background:red; cursor:pointer' " +
                                        " onclick='getParamDiffDetail(\"" + data[i]['BSC_ENGNAME'] + "\",\""
                                        + paramType + "\",\"" + title + "\",\"" + cityId + "\",\"" + date1 + "\",\"" + date2 + "\")'>"
                                        + data[i][title] + "</td>";
                                } else {
                                    html += "<td>" + data[i][title] + "</td>";
                                }
                            }
                            html += "</tr>";
                        }
                        $("#paramDiffTable").html(html);
                    }
                }
            }, error: function (err) {
                $(".loading").css("display", "none");
                $("#info").css("background", "red");
                showInfoInAndOut("info", "后台程序错误！");
            },complete: function () {
                //解除禁止点击
                $("#compareBtn").removeAttr("disabled");
            }
        });
    }else if(flag==='export'){
        $("#info").css("background", "yellow");
        showInfoInAndOut("info", "正在导出文件，请耐心等待.....");
        $("#paramType").val(paramType);
        $("#cityId").val(cityId);
        $("#bscStr").val(bscStr);
        $("#paramStr").val(paramStr);
        //提交导出信息
        $("#downloadChangeParamDataForm").submit;
    }
}

/**
 * 获取参数对比详情
 */
function getParamDiffDetail(bsc, paramType, param, cityId, date1, date2) {
    $(".loading").show();
    $.ajax({
        url: '/api/gsm-param-change/get-param-detail',
        data: {
            'bsc': bsc,
            'paramType': paramType,
            'param': param,
            'cityId': cityId,
            'date1': date1,
            'date2': date2
        },
        type: 'get',
        dataType: 'text',
        success: function (raw) {
            $(".loading").css("display", "none");
            var data = eval("(" + raw + ")");
            $("#paramDiffDetailTable").html("");
            $("#paramName").html("");
            if (data.length === 0) {
                $("#info").css("background", "yellow");
                showInfoInAndOut("info", "没有找到相应的数据");
            }else {
                var html = "";
                if (paramType == "cell") {
                    html += "<tr><th>BSC</th><th>CELL</th><th>" + date1 + "</th><th>" + date2 + "</th></tr>";
                    for (var i = 0; i < data.length; i++) {
                        html += "<tr>";
                        html += "<td>" + bsc + "</td>";
                        html += "<td>" + getValidValue(data[i]['CELL']) + "</td>";
                        html += "<td>" + getValidValue(data[i]['PARAM1']) + "</td>";
                        html += "<td>" + getValidValue(data[i]['PARAM2']) + "</td>";
                        html += "</tr>";
                    }
                } else if (paramType == "channel") {
                    html += "<tr><th>BSC</th><th>CELL</th><th>CHGR</th><th>" + date1 + "</th><th>" + date2 + "</th></tr>";
                    for (var i = 0; i < data.length; i++) {
                        html += "<tr>";
                        html += "<td>" + bsc + "</td>";
                        html += "<td>" + getValidValue(data[i]['CELL']) + "</td>";
                        html += "<td>" + getValidValue(data[i]['CH_GROUP']) + "</td>";
                        html += "<td>" + getValidValue(data[i]['PARAM1']) + "</td>";
                        html += "<td>" + getValidValue(data[i]['PARAM2']) + "</td>";
                        html += "</tr>";
                    }
                } else if (paramType == "neighbour") {
                    html += "<tr><th>BSC</th><th>CELL</th><th>NCELL</th><th>" + date1 + "</th><th>" + date2 + "</th></tr>";
                    for (var i = 0; i < data.length; i++) {
                        html += "<tr>";
                        html += "<td>" + bsc + "</td>";
                        html += "<td>" + getValidValue(data[i]['CELL']) + "</td>";
                        html += "<td>" + getValidValue(data[i]['N_CELL']) + "</td>";
                        html += "<td>" + getValidValue(data[i]['PARAM1']) + "</td>";
                        html += "<td>" + getValidValue(data[i]['PARAM2']) + "</td>";
                        html += "</tr>";
                    }
                }
                $("#paramName").html(param);
                $("#paramDiffDetailTable").html(html);
            }
        }, error: function (err) {
            $(".loading").css("display", "none");
            $("#info").css("background", "red");
            showInfoInAndOut("info", "后台程序错误！");
        }
    });
}

/**
 * 将参数类型数组转成ul格式的html字符串
 * 并以首字母为分组，排序
 */
function strToHtmlByParams(params) {
    var html = "";
    //将字符串按字母排序
    params.sort();
    //获取原数组中的元素首字母，作为一个新数组，并去掉重复
    var ul = [];
    var a;
    for (var i = 0; i < params.length; i++) {
        a = params[i].substring(0, 1);
        ul.push(a);
    }
    ul = unique(ul);
    //拼接html
    html += "<li><input name='pmChk' checked='checked' type='checkbox'/><span>全选</span><ul>";
    for (var i = 0; i < ul.length; i++) {
        html += "<li><input name='pmChk' checked='checked' type='checkbox'/> <span>" + ul[i] + "</span><ul>";
        for (var j = 0; j < params.length; j++) {
            if (ul[i] == params[j].substring(0, 1)) {
                html += "<li><input name='params' checked='checked' value='" + params[j] + "' type='checkbox'/> <span>" + params[j] + "</span></li>";
            }
        }
        html += "</ul></li>";
    }
    html += "</ul></li>";
    return html;
}

/**
 * 返回元素不重复的数组
 */
function unique(arr) {
    var result = [], hash = {};
    for (var i = 0, elem; (elem = arr[i]) != null; i++) {
        if (!hash[elem]) {
            result.push(elem);
            hash[elem] = true;
        }
    }
    return result;
}

//提示信息
function showInfoInAndOut(div, info) {
    var divSet = $("#" + div);
    divSet.html(info);
    divSet.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

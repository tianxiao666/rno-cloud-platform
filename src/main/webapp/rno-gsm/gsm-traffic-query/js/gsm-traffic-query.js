var isSerched = false;
var exportParamMap;
$(function () {
    // 设置导航标题
    setNavTitle("navTitle");
    $(".draggable").draggable();
    $("#trigger").css("display", "none");
    // 设置jquery ui
    jqueryUiSet();

    //执行 laydate 实例 
    laydate.render({elem: '#audioBeginTime', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#audioLatestAllowedTime', value: new Date()});
    laydate.render({elem: '#dataBeginTime', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#dataLatestAllowedTime', value: new Date()});
    laydate.render({elem: '#qualityBeginTime', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#qualityLatestAllowedTime', value: new Date()});

    initAreaSelectors({selectors: ["provinceId", "cityId", "audioAreaId"]});
    initAreaSelectors({selectors: ["provinceId1", "cityId1", "dataAreaId"]});
    initAreaSelectors({selectors: ["qualityProvinceId", "qualityCityId"]});
});
//jquery ui 效果
function jqueryUiSet() {
    $("#tabs").tabs();
    $("#searchImportDiv").css("height","46px");
    $("#importDiv").css("height","290px");
}
/**
 *小区 英文名 中文名 输入查询条件input onfocus事件
 */
function cellInputFocus(me) {
    var val = $(me).val();
    if ($(me).attr("class") === "encell") {
        if (val === "小区英文名") {
            $(me).val("");
        }
    } else {
        if (val === "小区中文名") {
            $(me).val("");
        }
    }
    $(me).css("color", "");
}

/**
 *小区 英文名 中文名 输入查询条件input onblur事件
 */
function cellInputBlur(me) {
    var val = $(me).val();
    if ($(me).attr("class") === "encell") {
        if ($.trim(val) === "") {
            $(me).val("小区英文名").css("color", "grey");
        }
    } else {
        if ($.trim(val) === "") {
            $(me).val("小区中文名").css("color", "grey");
        }
    }
}

function exportData(exportFromId) {
    if (isSerched === false){
        alert("请先查询！");
    }else {
        $("#" + exportFromId + "DownloadFileForm").submit();

    }
}

//小区业务指标查询
function trafficQuery(tab){
    var beginTime = $("#" + tab +"BeginTime").val();
    $("#" + tab +"BeginTime").val(new Date(beginTime));
    var endTime = $("#" + tab +"LatestAllowedTime").val();
    $("#" + tab +"LatestAllowedTime").val(new Date(endTime));
    if ($("#" + tab +"Cell").val() === '小区英文名'){
        $("#" + tab +"Cell").val('');
    }
    if ($("#" + tab +"CellChineseName").val() === '小区中文名'){
        $("#" + tab +"CellChineseName").val('');
    }
    $("#" + tab +"DlSearchType").val($("#" + tab +"SearchType").val());
    $("#" + tab +"DlAreaId").val($("#" + tab +"AreaId").val());
    $("#" + tab +"DlBeginTime").val($("#" + tab +"BeginTime").val());
    $("#" + tab +"DlLatestAllowedTime").val($("#" + tab +"LatestAllowedTime").val());
    $("#" + tab +"DlStsPeriod").val($("#" + tab +"StsPeriod").val());
    $("#" + tab +"DlEngName").val($("#" + tab +"EngName").val());
    $("#" + tab +"DlCell").val($("#" + tab +"Cell").val());
    $("#" + tab +"DlCellChineseName").val($("#" + tab +"CellChineseName").val());
    $("#" + tab +"DlStsType").val($("#" + tab +"StsType").val());
    $("#form_tab_" + tab).ajaxSubmit({
        url:'/api/gsm-traffic-query',
        type:'post',
        dataType:'text',
        success:function(data){
            isSerched =true;
            $('#'+ tab + 'QueryCellTrafficTab').css("line-height", "12px");
            $('#'+ tab + 'QueryCellTrafficTab').DataTable( {
                "data": JSON.parse(data),
                "columns": [
                    { "data": "stsDate" , "render": function (data) {
                        if($("#" + tab +"StsType").val() ==="default"){
                            return (new Date(data)).Format("yyyy-MM-dd hh:mm:ss");
                        }
                        return (new Date(data)).Format("yyyy-MM-dd");
                    }},
                    { "data": "period" },
                    { "data": "bsc" },
                    { "data": "cellEnglishName" },
                    { "data": "cellChineseName" },
                    { "data": "trate" },
                    { "data": "defChannel" },
                    { "data": "availableChannel" },
                    { "data": "carrierNum" },
                    { "data": "wirelessUseRate" },
                    { "data": "trafficVolume" },
                    { "data": "preTrafficVolume" }
                ],
                "lengthChange": false,
                "ordering": true,
                "searching": false,
                "destroy": true,
                "language": {
                    url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
                }
            } );
        },
        complete:function () {
            if ($("#" + tab +"Cell").val() === ''){
                $("#" + tab +"Cell").val('小区英文名');
            }
            if ($("#" + tab +"CellChineseName").val() === ''){
                $("#" + tab +"CellChineseName").val('小区中文名');
            }
        }

    });
    $("#" + tab +"BeginTime").val(beginTime);
    $("#" + tab +"LatestAllowedTime").val(endTime);
    $('#queryCellTrafficTab').css("line-height", "12px");

}

function searchCityNetQuality() {
    var beginTime = $("#qualityBeginTime").val();
    var endTime = $("#qualityLatestAllowedTime").val();
    $("#qualityBeginTime").val(new Date(beginTime));
    $("#qualityLatestAllowedTime").val(new Date(endTime));
    $("#form_tab_2").ajaxSubmit({
        url:'/api/gsm-traffic-quality-query',
        type:'post',
        dataType:'text',
        beforeSend:function () {

        },
        success:function(data){
            $('#queryCityNetworkDT').css("line-height", "12px");
            $('#queryCityNetworkDT').DataTable( {
                "data": JSON.parse(data),
                "columns": [
                    { "data": "staticTime" , "render": function (data) {
                        return (new Date(data)).Format("yyyy-MM-dd");
                    }},
                    { "data": "type" },
                    { "data": "area.name" },
                    { "data": "score" },
                    { "data": "indexClass" },
                    { "data": "indexName" },
                    { "data": "indexValue" }
                ],
                "lengthChange": false,
                "ordering": true,
                "searching": false,
                "destroy": true,
                "language": {
                    url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
                }
            } );
        },
        complete:function () {

        }
    });
    $("#qualityBeginTime").val(beginTime);
    $("#qualityLatestAllowedTime").val(endTime);
}

Date.prototype.Format = function(fmt){
    //author: Shf
    var o = {
        "M+" : this.getMonth()+1,                 //月份
        "d+" : this.getDate(),                    //日
        "h+" : this.getHours(),                   //小时
        "m+" : this.getMinutes(),                 //分
        "s+" : this.getSeconds(),                 //秒
        "q+" : Math.floor((this.getMonth()+3)/3), //季度
        "S"  : this.getMilliseconds()             //毫秒
    };
    if(/(y+)/.test(fmt)){
        fmt = fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    }

    for(var k in o){
        if(new RegExp("("+ k +")").test(fmt)){
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length===1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
        }
    }
    return fmt;
};


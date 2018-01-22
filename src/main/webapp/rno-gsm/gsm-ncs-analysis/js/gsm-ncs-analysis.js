var ncsDates = [];// 日期
var ncsIdToDetail = {};// key为ncsId，value为ncs对象
var ncsDateToTime = {};// key为date
var currentCellNcsData = null;
var selectCell = "";
$(function () {
    // 设置导航标题
    setNavTitle("navTitle");
    //默认右侧栏打开
    var switchHidden = $(".switch_hidden");
    $(".resource_list300_box").css("display", "block");
    switchHidden.hide();
    $(".resource_list_icon").css("right", "300px;");
    $(".switch").click(function () {
        $(this).hide();
        $(".switch_hidden").show();
        $(".resource_list_icon").animate({
            right: "0px"
        }, "fast");
        $(".resource_list300_box").hide("fast");
    });
    switchHidden.click(function () {
        $(this).hide();
        $(".switch").show();
        $(".resource_list_icon").animate({
            right: "300px"
        }, "fast");
        $(".resource_list300_box").show("fast");
    });
    $(".zy_show").click(function () {
        $(".search_box_alert").slideToggle("fast");
    });

    // set highchart
    sethighchart();
    $(".draggable").draggable();
    $("#trigger").css("display", "none");

    laydate.render({elem: '#beginTime', value: new Date(), format:'yyyy/MM/dd'});
    initAreaSelectors({selectors: ["provinceId", "cityId"]});

    // 展开、收缩小区ncs信息的控制
    $("#toggleCellInfoDiv").click(function() {
        if ($("#cellInfoTab_div").css("display") !== 'none') {
            $("#cellInfoTab_div").css("display", "none");
            $("#toggleCellInfoDiv").html("原始数据(点击展开)");
        } else {
            $("#toggleCellInfoDiv").html("原始数据(点击收缩)");
            $("#cellInfoTab_div").css("display", 'block');
        }
    });

    // 数据类型变化
    $("#ncsDataType").change(function() {
        var ncsId = $("#ncsTime").val();
        if (!ncsId || ncsId === '') {
            return;
        }
        // 图表
        displayChart(currentCellNcsData, $("#ncsDataType").val());
    });


    $("#queryBtn").click(function () {
        selectCell = $("#inputCell").val();
        $("#cellNcsForm").ajaxForm({
            url:"../../api/gsm-ncs-analysis/cell-ncs-query",
            type:"post",
            dataType:'text',
            success:function(raw){
                ncsDateToTime = {};
                ncsIdToDetail = {};
                ncsDates.splice(0, ncsDates.length);

                var data = eval("(" + raw + ")");
                if(data.length !== 0) {
                    currentCellNcsData = data;// 更新小区对应的该ncs的信息
                    $("#chartHide").hide();
                    $("#ncsDataType").show();
                    $("#hiddenDiv").show();
                    $("#chartDiv").show();
                    $("#cellInfoTab").show();
                } else {
                    $("#chartHide").show();
                    $("#hiddenDiv").hide();
                    $("#chartDiv").hide();
                    $("#cellInfoTab").hide();
                    $("#ncsDate").html("");
                    $("#ncsTime").html("");
                    showNcsInfo([]);
                    hideOperTips("loadingDataDiv");
                    animateInAndOut("operInfo", 500, 500, 1000, "operTip",
                        "该小区无测量数据！");
                    return;
                }
                var one;
                var ncsId;
                var index, startdate, starthour;
                var times;
                var manufacturers;
                var t;
                for ( var i = 0; i < data.length; i++) {
                    one = data[i];
                    if (!one) {
                        continue;
                    }
                    manufacturers = one['manufacturers'];
                    ncsId = one['ncsId'];
                    var startTime = (new Date(one['meaTime'])).Format("yyyy-MM-dd hh:mm:ss");
                    index = startTime.indexOf(' ');
                    startdate = startTime.substring(0, index);
                    starthour = startTime.substring(index + 1);

                    // 记录
                    ncsIdToDetail[ncsId] = one;
                    //
                    times = ncsDateToTime[startdate];
                    if (!times) {
                        times = [];
                        ncsDateToTime[startdate] = times;

                        ncsDates.push(startdate);
                    }
                    t = {};
                    t['time'] = starthour;
                    t['ncsId'] = ncsId;
                    t['manufacturers'] = manufacturers;
                    times.push(t);
                }

                // 填充日期和时间下来框
                if (ncsDates.length > 0) {
                    var ds = "";
                    for ( var j = 0; j < ncsDates.length; j++) {
                        ds += "<option value='" + ncsDates[j] + "'>" + ncsDates[j]
                            + "</option>";
                    }
                    // alert(ds);
                    $("#ncsDate").html(ds);

                    // 取第一个日期的时间填充时间下来框
                    times = ncsDateToTime[ncsDates[0]];
                    console.log(times);
                    if (times) {
                        var ts = "";
                        for ( j = 0; j < times.length; j++) {
                            ts += "<option value='" + times[j]['ncsId'] +","+ times[j]['manufacturers'] + "'>"
                                + times[j]['time'] + "</option>"
                        }
                        // alert(ts);
                        $("#ncsTime").html(ts);
                        $("#hiddenDiv").show();
                    }

                    // 触发一次获取小区在ncs里的测量信息的事件
                    $("#ncsTime").trigger("change");
                } else {
                    hideOperTips("loadingDataDiv");
                    animateInAndOut("operInfo", 500, 500, 3000, "operTip",
                        "该小区无测量数据！");
                    hideOperTips("loadingDataDiv");
                    return;
                }
                $('#cellInfoTab').css("line-height", "12px");
                var table = $('#cellInfoTab').DataTable( {
                    "data": JSON.parse(raw),
                    "columns": [
                        { "data": null },
                        { "data": "cell" },
                        { "data": "bsic" },
                        { "data": "arfcn" },
                        { "data": "rep" },
                        { "data": "topsix" },
                        { "data": "toptwo" },
                        { "data": "abss" },
                        { "data": "alone" },
                        { "data": "ncell" },
                        { "data": "distance" },
                        { "data": "ncells" },
                        { "data": "defined" }
                    ],
                    "columnDefs": [ {
                        "searchable": false,
                        "orderable": false,
                        "targets": 0
                    } ],
                    "order": [[ 1, 'asc' ]],
                    "lengthChange": false,
                    "ordering": true,
                    "searching": false,
                    "destroy": true,
                    "language": {
                        url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
                    }
                } );
                table.on( 'order.dt search.dt', function () {
                    table.column(0, {search:'applied', order:'applied'}).nodes().each( function (cell, i) {
                        cell.innerHTML = i+1;
                    } );
                } ).draw();
            },
            complete : function() {
                $("#ncsDataType").trigger("change");

            }
        });

    });

    // 日期下来框联动
    $("#ncsDate").change(
        function() {
            // 情况时间下拉框，重新绑定时间
            $("#ncsTime").html("");
            var startdate = $("#ncsDate").val();
            var times = ncsDateToTime[startdate];
            if (times) {
                var ts = "";
                for ( var j = 0; j < times.length; j++) {
                    ts += "<option value='" + times[j]['ncsId'] +","+ times[j]['manufacturers']
                        + "'>" + times[j]['time'] + "</option>"
                }
                // alert(ts);
                $("#ncsTime").html(ts);
                //加一次触发事件，是因为华为ncs数据没有时间，取00:00:00，当改变日期的时候，
                //时间select不变，change事件不触发
                $("#ncsTime").trigger("change");
            }
        });
    // 时间下来框
    $("#ncsTime").change(function() {
        var valStr = $("#ncsTime").val();
        var val = valStr.split(",");
        var ncsId = val[0];
        var manufacturers = val[1];
        $.ajax({
            url : '../../api/gsm-ncs-analysis/ncs-desc-query',
            data : {
                'ncsId' : ncsId,
                'manufacturers' : manufacturers
            },
            type : 'post',
            dataType : 'text',
            success:function(data) {
                if (data === ''){
                    showNcsInfo([]);
                }else {
                    data = eval("(" + data + ")");
                    showNcsInfo(data);
                }

            }
        })
    });
});

/**
 * 显示ncs信息
 *
 * @param ncs
 */
function showNcsInfo(ncs) {
    $("#ncsInfoTab").html("");
    var ht = "<tr><td colspan=2>测量信息</td></tr>";
    ht += "<TR><td class='menuTd'>BSC</td><td>" + getValidValue(ncs['bsc']) + "</td></TR>";
    if (ncs.length === 0){
        ht += "<TR><td class='menuTd'>CREATE_TIME</td><td></td>";
    }else {
        ht += "<TR><td class='menuTd'>CREATE_TIME</td><td>" + getValidValue((new Date(ncs['meaTime'])).Format("yyyy-MM-dd hh:mm:ss"))
            + "</td>";
    }

    ht += "<TR><td class='menuTd'>RECORDCOUNT</td><td>" + getValidValue(ncs['recordCount'])
        + "</td>";
    ht += "<TR><td class='menuTd'>RID</td><td>" + getValidValue(ncs['rid']) + "</td>";
    ht += "<TR><td class='menuTd'>RELSS</td><td>" +getValidValue(ncs['relss']) + "</td>";
    ht += "<TR><td class='menuTd'>RELSS2</td><td>" + getValidValue(ncs['relss2']) + "</td>";
    ht += "<TR><td class='menuTd'>RELSS3</td><td>" + getValidValue(ncs['relss3'])+ "</td>";
    ht += "<TR><td class='menuTd'>RELSS4</td><td>" + getValidValue(ncs['relss4']) + "</td>";
    ht += "<TR><td class='menuTd'>RELSS5</td><td>" + getValidValue(ncs['relss5']) + "</td>";
    ht += "<TR><td class='menuTd'>NCELLTYPE</td><td>" + getValidValue(ncs['ncelltype'])
        + "</td>";
    ht += "<TR><td class='menuTd'>NUMFREQ</td><td>" + getValidValue(ncs['numfreq']) + "</td>";
    ht += "<TR><td class='menuTd'>PERIODLEN</td><td>" + getValidValue(ncs['rectime'])
        + "</td>";
    ht += "<TR><td class='menuTd'>TERMREASON</td><td>" + getValidValue(ncs['termReason'])
        + "</td>";
    ht += "<TR><td class='menuTd'>RECTIME</td><td>" + getValidValue(ncs['rectime']) + "</td>";
    ht += "<TR><td class='menuTd'>ECNOABSS</td><td>" + getValidValue(ncs['ecnoabss'])
        + "</td>";
    ht += "<TR><td class='menuTd'>NUCELLTYPE</td><td>" + getValidValue(ncs['nucelltype'])
        + "</td>";
    ht += "<TR><td class='menuTd'>TFDDMRR</td><td>" + getValidValue(ncs['tfddmrr']) + "</td>";
    ht += "<TR><td class='menuTd'>NUMUMFI</td><td>" + getValidValue(ncs['numumfi']) + "</td>";
    $("#ncsInfoTab").html(ht);
}

/**
 * 显示数据
 *
 * @param data
 * @param type
 *            类型：包括；six,two,abss,alone
 */
function displayChart(data, type) {

    if (!data || data.length === 0) {
        return;
    }
    var cells = [];
    var title = "";
    if (type === 'topsix') {
        title = '六强';
    } else if (type === 'toptwo') {
        title = '两强'
    } else if (type === 'cellRate') {
        title = '强于主小区'
    } else if (type === 'abss') {
        title = 'ABSS';
    } else if (type === 'alone') {
        title = 'ALONE';
    } else {
        return;
    }

    var ratioData = createRatioData(data, type);
    for ( i = 0; i < ratioData.length; i++) {
        cells.push(ratioData[i]['ncell']);
    }
    var ratioRedData=[];
    var ratioYelData=[];
    if (type === 'topsix') {
        title = '六强';
        for (i = 0; i < ratioData.length; i++) {
            var one = ratioData[i];
            if (ratioData[i]['color'] === 'red') {
                ratioRedData.push([i, one['topsix'] * 100]);
            } else {
                ratioYelData.push([i, one['topsix'] * 100]);
            }
        }
    }
    if (type === 'toptwo') {
        title = '两强';
        for (i = 0; i < ratioData.length; i++) {
            one = ratioData[i];
            if (ratioData[i]['color'] === 'red') {
                ratioRedData.push([i, one['toptwo'] * 100]);
            } else {
                ratioYelData.push([i, one['toptwo'] * 100]);
            }
        }
    }
    if (type === 'cellRate') {
        title = '强于主小区';
        for (i = 0; i < ratioData.length; i++) {
            one = ratioData[i];
            if (ratioData[i]['color'] === 'red') {
                ratioRedData.push([i, one['cellRate'] * 100]);
            } else {
                ratioYelData.push([i, one['cellRate'] * 100]);
            }
        }
    }
    if (type === 'abss') {
        title = 'ABSS';
        for (i = 0; i < ratioData.length; i++) {
            one = ratioData[i];
            if (ratioData[i]['color'] === 'red') {
                ratioRedData.push([i, one['abss'] * 100]);
            } else {
                ratioYelData.push([i, one['abss'] * 100]);
            }
        }
    }
    if (type === 'alone') {
        title = 'ALONE';
        for (i = 0; i < ratioData.length; i++) {
            one = ratioData[i];
            if (ratioData[i]['color'] === 'red') {
                ratioRedData.push([i, one['alone'] * 100]);
            } else {
                ratioYelData.push([i, one['alone'] * 100]);
            }
        }
    }

    /*console.log(ratioRedData);
    console.log(ratioYelData);*/
    // 比例
    var chart = $('#chartDiv').highcharts();
    var disData = [];
    for ( var i = 0; i < ratioData.length; i++) {
        disData.push(ratioData[i]['dis']);
    }
    /*console.log(disData);*/
    if (chart) {
        chart.destroy();
    }
    var options = {
        chart : {
            zoomType : 'xy'
        },
        title : {
            text : '[' + selectCell + ']' + title + '比例',
            align:'left',
            x:500,
            y:10
        },
        xAxis : [ {
            id : 'ncellAxis',
            gridLineWidth : 1,
            categories : cells,
            labels : {
                rotation : 90
            }
        } ],
        yAxis : [ { // Primary yAxis
            id : 'ratioAxis',
            labels : {
                style : {
                    color : '#89A54E'
                }
            },
            title : {
                text : title + '比例（%）'
            }

        }, { // Tertiary yAxis
            id : 'disAxis',
            gridLineWidth : 1,
            title : {
                text : '距离(km)',
                style : {
                    color : '#AA4643'
                }
            },
            labels : {
                style : {
                    color : '#AA4643'
                }
            },
            opposite : true
        } ],
        tooltip : {
            shared : false,
            formatter : function() {
                return this.x + "---" + this.y;
            }
        },
        legend : {
            layout : 'horizontal',
            align : 'left',
            x : 10,//20
            verticalAlign : 'top',
            y : -5,
            floating : true,
            backgroundColor : '#FFFFFF'
        },
        series : [{
            id : 'ratioSeries',
            name : '红色：已定义邻区',
            color : '#FF0000',
            type : 'column',
            yAxis : 'ratioAxis',
            data : ratioRedData,
            tooltip : {
                // // valueSuffix : ' %',
                // formatter : function() {
                // return this.y + "bili";
                // }
            }
        },
            {
                id : 'ratioSeries',
                name : '黄色：未定义邻区',
                color : '#FFFF00',
                type : 'column',
                yAxis : 'ratioAxis',
                data : ratioYelData,
                tooltip : {

                }

            }
            , {
                id : 'disSeries',
                name : '与目标小区的距离',
                color : '#89A54E',
                type : 'scatter',
                yAxis : 'disAxis',
                data : disData,
                tooltip : {
                    valueSuffix : ' km'
                }
            }
            ]
        //
    };
    $('#chartDiv').highcharts(options);
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

function showOperTips(outerId, tipId, tips) {
    try {
        var outerIdDiv = $("#" + outerId);
        outerIdDiv.css("display", "");
        outerIdDiv.find("#" + tipId).html(tips);
    } catch (err) {
    }
}

function hideOperTips(outerId) {
    try {
        $("#" + outerId).css("display", "none");
    } catch (err) {
    }
}


/**
 * 创建柱状图的data
 *
 * @param cellNcsArr
 * @param code
 */
function createRatioData(cellNcsArr, code) {

    var data = [];
    if (!cellNcsArr) {
        return data;
    }
    var one = null;
    var isnei = 0;
    var field = "";
    if (code === 'topsix') {
        field = 'TOPSIX'
    } else if (code === 'toptwo') {
        field = 'TOPTWO';
    } else if (code === 'cellRate') {
        field = 'CELLRATE';
    } else if (code === 'abss') {
        field = 'ABSSRATE';
    } else if (code === 'alone') {
        field = 'ALONERATE';
    } else {
        return data;
    }

    for ( i = 0; i < cellNcsArr.length; i++) {
        one = {};
        isnei = cellNcsArr[i]['defined'];

        var aa=cellNcsArr[i];
        if (isnei === 0) {
            one['color'] = 'yellow';
        } else {
            one['color'] = 'red';
        }
        one['y'] = cellNcsArr[i][field];
        one['x']=i;//cellNcsArr记录x下标

        //peng.jm 2015-1-26 加入 start
        one['topsix'] = cellNcsArr[i]['topsix']; //记录topsix
        one['toptwo'] = cellNcsArr[i]['toptwo']; //记录toptwo
        one['abss'] = cellNcsArr[i]['abss']; //记录abss
        one['alone'] = cellNcsArr[i]['alone']; //记录alone
        one['cellRate'] = cellNcsArr[i]['cellRate']; //记录cellRate
        one['ncell'] = cellNcsArr[i]['ncell']; //记录ncell
        cellNcsArr[i]['distance'] === "10000000000" ? "": cellNcsArr[i]['distance'];
        one['dis'] = cellNcsArr[i]['distance']; // 记录距离
        //peng.jm 2015-1-26 加入end

//		console.log("one:"+one['color']);
        data.push(one);
    }


    //peng.jm 2015-1-26 加入 start
    //对各项指标值进行排序
    var temp;
    var x;
    for ( var i = 0; i < data.length; i++) {
        for ( var j = i; j < data.length; j++) {
            if(data[j]['y'] >= data[i]['y']) {
                temp = data[i];
                data[i] = data[j];
                data[j] = temp;

                x = data[i]['x'];
                data[i]['x'] = data[j]['x'];
                data[j]['x'] = x;
            }
        }
    }
    //peng.jm 2015-1-26 加入 end

    //peng.jm 2015-3-18 加入 start

    var isNeiArray = []; //定义邻区
    var notNeiArray = []; //未定义邻区
    for ( i = 0; i < data.length; i++) {
        if(data[i]['color'] === 'red') {
            isNeiArray.push(data[i]);
        } else {
            notNeiArray.push(data[i]);
        }
    }
	/*console.log(isNeiArray);
	console.log(notNeiArray);*/

    var result = [];

    if(isNeiArray.length === 0) {
        for ( i = 0; i < notNeiArray.length; i++) {
            if(i>=60) break;
            result.push(notNeiArray[i]);
        }
    }
    else if(isNeiArray.length>0 && isNeiArray.length<60) {
        for ( i = 0; i < isNeiArray.length; i++) {
            result.push(isNeiArray[i]);
        }
        for ( i = result.length; i < notNeiArray.length; i++) {
            if(i>=60) break;
            result.push(notNeiArray[i]);
        }
    }
    else if(isNeiArray.length>60) {
        for ( i = 0; i < isNeiArray.length; i++) {
            if(i>=60) break;
            result.push(isNeiArray[i]);
        }
    }

    //初始化60条数据的x值
    for ( i = 0; i < result.length; i++) {
        result[i]['x'] = i;
    }
    //将整理好的60条数据再进行排序，保证从大到小
    for ( i = 0; i < result.length; i++) {
        for ( j = i; j < result.length; j++) {
            if(result[j]['y'] >= result[i]['y']) {
                temp = result[i];
                result[i] = result[j];
                result[j] = temp;

                x = result[i]['x'];
                result[i]['x'] = result[j]['x'];
                result[j]['x'] = x;
            }
        }
    }
    return result;
}

/**
 * 设置highchart
 */
function sethighchart() {

    Highcharts.setOptions({
        chart : {
            backgroundColor : {
                linearGradient : [ 0, 0, 500, 500 ],
                stops : [ [ 0, 'rgb(255, 255, 255)' ],
                    [ 1, 'rgb(240, 240, 255)' ] ]
            },
            borderWidth : 2,
            plotBackgroundColor : 'rgba(255, 255, 255, .9)',
            plotShadow : true,
            plotBorderWidth : 1
        }
    });

    // 空图表
    var options = {
        chart : {
            zoomType : 'xy'
        },
        title : {
            text : '六强比例'
        },
        xAxis : [ {
            id : 'ncellAxis',
            gridLineWidth : 1,
            categories : [ '邻区' ],
            labels : {
                rotation : 90
            }
        } ],
        yAxis : [ { // Primary yAxis
            id : 'ratioAxis',
            labels : {
                style : {
                    color : '#89A54E'
                }
            },
            title : {
                text : '六强比例（%）'
            }
        }, { // Tertiary yAxis
            id : 'disAxis',
            gridLineWidth : 1,
            title : {
                text : '距离',
                style : {
                    color : '#AA4643'
                }
            },
            labels : {
                style : {
                    color : '#AA4643'
                }
            },
            opposite : true
        } ],
        tooltip : {
            shared : false,
            formatter : function() {
                return this.x + "---" + this.y;
            }
        },
        legend : {
            layout : 'horizontal',
            align : 'left',
            x : 20,
            verticalAlign : 'top',
            y : -5,
            floating : true,
            backgroundColor : '#FFFFFF'
        }

        //
    };
    chart = $('#chartDiv').highcharts(options);

}
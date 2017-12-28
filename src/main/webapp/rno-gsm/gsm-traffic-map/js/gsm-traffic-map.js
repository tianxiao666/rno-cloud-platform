var map;
var onLoadingGisCell=false;
var isRuleChanged=false;
var rendererFactory = null;

$(function () {
    rendererFactory=new RendererFactory(false);// 缓存的渲染规则
    $(".draggable").draggable();
    $("#trigger").css("display", "none");

    $(".switch").click(function () {
        $(this).hide();
        $(".switch_hidden").show();
        $(".resource_list_icon").animate({
            right: '0px'
        }, 'fast');
        $(".resource_list300_box").hide("fast");
    });
    $(".switch_hidden").click(function () {
        $(this).hide();
        $(".switch").show();
        $(".resource_list_icon").animate({
            right: '380px'
        }, 'fast');
        $(".resource_list300_box").show("fast");
    });
    $(".zy_show").click(function () {
        $(".search_box_alert").slideToggle("fast");
    });

    $("#provinceId").change(function () {
        var provinceId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, provinceId, "cityId",false);
            $("#cityId").change();
        })
    });

    $("#cityId").change(function () {
        var cityId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, cityId, "queryCellAreaId",true);
            $("#queryCellAreaId").change();
        })
    });

    var baseLayer = new ol.layer.Tile({
        source: new ol.source.XYZ({
            url: 'http://rno-omt.hgicreate.com/styles/rno-omt/{z}/{x}/{y}.png'
        })
    });

    $("#queryCellAreaId").change(function () {
        var lon = parseFloat($(this).find("option:checked").attr("data-lon"));
        var lat = parseFloat($(this).find("option:checked").attr("data-lat"));
        if (map === undefined) {
            map = new ol.Map({
                target: 'map',
                layers: [baseLayer],
                view: new ol.View({
                    center: ol.proj.fromLonLat([lon, lat]),
                    zoom: 16
                })
            });
        } else {
            map.getView().setCenter([lon, lat]);
        }
    });

    //初始化区域
    $.ajax({
        url: "../../data/area.json",
        dataType: "json",
        async: false,
        success: function (data) {
            renderArea(data, 0, "provinceId");
            $("#provinceId").change();
        }
    });
    loadAndShowAnalysisList();
});

/**
 * 获取和展现被加载的分析列表
 */
function loadAndShowAnalysisList() {
    var data = "1,2";
    $("#analysisListTable").empty();
    $
        .ajax({
            url : '/portal/api/gsm-traffic-statics/get-cell-performance-quota-list',
            data: {
                'data':"1,2"
            },
            dataType : 'json',
            type : 'post',
            success : function(data) {
                console.log(data)
                if (!data) {
                    return;
                }
                // 在面板显示
                var htmlstr = "";
                var one;
                var trClass = "";
                for ( var i = 0; i < data.length; i++) {
                    one = data[i]['stsAnaItemDetail'];
                    console.log(one)
                    if (i % 2 == 0) {
                        trClass = "tb-tr-bg-coldwhite";
                    } else {
                        trClass = "tb-tr-bg-warmwhite";
                    }
                    if (one) {
                        htmlstr += "<tr class=\""
                            + trClass
                            + "\">"// table 内容 yuan.yw 修改 2013-10-28
                            + "  <td width=\"45%\" class=\"bd-right-white\" >    "
                            + "  <span >"
                            + one['areaName']
                            + "</span>"
                            + "  </td>"
                            + "  <td  width=\"20%\"  class=\"bd-right-white td_nowrap\">"
                            + "  <span >"
                            + getValidValue(one['stsType'],"未知")
                            + "</span>"
                            + "  </td>"
                            + "  <td  width=\"20%\"  class=\"td-standard-date bd-right-white td_nowrap\">"
                            + "  <span >"
                            + getValidValue(one['stsDate'],"未知")
                            + "</span>"
                            + "  </td>"
                            + "  <td width=\"20%\" class=\"bd-right-white td_nowrap\">"
                            + "  <span>"
                            + getValidValue(one['periodType'],"不规则指标")
                            + "</span>"
                            + "  </td>"
                            + "  <td width=\"5%\" class=\"bd-right-white\">"
                            + "  <input type=\"checkbox\" class=\"forselect\" name=\"checkbox\" id='"
                            + data[i]['configId']
                            + "' />"
                            + "  <label for=\"checkbox\"></label>"
                            + "  </td>"
                            + "  <td width=\"10%\">"
                            + "  <input type=\"button\" class=\"removebtn\"  value=\"移除\" /><input type=\"hidden\" class=\"hiddenconfigid\" value=\""
                            + data[i]['configId']
                            + "\" />"
                            + "  </td >                                                                                                                     "
                            + "  </tr>";
                    }
                }
                $("#analysisListTable").append(htmlstr);
                if (htmlstr != "") {
                    $("#analysisBtnDiv").show();
                }

            }
        });
}
function getValidValue(v, defaultValue, precision) {
    if (v == null || v == undefined || v == "null" || v == "NULL"
        || v == "undefined" || v == "UNDEFINED") {
        if (defaultValue != null && defaultValue != undefined)
            return defaultValue;
        else
            return "";
    }
    if (typeof v == "number") {
        try {
            v = new Number(v).toFixed(precision);
        } catch (err) {
            console.error("v=" + v + "," + err);
        }
    }
    return v;
}

function showTips(tip) {
    $(".loading_cover").css("display", "block");
    $(".loading_fb").html(tip);
}

function hideTips(tip) {
    $(".loading_cover").css("display", "none");
}
/**
 * 统计功能
 *
 * @param type
 *            指明统计类型：无线资源利用率，
 * @param action
 *            提交到后台的action名称
 * @param name
 *            如果name不为空，说明要获取type类型的name指定的范围的统计值。如获取type=无线资源利用率，name=超忙小区
 */
function commonstatics(type, action, name) {
    showTips("正在加载资源无线利用率")
    loadStaticsInfo(action, type, name, 0, function() {
        hideTips();
    });
}


/**
 * 从服务器加载指定指标的统计情况
 *
 * @param action
 * @param type
 *            指标类型
 * @param name
 *            指标类型下的某类型小区
 * @param startIndex
 *            起始下标
 */
function loadStaticsInfo(action, type, name, startIndex, callback) {
    if (!type) {
        return;
    }
    var selectedList="";
    $('input[name="checkbox"]:checked').each(function () {
        selectedList+=($(this).attr('id'))+",";
    });
    if(selectedList.length<=0){
        animateInAndOut("operInfo", 500, 500, 1000, "operTip", "请先选择一个小区指标");
        return;
    }
    selectedList = selectedList.substring(0,selectedList.length-1);
    $.ajax({
        url : "/portal/api/gsm-traffic-statics/"+action,
        data : {
            'stsCode' : type,
            'startIndex' : startIndex,
            'selectedList' : selectedList
        },
        dataType : 'json',
        type : 'post',
        async : false,
        success : function(data) {
            console.log(data)
            if (!data) {
                return;
            }
            try {
                var stsresult = data['rnoStsResults'];
                startIndex = data['startIndex'];
                var hasMore = data['hasMore'];
                if (stsresult) {
                    //组装数据
                    var keyStr = "";
                    var avgValueStr = "";
                    var maxValueStr = "";
                    var minValueStr = "";
                    $.each(stsresult, function (key, value) {
                        keyStr += ",'" + value['CELL'] + "'";
                        avgValueStr += "," + value['AVGVALUE'];
                        maxValueStr += "," + value['MAXVALUE'];
                        minValueStr += "," + value['MINVALUE'];
                    })
                    avgValueStr = avgValueStr.substring(1, avgValueStr.length);
                    maxValueStr = maxValueStr.substring(1, maxValueStr.length);
                    minValueStr = minValueStr.substring(1, minValueStr.length);
                    keyStr = keyStr.substring(1, keyStr.length);
                    var categories = "[" + keyStr + "]";
                    var series = "[{name: 'AVG',type: 'bar',data: [" + avgValueStr + "]}, {name: 'MAX',type: 'bar',data: [" + maxValueStr + "]},{name: 'MIN',type: 'bar',data: [" + minValueStr + "]}]";
                    categories = eval(categories);
                    series = eval(series);

                    $("#radioResourceUseRateDialog").show();
                        // var option = createOptionFromStsResult(stsresult[i]);
                        var myChart = echarts.init(document.getElementById('radioResourceUseRateEChart'));
                        myChart.showLoading({
                            text: "图表数据正在努力加载..."
                        });
                        var option = {
                            legend: {
                                data: ['AVG', 'MAX', 'MIN']
                            },
                            tooltip: {
                                trigger: 'axis',
                                axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                                    type: 'line'        // 默认为直线，可选为：'line' | 'shadow'
                                }
                            },
                            grid: {
                                left: '3%',
                                right: '4%',
                                bottom: '3%',
                                containLabel: true
                            },
                            xAxis: [
                                {
                                    type: 'category',
                                    data: categories,
                                    axisTick: {
                                        alignWithLabel: true
                                    }
                                }
                            ],
                            yAxis: [
                                {
                                    type: 'value'
                                }
                            ],
                            series: series
                        };
                        myChart.hideLoading();
                        myChart.setOption(option);
                }
                if (hasMore === true) {
                    loadStaticsInfo(action, type, name, startIndex, callback);
                } else {
                    callback();
                }
            } catch (err) {
                console.error(err);
                hideTips("");
            }
        },
        error:function(err){
            hideTips("");
            console.log(err)
        },
        complete:function(){
            hideTips("");
        }
    });
}


// 渲染区域
function renderArea(data, parentId, areaMenu, boolLonLat) {
    var arr = data.filter(function (v) {
        return v.parentId === parentId;
    });
    if (arr.length > 0) {
        var areaHtml = [];
        $.each(arr, function (index) {
            var area = arr[index];
            if (boolLonLat) {
                areaHtml.push("<option value='"+area.id+"' data-lon='"+area.longitude+"' data-lat='"+area.latitude+"'>");
            } else {
                areaHtml.push("<option value='"+area.id+"'>");
            }

            areaHtml.push(area.name+"</option>");
        });
        $("#" + areaMenu).html(areaHtml.join(""));
    } else {
        console.log("父ID为" + parentId + "时未找到任何下级区域。");
    }
}

/**
 * 使一个元素渐渐展现然后又渐渐隐去
 *
 * @param objId
 * @param timeIn
 * @param timeOut
 * @param stayTime
 *
 */
function animateInAndOut(objId, timeIn, timeOut, stayTime, tipId, tips) {
    if (objId == null || objId == undefined) {
        return;
    }
    if (tipId && tips) {
        try {
            $("#" + tipId).html(tips);
        } catch (err) {
        }
    }
    try {
        if (typeof timeIn == "number" && typeof timeOut == "number") {
            $("#" + objId).fadeIn(timeIn, function () {
                window.setTimeout(function () {
                    $("#" + objId).fadeOut(timeOut);
                }, stayTime);
            });
        }
    } catch (err) {

    }
}


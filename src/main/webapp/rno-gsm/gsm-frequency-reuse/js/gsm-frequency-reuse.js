var map, tiled;
var currentSelConfigId = "";// 当前选择的用于分析的分析列表配置id
var markCellOverlay;
let queryCellOverlay;
let bcch;
let tchList = new Array();
var currentFeature;
$(function () {
    $(".draggable").draggable();
    $("#report_Dialog").draggable();
    $("#analyzeedit_Dialog").draggable();

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
            right: '381px'
        }, 'fast');
        $(".resource_list300_box").show("fast");
    });
    $(".zy_show").click(function () {
        $(".search_box_alert").slideToggle("fast");
    });

    var baseLayer = new ol.layer.Tile({
        source: new ol.source.XYZ({
            url: 'http://rno-omt.hgicreate.com/styles/rno-omt/{z}/{x}/{y}.png'
        })
    });
    let redStyle = new ol.style.Style({
        stroke: new ol.style.Stroke({
            color: 'yellow',
            size: 5
        }),
        fill: new ol.style.Fill({
            color: 'rgba(255, 0, 0, 1.0)'
        })
    });
    var getText = function (feature) {
        var text = feature.values_.CELL_NAME;
        return text;
    };
    var createTextStyle = function (feature) {
        return new ol.style.Text({
            // textAlign: align == '' ? undefined : align,
            // textBaseline: baseline,
            font: 'bold' + ' 16px' +' 宋体',
            text: getText(feature),
            fill: new ol.style.Fill({color: '#000'}),
            stroke: new ol.style.Stroke({color: '#cccccc', width: 1}),
            offsetX: 0,
            offsetY: 0,
            placement: 'line',
            maxAngle: '0.7853981633974483',
            overflow: 'true',
            rotation: "0"
        })
    }

    function greenBlueStyle(feature, resolution) {
        return new ol.style.Style({
            stroke: new ol.style.Stroke({
                color: 'yellow',
                size: 5
            }),
            fill: new ol.style.Fill({
                color: 'rgba(0, 255, 255, 1.0)'
            }),
            text: createTextStyle(feature)
        })
    }

    markCellOverlay = new ol.layer.Vector({
        zIndex: 7,
        source: new ol.source.Vector(),
        style: greenBlueStyle
    });
    queryCellOverlay = new ol.layer.Vector({
        zIndex: 6,
        source: new ol.source.Vector(),
        style: redStyle
    });
    $("#queryCellAreaId").change(function () {
        var lon = parseFloat($(this).find("option:checked").attr("data-lon"));
        var lat = parseFloat($(this).find("option:checked").attr("data-lat"));
        if (map === undefined) {
            map = new ol.Map({
                target: 'map',
                layers: [baseLayer, markCellOverlay, queryCellOverlay],
                view: new ol.View({
                    projection: 'EPSG:4326',
                    center: ol.proj.fromLonLat([lon, lat]),
                    zoom: 16
                })
            });
        } else {
            map.getView().setCenter([lon, lat]);
        }
    });

    $("#cityId").change(function () {
        var cityId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, cityId, "queryCellAreaId", true);
            $("#queryCellAreaId").trigger("change");
        })
    });

    $("#provinceId").change(function () {
        var provinceId = parseInt($(this).find("option:checked").val());
        var cityId = $("#cityId").val();
        loadAndShowCellConfigAnalysisList();//小区配置
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, provinceId, "cityId", false);
            $("#cityId").trigger("change");
        })
    });

    //初始化区域
    $.ajax({
        url: "../../data/area.json",
        dataType: "json",
        async: false,
        success: function (data) {
            renderArea(data, 0, "provinceId");
            renderArea(data, 440000, "cityId");
            $("#provinceId").change();
            $("#cityId").change();
        }
    });

    $("#queryGsmCell").click(function () {
        var cityId = parseInt($("#cityId").find("option:checked").val());
        map.removeLayer(tiled);
        tiled = new ol.layer.Tile({
            zIndex: 3,
            source: new ol.source.TileWMS({
                url: 'http://rno-gis.hgicreate.com/geoserver/rnoprod/wms',
                params: {
                    'FORMAT': 'image/png',
                    'VERSION': '1.1.1',
                    tiled: true,
                    STYLES: '',
                    LAYERS: 'rnoprod:RNO_GSM_CELL_GEOM',
                    CQL_FILTER: "AREA_ID=" + cityId
                }
            }),
            opacity: 0.5
        });
        map.addLayer(tiled);
    });

    $("#showCellBtn").click(function () {
        //确定加载某个配置的小区到地图
        var btsType = $("input[name='btsType']:checked").val();
        if (!btsType) {
            animateInAndOut("operInfo", 500, 500, 1000, "operTip", "请先选择一个小区配置");
            return;
        }
        currentSelConfigId = btsType;//currentSelConfigIds
        currentloadtoken = getLoadToken();
        loadGisCellData(currentloadtoken, btsType);
    });

    // 从小区干扰加载列表中，选择若干列表进行分析
    $("#confirmSelectionAnalysisBtn").click(function () {
        return;
    });

});

//获取和展现被加载的小区配置分析列表
function loadAndShowCellConfigAnalysisList() {
    $("#analysisListTable_cellconfig").empty();
    $.ajax({
        url: "/api/gsm-frequency-reuse-analysis/cell-config-analysis-list",
        dataType: 'json',
        type: 'GET',
        success: function (data) {
            var htmlstr = "";
            htmlstr = getCellConfigAnaliysisHtml(data);
            $("#analysisListTable_cellconfig").append(htmlstr);
            if (htmlstr != "") {
                $("#showCellBtnDiv").show();
            }
        }
    });
}

/**
 * 获取小区配置html
 * @param {} data
 */
function getCellConfigAnaliysisHtml(data) {
    if (!data) {
        return;
    }
    var htmlstr = "";
    var trClass = "";
    for (var i = 0; i < data.length; i++) {
        if (i % 2 == 0) {
            trClass = "tb-tr-bg-coldwhite";
        } else {
            trClass = "tb-tr-bg-warmwhite";
        }
        var tempType = "";
        htmlstr += "<tr class=\"" + trClass + "\">"//table 内容
            + "  <td width=\"45%\" class=\"bd-right-white\" >    "
            + "  <span >"
            + data[i]['title']
            + "</span>"
            + "  </td>"
            + "  <td width=\"45%\" class=\"bd-right-white\" >    "
            + "  <span >"
            + data[i]['name']
            + "</span>"
            + "  </td>"
            + "  <td  width=\"20%\"  class=\"td-standard-date bd-right-white td_nowrap\">"
            + "  <span >"
            + data[i]['collectTime']
            + "</span>"
            + "  </td>"
            + "  <td width=\"5%\" class=\"bd-right-white\">"
            + "  <input type=\"hidden\"  name=\"isTemp\" value='"
            + data[i]['isTemp']
            + "' />"
            + "  <input type=\"hidden\"  name=\"type\" value='"
            + data[i]['type']
            + "' />"
            + "  <input type=\"hidden\" class=\"forselect\" name=\"cellconfigradio\" value='"
            + data[i]['configId']
            + "' />"
            + "  <input type=\"radio\" class=\"forselect\" style='margin-top: 20px' name=\"btsType\" value='"
            + data[i]['btsType']
            + "' />"
            + "  <label for=\"checkbox\"></label>"
            + "  </td>"
            + "  <td width=\"10%\">"
            + "  <input type=\"hidden\" class=\"hiddenconfigid\" value=\""+ data[i]['configId'] + "\" />"
            + "  </td >"
            + "  </tr>";
    }
    return htmlstr;
}

/**
 * 根据所选择的分析列表加载小区数据
 */
function loadGisCellData(loadToken, btsType) {
    $("#loadingMapCoverDiv").show();
    queryCellOverlay.getSource().clear();
    let cityId = $("#cityId").val();
    let filter = `BTS_TYPE = '` + btsType + `' and AREA_ID = '` + cityId + `'`;
    $.ajax({
        url: "http://rno-gis.hgicreate.com/geoserver/rnoprod/ows",
        data: {
            service: 'WFS',
            request: 'GetFeature',
            typeName: 'rnoprod:RNO_GSM_CELL_GEOM',
            outputFormat: 'application/json',
            'CQL_FILTER': filter
        }
    }).then(function (response) {
        let features = new ol.format.GeoJSON().readFeatures(response);
        if (features.length) {
            currentFeature = features;
            queryCellOverlay.getSource().addFeatures(features);
            map.getView().animate({
                // center: [features[0].get('LONGITUDE'), features[0].get('LATITUDE')],
                duration: 1000,
                zoom: 14
            });
            bcch = features[0].get("BCCH");
            var tch = features[0].get("TCH");
            var tchLists = tch.split(",");
            tchLists.forEach(function (one) {
                tchList.push(parseInt(one))
            })
            tchList.sort(function (a, b) {
                return a - b
            });
            $("#reportPageSize").val(20);
            $("#reportCurrentPage").val(0);
            $("#reportPageCount").val(parseInt(tchList.length / $("#reportPageSize").val()) + 1);
            $("#reportTotalCount").val(tchList.length);
            $("#loadingMapCoverDiv").hide();
        } else {
            animateInAndOut("operInfo", 1000, 1000, 2000, "operTip", "不存在该空间数据");
        }
    })
}

function reportFreqReuse() {
    $("#reportCurrentPage").val(0);
    getReportFreqReuseData();
}

/**
 * 获取频点统计数据并生成统计图
 */
function getReportFreqReuseData() {
    $("#reportCoverDiv").show();
    $("#report_Dialog").show();
    $("#analyzeedit_Dialog").hide();
    var btsType = $("input[name='btsType']:checked").val();
    currentSelConfigId = btsType;
    if (currentSelConfigId == "" || currentSelConfigId == undefined) {
        animateInAndOut("operInfo", 500, 500, 1000, "operTip", "请先选择一个小区配置再进行分析");
        return false;
    }
    if($("#reportCurrentPage").val()==0){
        $("#reportCurrentPage").val(1);
    }
    var currentPage = $("#reportCurrentPage").val();
    var pageSize = $("#reportPageSize").val();
    var areaId = $("#cityId").val();

    $("#reportId").val(currentSelConfigId);
    $("#reportType").val("CELLDATA");
    $("#reportForm")
        .ajaxSubmit({
            url: '/api/gsm-frequency-reuse-analysis/statistics-frequency-reuse-info',
            dataType: 'json',
            data: {
                'btsType': 'GSM900',
                'currentPage': currentPage,
                'pageSize': pageSize,
                'areaId': areaId
            },//上次加载的数据
            async: false,
            success: function (data) {
                var page = data.page;
                if (page) {
                    var pageSize = page.pageSize ? page.pageSize : 0;
                    $("#reportPageSize").val(pageSize);

                    var currentPage = page.currentPage && pageSize != 0 ? page.currentPage : 0;
                    $("#reportCurrentPage").val(currentPage);

                    var totalPageCnt = page.totalPageCnt ? page.totalPageCnt : 0;
                    $("#reportPageCount").val(totalPageCnt);

                    var totalCnt = page['totalCnt'] ? page['totalCnt'] : 0;
                    $("#reportTotalCount").val(totalCnt);

                    // 跳转
                    $("#emTotalCnt").html(totalCnt);
                    if(pageSize==1000){
                        $("#emEachPageTotalCnt").html(totalCnt);
                    }else{
                        $("#emEachPageTotalCnt").html(20);
                    }
                    $("#showCurrentPage").val(currentPage);
                    $("#emTotalPageCnt").html(totalPageCnt);
                }
                createReportChart(data.freqReuseInfos);//生成频点统计图
            },
            error: function (xmh, textstatus, e) {
                //alert("出错啦！" + textstatus);
            },
            complete: function () {
                // $("#reportCoverDiv").css("display", "none");
            }
        });
    return true;
}

/**
 * 生成频点统计图
 */
function createReportChart(data) {
    if (!data) {
        return;
    }
    //组装数据
    var keyStr = "";
    var tchStr = "";
    var bcchStr = "";
    $.each(data, function (key, value) {
        keyStr += ",'" + key + "'";
        tchStr += "," + value['tchCount'];
        bcchStr += "," + value['bcchCount'];
    })
    tchStr = tchStr.substring(1, tchStr.length);
    bcchStr = bcchStr.substring(1, bcchStr.length);
    keyStr = keyStr.substring(1, keyStr.length);
    var categories = "[" + keyStr + "]";
    var series = "[{name: 'TCH',type: 'bar',stack:'tchbcch',data: [" + tchStr + "]}, {name: 'BCCH',type: 'bar',stack:'tchbcch',data: [" + bcchStr + "]}]";
    categories = eval(categories);
    series = eval(series);

    var myChart = echarts.init(document.getElementById('reportDiv'));
    myChart.showLoading({
        text: "图表数据正在努力加载..."
    });
    var option = {
        legend: {
            data: ['TCH', 'BCCH']
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
    $("#reportCoverDiv").hide();
}

// 分页统计
function reportFreqByPage(dir) {
    var pageSize = new Number($("#reportPageSize").val());
    var currentPage = new Number($("#reportCurrentPage").val());
    var totalPageCnt = new Number($("#reportPageCount").val());
    var totalCnt = new Number($("#reportTotalCount").val());

    if (dir == "first") {
        if (currentPage < 0 && currentPage == 1) {
            return;
        } else {
            $("#reportCurrentPage").val("1");
        }
    } else if (dir == "last") {
        if (currentPage >= totalPageCnt) {
            return;
        } else {
            $("#reportCurrentPage").val(totalPageCnt);
        }
    } else if (dir == "back") {
        if (currentPage <= 1) {
            return;
        } else {
            $("#reportCurrentPage").val(currentPage - 1);
        }
    } else if (dir == "next") {
        if (currentPage >= totalPageCnt) {
            return;
        } else {
            $("#reportCurrentPage").val(currentPage + 1);
        }
    } else if (dir == "num") {
        var userinput = $("#showCurrentPage").val();
        if (isNaN(userinput)) {
            alert("请输入数字！")
            return;
        }
        if (userinput > totalPageCnt || userinput < 0) {
            alert("输入页面范围不在范围内！");
            return;
        }
        $("#reportCurrentPage").val(userinput);
    } else if (dir == "all") {
        $("#reportCurrentPage").val(0);
        $("#reportPageSize").val(1000);
    } else if (dir == "split") {
        $("#reportCurrentPage").val(0);
        $("#reportPageSize").val(20);
    } else {
        return;
    }
    //获取统计数据
    getReportFreqReuseData();
}

/**
 * 频率分布标记 div弹出
 */
function analyzeFreqReuse() {
    $("#report_Dialog").hide();
    var btsType = $("input[name='btsType']:checked").val();
    currentSelConfigId = btsType;//currentSelConfigIds
    if (currentSelConfigId == "" || currentSelConfigId == undefined) {
        animateInAndOut("operInfo", 500, 500, 1000, "operTip", "请先选择一个小区配置再进行分析");
        return false;
    }
    $("#analyzeedit_Dialog").show();
}

/**
 * 验证数字
 * @param {} node
 */
function checkNumber(node) {
    var number = node.value;
    number = number.replace(/[^1234567890]/g, '');
    node.value = number;
}

/**
 * 频点复用地图标记
 */
function markCellForFreqReuse() {

    var freq_value = $("input[name='freq_value']").val();
    if (freq_value == "") {
        animateInAndOut("operInfo", 1000, 1000, 1000, "operTip", "请输入频点值！");
        $("input[name='freq_value']").focus();
        return false;
    }
    var cityId = $("#cityId").val();
    markCellOverlay.getSource().clear();
    // var ltt = parseFloat(currentFeature[0].values_.LATITUDE);
    // var lgt = parseFloat(currentFeature[0].values_.LONGITUDE);
    let filter = `BCCH = '` + freq_value
        // + `' and LONGITUDE > '` + (lgt - 0.05)
        // + `' and LONGITUDE < '` + (lgt + 0.05)
        // + `' and LATITUDE > '` + (ltt - 0.05)
        // + `' and LATITUDE < '` + (ltt + 0.05)
        + `' and AREA_ID = '` + cityId + `'`;
    $.ajax({
        url: "http://rno-gis.hgicreate.com/geoserver/rnoprod/ows",
        data: {
            service: 'WFS',
            request: 'GetFeature',
            typeName: 'rnoprod:RNO_GSM_CELL_GEOM',
            outputFormat: 'application/json',
            'CQL_FILTER': filter
        }
    }).then(function (response) {
        let features = new ol.format.GeoJSON().readFeatures(response);
        if (features.length) {
            features.forEach(function (ft) {
                markCellOverlay.getSource().addFeatures(features);
            })
            map.getView().animate({
                // center: [features[0].get('LONGITUDE'), features[0].get('LATITUDE')],
                duration: 1000,
                zoom: 16
            });
        } else {
            animateInAndOut("operInfo", 1000, 1000, 2000, "operTip", "不存在该空间数据");
        }
    })
}

function clearMarkCellForFreqReuse() {
    markCellOverlay.getSource().clear();
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
                areaHtml.push("<option value='" + area.id + "' data-lon='" + area.longitude + "' data-lat='" + area.latitude + "'>");
            } else {
                areaHtml.push("<option value='" + area.id + "'>");
            }

            areaHtml.push(area.name + "</option>");
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


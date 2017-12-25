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
        zIndex: 5,
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
            // map.
            map.on('singleclick', function (evt) {
                if (map.getView().getZoom() < 15) {
                    return;
                }
                let view = map.getView();
                if (tiled) {
                    let url = tiled.getSource().getGetFeatureInfoUrl(
                        evt.coordinate, view.getResolution(), view.getProjection(), {
                            'INFO_FORMAT': 'application/json',
                            'FEATURE_COUNT': 1000
                        });
                    if (url) {
                        $.ajax(url).then(function (response) {
                            let features = new ol.format.GeoJSON().readFeatures(response);
                            if (features.length > 0) {
                                console.log(features)
                            }
                        })
                    }
                }
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
        console.log("" + cityId);
        loadAndShowCellConfigAnalysisList();//小区配置
        loadAndShowCellInteferenceAnalysisList(); //小区干扰
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
        var id = $("input[name='cellconfigradio']:checked").val();
        console.log(id);
        if (!id) {
            animateInAndOut("operInfo", 500, 500, 1000, "operTip", "请先选择一个小区配置");
            return;
        }
        // clearAllData();
        // initPageParam();//恢复分页参数
        currentSelConfigId = id;//currentSelConfigIds
        // showOperTips("operInfo","operTip","正在加载小区。。。");
        currentloadtoken = getLoadToken();
        loadGisCellData(currentloadtoken, id);
    });

    // 从小区干扰加载列表中，选择若干列表进行分析
    $("#confirmSelectionAnalysisBtn").click(function () {
        // 触发改变的事件
        changeAnalysisListSelection();
    });

});

//获取和展现被加载的小区配置分析列表
function loadAndShowCellConfigAnalysisList() {
    console.log("加载小区配置")
    $("#analysisListTable_cellconfig").empty();
    $.ajax({
        url: "/api/gsm-frequency-reuse-analysis/cell-config-analysis-list",
        dataType: 'json',
        type: 'GET',
        success: function (data) {
            console.log(data);
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
 * 获取和展现被加载的小区干扰分析列表
 */
function loadAndShowCellInteferenceAnalysisList() {
    $("#analysisListTable_cellinterference").empty();
    $.ajax({
        url: "/api/gsm-frequency-reuse-analysis/cell-interference-analysis-list",
        dataType: 'json',
        type: 'get',
        success: function (data) {
            console.log(data);
            var htmlstr = getCellInterferenceAnaliysisHtml(data);
            $("#analysisListTable_cellinterference").append(htmlstr);
            if (htmlstr != "") {
                $("#analysisBtnDiv").show();
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
            + "  <input type=\"radio\" class=\"forselect\" name=\"cellconfigradio\" value='"
            + data[i]['configId']
            + "' />"
            + "  <label for=\"checkbox\"></label>"
            + "  </td>"
            + "  <td width=\"10%\">"
            + "  <input type=\"button\" class=\"removebtn\" name=\"cellconfig\" value=\"移除\" /><input type=\"hidden\" class=\"hiddenconfigid\" value=\""
            + data[i]['configId'] + "\" />"
            + "  </td >                                                                                                                     "
            + "  </tr>"
            + "<tr><td colspan=\"6\" style=\"background-color: #e7e7e7; height:1px; width:100%\"></td> </tr> ";
    }
    return htmlstr;
}

/**
 * 获取小区干扰列表html
 * @param {} data
 */
function getCellInterferenceAnaliysisHtml(data) {
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
            + "  <input type=\"radio\" class=\"forselect\" name=\"interconfigradio\" value='"
            + data[i]['configId']
            + "' />"
            + "  <label for=\"checkbox\"></label>"
            + "  </td>"
            + "  <td width=\"10%\">"
            + "  <input type=\"button\" class=\"removebtn\" name=\"celliterference\"  value=\"移除\" /><input type=\"hidden\" class=\"hiddenconfigid\" value=\""
            + data[i]['configId'] + "\" />"
            + "  </td >                                                                                                                     "
            + "  </tr>"
            + "<tr><td colspan=\"6\" style=\"background-color: #e7e7e7; height:1px; width:100%\"></td> </tr> ";
    }
    return htmlstr;
}


/**
 * 根据所选择的分析列表加载小区数据
 */

var currentPageFull = -1;

function loadGisCellData(loadToken, cellId) {
    // onLoadingGisCell = true;
    queryCellOverlay.getSource().clear();
    let cityId = $("#cityId").val();
    let filter = `CELL_ID = '` + cellId + `' and AREA_ID = '` + cityId + `'`;
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
            console.log(features)
            queryCellOverlay.getSource().addFeatures(features);
            map.getView().animate({
                center: [features[0].get('LONGITUDE'), features[0].get('LATITUDE')],
                duration: 1000,
                zoom: 18
            });
            currentPageFull += 1;
            bcch = features[0].get("BCCH");
            var tch = features[0].get("TCH");
            var tchLists = tch.split(",");
            tchLists.forEach(function (one) {
                tchList.push(parseInt(one))
            })
            tchList.sort(function (a, b) {
                return a - b
            });
            console.log(tchList)
            $("#reportPageSize").val(20);
            $("#reportCurrentPage").val(0);
            $("#reportPageCount").val(parseInt(tchList.length / $("#reportPageSize").val()) + 1);
            $("#reportTotalCount").val(tchList.length);
        } else {
            animateInAndOut("operInfo", 1000, 1000, 2000, "operTip", "不存在该空间数据");
        }
    })
    // $("#conditionForm")
    //     .ajaxSubmit(
    //         {
    //             url: '/api/gsm-frequency-reuse-analysis/frequency-reuse-cell-gis-info',
    //             data: {"configId": configId},
    //             dataType: 'json',
    //             success: function (data) {
    //                 console.log(data);
    //                 if (loadToken != currentloadtoken) {
    //                     // 新的加载开始了，这些旧的数据要丢弃
    //                     //console.log("丢弃此次的加载结果。");
    //                     return;
    //                 }
    //                 var obj = data;
    //                 try {
    //                     console.log(11111111)
    //                     showGisCellOnMap(obj['gisCells']);
    //                     console.log(222)
    //                     var page = obj['page'];
    //                     console.log(page)
    //                     if (page) {
    //                         var pageSize = page['pageSize'] ? page['pageSize']
    //                             : 0;
    //                         $("#hiddenPageSize").val(pageSize);
    //
    //                         var currentPage = new Number(
    //                             page['currentPage'] ? page['currentPage']
    //                                 : 1);
    //                         currentPage++;// 向下一页
    //                         $("#hiddenCurrentPage").val(currentPage);
    //
    //                         var totalPageCnt = new Number(
    //                             page['totalPageCnt'] ? page['totalPageCnt']
    //                                 : 0);
    //                         $("#hiddenTotalPageCnt").val(totalPageCnt);
    //
    //                         var forcedStartIndex = new Number(
    //                             page['forcedStartIndex'] ? page['forcedStartIndex']
    //                                 : -1);
    //                         forcedStartIndex = -1;//禁用
    //
    //                         $("#hiddenForcedStartIndex").val(
    //                             forcedStartIndex);
    //
    //                         var totalCnt = page['totalCnt'] ? page['totalCnt']
    //                             : 0;
    //                         totalCellCnt = totalCnt;
    //                         $("#hiddenTotalCnt").val(totalCnt);
    //
    //                         var nextStartIndex = (currentPage - 1) * pageSize;
    //                         if (totalCnt > nextStartIndex) {
    //                             loadGisCellData(loadToken, configId);
    //                         } else {
    //                             onLoadingGisCell = false;
    //                             hideOperTips("operInfo");
    //                         }
    //                     }
    //                     // 如果没有获取完成，则继续获取
    //                 } catch (err) {
    //                     console.log("处理过程中问题:" + err);
    //                     if (loadToken == currentloadtoken) {
    //                         onLoadingGisCell = false;//终止
    //                     }
    //                     hideOperTips("operInfo");
    //                 }
    //             },
    //             error: function (xmh, textstatus, e) {
    //                 //alert("出错啦！" + textstatus);
    //                 if (loadToken == currentloadtoken) {
    //                     onLoadingGisCell = false;//终止
    //                 }
    //                 hideOperTips("operInfo");
    //             },
    //             complete: function () {
    //                 $(".loading_cover").css("display", "none");
    //             }
    //         });
}

/**
 * 改变选择的分析列表
 */
function changeAnalysisListSelection() {
    // 比对当前选择的分析列表和之前的分析列表
    // currentSelConfigIds
    var ids = new Array();
    $("#analysisListTable").find("input.forselect:checked").each(
        function (i, ele) {
            ids.push($(ele).attr("id"));
        });
    // 默认没有变化
    var hasChanged = false;
    if (ids.length == currentSelConfigIds.length) {
        for (var i = 0; i < ids.length; i++) {
            for (var j = 0; j < currentSelConfigIds.length; j++) {
                if (ids[i] == currentSelConfigIds[j]) {
                    break;
                }
            }
            if (j == currentSelConfigIds.length) {
                hasChanged = true;
                break;
            }
        }
    } else {
        hasChanged = true;
    }
    console.log(hasChanged)

    if (hasChanged) {
        // 有变化
        currentSelConfigIds.splice(0, currentSelConfigIds.length);
        for (var i = 0; i < ids.length; i++) {
            currentSelConfigIds.push(ids[i]);
        }

        clearAllData();
        initPageParam();// 重置分页参数

        console.log(44444444444)

        // 重选
        $.ajax({
            url: 'reselectAnalysisListForAjaxAction',
            data: {
                'selIds': currentSelConfigIds.join(",")
            },
            type: 'post',
            dataType: 'text',
            success: function (data) {
                currentloadtoken = getLoadToken();
                loadGisCellData(currentloadtoken);
            }
        });
    }
}

/*
function showGisCellOnMap(cellInfo) {

    console.log(cellInfo[0])
    let pointsStr = JSON.parse(cellInfo[0].allLngLats)["baidu"];
    let parts = pointsStr.split(";");
    console.log(parts);
    var coordinates = [];
    parts.forEach(function (onePart) {
        let lnglat = onePart.split(",");
        coordinates.push([parseFloat(lnglat[0]), parseFloat(lnglat[1])]);
    })
    console.log(coordinates)
    var styles = [
        new ol.style.Style({
            stroke: new ol.style.Stroke({
                color: 'blue',
                width: 50 * $("#imgCoeff").val()
            }),
            fill: new ol.style.Fill({
                color: 'rgba(0, 0, 255, 0.1)'
            })
        }),
        new ol.style.Style({
            image: new ol.style.Circle({
                radius: 30 * $("#imgCoeff").val(),
                fill: new ol.style.Fill({
                    color: 'orange'
                })
            }),
            geometry: function (feature) {
                var coordinates = feature.getGeometry().getCoordinates()[0];
                return new ol.geom.MultiPoint(coordinates);
            }
        })
    ];
    var geojsonObject = {
        'type': 'FeatureCollection',
        'crs': {
            'type': 'name',
            'properties': {
                'name': 'EPSG:4326'
            }
        },
        'features': [{
            'type': 'Feature',
            'geometry': {
                'type': 'Polygon',
                'coordinates': [coordinates]
            }
        }]
    };

    var source = new ol.source.Vector({
        features: (new ol.format.GeoJSON()).readFeatures(geojsonObject)
    });
    dynamicCoverageOverlay = new ol.layer.Vector({
        source: source,
        style: styles,
        zIndex: 100
    });
    map.removeLayer(dynamicCoverageOverlay);
    map.addLayer(dynamicCoverageOverlay);
    map.getView().setCenter(coordinates[0])
}
*/
function reportFreqReuse() {
    // $("#reportPageSize").val(20);
    // $("#reportCurrentPage").val(0);
    // $("#reportPageCount").val(0);
    // $("#reportTotalCount").val(0);
    // getReportFreqReuseData();
    console.log(bcch)
    console.log(tchList)
    createReportChart(0, $("#reportPageSize").val());//生成频点统计图
}

/**
 * 获取频点统计数据并生成统计图
 */

/*
function getReportFreqReuseData() {
    $("#analyzeedit_Dialog").hide();

    // console.log(map.get);

//	var id = $("input[name='cellconfigradio']:checked").val();
//	var type = $("input[name='cellconfigradio']:checked").prev().val();
    if (currentSelConfigId == "" || currentSelConfigId == undefined) {
        animateInAndOut("operInfo", 500, 500, 1000, "operTip", "请先选择一个小区配置再进行分析");
        return false;
    }
    if($("#reportCurrentPage").val()==0){
        $("#reportCurrentPage").val(1);
    }
    var currentPage = $("#reportCurrentPage").val();
    var pageSize = $("#reportPageSize").val();

    $("#reportCoverDiv").show();
    $("#report_Dialog").show();
    $("#reportId").val(currentSelConfigId);
    $("#reportType").val("CELLDATA");
    $("#reportForm")
        .ajaxSubmit({
            url: '/api/gsm-frequency-reuse-analysis/statistics-frequency-reuse-info',
            dataType: 'json',
            data: {
                'configId': currentSelConfigId,
                'currentPage': currentPage,
                'pageSize': pageSize
            },//上次加载的数据
            async: false,
            success: function (data) {
//			console.log("data:"+data);
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
                    $("#showCurrentPage").val(currentPage);
                    $("#emTotalPageCnt").html(totalPageCnt);
                }
                console.log(data)
                // createReportChart();//生成频点统计图
                createReportChart();//生成频点统计图
            },
            error: function (xmh, textstatus, e) {
                //alert("出错啦！" + textstatus);
            },
            complete: function () {
                $("#reportCoverDiv").css("display", "none");
            }
        });
    return true;
}
*/
/**
 * 生成频点统计图
 */
function createReportChart(startIndex, endIndex) {
    var cp = $("#reportCurrentPage").val();
    $("#showCurrentPage").val(parseInt(cp) + 1);
    console.log(startIndex);
    $("#emTotalCnt").text($("#reportTotalCount").val());
    $("#emTotalPageCnt").text($("#reportPageCount").val());

    $("#reportCoverDiv").show();
    $("#report_Dialog").show();
    var jsonStr = {};
    for (var i = 0; i < tchList.length; i++) {
        if (i < startIndex) {
            continue;
        }
        if (i == endIndex) {
            break;
        }
        var alreadyHaveOne = 0;
        var alreadyHaveOneId;
        $.each(jsonStr, function (key, value) {
            if (tchList[i] == key) {
                alreadyHaveOne += 1;
                alreadyHaveOneId = key;
            }
        })
        if (alreadyHaveOne > 0) {

        } else {
            var bccCount = 0;
            if (bcch == tchList[i]) {
                bccCount += 1;
            }
            jsonStr[tchList[i]] = {"freq": parseInt(tchList[i]), "bcchCount": bccCount, "tchCount": 1 + alreadyHaveOne};
        }
    }
    //组装数据
    var keyStr = "";
    var tchStr = "";
    var bcchStr = "";
    $.each(jsonStr, function (key, value) {
        keyStr += ",'" + key + "'";
        tchStr += "," + value['tchCount'];
        bcchStr += "," + value['bcchCount'];
    })
    tchStr = tchStr.substring(1, tchStr.length);
    bcchStr = bcchStr.substring(1, bcchStr.length);
    keyStr = keyStr.substring(1, keyStr.length);
    var categories = "[" + keyStr + "]";
    var series = "[{name: 'TCH',type: 'line',data: [" + tchStr + "]}, {name: 'BCCH',type: 'line',data: [" + bcchStr + "]}]";
    categories = eval(categories);
    series = eval(series);

    var myChart = echarts.init(document.getElementById('reportDiv'));
    myChart.showLoading({
        text: "图表数据正在努力加载..."
    });
    var option = {
        // color: ['#3398DB'],
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
    var endIndex = 0;
    if (dir == "first") {
        if (currentPage < 0) {
            return;
        } else {
            $("#reportCurrentPage").val(0);
        }
    } else if (dir == "last") {
        if (currentPage >= totalPageCnt - 1) {
            return;
        } else {
            $("#reportCurrentPage").val(totalPageCnt - 1);
        }
    } else if (dir == "back") {
        if (currentPage <= 0) {
            return;
        } else {
            $("#reportCurrentPage").val(currentPage - 1);
        }
    } else if (dir == "next") {
        if (currentPage >= totalPageCnt - 1) {
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
        if (userinput > totalPageCnt || userinput <= 0) {
            alert("输入页面范围不在范围内！");
            return;
        }
        $("#reportCurrentPage").val(userinput - 1);
    } else if (dir == "all") {
        $("#reportCurrentPage").val(0);
        createReportChart(0, totalCnt);
        return;
    } else {
        return;
    }

    var cp = $("#reportCurrentPage").val();
    endIndex = cp * pageSize + pageSize;
    createReportChart(cp * pageSize, endIndex);
    //获取统计数据
    // getReportFreqReuseData();
}

/**
 * 频率分布标记 div弹出
 */
function analyzeFreqReuse() {
    $("#report_Dialog").hide();

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
    // gisCellDisplayLib.clearOnlyExtraOverlay();//清除额外覆盖物

    // if (onLoadingGisCell == true) {
    //     animateInAndOut("operInfo", 500, 500, 1000, "operTip", "正在加载小区数据，请稍后尝试...");
    //     return;
    // }
    // var cell = null;

//	for(var i in allCellStsResults){
//		cell=i;
//		break;
//	}
//
//	if(!cell){//无小区可标记
//		return;
//	}
//     if (gisCellDisplayLib.getPolygonCnt() == 0) {
//         animateInAndOut("operInfo", 500, 500, 1000, "operTip", "不存在任何小区...");
//         return;
//     }

    var freq_value = $("input[name='freq_value']").val();
    if (freq_value == "") {
        animateInAndOut("operInfo", 500, 500, 1000, "operTip", "请输入频点值！");
        $("input[name='freq_value']").focus();
        return false;
    }
    var cityId = $("#cityId").val();
    markCellOverlay.getSource().clear();
    var ltt = parseFloat(currentFeature[0].values_.LATITUDE);
    var lgt = parseFloat(currentFeature[0].values_.LONGITUDE);
    // console.log(map.getBoundary());
    // console.log(map.getBounds().features());
    let filter = `BCCH = '` + freq_value
        + `' and LONGITUDE > '` + (lgt - 0.1)
        + `' and LONGITUDE < '` + (lgt + 0.1)
        + `' and LATITUDE > '` + (ltt - 0.1)
        + `' and LATITUDE < '` + (ltt + 0.1)
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
            console.log(features)
            features.forEach(function (ft) {

                markCellOverlay.getSource().addFeatures(features);
            })
            map.getView().animate({
                center: [features[0].get('LONGITUDE'), features[0].get('LATITUDE')],
                duration: 1000,
                zoom: 18
            });
        } else {
            animateInAndOut("operInfo", 1000, 1000, 2000, "operTip", "不存在该空间数据");
        }
    })


//     markCellOverlay.getSource().clear();
//
//     // 可视区域
//     var visiblePolygons = gisCellDisplayLib.get("visiblePolygons");
//     var size = visiblePolygons.length;
//     var pl;
//     var cont;
//     //var label;
//     var cnt = 0;
//     clearMarkCellForFreqReuse();//清除之前标记
//     for (var i = 0; i < size; i++) {
//         cont = null;
//         pl = visiblePolygons[i];
//         var cmk = gisCellDisplayLib.getComposeMarkerByShape(pl);
//         cont = cmk.getFreqCellLabelContent(pl, freq_value, gisCellDisplayLib);
//         if (cont != "") {
//             cnt++;
//             var pt = gisCellDisplayLib.getLnglatObjByComposeMarker(cmk);
//             var label = new IsTextLabel(pt.lng, pt.lat, cont, null, gisCellDisplayLib);
//             if (label) {
//                 if (map instanceof BMap.Map) {
//                     label.setStyle({"cursor": "pointer"});
//                     gisCellDisplayLib.addOverlay(label);
//                     label.addEventListener("contextmenu", function (e) {
//                         try {
//                             //map.removeOverlay(label);
//                             gisCellDisplayLib.removeOverlay(this);
// //								$(this).attr("display","none");
//                             animateInAndOut("operInfo", 200, 200, 500, "operTip", $(this).text() + "删除文本标注成功！");
//                         } catch (e) {
//                             animateInAndOut("operInfo", 200, 200, 500, "operTip", $(this).text() + "删除文本标注失败！" + e);
//                         }
//                     });
//                 }
//                 specRenderderLabel.push(label);
//             }
//         }
//     }
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


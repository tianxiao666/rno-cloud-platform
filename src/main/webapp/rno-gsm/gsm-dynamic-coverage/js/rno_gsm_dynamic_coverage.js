// 地图主对象
var map;
// 弹窗对象
let popup;
// 图层组
let cellLayerGroup;
let cellNameLayerGroup;
// 图层
let tiled;
let clickedCellLayer;
let queryCellOverlay;
let queryNCellOverlay;
let queryFreqOverlay;
let dynamicCoverageOverlay;
let dynamicCoverageCell;

// 其他参数
let repaintCell = [];
let dynaPolylineColor_12 = "#2EFE2E";

var isShowCellName = false; //地图是否显示小区

$(document).ready(function () {

    // laydate.render({elem: '#begUploadDate', type: 'datetime', value: new Date("2015-01-01 00:00:00")});
    laydate.render({elem: '#begUploadDate', type: 'datetime', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endUploadDate', type: 'datetime', value: new Date()});
    $(".draggable").draggable();

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
    //控制地图小区名称是否显示
    $("#showCellName").click(function () {
        if (isShowCellName) {
            isShowCellName = false;
            hideCellName();
        } else {
            isShowCellName = true;
            showCellName();
        }
    });
    //打开查找小区窗口
    $(".queryButton").click(function () {
        $("#searchDiv").toggle();
    });
    // 根据条件搜索小区
    $("#searchCellBtn").click(function () {
        searchCell(-1);
    });
    $("#searchNcellBtn").click(function () {
        var ncell = $("#cellForNcell").val();
        searchNcell(ncell);
    });
    // 搜频点
    $("#searchFreqBtn").click(function () {
        var freq = $("#freqValue").val();
        searchFreq(freq);
    });

    $("#clearCoverPolygon").click(function () {
        clearAll();
    });

    $("#map").bind("contextmenu", function () {
        return false;
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
    let greenStyle = new ol.style.Style({
        stroke: new ol.style.Stroke({
            color: 'yellow',
            size: 5
        }),
        fill: new ol.style.Fill({
            color: 'rgba(0, 255, 0, 1.0)'
        })
    });
    let blueStyle = new ol.style.Style({
        stroke: new ol.style.Stroke({
            color: 'yellow',
            size: 5
        }),
        fill: new ol.style.Fill({
            color: 'rgba(255, 100, 255, 1.0)'
        })
    });
    let toolTipStyle = new ol.style.Style({
        stroke: new ol.style.Stroke({
            color: 'yellow',
            size: 5
        }),
        fill: new ol.style.Fill({
            color: 'rgba(0, 255, 0, 1.0)'
        })
    });

    var baseLayer = new ol.layer.Tile({
        source: new ol.source.XYZ({
            url: 'http://rno-omt.hgicreate.com/styles/rno-omt/{z}/{x}/{y}.png',
            zIndex: 1
        })
    });

    cellLayerGroup = new ol.layer.Group({
        zIndex: 2,
        layers: []
    });

    //点击小区图层
    clickedCellLayer = new ol.layer.Vector({
        source: new ol.source.Vector(),
        zIndex: 10,
        style: redStyle
    });
    queryCellOverlay = new ol.layer.Vector({
        zIndex: 11,
        source: new ol.source.Vector(),
        style: greenStyle
    });
    queryNCellOverlay = new ol.layer.Vector({
        zIndex: 5,
        source: new ol.source.Vector(),
        style: redStyle
    });
    queryFreqOverlay = new ol.layer.Vector({
        zIndex: 6,
        source: new ol.source.Vector(),
        style: blueStyle
    });
    dynamicCoverageCell = new ol.layer.Vector({
        zIndex: 3,
        source: new ol.source.Vector(),
        style: redStyle
    });

    cellNameLayerGroup = new ol.layer.Group();

    var lon = parseFloat($(this).find("option:checked").attr("data-lon"));
    var lat = parseFloat($(this).find("option:checked").attr("data-lat"));
    var center = [lon, lat];
    map = new ol.Map({
        target: 'map',
        layers: [baseLayer, cellLayerGroup, cellNameLayerGroup, clickedCellLayer,queryFreqOverlay, queryCellOverlay, queryNCellOverlay],
        view: new ol.View({
            projection: 'EPSG:4326',
            center: center,
            zoom: 16
        })
    });

    //tooltip,显示小区名
    let tooltip = document.getElementById('tooltip');
    let overlay = new ol.Overlay({
        element: tooltip,
        offset: [10, 0],
        positioning: 'bottom-left'
    });
    map.addOverlay(overlay);
    map.on('pointermove', function (e) {
        if (e.dragging)
            return;
        let pixel = e.pixel;
        let feature = map.forEachFeatureAtPixel(pixel, function (feature) {
            return feature;
        });
        map.getTargetElement().style.cursor = feature ? 'pointer' : '';
        tooltip.style.display = feature ? '' : 'none';
        if (feature) {
            overlay.style = toolTipStyle;
            overlay.setPosition(e.coordinate);
            tooltip.innerHTML = typeof(feature.get('CELL_NAME')) !== 'undefined' ? feature.get('CELL_NAME') : '覆盖图';
        }
    });

    popup = new ol.Overlay({
        element: document.getElementById('popup')
    });
    map.addOverlay(popup);

    //地图小区右键菜单
    var contextMenuItems = [
        {
            text: '动态覆盖图',
            callback: function (evt) {
                let features = clickedCellLayer.getSource().getFeatures();
                var element = popup.getElement();
                $(element).popover('destroy');
                if (features.length>1) {
                    popup.setPosition(evt.coordinate);
                    $(element).popover({
                        'placement': 'top',
                        'animation': false,
                        'html': true,
                        'content': createPopupContent(features)
                    });
                    $(element).popover('show');
                    $('.table-popup > tbody > tr').click(function () {
                        let first = $(this).find('td:first');
                        showDynaCoverage(first.text(), first.data("enname"), first.data("lon"), first.data("lat"));
                    });
                }else if(features.length == 1){
                    showDynaCoverage(features[0].get('CELL_ID'), features[0].get('EN_NAME'), features[0].get('LONGITUDE'), features[0].get('LATITUDE'));
                }
            }
        },
        {
            text: '查邻区',
            callback: function (evt) {
                let features = clickedCellLayer.getSource().getFeatures();
                var element = popup.getElement();
                $(element).popover('destroy');
                if (features.length>1) {
                    popup.setPosition(evt.coordinate);
                    $(element).popover({
                        'placement': 'top',
                        'animation': false,
                        'html': true,
                        'content': createPopupContent(features)
                    });
                    $(element).popover('show');
                    $('.table-popup > tbody > tr').click(function () {
                        let first = $(this).find('td:first');
                        searchNcell(first.text());
                    });
                }else if(features.length == 1){
                    searchNcell(features[0].get('CELL_ID'));
                }
            }
        }
    ];

    map.on('singleclick', function (evt) {
        if (map.getView().getZoom() < 15) {
            return;
        }
        let element = popup.getElement();
        $(element).popover('destroy');
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
                    if (features.length>0) {
                        clickedCellLayer.getSource().clear();
                        clickedCellLayer.getSource().addFeatures(features);
                        popup.setPosition(evt.coordinate);
                        $(element).popover({
                            'placement': 'top',
                            'animation': false,
                            'html': true,
                            'content': createPopupContent(features)
                        });
                        $(element).popover('show');
                    }
                    else {
                        clickedCellLayer.getSource().clear();
                    }
                }).catch(function (err) {
                    console.error(err);
                });
            }
        }
    });

    let contextmenu = new ContextMenu({
        width: 135,
        items: contextMenuItems
    });
    //将上下文菜单项加入地图控件
    map.addControl(contextmenu);
    contextmenu.on('beforeopen', function (evt) {
        let element = popup.getElement();
        $(element).popover('destroy');
        if (tiled) {
            let view = map.getView();
            let url = tiled.getSource().getGetFeatureInfoUrl(evt.coordinate, view.getResolution(), view.getProjection(), {
                'INFO_FORMAT': 'application/json',
                'FEATURE_COUNT': 1000
            });
            if (url) {
                $.ajax(url).then(function (response) {
                    let features = new ol.format.GeoJSON().readFeatures(response);
                    if (features.length > 0) {
                        clickedCellLayer.getSource().clear();
                        clickedCellLayer.getSource().addFeatures(features);
                    } else {
                        clickedCellLayer.getSource().clear();
                    }
                }).catch(function (err) {
                    console.error(err);
                });
            }
        }

    });

    $(".ol-unselectable.ol-control.layer-switcher").css("right", "350px").css("top", "0px");

    $("#areaId").change(function () {
        var lon = parseFloat($(this).find("option:checked").attr("data-lon"));
        var lat = parseFloat($(this).find("option:checked").attr("data-lat"));
        map.getView().setCenter([lon, lat]);
    });

    $("#cityId").change(function () {
        var cityId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, cityId, "areaId", true);
            $("#areaId").trigger("change");
        })
    });

    $("#provinceId").change(function () {
        var provinceId = parseInt($(this).find("option:checked").val());
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
    $("#queryCellAreaId").click(function () {
        var btn = $("#queryCellAreaId");
        btn.val("loading");
        var cityId = parseInt($("#cityId").find("option:checked").val());
        var filter = " AREA_ID=" + cityId;
        cellLayerGroup.getLayers().clear();
        tiled = new ol.layer.Tile({
            zIndex: 2,
            source: new ol.source.TileWMS({
                url: 'http://rno-gis.hgicreate.com/geoserver/rnoprod/wms',
                params: {
                    FORMAT: 'image/png',
                    VERSION: '1.1.1',
                    tiled: true,
                    STYLES: '',
                    LAYERS: 'rnoprod:RNO_GSM_CELL_GEOM',
                    CQL_FILTER: filter
                }
            }),
            opacity: 0.5
        });
        cellLayerGroup.getLayers().push(tiled);
        setTimeout(function () {
            btn.val("加载小区信息");
        }, 2000);
    });
});

/**
 * 创建弹窗内容
 * @param features
 * @returns {string}
 */
function createPopupContent(features) {
    let content = '<table class="table table-hover table-popup">';
    content += '<thead style="white-space: nowrap"><th>小区ID</th><th>小区名称</th><th>PCI</th></thead>';
    content += '<tbody>';
    // 获取多个重叠 feature
    for (let i = 0; i < features.length; i++) {
        let feature = features[i];
        content += '<tr style="word-break:break-all">';

        content += '<td style="white-space: nowrap" data-lon=' + feature.get('LONGITUDE') + ' data-lat=' + feature.get('LATITUDE') + ' data-enname=' + feature.get('EN_NAME') + '>' + feature.get('CELL_ID') + '</td>';
        content += '<td>' + feature.get('CELL_NAME') + '</td>';
        content += '<td style="white-space: nowrap">' + feature.get('PCI') + '</td>';
        content += '</tr>';
    }
    content += '</tbody></table>';
    return content;
}

function clearAll() {

    //clickedCellLayer,queryFreqOverlay, queryCellOverlay, queryNCellOverlay
    clearPopup();
    clearDetail();
    clearDynamicCoverlayer();
    clearAllOveraly();
}

function clearAllOveraly() {
    if (clickedCellLayer) {
        clickedCellLayer.getSource().clear();
    }
    if (queryFreqOverlay) {
        queryFreqOverlay.getSource().clear();
    }
    if (queryCellOverlay) {
        queryCellOverlay.getSource().clear();
    }
    if (queryNCellOverlay) {
        queryNCellOverlay.getSource().clear();
    }

}

function clearDynamicCoverlayer() {
    if (dynamicCoverageOverlay) {
        dynamicCoverageOverlay.getSource().clear();
    }
}

function clearPopup() {
    let element = popup.getElement();
    $(element).popover('destroy');
}

function clearDetail() {
    $("#interDetailTab").find("tbody").remove();
}

function showOperTips(outerId, tipId, tips) {
    try {
        var oId = $("#" + outerId);
        oId.css("display", "");
        oId.find("#" + tipId).html(tips);
    } catch (err) {
    }
}

function hideOperTips(outerId) {
    try {
        $("#" + outerId).css("display", "none");
    } catch (err) {
    }
}

function searchCell(cellIdFromOtherMethod) {
    showOperTips("loadingDataDiv", "loadContentId", "正在查找小区");
    let filter = '';
    if(cellIdFromOtherMethod!==-1){
        filter = `CELL_ID in('`+cellIdFromOtherMethod+`')`;
    }else{
        let inputValue = $.trim($("#conditionValue").val());
        if ($.trim(inputValue) === "") {
            hideOperTips("loadingDataDiv");
            animateInAndOut("operInfo", 1000, 1000, 2000, "operTip", "请输入搜索条件");
            return;
        }
        let queryType = $("#conditionType").val();
        let cellArr = inputValue.split(",");
        let cellStr = "";
        for (let i = 0; i < cellArr.length; i++) {
            if ($.trim(cellArr[i]) !== "") {
                if (ifHasSpecChar(cellArr[i].trim())) {
                    hideOperTips("loadingDataDiv");
                    animateInAndOut("operInfo", 1000, 1000, 2000, "operTip", "查询内容不能包含特殊字符和中文标点符号!");
                    return;
                }
                if (queryType === 'CELL_ID' || queryType === 'LAC' || queryType === 'CI') {
                    if (!isOnlyNumberAndComma(cellArr[i].trim())) {
                        hideOperTips("loadingDataDiv");
                        animateInAndOut("operInfo", 1000, 1000, 2000, "operTip", "当前条件只能输入数字和半角-,用半角逗号隔开!");
                        return;
                    }
                }
                cellStr += "'" + cellArr[i].trim() + "',";
            }
        }
        cellStr = cellStr.substring(0, cellStr.length - 1);

        if (queryType === 'CELL_ID') {
            filter = `CELL_ID in (${cellStr})`;
        } else if (queryType === 'CHINESE_NAME') {
            filter = `CELL_NAME in (${cellStr})`
        }
        else if (queryType === 'ENGLISH_NAME') {
            filter = `EN_NAME in (${cellStr})`
        }
        else if (queryType === 'LAC') {
            filter = `LAC in (${cellStr})`
        } else if (queryType === 'CI') {
            filter = `CI in (${cellStr})`
        }
    }
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
            queryCellOverlay.getSource().addFeatures(features);
            map.getView().animate({
                center: [features[0].get('LONGITUDE'), features[0].get('LATITUDE')],
                duration: 1000,
                zoom: 18
            });
        } else {
            animateInAndOut("operInfo", 1000, 1000, 2000, "operTip", "不存在该空间数据");
        }
    }).catch(function (err) {
        console.error(err);
    });
    hideOperTips("loadingDataDiv");
}

function searchNcell(cellId) {
    clearPopup();
    if(!tiled){
        animateInAndOut("operInfo", 1000, 1000, 2000, "operTip", "请先加载小区信息!");
        return;
    }
    let inUsedAreaId = getAreaIdInUsed();
    showOperTips("loadingDataDiv", "loadContentId", "正在查找邻区");
    if ($.trim(cellId) === "") {
        hideOperTips("loadingDataDiv");
        animateInAndOut("operInfo", 1000, 1000, 2000, "operTip", "请输入搜索条件");
        return;
    }
    if (ifHasSpecChar(cellId.trim())) {
        hideOperTips("loadingDataDiv");
        animateInAndOut("operInfo", 1000, 1000, 2000, "operTip", "查询内容不能包含特殊字符和中文标点符号!");
        return;
    }
    if (!isOnlyNumberAndComma(cellId.trim())) {
        hideOperTips("loadingDataDiv");
        animateInAndOut("operInfo", 1000, 1000, 2000, "operTip", "小区ID只能输入数字和半角-,用半角逗号隔开!");
        return;
    }
    var ncells = new Array();
    queryNCellOverlay.getSource().clear();
    $.ajax({
        url: '/portal/api/dynamic-coverage/get-ncell-details',
        data: {
            'cell': cellId,
            'cityId': $("#cityId").val(),
        },
        dataType: 'json',
        type: 'GET',
        success: function (data) {
            hideOperTips("loadingDataDiv");
            var obj = data;
            var view = map.getView();
            if (0 === obj.length) {
                animateInAndOut("operInfo", 1000, 1000, 2000, "operTip", "不存在该空间数据");
                return;
            }
            if (obj) {
                for (var i = 0; i < obj.length; i++) {
                    if (obj[i]['CELL_ID'] == cellId) {
                        continue;
                    }
                    ncells.push("'" + obj[i]['CELL_ID'] + "'");
                }
                let filter = `CELL_ID IN (` + ncells + `) and AREA_ID = ` + "'" + $("#cityId").val() + "'";
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
                    hideOperTips("loadingDataDiv");
                    let view = map.getView();
                    let features = new ol.format.GeoJSON().readFeatures(response);
                    if (features.length) {
                        features.forEach(function (feature) {
                            if (tiled) {
                                var coordinate = [feature.values_.LONGITUDE, feature.values_.LATITUDE];
                                let url = tiled.getSource().getGetFeatureInfoUrl(
                                    coordinate, view.getResolution(), view.getProjection(), {
                                        'INFO_FORMAT': 'application/json',
                                        'FEATURE_COUNT': 1000
                                    });
                                if (url) {
                                    $.ajax(url).then(function (response) {
                                        let features = new ol.format.GeoJSON().readFeatures(response);
                                        if (features.length) {
                                            queryNCellOverlay.getSource().addFeatures(features);
                                        }
                                    }).catch(function (err) {
                                        console.error(err);
                                        animateInAndOut("operInfo", 1000, 1000, 2000, "operTip", "查询出错");
                                    });
                                }
                            }
                        });
                        searchCell(cellId);
                    }
                })
            }
        },
        error: function (xhr, textstatus, e) {
            console.log(textstatus);
            hideOperTips("loadingDataDiv");
        }
    });
}

function searchFreq(freq) {
    if(!tiled){
        animateInAndOut("operInfo", 1000, 1000, 2000, "operTip", "请先加载小区信息!");
        return;
    }
    showOperTips("loadingDataDiv", "loadContentId", "正在查找频点");
    let inputValue = freq;
    if ($.trim(inputValue) === "") {
        hideOperTips("loadingDataDiv");
        animateInAndOut("operInfo", 1000, 1000, 2000, "operTip", "请输入搜索条件");
        return;
    }
    if (!isOnlyNumberAndComma(freq.trim())) {
        hideOperTips("loadingDataDiv");
        animateInAndOut("operInfo", 1000, 1000, 2000, "operTip", "频点只能输入数字!");
        return;
    }
    let cityId = $("#cityId").val();
    let filter = `BCCH = '`+ freq + `' and AREA_ID = '`+ cityId +`'`;
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
        hideOperTips("loadingDataDiv");
        queryFreqOverlay.getSource().clear();
        let view = map.getView();
        let features = new ol.format.GeoJSON().readFeatures(response);
        if (features.length) {
            features.forEach(function (feature) {
                if (tiled) {
                    var coordinate = [feature.values_.LONGITUDE, feature.values_.LATITUDE];
                    let url = tiled.getSource().getGetFeatureInfoUrl(
                        coordinate, view.getResolution(), view.getProjection(), {
                            'INFO_FORMAT': 'application/json',
                            'FEATURE_COUNT': 1000
                        });
                    if (url) {
                        $.ajax(url).then(function (response) {
                            let features = new ol.format.GeoJSON().readFeatures(response);
                            if (features.length) {
                                queryFreqOverlay.getSource().addFeatures(features);
                            }
                        }).catch(function (err) {
                            console.error(err);
                            animateInAndOut("operInfo", 1000, 1000, 2000, "operTip", "查询出错");
                        });
                    }
                }
            })
            var coordinateo = [features[0].values_.LONGITUDE, features[0].values_.LATITUDE];
            map.getView().animate({
                center: coordinateo,
                duration: 2000
            })
        }else{
            animateInAndOut("operInfo", 1000, 1000, 2000, "operTip", "不存在此空间数据");
            return;
        }

    });
}

/**
 * 查看小区动态覆盖图(折线)
 */
function showDynaCoverage(cellId, enName, cellLon, cellLat) {
    showOperTips("loadingDataDiv", "loadContentId", "正在渲染小区动态覆盖图");
    if (!enName || !cellLon || !cellLat) {
        return;
    }
    repaintCell = [enName, cellLon, cellLat];
    // 清空详情表格
    clearDetail();
    //清空界面数据
    clearPopup();
    let cityId = $("#cityId").val();
    let startDate = $("#begUploadDate").val().toString().substring(0, 10);
    let endDate = $("#endUploadDate").val().toString().substring(0, 10);
    //获取图形大小系数
    let imgCoeff = $("#imgCoeff").val();
    let valiNumber = /^[+]?[0-9]+(\.[0-9]+)?$/;   //验证数字
    if (!valiNumber.test(Number(imgCoeff))) {
        animateInAndOut("operInfo", 1000, 1000, 2000, "operTip", "折线图系数请输入数字");
        return;
    }
    if (Number(imgCoeff) <= 0 ||Number(imgCoeff) > 0.5) {
        animateInAndOut("operInfo", 1000, 1000, 2000, "operTip", "折线图系数值应大于0且小于等于0.5！");
        return;
    }
    $.ajax({
        url: '/portal/api/dynamic-coverage/get-dynamic-coverage-data',
        data: {
            'cityId': cityId,
            'cellId': cellId,
            'enName': enName,
            'startDate': startDate,
            'endDate': endDate
        },
        dataType: 'json',
        type: 'post',
        success: function (data) {
            hideOperTips("loadingDataDiv");
            if (data === null) {
                animateInAndOut("operInfo", 1000, 1000, 2000, "operTip", "未能查询到数据，请检查日期！");
            }
            if (data !== null) {
                let curvePoints_12 = data['curvePoints_12'];
                let resInterDetail = data['resInterDetail'];
                if (curvePoints_12 !== null) {
                    let vectorPoints_12 = data['vectorPoint_12'];
                    let vecLng_12 = vectorPoints_12[0]['lng'];
                    let vecLat_12 = vectorPoints_12[0]['lat'];
                    let pointArray_12 = [];
                    for (let i = 0, idx; idx = curvePoints_12[i++];) {
                        pointArray_12.push([idx["lng"], idx["lat"]]);
                    }
                    let title = "【" + enName + "】的动态覆盖数据列表";
                    $("#title").text(title);
                    let html = "<tbody>";
                    if (pointArray_12.length > 4) {
                        drawArrow(cellLon, cellLat, vecLng_12, vecLat_12, 0.002, dynaPolylineColor_12, curvePoints_12);
                        let element = popup.getElement();
                        $(element).popover('destroy');
                    } else {
                        // alert("Each LinearRing of a Polygon must have 4 or more Positions");
                        animateInAndOut("operInfo", 1000, 1000, 1000, "operTip", "多边形点数不足！");
                    }
                } else {
                    animateInAndOut("operInfo", 1000, 1000, 1000, "operTip", "该小区在搜索的时间段内没有数据！");
                }
            } else {
                animateInAndOut("operInfo", 1000, 1000, 1000, "operTip", "该小区在搜索的时间段内没有数据！");
            }
        }, error: function (err) {
            console.log(err);
            hideOperTips("loadingDataDiv");
        }
    });
}

function drawArrow(cellLon, cellLat, vecLng, vecLat, ratio, color, points) {
    map.removeLayer(dynamicCoverageOverlay);
    let difflng = vecLng - cellLon;
    let difflat = vecLat - cellLat;
    let r = Math.sqrt(difflng * difflng + difflat * difflat);
    let conV = difflng / r;
    let sinV = difflat / r;
    var coordinates = [];
    points.forEach(function (point) {
        coordinates.push([point.lng, point.lat]);
    });
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
    map.addLayer(dynamicCoverageOverlay);
    map.getView().setCenter(coordinates[0])
}

function isOnlyNumberAndComma(str) {
    let reg = /^[0-9,-]+$/;
    return reg.test(str);
}

function toConverVal(val) {
    let num = Number(val);
    if (num !== 0) {
        return num.toFixed(4);
    } else {
        return num;
    }
}

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

function hideCellName() {
    $("#showCellName").val("loading");
    cellNameLayerGroup.getLayers().clear();
    setTimeout(function () {
        $("#showCellName").val("显示小区名");
    }, 100);
}

function showCellName() {
    // 在地图上显示相应频段的小区
    var btn = $("#showCellName");
    $("#showCellName").val("loading");
    var cityId = $("#cityId").val();
    var area_id = "AREA_ID=" + cityId;
    var filter = area_id;

    cellNameLayerGroup.getLayers().clear();
    var cellNameLayer = new ol.layer.Tile({
        zIndex: 4,
        source: new ol.source.TileWMS({
            url: 'http://rno-gis.hgicreate.com/geoserver/rnoprod/wms',
            params: {
                FORMAT: 'image/png',
                VERSION: '1.1.1',
                TILED: true,
                STYLES: '',
                LAYERS: 'rnoprod:RNO_LTE_CELL_CENTROID',
                'CQL_FILTER': filter
            }
        }),
        opacity: 0.5
    });
    cellNameLayerGroup.getLayers().push(cellNameLayer);
    setTimeout(function () {
        $("#showCellName").val("隐藏小区名");
    }, 2000);
}
function getAreaIdInUsed() {
    if(!tiled){
        animateInAndOut("operInfo", 1000, 1000, 2000, "operTip", "请先加载小区信息!");
        return;
    }
    let keyString = tiled.getSource().key_;
    let areaIdInUsed = keyString.substring(keyString.indexOf("AREA_ID=")+8,keyString.length);
    return areaIdInUsed;
}
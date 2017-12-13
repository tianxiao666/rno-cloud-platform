// 配置参数缓存对象
let cellLayers;
let cellNameLayerName;
let baseLayersUrl;
let cellLayersUrl;
let wfsCellUrl;
let contextPath;
// 区域数据对象
let areaObj;
let currentProvinceObj;
let currentCityObj;
let currentAreaObj;
// 地图主对象
var map;
// 弹窗对象
let popup;
// 图层组
let cellLayerGroup;
let overlaysGroup;
let cellNameLayerGroup;
// 图层
let tiled;
let highlightOverlay;
let queryCellOverlay;
let interCellOverlay;
let thisCellOverlay;
let bezierOverlay;
let lineOverlay;
let dynamicCoverageOverlay;

// 其他参数
let repaintCell = [];
let dynaPolylineColor_12 = "#2EFE2E";

var isShowCellName = false; //地图是否显示小区

$(document).ready(function () {

    laydate.render({elem: '#begUploadDate', type: 'datetime', value: new Date("2015-09-01 00:00:00")});
    // laydate.render({elem: '#begUploadDate', type: 'datetime', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endUploadDate', type: 'datetime', value: new Date()});
    $(".draggable").draggable();
    //绑定事件
    // bindEvent();
    // 切换区域
    // initAreaCascade(areaObj);

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
            console.log("正在隐藏小区名字");
            // $("#showCellName").val("显示小区名字");
            isShowCellName = false;
            hideCellName();
        } else {
            console.log("正在显示小区名字");
            // $("#showCellName").val("关闭小区名字");
            isShowCellName = true;
            showCellName();
        }
    });
    //打开查找小区窗口
    $(".queryButton").click(function() {
        //$("#searchDiv").slideToggle();
        $("#searchDiv").toggle();
    });
    // 根据条件搜索小区
    $("#searchCellBtn").click(function () {
        searchCell();
    });
    $("#searchNcellBtn").click(function() {
        $("span#errorDiv").html("");
        var ncell = $("#cellForNcell").val();
        var strExp=/^[\u4e00-\u9fa5A-Za-z0-9\s_-]+$/;
        if(!strExp.test(ncell)){
            $("span#errorDiv").html("含有非法字符！");
            return false;
        }else if(!(ncell.length<40)){
            $("span#errorDiv").html("输入信息过长！");
            return false;
        }
        searchNcell(ncell);
    });
    // 搜频点
    $("#searchFreqBtn").click(function() {
        $("span#errorDiv").html("");
        var freq = $("#freqValue").val();
        var strExp=/^[\u4e00-\u9fa5A-Za-z0-9\s_-]+$/;
        if(!strExp.test(freq)){
            $("span#errorDiv").html("含有非法字符！");
            return false;
        }else if(!(freq.length<40)){
            $("span#errorDiv").html("输入信息过长！");
            return false;
        }
        searchFreq(freq);
    });

    //清除地图上的动态覆盖图
    $("#clearCoverPolygon").click(function () {
        //清除动态覆盖图，方向线，箭头
        clearAll();
    });

    // 禁止显示浏览器HTML页面缺省的右键菜单，不影响 Openlayers3 自定义的右键菜单
    $("#map").bind("contextmenu", function () {
        return false;
    });

    //样式设置
    let redStyle = new ol.style.Style({
        stroke: new ol.style.Stroke({
            // 设置线条颜色
            color: 'yellow',
            size: 5
        }),
        fill: new ol.style.Fill({
            // 设置填充颜色与不透明度
            color: 'rgba(255, 0, 0, 1.0)'
        })
    });

    let blackStyle = new ol.style.Style({
        stroke: new ol.style.Stroke({
            // 设置线条颜色
            color: 'yellow',
            size: 5
        }),
        fill: new ol.style.Fill({
            // 设置填充颜色与不透明度
            color: 'black',
            opacity: 1.0
        })
    });

    let greenStyle = new ol.style.Style({
        stroke: new ol.style.Stroke({
            // 设置线条颜色
            color: 'yellow',
            size: 5
        }),
        fill: new ol.style.Fill({
            // 设置填充颜色与不透明度
            color: '#008000',
            opacity: 1.0
        })
    });

    var baseLayerGroup = new ol.layer.Group({
        'title': '基础地图',
        zIndex: 1,
        layers: [
            // 添加一个使用离线瓦片地图的层
            new ol.layer.Tile({
                title: '本地地图',
                // type: 'base',
                // visible: true,
                zIndex: 1,
                source: new ol.source.XYZ({
                    url: 'http://rno-omt.hgicreate.com/styles/rno-omt/{z}/{x}/{y}.png'
                })
            })]
    });

    var baseLayer = new ol.layer.Tile({
        zIndex: 1,
        source: new ol.source.XYZ({
            url: 'http://rno-omt.hgicreate.com/styles/rno-omt/{z}/{x}/{y}.png'
        })
    });

    // tiled = new ol.layer.Tile({
    //     // title: 'LTE小区',
    //     // zIndex: 3,
    //     source: new ol.source.TileWMS({
    //         url: 'http://rno-gis.hgicreate.com/geoserver/rnoprod/wms',
    //         params: {
    //             FORMAT: 'image/png',
    //             VERSION: '1.1.1',
    //             tiled: true,
    //             STYLES: '',
    //             LAYERS: 'rnoprod:RNO_GSM_CELL_GEOM'
    //         }
    //     }),
    //     visible: false,
    //     opacity: 0.5
    // });

    // 建立小区图层组，用于维持图层相对关系，获取小区图层载入点。
    cellLayerGroup = new ol.layer.Group({
        zIndex: 2,
        layers: []
    });
    highlightOverlay = new ol.layer.Vector({
        zIndex: 5,
        source: new ol.source.Vector(),
        style: redStyle
    });
    queryCellOverlay = new ol.layer.Vector({
        zIndex: 6,
        source: new ol.source.Vector(),
        style: redStyle
    });

    interCellOverlay = new ol.layer.Vector({
        zIndex: 7,
        source: new ol.source.Vector(),
        style: greenStyle
    });

    thisCellOverlay = new ol.layer.Vector({
        zIndex: 8,
        source: new ol.source.Vector(),
        style: blackStyle
    });

    bezierOverlay = new ol.layer.Vector({
        zIndex: 9,
        source: new ol.source.Vector()
    });

    lineOverlay = new ol.layer.Vector({
        zIndex: 10,
        source: new ol.source.Vector(),
        style: redStyle
    });

    overlaysGroup = new ol.layer.Group({
        layers: [highlightOverlay, queryCellOverlay, interCellOverlay, thisCellOverlay, bezierOverlay, lineOverlay]
        // layers: [highlightOverlay]
    });

    // 小区名放在最上面
    cellNameLayerGroup = new ol.layer.Group();


    var lon = parseFloat($(this).find("option:checked").attr("data-lon"));
    var lat = parseFloat($(this).find("option:checked").attr("data-lat"));
    var center = ol.proj.fromLonLat([lon, lat]);
    map = new ol.Map({
        target: 'map',
        // layers: [baseLayer,cellLayerGroup, overlaysGroup],
        layers: [baseLayerGroup, cellLayerGroup, overlaysGroup, cellNameLayerGroup],
        view: new ol.View({
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
        // console.log(feature);
        if (feature) {
            console.log(feature)
            overlay.setPosition(e.coordinate);
            tooltip.innerHTML = typeof(feature.get('CELL_NAME')) !== 'undefined' ? feature.get('CELL_NAME') : '覆盖图';
        }
    });

    // Popup showing the position the user clicked

    popup = new ol.Overlay({
        element: document.getElementById('popup')
    });
    map.addOverlay(popup);


    map.on('singleclick', function (evt) {
        if (map.getView().getZoom() < 15) {
            return;
        }
        let element = popup.getElement();
        $(element).popover('destroy');
        let view = map.getView();
        let url = tiled.getSource().getGetFeatureInfoUrl(evt.coordinate, view.getResolution(), view.getProjection(), {
            'INFO_FORMAT': 'application/json',
            'FEATURE_COUNT': 1000
        });
        if (url) {
            $.ajax(url).then(function (response) {
                let features = new ol.format.GeoJSON().readFeatures(response);
                if (features.length) {
                    // 高亮 Features
                    // baseLayerGroup.getLayers().clear()
                    highlightOverlay.getSource().clear();
                    queryCellOverlay.getSource().clear();
                    highlightOverlay.getSource().addFeatures(features);
                    console.log(highlightOverlay);
                    popup.setPosition(evt.coordinate);
                    // the keys are quoted to prevent renaming in ADVANCED mode.
                    $(element).popover({
                        'placement': 'top',
                        'animation': false,
                        'html': true,
                        'content': createPopupContent(features)
                    });
                    $(element).popover('show');
                }
            }).catch(function (err) {
                console.error(err);
            });
        }
    });

    let showDynaCoverageCallBack = function dynaCoverage(evt) {
        let features = highlightOverlay.getSource().getFeatures();
        let element = popup.getElement();
        console.log(features)
        if (features.length) {
            popup.setPosition(evt.coordinate);
            // the keys are quoted to prevent renaming in ADVANCED mode.
            $(element).popover({
                'placement': 'top',
                'animation': false,
                'html': true,
                'content': createPopupContent(features)
            });
            $(element).popover('show');
            $('.table-popup > tbody > tr').click(function () {
                // row was clicked
                let cell = $(this).find('td:first');
                showDynaCoverage(cell.text(), cell.data("lon"), cell.data("lat"));
            });
        }
    };

    //地图小区右键菜单
    var contextMenuItems = [
        {
            text:'动态覆盖图1',
            callback:function(evt){
                console.log(contextMenuItems[1].polygon)
                //清理弹出小区信息覆盖物
                let features = highlightOverlay.getSource().getFeatures();
                var element = popup.getElement();
                $(element).popover('destroy');
                if (features.length) {
                    popup.setPosition(evt.coordinate);
                    $(element).popover({
                        'placement': 'top',
                        'animation': false,
                        'html': true,
                        'content': createPopupContent(features)
                    });
                    $(element).popover('show');
                    $('.table-popup > tbody > tr').click(function () {
                        // row was clicked
                        let cell = $(this).find('td:first');
                        showDynaCoverage(cell.text(), cell.data("lon"), cell.data("lat"));
                    });
                }
            }
        },{
            text:'动态覆盖图2',
            callback:function(evt){
                let features = highlightOverlay.getSource().getFeatures();
                var element = popup.getElement();
                $(element).popover('destroy');
                console.log(features)
                if (features.length) {
                    popup.setPosition(evt.coordinate);
                    $(element).popover({
                        'placement': 'top',
                        'animation': false,
                        'html': true,
                        'content': createPopupContent(features)
                    });
                    $(element).popover('show');
                    $('.table-popup > tbody > tr').click(function () {
                        let cell = $(this).find('td:first');
                        showDynaCoverage2(cell.text(), cell.data("lon"), cell.data("lat"));
                    });
                }
            }
        }
    ];


    let contextmenu = new ContextMenu({
        width: 135,
        items: contextMenuItems
    });
    //将上下文菜单项加入地图控件
    map.addControl(contextmenu);
    // 右键菜单打开之前，判断是否在 feature 上，如果不是则禁止右键菜单
    // 先将该处feature重绘然后再打开feature;
    contextmenu.on('beforeopen', function (evt) {
        //清理弹出小区信息覆盖物
        let element = popup.getElement();
        $(element).popover('destroy');
        highlightOverlay.getSource().clear();
        let view = map.getView();
        let url = tiled.getSource().getGetFeatureInfoUrl(evt.coordinate, view.getResolution(), view.getProjection(), {
            'INFO_FORMAT': 'application/json',
            'FEATURE_COUNT': 1000
        });
        console.log(url)
        // contextmenu.disable();
        if (url) {
            $.ajax(url).then(function (response) {
                console.log(response)
                let features = new ol.format.GeoJSON().readFeatures(response);
                console.log(features.length)
                if (features.length > 0) {
                    highlightOverlay.getSource().clear();
                    src = 'EPSG:4326';
                    dest = 'EPSG:3857';
                    for(var i=0;i<features.length;i++){
                        features[i].values_.LONGITUDE = (ol.proj.transform([features[i].values_.LONGITUDE,features[i].values_.LATITUDE], src, dest))[0];
                        features[i].values_.LATITUDE = (ol.proj.transform([features[i].values_.LONGITUDE,features[i].values_.LATITUDE], src, dest))[1];
                    }
                    highlightOverlay.getSource().addFeatures(features);

                    // var source = new ol.source.Vector({
                    //     features: f
                    // });
                    //
                    // var layer = new ol.layer.Vector({
                    //     source: source,
                    //     style: new ol.style.Style({
                    //         stroke: new ol.style.Stroke({
                    //             color: 'blue',
                    //             width: 30
                    //         }),
                    //         fill: new ol.style.Fill({
                    //             color: 'rgba(0, 0, 255, 0.1)'
                    //         })
                    //     })
                    // });
                    // map.addLayer(layer);
                } else {
                    highlightOverlay.getSource().clear();
                    console.log("contextMenu unable");
                }
            }).catch(function (err) {
                console.error(err);
            });
        }
    });

    $(".ol-unselectable.ol-control.layer-switcher").css("right", "350px").css("top", "0px");


    $("#areaId").change(function () {

        var lon = parseFloat($(this).find("option:checked").attr("data-lon"));
        var lat = parseFloat($(this).find("option:checked").attr("data-lat"));
        map.getView().setCenter(ol.proj.transform([lon, lat], 'EPSG:4326', 'EPSG:3857'));
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
        var band_type;
        var area_id;

        // band_type = "BAND_TYPE in('D','E','F')";

        var cityId = parseInt($("#cityId").find("option:checked").val());
        area_id = " AREA_ID=" + cityId;
        var filter = band_type + area_id;
        cellLayerGroup.getLayers().clear();
        // clearAll();
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
 * 绑定联动事件，默认实现，需要可以替换
 */
function bindCascade(data, provinceId, cityId, areaId) {
    $("#" + provinceId).change(function () {
        // 触发父区域改变事件。
        currentProvinceObj = areaChange(data, provinceId, cityId);
        $("#" + cityId).change();
    });

    $("#" + cityId).change(function () {
        currentCityObj = areaChange(currentProvinceObj['children'], cityId, areaId);
        $("#" + areaId).change();

        //在市级层面联动前将先前的区域小区图层清理掉
        clearAll();
        cellLayerGroup.getLayers().clear();
        cellNameLayerGroup.getLayers().clear();
        $("#showCellName").button('show');

        //获取mr数据记录
        findByAreaIdAndDataType();
    });

    $("#" + areaId).change(function () {
        let areaCode = $("#" + areaId).val();
        if (areaCode === null || areaCode === undefined || areaCode === '') {
            return;
        }
        let one;
        for (let i = 0; i < currentCityObj['children'].length; i++) {
            one = currentCityObj['children'][i];
            if (one['id'] && one['id'] === Number(areaCode)) {
                currentAreaObj = one;
                break;
            }
        }
        let lon = currentAreaObj['lon'];
        let lat = currentAreaObj['lat'];
        if (lon && lat) {
            map.getView().animate({
                center: [lon, lat],
                duration: 1000
            });
        }
    });
}

/*
function bindEvent() {
    // tab选项卡
    $(".draggable").draggable();

    // 展开面板
    $(".switch").click(function () {
        $(this).hide();
        $(".switch_hidden").show();
        $(".resource_list_icon").animate({
            right: '0px'
        }, 'fast');
        $(".resource_list_box").hide("fast");
        $(".ol-unselectable.ol-control.layer-switcher").css("right", "82px").css("top", "0px");
    });
    // 收起面板
    $(".switch_hidden").click(function () {
        $(this).hide();
        $(".switch").show();
        $(".resource_list_icon").animate({
            right: '286px'
        }, 'fast');
        $(".resource_list_box").show("fast");
        $(".ol-unselectable.ol-control.layer-switcher").css("right", "350px").css("top", "0px");
    });

    // 在地图上显示相应频段的小区
    $("#loadCellToMap").click(function () {
        let btn = $("#loadCellToMap");
        btn.button('loading');
        let band_type;
        let area_id;
        let bt = $("#bandType").val();
        if (bt === 'all') {
            band_type = "BAND_TYPE in('D','E','F')";
            currentBandType = "all";
        } else {
            band_type = "BAND_TYPE = '" + bt + "'";
            currentBandType = bt;
        }
        let cityId = $("#cityId").val();
        area_id = " and AREA_ID=" + cityId;
        let filter = band_type + area_id;

        clearAll();
        cellLayerGroup.getLayers().clear();
        tiled = new ol.layer.Tile({
            zIndex: 3,
            source: new ol.source.TileWMS({
                url: cellLayersUrl,
                params: {
                    FORMAT: 'image/png',
                    VERSION: '1.1.1',
                    tiled: true,
                    STYLES: '',
                    LAYERS: cellLayers,
                    CQL_FILTER: filter
                }
            }),
            opacity: 0.5
        });
        cellLayerGroup.getLayers().push(tiled);

        setTimeout(function () {
            btn.button('reset');
        }, 2000);
    });

    // 在地图上显示相应频段的小区
    $("#showCellName").click(function () {
        let btn = $("#showCellName");
        if ("显示小区名" === btn.val()) {
            btn.button('loading');

            let band_type;
            let area_id;
            let bt = $("#bandType").val();
            band_type = "BAND_TYPE in('D','E','F')";
            let cityId = $("#cityId").val();
            area_id = " and AREA_ID=" + cityId;
            let filter = band_type + area_id;

            cellNameLayerGroup.getLayers().clear();

            let cellNameLayer = new ol.layer.Tile({
                zIndex: 100,
                source: new ol.source.TileWMS({
                    url: 'http://rno-gis.hgicreate.com/geoserver/rnoprod/wms',
                    params: {
                        FORMAT: 'image/png',
                        VERSION: '1.1.1',
                        TILED: true,
                        STYLES: '',
                        LAYERS: cellNameLayerName,
                        'CQL_FILTER': filter
                    }
                }),
                opacity: 0.5
            });

            cellNameLayerGroup.getLayers().push(cellNameLayer);

            setTimeout(function () {
                btn.button("hidden");
            }, 2000);
        } else {
            btn.button('loading');
            cellNameLayerGroup.getLayers().clear();

            setTimeout(function () {
                btn.button("show");
            }, 2000);
        }
    });

    // 重绘
    $("#repaintBtn").click(function () {
        if (repaintCell.length === 0) {
            alert("请先绘制动态覆盖图，再进行重绘操作！");
        } else {
            lineOverlay.getSource().clear();
            bezierOverlay.getSource().clear();
            let element = popup.getElement();
            $(element).popover('destroy');
            $("#repaintBtn").button('loading');
            showDynaCoverage2(repaintCell[0], repaintCell[1], repaintCell[2]);
            setTimeout(function () {
                $("#repaintBtn").button('reset');
            }, 1000);
        }
    });

    // 根据条件搜索小区
    $("#searchCellBtn").click(function () {
        searchCell();
    });

    //清除地图上的动态覆盖图
    $("#clearCoverPolygon").click(function () {
        //清除动态覆盖图，方向线，箭头
        clearAll();
    });

    if ($("#dateSelect").find("option").length === 0) {
        $("#mrDataTip").text("没有可用的MR数据，请先进行MR数据导入");
    }

    // 默认不选日期
    $("#dateSelect").val(0);

    // 绑定日期多选事件
    popUp();
}
*/
let showDynaCoverage2CallBack = function dynaCoverage2(evt) {
    let features = highlightOverlay.getSource().getFeatures();
    let element = popup.getElement();
    console.log(features)
    if (features.length) {
        popup.setPosition(evt.coordinate);
        // the keys are quoted to prevent renaming in ADVANCED mode.
        $(element).popover({
            'placement': 'top',
            'animation': false,
            'html': true,
            'content': createPopupContent(features)
        });
        $(element).popover('show');
        $('.table-popup > tbody > tr').click(function () {
            // row was clicked
            let cell = $(this).find('td:first');
            showDynaCoverage2(cell.text(), cell.data("lon"), cell.data("lat"));
        });
    }
};

let InInterCellLineCallBack = function InCellLine(evt) {
    let features = highlightOverlay.getSource().getFeatures();
    let element = popup.getElement();
    if (features.length) {
        popup.setPosition(evt.coordinate);
        // the keys are quoted to prevent renaming in ADVANCED mode.
        $(element).popover({
            'placement': 'top',
            'animation': false,
            'html': true,
            'content': createPopupContent(features)
        });
        $(element).popover('show');
        $('.table-popup > tbody > tr').click(function () {
            // row was clicked
            let cell = $(this).find('td:first');
            InInterCellLine(cell.text(), cell.data("lon"), cell.data("lat"));
        });
    }
};

let OutInterCellLineCallBack = function OutCellLine(evt) {
    let features = highlightOverlay.getSource().getFeatures();
    let element = popup.getElement();
    if (features.length) {
        popup.setPosition(evt.coordinate);
        // the keys are quoted to prevent renaming in ADVANCED mode.
        $(element).popover({
            'placement': 'top',
            'animation': false,
            'html': true,
            'content': createPopupContent(features)
        });
        $(element).popover('show');

        $('.table-popup > tbody > tr').click(function () {
            // row was clicked
            let cell = $(this).find('td:first');
            OutInterCellLine(cell.text(), cell.data("lon"), cell.data("lat"));
        });
    }
};

/**
 * 创建弹窗内容
 * @param features
 * @returns {string}
 */
function createPopupContent(features) {
    let content = '<table class="table table-hover table-popup">';
    // content += '<thead><th>小区ID</th><th>小区名称</th><th>PCI</th></thead>';
    content += '<thead style="white-space: nowrap"><th>小区ID</th><th>小区名称</th><th>PCI</th></thead>';
    content += '<tbody>';
    // 获取多个重叠 feature
    for (let i = 0; i < features.length; i++) {
        let feature = features[i];
        content += '<tr style="word-break:break-all">';
        content += '<td style="white-space: nowrap" data-lon=' + feature.get('LONGITUDE') + ' data-lat=' + feature.get('LATITUDE') + '>' + feature.get('CELL_ID') + '</td>';
        content += '<td>' + feature.get('CELL_NAME') + '</td>';
        content += '<td style="white-space: nowrap">' + feature.get('PCI') + '</td>';
        content += '</tr>';
    }
    content += '</tbody></table>';
    return content;
}

/**
 * 画出两点坐标的连线
 */
function drawLineBetweenPoints(cellLon, cellLat, ncellLon, ncellLat, option) {
    let line = new ol.geom.LineString(ol.proj.transform([[cellLon, cellLat], [ncellLon, ncellLat]], 'EPSG:4326', 'EPSG:4326'));
    let lineFeature = new ol.Feature(line);

    lineFeature.setStyle(new ol.style.Style({
        fill: new ol.style.Fill({
            color: option.strokeColor
        }),
        stroke: new ol.style.Stroke({
            width: option.strokeWeight,
            color: option.strokeColor
        })
    }));
    return lineFeature;
}



/**
 * 清除全部覆盖数据数据
 */
function clearAll() {
    clearOverlayLayers();

    clearPopup();

    clearDetail();

    clearDynamicCoverlayer();
}

/**
 * 清除动态覆盖图
 */
function clearDynamicCoverlayer() {
    if(dynamicCoverageOverlay){
        dynamicCoverageOverlay.getSource().clear();
    }
}
/**
 * 清除方向线，箭头
 */
function clearOverlayLayers() {
    overlaysGroup.getLayers().forEach(function (layer) {
        layer.getSource().clear();
    });
}

/**
 * 清除弹窗
 */
function clearPopup() {
    let element = popup.getElement();
    $(element).popover('destroy');
}

/**
 * 清除详情表格
 */
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


/***************** 查找地图小区 start ********************/
/**
 * 按条件搜索小区
 */
function searchCell() {
    showOperTips("loadingDataDiv", "loadContentId", "正在查找小区");

    // 获取输入的值
    let inputValue = $.trim($("#conditionValue").val());
    if ($.trim(inputValue) === "") {
        hideOperTips("loadingDataDiv");
        alert("请输入搜索条件");
        return;
    }

    let queryType = $("#conditionType").val();
    let cellArr = inputValue.split(",");
    let cellStr = "";
    for (let i = 0; i < cellArr.length; i++) {
        if ($.trim(cellArr[i]) !== "") {
            if (ifHasSpecChar(cellArr[i].trim())) {
                hideOperTips("loadingDataDiv");
                alert("查询内容不能包含特殊字符和中文标点符号!");
                return;
            }
            if (queryType === 'cell') {
                if (!isOnlyNumberAndComma(cellArr[i].trim())) {
                    hideOperTips("loadingDataDiv");
                    alert("小区ID只能输入数字和半角-,用半角逗号隔开!");
                    return;
                }
            }
            cellStr += "'" + cellArr[i].trim() + "',";
        }
    }
    cellStr = cellStr.substring(0, cellStr.length - 1);

    let filter = queryType === 'cell' ? `CELL_ID in (${cellStr})` : `CELL_NAME in (${cellStr})`;

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
            // 高亮 Features
            clearAll();
            // queryCellOverlay.getSource().addFeatures(features);
            var src = 'EPSG:4326';
            var dest = 'EPSG:3857';

            // 取第一个小区扇形的顶点为新的地图中心点
            let lonlat = (ol.proj.transform([features[0].get('LONGITUDE'),features[0].get('LATITUDE')], src, dest));

            var a = [lonlat[0],lonlat[1]];

            var styles = [
                new ol.style.Style({
                    stroke: new ol.style.Stroke({
                        color: 'blue',
                        width: 50
                    }),
                    fill: new ol.style.Fill({
                        color: 'rgba(0, 0, 255, 0)'
                    })
                }),
                new ol.style.Style({
                    image: new ol.style.Circle({
                        radius: 20,
                        fill: new ol.style.Fill({
                            color: 'orange'
                        })
                    }),
                    geometry: function(feature) {
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
                'features': [  {
                    'type': 'Feature',
                    'geometry': {
                        'type': 'Polygon',
                        'coordinates': [a]
                    }
                }]
            };
            var source = new ol.source.Vector({
                features: (new ol.format.GeoJSON()).readFeatures(geojsonObject)
            });
            var cellQueryOverlay = new ol.layer.Vector({
                source: source,
                style: styles,
                zIndex: 200
            });
            map.addLayer(cellQueryOverlay);
            console.log(map)
            console.log(a)
            map.getView().animate({
                center: [lonlat[0],lonlat[1]],
                duration: 1000,
                zoom:20
            });
        } else {
            animateInAndOut("operInfo", 1000, 1000, 1000, "operTip", "不存在该空间数据");
        }
    }).catch(function (err) {
        console.error(err);
    });
    hideOperTips("loadingDataDiv");
}

function searchNcell(cellId){
    showOperTips("loadingDataDiv", "loadContentId", "正在查找小区");

    // 获取输入的值
    let inputValue = cellId;
    if ($.trim(inputValue) === "") {
        hideOperTips("loadingDataDiv");
        alert("请输入搜索条件");
        return;
    }

    let queryType = $("#conditionType").val();
    let cellArr = inputValue.split(",");
    let cellStr = "";
    for (let i = 0; i < cellArr.length; i++) {
        if ($.trim(cellArr[i]) !== "") {
            if (ifHasSpecChar(cellArr[i].trim())) {
                hideOperTips("loadingDataDiv");
                alert("查询内容不能包含特殊字符和中文标点符号!");
                return;
            }
            if (queryType === 'cell') {
                if (!isOnlyNumberAndComma(cellArr[i].trim())) {
                    hideOperTips("loadingDataDiv");
                    alert("小区ID只能输入数字和半角-,用半角逗号隔开!");
                    return;
                }
            }
            cellStr += "'" + cellArr[i].trim() + "',";
        }
    }
    cellStr = cellStr.substring(0, cellStr.length - 1);

    let filter = queryType === 'cell' ? `CELL_ID in (${cellStr})` : `CELL_NAME in (${cellStr})`;

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
            // 高亮 Features
            clearAll();
            // queryCellOverlay.getSource().addFeatures(features);
            var src = 'EPSG:4326';
            var dest = 'EPSG:3857';

            // 取第一个小区扇形的顶点为新的地图中心点
            let lonlat = (ol.proj.transform([features[0].get('LONGITUDE'),features[0].get('LATITUDE')], src, dest));

            showDynaCoverage(cellId,lonlat[0],lonlat[1] );
        }
    });
}

function searchFreq(freq) {
    console.log("搜索频点");
}

/**
 * 查看小区动态覆盖图(折线)
 */
function showDynaCoverage(cellId, cellLon, cellLat) {
    console.log(cellId)
    if (!cellId || !cellLon || !cellLat) {
        return;
    }
    repaintCell = [cellId, cellLon, cellLat];
    // 清空详情表格
    clearDetail();
    //清空界面数据
    clearPopup();
    let cityId = $("#cityId").val();
    let startDate = $("#begUploadDate").val().toString().substring(0,10);
    let endDate = $("#endUploadDate").val().toString().substring(0,10);
    //获取图形大小系数
    let imgCoeff = $("#imgCoeff").val();
    let valiNumber = /^[+]?[0-9]+(\.[0-9]+)?$/;   //验证数字
    if (!valiNumber.test(Number(imgCoeff))) {
        alert("折线图系数请输入数字");
        return;
    }
    if (Number(imgCoeff) <= 0) {
        alert("折线图系数值应大于0！");
        return;
    }

    // //获取图形大小系数
    // let imgSizeCoeff = $("#imgSizeCoeff").val();
    //
    // if (!valiNumber.test(Number(imgSizeCoeff))) {
    //     alert("折线图形大小系数请输入数字且值大于0.001小于10000！");
    //     return;
    // }
    // if (Number(imgSizeCoeff) <= 0.001) {
    //     alert("折线图形大小系数请输入数字且值大于0.001小于10000！");
    //     return;
    // }
    // if (Number(imgSizeCoeff) >= 10000) {
    //     alert("折线图形大小系数请输入数字且值大于0.001小于10000！");
    //     return;
    // }
    showOperTips("loadingDataDiv", "loadContentId", "正在生成动态覆盖图");
    $.ajax({
        url: '/api/dynamicCoverageMapPage/getDynaCoverageDataForAction',
        data: {
            'cityId': cityId,
            'cellId': cellId,
            'startDate': startDate,
            'endDate': endDate
            // 'imgCoeff': imgCoeff
            // 'imgSizeCoeff': imgSizeCoeff
        },
        dataType: 'json',
        type: 'post',
        success: function (data) {
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
                    console.log(curvePoints_12[0]);
                    console.log(pointArray_12);
                    let title = "【" + cellId + "】的动态覆盖数据列表";
                    $("#title").text(title);
                    let html = "<tbody>";
                    if (pointArray_12.length > 4) {
                        // drawBezier(pointArray_12);
                        //添加方向线

                        console.log(cellLon);
                        console.log(cellLat);
                        drawArrow(cellLon, cellLat, vecLng_12, vecLat_12, 0.002, dynaPolylineColor_12, curvePoints_12);
                        let element = popup.getElement();
                        $(element).popover('destroy');
                    } else {
                        alert("Each LinearRing of a Polygon must have 4 or more Positions");
                    }
                } else {
                    animateInAndOut("operInfo", 1000, 1000, 1000, "operTip", "该小区在搜索的时间段内没有数据！");
                    alert("该小区在搜索的时间段内没有数据！");
                }
            } else {
                animateInAndOut("operInfo", 1000, 1000, 1000, "operTip", "该小区在搜索的时间段内没有数据！");
                alert("该小区在搜索的时间段内没有数据！");
            }
        }
    });
    hideOperTips("loadingDataDiv");
}

function showDynaCoverage2(cellId, lng, lat) {
    console.log(cellId);
    if (!cellId || !lng || !lat) {
        return;
    }
    repaintCell = [cellId, lng, lat];
    //清空界面数据
    clearAll();
    //获取城市id
    var cityId = $("#cityId").val();
    //获取图形大小系数
    let imgCoeff = $("#imgCoeff").val();
    //获取日期范围
    let startDate = $("#begUploadDate").val().toString().substring(0,10);
    let endDate = $("#endUploadDate").val().toString().substring(0,10);

    var celllng,celllat;
    if(lng) {
        celllng = lng;
    } else {
        celllng = polygon._data.getLng();
    }
    if(lat) {
        celllat = lat;
    } else {
        celllat = polygon._data.getLat();
    }

    showOperTips("loadingDataDiv", "loadContentId", "正在生成动态覆盖图");

    $.ajax({
        url : '/api/dynamicCoverageMapPage/getDynaCoverageData2ForAction',
        data : {
            'cityId' : cityId,
            'cellId' : cellId,
            'startDate' : startDate,
            'endDate' : endDate,
            'imgCoeff': imgCoeff
        },
        dataType : 'json',
        type : 'post',
        success : function(data) {
            console.log(data)
            if(data != null) {
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
                    console.log(curvePoints_12[0]);
                    console.log(pointArray_12);
                    let title = "【" + cellId + "】的动态覆盖数据列表";
                    $("#title").text(title);
                    let html = "<tbody>";
                    if (pointArray_12.length > 0) {
                        // drawBezier(pointArray_12);
                        //添加方向线

                        console.log(lng);
                        console.log(lat);
                        drawArrow(lng, lat, vecLng_12, vecLat_12, 0.002, dynaPolylineColor_12, curvePoints_12);
                        let element = popup.getElement();
                        $(element).popover('destroy');
                    } else {
                        console.log(pointArray_12.length)
                        alert("Each LinearRing of a Polygon must have 4 or more Positions");
                    }
                } else {
                    hideOperTips("loadingDataDiv");
                    alert("该小区在搜索的时间段内没有数据！");
                }
            } else {
                hideOperTips("loadingDataDiv");
                alert("该小区在搜索的时间段内没有数据！");
            }
        },
        error : function(xhr, textstatus, e) {

        },
        complete : function() {
            hideOperTips("loadingDataDiv");
        }
    });
}


/**
 * I N干扰小区连线：主小区被所有小区检测的连线。
 */
function InInterCellLine(cell, lng, lat) {
    if (!cell || !lng || !lat) {
        return;
    }
    thisCellOverlay.getSource().clear();
    interCellOverlay.getSource().clear();
    lineOverlay.getSource().clear();
    // 清空详情表格
    clearDetail();
    // 清空界面数据
    clearPopup();
    //获取城市id
    let cityId = $("#cityId").val();
    //获取日期范围
    let dateSelect = $("#begUploadDate").val().toString().substring(0,10);
    if ($.trim(dateSelect).length === 0 || $.trim(dateSelect) === "-1") {
        alert("日期不能为空");
        return;
    }

    showOperTips("loadingDataDiv", "loadContentId", "正在获取IN干扰数据");
    $.ajax({
        url: '/api/dynamicCoverageMapPage/getLteDynamicCoverageShapeData',
        data: {
            'cityId': cityId,
            'lteCellId': cell,
            'dates': dateSelect,
            'inOrOut': 'in'
        },
        dataType: 'json',
        type: 'post',
        success: function (data) {
            console.log(data)
            if (data !== null && data.length > 0) {
                let ncellArr = [];
                let lineFeatureArr = [];
                for (let i = 0; i < data.length; i++) {
                    let ncellId = data[i]['NCELL_ID'];
                    let ncellLat = data[i]['NCELL_LAT'];
                    let ncellLon = data[i]['NCELL_LON'];
                    ncellArr.push(ncellId);
                    //连线
                    let feature = drawLineBetweenPoints(lng, lat, ncellLon, ncellLat, {
                        'strokeColor': 'red',
                        "strokeWeight": 1
                    });
                    lineFeatureArr.push(feature);
                }
                lineOverlay.getSource().addFeatures(lineFeatureArr);
                // 渲染主小区
                renderCellShape(cell);
                // 渲染邻区
                renderNcellsShape(ncellArr);
            } else {
                alert("该小区在搜索的时间段内没有数据！");
            }
        }
    });
    hideOperTips("loadingDataDiv");
}

/**
 *OUT干扰小区连线：主小区检测到所有邻小区连线。
 */
function OutInterCellLine(cell, lng, lat) {
    if (!cell || !lng || !lat) {
        return;
    }
    thisCellOverlay.getSource().clear();
    interCellOverlay.getSource().clear();
    lineOverlay.getSource().clear();
    // 清空详情表格
    clearDetail();
    //清空界面数据
    clearPopup();
    //获取城市id
    let cityId = $("#cityId").val();
    //获取日期范围
    let dateSelect = $("#begUploadDate").val().toString().substring(0,10)
    if ($.trim(dateSelect).length === 0 || $.trim(dateSelect) === "-1") {
        alert("日期不能为空");
        return;
    }

    showOperTips("loadingDataDiv", "loadContentId", "正在获取OUT干扰数据");
    $.ajax({
        url: '/api/dynamicCoverageMapPage/out',
        data: {
            'cityId': cityId,
            'lteCellId': cell,
            'dates': dateSelect
        },
        dataType: 'json',
        type: 'post',
        success: function (data) {
            if (data !== null && data.length > 0) {
                let ncellArr = [];
                let lineFeatureArr = [];
                for (let i = 0; i < data.length; i++) {
                    let ncellId = data[i]['NCELL_ID'];
                    let ncellLon = data[i]['NCELL_LON'];
                    let ncellLat = data[i]['NCELL_LAT'];
                    ncellArr.push(ncellId);
                    //连线
                    let feature = drawLineBetweenPoints(lng, lat, ncellLon, ncellLat, {
                        'strokeColor': 'red',
                        "strokeWeight": 1
                    });
                    lineFeatureArr.push(feature);
                }
                lineOverlay.getSource().addFeatures(lineFeatureArr);
                // 渲染主小区
                renderCellShape(cell);
                // 渲染邻区
                renderNcellsShape(ncellArr);
            } else {
                alert("该小区在搜索的时间段内没有数据！");
            }
        }
    });
    hideOperTips("loadingDataDiv");
}

/**
 * 获取干扰小区并绘制干扰小区
 * @param ncells
 */
function renderNcellsShape(ncells) {
    let ncellStr = "";
    for (let i = 0, idx; idx = ncells[i++];) {
        ncellStr += "'" + idx + "',";
    }
    ncellStr = ncellStr.substring(0, ncellStr.length - 1);

    let filter = "CELL_ID in (" + ncellStr + ")";
    $.ajax({
        url: wfsCellUrl,
        data: {
            service: 'WFS',
            request: 'GetFeature',
            typeName: cellLayers,
            outputFormat: 'application/json',
            'CQL_FILTER': filter
        }
    }).then(function (response) {
        let features = new ol.format.GeoJSON().readFeatures(response);
        if (features.length) {
            interCellOverlay.getSource().addFeatures(features);
        }
    }).catch(function (err) {
        console.error(err);
    });
}

/**
 * 渲染主小区
 * @param cellId
 */
function renderCellShape(cellId) {
    let filter = "CELL_ID = '" + cellId + "'";
    $.ajax({
        url: wfsCellUrl,
        data: {
            service: 'WFS',
            request: 'GetFeature',
            typeName: cellLayers,
            outputFormat: 'application/json',
            'CQL_FILTER': filter
        },
        method: 'post'
    }).then(function (response) {
        let features = new ol.format.GeoJSON().readFeatures(response);
        if (features.length) {
            for (let i = 0, idx; idx = features[i++];) {
                thisCellOverlay.getSource().addFeature(idx);
            }
        }
    }).catch(function (err) {
        console.error(err);
    });
}

/**
 * 添加带有箭头方向的箭线
 */
function drawArrow(cellLon, cellLat, vecLng, vecLat, ratio, color,points) {
    let difflng = vecLng - cellLon;
    let difflat = vecLat - cellLat;
    let r = Math.sqrt(difflng * difflng + difflat * difflat);
    let conV = difflng / r;
    let sinV = difflat / r;

    var coordinates = [];
    points.forEach(function(point) {
        var temp = ol.proj.transform([point.lng,point.lat], 'EPSG:4326', 'EPSG:3857');
        coordinates.push(temp);
    });
    var styles = [
        new ol.style.Style({
            stroke: new ol.style.Stroke({
                color: 'blue',
                width: 5
            }),
            fill: new ol.style.Fill({
                color: 'rgba(0, 0, 255, 0.1)'
            })
        }),
        new ol.style.Style({
            image: new ol.style.Circle({
                radius: 3,
                fill: new ol.style.Fill({
                    color: 'orange'
                })
            }),
            geometry: function(feature) {
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
        'features': [  {
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
/**
 * 画出百度地图线的箭头
 * @param polyline  需要画箭头的线
 * @param length  长度
 * @param angleValue  箭头角度
 */
function addArrow(polyline,length,angleValue,type){ //绘制箭头的函数
    var linePoint=polyline.getPath();//线的坐标串
    var arrowCount=linePoint.length;
    for(var i =1;i<arrowCount;i++){ //在拐点处绘制箭头
        var pixelStart=map.pointToPixel(linePoint[i-1]);
        var pixelEnd=map.pointToPixel(linePoint[i]);
        var angle=angleValue;//箭头和主线的夹角
        var r=length; // r/Math.sin(angle)代表箭头长度
        var delta=0; //主线斜率，垂直时无斜率
        var param=0; //代码简洁考虑
        var pixelTemX,pixelTemY;//临时点坐标
        var pixelX,pixelY,pixelX1,pixelY1;//箭头两个点
        if(pixelEnd.x-pixelStart.x==0){ //斜率不存在是时
            pixelTemX=pixelEnd.x;
            if(pixelEnd.y>pixelStart.y){
                pixelTemY=pixelEnd.y-r;
            } else {
                pixelTemY=pixelEnd.y+r;
            }
            //已知直角三角形两个点坐标及其中一个角，求另外一个点坐标算法
            pixelX=pixelTemX-r*Math.tan(angle);
            pixelX1=pixelTemX+r*Math.tan(angle);
            pixelY=pixelY1=pixelTemY;
        }
        //斜率存在时
        else {
            delta=(pixelEnd.y-pixelStart.y)/(pixelEnd.x-pixelStart.x);
            param=Math.sqrt(delta*delta+1);

            //第二、三象限
            if((pixelEnd.x-pixelStart.x)<0) {
                pixelTemX=pixelEnd.x+ r/param;
                pixelTemY=pixelEnd.y+delta*r/param;
            }
            //第一、四象限
            else {
                pixelTemX=pixelEnd.x- r/param;
                pixelTemY=pixelEnd.y-delta*r/param;
            }
            //已知直角三角形两个点坐标及其中一个角，求另外一个点坐标算法
            pixelX=pixelTemX+ Math.tan(angle)*r*delta/param;
            pixelY=pixelTemY-Math.tan(angle)*r/param;

            pixelX1=pixelTemX- Math.tan(angle)*r*delta/param;
            pixelY1=pixelTemY+Math.tan(angle)*r/param;
        }

        var pointArrow=map.pixelToPoint(new BMap.Pixel(pixelX,pixelY));
        var pointArrow1=map.pixelToPoint(new BMap.Pixel(pixelX1,pixelY1));
        if(type=="1") {
            dynaArrow_12 = new BMap.Polyline([pointArrow,linePoint[i],pointArrow1],
                {strokeColor:dynaPolylineColor_12, strokeWeight:3, strokeOpacity:0.5});
            map.addOverlay(dynaArrow_12);
        }
//		else if(type=="2") {
//			dynaArrow_3 = new BMap.Polyline([pointArrow,linePoint[i],pointArrow1],
//					{strokeColor:dynaPolylineColor_3, strokeWeight:3, strokeOpacity:0.5});
//			map.addOverlay(dynaArrow_3);
//		}

    }
}

/**
 * 只包括数字和逗号
 */
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

/**
 * 贝塞尔曲线
 * @param coords 经纬度二维数组
 */
function drawBezier(coords) {
    coords.push(coords[0]);
    let line = turf.lineString(coords);
    let curved = turf.bezierSpline(line);
    curved.properties = {stroke: 'rgba(255, 0, 0, 0.2)', fill: 'rgba(255, 0, 0, 0.2)'};
    let collection = turf.featureCollection([curved]);
    let features = new ol.format.GeoJSON().readFeatures(collection, {
        dataProjection: 'EPSG:4326',
        featureProjection: 'EPSG:4326'
    });
    bezierOverlay.getSource().addFeatures(features);
}

/**
 * 获取MR测量日期通过不同的地市
 */
function findByAreaIdAndDataType() {

    $("#dateSelect").empty();
    $('#search').empty();
    $("#search_to").empty();
    $("#mrDataTip").empty();
    let cityId = $("#cityId").find("option:selected").val();
    $.ajax({
        url: contextPath + '/dynamicCoverageMapPage/findByAreaIdAndDataType',
        data: {
            'cityId': cityId,
            'dataType': 'MR'
        },
        dataType: 'json',
        type: 'post',
        success: function (data) {
            if (data.length !== 0) {
                let optPartHtml = "";
                let optHtml = "";
                for (let i = 0; i < data.length; i++) {
                    let one = data[i];
                    if (i <= 4) {
                        optPartHtml += "<option value='" + one['DATA_DATE'] + "'>" + one['DATA_DATE'] + "</option>";
                    }
                    optHtml += "<option value='" + one['DATA_DATE'] + "'>" + one['DATA_DATE'] + "</option>";
                }
                $('#myModalLabel1').html("可选" + (data.length) + "个,双击可添加");
                $('#myModalLabel2').html("已选0个，双击可删除");
                $("#dateSelect").append(optPartHtml);
                $("#search").append(optHtml);
            } else {
                $('#myModalLabel2').html("已选0个，双击可删除");
                $('#myModalLabel1').html("可选" + (data.length) + "个,双击可添加");
                $("#mrDataTip").text("无相应的MR数据,请先MR数据入库!");
            }
        }
    });
}

function popUp() {

    let thisDom;
    //日期多选
    $("#dateBtn").click(function () {
        $("#search_to").empty();
        $('#match').val('');
        $("#myModalLabel").text("选择日期");
        thisDom = "dateSelect";
        let dateStr = $("#dateSelect").val();
        let dateVal = [];
        if (dateStr !== null) {
            dateVal = dateStr.split(",");
        }
        for (let i = 0; i < dateVal.length; i++) {
            if (dateVal[i] !== -1) {
                $('#search_to').append("<option>" + dateVal[i] + "</option>");
            }
        }
        $('#myModalLabel2').html("已选" + ($('#search_to').find('option').length) + "个，双击可删除");

    });
    //确定按钮
    $('#ensure').click(function () {
        //$("#" + thisDom).children().remove();
        let html = "";
        $("#search_to").find("option").each(function (index) {
            if (index === 0) {
                html += $(this).val();
            } else {
                html += "," + $(this).val();
            }
        });
        //alert("html="+html+","+html.length+","+"".length);
        if (html.length === 0) {
            $("#" + thisDom).val(0);
        } else {
            let arr = html.split(",");
            //alert(arr.length);
            if (arr.length === 1 && thisDom !== 'cellSelect') {
                $("#search").find("option").each(function () {
                    if (html === $(this).val()) {
                        $("#" + thisDom + " option[value='" + $(this).val() + "']").attr("selected", "selected");
                    }
                })
            } else {
                $("#" + thisDom).prepend("<option selected='selected' style='display:none'>" + html + "</option>");
            }
        }
        $('#myModal').modal('hide');
    })
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
    // btn.button('loading');
    var band_type;
    var area_id;

    band_type = "BAND_TYPE in('D','E','F')";

    var cityId = $("#cityId").val();
    area_id = " and AREA_ID=" + cityId;
    var filter = band_type + area_id;

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
        // btn.button("hidden");
    }, 2000);
}
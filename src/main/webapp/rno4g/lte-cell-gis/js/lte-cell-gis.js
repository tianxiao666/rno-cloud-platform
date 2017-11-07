var map, cellLayer, clickedCellLayer, nCellLayer, thisCellLayer, lineLayer;
var popup;
var redStyle, orangeStyle, blackStyle;

$(function () {
    $(".dialog").draggable();
    $("#trigger").css("display", "none");

    //默认右侧栏关闭
    $(".resource_list_box").css("display", "none");
    $(".switch_hidden").show();
    $(".resource_list_icon").css("right", "0");

    $(".switch").click(function () {
        $(this).hide();
        $(".switch_hidden").show();
        $(".resource_list_icon").animate({
            right: '0px'
        }, 'fast');
        $(".resource_list_box").hide("fast");
    });
    $(".switch_hidden").click(function () {
        $(this).hide();
        $(".switch").show();
        $(".resource_list_icon").animate({
            right: '286px'
        }, 'fast');
        $(".resource_list_box").show("fast");
    });
    $(".zy_show").click(function () {
        $(".search_box_alert").slideToggle("fast");
    });

    //禁止右键菜单
    $("#map").bind("contextmenu", function() {
        return false;
    });

    var baseLayer = new ol.layer.Tile({
        source: new ol.source.XYZ({
            url: 'http://rno-omt.hgicreate.com/styles/rno-omt/{z}/{x}/{y}.png',
            // url: 'http://rno-map.hgicreate.com/tiles/osm/{z}/{x}/{y}.png',
            zIndex: 1
        })
    });

    //主小区图层
    thisCellLayer = new ol.layer.Vector({
        source: new ol.source.Vector(),
        zIndex: 5
    });

    //邻区图层
    nCellLayer = new ol.layer.Vector({
        source: new ol.source.Vector(),
        zIndex: 3
    });

    //线图层
    lineLayer = new ol.layer.Vector({
        source: new ol.source.Vector(),
        zIndex: 6
    });

    //点击小区图层
    clickedCellLayer = new ol.layer.Vector({
        source: new ol.source.Vector(),
        zIndex: 4
    });

    // 小区名图层
    var textImageTile = new ol.layer.Tile({
        source: new ol.source.TileWMS({
            url: 'http://rno-gis.hgicreate.com/geoserver/wms',
            params: {
                'FORMAT': 'image/png',
                'SRS': 'EPSG:4326',
                'tiled': true,
                'LAYERS': 'rnoprod:RNO_LTE_CELL_CENTROID'
            },
            serverType: 'geoserver'
        }),
        opacity: 0.8
    });

    //点击小区专用
    redStyle = new ol.style.Style({
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

    //邻区专用
    orangeStyle = new ol.style.Style({
        stroke: new ol.style.Stroke({
            // 设置线条颜色
            color: 'yellow',
            size: 5
        }),
        fill: new ol.style.Fill({
            // 设置填充颜色与不透明度
            color: 'rgba(255, 165, 0, 1.0)'
        })
    });

    //主小区专用
    blackStyle = new ol.style.Style({
        stroke: new ol.style.Stroke({
            // 设置线条颜色
            color: 'yellow',
            size: 5
        }),
        fill: new ol.style.Fill({
            // 设置填充颜色与不透明度
            color: 'rgba(0, 0, 0, 1.0)'
        })
    });

    //右键菜单
    var contextmenu_items = [
        {
            text: '显示邻区',
            data: 1,
            callback: showNcell
        }
    ];

    var contextmenu = new ContextMenu({
        width : 120,
        items : contextmenu_items
    });

    $("#districtId").change(function () {
        var lon = parseFloat($(this).find("option:checked").attr("data-lon"));
        var lat = parseFloat($(this).find("option:checked").attr("data-lat"));
        if (map === undefined) {
            map = new ol.Map({
                target: 'map',
                layers: [clickedCellLayer, baseLayer, nCellLayer, thisCellLayer, lineLayer],
                view: new ol.View({
                    projection: 'EPSG:4326',
                    center: [lon, lat],
                    zoom: 16
                })
            });

            popup = new ol.Overlay({element: document.getElementById('popup')});
            map.addOverlay(popup);

            // 右键菜单打开之前，判断是否在 feature 上，如果不是则禁止右键菜单
            contextmenu.on('beforeopen', function (e) {
                var feature = map.forEachFeatureAtPixel(e.pixel, function (feature) {
                    return feature;
                });

                if (feature) {
                    contextmenu.enable();
                } else {
                    contextmenu.disable();
                }
            });

            // 打开右键菜单
            contextmenu.on('open', function (e) {
                var element = popup.getElement();
                $(element).popover('destroy');
                var feature = map.forEachFeatureAtPixel(e.pixel, function (feature) {
                    return feature;
                });
                // console.log(feature);
                if (feature) {
                    contextmenu.clear();
                    contextmenu.extend(contextmenu_items);
                }
            });
            map.addControl(contextmenu);

            map.on('singleclick', function (evt) {

                var element = popup.getElement();
                $(element).popover('destroy');

                var view = map.getView();
                var url = cellLayer.getSource().getGetFeatureInfoUrl(evt.coordinate, view.getResolution(), view.getProjection(), {
                    'INFO_FORMAT': 'text/javascript',
                    'FEATURE_COUNT': 50
                });

                if (url) {
                    var parser = new ol.format.GeoJSON();
                    $.ajax({
                        url: url,
                        dataType: 'jsonp',
                        jsonpCallback: 'parseResponse'
                    }).then(function (response) {
                        var allFeatures = parser.readFeatures(response);
                        var allFeatureNum = allFeatures.length;

                        if (allFeatureNum) {
                            // 高亮 Features
                            clickedCellLayer.getSource().clear();
                            clickedCellLayer.getSource().addFeatures(allFeatures);

                            var content = '<table id="cellTable" class="table custom">';
                            content += '<thead style="white-space: nowrap"><th>小区ID</th><th>小区名称</th><th>PCI</th></thead>';
                            content += '<tbody>';
                            // 获取多个重叠 feature
                            for (var i = 0; i < allFeatureNum; i++) {
                                var feature = allFeatures[i];
                                console.log(feature);

                                content += '<tr style="word-break:break-all" onclick="addColor(this, true)">';
                                content += '<td style="display:none">' + i + '</td>';
                                content += '<td style="white-space: nowrap">' + feature.get('CELL_ID') + '</td>';
                                content += '<td>' + feature.get('CELL_NAME') + '</td>';
                                content += '<td style="white-space: nowrap">' + feature.get('PCI') + '</td>';
                                content += '</tr>';

                                // 设置 feature 的样式
                                feature.setStyle(redStyle);
                            }
                            content += '</tbody></table>';

                            popup.setPosition(evt.coordinate);
                            $(element).popover({
                                'placement': 'auto',
                                'animation': false,
                                'html': true,
                                'content': content
                            });
                            $(element).popover('show');

                            $('#cellTable tbody tr').click(function () {
                                var index = $(this).find('td:first').text();
                                var feature = allFeatures[index];
                                $.ajax({
                                    url: "/api/lte-cell-gis/getCellByCellId",
                                    dataType: "json",
                                    data: {
                                        'cellId' : feature.get('CELL_ID')
                                    },
                                    async: false,
                                    success: function (data) {
                                        var cell = data[0];
                                        $("#showCellLabelId").text(cell.cellId);
                                        $("#showCellNameId").text(cell.cellName);
                                        $("#showManufacturer").text(cell.manufacturer);
                                        $("#showBandType").text(cell.bandType);
                                        $("#showBandIndicator").text(cell.bandIndicator);
                                        $("#showEarfcn").text(cell.earfcn);
                                        $("#showPci").text(cell.pci);
                                        $("#showCoverType").text(cell.coverType);
                                        $("#showCoverScene").text(cell.coverScene);
                                        $("#showLongitude").text(cell.longitude);
                                        $("#showLatitude").text(cell.latitude);
                                        $("#showAzimuth").text(cell.azimuth);
                                        $("#showEDowntilt").text(cell.eDowntilt?cell.eDowntilt:0);
                                        $("#showMDowntilt").text(cell.mDowntilt?cell.mDowntilt:0);
                                        $("#showTotalDowntilt").text(cell.totalDowntilt?cell.totalDowntilt:0);
                                        $("#showAntennaHeight").text(cell.antennaHeight?cell.antennaHeight:0);
                                        $("#showRemoteCell").text(cell.remoteCell);
                                        $("#showRelatedParam").text(cell.relatedParam);
                                        $("#showRelatedResouce").text(cell.relatedResouce);
                                        $("#showStationSpace").text(cell.stationSpace);
                                    }
                                });
                            });
                        } else {
                            console.log('No result');
                        }
                    });
                }
            });
        } else {
            //map.getView().setCenter(ol.proj.transform([lon, lat], 'EPSG:4326', 'EPSG:3857'));
            map.getView().animate({
                center: [ parseFloat(lon), parseFloat(lat) ],
                duration: 2000
            });
        }
    });

    //初始区域
    initAreaSelectors({selectors: ["provinceId", "cityId", "districtId"], coord: true});

    $("#loadGisCell").click(function () {
        var cityId = parseInt($("#cityId").find("option:checked").val());
        map.removeLayer(cellLayer);
        cellLayer = new ol.layer.Tile({
            zIndex: 2,
            source: new ol.source.TileWMS({
                url: 'http://rno-gis.hgicreate.com/geoserver/rnoprod/wms',
                params: {
                    'FORMAT': 'image/png',
                    'VERSION': '1.1.1',
                    tiled: true,
                    STYLES: '',
                    LAYERS: 'rnoprod:RNO_LTE_CELL_GEOM',
                    CQL_FILTER: "AREA_ID=" + cityId
                }
            }),
            opacity: 0.5
        });
        map.addLayer(cellLayer);
    });

    $("#showCellName").click(function () {
        if ($(this).text() === "显示小区名字") {
            $(this).text("关闭小区名字");
            map.addLayer(textImageTile);
        } else {
            $(this).text("显示小区名字");
            map.removeLayer(textImageTile);
        }
    })
});

//点击popup表格，添加选中行的背景色
function addColor(t, isShowRightBox) {
    if(isShowRightBox) {
        $(".switch_hidden").trigger("click");
    }else {
        $(".switch").trigger("click");
    }
    $(t).siblings().removeClass('custom-bg');
    $(t).addClass('custom-bg');
}

//显示邻区
var showNcell = function getNcell(evt) {
    thisCellLayer.getSource().clear();
    nCellLayer.getSource().clear();
    lineLayer.getSource().clear();
    var element = popup.getElement();
    $(element).popover('destroy');

    var view = map.getView();
    var url = cellLayer.getSource().getGetFeatureInfoUrl(evt.coordinate, view.getResolution(), view.getProjection(), {
        'INFO_FORMAT': 'text/javascript',
        'FEATURE_COUNT': 50
    });

    if (url) {
        var parser = new ol.format.GeoJSON();
        $.ajax({
            url: url,
            dataType: 'jsonp',
            jsonpCallback: 'parseResponse'
        }).then(function (response) {
            var allFeatures = parser.readFeatures(response);
            var allFeatureNum = allFeatures.length;

            if (allFeatureNum) {
                // 高亮 Features
                clickedCellLayer.getSource().clear();
                clickedCellLayer.getSource().addFeatures(allFeatures);

                var content = '<table id="cellTable1" class="table custom">';
                content += '<thead style="white-space: nowrap"><th>小区ID</th><th>小区名称</th><th>PCI</th></thead>';
                content += '<tbody>';
                // 获取多个重叠 feature
                for (var i = 0; i < allFeatureNum; i++) {
                    var feature = allFeatures[i];
                    console.log(feature);

                    content += '<tr style="word-break:break-all" onclick="addColor(this, false)">';
                    content += '<td style="display:none">' + i + '</td>';
                    content += '<td style="white-space: nowrap">' + feature.get('CELL_ID') + '</td>';
                    content += '<td>' + feature.get('CELL_NAME') + '</td>';
                    content += '<td style="white-space: nowrap">' + feature.get('PCI') + '</td>';
                    content += '</tr>';

                    // 设置 feature 的样式
                    feature.setStyle(redStyle);
                }
                content += '</tbody></table>';

                popup.setPosition(evt.coordinate);
                $(element).popover({
                    'placement': 'auto',
                    'animation': false,
                    'html': true,
                    'content': content
                });
                $(element).popover('show');

                $('#cellTable1 tbody tr').click(function () {
                    $(element).popover('destroy');
                    $("#loading").show();
                    var index = $(this).find('td:first').text();
                    var cellId = allFeatures[index].get('CELL_ID');
                    $.ajax({
                        url: "/api/lte-cell-gis/getNcellByCellId",
                        dataType: "json",
                        data: {
                            'cellId' : cellId
                        },
                        async: false,
                        success: function (data) {
                            if(data!='') {
                                paintNcell(cellId, data);
                            }else {
                                $("#loading").css("display", "none");
                                showInfoInAndOut('info', '没有找到邻区数据！');
                            }
                            //console.log(data);
                        }
                    });
                });
            } else {
                console.log('No result');
            }
        });
    }
}

//提示信息淡入淡出
function showInfoInAndOut(div, info) {
    $("#" + div).html(info);
    $("#" + div).fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

//绘制邻区
function paintNcell(cellId, cells) {
    var ncellStr = "'" + cellId + "',";
    $.each(cells, function (index, value) {
        ncellStr += "'" + value + "',";
    });
    //console.log(ncellStr);
    var cityId = parseInt($("#cityId").find("option:checked").val());
    var filter = encodeURIComponent("CELL_ID in (" + ncellStr.substring(0, ncellStr.length-1) + ")");
    //console.log(filter);

    var url = 'http://rno-gis.hgicreate.com/geoserver/rnoprod/ows?service=WFS&version=1.1.1' +
        '&request=GetFeature&typeName=rnoprod:RNO_LTE_CELL_GEOM&maxFeatures=50&' +
        'outputFormat=text%2Fjavascript&CQL_FILTER=' + filter;
    //console.log(url);
    var parser = new ol.format.GeoJSON();
    $.ajax({
        url : url,
        dataType : 'jsonp',
        jsonpCallback : 'parseResponse'
    }).then(function(response) {
        var features = parser.readFeatures(response);
        var cellCoors = [];
        var ncellCoors = [];
        //console.log(features.length);
        if (features.length) {
            for(var m = 0; m < features.length; m++) {
                var onefeature = features[m];
                if(onefeature.get('CELL_ID')==cellId){
                    cellCoors = [onefeature.get('LONGITUDE'), onefeature.get('LATITUDE')];
                    onefeature.setStyle(blackStyle);
                    thisCellLayer.getSource().addFeature(onefeature);
                }else{
                    ncellCoors.push([onefeature.get('LONGITUDE'), onefeature.get('LATITUDE')]);
                    onefeature.setStyle(orangeStyle);
                    nCellLayer.getSource().addFeature(onefeature);
                }
            }
            drawLine(cellCoors, ncellCoors);
        }
        $("#loading").css("display", "none");
    });
}

//绘制主小区与邻区之间的连线
function drawLine(cellCoors, ncellCoors){
    $.each(ncellCoors, function (index, value) {
        var line = new ol.geom.LineString([cellCoors, value]);
        var feature = new ol.Feature({
            geometry: line,
        });
        feature.setStyle(new ol.style.Style({
            stroke: new ol.style.Stroke({
                color: 'green',
                width: 1,
            }),
        }));
        lineLayer.getSource().addFeature(feature);
    });
}
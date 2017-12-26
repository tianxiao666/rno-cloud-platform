var map, cellLayer,busyCellLayer,idleNcellLayer,clickedCellLayer,lineLayer;
var busyCells;
var busyCellRedStyle,idleNcellOrangeLayer;
var busyCellId,busyCellName;
$(function () {

    $(".draggable").draggable();
    $("#trigger").css("display", "none");

    $(".switch").click(function () {
        $(this).hide();
        $(".switch_hidden").show();
        $(".resource_list_icon").animate({
            right: '0px'
        }, 'fast');
        $(".resource_list_box").hide("fast");
    })
    $(".switch_hidden").click(function () {
        $(this).hide();
        $(".switch").show();
        $(".resource_list_icon").animate({
            right: '286px'
        }, 'fast');
        $(".resource_list_box").show("fast");
    })
    $(".zy_show").click(function () {
        $(".search_box_alert").slideToggle("fast");
    });

    //禁止右键菜单
    $("#map").bind("contextmenu", function () {
        return false;
    });

    //繁忙小区专用
    busyCellRedStyle = new ol.style.Style({
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

    //闲邻区专用
    idleNcellOrangeLayer = new ol.style.Style({
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

    var baseLayer = new ol.layer.Tile({
        source: new ol.source.XYZ({
            url: 'http://rno-omt.hgicreate.com/styles/rno-omt/{z}/{x}/{y}.png',
            zIndex: 1
        })
    });

    //忙小区图层
    busyCellLayer = new ol.layer.Vector({
        source: new ol.source.Vector(),
        zIndex: 3
    });

    //闲邻区图层
    idleNcellLayer = new ol.layer.Vector({
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
                'LAYERS': 'rnoprod:RNO_GSM_CELL_CENTROID'
            },
            serverType: 'geoserver'
        }),
        opacity: 0.8
    });

    //线图层
    lineLayer = new ol.layer.Vector({
        source: new ol.source.Vector(),
        zIndex: 5
    });

    //右键菜单
    var contextmenu_items = [
        {
            text: '显示闲邻区',
            data: 1,
            callback: showIdleNcell
        }
    ];

    //点击小区图层
    clickedCellLayer = new ol.layer.Vector({
        source: new ol.source.Vector(),
        zIndex: 2
    });

    var contextmenu = new ContextMenu({
        width: 120,
        items: contextmenu_items
    });

    $("#areaId").change(function () {
        var lon = parseFloat($(this).find("option:checked").attr("data-lon"));
        var lat = parseFloat($(this).find("option:checked").attr("data-lat"));
        if (map === undefined) {
            map = new ol.Map({
                target: 'map',
                layers: [baseLayer,busyCellLayer,clickedCellLayer,idleNcellLayer,lineLayer],
                view: new ol.View({
                    projection: 'EPSG:4326',
                    center: [lon, lat],
                    zoom: 16
                })
            });

            popup = new ol.Overlay({element: document.getElementById('popup')});
            map.addOverlay(popup);

            map.on('singleclick', function (evt) {
                var element = popup.getElement();
                $(element).popover('destroy');

                var view = map.getView();
                var url = cellLayer.getSource().getGetFeatureInfoUrl(evt.coordinate, view.getResolution(),
                    view.getProjection(), {
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
                            content += '<thead style="white-space: nowrap"><th>小区ID</th><th>小区名称</th></thead>';
                            content += '<tbody>';
                            // 获取多个重叠 feature
                            var cellIds = "";
                            for (var i = 0; i < allFeatureNum; i++) {
                                var feature = allFeatures[i];
                                // 设置 feature 的样式
                                // feature.setStyle(orangeStyle);
                                cellIds += allFeatures[i].get('CELL_ID') + ",";
                                content += '<tr style="word-break:break-all" class="custom-content">';
                                content += '<td style="white-space: nowrap">' + allFeatures[i].get('CELL_ID')+ '</td>';
                                content += '<td>' + allFeatures[i].get('CELL_NAME') + '</td>';
                                content += '</tr>';
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
                        }
                    })
                }
            });
        } else {
            map.getView().animate({
                center:[lon, lat],
                duration:2000
            });
        }
    });

    //初始区域
    initAreaSelectors({selectors: ["provinceId", "cityId", "areaId"], coord: true});

    // 加载小区
    $("#loadGisCell").click(function () {
        var cityId = parseInt($("#cityId").find("option:checked").val());
        map.removeLayer(cellLayer);
        cellLayer = new ol.layer.Tile({
            zIndex : 2,
            source : new ol.source.TileWMS({
                url : 'http://rno-gis.hgicreate.com/geoserver/rnoprod/wms',
                params : {
                    'FORMAT' : 'image/png',
                    'VERSION' : '1.1.1',
                    tiled : true,
                    STYLES : '',
                    LAYERS : 'rnoprod:RNO_GSM_CELL_GEOM',
                    CQL_FILTER : "AREA_ID=" + cityId
                }
            }),
            opacity : 0.5
        });
        map.addLayer(cellLayer);
    });

    //小区名称显示
    $("#loadGisCellName").click(function () {
        if ($(this).val() === "显示小区名字") {
            $(this).val("关闭小区名字");
            map.addLayer(textImageTile);
        } else {
            $(this).val("显示小区名字");
            map.removeLayer(textImageTile);
        }
    });

    $("#displayBusyCellBtn").click(function () {
        busyCellLayer.getSource().clear();
        $("#busyCount").text("");
        if(cellLayer===undefined){
            showInfoInAndOut("warn","请先加载小区！");
        }else{
            $("#loading").css("display", "block");
            $("#busyCellDT tbody").html("");
            $.ajax({
                url:"/api/gsm-busy-cell-analysis/busy-cell",
                dataType: "json",
                data: {
                    'areaId' : parseInt($("#areaId").find("option:checked").val())
                },
                async:true,
                success:function (data) {
                    if(data===""||data.length===0||data===null){
                        $("#loading").css("display", "none");
                        showInfoInAndOut("success","该地区未找到繁忙小区！");
                        return;
                    }
                    busyCells = data;
                    $("#busyCount").text("找到繁忙小区" + data.length + "个");
                    var cellIds = "";
                    var cellId="";
                    $.each(data, function (key, value) {
                        cellId = "'" + value.cellId + "' ";
                        $("#busyCellDT").append("<tr onclick='addColor(this, false)'><td style='display: none'>" +
                            value.cellId + "</td><td>" + value.cellName + "</td></tr>");
                        cellIds += "'" + value.cellId + "' ,";
                    });
                    getCell(cellIds);
                    $("#busyCellDT tbody tr").click(function () {
                        var thisCell = $(this).find('td:eq(0)').text();
                        var element = popup.getElement();
                        $(element).popover('destroy');
                        var content = '<table id="cellTable1" class="table custom">';
                        content += '<thead style="white-space: nowrap"><th>小区ID</th><th>小区名称</th></thead>';
                        content += '<tbody>';
                        content += '<tr style="word-break:break-all" class="custom-content">';
                        content += '<td style="white-space: nowrap">' + thisCell+ '</td>';
                        content += '<td>' + $(this).find('td:eq(1)').text() + '</td>';
                        content += '</tr>';
                        content += '</tbody></table>';

                        var filter = encodeURIComponent("CELL_ID in ('" + thisCell + "')");

                        var url = 'http://rno-gis.hgicreate.com/geoserver/rnoprod/ows?service=WFS&version=1.1.1' +
                            '&request=GetFeature&typeName=rnoprod:RNO_GSM_CELL_GEOM&maxFeatures=50&' +
                            'outputFormat=text%2Fjavascript&CQL_FILTER=' + filter;
                        //console.log(url);
                        var parser = new ol.format.GeoJSON();
                        $.ajax({
                            url : url,
                            dataType : 'jsonp',
                            jsonpCallback : 'parseResponse'
                        }).then(function(response) {
                            var feature = parser.readFeatures(response)[0];
                            var cellCoor = [feature.get('LONGITUDE'), feature.get('LATITUDE')];

                            popup.setPosition(cellCoor);
                            map.getView().setCenter(cellCoor);
                        });

                        $(element).popover({
                            'placement': 'auto',
                            'animation': false,
                            'html': true,
                            'content': content
                        });
                        $(element).popover('show');
                    });
                    // 右键菜单打开之前
                    contextmenu.on('beforeopen', function (e) {
                        // console.log("e.coordinate====="+e.coordinate);
                        var element = popup.getElement();
                        $(element).popover('destroy');
                        var view = map.getView();
                        //contextmenu_items[contextmenu_items.length-1] = e.coordinate;
                        var url = cellLayer.getSource().getGetFeatureInfoUrl(
                            e.coordinate,
                            view.getResolution(),
                            view.getProjection(),
                            {
                                'INFO_FORMAT': 'text/javascript',
                                'FEATURE_COUNT': 50
                            }
                        );
                        if (url) {
                            var parser = new ol.format.GeoJSON();
                            $.ajax({
                                url: url,
                                dataType: 'jsonp',
                                jsonpCallback: 'parseResponse'
                            }).then(function (response) {
                                var features = parser.readFeatures(response);
                                if (features.length > 0) {
                                    // console.log(features.length);
                                    clickedCellLayer.getSource().clear();
                                    for (var i = 0; i < features.length; i++) {
                                        var feature = features[i];
                                        if(cellIds.indexOf(feature.get("CELL_ID"))>=0){
                                            contextmenu.enable();
                                            clickedCellLayer.getSource().addFeatures(features);
                                            busyCellId = feature.get("CELL_ID");
                                            busyCellName = feature.get("CELL_NAME");
                                            break;
                                        }else{
                                            contextmenu.disable();
                                        }
                                    }
                                } else {
                                    console.log('No result');
                                    contextmenu.disable();
                                }
                            });
                        } else {
                            contextmenu.disable();
                        }
                    });
                    //打开右键菜单
                    contextmenu.on('open', function () {
                        contextmenu.clear();
                        contextmenu.extend(contextmenu_items);
                    });
                    map.addControl(contextmenu);
                    $("#loading").css("display", "none");
                    showInfoInAndOut("success","繁忙小区表已生成！");
                }
            })
        }
    })
});

function getCell(cellIds) {
    var filter = encodeURIComponent("CELL_ID in (" + cellIds.substring(0, cellIds.length-1) + ")");

    var url = 'http://rno-gis.hgicreate.com/geoserver/rnoprod/ows?service=WFS&version=1.1.1' +
        '&request=GetFeature&typeName=rnoprod:RNO_GSM_CELL_GEOM&maxFeatures=50&' +
        'outputFormat=text%2Fjavascript&CQL_FILTER=' + filter;
    // console.log(url);
    var parser = new ol.format.GeoJSON();
    $.ajax({
        url : url,
        dataType : 'jsonp',
        jsonpCallback : 'parseResponse'
    }).then(function(response) {
        var features = parser.readFeatures(response);
        // console.log(features.length);
        if (features.length) {
            for(var m = 0; m < features.length; m++) {
                var onefeature = features[m];
                onefeature.setStyle(busyCellRedStyle);
                busyCellLayer.getSource().addFeature(onefeature);
            }
        }
    });
}

var showIdleNcell = function getIdleNcell(evt) {
    idleNcellLayer.getSource().clear();
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
            if (busyCellId!==null&&busyCellId!=="") {
                $("#loading").show();
                $("#idleBusyCellDT tbody").html("");
                $.ajax({
                    url: "/api/gsm-busy-cell-analysis/idle-ncell-detail",
                    dataType: "json",
                    data: {
                        'cellId': busyCellId,
                        'areaId' : parseInt($("#areaId").find("option:checked").val())
                    },
                    async: false,
                    success: function (data) {
                        if (data != '' && data != null) {
                            console.log(data);
                            $("#myTab li:eq(1)").addClass("active");
                            $("#myTab li:eq(1)").siblings().removeClass("active");
                            $("#idleCellList").addClass("active");
                            $("#idleCellList").siblings().removeClass("active");
                            $("#idleBusyCount").text("找到"+busyCellName+"小区的闲邻区" + data.length + "个");
                            var ncellIds = "";
                            $.each(data, function (key, value) {
                                $("#idleBusyCellDT").append("<tr onclick='addColor(this, false)'>"+
                                    "<td style='display: none'>" + value.cellId +
                                    "</td><td>" + value.cellName + "</td></tr>");
                                ncellIds += "'" + value.cellId + "' ,";
                            });
                            paintNcell(busyCellId, ncellIds);
                            $("#idleBusyCellDT tbody tr").click(function () {
                                var thisCell = $(this).find('td:eq(0)').text();
                                var element = popup.getElement();
                                $(element).popover('destroy');
                                var content = '<table id="cellTable1" class="table custom">';
                                content += '<thead style="white-space: nowrap"><th>小区ID</th><th>小区名称</th></thead>';
                                content += '<tbody>';
                                content += '<tr style="word-break:break-all" class="custom-content">';
                                content += '<td style="white-space: nowrap">' + thisCell+ '</td>';
                                content += '<td>' + $(this).find('td:eq(1)').text() + '</td>';
                                content += '</tr>';
                                content += '</tbody></table>';

                                var filter = encodeURIComponent("CELL_ID in ('" + thisCell + "')");

                                var url = 'http://rno-gis.hgicreate.com/geoserver/rnoprod/ows?service=WFS&version=1.1.1' +
                                    '&request=GetFeature&typeName=rnoprod:RNO_GSM_CELL_GEOM&maxFeatures=50&' +
                                    'outputFormat=text%2Fjavascript&CQL_FILTER=' + filter;
                                //console.log(url);
                                var parser = new ol.format.GeoJSON();
                                $.ajax({
                                    url : url,
                                    dataType : 'jsonp',
                                    jsonpCallback : 'parseResponse'
                                }).then(function(response) {
                                    var feature = parser.readFeatures(response)[0];
                                    var cellCoor = [feature.get('LONGITUDE'), feature.get('LATITUDE')];

                                    popup.setPosition(cellCoor);
                                    map.getView().setCenter(cellCoor);
                                });

                                $(element).popover({
                                    'placement': 'auto',
                                    'animation': false,
                                    'html': true,
                                    'content': content
                                });
                                $(element).popover('show');
                            });
                        } else {
                            $("#loading").css("display", "none");
                            showInfoInAndOut('info', '没有找到闲邻区数据！');
                        }
                        //console.log(data);
                    }
                });
            }else {
                console.log('No result');
            }
        })
    }
};

//绘制邻区
function paintNcell(cellId, cells) {
    var ncellStr = "'" + cellId + "',"+cells;
    // $.each(cells, function (index, value) {
    //     ncellStr += "'" + value + "',";
    // });
    //console.log(ncellStr);
    // var cityId = parseInt($("#cityId").find("option:checked").val());
    var filter = encodeURIComponent("CELL_ID in (" + ncellStr.substring(0, ncellStr.length - 1) + ")");
    //console.log(filter);

    var url = 'http://rno-gis.hgicreate.com/geoserver/rnoprod/ows?service=WFS&version=1.1.1' +
        '&request=GetFeature&typeName=rnoprod:RNO_GSM_CELL_GEOM&maxFeatures=50&' +
        'outputFormat=text%2Fjavascript&CQL_FILTER=' + filter;
    //console.log(url);
    var parser = new ol.format.GeoJSON();
    $.ajax({
        url: url,
        dataType: 'jsonp',
        jsonpCallback: 'parseResponse'
    }).then(function (response) {
        var features = parser.readFeatures(response);
        var cellCoors = [];
        var ncellCoors = [];
        //console.log(features.length);
        if (features.length) {
            for (var m = 0; m < features.length; m++) {
                var onefeature = features[m];
                if (onefeature.get('CELL_ID') === cellId) {
                    cellCoors = [onefeature.get('LONGITUDE'), onefeature.get('LATITUDE')];
                    onefeature.setStyle(busyCellRedStyle);
                    busyCellLayer.getSource().addFeature(onefeature);
                } else {
                    ncellCoors.push([onefeature.get('LONGITUDE'), onefeature.get('LATITUDE')]);
                    onefeature.setStyle(idleNcellOrangeLayer);
                    idleNcellLayer.getSource().addFeature(onefeature);
                }
            }
            drawLine(cellCoors, ncellCoors);
        }
        $("#loading").css("display", "none");
    });
}

//绘制主小区与邻区之间的连线
function drawLine(cellCoors, ncellCoors) {
    var line, feature;
    $.each(ncellCoors, function (index, value) {
        line = new ol.geom.LineString([cellCoors, value]);
        feature = new ol.Feature({
            geometry: line
        });
        feature.setStyle(new ol.style.Style({
            stroke: new ol.style.Stroke({
                color: 'green',
                width: 1
            })
        }));
        lineLayer.getSource().addFeature(feature);
    });
}

//点击popup表格，添加选中行的背景色
function addColor(t, isShowRightBox) {
    if(isShowRightBox) {
        //console.log($("#myTab li:eq(1)"));
        $("#myTab li:eq(1)").addClass("active");
        $("#myTab li:eq(1)").siblings().removeClass("active");
        $("#cellIndexDetail").addClass("active");
        $("#cellIndexDetail").siblings().removeClass("active");
    }
    $(t).siblings().removeClass('custom-bg');
    $(t).addClass('custom-bg');
}

//提示信息淡入淡出
function showInfoInAndOut(div, info) {
    $("#" + div).html(info);
    $("#" + div).fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

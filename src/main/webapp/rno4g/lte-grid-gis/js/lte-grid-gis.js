var map, tiled, gridLayer, gridTextLayer;

$(function () {
    $(".dialog").draggable();
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

    $("input[name='gridType']").change(function () {
        if($("input[name='gridType']:checked").val()){
            $("#loadGrid").attr('disabled', false);
            $("#exportGrid").attr('disabled', false);
        }else {
            $("#loadGrid").attr("disabled", true);
            $("#exportGrid").attr('disabled', true);
        }
    });

    var baseLayer = new ol.layer.Tile({
        source: new ol.source.XYZ({
            url: 'http://rno-omt.hgicreate.com/styles/rno-omt/{z}/{x}/{y}.png'
        })
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

    //网格图层
    gridLayer = new ol.layer.Vector({
        source: new ol.source.Vector(),
        zIndex: 4,
        opacity: 1.0
    });

    //网格文字图层
    gridTextLayer = new ol.layer.Vector({
        source: new ol.source.Vector(),
        zIndex: 5,
        opacity: 1.0
    });

    $("#districtId").change(function () {
        var lon = parseFloat($(this).find("option:checked").attr("data-lon"));
        var lat = parseFloat($(this).find("option:checked").attr("data-lat"));
        if (map === undefined) {
            map = new ol.Map({
                target: 'map',
                layers: [baseLayer, gridLayer, gridTextLayer],
                view: new ol.View({
                    center: ol.proj.fromLonLat([lon, lat]),
                    zoom: 12
                })
            });
        } else {
            map.getView().setCenter(ol.proj.transform([lon, lat], 'EPSG:4326', 'EPSG:3857'));
        }
    });

    initAreaSelectors({selectors: ["provinceId", "cityId", "districtId"], coord: true});

    $("#loadGisCell").click(function () {
        var cityId = parseInt($("#cityId").find("option:checked").val());
        map.removeLayer(tiled);
        tiled = new ol.layer.Tile({
            zIndex : 3,
            source : new ol.source.TileWMS({
                url : 'http://rno-gis.hgicreate.com/geoserver/rnoprod/wms',
                params : {
                    'FORMAT' : 'image/png',
                    'VERSION' : '1.1.1',
                    tiled : true,
                    STYLES : '',
                    LAYERS : 'rnoprod:RNO_LTE_CELL_GEOM',
                    CQL_FILTER : "AREA_ID=" + cityId
                }
            }),
            opacity : 0.5
        });
        map.addLayer(tiled);
    });

    $("#loadGisCellName").click(function () {
        if ($(this).text() === "显示小区名字") {
            $(this).text("关闭小区名字");
            map.addLayer(textImageTile);
        } else {
            $(this).text("显示小区名字");
            map.removeLayer(textImageTile);
        }
    });

    $("#exportGrid").click(function () {
        var gridType="";
        $("input[name='gridType']:checked").each(function () {
            gridType += $(this).val() + "," ;
        });
        $("#type").val(gridType.substring(0, gridType.length -1));
        $("#areaId").val($("#cityId").find("option:checked").val());
        $('#gridForm').attr("action", "/api/lte-grid-gis/download-cell-data");
        $("#gridForm").submit();
    });

    $("#loadGrid").click(function () {
        $("#loading").css("display", "block");
        gridTextLayer.getSource().clear();
        gridLayer.getSource().clear();
        var gridType="";
        $("input[name='gridType']:checked").each(function () {
            gridType += $(this).val() + "," ;
        });
        $.ajax({
            url: "/api/lte-grid-gis/grid-data",
            dataType: "json",
            data: {
                'gridType' : gridType.substring(0, gridType.length -1),
                'areaId': $("#cityId").find("option:checked").val()
            },
            async: false,
            success: function (data) {
                //console.log(data);
                var gridData = data['gridData'];
                var gridCoord = data['gridCoords'];
                if(gridData != "") {
                    //console.log(gridCoord);
                    var arr = new Array();
                    var center, grid, feature, color;
                    $.each(gridData, function (index, value) {
                        arr = [];
                        $.each(gridCoord, function (i, v) {
                            if(value.id === v.gridId) {
                                //console.log(value.id);
                                arr.push(ol.proj.transform([parseFloat(v.longitude),
                                    parseFloat(v.latitude)], 'EPSG:4326', 'EPSG:3857'));
                            }
                        });

                        if(value.gridType === 'A') {
                            color = 'red';
                        }else if(value.gridType === 'B') {
                            color = 'green';
                        }else {
                            color = 'blue';
                        }
                        //console.log(arr);
                        grid = new ol.geom.LineString(arr);
                        feature = new ol.Feature({
                            geometry: grid,
                        });
                        feature.setStyle(new ol.style.Style({
                            stroke: new ol.style.Stroke({
                                color: color,
                                width: 2,
                            }),
                            fill: new ol.style.Fill({
                                color: [0, 0, 255, 0.6]
                            })
                        }));
                        gridLayer.getSource().addFeature(feature);

                        center = ol.proj.transform([parseFloat(value.centerCoord.split(",")[0].trim()),
                            parseFloat(value.centerCoord.split(",")[1].trim())], 'EPSG:4326', 'EPSG:3857')
                        feature = new ol.Feature({
                            geometry: new ol.geom.Point(center),
                        });
                        feature.setStyle(new ol.style.Style({
                            text: new ol.style.Text({
                                font: '10px sans-serif',
                                stroke: new ol.style.Stroke({ color: '#fff', width: 10 }),
                                text: value.gridCode,
                                //offsetY: -50,
                                fill: new ol.style.Fill({ color: '#3367D6' }),
                            }),
                        }));
                        gridTextLayer.getSource().addFeature(feature);
                    })
                    $("#loading").css("display", "none");
                    showInfoInAndOut('success', '加载完成！');
                } else {
                    $("#loading").css("display", "none");
                    showInfoInAndOut('warn', '没有找到数据！');
                }
            },
            error: function () {
                $("#loading").css("display", "none");
                showInfoInAndOut('error', '程序出错了！');
            }
        })
    })
});

//提示信息淡入淡出
function showInfoInAndOut(div, info) {
    $("#" + div).html(info);
    $("#" + div).fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}
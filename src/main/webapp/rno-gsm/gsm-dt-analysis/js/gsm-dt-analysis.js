var map, tiled, samplePointLayer, greenStyle;
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
    });
    $(".switch_hidden").click(function () {
        $(this).hide();
        $(".switch").show();
        $(".resource_list_icon").animate({
            right: '400px'
        }, 'fast');
        $(".resource_list_box").show("fast");
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
                'LAYERS': 'rnoprod:RNO_GSM_CELL_CENTROID'
            },
            serverType: 'geoserver'
        }),
        opacity: 0.8
    });

    $("#areaId").change(function () {
        var lon = parseFloat($(this).find("option:checked").attr("data-lon"));
        var lat = parseFloat($(this).find("option:checked").attr("data-lat"));
        if (map === undefined) {
            //采样点图层
            samplePointLayer = new ol.layer.Vector({
                source: new ol.source.Vector(),
                zIndex: 3
            });
            clickedLayer = new ol.layer.Vector({
                source: new ol.source.Vector(),
                zIndex: 4
            });

            //采样点专用
            greenStyle = new ol.style.Style({
                stroke: new ol.style.Stroke({
                    // 设置线条颜色
                    color: 'yellow',
                    size: 5
                }),
                fill: new ol.style.Fill({
                    // 设置填充颜色与不透明度
                    color: 'rgba(0, 255, 0, 1.0)'
                })
            });

            map = new ol.Map({
                target: 'map',
                layers: [baseLayer, samplePointLayer],
                view: new ol.View({
                    center: ol.proj.fromLonLat([lon, lat]),
                    zoom: 16,
                })
            });

            var feature;
            map.on('singleclick', function (evt) {
                //获取新点击采样点，渲染为红色
                feature = map.forEachFeatureAtPixel(evt.pixel, function (feature) {
                    return feature;
                });
                if (feature) {
                    $.ajax({
                        url: "../../api/gsm-dt-analysis/dt-data-detail",
                        type: "GET",
                        data: {
                            'dataId': feature.getId()
                        },
                        success: function (data) {
                            if (data != '') {
                                $("#tdSampleTime").text(data[0].time);
                                $("#tdServerCell").text(data[0].cell);
                                $("#tdRxLev").text(data[0].rxlevsub);
                                $("#tdRxQual").text(data[0].rxqualsub);
                                var avg = (data[0].ncellNameOne +","+ data[0].ncellNameTwo +","+ data[0].ncellNameThree +","+
                                    data[0].ncellNameFour +","+ data[0].ncellNameFive +","+ data[0].ncellNameSix);
                                $("#tdNcells").text(avg);
                                $("#tdNcellRxLev").text(data[0].ncellAvgRxlev);
                                $("#tdServerCellToSampleAngle").text(data[0].distance);
                                $("#tdServerCellAngle").text(data[0].angle);
                                $("#myTab li:eq(1)").addClass("active");
                                $("#myTab li:eq(1)").siblings().removeClass("active");
                                $("#sampleDetail").addClass("active");
                                $("#sampleDetail").siblings().removeClass("active");
                            }
                        }
                    });
                }
            });
        } else {
            map.getView().setCenter(ol.proj.transform([lon, lat], 'EPSG:4326', 'EPSG:3857'));
        }
    });

    //初始区域
    initAreaSelectors({selectors: ["provinceId", "cityId", "areaId"], coord: true, relate: true});
    //加载小区
    $("#loadGsmDtGisCell").click(function () {
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
    //小区中文名
    $("#showCellName").click(function () {
        if ($(this).text() === "显示小区名字") {
            $(this).text("关闭小区名字");
            map.addLayer(textImageTile);
        } else {
            $(this).text("显示小区名字");
            map.removeLayer(textImageTile);
        }
    });
    //显示渲染图例
    $("#queryDt").click(function () {
        samplePointLayer.getSource().clear();
        $("#loading").css("display", "block");
        $.ajax({
            url: "../../api/gsm-dt-analysis/dt-data",
            type: "GET",
            data: {
                "descId": $("#areaId").val(),
            },
            success: showDtData,
            error: function (err) {
                $("#loading").css("display", "none");
                showInfoInAndOut('error', '程序出错了！');
            }
        });
    });
    //显示信号强度
    $("#signalstrenthbtn").click(function () {
        $('#showRxlev').show();
        $('#showQuality').hide();
        samplePointLayer.getSource().clear();
        $("#loading").css("display", "block");
        $.ajax({
            url: "../../api/gsm-dt-analysis/dt-data",
            type: "GET",
            data: {
                "descId": $("#areaId").val(),
            },
            success: showStrenthData,
            error: function (err) {
                $("#loading").css("display", "none");
                showInfoInAndOut('error', '程序出错了！');
            }
        });
    });
    //显示信号质量
    $("#signalqualitybtn").click(function () {
        $('#showQuality').show();
        $('#showRxlev').hide();
        samplePointLayer.getSource().clear();
        $("#loading").css("display", "block");
        $.ajax({
            url: "../../api/gsm-dt-analysis/dt-data",
            type: "GET",
            data: {
                "descId": $("#areaId").val(),
            },
            success: showQualityData,
            error: function (err) {
                $("#loading").css("display", "none");
                showInfoInAndOut('error', '程序出错了！');
            }
        });
    });
});

function showDtData(data) {
    var point, feature;
    $("#loading").css("display", "none");
    if (data[0] === undefined) {
        showInfoInAndOut('warn', '没有找到数据！');
    } else {
        $.each(data, function (index, value) {
            point = ol.proj.transform([parseFloat(value.longitute),
                parseFloat(value.latitude)], 'EPSG:4326', 'EPSG:3857');
            feature = new ol.Feature({
                geometry: new ol.geom.Point(point),
            });
            feature.setId(value.id);
            //console.log(feature.getId());
            feature.setStyle(new ol.style.Style({
                image: new ol.style.Circle({
                    radius: 4,
                    fill: new ol.style.Fill({color: 'rgba(0, 255, 0, 1.0)'}),
                    stroke: new ol.style.Stroke({color: 'blue', width: 1}),
                })
            }));
            // console.log(feature);
            samplePointLayer.getSource().addFeature(feature);
            if (index === 0) {
                map.getView().animate({
                    center: point,
                    duration: 2000
                });
            }
        });
        showInfoInAndOut('success', '渲染完成！');
    }
}
function showStrenthData(data) {
    var point, feature;
    $("#loading").css("display", "none");
    if (data[0] === undefined) {
        showInfoInAndOut('warn', '没有找到数据！');
    } else {
        $.each(data, function (index, value) {
            point = ol.proj.transform([parseFloat(value.longitute),
                parseFloat(value.latitude)], 'EPSG:4326', 'EPSG:3857');
            feature = new ol.Feature({
                geometry: new ol.geom.Point(point),
            });
            feature.setId(value.id);
            //console.log(feature.getId());
            var color;
            if(value.rxlevsub<-90) {
                color = "#EE3B3B";
            }else if(value.rxlevsub>=-90&&value.rxlevsub<-85) {
                color = "#FF00FF" ;
            }else if(value.rxlevsub>=-85&&value.rxlevsub<-80) {
                color = "#FFA500" ;
            }else if(value.rxlevsub>=-80&&value.rxlevsub<-75) {
                color = "#8B8B7A" ;
            }else if(value.rxlevsub>=-75&&value.rxlevsub<-70) {
                color = "#0000FF" ;
            }else if(value.rxlevsub>=-70&&value.rxlevsub<-65) {
                color = "#87CEFA" ;
            }else if(value.rxlevsub>=-65&&value.rxlevsub<-60) {
                color = "#7CFC00" ;
            }else if(value.rxlevsub>=-60&&value.rxlevsub<0) {
                color = "#32CD32" ;
            }else {
               color = "#E8E8E8" ;
            }
            feature.setStyle(new ol.style.Style({
                image: new ol.style.Circle({
                    radius: 4,
                    fill: new ol.style.Fill({color: color}),
                    stroke: new ol.style.Stroke({color: 'blue', width: 1}),
                })
            }));
            // console.log(feature);
            samplePointLayer.getSource().addFeature(feature);
            if (index === 0) {
                map.getView().animate({
                    center: point,
                    duration: 2000
                });
            }
        });
        showInfoInAndOut('success', '渲染完成！');
    }
}
function showQualityData(data) {
    var point, feature;
    $("#loading").css("display", "none");
    if (data[0] === undefined) {
        showInfoInAndOut('warn', '没有找到数据！');
    } else {
        $.each(data, function (index, value) {
            point = ol.proj.transform([parseFloat(value.longitute),
                parseFloat(value.latitude)], 'EPSG:4326', 'EPSG:3857');
            feature = new ol.Feature({
                geometry: new ol.geom.Point(point),
            });
            feature.setId(value.id);
            //console.log(feature.getId());
            var color;
            if(value.rxqualsub==null||value.rxqualsub<0){
                color = "#E8E8E8" ;
            }
            else if(value.rxqualsub>=0&&value.rxqualsub<4) {
                color = "#FF00FF" ;
            }else if(value.rxqualsub>=4&&value.rxqualsub<7) {
                color = "#0000FF" ;
            }else if(value.rxqualsub>=7&&value.rxqualsub<10) {
                color = "#87CEFA" ;
            }else if(value.rxqualsub>=10) {
                color = "#7CFC00" ;
            }
            feature.setStyle(new ol.style.Style({
                image: new ol.style.Circle({
                    radius: 4,
                    fill: new ol.style.Fill({color: color}),
                    stroke: new ol.style.Stroke({color: 'blue', width: 1}),
                })
            }));
            // console.log(feature);
            samplePointLayer.getSource().addFeature(feature);
            if (index === 0) {
                map.getView().animate({
                    center: point,
                    duration: 2000
                });
            }
        });
        showInfoInAndOut('success', '渲染完成！');
    }
}
//提示信息淡入淡出
function showInfoInAndOut(div, info) {
    $("#" + div).html(info);
    $("#" + div).fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

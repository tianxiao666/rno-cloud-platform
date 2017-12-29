var map, tiled, samplePointLayer, lineLayer, cellLayer;
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
            //主小区图层
            cellLayer = new ol.layer.Vector({
                source: new ol.source.Vector(),
                zIndex: 4
            });
            //线图层
            lineLayer = new ol.layer.Vector({
                source: new ol.source.Vector(),
                zIndex: 5
            });

            map = new ol.Map({
                target: 'map',
                layers: [baseLayer, samplePointLayer, cellLayer, lineLayer],
                view: new ol.View({
                    projection: 'EPSG:4326',
                    center: [lon, lat],
                    zoom: 21,
                })
            });

            //获取新点击采样点信息
            var feature;
            map.on('singleclick', function (evt) {
                feature = map.forEachFeatureAtPixel(evt.pixel, function (feature) {
                    return feature;
                });
                if (feature.getId()) {
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
                                var ncell1 = data[0].ncellNameOne ? data[0].ncellNameOne + "," : "";
                                var ncell2 = data[0].ncellNameTwo ? data[0].ncellNameTwo + "," : "";
                                var ncell3 = data[0].ncellNameThree ? data[0].ncellNameThree + "," : "";
                                var ncell4 = data[0].ncellNameFour ? data[0].ncellNameFour + "," : "";
                                var ncell5 = data[0].ncellNameFive ? data[0].ncellNameFive + "," : "";
                                var ncell6 = data[0].ncellNameSix ? data[0].ncellNameSix : "";
                                $("#tdNcells").text(ncell1 + ncell2 + ncell3 + ncell4 + ncell5 + ncell6);
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
            map.getView().setCenter([lon, lat]);
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
});

var strenth1 = 0, strenth2 = 0, strenth3 = 0, strenth4 = 0, strenth5 = 0, strenth6 = 0, strenth7 = 0, strenth8 = 0,
    strenth9 = 0;
var quality1 = 0, quality2 = 0, quality3 = 0, quality4 = 0, quality5 = 0;

function showDtAnalysisResult(type) {
    //隐藏信息表
    $("#showQuality").hide();
    $("#showRxlev").hide();
    $("#showStructure").hide();
    $("#showResultTable").hide();
    //清空图层
    samplePointLayer.getSource().clear();
    cellLayer.getSource().clear();
    lineLayer.getSource().clear();
    //显示所需信息表
    if (type === "strenth") {
        $("#showRxlev").show();
        strenth1 = 0;
        strenth2 = 0;
        strenth3 = 0;
        strenth4 = 0;
        strenth5 = 0;
        strenth6 = 0;
        strenth7 = 0;
        strenth8 = 0, strenth9 = 0;
    } else if (type === "quality") {
        $("#showQuality").show();
        quality1 = 0;
        quality2 = 0;
        quality3 = 0;
        quality4 = 0;
        quality5 = 0;
    } else if (type === "structure") {
        $("#showStructure").show();
    }
    $("#loading").css("display", "block");
    $.ajax({
        url: "../../api/gsm-dt-analysis/dt-data",
        type: "GET",
        data: {
            "descId": $("#areaId").val(),
        },
        success: function (data) {
            var point, feature;
            $("#loading").css("display", "none");
            if (data[0] === undefined) {
                showInfoInAndOut('warn', '没有找到数据！');
            } else {
                //console.info(data)
                $.each(data, function (index, value) {
                    point = [parseFloat(value.longitude),
                        parseFloat(value.latitude)];
                    feature = new ol.Feature({
                        geometry: new ol.geom.Point(point),
                    });
                    feature.setId(value.id);
                    var color = setColor(type, value);
                    feature.setStyle(new ol.style.Style({
                        image: new ol.style.Circle({
                            radius: 4,
                            fill: new ol.style.Fill({color: color}),
                            stroke: new ol.style.Stroke({color: 'blue', width: 1}),
                        })
                    }));
                    samplePointLayer.getSource().addFeature(feature);
                    if (index === 0) {
                        map.getView().animate({
                            center: point,
                            duration: 2000
                        });
                    }
                });
                if (type === "sampleCell") {
                    //获取服务小区与其对应的采样点
                    showNcellLine();
                }
                showInfoInAndOut('success', '渲染完成！');
                showResultTable(type);
            }
        },
        error: function (err) {
            $("#loading").css("display", "none");
            showInfoInAndOut('error', '程序出错了！');
        }
    });
}

function showNcellLine() {
    $.ajax({
        url: "../../api/gsm-dt-analysis/get-cell",
        type: "GET"
    }).then(function (data) {
        var cellList = [];
        $.each(data, function (index, value) {
            cellList.push([parseFloat(value["CELL_LONGITUDE"]), parseFloat(value["CELL_LATITUDE"])]);
        });
        for (var i = 0; i < cellList.length; i++) {
            showCellChart(cellList[i]);
            getNcell(cellList[i]);
        }
    });
}

function getNcell(cell) {
    $.ajax({
        url: "../../api/gsm-dt-analysis/get-ncell",
        type: "GET",
        data: {
            "longitude": cell[0],
            "latitude": cell[1]
        }
    }).then(function (data) {
        var ncellList = [];
        $.each(data, function (index, value) {
            ncellList.push([parseFloat(value["LONGITUDE"]), parseFloat(value["LATITUDE"])]);
        });
        //绘制主小区与邻区之间的连线
        drawLine(cell, ncellList);
    });
}

function showCellChart(cell) {
    var pointN, featureN;
    pointN = [cell[0], cell[1]];
    featureN = new ol.Feature({
        geometry: new ol.geom.Point(pointN),
    });
    featureN.setStyle(new ol.style.Style({
        image: new ol.style.Circle({
            radius: 4,
            fill: new ol.style.Fill({color: "#EE3B3B"}),
            stroke: new ol.style.Stroke({color: 'blue', width: 1}),
        })
    }));
    cellLayer.getSource().addFeature(featureN);
}

function drawLine(cellCoors, ncellCoors) {
    var line, feature;
    $.each(ncellCoors, function (index, value) {
        line = new ol.geom.LineString([cellCoors, value]);
        feature = new ol.Feature({
            geometry: line
        });
        feature.setStyle(new ol.style.Style({
            stroke: new ol.style.Stroke({
                color: "rgba(0, 255, 0, 1.0)",
                width: 1
            })
        }));
        lineLayer.getSource().addFeature(feature);
    });
}

function setColor(type, value) {
    var color = "rgba(0, 255, 0, 1.0)";
    if (type === "strenth") {
        if (value.rxlevsub < -90) {
            color = "#EE3B3B";
            strenth1++;
        } else if (value.rxlevsub >= -90 && value.rxlevsub < -85) {
            color = "#FF00FF";
            strenth2++;
        } else if (value.rxlevsub >= -85 && value.rxlevsub < -80) {
            color = "#FFA500";
            strenth3++;
        } else if (value.rxlevsub >= -80 && value.rxlevsub < -75) {
            color = "#8B8B7A";
            strenth4++;
        } else if (value.rxlevsub >= -75 && value.rxlevsub < -70) {
            color = "#0000FF";
            strenth5++;
        } else if (value.rxlevsub >= -70 && value.rxlevsub < -65) {
            color = "#87CEFA";
            strenth6++;
        } else if (value.rxlevsub >= -65 && value.rxlevsub < -60) {
            color = "#7CFC00";
            strenth7++;
        } else if (value.rxlevsub >= -60 && value.rxlevsub < 0) {
            color = "#32CD32";
            strenth8++;
        } else {
            color = "#E8E8E8";
            strenth9++;
        }
    } else if (type === "quality") {
        if (value.rxqualsub == null || value.rxqualsub < 0) {
            color = "#E8E8E8";
            quality5++;
        } else if (value.rxqualsub >= 0 && value.rxqualsub < 4) {
            color = "#FF00FF";
            quality1++;
        } else if (value.rxqualsub >= 4 && value.rxqualsub < 7) {
            color = "#0000FF";
            quality2++;
        } else if (value.rxqualsub >= 7 && value.rxqualsub < 10) {
            color = "#87CEFA";
            quality3++;
        } else if (value.rxqualsub >= 10) {
            color = "#7CFC00";
            quality4++;
        } else {
            color = "#E8E8E8";
            quality5++;
        }
    } else if (type === "structure") {
        if (value.rxqualsub) {
            var count = 0;
            var ncellRxlevArr = new Array();
            ncellRxlevArr.push(value.ncellRxlevOne ? value.ncellRxlevOne : 0);
            ncellRxlevArr.push(value.ncellRxlevTwo ? value.ncellRxlevTwo : 0);
            ncellRxlevArr.push(value.ncellRxlevThree ? value.ncellRxlevThree : 0);
            ncellRxlevArr.push(value.ncellRxlevFour ? value.ncellRxlevFour : 0);
            ncellRxlevArr.push(value.ncellRxlevFive ? value.ncellRxlevFive : 0);
            ncellRxlevArr.push(value.ncellRxlevSix ? value.ncellRxlevSix : 0);
            for (var h = 0; h < ncellRxlevArr.length; h++) {
                if (ncellRxlevArr[h] >= (value.rxqualsub - 12) && ncellRxlevArr[h] <= (value.rxqualsub + 12)) {
                    count++;
                }
            }
            if (count == 0) {
                color = "#3A5FCD";
            } else if (count == 1) {
                color = "#87CEFA";
            } else if (count == 2) {
                color = "#FFA500";
            } else if (count == 3) {
                color = "#7CFC00";
            } else if (count == 4) {
                color = "#32CD32";
            } else if (count == 5) {
                color = "#FF00FF";
            } else if (count == 6) {
                color = "#EE3B3B";
            }
        }else {
            color = "#121212";
        }
    }else if(type === "sampleCell"){
        color = "#FF00FF";
    }
    return color;
}

function showResultTable(type) {
    //删除旧tr
    $("[name='move']").remove();
    if (type === "strenth") {
        $("#tdTitle").text("Rxlev渲染指标统计");
        var trLine = "";
        trLine = "<tr style='height: 20px' id='tr0' name='move'><td style='text-align: center'>X<-90</td></tr>" +
            "<tr style='height: 20px' id='tr1' name='move'><td style='text-align: center'>-90<=X<-85</td></tr>" +
            "<tr style='height: 20px' id='tr2' name='move'><td style='text-align: center'>-85<=X<-80</td></tr>" +
            "<tr style='height: 20px' id='tr3' name='move'><td style='text-align: center'>-80<=X<-75</td></tr>" +
            "<tr style='height: 20px' id='tr4' name='move'><td style='text-align: center'>-75<=X<-70</td></tr>" +
            "<tr style='height: 20px' id='tr5' name='move'><td style='text-align: center'>-70<=X<-65</td></tr>" +
            "<tr style='height: 20px' id='tr6' name='move'><td style='text-align: center'>-65<=X<-60</td></tr>" +
            "<tr style='height: 20px' id='tr7' name='move'><td style='text-align: center'>-60<=X<0</td></tr>" +
            "<tr style='height: 20px' id='tr8' name='move'><td style='text-align: center'>无效点</td></tr>";
        $("#tbContext").append(trLine);
        var total = strenth1 + strenth2 + strenth3 + strenth4 + strenth5 + strenth6 + strenth7 + strenth8;
        var strenthArray = new Array();
        strenthArray.push(strenth1);
        strenthArray.push(strenth2);
        strenthArray.push(strenth3);
        strenthArray.push(strenth4);
        strenthArray.push(strenth5);
        strenthArray.push(strenth6);
        strenthArray.push(strenth7);
        strenthArray.push(strenth8);
        strenthArray.push(strenth9);
        var strenthCount = new Array();
        strenthCount.push(mathPercent(strenth1, total));
        strenthCount.push(mathPercent(strenth2, total));
        strenthCount.push(mathPercent(strenth3, total));
        strenthCount.push(mathPercent(strenth4, total));
        strenthCount.push(mathPercent(strenth5, total));
        strenthCount.push(mathPercent(strenth6, total));
        strenthCount.push(mathPercent(strenth7, total));
        strenthCount.push(mathPercent(strenth8, total));
        strenthCount.push(mathPercent(strenth9, total));
        for (var i = 0; i < 9; i++) {
            var html = "";
            html += "<td style='text-align: center;'>" + strenthArray[i] + "</td>";
            html += "<td style='text-align: center;'>" + strenthCount[i] + "</td>";
            $("#tr" + i).append(html);
        }
        $("#showResultTable").show();
    } else if (type === "quality") {
        $("#tdTitle").text("可用信号渲染指标统计");
        var trList = "";
        trList = "<tr style='height: 20px' id='tr0' name='move'><td style='text-align: center'>0<=X<4</td></tr>" +
            "<tr style='height: 20px' id='tr1' name='move'><td style='text-align: center'>4<=X<7</td></tr>" +
            "<tr style='height: 20px' id='tr2' name='move'><td style='text-align: center'>-7<=X<10</td></tr>" +
            "<tr style='height: 20px' id='tr3' name='move'><td style='text-align: center'>X>=10</td></tr>" +
            "<tr style='height: 20px' id='tr4' name='move'><td style='text-align: center'>无效点</td></tr>";
        $("#tbContext").append(trList);
        var tot = quality1 + quality2 + quality3 + quality4 + quality5;
        var qualityArray = new Array();
        qualityArray.push(quality1);
        qualityArray.push(quality2);
        qualityArray.push(quality3);
        qualityArray.push(quality4);
        qualityArray.push(quality5);
        var qualityCount = new Array();
        qualityCount.push(mathPercent(quality1, tot));
        qualityCount.push(mathPercent(quality2, tot));
        qualityCount.push(mathPercent(quality3, tot));
        qualityCount.push(mathPercent(quality4, tot));
        qualityCount.push(mathPercent(quality5, tot));
        for (var i = 0; i < 5; i++) {
            var html = "";
            html += "<td style='text-align: center;'>" + qualityArray[i] + "</td>";
            html += "<td style='text-align: center;'>" + qualityCount[i] + "</td>";
            $("#tr" + i).append(html);
        }
        $("#showResultTable").show();
    }
}

function mathPercent(num, total) {
    return Math.round(num / total * 10000) / 100.00 + "%";
}

//提示信息淡入淡出
function showInfoInAndOut(div, info) {
    $("#" + div).html(info);
    $("#" + div).fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

var map, tiled, samplePointLayer;
var greenStyle;
$(function () {

    laydate.render({elem: '#endDate', value: new Date(2017, 8, 15)});

    //tab("div_tab", "li", "onclick");
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
            url: 'http://rno-omt.hgicreate.com/styles/rno-omt/{z}/{x}/{y}.png',
            zIndex: 1
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

    $("#districtId").change(function () {
        $("#listDtRes tbody").html("");
        var lon = parseFloat($(this).find("option:checked").attr("data-lon"));
        var lat = parseFloat($(this).find("option:checked").attr("data-lat"));
        console.log(lon + ","+ lat);
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

                if(feature) {
                    $.ajax({
                        url: "/api/lte-dt-analysis/dt-data-detail",
                        type: "GET",
                        data:{
                            'dataId': feature.getId()
                        },
                        success: function (data) {
                            $("tr[name = 'ncell']").remove();
                            var html = "";
                            if(data!="") {
                                var d = data['cell'][0];//console.log(d);
                                if(d!=undefined) {
                                    var date = new Date();
                                    date.setTime(d.metaTime?d.metaTime:'');
                                    $("#time").text(date.format("yyyy-MM-dd hh:mm:ss"));
                                    $("#serverCell").text(d.cellName?d.cellName:'');
                                    $("#serverCellFreq").text(d.earfcn?d.earfcn:'');
                                    $("#serverCellPci").text(d.pci?d.pci:'');
                                    $("#serverCellRsrp").text(d.rsrp?d.rsrp:'');
                                    $("#serverCellRsSinr").text(d.rsSinr?d.rsSinr:'');
                                    $("#serverCellToSampleDis").text(d.scellDist?d.scellDist:'');
                                    $("#area2Type").text(d.areaType?d.areaType:'');
                                }

                                $.each(data['ncell'], function (index, value) {
                                    html += "<tr name='ncell'><td style='width:40%;text-align: right;font-weight: bold'>邻区"+ (index+1)
                                        +"</td><td>"+ value.ncellName +"</td></tr>";
                                    html += "<tr name='ncell'><td style='width:40%;text-align: right;font-weight: bold'>邻区"+ (index+1)
                                        +" RSRP</td><td>"+ value.ncellRsrp +"</td></tr>";
                                })

                                $("#sampleDetailTable").append(html);
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
    initAreaSelectors({selectors: ["provinceId", "cityId", "districtId"], coord: true, relate: true});

    $("#loadGisCell").click(function () {
        var cityId = parseInt($("#cityId").find("option:checked").val());
        map.removeLayer(tiled);
        tiled = new ol.layer.Tile({
            zIndex : 2,
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

    $("#queryDt").click(function () {
        $("#loading").css("display", "block");
        var dataType = $('#factory').find("option:selected").val();
        var areaType = $('#areaType').find("option:selected").val();
        $.ajax({
            url : "/api/lte-dt-analysis/dt-desc",
            type: "GET",
            data: {
                "areaId": $('#cityId').val(),
                    "createdDate": $('#endDate').val(),
                    "dataType": dataType === 'ALL' ? '数据业务,扫频业务' : dataType,
                    "areaType": areaType === 'ALL' ? '城区,非城区,高速' : areaType
            },
            success: showDatatables,
            error: function (err) {
                $("#loading").css("display", "none");
                showInfoInAndOut('error', '程序出错了！');
                console.log(err);
            }
        })
    });

    $("#loadDtData").click(function () {
        samplePointLayer.getSource().clear();
        if($('input[name="fileId"]:checked').val() === undefined) {
            showInfoInAndOut('warn', '请先选择文件后加载！');
            return;
        }
        $("#loading").css("display", "block");
        var id_array = new Array();
        $('input[name="fileId"]:checked').each(function(){
            id_array.push($(this).val());//向数组中添加元素
        });
        $.ajax({
            url : "/api/lte-dt-analysis/dt-data",
            type: "GET",
            data: {
                "descId": id_array.join(','),
            },
            success: showDtData,
            error: function (err) {
                $("#loading").css("display", "none");
                showInfoInAndOut('error', '程序出错了！');
                console.log(err);
            }
        })
    });

    $("#weakCoverageBtn").click(function () {
        if(samplePointLayer.getSource().getFeatures().length === 0) {
            showInfoInAndOut('warn', '请先加载数据后进行分析！');
            return;
        }
        $("#loading").css("display", "block");
        var id_array = new Array();
        $('input[name="fileId"]:checked').each(function(){
            id_array.push($(this).val());//向数组中添加元素
        });

        $.ajax({
            url : "/api/lte-dt-analysis/weak-coverage",
            type: "GET",
            data: {
                "descId": id_array.join(','),
            },
            success: showAnalysisResult,
            error: function (err) {
                $("#loading").css("display", "none");
                showInfoInAndOut('error', '程序出错了！');
                console.log(err);
            }
        })
    })

    $("#roomLeakageBtn").click(function () {
        if(samplePointLayer.getSource().getFeatures().length === 0) {
            showInfoInAndOut('warn', '请先加载数据后进行分析！');
            return;
        }
        $("#loading").css("display", "block");
        var id_array = new Array();
        $('input[name="fileId"]:checked').each(function(){
            id_array.push($(this).val());//向数组中添加元素
        });

        $.ajax({
            url : "/api/lte-dt-analysis/room-leakage",
            type: "GET",
            data: {
                "descId": id_array.join(','),
            },
            success: showAnalysisResult,
            error: function (err) {
                $("#loading").css("display", "none");
                showInfoInAndOut('error', '程序出错了！');
                console.log(err);
            }
        })
    });

    $("#overlapCoverageBtn").click(function () {
        if(samplePointLayer.getSource().getFeatures().length === 0) {
            showInfoInAndOut('warn', '请先加载数据后进行分析！');
            return;
        }
        $("#loading").css("display", "block");
        var id_array = new Array();
        $('input[name="fileId"]:checked').each(function(){
            id_array.push($(this).val());//向数组中添加元素
        });

        $.ajax({
            url : "/api/lte-dt-analysis/overlap-coverage",
            type: "GET",
            data: {
                "descId": id_array.join(','),
            },
            success: showAnalysisResult,
            error: function (err) {
                $("#loading").css("display", "none");
                showInfoInAndOut('error', '程序出错了！');
                console.log(err);
            }
        })
    });

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

function showDatatables(data) {
    $("#loading").css("display", "none");
    if(data === "") {
        showInfoInAndOut('warn', '没有找到数据！');
    }else {
        $('#listDtRes').css("line-height", "12px").DataTable({
            "data": data,
            "columns": [
                { "data" : null },
                { "data" : "filename" },
                { "data" : "dataType" },
                { "data" : "areaType" }
            ],
            "columnDefs": [
                {
                    "render": function(data, type, row) {
                        return "<input name='fileId' value='" + row.id + "' type='checkbox'>";
                    },
                    "targets": 0,
                    "data": null,
                }
            ],
            "lengthChange": false,
            "ordering": true,
            "searching": false,
            "scrollY":310,
            "scrollX":310,
            "destroy": true,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        });
        showInfoInAndOut('success', '加载完成！');
    }

}

function showDtData(data) {
    var point, feature;
    samplePointLayer.getSource().clear();
    $("#loading").css("display", "none");

    if(data[0] === undefined) {
        showInfoInAndOut('warn', '没有找到数据！');
    }else  {
        $.each(data, function (index, value) {
            point = ol.proj.transform([parseFloat(value.longitude),
                parseFloat(value.latitude)], 'EPSG:4326', 'EPSG:3857');
            feature = new ol.Feature({
                geometry: new ol.geom.Point(point),
            });
            feature.setId(value.id);
            //console.log(feature.getId());
            feature.setStyle(new ol.style.Style({
                image: new ol.style.Circle({
                    radius: 4,
                    fill: new ol.style.Fill({ color: 'rgba(0, 255, 0, 1.0)' }),
                    stroke: new ol.style.Stroke({ color: 'blue', width: 1 }),
                })
            }));
            //console.log(feature);
            samplePointLayer.getSource().addFeature(feature);
            if(index===0) {
                map.getView().animate({
                    center: point,
                    duration: 2000
                });
            }
        });
        showInfoInAndOut('success', '渲染完成！');
    }

}

function showAnalysisResult(data) {

    $.each(samplePointLayer.getSource().getFeatures(), function (index, value) {
        value.setStyle(new ol.style.Style({
            image: new ol.style.Circle({
                radius: 4,
                fill: new ol.style.Fill({ color: 'rgba(0, 255, 0, 1.0)' }),
                stroke: new ol.style.Stroke({ color: 'blue', width: 1 }),
            })
        }));
    });

    $("#loading").css("display", "none");
    if(data !== ""){
        $.each(data, function (i, v) {
            samplePointLayer.getSource().getFeatureById(v).setStyle(new ol.style.Style({
                image: new ol.style.Circle({
                    radius: 4,
                    fill: new ol.style.Fill({ color: 'rgba(255, 0, 0, 1.0)' }),
                    stroke: new ol.style.Stroke({ color: 'blue', width: 1 }),
                })
            }));
        })
        showInfoInAndOut('success', '渲染完成！');
    }else {
        showInfoInAndOut('warn', '没有找到数据！');
    }
}

//提示信息淡入淡出
function showInfoInAndOut(div, info) {
    $("#" + div).html(info);
    $("#" + div).fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

Date.prototype.format = function(format) {
    var date = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "h+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "q+": Math.floor((this.getMonth() + 3) / 3),
        "S+": this.getMilliseconds()
    };
    if (/(y+)/i.test(format)) {
        format = format.replace(RegExp.$1, (this.getFullYear() + '').substr(4 - RegExp.$1.length));
    }
    for (var k in date) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1
                ? date[k] : ("00" + date[k]).substr(("" + date[k]).length));
        }
    }
    return format;
}

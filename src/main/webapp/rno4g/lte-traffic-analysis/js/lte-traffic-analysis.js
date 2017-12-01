var map, tiled, clickedCellLayer, problemCellLayer;
var redStyle, orangeStyle;
var popup;
var problemCells;
$(function () {
    $(".draggable").draggable();

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

    //点击小区图层
    clickedCellLayer = new ol.layer.Vector({
        source: new ol.source.Vector(),
        zIndex: 3
    });

    //问题小区图层
    problemCellLayer = new ol.layer.Vector({
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

    //问题小区专用
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

    $("#districtId").change(function () {
        var lon = parseFloat($(this).find("option:checked").attr("data-lon"));
        var lat = parseFloat($(this).find("option:checked").attr("data-lat"));
        if (map === undefined) {
            map = new ol.Map({
                target: 'map',
                layers: [baseLayer, clickedCellLayer, problemCellLayer],
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
                var url = tiled.getSource().getGetFeatureInfoUrl(evt.coordinate, view.getResolution(), view.getProjection(), {
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
                            content += '<tbody>';
                            // 获取多个重叠 feature
                            var cellIds="";
                            for (var i = 0; i < allFeatureNum; i++) {
                                var feature = allFeatures[i];
                                // 设置 feature 的样式
                                feature.setStyle(orangeStyle);
                                cellIds += allFeatures[i].get('CELL_ID') + ",";
                            }
                            //console.log(cellIds.substring(0, cellIds.length-1));
                            $.ajax({
                                url: "/api/lte-traffic-analysis/cell-record",
                                dataType: "json",
                                data: {
                                    'cellIds': cellIds.substring(0, cellIds.length-1)
                                },
                                async: false,
                                success: function (data) {
                                    if(data != "") {
                                        //console.log(data);
                                        var date = new Date();
                                        var col="blue";
                                        $.each(data["res"], function (key, value) {
                                            //console.log(key);
                                            content += '<tr class="custom-title"><td>' + key + '</td></tr>';
                                            $.each(value, function (i, v) {
                                                date.setTime(v.lteTrafficDesc.beginTime);
                                                $.each(data['problem'], function (c,d) {
                                                    if((date.format("yyyy-MM-dd hh:mm:ss")+".0"+ v.pmUserLabel)===data['problem'][c]){
                                                        col = "red";
                                                    }
                                                })
                                                content += '<tr class="custom-content" onclick="addColor(this, true)">';
                                                content += '<td style="color:'+ col +'">' + date.format("yyyy-MM-dd hh:mm:ss")+ '</td>';
                                                content += '<td style="display: none">' + v.cellId + '</td>';
                                                content += '</tr>';
                                                col = "blue";
                                            })
                                        })

                                        content += '</tbody></table>';

                                        console.log(evt.coordinate);
                                        popup.setPosition(evt.coordinate);
                                        $(element).popover({
                                            'placement': 'auto',
                                            'animation': false,
                                            'html': true,
                                            'content': content
                                        });
                                        $(element).popover('show');
                                    }
                                }
                            });

                            $('#cellTable tbody tr').click(function () {
                                if($(this).children().length===1){
                                    return;
                                }
                                var beginTime = $(this).find('td:eq(0)').text();
                                var cellId = $(this).find('td:eq(1)').text();
                                $("#lteStsCellIndex tbody").html("");
                                $.ajax({
                                    url: "/api/lte-traffic-analysis/cell-index",
                                    dataType: "json",
                                    data: {
                                        'beginTime' : beginTime,
                                        'cellId' : cellId
                                    },
                                    async: true,
                                    success: function (data) {
                                        //console.log(data);
                                        var date = new Date();
                                        $.each(data, function (key, value) {
                                            if(key==='测量开始时间' || key === '测量结束时间'){
                                                date.setTime(value);
                                                value = date.format("yyyy-MM-dd hh:mm:ss");
                                            }
                                            $("#lteStsCellIndex").append("<tr><td style='width:40%;font-weight: bold;text-align: right'>" + key +
                                                "</td><td style='text-align: left'>" + value + "</td></tr>");
                                        });

                                        $("#lteStsCellThreshold tbody").html("");

                                        var color;
                                        $.ajax({
                                            url: "/api/lte-traffic-analysis/one-problem-cell",
                                            dataType: "json",
                                            data: {
                                                'beginTime' : beginTime,
                                                'cellId' : cellId
                                            },
                                            async: false,
                                            success: function (d) {
                                                $.each(d, function (key, value) {
                                                    if(key==='测量开始时间' || key === '测量结束时间'){
                                                        date.setTime(value);
                                                        value = date.format("yyyy-MM-dd hh:mm:ss");
                                                    }
                                                    if(value==='异常'){
                                                        color = 'red';
                                                    }else {color='black';}
                                                    $("#lteStsCellThreshold").append("<tr><td style='width:40%;font-weight: bold;text-align: right'>" + key +
                                                        "</td><td style='text-align:left;color: "+ color+"'>" + value + "</td></tr>");
                                                });
                                            }
                                        })
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
            map.getView().setCenter([lon, lat]);
        }
    });

    //初始区域
    initAreaSelectors({selectors: ["provinceId", "cityId", "districtId"], coord: true});

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

    $("#displayProbCellBtn").click(function () {
        problemCellLayer.getSource().clear();
        $("#probCount").text("");
        if(tiled === undefined) {
            showInfoInAndOut("warn","请先加载小区！");
        }else {
            $("#loading").css("display", "block");
            $("#probCellList tbody").html("");
            $.ajax({
                url: "/api/lte-traffic-analysis/problem-cell",
                dataType: "json",
                data: {
                    'areaId' : parseInt($("#cityId").find("option:checked").val())
                },
                async: true,
                success: function (data) {
                    //console.log(data);
                    problemCells = data;
                    $("#probCount").text("找到问题小区" + data.length + "个");
                    var cellIds = "";
                    var cellId="";
                    $.each(data, function (key, value) {
                        cellId = "'" + value.小区ID + "' ";
                        $("#probCellList").append("<tr onclick='addColor(this, false)'><td style='display: none'>" + value.小区ID + "</td><td>" + value.小区名 + "</td></tr>");
                        cellIds += "'" + value.小区ID + "' ,";
                    });
                    //console.log(cellIds);
                    getCell(cellIds);
                    $("#probCellList tbody tr").click(function () {
                        //console.log("adasd");
                        var thisCell = $(this).find('td:eq(0)').text();
                        var element = popup.getElement();
                        $(element).popover('destroy');
                        var content = '<table id="cellTable1" class="table custom">';
                        content += '<tbody>';
                        $.ajax({
                            url: "/api/lte-traffic-analysis/cell-record",
                            dataType: "json",
                            data: {
                                'cellIds': thisCell
                            },
                            async: false,
                            success: function (data) {
                                //console.log(data['problem'][0]);
                                if(data['res'] != "") {
                                    var date = new Date();
                                    var color;
                                    $.each(data['res'], function (key, value) {
                                        //console.log(key);
                                        content += '<tr class="custom-title"><td>' + key + '</td></tr>';
                                        $.each(value, function (i, v) {
                                            date.setTime(v.lteTrafficDesc.beginTime);
                                            if((date.format("yyyy-MM-dd hh:mm:ss")+".0"+ v.pmUserLabel)===data['problem'][0]){
                                                color = "red";
                                            }else {
                                                color = "blue";
                                            }
                                            content += '<tr class="custom-content" onclick="addColor(this, true)">';
                                            content += '<td style="color:'+ color +'">' + date.format("yyyy-MM-dd hh:mm:ss")+ '</td>';
                                            content += '<td style="display: none">' + v.cellId + '</td>';
                                            content += '</tr>';
                                        })
                                    })
                                }
                            }
                        });
                        content += '</tbody></table>';

                        var filter = encodeURIComponent("CELL_ID in ('" + thisCell + "')");

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
                            var feature = parser.readFeatures(response)[0];
                            var cellCoor = [feature.get('LONGITUDE'), feature.get('LATITUDE')];

                            popup.setPosition(cellCoor);
                            map.getView().setCenter(cellCoor);
                        });

                        //popup.setPosition([113.36325993652345, 23.128390719604493]);
                        //map.getView().setCenter([113.36325993652345, 23.128390719604493]);
                        $(element).popover({
                            'placement': 'auto',
                            'animation': false,
                            'html': true,
                            'content': content
                        });
                        $(element).popover('show');

                        $('#cellTable1 tbody tr').click(function () {
                            if($(this).children().length===1){
                                return;
                            }
                            $("#lteStsCellThreshold tbody").html("");
                            $("#lteStsCellIndex tbody").html("");
                            var beginTime = $(this).find('td:eq(0)').text();
                            var cellId = $(this).find('td:eq(1)').text();
                            $.ajax({
                                url: "/api/lte-traffic-analysis/cell-index",
                                dataType: "json",
                                data: {
                                    'beginTime' : beginTime,
                                    'cellId' : cellId
                                },
                                async: false,
                                success: function (data) {
                                    //console.log(data);
                                    var date = new Date();
                                    $.each(data, function (key, value) {
                                        if(key==='测量开始时间' || key === '测量结束时间'){
                                            date.setTime(value);
                                            value = date.format("yyyy-MM-dd hh:mm:ss");
                                        }
                                        $("#lteStsCellIndex").append("<tr><td style='width:40%;font-weight: bold;text-align: right'>" + key +
                                            "</td><td style='text-align: left'>" + value + "</td></tr>");
                                    });
                                    //console.log($(this).find('td:eq(1)').text());
                                    var color;
                                    $.ajax({
                                        url: "/api/lte-traffic-analysis/one-problem-cell",
                                        dataType: "json",
                                        data: {
                                            'beginTime' : beginTime,
                                            'cellId' : cellId
                                        },
                                        async: false,
                                        success: function (d) {
                                            $.each(d, function (key, value) {
                                                if(key==='测量开始时间' || key === '测量结束时间'){
                                                    date.setTime(value);
                                                    value = date.format("yyyy-MM-dd hh:mm:ss");
                                                }
                                                if(value==='异常'){
                                                    color = 'red';
                                                }else {color='black';}
                                                $("#lteStsCellThreshold").append("<tr><td style='width:40%;font-weight: bold;text-align: right'>" + key +
                                                    "</td><td style='text-align:left;color: "+ color+"'>" + value + "</td></tr>");
                                            });
                                        }
                                    })
                                }
                            });
                        });
                    })

                    $("#loading").css("display", "none");
                    showInfoInAndOut("success","问题小区表已生成！");
                }
            });
        }
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

function getCell(cellIds) {
    var filter = encodeURIComponent("CELL_ID in (" + cellIds.substring(0, cellIds.length-1) + ")");

    var url = 'http://rno-gis.hgicreate.com/geoserver/rnoprod/ows?service=WFS&version=1.1.1' +
        '&request=GetFeature&typeName=rnoprod:RNO_LTE_CELL_GEOM&maxFeatures=50&' +
        'outputFormat=text%2Fjavascript&CQL_FILTER=' + filter;
    console.log(url);
    var parser = new ol.format.GeoJSON();
    $.ajax({
        url : url,
        dataType : 'jsonp',
        jsonpCallback : 'parseResponse'
    }).then(function(response) {
        var features = parser.readFeatures(response);
        console.log(features.length);
        if (features.length) {
            for(var m = 0; m < features.length; m++) {
                var onefeature = features[m];
                onefeature.setStyle(redStyle);
                problemCellLayer.getSource().addFeature(onefeature);
            }
        }
    });
}

//提示信息淡入淡出
function showInfoInAndOut(div, info) {
    $("#" + div).html(info);
    $("#" + div).fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
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
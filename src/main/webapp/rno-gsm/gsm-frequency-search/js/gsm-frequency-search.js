var map, cellLayer, clickedCellLayer, thisCellLayer,nCellLayer, lineLayer;
var popup;
var redStyle;
//bcch与tch缓存
var bcchCache = "";
var tchCache = "";
$(function () {
    tab("div_tab", "li", "onclick");
    $(".draggable").draggable();


    $("#trigger").css("display", "none");

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


    //禁止右键菜单
    $("#map").bind("contextmenu", function () {
        return false;
    });

    var baseLayer = new ol.layer.Tile({
        source: new ol.source.XYZ({
            url: 'http://rno-omt.hgicreate.com/styles/rno-omt/{z}/{x}/{y}.png',
            zIndex: 1
        })
    });

    //主小区图层
    thisCellLayer = new ol.layer.Vector({
        source: new ol.source.Vector(),
        zIndex: 5
    });


    //点击小区图层
    clickedCellLayer = new ol.layer.Vector({
        source: new ol.source.Vector(),
        zIndex: 4
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

    //主小区专用
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

    //右键菜单
    var contextmenu_items = [
        {
            text: '查看NOISE',
            data: 1,
            callback: showOut
        },{
            text: 'IN干扰',
            data: 2,
            callback: showOut
        },{
            text: 'OUT干扰',
            data: 3,
            callback: showOut
        },{
            text: '改频点',
            data: 3,
            callback: showFreqDialog
        }
    ];

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
                layers: [clickedCellLayer, baseLayer, thisCellLayer,nCellLayer,lineLayer],
                view: new ol.View({
                    projection: 'EPSG:4326',
                    center: [lon, lat],
                    zoom: 16
                })
            });

            cellLayer = new ol.layer.Tile({
                zIndex: 2,
                source: new ol.source.TileWMS({}),
                visible: false,
                opacity: 0.5
            });
            map.addLayer(cellLayer);

            popup = new ol.Overlay({element: document.getElementById('popup')});

            map.addOverlay(popup);

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
                            content += '<thead style="white-space: nowrap"><th>小区ID</th><th>小区名称</th><th>BCCH</th></thead>';
                            content += '<tbody>';
                            // 获取多个重叠 feature
                            for (var i = 0; i < allFeatureNum; i++) {
                                var feature = allFeatures[i];
                                console.log(feature);

                                content += '<tr style="word-break:break-all"  onclick="addColor(this, true)">';
                                content += '<td style="display:none">' + i + '</td>';
                                content += '<td style="white-space: nowrap">' + feature.get('CELL_ID') + '</td>';
                                content += '<td>' + feature.get('CELL_NAME') + '</td>';
                                content += '<td style="white-space: nowrap">' + feature.get('BCCH') + '</td>';
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
                        } else {
                            console.log('No result');
                        }
                    });
                }
            });
        } else {
            map.getView().animate({
                center: [parseFloat(lon), parseFloat(lat)],
                duration: 2000
            });
        }
    });

    initAreaSelectors({selectors: ["provinceId", "cityId", "areaId"], coord: true});

    $("#loadGsmCellToMap").click(function () {
        var cityId = parseInt($("#cityId").find("option:checked").val());
        var btsType = $("input[name='freqType']:checked").val();
        //console.log(btsType);
        var requestParams ="AREA_ID=" + cityId ;
        if(btsType !== '-1'){
            requestParams = "AREA_ID=" + cityId +"and BTS_TYPE='"+ btsType +"'";
        }
        map.removeLayer(cellLayer);
        cellLayer = new ol.layer.Tile({
            zIndex : 3,
            source : new ol.source.TileWMS({
                url : 'http://rno-gis.hgicreate.com/geoserver/rnoprod/wms',
                params : {
                    'FORMAT' : 'image/png',
                    'VERSION' : '1.1.1',
                    tiled : true,
                    STYLES : '',
                    LAYERS : 'rnoprod:RNO_GSM_CELL_GEOM',
                    CQL_FILTER : requestParams
                }
            }),
            opacity : 0.5
        });
        map.addLayer(cellLayer);
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

    //小区名称显示
    $("#showCellName").click(function () {
        if ($(this).text() === "显示小区名字") {
            $(this).text("关闭小区名字");
            map.addLayer(textImageTile);
        } else {
            $(this).text("显示小区名字");
            map.removeLayer(textImageTile);
        }
    });

    // 右键菜单打开之前
    contextmenu.on('beforeopen', function (e) {
        // console.log("e.coordinate====="+e.coordinate);
        var element = popup.getElement();
        $(element).popover('destroy');
        var view = map.getView();
        //contextmenu_items[contextmenu_items.length-1] = e.coordinate;
        var url = cellLayer.getSource().getGetFeatureInfoUrl(
            e.coordinate, view.getResolution(), view.getProjection(),
            {'INFO_FORMAT': 'text/javascript', 'FEATURE_COUNT': 50});
        if (url) {
            var parser = new ol.format.GeoJSON();
            $.ajax({
                url: url,
                dataType: 'jsonp',
                jsonpCallback: 'parseResponse'
            }).then(function (response) {
                var features = parser.readFeatures(response);
                if (features.length > 0) {
                    console.log(features.length);
                    clickedCellLayer.getSource().clear();
                    clickedCellLayer.getSource().addFeatures(features);
                    for (var i = 0; i < features.length; i++) {
                        var feature = features[i];
                        feature.setStyle(redStyle);
                    }
                    contextmenu.enable();
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

    // 打开小区干扰的窗口
    $("#showCellInterWinBtn").click(function() {
        $("#interference_dialogId").toggle();
    });

    //打开查找小区窗口
    $("#queryButton").click(function() {
        //$("#searchDiv").slideToggle();
        $("#searchDiv").toggle();
    });

    //搜小区
    $("#searchCellBtn").click(function () {
       var conditionType = $("#conditionType").val();
       var conditionValue = $("#conditionValue").val();
       var strExp=/^[\u4e00-\u9fa5A-Za-z0-9\s_-]+$/;
        if(conditionValue.trim() ===""){
            $("span#errorDiv").html("请输入小区信息！");
            return false;
        }
        if(!strExp.test(conditionValue)){
            $("span#errorDiv").html("含有非法字符！");
            return false;
        }
        if(!(conditionValue.length < 40)){
            $("span#errorDiv").html("输入信息过长！");
            return false;
        }

        searchCell(conditionType,conditionValue);
    });

    //搜邻区
    $("#searchNcellBtn").click(function () {
        var cellForNcell = $("#cellForNcell").val();
        var strExp=/^[\u4e00-\u9fa5A-Za-z0-9\s_-]+$/;
        if(cellForNcell.trim() ===""){
            $("span#errorDiv").html("请输入小区ID！");
            return false;
        }
        if(!strExp.test(cellForNcell)){
            $("span#errorDiv").html("含有非法字符！");
            return false;
        }
        if(!(cellForNcell.length < 40)){
            $("span#errorDiv").html("输入信息过长！");
            return false;
        }
        searchNcell(cellForNcell);
    });

    //搜频点
    $("#searchFreqBtn").click(function () {
        $("span#errorDiv").html("");
        var freq = $("#freqValue").val();
        var strExp=/^[\u4e00-\u9fa5A-Za-z0-9\s_-]+$/;
        if(freq.trim()===''){
            $("span#errorDiv").html("频点值不能为空！");
            return false;
        }
        if(!strExp.test(freq)){
            $("span#errorDiv").html("含有非法字符！");
            return false;
        }
        if(!(freq.length<40)){
            $("span#errorDiv").html("输入信息过长！");
            return false;
        }
        if(isNaN(freq)){
            $("span#errorDiv").html("频点应为数字！");
            return false;
        }
        searchFreq(freq);
    });

    //清除搜索结果
    $("#clearSearchResultBtn").click(function () {
        map.removeLayer(cellLayer);
        map.removeLayer(nCellLayer);
        map.removeLayer(lineLayer);
        map.removeLayer(thisCellLayer);
        map.removeLayer(clickedCellLayer);
    });
});

function searchCell(conditionType, conditionValue) {
    var longitude=113.3612,latitude=23.1247;
    $.ajax({
       url: '../../api/gsm-frequency-search/cell-search',
       dataType: 'text',
       data:{
           conditionType: conditionType,
           conditionValue: conditionValue
       },
       type: 'get',
       async: false,
       success: function (dat) {
           var data =eval('('+dat+')');
           console.log(data[0]);
           if(data===""|| data ===null||data.length === 0){
               showInfoInAndOut('info','未找到符合条件的小区！');
               return false;
           }
           longitude=parseFloat(data[0].longitude);
           latitude=parseFloat(data[0].latitude);
       }
    });
   // console.log(longitude);
    var cityId = parseInt($("#cityId").find("option:checked").val());
    //console.log(btsType);
    var requestParams ="AREA_ID=" + cityId ;
    if(conditionType === 'cellId'){
        requestParams += "and CELL_ID='"+ conditionValue +"'";
    }else if(conditionType === 'cellName'){
        requestParams += "and CELL_NAME like '%"+ conditionValue +"%'";
    }else if(conditionType === 'cellEnName'){
        requestParams += "and EN_NAME='"+ conditionValue +"'";
    }else if(conditionType === 'lac'){
        requestParams += "and LAC='"+ conditionValue +"'";
    }else{
        requestParams += "and CI='"+ conditionValue +"'";
    }
    map.removeLayer(cellLayer);
    cellLayer = new ol.layer.Tile({
        zIndex : 4,
        source : new ol.source.TileWMS({
            url : 'http://rno-gis.hgicreate.com/geoserver/rnoprod/wms',
            params : {
                'FORMAT' : 'image/png',
                'VERSION' : '1.1.1',
                tiled : true,
                STYLES : '',
                LAYERS : 'rnoprod:RNO_GSM_CELL_GEOM',
                CQL_FILTER : requestParams
            }
        }),
        opacity : 0.5
    });
    map.addLayer(cellLayer);
    map.getView().animate({
        center: [parseFloat(longitude), parseFloat(latitude)],
        duration: 2000
    });
}

function searchNcell(cellForNcell) {
    map.removeLayer(thisCellLayer);
    map.removeLayer(nCellLayer);
    map.removeLayer(lineLayer);
    var longitude=113.3612,latitude =23.1247;
    var cells = [];
    $.ajax({
        url: '../../api/gsm-frequency-search/ncell-search',
        dataType: 'text',
        data:{
            cellForNcell: cellForNcell
        },
        type: 'get',
        async: false,
        success: function (dat) {
            var data =eval('('+dat+')');
            //console.log(data[0]);
            if(data===""|| data ===null||data.length === 0){
                showInfoInAndOut('info','未找到符合条件的小区！');
                return false;
            }
            longitude=parseFloat(data[0].longitude);
            latitude=parseFloat(data[0].latitude);
            for(var i = 0; i< data.length; i ++){
                cells.push(data[i].cellId)
            }
        }
    });

   // console.log(cells);
    var ncellStr = "'" + cellForNcell + "',";
    $.each(cells, function (index, value) {
        ncellStr += "'" + value + "',";
    });
    //console.log(ncellStr);
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
                if (onefeature.get('CELL_ID') === cellForNcell) {
                    cellCoors = [onefeature.get('LONGITUDE'), onefeature.get('LATITUDE')];
                    onefeature.setStyle(redStyle);
                    thisCellLayer.getSource().addFeature(onefeature);
                } else {
                    ncellCoors.push([onefeature.get('LONGITUDE'), onefeature.get('LATITUDE')]);
                    onefeature.setStyle(orangeStyle);
                    nCellLayer.getSource().addFeature(onefeature);
                }
            }
            drawLine(cellCoors, ncellCoors);
        }
        $("#loading").css("display", "none");
    });

    map.addLayer(nCellLayer);
    map.addLayer(lineLayer);
    map.addLayer(thisCellLayer);

    map.getView().animate({
        center: [parseFloat(longitude), parseFloat(latitude)],
        duration: 2000
    });

}

function searchFreq(freq) {
    var longitude=113.3612,latitude=23.1247;
    var cityId = parseInt($("#cityId").find("option:checked").val());
    $.ajax({
        url: '../../api/gsm-frequency-search/frequency-search',
        dataType: 'text',
        data:{
            bcch: freq,
            cityId: cityId
        },
        type: 'get',
        async: false,
        success: function (dat) {
            var data =eval('('+dat+')');
            console.log(data[0]);
            if(data===""|| data ===null||data.length === 0){
                showInfoInAndOut('info','未找到符合条件的小区！');
                return false;
            }
            longitude=parseFloat(data[0].longitude);
            latitude=parseFloat(data[0].latitude);
        }
    });
    // console.log(longitude);

    //console.log(btsType);
    var requestParams ="AREA_ID=" + cityId + "and BCCH='"+ freq +"'";

    map.removeLayer(cellLayer);
    cellLayer = new ol.layer.Tile({
        zIndex : 4,
        source : new ol.source.TileWMS({
            url : 'http://rno-gis.hgicreate.com/geoserver/rnoprod/wms',
            params : {
                'FORMAT' : 'image/png',
                'VERSION' : '1.1.1',
                tiled : true,
                STYLES : '',
                LAYERS : 'rnoprod:RNO_GSM_CELL_GEOM',
                CQL_FILTER : requestParams
            }
        }),
        opacity : 0.5
    });
    map.addLayer(cellLayer);
    map.getView().animate({
        center: [parseFloat(longitude), parseFloat(latitude)],
        duration: 2000
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
    if (isShowRightBox) {
        $(".switch_hidden").trigger("click");
    }
    $(t).siblings().removeClass('custom-bg');
    $(t).addClass('custom-bg');
}


//提示信息淡入淡出
function showInfoInAndOut(div, info) {
    var divX = $("#" + div);
    divX.html(info);
    divX.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

function showNoise(evt) {
    thisCellLayer.getSource().clear();
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
                content += '<thead style="white-space: nowrap"><th>小区ID</th><th>小区名称</th><th>BCCH</th></thead>';
                content += '<tbody>';
                // 获取多个重叠 feature
                for (var i = 0; i < allFeatureNum; i++) {
                    var feature = allFeatures[i];
                    console.log(feature);

                    content += '<tr style="word-break:break-all" onclick="addColor(this, false)">';
                    content += '<td style="display:none">' + i + '</td>';
                    content += '<td style="white-space: nowrap">' + feature.get('CELL_ID') + '</td>';
                    content += '<td>' + feature.get('CELL_NAME') + '</td>';
                    content += '<td style="white-space: nowrap">' + feature.get('BCCH') + '</td>';
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
                    var cellName = allFeatures[index].get('CELL_NAME');
                    $.ajax({
                        url: "../../api/gsm-frequency-search/cell-noise",
                        dataType: "json",
                        data: {
                            'cellId': cellId
                        },
                        async: false,
                        success: function (data) {
                            if (data !== '' && data !== null) {
                                console.log(data);


                            } else {
                                $("#loading").css("display", "none");
                                showInfoInAndOut('info', '未找到小区'+cellId+'的干扰记录！');
                            }
                        },error: function (XMLHttpRequest, textStatus) {
                        $("#loading").css("display", "none");
                            $("#loadingStatus").html('未找到小区'+cellName+'的干扰记录！')
                        },complete: function (XMLHttpRequest, textStatus) {
                            $("#loading").css("display", "none");
                        }
                    });
                });
            } else {
                console.log('No result');
            }
        });
    }
}

function showIn(evt) {
    thisCellLayer.getSource().clear();
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
                content += '<thead style="white-space: nowrap"><th>小区ID</th><th>小区名称</th><th>BCCH</th></thead>';
                content += '<tbody>';
                // 获取多个重叠 feature
                for (var i = 0; i < allFeatureNum; i++) {
                    var feature = allFeatures[i];
                    console.log(feature);

                    content += '<tr style="word-break:break-all" onclick="addColor(this, false)">';
                    content += '<td style="display:none">' + i + '</td>';
                    content += '<td style="white-space: nowrap">' + feature.get('CELL_ID') + '</td>';
                    content += '<td>' + feature.get('CELL_NAME') + '</td>';
                    content += '<td style="white-space: nowrap">' + feature.get('BCCH') + '</td>';
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
                    var cellName = allFeatures[index].get('CELL_NAME');
                    $.ajax({
                        url: "../../api/gsm-frequency-search/cell-in",
                        dataType: "json",
                        data: {
                            'cellId': cellId
                        },
                        async: false,
                        success: function (data) {
                            $("#loading").css("display", "none");
                            if (data !== '' && data !== null) {
                                console.log(data);


                            } else {

                                showInfoInAndOut('info', '未找到小区'+cellName+'的干扰记录！');
                            }
                            //console.log(data);
                        },error: function (XMLHttpRequest, textStatus) {
                            $("#loading").css("display", "none");
                            $("#loadingStatus").html('未找到小区'+cellName+'的干扰记录！')
                        },complete: function (XMLHttpRequest, textStatus) {
                            $("#loading").css("display", "none");
                        }
                    });
                });
            } else {
                console.log('No result');
            }
        });
    }
}

function showOut(evt) {


    thisCellLayer.getSource().clear();
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
                content += '<thead style="white-space: nowrap"><th>小区ID</th><th>小区名称</th><th>BCCH</th></thead>';
                content += '<tbody>';
                // 获取多个重叠 feature
                for (var i = 0; i < allFeatureNum; i++) {
                    var feature = allFeatures[i];
                    //console.log(feature);

                    content += '<tr style="word-break:break-all" onclick="addColor(this, false)">';
                    content += '<td style="display:none">' + i + '</td>';
                    content += '<td style="white-space: nowrap">' + feature.get('CELL_ID') + '</td>';
                    content += '<td>' + feature.get('CELL_NAME') + '</td>';
                    content += '<td style="white-space: nowrap">' + feature.get('BCCH') + '</td>';
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

                    var modalTbody =$("#freqintersituation tbody");
                    modalTbody.children().remove();
                    $("#cellFreq").html("BCCH:   TCH:");
                    $("#cellInterfereId").html("");
                    $("#cellname").text('');
                    $(element).popover('destroy');
                    $("#loading").show();
                    var index = $(this).find('td:first').text();
                    var cellId = allFeatures[index].get('CELL_ID');
                    var cellName = allFeatures[index].get('CELL_NAME');
                    var bcch = allFeatures[index].get('BCCH');
                    var tch =allFeatures[index].get('TCH');
                    var btsType = allFeatures[index].get('BTS_TYPE');
                    $.ajax({
                        url: "../../api/gsm-frequency-search/cell-out",
                        dataType: "json",
                        data: {
                            'cellId': cellId
                        },
                        async: false,
                        success: function (data) {
                            var datas = eval('('+data+')');
                            console.log(datas);
                            if(data === null){
                                $("#loading").css("display", "none");
                                showInfoInAndOut('info', '未找到小区'+cellName+'的干扰记录！');
                            }
                        },error: function (XMLHttpRequest, textStatus) {
                            $("#loading").css("display", "none");
                            showInfoInAndOut('info', '未找到小区'+cellName+'的干扰记录！');
                            $("#loadingStatus").html('未找到小区'+cellName+'的干扰记录！');
                            $("#cellname").text(cellName);
                            $("#cellFreq").html("BCCH:"+bcch+"<br/>TCH:"+tch);
                            $("#cellInterfereId").html(cellName);

                            for(var i = 0; i< tch.split(",").length;i++){
                                modalTbody.append("<tr><td>"+tch.split(',')[i]+"</td><td></td><td></td><td></td><td>"+btsType+"</td></tr>")
                            }
                        },complete: function (XMLHttpRequest, textStatus) {
                            $("#loading").css("display", "none");
                        }
                    });
                });
            } else {
                console.log('No result');
            }
        });
    }
}

function showFreqDialog(evt) {
    thisCellLayer.getSource().clear();
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
                content += '<thead style="white-space: nowrap"><th>小区ID</th><th>小区名称</th><th>BCCH</th></thead>';
                content += '<tbody>';
                // 获取多个重叠 feature
                for (var i = 0; i < allFeatureNum; i++) {
                    var feature = allFeatures[i];
                    console.log(feature);

                    content += '<tr style="word-break:break-all" onclick="addColor(this, false)">';
                    content += '<td style="display:none">' + i + '</td>';
                    content += '<td style="white-space: nowrap">' + feature.get('CELL_ID') + '</td>';
                    content += '<td>' + feature.get('CELL_NAME') + '</td>';
                    content += '<td style="white-space: nowrap">' + feature.get('BCCH') + '</td>';
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
                        url: "../../api/gsm-cell-gis/cell-detail",
                        dataType: "json",
                        data: {
                            'cellId': cellId
                        },
                        async: false,
                        success: function (data) {
                            if (data !== '' && data !== null) {
                                $("#loading").css("display", "none");
                                // console.log(data);
                                bcchCache =data[0].bcch;
                                tchCache =data[0].tch;
                                $("#cellBcch").val(bcchCache);
                                $("#cellTch").val(tchCache);
                                $("#freqCellId").val(data[0].cellId);
                                $("#freq_dialogId").toggle();
                            } else {
                                $("#loading").css("display", "none");
                                showInfoInAndOut('info', '没有找到频点数据！');
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

/**
 * 更新频点
 */
function updateCellFreq() {
    var bcch = $("#cellBcch").val();
    var tch = $("#cellTch").val();
    var tchArray = tch.split(",");
    //标记
    var flag = true;

    //判断是否存在数字与逗号以外的字符
    var regBcch = /^[0-9]+$/;
    var regTch = /^[0-9,]+$/;
    if(!regBcch.test(bcch)) {
        flag = false;
        alert("bcch不能输入数字以外的字符！");
        return;
    }
    if(!regTch.test(tch)) {
        flag = false;
        alert("tch不能输入数字与逗号以外的字符！");
        return;
    }

    //判断是否为空
    if(tch === "" || bcch === "") {
        flag = false;
        alert("tch与bcch不能为空！");
        return;
    }
    //判断bcch是否在tch里面
    /*for(var k = 0; k < tchArray.length; k++) {
        if(tchArray[k] === bcch) {
            flag = false;
            alert("bcch的频点不能出现在tch里面！");
            return;
        }
    }*/
    //判断tch的频点是否存在同频，邻频
    for(var i = 0; i < tchArray.length; i++) {
        var temp = tchArray[i];
        var tempL = Number(temp) - 1;
        var tempU = Number(temp) + 1;
        for(var j = i + 1; j < tchArray.length; j++) {
            if(tchArray[j] === temp
                || tchArray[j] === tempL
                || tchArray[j] === tempU) {
                flag = false;
                alert("频点{"+temp+"}在tch里面存在同频，邻频关系！");
                return;
            }
        }
    }
    //判断是否符合GSM类型
    var is900;
    if(bcchCache >= 1 && bcchCache <= 94) {
        is900 = true;
    }
    if(bcchCache >= 512 && bcchCache <= 635) {
        is900 = false;
    }
    if(is900) {
        if(bcch < 1 || bcch >94) {
            flag = false;
            alert("bcch的频点不符合GSM900要求！");
            return;
        }
        for(var i = 0; i < tchArray.length; i++) {
            if(tchArray[i] < 1 || tchArray[i] >94) {
                flag = false;
                alert("tch的频点不符合GSM900要求！");
                return;
            }
        }
    } else {
        if(bcch < 512 || bcch > 635) {
            flag = false;
            alert("bcch的频点不符合GSM1800要求！");
            return;
        }
        for(var i = 0; i < tchArray.length; i++) {
            if(tchArray[i] < 512 || tchArray[i] > 635) {
                flag = false;
                alert("tch的频点不符合GSM1800要求！");
                return;
            }
        }
    }
    var cellId = $("#freqCellId").val();
    if(flag) {
        $.ajax({
            url : '../../api/gsm-frequency-search/update-cell-freq-by-cellId',
            data : {
                'cellId' : cellId,
                'bcch' : bcch,
                'tch' : tch
            },
            dataType : 'json',
            type : 'post',
            success : function(data) {
                $("#freq_dialogId").hide();
                $("#info").css("background","green");
                showInfoInAndOut("info","更新频点成功！");
            },
            error : function(xhr, textstatus, e) {
                //出错隐藏编辑窗口
                $("#freq_dialogId").hide();
                showInfoInAndOut("info","更新频点出错！")
            }
        });
    } else {
        alert("bcch或者tch输入有误，请重新输入提交！");
    }

}

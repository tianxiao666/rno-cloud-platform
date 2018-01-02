var map, tiled, clickedCellLayer, cellLayer, thisCellLayer, lineLayer;
var popup;
var redStyle;
var whetherLoadCellToMap=false;
var ids =[];
var configIds =[];
$(function () {
    tab("div_tab", "li", "onclick");
    tab("div_tab1", "li", "onclick");
    $(".dialog").draggable();
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
            right: '286px'
        }, 'fast');
        $(".resource_list_box").show("fast");
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
            /* url: 'http://rno-omt.hgicreate.com/styles/rno-omt/{z}/{x}/{y}.png'*/
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

    //线图层
    lineLayer = new ol.layer.Vector({
        source: new ol.source.Vector(),
        zIndex: 6
    });

    //邻区图层
    nCellLayer = new ol.layer.Vector({
        source: new ol.source.Vector(),
        zIndex: 3
    });

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
            text: '显示邻区',
            data: 1,
            callback: showNcell
        }
    ];

    var contextmenu = new ContextMenu({
        width: 120,
        items: contextmenu_items
    });


    $("#queryCellAreaId").change(function () {
        var lon = parseFloat($(this).find("option:checked").attr("data-lon"));
        var lat = parseFloat($(this).find("option:checked").attr("data-lat"));
        if (map === undefined) {
            map = new ol.Map({
                target: 'map',
                layers: [clickedCellLayer, baseLayer,nCellLayer, thisCellLayer, lineLayer],
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
                var url = cellLayer.getSource().getGetFeatureInfoUrl(
                    evt.coordinate, view.getResolution(), view.getProjection(), {
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

                            $('#cellTable tbody tr').click(function () {
                                var index = $(this).find('td:first').text();
                                var feature = allFeatures[index];
                                $.ajax({
                                    url: "../../api/gsm-cell-gis/cell-detail",
                                    dataType: "json",
                                    data: {
                                        'cellId': feature.get('CELL_ID')
                                    },
                                    async: false,
                                    success: function (data) {
                                        var cell = data[0];
                                        $("#showCellLabelId").text(cell.cellId);
                                        $("#showCellNameId").text(cell.cellName);
                                        $("#showCellLacId").text(cell.lac);
                                        $("#showCellCiId").text(cell.ci);
                                        $("#showCellBcchId").text(cell.bcch);
                                        $("#showCellTchId").text(cell.tch);
                                        $("#showCellBsicId").text(cell.bsic);
                                        $("#showCellAzimuthId").text(cell.azimuth);
                                        $("#showCellDownId").text(cell.mDowntilt ? cell.mDowntilt : 0);
                                        $("#showCellBtsTypeId").text(cell.btsType);
                                        $("#showCellAntHeightId").text(cell.antennaHeight);
                                        $("#showCellCoverTypeId").text(cell.coverType);
                                        $("#showCellLngId").text(cell.longitude);
                                        $("#showCellLatId").text(cell.latitude);
                                        $("#showCellCoverareaId").text(cell.coverArea);
                                        $("#showCellEnNameId").text(cell.enName);


                                        $("#tab0_li").attr('class', '');
                                        $("#tab1_li").attr('class', 'selected');
                                        $("#tab2_li").attr('class', '');
                                        $("#div_tab_0").css('display', 'none');
                                        $("#div_tab_1").css('display', 'block');
                                        $("#div_tab_2").css('display', 'none');
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
            map.getView().animate({
                center: [parseFloat(lon), parseFloat(lat)],
                duration: 2000
            });
        }

    });

    //初始化区域
    initAreaSelectors({selectors: ["provinceId", "cityId", "queryCellAreaId"], coord: true});
    initAreaSelectors({selectors: ["provinceId1", "cityId1", null]});
    initAreaSelectors({selectors: ["provinceId2", "cityId2", "areaId2"]});

    //加载小区
    $("#cellConfigConfirmSelectionAnalysisBtn").click(function () {
        var cityId = parseInt($("#cityId").find("option:checked").val());
        map.removeLayer(cellLayer);
        cellLayer = new ol.layer.Tile({
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
        map.addLayer(cellLayer);
        whetherLoadCellToMap = true;
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

    //全网干扰查询
    $("#wholeNetInterferQuery").click(function () {
        if(!whetherLoadCellToMap){
            showInfoInAndOut("info","请先加载小区到地图！");
            return false;
        }
        var reSelected = false;
        var selAreaVal = $("#cityId").find("option:selected").val();
        if(configIds.length === 0){
            getCobsicCellWholenet(reSelected, selAreaVal, null);
        }else{
            getCobsicCellWholenet(reSelected, selAreaVal, configIds);
        }

    });

    //干扰查询
    $("#interferQuery").click(function () {
        if($("#bcch").val().trim() ==='' || $("#bsic").val().trim() ===''){
            showInfoInAndOut("info","BCCH和BSIC均不能为空！");
            return false;
        }
        if(!whetherLoadCellToMap){
            showInfoInAndOut("info","请先加载小区到地图！");
            return false;
        }

        var reSelected = false;
        var areaIdStr =$("#cityId").find("option:selected").val();
        if(configIds.length === 0){
            cobsiccell(reSelected, areaIdStr, null);
        }else{
            cobsiccell(reSelected, areaIdStr, configIds);
        }
    });

    //弹出重选配置模态框
    $("#showCellConfigBtn").click(function() {
        var reSelCellConfig_Dialog =$("#reSelCellConfig_Dialog");
        reSelCellConfig_Dialog.toggle();
        var display = reSelCellConfig_Dialog.css("display");
    });

    //恢复默认
    $("#restoreDefaultBtn").click(function () {
        $('#loadRefreshlistTable3').DataTable({
            "lengthChange": false,
            "ordering": false,
            "searching": false,
            "destroy": true,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        }).rows().remove().draw();
    });

    // AJAX 上传文件
    var progress = $('.upload-progress');
    var bar = $('.bar');
    var percent = $('.percent');

    //导入文件类型判断
    $("#importBtn").click(function () {
        var path =$("#file").val();
        var format = path.substring(path.lastIndexOf("."), path.length).toLowerCase();
        if (format !== '.csv') {
            showInfoInAndOut("info", "请上传csv格式的工参数据文件");
            return false;
        }
    });

    // 当上传文件域改变时，隐藏进度条
    $("input[name='file']").change(function () {
        progress.css("display", "none");
    });

    //上传
    $("#formImportCell").ajaxForm({
        url: "../../api/gsm-co-bsic-analysis/upload-file",
        beforeSend: function () {
            progress.css("display", "block");
            var percentVal = '0%';
            bar.width(percentVal);
            percent.html(percentVal);
        },
        uploadProgress: function (event, position, total, percentComplete) {
            var percentVal = percentComplete + '%';
            bar.width(percentVal);
            percent.html(percentVal);
        },
        success: function () {
            var percentVal = '100%';
            bar.width(percentVal);
            percent.html(percentVal);
            $("#info").css("background","green");
            showInfoInAndOut("info","文件导入成功！");
        }
    });

    //查询配置方案
    $("#queryCellConfigureBtn").click(function () {
        if($("input[name='searchType']:checked").val() !=='GSM'){
            $("#tab_2_queryResultTab").DataTable({
                "lengthChange": false,
                "ordering": false,
                "searching": false,
                "destroy": true,
                "language": {
                    url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
                }
            }).rows().remove().draw();
            showInfoInAndOut("info","小区配置信息不存在！");
            return false;
        }
        $("#form_tab_2").ajaxForm({
            url: '../../api/gsm-co-bsic-analysis/config-schema-query',
            dataType: 'text',
            success: function (data) {
                var i =0;
                //console.log(data);
                var datas =eval('('+data+')');
                var tab_2_queryResultTab= $("#tab_2_queryResultTab");
                tab_2_queryResultTab.DataTable().rows().remove().draw();
                tab_2_queryResultTab.css("line-height", "10px")
                    .DataTable({
                        "data": datas,
                        "columns": [
                            {"data": "AREANAME"},
                            {"data": "NAME"},
                            {"data": "CREATETIME"},
                            {"data": null}
                        ],
                        "columnDefs": [{
                            "render": function (row) {
                                var id = row['ID'];
                               return "<input name='bsicCheckbox' type='checkbox' value='"+id+"'>";
                            },
                            "targets": -1,
                            "data": null
                        }],
                        "lengthChange": false,
                        "ordering": false,
                        "searching": false,
                        "destroy": true,
                        "language": {
                            url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
                        }

                    });
            },error: function (err) {
                console.log(err);
            }
        });
    });


    //加载到分析列表
    $("#loadtoanalysisBtn").click(function () {
        ids = [];
        if($(":checkbox[name=bsicCheckbox]:checked").length === 0) {
            showInfoInAndOut("info","请至少选择一项配置纪录！");
            return false;
        }
        $("input[name='bsicCheckbox']").each(function () {
            if(this.checked){
                ids.push(this.value);
            }
        });

        $.ajax({
           url: '../../api/gsm-co-bsic-analysis/query-schemas-by-id',
           type: 'get',
           dataType: 'text',
           data:{ids: ids.toString()},
           async: false,
           success: function (data) {
               var datas = eval("("+data+")");
               var loadRefreshlistTable3 =  $("#loadRefreshlistTable3");
               loadRefreshlistTable3.DataTable().clear();
               loadRefreshlistTable3.css("line-height", "12px")
                   .DataTable({
                       "data": datas,
                       "columns": [
                           {"data": "AREANAME"},
                           {"data": "NAME"},
                           {"data": "CREATETIME"},
                           {"data": null}
                       ],
                       "columnDefs": [{
                           "render": function (row) {
                               var id = row['ID'];
                               return "<a id='removeTr'>移除</a>";
                           },
                           "targets": -1,
                           "data": null
                       }],
                       "lengthChange": false,
                       "ordering": false,
                       "searching": false,
                       "destroy": true,
                       "language": {
                           url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
                       }

                   });

           }
        });
    });

   $("#loadRefreshlistTable3 tbody").on('click','#removeTr',function () {
        $('#loadRefreshlistTable3').DataTable()
            .row($(this).parents('tr'))
            .remove()
            .draw();
   });
});


//全选表格条目
function operAllCheckbox(obj) {
    var check = !obj.checked;

    if (check ) {
        $("input[type='checkbox']").each(function () {
            this.checked = false;
        });
        check = false;
    } else {
        $("input[type='checkbox']").each(function () {
            this.checked = true;
        });
        check =true;
    }
}

//点击popup表格，添加选中行的背景色
function addColor(t, isShowRightBox) {
    if (isShowRightBox) {
        $(".switch_hidden").trigger("click");
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
                        url: "../../api/gsm-cell-gis/ncell-detail",
                        dataType: "json",
                        data: {
                            'cellId': cellId
                        },
                        async: false,
                        success: function (data) {
                            if (data != '' && data != null) {
                                console.log(data);
                                paintNcell(cellId, data);
                            } else {
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
};

//提示信息淡入淡出
function showInfoInAndOut(div, info) {
    var divX = $("#" + div);
    divX.html(info);
    divX.fadeIn(2000);
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

function getCobsicCellWholenet (reSelected, areaIdStr,cellConfigIds) {
    var sendData = {
        "reselected" : reSelected,
        "areaId" : areaIdStr,
        "configIds" : cellConfigIds
    };
    $(".loading").css("display", "block");
    $("#conditionForm").ajaxSubmit({
        type: "GET",
        data: sendData,
        timeout: 20000,
        url: "../../api/gsm-co-bsic-analysis/whole-net-cobsic-query",
        dataType: "text",
        async: true,
        success: function (data) {
            if(data ===null || data ===''){
                showInfoInAndOut('info','不存在co-bsic小区');
                return false;
            }
            var res = eval('('+data+')');
           // console.log(res);
            if(res['fail']){
                showInfoInAndOut('info',res['fail']);
            }else{
                createWholeNetInterferTable(res);
            }
        },
        error : function(XMLHttpRequest, textStatus) {
            console.log(textStatus);
        },
        complete:function(XMLHttpRequest,status){
            $(".loading").css("display", "none");
            if(status ==='timeout'){
                showInfoInAndOut('info','请求超时！');
            }

        }
    });


}


/**
 * 创建全网bcch/bsic匹配小区干扰组表
 */
function createWholeNetInterferTable(data) {
    var mes_obj = data;

    for (var key in mes_obj) {
        var isPanto = false;
        var cellLists = mes_obj[key].combinedCells;
        var cellscount = cellLists.length;// aa
        if (cellscount > 0) {
            // console.log("进入cellLists循环");
            var whetherNcell = cellLists[0]['whetherNcell'];
            var whetherComNcell = cellLists[0]['whetherComNcell'];
            var commonNcell = cellLists[0]['commonNcell'];
            var cs = cellLists[0]['combinedCell'].split(",");
            if (whetherNcell && whetherComNcell) {
                drawLineBetweenCells(cs[0], cs[1]);
                drawLineBetweenCells(cs[0], commonNcell);
                drawLineBetweenCells(cs[1], commonNcell);
            }
            if (whetherNcell) {
                drawLineBetweenCells(cs[0], cs[1]);
            } else {
                drawLineBetweenCells(cs[0], commonNcell);
                drawLineBetweenCells(cs[1], commonNcell);
                drawLineBetweenCells(cs[0], cs[1]);
            }
            if (!isPanto) {
                panToCell(cs[0]);
            }
            isPanto = true;
            var string = "";
            for (var k = 0; k < cs.length; k++) {
                if (!cs[k] || cs[k] === "") {
                    continue;
                }
                string += "<a title='点击移动到该小区' href='javascript:panToCell(\""
                    + cs[k] + "\")' style='margin-right:2px'>" + cs[k]
                    + "</a>";
            }
            var strcomme = "<a title='点击移动到该小区' href='javascript:panToCell(\""
                + (typeof(commonNcell) === undefined ? "" : commonNcell)
                + "\")' style='margin-right:2px'>"
                + (typeof(commonNcell) === undefined ? "" : commonNcell)
                + "</a>";
            var str1 = "<tr>"
                + "<td class='menuTd' style='width: 30%' rowspan=\""
                + cellscount + "\" align=\"center\">" + cobsic + "</td>"
                + "<td align=\"center\">" + string + " "
                + getNcellDesc(whetherNcell, whetherComNcell, strcomme)
                + "</td>" + "</tr>";

            var str3 = "";
            if (cellscount > 1) {
                for (var j = 1; j < cellscount; j++) {
                    var whetherNcell = cellLists[j]['whetherNcell'];
                    var whetherComNcell = cellLists[j]['whetherComNcell'];
                    var commonNcell = cellLists[j]['commonNcell'];
                    var cs = cellLists[j]['combinedCell'].split(",");
                    if (whetherNcell && whetherComNcell) {
                        drawLineBetweenCells(cs[0], cs[1]);
                        drawLineBetweenCells(cs[0], commonNcell);
                        drawLineBetweenCells(cs[1], commonNcell);
                    }
                    if (whetherNcell) {
                        drawLineBetweenCells(cs[0], cs[1]);
                    } else {
                        drawLineBetweenCells(cs[0], commonNcell);
                        drawLineBetweenCells(cs[1], commonNcell);
                        drawLineBetweenCells(cs[0], cs[1]);
                    }
                    var str = "";
                    for (var k = 0; k < cs.length; k++) {
                        if (!cs[k] || cs[k] === "") {
                            continue;
                        }
                        str += "<a title='点击移动到该小区' href='javascript:panToCell(\""
                            + cs[k]
                            + "\")' style='margin-right:2px'>"
                            + cs[k] + "</a>";
                    }
                    var strcomm = "<a title='点击移动到该小区' href='javascript:panToCell(\""
                        + (typeof(commonNcell) === undefined
                            ? ""
                            : commonNcell)
                        + "\")' style='margin-right:2px'>"
                        + (typeof(commonNcell) === undefined
                            ? ""
                            : commonNcell) + "</a>";
                    str3 += "<tr>"
                        + "<td align=\"center\">"
                        + str
                        + " "
                        + getNcellDesc(whetherNcell, whetherComNcell,
                            strcomm) + "</td></tr>";
                }
                $("#interfertable").append(str1 + str3);
            } else {
                $("#interfertable").append(str1);
            }
        }
        // }
    }
}


function drawLineBetweenCells(x,y) {
    var cell1 ="'"+x+"'"+",";
    var cell2 ="'"+y+"'";
    var filter = encodeURIComponent("CELL_ID in (" + cell1+cell2 + ")");
    //console.log(filter);

    var url = 'http://rno-gis.hgicreate.com/geoserver/rnoprod/ows?service=WFS&version=1.1.1' +
        '&request=GetFeature&typeName=rnoprod:RNO_GSM_CELL_GEOM&maxFeatures=50&' +
        'outputFormat=text%2Fjavascript&CQL_FILTER=' + filter;
    var parser = new ol.format.GeoJSON();
    $.ajax({
        url: url,
        dataType: 'jsonp',
        jsonpCallback: 'parseResponse'
    }).then(function (response) {
        var features = parser.readFeatures(response);
        var xCoors =[];
        var yCoors =[];
        if(features.length){
            for(var n = 0; n < features.length; n++){
                if(features[n].get('CELL_ID')===x){
                    xCoors =[features[n].get('LONGITUDE'), features[n].get('LATITUDE')];
                    features[n].setStyle(redStyle);
                    thisCellLayer.getSource().addFeature(features[n]);
                }else{
                    yCoors = [features[n].get('LONGITUDE'), features[n].get('LATITUDE')];
                    features[n].setStyle(orangeStyle);
                    thisCellLayer.getSource().addFeature(features[n]);
                }
            }

            var line = new ol.geom.LineString([xCoors,yCoors]);
            var feature = new ol.Feature({geometry : line});
            feature.setStyle(new ol.style.Style({
                stroke: new ol.style.Stroke({
                    color: 'black',
                    width: 1
                })
            }));
            lineLayer.getSource().addFeature(feature);
        }
    });


}

/**
 * 判断是否相邻或共邻,返回描述信息
 */
function getNcellDesc(whetherNcell,whetherComNcell,commonNcell){
    if(whetherNcell && whetherComNcell){
        return "相邻且与 "+commonNcell+"共邻";
    }
    if(whetherNcell){
        return "相邻 "+commonNcell;
    }
    if(whetherComNcell){
        return "不相邻但与 "+commonNcell+"共邻";
    }
}

function panToCell(cell) {
    if (cell == null || cell === undefined) {
        return;
    }
    var filter ='CELL_ID='+cell;
    var url = 'http://rno-gis.hgicreate.com/geoserver/rnoprod/ows?service=WFS&version=1.1.1' +
        '&request=GetFeature&typeName=rnoprod:RNO_GSM_CELL_GEOM&maxFeatures=50&' +
        'outputFormat=text%2Fjavascript&CQL_FILTER=' + filter;
    var parser = new ol.format.GeoJSON();
    $.ajax({
        url: url,
        dataType: 'jsonp',
        jsonpCallback: 'parseResponse'
    }).then(function (response) {
        var feature = parser.readFeatures(response);
        map.getView().animate({
            center: [parseFloat(feature.get('LONGITUDE')), parseFloat(feature.get('LATITUDE'))],
            duration: 2000
        });
    });
}

/**
 * bcch,bsic匹配干扰查询功能
 */
function cobsiccell(reSelected, areaIdStr, cellConfigIdStr) {
    var sendData = {
        "reSelected" : reSelected,
        "areaIds" : areaIdStr,
        "configIds" : cellConfigIdStr
    };
    $("#loading").css("display", "block");
    $("#conditionForm").ajaxSubmit({
        type : "GET",
        data : sendData,
        url : "../../api/gsm-co-bsic-analysis/cobsic-query-by-bcch-bsic",
        dataType : "text",
        async : true,
        success : function(data, textStatus) {

            var mes_obj = eval("(" + data + ")");
            // console.log(mes_obj);
            var bcch = $("#bcch").val();
            var bsic = $("#bsic").val();
            var cobsic = bcch + "," + bsic;
            // Js循环读取JSON数据，并增加下拉列表选项
            clearinterfertable();
            if (mes_obj['fail'] !== null
                && mes_obj['fail'] !== undefined) {
                // console.log(mes_obj['fail']);
                showInfoInAndOut("info", mes_obj['fail'])
            } else {
                createWholeNetInterferTable(mes_obj);
            }
        },
        error : function(XMLHttpRequest, textStatus) {
            alert("\u8fd4\u56de\u6570\u636e\u9519\u8bef\uff1a"
                + textStatus);
        },
        complete:function(){
            $("#loading").css("display", "none");
        }
    });

}

function clearinterfertable() {
   var table =$("#interfertable");
    var trslength = table.find("tr").length;
    var trslength1 = $("tr", table);

    for (var i = 1; i < trslength1.length; i++) {
        trslength1.eq(i).remove();
    }
}
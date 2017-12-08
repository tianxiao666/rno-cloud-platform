var map, tiled, clickedCellLayer, cellLayer, thisCellLayer;
var popup;
var redStyle;
$(function () {
    tab("div_tab", "li", "onclick");
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

    $("#queryCellAreaId").change(function () {
        var lon = parseFloat($(this).find("option:checked").attr("data-lon"));
        var lat = parseFloat($(this).find("option:checked").attr("data-lat"));
        if (map === undefined) {
            map = new ol.Map({
                target: 'map',
                layers: [baseLayer, clickedCellLayer, thisCellLayer],
                view: new ol.View({
                    center: ol.proj.fromLonLat([lon, lat]),
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

                            $('#cellTable tbody tr').click(function () {
                                var index = $(this).find('td:first').text();
                                var feature = allFeatures[index];
                                $.ajax({
                                    url: "/api/gsm-cell-gis/cell-detail",
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
                                        $("#showCellDownId").text(cell.mDowntilt);
                                        $("#showCellBtsTypeId").text(cell.btsType);
                                        $("#showCellAntHeightId").text(cell.antennaHeight);
                                        $("#showCellAntTypeId").text("");
                                        $("#showCellLngId").text(cell.longitude);
                                        $("#showCellLatId").text(cell.latitude);
                                        $("#showCellCoverareaId").text(cell.coverArea);
                                        $("#showCellFreqSectionId").text("");

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
            map.getView().setCenter(ol.proj.transform([lon, lat], 'EPSG:4326', 'EPSG:3857'));
        }
    });

    //初始化区域
    initAreaSelectors({selectors: ["provinceId", "cityId", "queryCellAreaId"], coord: true});

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
});

//点击popup表格，添加选中行的背景色
function addColor(t, isShowRightBox) {
    if (isShowRightBox) {
        $(".switch_hidden").trigger("click");
    }
    $(t).siblings().removeClass('custom-bg');
    $(t).addClass('custom-bg');
}

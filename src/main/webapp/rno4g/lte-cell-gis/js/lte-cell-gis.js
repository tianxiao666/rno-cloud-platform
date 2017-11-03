var map, cellLayer;
var popup;

$(function () {
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

    var baseLayer = new ol.layer.Tile({
        source: new ol.source.XYZ({
            url: 'http://rno-omt.hgicreate.com/styles/rno-omt/{z}/{x}/{y}.png',
            // url: 'http://rno-map.hgicreate.com/tiles/osm/{z}/{x}/{y}.png',
            zIndex: 1
        })
    });

    var clickedCellLayer = new ol.layer.Vector({
        source: new ol.source.Vector(),
        zIndex: 3
    });

    var redStyle = new ol.style.Style({
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
        console.log("区县改变。");
        var lon = parseFloat($(this).find("option:checked").attr("data-lon"));
        var lat = parseFloat($(this).find("option:checked").attr("data-lat"));
        if (map === undefined) {
            map = new ol.Map({
                target: 'map',
                layers: [clickedCellLayer, baseLayer],
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

                            var content = '<table id="cellTable" class="table table-striped">';
                            content += '<thead style="white-space: nowrap"><th>小区ID</th><th>小区名称</th><th>PCI</th></thead>';
                            content += '<tbody>';
                            // 获取多个重叠 feature
                            for (var i = 0; i < allFeatureNum; i++) {
                                var feature = allFeatures[i];
                                console.log(feature);

                                content += '<tr style="word-break:break-all">';
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
                                $("#showCellLabelId").text(feature.get('CELL_ID'));
                                $("#showCellNameId").text(feature.get('CELL_NAME'));
                                $("#showCellPciId").text(feature.get('PCI'));
                                $("#showCellLngId").text(feature.get('LONGITUDE'));
                                $("#showCellLatId").text(feature.get('LATITUDE'));
                                $("#showCellAzimuthId").text(feature.get('AZIMUTH'));
                                $("#showCellBandTypeId").text(feature.get('BAND_TYPE'));
                                $("#showCellEarfcnId").text(feature.get('EARFCN'));
                                $("#showCellCoverTypeId").text(feature.get('COVER_TYPE'));
                                /*
                                showCellGroundHeightId
                                showCellAntennaTypeId
                                showCellIntegratedId
                                showCellRruverId
                                showCellRrunumId
                                showCellRspowerId
                                showCellBandId
                                showCellCoverRangeId
                                 */
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

    initAreaSelector("provinceId", "cityId", "districtId");
    $("#districtId").trigger("change");

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
        if ($(this).val() === "显示小区名字") {
            $(this).val("关闭小区名字");
        } else {
            $(this).val("显示小区名字");
        }
    })
});


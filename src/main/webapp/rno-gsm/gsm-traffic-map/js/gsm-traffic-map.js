let map;
let rendererFactory = null;

let veryGoodConditionCellLayer;
let goodConditionCellLayer;
let normalConditionCellLayer;
let badConditionCellLayer;
let veryBadConditionCellLayer;
let Firebrick = '#B22222';
let Tomato = '#FF6347';
let Violet = '#EE82EE';
let Cyan = '#00FFFF';
let Yellow = '#00FF00';

$(function () {
    rendererFactory = new RendererFactory(false);// 缓存的渲染规则
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
            right: '380px'
        }, 'fast');
        $(".resource_list300_box").show("fast");
    });
    $(".zy_show").click(function () {
        $(".search_box_alert").slideToggle("fast");
    });


    let baseLayer = new ol.layer.Tile({
        zIndex: 1,
        source: new ol.source.XYZ({
            url: 'http://rno-omt.hgicreate.com/styles/rno-omt/{z}/{x}/{y}.png'
        })
    });
    let veryBadStyle = new ol.style.Style({
        stroke: new ol.style.Stroke({
            color: 'yellow',
            size: 5
        }),
        fill: new ol.style.Fill({
            color: Firebrick
        })
    });
    let badStyle = new ol.style.Style({
        stroke: new ol.style.Stroke({
            color: 'yellow',
            size: 5
        }),
        fill: new ol.style.Fill({
            color: Tomato
        })
    });
    let normalStyle = new ol.style.Style({
        stroke: new ol.style.Stroke({
            color: 'yellow',
            size: 5
        }),
        fill: new ol.style.Fill({
            color: Violet
        })
    });
    let goodStyle = new ol.style.Style({
        stroke: new ol.style.Stroke({
            color: 'yellow',
            size: 5
        }),
        fill: new ol.style.Fill({
            color: Cyan
        })
    });
    let veryGoodStyle = new ol.style.Style({
        stroke: new ol.style.Stroke({
            color: 'yellow',
            size: 5
        }),
        fill: new ol.style.Fill({
            color: Yellow
        })
    });

    veryBadConditionCellLayer = new ol.layer.Vector({
        zIndex: 9,
        source: new ol.source.Vector(),
        style: veryBadStyle
    });
    badConditionCellLayer = new ol.layer.Vector({
        zIndex: 8,
        source: new ol.source.Vector(),
        style: badStyle
    });
    normalConditionCellLayer = new ol.layer.Vector({
        zIndex: 7,
        source: new ol.source.Vector(),
        style: normalStyle
    });
    goodConditionCellLayer = new ol.layer.Vector({
        zIndex: 6,
        source: new ol.source.Vector(),
        style: goodStyle
    });
    veryGoodConditionCellLayer = new ol.layer.Vector({
        zIndex: 5,
        source: new ol.source.Vector(),
        style: veryGoodStyle
    });
    $("#districtId").change(function () {
        let lon = parseFloat($(this).find("option:checked").attr("data-lon"));
        let lat = parseFloat($(this).find("option:checked").attr("data-lat"));
        if (map === undefined) {
            map = new ol.Map({
                target: 'map',
                layers: [baseLayer, veryGoodConditionCellLayer, goodConditionCellLayer, normalConditionCellLayer, badConditionCellLayer, veryBadConditionCellLayer],
                view: new ol.View({
                    projection: 'EPSG:4326',
                    center: ol.proj.fromLonLat([lon, lat]),
                    zoom: 16
                })
            });
        } else {
            map.getView().setCenter([lon, lat]);
        }
    });
    $("#districtId").trigger("change");
    //初始化区域
    initAreaSelectors({selectors: ["provinceId", "cityId", "districtId"], coord: true});

    loadAndShowAnalysisList();
});

/**
 * 获取和展现被加载的分析列表
 */
function loadAndShowAnalysisList() {
    var data = "31,32,33";
    $("#analysisListTable").empty();
    $
        .ajax({
            url: '/portal/api/gsm-traffic-statics/get-cell-performance-quota-list',
            data: {
                'data': data
            },
            dataType: 'json',
            type: 'post',
            success: function (data) {
                if (!data) {
                    return;
                }
                // 在面板显示
                let htmlstr = "";
                let one;
                let trClass = "";
                for (let i = 0; i < data.length; i++) {
                    one = data[i]['stsAnaItemDetail'];
                    if (i % 2 === 0) {
                        trClass = "tb-tr-bg-coldwhite";
                    } else {
                        trClass = "tb-tr-bg-warmwhite";
                    }
                    if (one) {
                        htmlstr += "<tr class=\""
                            + trClass
                            + "\">"// table 内容 yuan.yw 修改 2013-10-28
                            + "  <td width=\"25%\" class=\"bd-right-white\" >    "
                            + "  <span >"
                            + one['areaName']
                            + "</span>"
                            + "  </td>"
                            + "  <td  width=\"40%\"  class=\"bd-right-white td_nowrap\">"
                            + "  <span >"
                            + getValidValue(one['stsType'], "未知")
                            + "</span>"
                            + "  </td>"
                            + "  <td  width=\"20%\"  class=\"td-standard-date bd-right-white td_nowrap\">"
                            + "  <span >"
                            + getValidValue(one['stsDate'], "未知")
                            + "</span>"
                            + "  </td>"
                            + "  <td width=\"20%\" class=\"bd-right-white td_nowrap\">"
                            + "  <span>"
                            + getValidValue(one['periodType'], "不规则指标")
                            + "</span>"
                            + "  </td>"
                            + "  <td width=\"5%\" class=\"bd-right-white\">"
                            + "  <input type=\"checkbox\" class=\"forselect\" name=\"checkbox\" value="+getValidValue(one['areaId'], "未知")+" id='"
                            + data[i]['configId']
                            + "' />"
                            + "  <label for=\"checkbox\"></label>"
                            + "  </td>"
                            + "  </tr>";
                    }
                }
                $("#analysisListTable").append(htmlstr);
                if (htmlstr !== "") {
                    $("#analysisBtnDiv").show();
                }
            }
        });
}

function getValidValue(v, defaultValue, precision) {
    if (v == null || v === undefined || v === "null" || v === "NULL"
        || v === "undefined" || v === "UNDEFINED") {
        if (defaultValue != null && defaultValue !== undefined)
            return defaultValue;
        else
            return "";
    }
    if (typeof v === "number") {
        try {
            v = new Number(v).toFixed(precision);
        } catch (err) {
            console.error("v=" + v + "," + err);
        }
    }
    return v;
}

function showTips(tip) {
    $(".loading_cover").css("display", "block");
}

function hideTips(tip) {
    $(".loading_cover").css("display", "none");
}

/**
 * 统计功能
 *
 * @param type
 *            指明统计类型：无线资源利用率，
 * @param action
 *            提交到后台的action名称
 * @param name
 *            如果name不为空，说明要获取type类型的name指定的范围的统计值。如获取type=无线资源利用率，name=超忙小区
 */
function commonstatics(type, action, name) {
    showTips("正在加载资源无线利用率")
    loadStaticsInfo(action, type, name, 0, function () {
        hideTips();
    });
}

/**
 * 从服务器加载指定指标的统计情况
 *
 * @param action
 * @param type 指标类型
 * @param name 指标类型下的某类型小区
 * @param startIndex 起始下标
 * @param callback 回调
 */
function loadStaticsInfo(action, type, name, startIndex, callback) {
    if (!type) {
        return;
    }
    let selectedList = "";
    $('input[name="checkbox"]:checked').each(function () {
        selectedList += ($(this).attr('id')) + ",";
    });

    if (selectedList.length <= 0) {
        animateInAndOut("operInfo", 500, 500, 1000, "operTip", "请先选择一个小区指标");
        return;
    }
    selectedList = selectedList.substring(0, selectedList.length - 1);
    $.ajax({
        url: "/portal/api/gsm-traffic-statics/" + action,
        data: {
            'stsCode': type,
            'startIndex': startIndex,
            'selectedList': selectedList
        },
        dataType: 'json',
        type: 'post',
        async: false,
        success: function (data) {
            if (!data) {
                return;
            }
            try {
                let stsresult = data['rnoStsResults'];
                startIndex = data['startIndex'];
                let hasMore = data['hasMore'];
                if (hasMore === true) {
                    loadStaticsInfo(action, type, name, startIndex, callback);
                } else {
                    callback();
                }
                veryGoodConditionCellLayer.getSource().clear();
                goodConditionCellLayer.getSource().clear();
                normalConditionCellLayer.getSource().clear();
                badConditionCellLayer.getSource().clear();
                veryBadConditionCellLayer.getSource().clear();

                let getFirstPointXY = 0;
                let lon = 0.0;
                let lat = 0.0;
                let legendJSON = [];
                let dataLoopTime = 0;
                if (!data["rnoStsResults"]) {
                    animateInAndOut("operInfo", 500, 500, 1000, "operTip", "查询结果为空");
                    return;
                }
                $.each(data["rnoStsResults"], function (key, value) {
                    let cellId = value['CELL'];
                    let avgValue = value['AVGVALUE'];
                    let cityId = $("#cityId").val();
                    let filter = `CELL_ID = '` + cellId + `' and AREA_ID = '` + cityId + `'`;
                    $.ajax({
                        url: "http://rno-gis.hgicreate.com/geoserver/rnoprod/ows",
                        data: {
                            service: 'WFS',
                            request: 'GetFeature',
                            typeName: 'rnoprod:RNO_GSM_CELL_GEOM',
                            outputFormat: 'application/json',
                            'CQL_FILTER': filter
                        },
                        success: function (response) {
                            let features = new ol.format.GeoJSON().readFeatures(response);
                            if (features.length) {
                                let a = addFeaturesToLayerAcoordingToType(type, avgValue, features);
                                let ifExist = false;
                                $.each(legendJSON, function (key, value) {
                                    if (value.name === a.name) {
                                        ifExist = true;
                                    }
                                })
                                if (!ifExist) {
                                    legendJSON.push(a);
                                }
                                getFirstPointXY += 1;
                                if (getFirstPointXY === 1) {
                                    lon = features[0].values_.LONGITUDE;
                                    lat = features[0].values_.LATITUDE;
                                    if (lon !== 0 && lat !== 0) {
                                        map.getView().animate({
                                            center: [lon, lat],
                                            duration: 1000,
                                            zoom: 16
                                        });
                                    }
                                }
                            }
                            dataLoopTime += 1;
                            if (dataLoopTime === data["rnoStsResults"].length) {
                                legendJSON.sort(sortJSON);
                                $("#legendExplain").html("");
                                let html = "";
                                $.each(legendJSON, function (key, value) {
                                    if (value.value !== "") {
                                        html += '<tr>'
                                            + '<td class="col-md-5"><div style = "width: 20px;height: 20px;background-color:' + value.color + ';float: right;"></div></td>'
                                            + '<td class="col-md-2">' + value.value + '</td>'
                                            + '<td class="col-md-5">' + value.name + '</td>'
                                            + '</tr>'
                                    } else {
                                        html += '<tr>'
                                            + '<td class="col-md-6"><div style = "width: 20px;height: 20px;background-color:' + value.color + ';float: right;"></div></td>'
                                            + '<td class="col-md-6">' + value.name + '</td>'
                                            + '</tr>'
                                    }
                                })
                                $("#legendExplain").html('<table class="table table-striped">' + html + '</table>');
                                let chineseName = getChineseNameByType(type);
                                $("#legendTitle").text(chineseName + "图例说明");
                            }
                        }
                    })
                })
            } catch (err) {
                console.error(err);
                hideTips("");
            }
        },
        error: function (err) {
            hideTips("");
            console.log(err)
        },
        complete: function () {
            hideTips("");
        }
    });
}

function getChineseNameByType(type) {
    let chineseName = "";
    if ("radioresourcerate" === type) {
        chineseName = "无线资源利用率";
    } else if ("accsucrate" === type) {
        chineseName = "接通率";
    } else if ("droprate" === type) {
        chineseName = "掉话率";
    } else if ("dropnum" === type) {
        chineseName = "掉话数";
    } else if ("handoversucrate" === type) {
        chineseName = "切换成功率";
    } else if ("veryidlecell" === type) {
        chineseName = "超闲小区";
    } else if ("overloadcell" === type) {
        chineseName = "超忙小区";
    } else if ("highuseradiocell" === type) {
        chineseName = "高无线利用率小区";
    } else if ("highcongindatacell" === type) {
        chineseName = "数据高拥塞率小区";
    } else if ("badlyicmcell" === type) {
        chineseName = "高干扰小区";
    }
    return chineseName;
}

function sortJSON(a, b) {
    return a.order - b.order;
}

function addFeaturesToLayerAcoordingToType(type, avgValue, features) {
    let thisTime;
    if ("radioresourcerate" === type) {
        if (avgValue < 30) {
            thisTime = {name: "超闲小区", value: "< 30%", order: 1, color: Yellow};
            veryGoodConditionCellLayer.getSource().addFeatures(features);
        } else if (avgValue < 70 && avgValue >= 30) {
            thisTime = {name: "闲小区", value: "30%~70%", order: 2, color: Cyan};
            goodConditionCellLayer.getSource().addFeatures(features);
        } else if (avgValue < 90 && avgValue >= 70) {
            thisTime = {name: "正常", value: "70%~90%", order: 3, color: Violet};
            normalConditionCellLayer.getSource().addFeatures(features);
        } else if (avgValue < 110 && avgValue >= 90) {
            thisTime = {name: "忙小区", value: "90%~110%", order: 4, color: Tomato};
            badConditionCellLayer.getSource().addFeatures(features);
        } else if (avgValue >= 110) {
            thisTime = {name: "超忙小区", value: ">= 110%", order: 5, color: Firebrick};
            veryBadConditionCellLayer.getSource().addFeatures(features);
        }
    } else if ("accsucrate" === type) {
        if (avgValue >= 98) {
            thisTime = {name: "优秀", value: ">= 98%", order: 1, color: Yellow};
            veryGoodConditionCellLayer.getSource().addFeatures(features);
        } else if (avgValue >= 95 && avgValue < 98) {
            thisTime = {name: "良好", value: "95%~98%", order: 2, color: Cyan};
            goodConditionCellLayer.getSource().addFeatures(features);
        } else if (avgValue >= 92 && avgValue < 95) {
            thisTime = {name: "正常", value: "92%~95%", order: 3, color: Violet};
            normalConditionCellLayer.getSource().addFeatures(features);
        } else if (avgValue >= 90 && avgValue < 92) {
            thisTime = {name: "较差", value: "90%~92%", order: 4, color: Tomato};
            badConditionCellLayer.getSource().addFeatures(features);
        } else if (avgValue < 90) {
            thisTime = {name: "严重", value: "< 90%", order: 5, color: Firebrick};
            veryBadConditionCellLayer.getSource().addFeatures(features);
        }
    } else if ("droprate" === type) {
        if (avgValue <= 2) {
            thisTime = {name: "优秀", value: "<= 2%", order: 1, color: Yellow};
            veryGoodConditionCellLayer.getSource().addFeatures(features);
        } else if (avgValue <= 4 && avgValue > 2) {
            thisTime = {name: "良好", value: "2%~4%", order: 2, color: Cyan};
            goodConditionCellLayer.getSource().addFeatures(features);
        } else if (avgValue <= 6 && avgValue > 4) {
            thisTime = {name: "正常", value: "4%~6%", order: 3, color: Violet};
            normalConditionCellLayer.getSource().addFeatures(features);
        } else if (avgValue <= 8 && avgValue > 6) {
            thisTime = {name: "较差", value: "6%~8%", order: 4, color: Tomato};
            badConditionCellLayer.getSource().addFeatures(features);
        } else if (avgValue > 8) {
            thisTime = {name: "严重", value: "> 8%", order: 5, color: Firebrick};
            veryBadConditionCellLayer.getSource().addFeatures(features);
        }
    } else if ("dropnum" === type) {
        if (avgValue < 500) {
            thisTime = {name: "优秀", value: "< 500", order: 1, color: Yellow};
            veryGoodConditionCellLayer.getSource().addFeatures(features);
        } else if (avgValue < 1000 && avgValue >= 500) {
            thisTime = {name: "良好", value: "500~1000", order: 2, color: Cyan};
            goodConditionCellLayer.getSource().addFeatures(features);
        } else if (avgValue < 1500 && avgValue >= 1000) {
            thisTime = {name: "正常", value: "1000~1500", order: 3, color: Violet};
            normalConditionCellLayer.getSource().addFeatures(features);
        } else if (avgValue < 2000 && avgValue >= 1500) {
            thisTime = {name: "较差", value: "1500~2000", order: 4, color: Tomato};
            badConditionCellLayer.getSource().addFeatures(features);
        } else if (avgValue >= 2000) {
            thisTime = {name: "严重", value: ">= 2000", order: 5, color: Firebrick};
            veryBadConditionCellLayer.getSource().addFeatures(features);
        }
    } else if ("handoversucrate" === type) {
        if (avgValue >= 98) {
            thisTime = {name: "优秀", value: ">= 98%", order: 1, color: Yellow};
            veryGoodConditionCellLayer.getSource().addFeatures(features);
        } else if (avgValue >= 95 && avgValue < 98) {
            thisTime = {name: "良好", value: "95%~98%", order: 2, color: Cyan};
            goodConditionCellLayer.getSource().addFeatures(features);
        } else if (avgValue >= 92 && avgValue < 95) {
            thisTime = {name: "正常", value: "92%~95%", order: 3, color: Violet};
            normalConditionCellLayer.getSource().addFeatures(features);
        } else if (avgValue >= 90 && avgValue < 92) {
            thisTime = {name: "较差", value: "90%~92%", order: 4, color: Tomato};
            badConditionCellLayer.getSource().addFeatures(features);
        } else if (avgValue < 90) {
            thisTime = {name: "严重", value: "< 90%", order: 5, color: Firebrick};
            veryBadConditionCellLayer.getSource().addFeatures(features);
        }
    } else if ("veryidlecell" === type) {
        thisTime = {name: "超闲小区", value: "", color: Yellow};
        veryGoodConditionCellLayer.getSource().addFeatures(features);
    } else if ("overloadcell" === type) {
        thisTime = {name: "超忙小区", value: "", color: Firebrick};
        veryBadConditionCellLayer.getSource().addFeatures(features);
    } else if ("highuseradiocell" === type) {
        thisTime = {name: "高无线利用率小区", value: "", color: Tomato};
        badConditionCellLayer.getSource().addFeatures(features);
    } else if ("highcongindatacell" === type) {
        thisTime = {name: "数据高拥塞率小区", value: "", color: Firebrick};
        veryBadConditionCellLayer.getSource().addFeatures(features);
    } else if ("badlyicmcell" === type) {
        thisTime = {name: "高干扰小区", value: "", color: Violet};
        normalConditionCellLayer.getSource().addFeatures(features);
    }
    return thisTime;
}

/**
 * 使一个元素渐渐展现然后又渐渐隐去
 */
function animateInAndOut(objId, timeIn, timeOut, stayTime, tipId, tips) {
    if (objId == null || objId === undefined) {
        return;
    }
    if (tipId && tips) {
        try {
            $("#" + tipId).html(tips);
        } catch (err) {
        }
    }
    try {
        if (typeof timeIn === "number" && typeof timeOut === "number") {
            $("#" + objId).fadeIn(timeIn, function () {
                window.setTimeout(function () {
                    $("#" + objId).fadeOut(timeOut);
                }, stayTime);
            });
        }
    } catch (err) {

    }
}


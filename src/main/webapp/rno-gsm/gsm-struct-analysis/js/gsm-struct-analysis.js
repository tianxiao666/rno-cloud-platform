var map,tiled;
$(function () {

    laydate.render({elem: '#measureDate', type: 'datetime', value: new Date()});
    //执行 laydate 实例 
    laydate.render({elem: '#begDate', type: 'datetime', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endDate', type: 'datetime', value: new Date()});

    laydate.render({elem: '#begDate2', type: 'datetime', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endDate2', type: 'datetime', value: new Date()});

    $(".draggable").draggable();
    $("#trigger").css("display", "none");

    //tab选项卡
    tab("div_tab_ncs_cell", "li", "onclick");//项目服务范围类别切换

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

    $("#provinceId").change(function () {
        var provinceId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, provinceId, "cityId");
        })
    });

    //初始化区域
    $.ajax({
        url: "../../data/area.json",
        dataType: "json",
        async: false,
        success: function (data) {
            console.log("取到数据。");
            renderArea(data, 0, "provinceId");
            $("#provinceId").change();
        }
    });

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

    $("#searchStructureTaskDT").click(function () {
        $('#optimizeResultDT').css("line-height", "12px");
        $('#optimizeResultDT').DataTable( {
            "ajax": "data/gsm-struct-analysis-list.json",
            "columns": [
                { "data": "JOB_NAME" },
                { "data": "JOB_RUNNING_STATUS" },
                {"data" : "CITY_NAME"},
                { "data": null },
                { "data": null },
                { "data": "LAUNCH_TIME" },
                { "data": "COMPLETE_TIME" },
                { "data": null }
            ],
            "columnDefs": [ {
                "render": function(data, type, row) {
                    return "--";
                },
                "targets": 3,
                "data": null
            },{
                "render": function(data, type, row) {
                    return row['BEG_MEA_TIME'] + " 至 "+row['END_MEA_TIME'];
                },
                "targets": 4,
                "data": null
            },{
                "render": function(data, type, row) {
                    return " <input type='button' value='下载结果文件'>" +
                        "<input type='button' value='查看运行报告' onclick='checkStructureTaskReport(\"4729\")'>" +
                        "<br><input type='button' value='查看渲染图' onclick='viewRenderImg(\"4729\")'>";
                },
                "targets": 7,
                "data": null
            }
            ],
            "lengthChange": true,
            "ordering": false,
            "searching": true,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        });
    });
});

function loadMap(){
    var baseLayer = new ol.layer.Tile({
        source: new ol.source.XYZ({
            url: 'http://rno-omt.hgicreate.com/styles/rno-omt/{z}/{x}/{y}.png'
        })
    });
    map = new ol.Map({
        target: 'map',
        layers: [baseLayer],
        view: new ol.View({
            center: ol.proj.fromLonLat([113.3612, 23.1247]),
            zoom: 16
        })
    });
}

// 渲染区域
function renderArea(data, parentId, areaMenu) {
    var arr = data.filter(function (v) {
        return v.parentId === parentId;
    });
    if (arr.length > 0) {
        console.log("ARR=" + arr.length);
        var areaHtml = [];
        $.each(arr, function (index) {
            var area = arr[index];
            areaHtml.push("<option value='"+area.id+"'>"+area.name+"</option>");
        });
        $("#" + areaMenu).html(areaHtml.join(""));
    } else {
        console.log("父ID为" + parentId + "时未找到任何下级区域。");
    }
}

function checkStructureTaskReport(jobId) {
    $("#viewReportForm").find("input#hiddenJobId").val(jobId);

    $("#reportDiv").css("display","block");
    $("#structureTaskDiv").css("display","none");
    $("#renderImgDiv").css("display","none");
    $('#runResultDT').css("line-height", "12px");
    $('#runResultDT').DataTable( {
        "ajax": "data/gsm-struct-analysis-report.json",
        "columns": [
            { "data": "STAGE" },
            { "data": "BEG_TIME" },
            {"data" : "END_TIME"},
            { "data": "STATE" },
            { "data": null }
        ],
        "columnDefs":[
            {
                "render": function(data, type, row) {
                    return " ";
            },
                "targets": -1,
                "data": null

            }
        ],
        "lengthChange": false,
        "ordering": false,
        "searching": false,
        "language": {
            url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
        }
    });
}

//查看渲染图
function viewRenderImg(jobId) {
    //保存jobId用于获取对应的渲染图
    $("#reportNcsTaskId").val(jobId);
    //加载渲染图
    var flag = confirm("是否加载渲染图？");
    if(!flag) {
        return;
    }
    //加载默认渲染规则
    // showRendererRuleColor();

    $("#renderImgDiv").css("display","block");
    $("#structureTaskDiv").css("display","none");
    $("#reportDiv").css("display","none");
    loadMap();
}

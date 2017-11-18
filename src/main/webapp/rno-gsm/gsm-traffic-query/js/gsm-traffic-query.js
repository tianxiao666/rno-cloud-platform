$(function () {

    $(".draggable").draggable();
    $("#trigger").css("display", "none");

    //执行 laydate 实例 
    laydate.render({elem: '#beginTime', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#latestAllowedTime', value: new Date()});

    laydate.render({elem: '#beginTime1', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#latestAllowedTime1', value: new Date()});

    laydate.render({elem: '#beginTime2', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#latestAllowedTime2', value: new Date()});

    $("#provinceId").change(function () {
        var provinceId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, provinceId, "cityId");
            var cityId = parseInt($("#cityId").find("option:checked").val());
            renderArea(data, cityId, "areaId");
        })
    });

    $("#cityId").change(function () {
        var cityId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, cityId, "areaId");
        })
    });

    $("#provinceId1").change(function () {
        var provinceId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, provinceId, "cityId1");
            var cityId = parseInt($("#cityId1").find("option:checked").val());
            renderArea(data, cityId, "areaId1");
        })
    });

    $("#cityId1").change(function () {
        var cityId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, cityId, "areaId1");
        })
    });

    $("#provinceId2").change(function () {
        var provinceId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, provinceId, "cityId2");
            var cityId = parseInt($("#cityId2").find("option:checked").val());
            renderArea(data, cityId, "areaId2");
        })
    });

    $("#cityId2").change(function () {
        var cityId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, cityId, "areaId2");
        })
    });

    //初始化区域
    $.ajax({
        url: "../../data/area.json",
        dataType: "json",
        async: false,
        success: function (data) {
            renderArea(data, 0, "provinceId");
            renderArea(data, 0, "provinceId1");
            renderArea(data, 0, "provinceId2");
            $("#provinceId").change();
            $("#provinceId1").change();
            $("#provinceId2").change();
        }
    });

    //小区语音业务指标查询
    $("#queryCellTrafficBtn").click(function () {
        $('#queryCellTrafficTab').css("line-height", "12px");
        $('#queryCellTrafficTab').DataTable( {
            "ajax": "data/gsm-traffic-query-cell-voice.json",
            "columns": [
                { "data": "data" },
                { "data": "period" },
                { "data": "bsc" },
                { "data": "cellEnglishName" },
                { "data": "cellChineseName" },
                { "data": "tRate" },
                { "data": "defChannel" },
                { "data": "availableChannel" },
                { "data": "carrierNum" },
                { "data": "wirelessUseRate" },
                { "data": "trafficVolume" },
                { "data": "preTrafficVolume" }
            ],
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        } );
    });

    //小区数据业务指标
    $("#queryCellDataBtn").click(function () {
        $('#queryCellDataTab').css("line-height", "12px");
        $('#queryCellDataTab').DataTable( {
            "ajax": "data/gsm-traffic-query-cell-data.json",
            "columns": [
                { "data": "data" },
                { "data": "period" },
                { "data": "bsc" },
                { "data": "cellEnglishName" },
                { "data": "cellChineseName" },
                { "data": "tRate" },
                { "data": "defChannel" },
                { "data": "availableChannel" },
                { "data": "carrierNum" },
                { "data": "wirelessUseRate" },
                { "data": "trafficVolume" },
                { "data": "preTrafficVolume" }
            ],
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        } );
    });

    //城市网络质量指标
    $("#queryCityNetworkBtn").click(function () {
        $('#queryCityNetworkDT').css("line-height", "12px");
        $('#queryCityNetworkDT').DataTable( {
            "ajax": "data/gsm-traffic-query-city-network.json",
            "columns": [
                { "data": "staticTime" },
                { "data": "grade" },
                { "data": "areaName" },
                { "data": "score" },
                { "data": "indexClass" },
                { "data": "indexName" },
                { "data": "indexValue" }
            ],
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        });
    });
});

// 渲染区域
function renderArea(data, parentId, areaMenu) {
    var arr = data.filter(function (v) {
        return v.parentId === parentId;
    });
    if (arr.length > 0) {
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
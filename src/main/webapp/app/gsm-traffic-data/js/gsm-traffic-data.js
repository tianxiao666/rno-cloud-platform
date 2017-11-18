$(function () {

    $(".draggable").draggable();
    $("#trigger").css("display", "none");

    //执行 laydate 实例 
    laydate.render({elem: '#beginTime', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#latestAllowedTime', value: new Date()});

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
            renderArea(data, 0, "provinceId2");
            $("#provinceId").change();
            $("#provinceId2").change();
        }
    });

    $("#queryTrafficBtn").click(function () {
        $('#telequeryResultDT').css("line-height", "12px");
        $('#telequeryResultDT').DataTable( {
            "ajax": "data/gsm-traffic-data-index-query.json",
            "columns": [
                { "data": "area" },
                { "data": "indexType" },
                { "data": "teleTime" },
                { "data": null }
            ],
            "columnDefs": [ {
                "render": function(data, type, row) {
                    return "<input  name='selectall' id='2' type='checkbox'>";
                },
                "targets": -1,
                "data": null
            }
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
$(function () {

    $(document).ready(function () {
        $(".draggable").draggable();
        $("#trigger").css("display", "none");

    });

    //执行 laydate 实例 
    laydate.render({elem: '#uploadQueryBegDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#uploadQueryEndDate', value: new Date()});

    laydate.render({elem: '#kpiMeaBegDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#kpiMeaEndDate', value: new Date()});

    $("#provincemenu").change(function () {
        var provinceId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, provinceId, "citymenu");
        })
    });

    $("#provincemenu2").change(function () {
        var provinceId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, provinceId, "citymenu2");
        })
    });

    //初始化区域
    $.ajax({
        url: "../../data/area.json",
        dataType: "json",
        async: false,
        success: function (data) {
            renderArea(data, 0, "provincemenu");
            renderArea(data, 0, "provincemenu2");
            $("#provincemenu").change();
            $("#provincemenu2").change();
        }
    });

    $("#queryImportBtn").click(function () {
        $('#queryImportTab').css("line-height", "12px");
        $('#queryImportTab').DataTable( {
            "ajax": "data/lte-kpi-data-import-list.json",
            "columns": [
                { "data": "cityId" },
                { "data": "uploadTime" },
                { "data": "fileName" },
                { "data": "fileSize" },
                { "data": "launchTime" },
                { "data": "completeTime" },
                { "data": "account" },
                { "data": "fileStatus" }
            ],
        "language": {
            url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
        }
        } );
    });

    $("#searchDataBtn").click(function () {
        $('#queryDataTab').css("line-height", "12px");
        $('#queryDataTab').DataTable( {
            "ajax": "data/lte-kpi-data-query.json",
            "columns": [
                { "data": "cityId" },
                { "data": "measureTime" },
                { "data": "measureDataNum" },
                { "data": "enterSysTime" }
            ],
        "language": {
            url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
        }
        } );
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
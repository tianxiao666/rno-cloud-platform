
$(function () {

    //执行 laydate 实例 
    laydate.render({elem: '#begUploadDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endUploadDate', value: new Date()});

    $("#cityId").change(function () {
        var cityId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, cityId, "districtId", true, true);
        })
    });

    $("#provinceId").change(function () {
        var provinceId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, provinceId, "cityId");
            $("#cityId").change();
        })
    });

    $("#provincemenu").change(function () {
        var provinceId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, provinceId, "citymenu");
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
            renderArea(data, 0, "provincemenu");
            $("#provinceId").change();
            $("#provincemenu").change();
        }
    });

    $("#searchImportBtn").click(function () {
        $('#importListTab').css("line-height", "12px");
        $('#importListTab').DataTable( {
            "ajax": "data/gsm-cell-data-import.json",
            "columns": [
                { "data": "uploadTime" },
                { "data": "fileName" },
                { "data": "fileSize" },
                { "data": "launchTime" },
                { "data": "completeTime" },
                { "data": "account" },
                { "data": "fileStatus" }
            ],
            "lengthChange": false,
            // "ordering": false,
            "searching": false,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        } );
    });

    $("#subBtn").click(function () {
        $('#queryResultTab').css("line-height", "12px");
        $('#queryResultTab').DataTable( {
            "ajax": "data/gsm-cell-data-query.json",
            "columns": [
                { "data": "CELL_ID" },
                { "data": "CELL_NAME" },
                { "data": "LAC" },
                { "data": "CI" },
                { "data": "BCCH" },
                { "data": "BSIC" },
                { "data": "TCH" },
                { "data": null }
            ],
            "columnDefs": [ {
                "render": function(data, type, row) {
                    return "<a onclick=\"alert('删除CELL: " + row["CELL_ID"] + "')\">删除</a>";
                },
                "targets": -1,
                "data": null
            }
            ],
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        } );
    });
});

// 渲染区域
function renderArea(data, parentId, areaMenu, hasAllOption) {
    var arr = data.filter(function (v) {
        return v.parentId === parentId;
    });
    var areaHtml = [];
    if (hasAllOption) {
        areaHtml.push("<option value='-1'>全部</option>");
    }
    if (arr.length > 0) {
        console.log("ARR=" + arr.length);
        $.each(arr, function (index) {
            var area = arr[index];
            areaHtml.push("<option value='"+area.id+"'>"+area.name+"</option>");
        });
    } else {
        console.log("父ID为" + parentId + "时未找到任何下级区域。");
    }
    $("#" + areaMenu).html(areaHtml.join(""));
}
$(function () {

    // 执行 laydate 实例 
    laydate.render({elem: '#begUploadDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endUploadDate', value: new Date()});

    //执行 区域 实例 
    $("#province-id").change(function () {
        var provinceId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, provinceId, "city-id");
            var cityId = parseInt($("#city-id").find("option:checked").val());
            renderArea(data, cityId, "area-id");
        })
    });

    $("#city-id").change(function () {
        var cityId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, cityId, "area-id");
        })
    });

    $("#province-name").change(function () {
        var provinceId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, provinceId, "city-name");
            var cityId = parseInt($("#city-name").find("option:checked").val());
            renderArea(data, cityId, "area-name");
        })
    });

    $("#city-name").change(function () {
        var cityId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, cityId, "area-name");
        })
    });


    //初始化区域
    $.ajax({
        url: "../../data/area.json",
        dataType: "json",
        async: false,
        success: function (data) {
            renderArea(data, 0, "province-id");
            $("#province-id").change();
            renderArea(data, 0, "province-name");
            $("#province-name").change();
        }
    });

    var x=0;
    $("#queryDtDataBtn").click(function () {
        $('#queryResultDT').css("line-height", "12px");
        $('#queryResultDT').DataTable( {
            "ajax": "data/gsm-dt-data-query-result.json",
            "columns": [
                { "data": "taskName" },
                { "data": "networkStandard" },
                { "data": "testType" },
                { "data": "measureTime" },
                { "data": "factory" },
                { "data": "equipment" },
                { "data": "version" },
                { "data": null }
            ],
            "columnDefs": [ {
                "render": function(data, type, row) {
                    return "<input  name='selectall' id='dtCheckbox1' name='dtCheckbox' type='checkbox'>";
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
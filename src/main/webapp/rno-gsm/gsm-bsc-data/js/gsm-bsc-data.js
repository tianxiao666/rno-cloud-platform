$(function () {

    $("#addSingleBscDiv").draggable();
    //tab选项卡
    tab("div_tab", "li", "onclick");//项目服务范围类别切换
    $("#provinceId").change(function () {
        var provinceId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, provinceId, "cityId");
        })
    });

    $("#provinceId2").change(function () {
        var provinceId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, provinceId, "cityId2");
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

    $("#queryBtn").click(function () {
        $('#queryResultTab').css("line-height", "12px");
        $('#queryResultTab').DataTable( {
            "ajax": "data/gsm-bsc-data.json",
            "columns": [
                { "data": "AREA_NAME" },
                { "data": "CHINESENAME" },
                { "data": "MANUFACTURERS" },
                { "data": null }
            ],
            "columnDefs": [ {
                "render": function(data, type, row) {
                    return "<a onclick=\"alert('删除BSC: " + row["BSC_ID"] + "')\">删除</a>";
                },
                "targets": -1,
                "data": null
            }
            ],
            // "lengthChange": false,
            // "ordering": false,
            // "searching": false,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        } );
    })
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

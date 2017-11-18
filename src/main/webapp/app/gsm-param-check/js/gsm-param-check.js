$(function () {

    // 执行 laydate 实例 
    laydate.render({elem: '#uploadDate', value: new Date()});

    //执行 区域 实例 
    $("#province-menu").change(function () {
        var cityId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, cityId, "city-menu");
        })
    });

    //初始化区域
    $.ajax({
        url: "../../data/area.json",
        dataType: "json",
        async: false,
        success: function (data) {
            renderArea(data, 0, "province-menu");
            $("#province-menu").change();
        }
    });

    $("#checkBtnDT").click(function () {
        $('#parameterCheckResultDT').css("line-height", "12px");
        $('#parameterCheckResultDT').DataTable( {
            "ajax": "data/gsm-param-check-data.json",
            "columns": [
                { data: "BSC" },
                { data: "CELL" },
                { data: "CHGR" },
                { data: "HOP" },
                { data: "SPOT_NUM" },
                { data: "SPOT_LIST" },
                { data: "ORDER"}
            ],
            "lengthChange": true,
            "ordering": true,
            "searching": true,
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
            areaHtml.push("<option value='" + area.id + "'>");
            areaHtml.push(area.name + "</option>");
        });
        $("#" + areaMenu).html(areaHtml.join(""));
    } else {
        console.log("父ID为" + parentId + "时未找到任何下级区域。");
    }
}

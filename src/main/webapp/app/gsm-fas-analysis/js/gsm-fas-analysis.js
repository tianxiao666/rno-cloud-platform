$(function () {

    $(".draggable").draggable();
    $("#trigger").css("display", "none");

    //执行 laydate 实例 
    laydate.render({elem: '#fasMeaBegDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#fasMeaEndDate', value: new Date()});

    $("#provinceId1").change(function () {
        var provinceId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, provinceId, "cityId1");
        })
    });

    //初始化区域
    $.ajax({
        url: "../../data/area.json",
        dataType: "json",
        async: false,
        success: function (data) {
            renderArea(data, 0, "provinceId1");
            $("#provinceId1").change();
        }
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

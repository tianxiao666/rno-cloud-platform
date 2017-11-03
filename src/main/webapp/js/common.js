// 初始化区域联动选择
function initAreaSelector(provinceId, cityId, districtId) {
    renderArea(0, provinceId);
    $("#" + provinceId).change(function () {
        var areaId = parseInt($(this).find("option:checked").val());
        renderArea(areaId, cityId);
    }).trigger("change");

    if (districtId !== undefined) {
        $("#" + cityId).change(function () {
            var areaId = parseInt($(this).find("option:checked").val());
            renderArea(areaId, districtId, true);
        }).trigger("change");
    }
}

// 渲染区域
function renderArea(parentId, areaSelector, boolLonLat) {
    $.ajax({
        url: "/api/areas",
        data: {"parentId": parentId},
        dataType: "json",
        async: false,
        success: function (data) {
            if (data.length > 0) {
                var areaHtml = [];
                $.each(data, function (index) {
                    var area = data[index];

                    if (boolLonLat) {
                        areaHtml.push("<option value='" + area.id + "' data-lon='"
                            + area.longitude + "' data-lat='" + area.latitude + "'>");
                    } else {
                        areaHtml.push("<option value='" + area.id + "'>");
                    }

                    areaHtml.push(area.name + "</option>");
                });
                $("#" + areaSelector).html(areaHtml.join(""));
            } else {
                console.log("父ID为" + parentId + "时未找到任何下级区域。");
            }
        }
    });
}

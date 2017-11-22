// 区域联动
function initAreaSelectors(options) {
    var $provinceId = $("#" + options.selectors[0]);

    // 缺省地市，如广东省广州市天河区 440106
    var defaultAreaId = 440106;

    if (options.defaultAreaId !== undefined) {
        defaultAreaId = options.defaultAreaId;
    }

    var coords = [false, false, false];

    // options.coord 属性表示最后一个选择器是否添加经纬度
    if (options.coord !== undefined) {
        coords[options.selectors.length - 1] = options.coord;
    }

    renderArea(0, $provinceId.attr("id"), coords[0]);

    // 缺省省份
    if (defaultAreaId > 0) {
        $provinceId.find("option[value='" + (Math.round(defaultAreaId / 10000) * 10000) + "']").attr("selected", true);
    }

    if (options.selectors.length > 1) {
        var $cityId = $("#" + options.selectors[1]);
        if (options.selectors.length > 2) {
            var $districtId = $("#" + options.selectors[2]);
            $cityId.change(function () {
                var areaId = parseInt($(this).find("option:checked").val());
                renderArea(areaId, $districtId.attr("id"), coords[2]);

                // 缺省区县
                if ((defaultAreaId % 100) > 0) {
                    $districtId.find("option[value='" + defaultAreaId + "']").attr("selected", true);
                }

                if(options.relate !== undefined) {
                    $("#" + options.selectors[1] + "1").text($cityId.find("option:selected").text());
                }

                $districtId.trigger("change");
            });
        }

        $provinceId.change(function () {
            var areaId = parseInt($(this).find("option:checked").val());
            renderArea(areaId, $cityId.attr("id"), coords[1]);

            // 缺省城市
            if ((defaultAreaId % 10000) > 99) {
                $cityId.find("option[value='" + (Math.round(defaultAreaId / 100) * 100) + "']").attr("selected", true);
            }

            if(options.relate !== undefined) {
                $("#" + options.selectors[0] + "1").text($provinceId.find("option:selected").text());
            }

            $cityId.trigger("change");
        });

        $provinceId.trigger("change");

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
                $("#" + areaSelector).empty();
                console.log("父ID为" + parentId + "时未找到任何下级区域。");
            }
        }
    });
}

/**
 * 设置导航标题
 * @param navTitleId 放置导航标题元素的ID
 */
function setNavTitle(navTitleId) {
    var navTitle = "";
    var param = location.search.split("nav=");
    if (param.length > 1) {
        navTitle = decodeURI(param[1]);
    }
    if (navTitle !== "") {
        $("#" + navTitleId).html("当前位置：" + navTitle);
    }
}

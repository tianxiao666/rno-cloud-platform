$(function () {

    // 执行 laydate 实例 
    laydate.render({elem: '#begUploadDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endUploadDate', value: new Date()});
    laydate.render({elem: '#fileDate', value: new Date()});
    laydate.render({elem: '#beginTestDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endTestDate', value: new Date()});

    //显示隐藏导入窗口
    $("#importTitleDiv").click(function(){
        var flag = $("#importDiv").is(":hidden");//是否隐藏
        if(flag) {
            $(".importContent").show("fast");
        } else {
            $(".importContent").hide("fast");
        }
    });

    //执行 区域 实例 
    $("#provincemenu").change(function () {
        var cityId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, cityId, "citymenu");
        })
    });
    $("#province-menu-2").change(function () {
        var cityId2 = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, cityId2, "city-menu-2");
        })
    });

    //初始化区域
    $.ajax({
        url: "../../data/area.json",
        dataType: "json",
        async: false,
        success: function (data) {
            renderArea(data, 0, "provincemenu");
            renderArea(data, 0, "province-menu-2");
            $("#provincemenu").change();
            $("#province-menu-2").change();
        }
    });

    $("#queryBtn").click(function () {
        $('#queryResultTab').css("line-height", "12px");
        $('#queryResultTab').DataTable( {
            "ajax": "data/lte-grid-data-import-query.json",
            "columns": [
                { data: "cityId" },
                { data: "uploadTime" },
                { data: "fileName" },
                { data: "fileSize" },
                { data: "launchTime" },
                { data: "completeTime" },
                { data: "account" },
                { data: "fileStatus" }
            ],
            "lengthChange": false,
            "ordering": true,
            "searching": true,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        } );
    });

    $("#queryBtn1").click(function () {
        $('#queryResultTab1').css("line-height", "12px");
        $('#queryResultTab1').DataTable( {
            "ajax": "data/lte-grid-data-query.json",
            "columns": [
                { data: "AREA_ID" },
                { data: "RN" },
                { data: "GRID_DESC" },
                { data: "GRID_CENTER" },
                { data: "CREATE_TIME" }
            ],
            "lengthChange": false,
            "ordering": true,
            "searching": true,
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
            areaHtml.push("<option value='" + area.id + "'>");
            areaHtml.push(area.name + "</option>");
        });
        $("#" + areaMenu).html(areaHtml.join(""));

    } else {
        console.log("父ID为" + parentId + "时未找到任何下级区域。");
    }

}

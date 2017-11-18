$(function () {

    // 执行 laydate 实例 
    laydate.render({elem: '#begUploadDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endUploadDate', value: new Date()});
    laydate.render({elem: '#fileDate', value: new Date()});
    laydate.render({elem: '#beginTestDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endTestDate', value: new Date()});

    //执行 区域 实例 
    $("#province-menu").change(function () {
        var cityId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, cityId, "city-menu");
        })
    });

    $("#province-id").change(function () {
        var provinceId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, provinceId, "city-id");
        })
    });


    //初始化区域
    $.ajax({
        url: "../../data/area.json",
        dataType: "json",
        async: false,
        success: function (data) {
            renderArea(data, 0, "province-id");
            renderArea(data, 0, "province-menu");
            $("#province-id").change();
            $("#province-menu").change();
        }
    });

    $("#queryBtn").click(function () {
        var a = 2781;
        $('#queryResultTab').css("line-height", "12px");
        $('#queryResultTab').DataTable( {
            "ajax": "data/gsm-ncs-data-import.json",
            "columns": [
                { "data": "area_name" },
                { "data": "uploadTime" },
                { "data": "fileName" },
                { "data": "fileSize" },
                { "data": "launchTime" },
                { "data": "completeTime" },
                { "data": "account" },
                { "data": "fileStatus" , "render": function (data, type, row) {
                    return '<a onclick="showDetail('+a+')">' + data + '</a>';
                }}
            ],
            // "lengthChange": false,
            // "ordering": false,
            // "searching": false,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        } );
    });

    $("#queryNcsBtn").click(function () {
        var a = 2781;
        $('#queryNcsResultTab').css("line-height", "12px");
        $('#queryNcsResultTab').DataTable( {
            "ajax": "data/gsm-ncs-data-query.json",
            "columns": [
                { "data": "CITY_NAME" },
                { "data": "MEA_TIME" },
                { "data": "BSC" },
                { "data": "FREQ_SECTION" },
                { "data": "CREATE_TIME" }
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

function showDetail(jobId) {
    alert("jobId为："+jobId);
    /*$("#listinfoDiv").css("display","none")
    $("#reportDiv").css("display","block");*/
}

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

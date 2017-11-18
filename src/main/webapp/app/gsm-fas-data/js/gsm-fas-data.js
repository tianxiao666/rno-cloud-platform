$(function () {

    // 执行 laydate 实例 
    laydate.render({elem: '#begUploadDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endUploadDate', value: new Date()});
    laydate.render({elem: '#fileDate', value: new Date()});
    laydate.render({elem: '#beginTestDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endTestDate', value: new Date()});

    //执行 区域 实例 
    $("#fas-province-menu").change(function () {
        var cityId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, cityId, "fas-city-menu");
        })
    });

    $("#fas-province-id").change(function () {
        var provinceId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, provinceId, "fas-city-id");
        })
    });


    //初始化区域
    $.ajax({
        url: "../../data/area.json",
        dataType: "json",
        async: false,
        success: function (data) {
            renderArea(data, 0, "fas-province-id");
            renderArea(data, 0, "fas-province-menu");
            $("#fas-province-id").change();
            $("#fas-province-menu").change();
        }
    });

    $("#queryBtn").click(function () {
        $('#queryResultTab').css("line-height", "12px");
        $('#queryResultTab').DataTable( {
            "ajax": "data/gsm-fas-data-import.json",
            "columns": [
                { "data": "area_name" },
                { "data": "uploadTime" },
                { "data": "fileName" },
                { "data": "fileSize" },
                { "data": "launchTime" },
                { "data": "completeTime" },
                { "data": "account" },
                { "data": "fileStatus" , "render": function (data, type, row) {
                    return '<a onclick="showDetail('+row["jobId"]+')">' + data + '</a>';
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

    $("#queryFasBtn").click(function () {
        $('#queryFasResultTab').css("line-height", "12px");
        $('#queryFasResultTab').DataTable( {
            "ajax": "data/gsm-fas-data-query.json",
            "columns": [
                { "data": "CITY_NAME" },
                { "data": "MEA_TIME" },
                { "data": "BSC" },
                { "data": "RECORD_COUNT" },
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

function showDetail(jobId) {
    alert("jobId为："+jobId);
    /*$('#queryDetailTab').css("line-height", "12px");
    $('#queryDetailTab').DataTable( {
        "ajax": "data/gsm-fas-data-import-detail.json",
        "columns": [
            { "data": "STAGE" },
            { "data": "BEG_TIME" },
            { "data": "END_TIME" },
            { "data": "STATE" },
            { "data": "ATT_MSG" }
        ],
        // "lengthChange": false,
        // "ordering": false,
        // "searching": false,
        "language": {
            url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
        }
    } );
    $("#importListDiv").css("display","none")
    $("#reportDiv").css("display","block");*/
}
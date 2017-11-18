
$(function () {

    //执行 laydate 实例 
    laydate.render({elem: '#begUploadDate', type: 'datetime', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endUploadDate', type: 'datetime', value: new Date()});

    laydate.render({elem: '#begUploadDate2', type: 'datetime', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endUploadDate2', type: 'datetime', value: new Date()});

    $(".draggable").draggable();
    $("#trigger").css("display", "none");

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
            console.log("取到数据。");
            renderArea(data, 0, "provinceId");
            renderArea(data, 0, "provinceId2");
            $("#provinceId").change();
            $("#provinceId2").change();
        }
    });

    $("#queryBtn").click(function () {
        $('#queryResultTab').css("line-height", "12px");
        $('#queryResultTab').DataTable( {
            "ajax": "data/gsm-interfer-matrix-list.json",
            "columns": [
                { data: "AREA_NAME" },
                { data: "CREATE_DATE" },
                { data: "MEA_DATE" },
                { data: "RECORD_NUM" },
                { data: "TYPE" },
                { data: "STATUS" }
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
            "ajax": "data/gsm-interfer-matrix-ncs.json",
            "columns": [
                { data: "NAME" },
                { data: "BSC" },
                { data: "FILE_NAME" },
                { data: "MEA_TIME" }
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
        console.log("ARR=" + arr.length);
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

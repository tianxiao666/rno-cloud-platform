
$(function () {

    //执行 laydate 实例 
    laydate.render({elem: '#begDate', type: 'datetime', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endDate', type: 'datetime', value: new Date()});

    laydate.render({elem: '#begDate2', type: 'datetime', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endDate2', type: 'datetime', value: new Date()});

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
    })

    $("#searchInterMartixDT").click(function () {
        $('#analysisResultDT').css("line-height", "12px");
        $('#analysisResultDT').DataTable( {
            "ajax": "data/gsm-network-coverage-list.json",
            "columns": [
                { "data": null },
                { "data": "CREATE_DATE" },
                {"data" : null},
                { "data": "RECORD_NUM" },
                { "data": "WORK_STATUS" }
            ],
            "columnDefs": [ {
                "render": function(data, type, row) {
                    return "广州";
                },
                "targets": 0,
                "data": null
            },{
                "render": function(data, type, row) {
                    return row['START_MEA_DATE'] + "~"+row['END_MEA_DATE'];
                },
                "targets": 2,
                "data": null
            }
            ],
            "lengthChange": true,
            "ordering": false,
            "searching": true,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        });
    });

    $("#showNcsDataDT").click(function () {
        $('#ncsResultDT').css("line-height", "12px");
        $('#ncsResultDT').DataTable( {
            "ajax": "data/gsm-network-coverage-ncs.json",
            "columns": [
                { "data": null },
                { "data": "BSC" },
                {"data" : "FILE_NAME"},
                { "data": "MEA_TIME" },
            ],
            "columnDefs": [ {
                "render": function(data, type, row) {
                    return "广州";
                },
                "targets": 0,
                "data": null
            }
            ],
            "lengthChange": true,
            "ordering": false,
            "searching": true,
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

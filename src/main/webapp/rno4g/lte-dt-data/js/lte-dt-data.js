$(function () {

    // 执行 laydate 实例 
    laydate.render({elem: '#begUploadDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endUploadDate', value: new Date()});
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

    $("#searchImportRecordDT").click(function () {
        $('#queryRecordResTab').css("line-height", "12px");
        $('#queryRecordResTab').DataTable( {
            "ajax": "data/lte-dt-data-record-list.json",
            "columns": [
                { "data": null },
                { "data": "uploadTime" },
                {"data" : "fileName"},
                { "data": "fileSize" },
                { "data": "launchTime" },
                { "data": "completeTime" },
                { "data": "account" },
                { "data": null }
            ],
            "columnDefs": [ {
                "render": function(data, type, row) {
                    return "佛山";
                },
                "targets": 0,
                "data": null
            },{
                "render": function(data, type, row) {
                    switch (row['fileStatus']){
                        case "部分失败":
                            return "<a style='color: red' onclick='showImportDetail()'>" + row['fileStatus'] +"</a>";
                        case "全部失败":
                            return "<a style='color: red' onclick='showImportDetail()'>" + row['fileStatus'] +"</a>";
                        case "全部成功":
                            return "<a onclick='showImportDetail()'>" + row['fileStatus'] +"</a>";
                        case "正在解析":
                            return "<a onclick='showImportDetail()'>" + row['fileStatus'] +"</a>";
                        case "等待解析":
                            return "<a onclick='showImportDetail()'>" + row['fileStatus'] +"</a>";
                    }
                },
                "targets": -1,
                "data": null
            }
            ],
            "lengthChange": false,
            "ordering": false,
            "searching": true,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        });
    });

    $("#searchDtBtnDT").click(function () {
        $('#dtDataResultDT').css("line-height", "12px");
        $('#dtDataResultDT').DataTable( {
            "ajax": "data/lte-dt-data-list.json",
            "columns": [
                { "data": null },
                { "data": "MEA_TIME" },
                {"data" : "DATA_TYPE"},
                { "data": "AREA_TYPE" },
                { "data": "FILE_NAME" },
                { "data": "RECORD_COUNT" },
                { "data": "CREATE_TIME" },
            ],
            "columnDefs": [
                {
                    "render": function(data, type, row) {
                        return "佛山";
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

function  showImportDetail() {
    $("#queryImportDetailTab").css("line-height", "12px");
    $('#queryImportDetailTab').DataTable( {
        "ajax": "data/lte-dt-data-record-detail.json",
        "columns": [
            { "data": "STAGE" },
            { "data": "BEG_TIME" },
            {"data" : "END_TIME"},
            { "data": "STATE" },
            { "data": "ATT_MSG" },
        ],
        "lengthChange": false,
        "ordering": false,
        "searching": false,
        "language": {
            url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
        }
    } );
    $("#reportDiv").css("display","block");
    $("#listinfoDiv").css("display","none");
}

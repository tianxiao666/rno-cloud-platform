$(function () {
    $("#tabs").tabs();

    // 初始化区域联动
    initAreaSelectors({selectors: ["provinceId", "cityId"]});
    initAreaSelectors({selectors: ["provinceId2", "cityId2"]});

    //显示隐藏导入窗口
    $("#importTitleDiv").click(function () {
        var flag = $("#importDiv").is(":hidden");//是否隐藏
        if (flag) {
            $(".importContent").show("fast");
        } else {
            $(".importContent").hide("fast");
        }
    });

    // 执行 laydate 实例 
    laydate.render({elem: '#begUploadDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endUploadDate', value: new Date()});
    laydate.render({elem: '#beginTestDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endTestDate', value: new Date()});

    // AJAX 提交查询条件表单
    $("#import-query-form").ajaxForm({
        url: "/api/lte-dt-data/query-import",
        success: showQueryImportResult
    });

    // AJAX 上传文件
    var progress = $('.upload-progress');
    var bar = $('.bar');
    var percent = $('.percent');
    //获取area_id上传
    $("#area").val($('#cityId option:selected').val());

    $("#dt-import").on('click', function() {
        var filename = fileid.value;
        if(!(filename.toUpperCase().endsWith(".CSV")||filename.toUpperCase().endsWith(".ZIP"))){
            showInfoInAndOut("info", "请选择csv或者zip格式的数据文件");
            return false;
        }else {
            return true;
        }
    });

    $("#file-upload-form").ajaxForm({
        url: "/api/lte-dt-data/upload-file",
        beforeSend: function () {
            progress.css("display", "block");
            var percentVal = '0%';
            bar.width(percentVal);
            percent.html(percentVal);
        },
        uploadProgress: function (event, position, total, percentComplete) {
            var percentVal = percentComplete + '%';
            bar.width(percentVal);
            percent.html(percentVal);
        },
        success: function () {
            var percentVal = '100%';
            bar.width(percentVal);
            percent.html(percentVal);
            $("#search-traffic-record").click();
        }
    });

    // 当上传文件域改变时，隐藏进度条
    $("input[name='file']").change(function () {
        var filename = fileid.value;
        if(!(filename.toUpperCase().endsWith(".CSV")||filename.toUpperCase().endsWith(".ZIP"))){
            $("#fileDiv").html("不支持该类型文件！");
        }else {
            $("#fileDiv").html("");
        }
        progress.css("display", "none");
    });

    $("#searchDtBtnDT").click(function () {
        $('#dtDataResultDT').css("line-height", "12px")
            .DataTable({
                "ajax": "data/lte-dt-data-list.json",
                "columns": [
                    {"data": null},
                    {"data": "MEA_TIME"},
                    {"data": "DATA_TYPE"},
                    {"data": "AREA_TYPE"},
                    {"data": "FILE_NAME"},
                    {"data": "RECORD_COUNT"},
                    {"data": "CREATE_TIME"}
                ],
                "columnDefs": [
                    {
                        "render": function () {
                            return "广州市";
                        },
                        "targets": 0,
                        "data": null
                    }
                ],
                "lengthChange": false,
                "ordering": false,
                "searching": false,
                "destroy": true,
                "language": {
                    url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
                }
            });
    });
});

function showImportDetail() {
    $("#queryImportDetailTab").css("line-height", "12px")
        .DataTable({
            "ajax": "data/lte-dt-data-record-detail.json",
            "columns": [
                {"data": "STAGE"},
                {"data": "BEG_TIME"},
                {"data": "END_TIME"},
                {"data": "STATE"},
                {"data": "ATT_MSG"}
            ],
            "lengthChange": false,
            "ordering": false,
            "searching": false,
            "destroy": true,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        });
    $("#reportDiv").css("display", "block");
    $("#listInfoDiv").css("display", "none");
}

function showInfoInAndOut(div, info) {
    var divSet = $("#" + div);
    divSet.html(info);
    divSet.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

function showQueryImportResult(data) {
    if (data == '') {
        showInfoInAndOut('info', '没有符合条件的路测数据');
    }

    $('#queryRecordResTab').css("line-height", "12px")
        .DataTable({
            "data": data,
            "columns": [
                {"data": "areaName"},
                {"data": "uploadTime"},
                {"data": "filename"},
                {"data": "fileSize"},
                {"data": null},
                {"data": null},
                {"data": "createdUser"},
                {"data": null}
            ],
            "columnDefs": [
                {
                    "render": function (data, type, row) {
                        switch (row['status']) {
                            case "全部失败":
                                return "<a style='color: red' <!--onclick='showImportDetail()'-->>" + row['status'] + "</a>";
                            case "部分失败":
                                return "<a style='color: red'>" + row['status'] + "</a>";
                            case "全部成功":
                                return "<a>" + row['status'] + "</a>";
                            case "正在处理":
                                return "<a>" + row['status'] + "</a>";
                            case "等待处理":
                                return "<a>" + row['status'] + "</a>";
                        }
                    },
                    "targets": -1,
                    "data": null
                },
                {
                    "render": function(data, type, row) {
                        if(row['startTime']==""||row['startTime']==null){
                            return " --- ";
                        }else {
                            return row['startTime'];
                        }
                    },
                    "targets": 4,
                    "data": "startTime"
                },{
                    "render": function(data, type, row) {
                        if(row['completeTime']==""||row['completeTime']==null){
                            return " --- ";
                        }else {
                            return row['completeTime'];
                        }
                    },
                    "targets": 5,
                    "data": "completeTime"
                },
            ],
            "lengthChange": false,
            "ordering": false,
            "searching": false,
            "destroy": true,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        });

}
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
        }
    });

    // 当上传文件域改变时，隐藏进度条
    $("input[name='file']").change(function () {
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
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        });
    $("#reportDiv").css("display", "block");
    $("#listInfoDiv").css("display", "none");
}

function showQueryImportResult(data) {
    $('#queryRecordResTab').css("line-height", "12px")
        .DataTable({
            "data": data,
            "columns": [
                {"data": null},
                {"data": "uploadTime"},
                {"data": "filename"},
                {"data": "fileSize"},
                {"data": "launchTime"},
                {"data": "completeTime"},
                {"data": "account"},
                {"data": null}
            ],
            "columnDefs": [{
                "render": function () {
                    return "佛山";
                },
                "targets": 0,
                "data": null
            }, {
                "render": function (data, type, row) {
                    switch (row['fileStatus']) {
                        case "部分失败":
                            return "<a style='color: red' onclick='showImportDetail()'>" + row['fileStatus'] + "</a>";
                        case "全部失败":
                            return "<a style='color: red' onclick='showImportDetail()'>" + row['fileStatus'] + "</a>";
                        case "全部成功":
                            return "<a onclick='showImportDetail()'>" + row['fileStatus'] + "</a>";
                        case "正在解析":
                            return "<a onclick='showImportDetail()'>" + row['fileStatus'] + "</a>";
                        case "等待解析":
                            return "<a onclick='showImportDetail()'>" + row['fileStatus'] + "</a>";
                    }
                },
                "targets": -1,
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
}
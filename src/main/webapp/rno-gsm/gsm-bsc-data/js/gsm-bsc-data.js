$(function () {
    $("#tabs").tabs();
    $("#addSingleBscDiv").draggable();
    // 设置导航标题
    setNavTitle("navTitle");
    // 初始化区域联动
    initAreaSelectors({selectors: ["provinceId", "cityId"]});
    initAreaSelectors({selectors: ["provinceIds", "cityIds"]});
    //显示隐藏导入窗口
    $("#importTitleDiv").click(function () {
        const flag = $("#importDiv").is(":hidden");//是否隐藏
        if (flag) {
            $(".importContent").show("fast");
        } else {
            $(".importContent").hide("fast");
        }
    });
    // 执行 laydate 实例 
    const start = laydate.render({
        elem: '#begUploadDate',
        value: new Date(new Date().getTime() - 7 * 86400000),
        done: function (value, date) {
            end.config.min = {
                year: date.year,
                month: date.month - 1,
                date: date.date,
            };
        }
    });

    const end = laydate.render({
        elem: '#endUploadDate',
        value: new Date(),
        done: function (value, date) {
            start.config.max = {
                year: date.year,
                month: date.month - 1,
                date: date.date,
            };
        }
    });

    const beginTest = laydate.render({
        elem: '#beginTestDate',
        value: new Date(new Date().getTime() - 7 * 86400000),
        done: function (value, date) {
            endTest.config.min = {
                year: date.year,
                month: date.month - 1,
                date: date.date,
            };
        }
    });

    const endTest = laydate.render({
        elem: '#endTestDate',
        value: new Date(),
        done: function (value, date) {
            beginTest.config.max = {
                year: date.year,
                month: date.month - 1,
                date: date.date,
            };
        }
    });

    // AJAX 提交查询条件表单
    $("#search-bsc-record").on('click', function () {
        //获取area_id上传
        $("#area").val($("#cityId").val());
        const startTime = begUploadDate.value;
        const endTime = endUploadDate.value;
        if (startTime > endTime) {
            showInfoInAndOut("info", "开始时间不能大于结束时间");
            return false;
        } else {
            return true;
        }
    });
    $("#import-query-form").ajaxForm({
        url: "../../api/gsm-bsc-data/query-import",
        success: showQueryImportResult
    });

    // AJAX 提交增加bsc的信息
    $("#addBscBtn").on('click', function () {
        //获取area_id上传
        const cityId = $("#cityIds").val();
        $("#areas").val(cityId);
        if (bscName.value.trim()==='') {
            showInfoInAndOut("info", "bsc不能为空");
            return false;
        } else {
            return true;
        }
    });
    $("#addSingleBscForm").ajaxForm({
        url: "../../api/gsm-bsc-data/bsc-data-update",
        success: showResult
    });

    //提交bsc信息查询
    $("#conditionForm").ajaxForm({
        url: "../../api/gsm-bsc-data/query-record",
        success: showRecord
    });

    // AJAX 上传文件
    let progress = $('.upload-progress');
    let bar = $('.bar');
    let percent = $('.percent');

    $("#bsc-import").on('click', function () {
        //获取area_id上传
        $("#area").val($("#cityId").val());
        const filename = fileid.value;
        if (!(filename.toUpperCase().endsWith(".CSV") || filename.toUpperCase().endsWith(".ZIP"))) {
            showInfoInAndOut("info", "请选择csv或者zip格式的数据文件");
            return false;
        } else {
            return true;
        }
    });

    $("#file-upload-form").ajaxForm({
        url: "../../api/gsm-bsc-data/upload-file",
        beforeSend: function () {
            progress.css("display", "block");
            const percentVal = '0%';
            bar.width(percentVal);
            percent.html(percentVal);
        },
        uploadProgress: function (event, position, total, percentComplete) {
            const percentVal = percentComplete + '%';
            bar.width(percentVal);
            percent.html(percentVal);
        },
        success: function () {
            const percentVal = '100%';
            bar.width(percentVal);
            percent.html(percentVal);
            $("#search-bsc-record").click();
        }
    });

    // 当上传文件域改变时，隐藏进度条
    $("input[name='file']").change(function () {
        progress.css("display", "none");
    });

});

function showInfoInAndOut(div, info) {
    let divSet = $("#" + div);
    divSet.html(info);
    divSet.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

//显示导入记录
function showQueryImportResult(data) {
    if (data === '') {
        $("#info").css("background", "red");
        showInfoInAndOut('info', '没有符合条件的BSC信息记录');
    }
    $('#queryRecordResTab').DataTable().clear();
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
                            case "部分成功":
                                return "<a style='color: red;cursor: pointer' onclick=\"showImportDetail('" + row['id'] + "')\">" + row['status'] + "</a>";
                            case "全部失败":
                                return "<a style='color: red;cursor: pointer' onclick=\"showImportDetail('" + row['id'] + "')\">" + row['status'] + "</a>";
                            case "全部成功":
                                return "<a style='cursor: pointer' onclick=\"showImportDetail('" + row['id'] + "')\">" + row['status'] + "</a>";
                            case "正在处理":
                                return "<a style='cursor: pointer' onclick=\"showImportDetail('" + row['id'] + "')\">" + row['status'] + "</a>";
                            case "等待处理":
                                return "<a style='cursor: pointer' onclick=\"showImportDetail('" + row['id'] + "')\">" + row['status'] + "</a>";
                        }
                    },
                    "targets": -1,
                    "data": null
                },
                {
                    "render": function (data, type, row) {
                        if (row['startTime'] == "" || row['startTime'] == null) {
                            return " --- ";
                        } else {
                            return row['startTime'];
                        }
                    },
                    "targets": 4,
                    "data": "startTime"
                }, {
                    "render": function (data, type, row) {
                        if (row['completeTime'] == "" || row['completeTime'] == null) {
                            return " --- ";
                        } else {
                            return row['completeTime'];
                        }
                    },
                    "targets": 5,
                    "data": "completeTime"
                },
            ],
            "lengthChange": false,
            "ordering": true,
            "searching": false,
            "destroy": true,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        });
}

//显示导入记录的状态的详情
function showImportDetail(id) {
    $.ajax({
        url: '../../api/gsm-bsc-data/query-report',
        dataType: 'text',
        data: {id: id},
        success: function (data) {
            $("#reportListTab").css("line-height", "12px");
            $("#reportDiv").css("display", "block");
            $("#listInfoDiv").css("display", "none");
            $("#reportListTab").DataTable({
                "data": JSON.parse(data),
                "columns": [
                    {"data": "stage"},
                    {"data": "startTime"},
                    {"data": "completeTime"},
                    {"data": "status"},
                    {"data": "message"}
                ],
                "lengthChange": false,
                "ordering": true,
                "searching": false,
                "destroy": true,
                "language": {
                    url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
                }
            });
        }, error: function (err) {
            console.log(err);
            $("#info").css("background", "red");
            showInfoInAndOut("info", "后台程序错误！");
        }
    });
}

/**
 * 从报告的详情返回列表页面
 */
function returnToImportList() {
    $("#reportDiv").css("display", "none");
    $("#listInfoDiv").css("display", "block");
}

//查询信息
function showRecord(data) {
    if (data == '') {
        $("#info").css("background", "red");
        showInfoInAndOut('info', '没有符合条件的BSC信息数据');
    }
    $('#queryResultTab').DataTable().clear();
    $('#queryResultTab').css("line-height", "12px")
        .DataTable({
            "data": data,
            "columns": [
                {"data": "areaName"},
                {"data": "bsc"},
                {"data": "vendor"},
                {"data": null}
            ],
            "columnDefs": [
                {
                    "render": function (data, type, row) {
                        return "<a style='color: blue;cursor: pointer' onclick=\"deleteBsc('" + row['id'] + "')\">" + '删除' + "</a>";
                    },
                    "targets": -1,
                    "data": null
                }
            ],
            "lengthChange": false,
            "ordering": true,
            "searching": false,
            "destroy": true,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        });
}

//显示导入记录的状态的详情
function deleteBsc(id) {
    $.ajax({
        url: '../../api/gsm-bsc-data/delete-by-bscId',
        dataType: 'text',
        data: {bscId: id},
        type: "Get",
        success: function (data) {
            $("#info").css("background", "green");
            showInfoInAndOut("info", "删除完成！");
            $("#queryBtn").click();
        },
        error: function (err) {
            $("#info").css("background", "red");
            showInfoInAndOut("info", "后台程序错误！");
        }
    });
}

//反馈更新状态
function showResult(data) {
    if(data === true) {
        $("#info").css("background", "green");
        showInfoInAndOut("info", "增加成功！");
        $("#queryBtn").click();
    } else {
        $("#info").css("background", "red");
        showInfoInAndOut("info", "后台程序错误！");
    }
}
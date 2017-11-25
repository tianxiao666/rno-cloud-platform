$(function () {
    $(".draggable").draggable();
    $("#trigger").css("display", "none");
    $("#tabs").tabs();

    // 设置导航标题
    setNavTitle("navTitle");

    // 执行 laydate 实例 
    laydate.render({elem: '#begUploadDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endUploadDate', value: new Date()});
    laydate.render({elem: '#fileDate', value: new Date()});
    laydate.render({elem: '#begMetaDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endMetaDate', value: new Date()});

    // 初始化区域联动
    initAreaSelectors({selectors: ["provincemenu", "citymenu"]});
    initAreaSelectors({selectors: ["province-menu-2", "city-menu-2"]});

    //显示隐藏导入窗口
    $("#importTitleDiv").click(function () {
        var flag = $("#importDiv").is(":hidden");//是否隐藏
        if (flag) {
            $(".importContent").show("fast");
        } else {
            $(".importContent").hide("fast");
        }
    });

    $("#queryBtn").click(function () {
        $(".loading").show();
    });

    $("#queryBtn1").click(function () {
        $(".loading").show();
    });

    // AJAX 上传文件
    var progress = $('.upload-progress');
    var bar = $('.bar');
    var percent = $('.percent');

    //导入文件类型判断
    $("#importBtn").click(function () {
        var path = $("#file").val();
        var cityId = $("#citymenu").val();
        $("#areaId").val(cityId);
        var fileType = path.substring(path.lastIndexOf("."), path.length).toLowerCase();
        if (fileType !== '.zip') {
            $("#info").css("background", "red");
            showInfoInAndOut("info", "请上传zip格式的数据文件");
            return false;
        }
    });

    $("#formImportNcs").ajaxForm({
        url: "/api/lte-grid-data/upload-file",
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
            $("#info").css("background", "green");
            showInfoInAndOut("info", "文件导入成功！");
            //AJAX 提交邻区导入记录查询条件表单
            $("#searchImportForm").submit();
        }
    });

    //AJAX 提交网格数据导入记录查询条件表单
    $("#searchImportForm").ajaxForm({
        url: "/api/lte-grid-data/import-query",
        success: showLteGridImportResult
    });

    //AJAX 提交网格数据记录查询条件表单
    $("#searchNcsForm").ajaxForm({
        url: "/api/lte-grid-data/data-query",
        success: showLteGridDataResult
    });

    // 当上传文件域改变时，隐藏进度条
    $("input[name='file']").change(function () {
        progress.css("display", "none");
    });
});

//显示网格数据导入记录查询结果
function showLteGridImportResult(data) {
    $(".loading").css("display", "none");
    if (data == '') {
        $("#info").css("background", "red");
        showInfoInAndOut('info', '没有符合条件的网格数据导入记录');
    }

    $('#queryResultTab').css("line-height", "12px")
        .DataTable({
            "data": data,
            "columns": [
                {data: "areaName"},
                {data: "uploadTime"},
                {data: "filename"},
                {data: "fileSize"},
                {data: null},
                {data: null},
                {data: "createdUser"},
                {data: null}
            ], "columnDefs": [{
                "render": function (data, type, row) {
                    if (row['startTime'] === "" || row['startTime'] === null) {
                        return "---";
                    } else {
                        return row['startTime'];
                    }
                },
                "targets": 4,
                "data": null
            }, {
                "render": function (data, type, row) {
                    if (row['completeTime'] === "" || row['completeTime'] === null) {
                        return "---";
                    } else {
                        return row['completeTime'];
                    }
                },
                "targets": 5,
                "data": null
            }, {
                "render": function (data, type, row) {
                    switch (row['status']) {
                        case "部分成功":
                            return "<a style='color: red' onclick=\"showImportDetail('" + row['id'] + "')\">" + row['status'] + "</a>";
                        case "全部失败":
                            return "<a style='color: red' onclick=\"showImportDetail('" + row['id'] + "')\">" + row['status'] + "</a>";
                        case "全部成功":
                            return "<a onclick=\"showImportDetail('" + row['id'] + "')\">" + row['status'] + "</a>";
                        case "正在处理":
                            return "<a onclick=\"showImportDetail('" + row['id'] + "')\">" + row['status'] + "</a>";
                        case "等待处理":
                            return "<a onclick=\"showImportDetail('" + row['id'] + "')\">" + row['status'] + "</a>";
                    }
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

//显示网格数据查询结果
function showLteGridDataResult(data) {
    $(".loading").css("display", "none");
    if (data == '') {
        $("#info").css("background", "red");
        showInfoInAndOut('info', '没有符合条件的网格数据记录');
    }

    $('#queryResultTab1').css("line-height", "12px")
        .DataTable({
            "data": data,
            "columns": [
                {data: "areaName"},
                {data: "gridType"},
                {data: "filename"},
                {data: "recordCount"},
                {data: "createdDate"}
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

function showInfoInAndOut(div, info) {
    var divSet = $("#" + div);
    divSet.html(info);
    divSet.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

//显示导入记录的状态的结果报告
function showImportDetail(id) {
    $.ajax({
        url: '/api/lte-grid-data/query-report',
        dataType: 'text',
        data: {id: id},
        success: function (data) {
            $("#reportDiv").css("display", "block");
            $("#listInfoDiv").css("display", "none");
            $("#reportListTab").css("line-height", "12px")
                .DataTable({
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

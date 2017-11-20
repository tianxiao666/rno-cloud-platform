$(function () {
    //tab选项卡
    $("#tabs").tabs();//项目服务范围类别切换

    // 执行 laydate 实例 
    laydate.render({elem: '#begUploadDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endUploadDate', value: new Date()});
    laydate.render({elem: '#beginRecordDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endRecordDate', value: new Date()});

    laydate.render({elem: '#recordDate', value: new Date()});

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

    $("#queryImportDT").click(function () {
        $(".loading").show();
    });

    // AJAX 上传文件
    var progress = $('.upload-progress');
    var bar = $('.bar');
    var percent = $('.percent');

    //导入文件类型判断
    $("#importBtn").click(function () {
        var path = $("#file").val();
        var cityId = $("#cityId").val();
        $("#areaId").val(cityId);
        var fileType = path.substring(path.lastIndexOf("."), path.length).toLowerCase();
        if (fileType !== '.csv' && fileType !== '.zip') {
            $("#info").css("background", "red");
            showInfoInAndOut("info", "请上传csv或者zip格式的数据文件");
            return false;
        }
    });

    $("#file-upload-form").ajaxForm({
        url: "/api/lte-mr-data/upload-file",
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
            //AJAX 提交Mr数据导入记录查询条件表单
            $("#importQuery").submit();
        }
    });

    // 当上传文件域改变时，隐藏进度条
    $("input[name='file']").change(function () {
        progress.css("display", "none");
    });

    //AJAX 提交MR数据导入记录查询条件表单
    $("#importQuery").ajaxForm({
        url: "/api/lte-mr-data/mr-import-query",
        success: showNcellImportResult
    });

    //AJAX 提交Mr数据记录查询条件表单
    $("#searchMrDtForm").ajaxForm({
        url: "/api/lte-mr-data/data-query",
        success: showNcellImportDtResult
    });
});

//显示Mr数据导入记录查询结果
function showNcellImportResult(data) {
    $(".loading").css("display", "none");
    if (data == '') {
        $("#info").css("background", "red");
        showInfoInAndOut('info', '没有符合条件的MR数据导入记录');
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
            "columnDefs": [{
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
            "ordering": false,
            "searching": false,
            "destroy": true,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        });
}

//显示Mr数据记录查询结果
function showNcellImportDtResult(data) {
    $(".loading").css("display", "none");
    if (data == '') {
        $("#info").css("background", "red");
        showInfoInAndOut('info', '没有符合条件的MR数据记录');
    }

    $('#queryDataResultDT').css("line-height", "12px")
        .DataTable({
            "data": data,
            "columns": [
                {"data": "areaName"},
                {"data": "recordDate"},
                {"data": "vendor"},
                {"data": "filename"},
                {"data": "recordCount"},
                {"data": "createdDate"}
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

//显示导入记录的状态的详情
function showImportDetail(id) {
    $.ajax({
        url: '/api/lte-mr-data/query-report',
        dataType: 'text',
        data: {id: id},
        success:function(data){
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
                    "ordering": false,
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

function showInfoInAndOut(div, info) {
    var divSet = $("#" + div);
    divSet.html(info);
    divSet.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

/**
 * 从报告的详情返回列表页面
 */
function returnToImportList(){
    $("#reportDiv").css("display","none");
    $("#listInfoDiv").css("display","block");
}
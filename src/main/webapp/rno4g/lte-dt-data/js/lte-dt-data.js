$(function () {
    $("#tabs").tabs();

    // 设置导航标题
    setNavTitle("navTitle");

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
    var dateBeg = new Date();
    dateBeg.setFullYear(2017,8,1);
    var start = laydate.render({
        elem: '#begUploadDate',
        value: new Date(new Date().getTime() - 7 * 86400000),
        done: function(value, date) {
            end.config.min = {
                year: date.year,
                month: date.month - 1,
                date: date.date,
            };
        }
    });

    var end = laydate.render({
        elem: '#endUploadDate',
        value: new Date(),
        done: function(value, date) {
            start.config.max = {
                year: date.year,
                month: date.month - 1,
                date: date.date,
            };
        }
    });

    var beginTest = laydate.render({
        elem: '#beginTestDate',
        value: dateBeg,
        done: function(value, date) {
            endTest.config.min = {
                year: date.year,
                month: date.month - 1,
                date: date.date,
            };
        }
    });

    var endTest = laydate.render({
        elem: '#endTestDate',
        value: new Date(),
        done: function(value, date) {
            beginTest.config.max = {
                year: date.year,
                month: date.month - 1,
                date: date.date,
            };
        }
    });

    // AJAX 提交查询条件表单
    $("#searchRecord").on('click', function() {
        //获取area_id上传
        $("#area").val($("#cityId").val());
        var startTime = begUploadDate.value;
        var endTime = endUploadDate.value;
        if(startTime>endTime) {
            showInfoInAndOut("info", "开始时间不能大于结束时间");
            return false;
        }else {
            return true;
        }
    });
    $("#import-query-form").ajaxForm({
        url: "/api/lte-dt-data/query-import",
        success: showQueryImportResult
    });

    // AJAX 上传文件
    var progress = $('.upload-progress');
    var bar = $('.bar');
    var percent = $('.percent');

    $("#dt-import").on('click', function() {
        //获取area_id上传
        $("#area").val($("#cityId").val());
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
            $("#searchRecord").click();
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

    //数据记录查询
    $("#searchDtBtnDT").on('click', function() {
        //获取area_id上传
        $("#area").val($("#cityId").val());
        var startTime = beginTestDate.value;
        var endTime = endTestDate.value;
        if(startTime>endTime) {
            showInfoInAndOut("info", "开始时间不能大于结束时间");
            return false;
        }else {
            return true;
        }
    });
    $("#searchDtRecordForm").ajaxForm({
       url: "/api/lte-dt-data/query-record",
        success: showDtRecord
    });

});

function showInfoInAndOut(div, info) {
    var divSet = $("#" + div);
    divSet.html(info);
    divSet.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

function showQueryImportResult(data) {
    if (data === '') {
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
                        var id = row['id'];
                        switch (row['status']) {
                            case "全部失败":
                                return "<a style='color: red;cursor: pointer' onclick='showImportDtDetail("+id+")'>" + row['status'] + "</a>";
                            case "部分成功":
                                return "<a style='color: red;cursor: pointer' onclick='showImportDtDetail("+id+")'>" + row['status'] + "</a>";
                            case "全部成功":
                                return "<a style='cursor: pointer' onclick='showImportDtDetail("+id+")'>" + row['status'] + "</a>";
                            case "正在处理":
                                return "<a style='cursor: pointer' onclick='showImportDtDetail("+id+")'>" + row['status'] + "</a>";
                            case "等待处理":
                                return "<a style='cursor: pointer' onclick='showImportDtDetail("+id+")'>" + row['status'] + "</a>";
                        }
                    },
                    "targets": -1,
                    "data": null
                },
                {
                    "render": function(data, type, row) {
                        if(row['startTime']===""||row['startTime']===null){
                            return " --- ";
                        }else {
                            return row['startTime'];
                        }
                    },
                    "targets": 4,
                    "data": "startTime"
                },{
                    "render": function(data, type, row) {
                        if(row['completeTime']===""||row['completeTime']===null){
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
            "ordering": true,
            "searching": false,
            "destroy": true,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        });

}

/**
 * 从报告的详情返回列表页面
 */
function returnToImportDtList(){
    $("#reportDtDiv").css("display","none");
    $("#listInfoDtDiv").css("display","block");
}

function showImportDtDetail(id) {
    $("#reportDtDiv").css("display","block");
    $("#listInfoDtDiv").css("display","none");
    var dataTable=$("#reportDtListTable");
    if (dataTable.hasClass('dataTable')) {
        dataTable.dataTable().fnClearTable();
    }
    $.ajax({
        url: '/api/lte-dt-data/query-import-detail-id',
        data:{id:id},
        dataType: 'text',
        type:'get',
        success: showImportDtDatailResult,
        error: function (err) {
            console.log(err);
            showInfoInAndOut("info", "后台程序错误！");
        }
    });
}

function showImportDtDatailResult(data) {
    $("#reportDtListTable").css("line-height", "12px")
        .dataTable({
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
}

function showDtRecord(data) {
    //console.log(data);
    if (data === '') {
        showInfoInAndOut('info', '没有符合条件的路测数据');
    }
    $('#dtDataResultDT').css("line-height", "12px")
        .DataTable({
            "data": data,
            "columns": [
                {"data": "areaName"},
                {"data": "dataType"},
                {"data": "areaType"},
                {"data": "filename"},
                {"data": "dataNum"},
                {"data": "createdDate"}
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
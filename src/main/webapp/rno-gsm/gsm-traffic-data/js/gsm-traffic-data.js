$(function () {

    $(".draggable").draggable();
    $("#trigger").css("display", "none");
    $("#tabs").tabs();

    // 设置导航标题
    setNavTitle("navTitle");

    //执行 laydate 实例 
    laydate.render({elem: '#beginTime', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#latestAllowedTime', value: new Date()});
    laydate.render({elem: '#begUploadDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endUploadDate', value: new Date()});
    // 初始化区域联动
    initAreaSelectors({selectors: ["provinceId", "cityId",'areaId']});
    initAreaSelectors({selectors: ["provinceId2", "cityId2",'areaId2']});
    initAreaSelectors({selectors: ["provinceId3", "cityId3",null]});

    //显示隐藏导入窗口
    $("#importTitleDiv").click(function () {
        var flag = $("#importDiv").is(":hidden");//是否隐藏
        if (flag) {
            $(".importContent").show("fast");
        } else {
            $(".importContent").hide("fast");
        }
    });



    // AJAX 上传文件
    var progress = $('.upload-progress');
    var bar = $('.bar');
    var percent = $('.percent');

    $("#importBtn").on('click', function () {
        var filename = $("#fileid").val();
        if (!filename.toUpperCase().endsWith(".CSV") ) {
            showInfoInAndOut("info", "请选择csv格式的数据文件");
            return false;
        }
    });

    $("#file-upload-form").ajaxForm({
        url: "/api/gsm-traffic-data/upload-file",
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
            var info = $("#info");
            info.css("background","green");
            info.css("color","white");
            showInfoInAndOut("info","文件导入成功！");
            $("#importRecordBtn").click();
        }
    });

    // 当上传文件域改变时，隐藏进度条
    $("input[name='file']").change(function () {
        var filename = fileid.value;
        if (!filename.toUpperCase().endsWith(".CSV") ) {
            $("#fileDiv").html("不支持该类型文件！");
        } else {
            $("#fileDiv").html("");
        }
        progress.css("display", "none");
    });

    //导入记录查询前校验
    $("#importRecordBtn").click(function () {
        if($("#begUploadDate").val().trim() === '' || $("#endUploadDate").val().trim() === ''){
            showInfoInAndOut('info','开始时间和结束时间不能为空！');
            return false;
        }
    });

    //导入记录查询
    $("#import-query-form").ajaxForm({
        url: "/api/gsm-traffic-data/query-import",
        success: showImportRecord
    });


    $("#query-traffic-desc-form").ajaxForm({
        url: "/api/gsm-traffic-data/query-traffic-desc",
        success: showTrafficDesc
    });
    $("#queryTrafficBtn").click(function () {

    });
});

function showInfoInAndOut(div, info) {
    var divSet = $("#" + div);
    divSet.html(info);
    divSet.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

function showImportRecord(data) {
    var queryRecordResTab =$('#queryRecordResTab');
    queryRecordResTab.DataTable().clear();
    queryRecordResTab.css("line-height", "12px")
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
                    var importRecordId =row['id'];
                    switch (row['status']) {
                        case "部分成功":
                            return "<a style='color: red' onclick='showImportDetail("+importRecordId+")'>" + row['status'] + "</a>";
                        case "全部失败":
                            return "<a style='color: red' onclick='showImportDetail("+importRecordId+")'>" + row['status'] + "</a>";
                        case "全部成功":
                            return "<a onclick='showImportDetail("+importRecordId+")'>" + row['status'] + "</a>";
                        case "正在处理":
                            return "<a onclick='showImportDetail("+importRecordId+")'>" + row['status'] + "</a>";
                        case "等待处理":
                            return "<a onclick='showImportDetail("+importRecordId+")'>" + row['status'] + "</a>";
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
                }],
            "lengthChange": false,
            "ordering": true,
            "searching": false,
            "destroy": true,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        });
}

function showImportDetail(id) {
    $("#reportDiv").css("display","block");
    $("#listinfoDiv").css("display","none");
    var dataTable=$("#reportListTable");
    if (dataTable.hasClass('dataTable')) {
        dataTable.dataTable().fnClearTable();
    }
    $.ajax({
        url: '/api/gsm-traffic-data/query-report',
        data:{id:id},
        dataType: 'text',
        type:'get',
        success: showImportDatailResult,
        error: function (err) {
            console.log(err);
            showInfoInAndOut("info", "后台程序错误！");
        }
    });
}

/**
 * 从报告的详情返回列表页面
 */
function returnToImportList(){
    $("#reportDiv").css("display","none");
    $("#listinfoDiv").css("display","block");
}

function showImportDatailResult(data) {
    var reportListTable = $("#reportListTable");
    reportListTable.DataTable().clear();
    reportListTable.css("line-height", "12px")
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

function showTrafficDesc(data) {
    var telequeryResultDT =$('#telequeryResultDT');
    telequeryResultDT.DataTable().clear();
    telequeryResultDT
        .css("line-height", "12px")
        .DataTable( {
            "data": data,
            "columns": [
                { "data": "areaName" },
                { "data": "specType" },
                { "data": "stsDate" },
                { "data": "createTime" }
            ],
            "destroy":true,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        });
}
$(function () {
    $("#tabs").tabs();

    // 设置导航标题
    setNavTitle("navTitle");

    //执行 laydate 实例 
    laydate.render({elem: '#begUploadDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endUploadDate', value: new Date()});

    laydate.render({elem: '#kpiMeaBegDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#kpiMeaEndDate', value: new Date()});

    initAreaSelectors({selectors: ["province", "city"]});
    initAreaSelectors({selectors: ["province2", "city2"]});


    //查询数据记录
    $("#searchRecordForm").ajaxForm({
        url: "/api/lte-kpi-data/query-record",
        success: showRecord
    });

    $("#searchDataBtn").click(function () {

    });

    // AJAX 上传文件
    var progress = $('.upload-progress');
    var bar = $('.bar');
    var percent = $('.percent');

    //导入文件类型判断
    $("#importBtn").click(function () {
        var path =$("#file").val();
        var cityId = $("#city").val();
        $("#areaId").val(cityId);
        var format = path.substring(path.lastIndexOf("."), path.length).toLowerCase();
        if (format !== '.csv' && format !=='.zip') {
            showInfoInAndOut("info", "请上传csv或zip格式的小区文件");
            return false;
        }
    });

    // 当上传文件域改变时，隐藏进度条
    $("input[name='file']").change(function () {
        progress.css("display", "none");
    });

    //上传
    $("#file-upload-form").ajaxForm({
        url: "/api/lte-kpi-data/upload-file",
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
            $("#info").css("background","green");
            showInfoInAndOut("info","文件导入成功！");
            $("#import-query-form").submit();
        }
    });

    // AJAX 查询导入记录
    $("#import-query-form").ajaxForm({
        url: "/api/lte-kpi-data/query-import",
        success: showImportRecord
    });
});

function showRecord(data) {
    $('#queryDataTab').css("line-height", "12px")
        .DataTable( {
            "data": data,
            "columns": [
                {"data": "areaName"},
                {"data": "dataType"},
                {"data": "filename"},
                {"data": "dataNum"},
                {"data": "createdDate"}
            ],
            "lengthChange": true,
            "ordering": true,
            "searching": true,
            "destroy": true,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        } );
}

function  showImportRecord(data) {
    $('#queryImportTab')
        .css("line-height", "12px")
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
                    var id = row['id'];
                    switch (row['status']) {
                        case "部分成功":
                            return "<a style='color: red' onclick='showImportDetail("+id+")'>" + row['status'] + "</a>";
                        case "全部失败":
                            return "<a style='color: red' onclick='showImportDetail("+id+")'>" + row['status'] + "</a>";
                        case "全部成功":
                            return "<a onclick='showImportDetail("+id+")'>" + row['status'] + "</a>";
                        case "正在处理":
                            return "<a onclick='showImportDetail("+id+")'>" + row['status'] + "</a>";
                        case "等待处理":
                            return "<a onclick='showImportDetail("+id+")'>" + row['status'] + "</a>";
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


function showInfoInAndOut(div, info) {
    var divSet = $("#" + div);
    divSet.html(info);
    divSet.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

function showImportDetail(id) {
    $("#reportDiv").css("display","block");
    $("#listinfoDiv").css("display","none");
    var dataTable=$("#reportListTab");
    if (dataTable.hasClass('dataTable')) {
        dataTable.dataTable().fnClearTable();
    }
    $.ajax({
       url: "/api/lte-kpi-data/query-import-detail-id",
        data:{id:id},
        dataType: 'text',
        type:'get',
        success:showImportDetailRecord,
        error: function (err) {
            console.log(err)
        }
    });
}

function showImportDetailRecord(data) {
    $("#reportListTab").css("line-height", "12px")
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
            "ordering": false,
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
function returnToImportList(){
    $("#reportDiv").css("display","none");
    $("#listinfoDiv").css("display","block");
}


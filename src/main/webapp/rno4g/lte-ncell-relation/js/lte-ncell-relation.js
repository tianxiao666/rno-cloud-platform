$(function () {
    //tab选项卡
    tab("div_tab", "li", "onclick");//项目服务范围类别切换

    // 执行 laydate 实例 
    laydate.render({elem: '#begUploadDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endUploadDate', value: new Date()});

    // 初始化区域联动
    initAreaSelectors({selectors: ["provinceId", "cityId"]});

    //验证邻区关系查询条件
    $("#queryBtn").click(function () {
        var reg = /^[0-9]+.?[0-9]*$/;
        var cellPci =$("#cellPci").val();
        var ncellPci =$("#ncellPci").val();
        if(!reg.test(cellPci) && cellPci.trim() !==''){
            showInfoInAndOut("info", "主小区PCI值必须为数字");
            return false;
        }
        if(cellPci.length >10 && cellPci.trim() !==''){
            showInfoInAndOut("info","主小区PCI值输入过长");
            return false;
        }
        if(!reg.test(ncellPci) && ncellPci.trim() !==''){
            showInfoInAndOut("info", "邻小区PCI值必须为数字");
            return false;
        }
        if(ncellPci.length >10 && ncellPci.trim() !==''){
            showInfoInAndOut("info","邻小区PCI值输入过长");
            return false;
        }
        $(".loading").show();
    });

    //AJAX 提交邻区关系查询条件表单
    $("#conditionForm").ajaxForm({
        url: "/api/lte-ncell-relation/ncell-query",
        success: showNcellRelationResult
    });

    //AJAX 提交邻区导入记录查询条件表单
    $("#importQuery").ajaxForm({
        url: "/api/lte-ncell-relation/ncell-import-query",
        success: showNcellImportResult
    });

    // AJAX 上传文件
    var progress = $('.upload-progress');
    var bar = $('.bar');
    var percent = $('.percent');

    $("#file-upload-form").ajaxForm({
        url: "/api/lte-ncell-relation/upload-file",
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
});

//显示邻区关系查询结果
function showNcellRelationResult(data) {
    $(".loading").css("display","none");
    if (data == '') {
        showInfoInAndOut('info', '没有符合条件的邻区关系');
    }

    $('#queryResultTab').css("line-height", "12px");
    $('#queryResultTab').DataTable({
        "data": data,
        "columns": [
            {"data": "cellName"},
            {"data": "ncellName"},
            {"data": "cellId"},
            {"data": "cellEnodebId"},
            {"data": "ncellId"},
            {"data": "ncellEnodebId"},
            {"data": "cellPci"},
            {"data": "ncellPci"},
            {"data": null}
        ],
        "columnDefs": [{
            "render": function (data, type, row) {
                var id = row['id'];
                return "<a onclick=\"deleteCell('"+id+"')\">删除</a>";
            },
            "targets": -1,
            "data": null
        }
        ],
        "destroy":true,
        "language": {
            url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
        }
    });
}

//显示邻区关系导入记录查询结果
function showNcellImportResult(data) {
    $(".loading").css("display","none");
    if (data == '') {
        showInfoInAndOut('info', '没有符合条件的邻区关系');
    }

    $('#queryRecordResTab').css("line-height", "12px");
    $('#queryRecordResTab').DataTable({
        "data": data,
        "data": data,
        "columns": [
            {"data": "areaName"},
            {"data": "uploadTime"},
            {"data": "filename"},
            {"data": "fileSize"},
            {"data": "launchTime"},
            {"data": "completeTime"},
            {"data": "account"},
            {"data": null}
        ],
        "columnDefs": [ {
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
        "destroy":true,
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

//删除邻区关系
function deleteCell(id) {
    $.ajax({
        url: '/api/lte-ncell-relation/deleteByCellIdAndNcellId',
        dataType: 'text',
        data: {id:id},
        success: function () {
            showInfoInAndOut("info","删除邻区关系成功！");
            $("#conditionForm").ajaxForm({
                url: "/api/lte-ncell-relation/ncell-query",
                success: showNcellRelationResult,
                error: function (err) {
                    console.log(err);
                }
            });
        },error: function (err) {
            console.log(err);
            showInfoInAndOut("info","后台程序错误！");
        }
    })
}

//显示导入记录的状态的详情
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
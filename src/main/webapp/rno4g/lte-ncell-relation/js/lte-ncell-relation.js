$(function () {
    //tab选项卡
    $("#tabs").tabs();//项目服务范围类别切换

    // 执行 laydate 实例 
    laydate.render({elem: '#begUploadDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endUploadDate', value: new Date()});
    laydate.render({elem: '#begUploadDate2', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endUploadDate2', value: new Date()});

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
    })

    //验证邻区关系查询条件
    $("#queryBtn").click(function () {
        var reg = /^[0-9]+.?[0-9]*$/;
        var cellPci = $("#cellPci").val();
        var ncellPci = $("#ncellPci").val();
        if (!reg.test(cellPci) && cellPci.trim() !== '') {
            showInfoInAndOut("info", "主小区PCI值必须为数字");
            return false;
        }
        if (cellPci.length > 10 && cellPci.trim() !== '') {
            showInfoInAndOut("info", "主小区PCI值输入过长");
            return false;
        }
        if (!reg.test(ncellPci) && ncellPci.trim() !== '') {
            showInfoInAndOut("info", "邻小区PCI值必须为数字");
            return false;
        }
        if (ncellPci.length > 10 && ncellPci.trim() !== '') {
            showInfoInAndOut("info", "邻小区PCI值输入过长");
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

    //AJAX 提交邻区关系数据查询条件表单
    $("#searchNcellDtForm").ajaxForm({
        url: "/api/lte-ncell-relation/ncell-import-data-query",
        success: showNcellImportDtResult
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
        if (fileType !== '.csv' && fileType != '.zip') {
            showInfoInAndOut("info", "请上传csv或者zip格式的数据文件");
            return false;
        }
    });

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
    $(".loading").css("display", "none");
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
                return "<a onclick=\"deleteCell('" + id + "')\">删除</a>";
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

//显示邻区关系导入记录查询结果
function showNcellImportResult(data) {
    $(".loading").css("display", "none");
    if (data == '') {
        showInfoInAndOut('info', '没有符合条件的邻区关系导入记录');
    }

    $('#queryRecordResTab').css("line-height", "12px");
    $('#queryRecordResTab').DataTable({
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
            "render": function(data, type, row) {
                var d = new Date(parseInt(row['uploadTime'])).Format("yyyy-MM-dd hh:mm:ss");
                return d;
            },
            "targets": 1,
            "data": null
        },{
            "render": function(data, type, row) {
                if(row['startTime']==""||row['startTime']==null){
                    return "---";
                }else{
                    return new Date(parseInt(row['startTime'])).Format("yyyy-MM-dd hh:mm:ss");
                }
            },
            "targets": 4,
            "data": null
        },{
            "render": function(data, type, row) {
                if(row['completeTime']==""||row['completeTime']==null){
                    return "---";
                }else{
                    return new Date(parseInt(row['completeTime'])).Format("yyyy-MM-dd hh:mm:ss");
                }
            },
            "targets": 5,
            "data": null
        },{
            "render": function (data, type, row) {
                switch (row['status']) {
                    case "部分成功":
                        return "<a style='color: red' onclick='showImportDetail()'>" + row['status'] + "</a>";
                    case "全部失败":
                        return "<a style='color: red' onclick='showImportDetail()'>" + row['status'] + "</a>";
                    case "全部成功":
                        return "<a onclick='showImportDetail()'>" + row['status'] + "</a>";
                    case "正在处理":
                        return "<a onclick='showImportDetail()'>" + row['status'] + "</a>";
                    case "等待处理":
                        return "<a onclick='showImportDetail()'>" + row['status'] + "</a>";
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

//显示邻区关系数据查询结果
function showNcellImportDtResult(data) {
    $(".loading").css("display", "none");
    if (data == '') {
        showInfoInAndOut('info', '没有符合条件的邻区关系');
    }

    $('#queryDataResultDT').css("line-height", "12px");
    $('#queryDataResultDT').DataTable({
        "data": data,
        "columns": [
            {"data": "areaName"},
            {"data": "dataType"},
            {"data": "fileName"},
            {"data": "recordCount"},
            {"data": "jobId"},
            {"data": "createTime"}
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

function showInfoInAndOut(div, info) {
    var divSet = $("#" + div);
    divSet.html(info);
    divSet.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

//删除邻区关系
function deleteCell(id) {
    $.ajax({
        url: '/api/lte-ncell-relation/delete-by-id',
        dataType: 'text',
        data: {id: id},
        success: function () {
            showInfoInAndOut("info", "删除邻区关系成功！");
            $("#conditionForm").ajaxForm({
                url: "/api/lte-ncell-relation/ncell-query",
                success: showNcellRelationResult,
                error: function (err) {
                    console.log(err);
                }
            });
        }, error: function (err) {
            console.log(err);
            showInfoInAndOut("info", "后台程序错误！");
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

/**
 * 对Date的扩展，将 Date 转化为指定格式的String
 *月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
 *年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
 *例子：
 *(new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
 *(new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18
 * @title
 * @param fmt
 * @returns
 */
Date.prototype.Format = function(fmt)
{
    var o = {
        "M+" : this.getMonth()+1,                 //月份
        "d+" : this.getDate(),                    //日
        "h+" : this.getHours(),                   //小时
        "m+" : this.getMinutes(),                 //分
        "s+" : this.getSeconds(),                 //秒
        "q+" : Math.floor((this.getMonth()+3)/3), //季度
        "S"  : this.getMilliseconds()             //毫秒
    };
    if(/(y+)/.test(fmt))
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    for(var k in o)
        if(new RegExp("("+ k +")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
    return fmt;
}
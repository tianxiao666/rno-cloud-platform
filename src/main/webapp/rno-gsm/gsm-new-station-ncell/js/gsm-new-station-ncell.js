$(function () {
    // 设置导航标题
    setNavTitle("navTitle");
    // 设置jquery ui
    jqueryUiSet();
    // 执行 laydate 实例 
    laydate.render({elem: '#begUploadDate', value: new Date(new Date().getTime() - 7 * 86400000), format:'yyyy/MM/dd'});
    laydate.render({elem: '#endUploadDate', value: new Date(), format:'yyyy/MM/dd'});
    laydate.render({elem: '#fileDate', value: new Date(), format:'yyyy/MM/dd'});
    laydate.render({elem: '#beginTestDate', value: new Date(new Date().getTime() - 7 * 86400000), format:'yyyy/MM/dd'});
    laydate.render({elem: '#endTestDate', value: new Date(), format:'yyyy/MM/dd'});

    initAreaSelectors({selectors: ["provinceSearch", "citySearch", "areaSearch"]});
    initAreaSelectors({selectors: ["provinceImport", "cityImport", "areaImport"]});
    initAreaSelectors({selectors: ["provinceDesc", "cityDesc", "areaDesc"]});

    //显示隐藏导入窗口
    $("#importTitleDiv").click(function(){
        var flag = $("#importDiv").is(":hidden");//是否隐藏
        if(flag) {
            $(".importContent").show("fast");
        } else {
            $(".importContent").hide("fast");
        }
    });

    $("#queryBtn").click(function () {
        var dataMap = {
            'status':$("#importStatus").val(),
            'areaId':$("#areaSearch").val(),
            'beginDate':new Date($("#begUploadDate").val()),
            'endDate':new Date($("#endUploadDate").val()),
            'fileCode':$("#dateType").val()
        };
        $('#queryResultTab').css("line-height", "12px");
        $.ajax({
            url: '../../api/gsm-new-station-ncell/import-query',
            dataType: 'json',
            data: dataMap,
            type: 'post',
            success:function(data){
                $("#queryResultTab").DataTable({
                    "data": data,
                    "columns": [
                        {"data": "areaName"},
                        { "data": "createdDate", "render": function (data) {
                            return (new Date(data)).Format("yyyy-MM-dd hh:mm:ss");
                        }},
                        { "data": "filename" },
                        { "data": "fileSize", "render": function (data) {
                            return conver(data);
                        }},
                        { "data": null},
                        { "data": null},
                        { "data": "createdUser" },
                        { "data": null }
                    ],
                    "columnDefs": [{
                        "render": function (data, type, row) {
                            if (row['startTime'] === "" || row['startTime'] === null) {
                                return "---";
                            } else {
                                return (new Date(row['startTime'])).Format("yyyy-MM-dd hh:mm:ss");
                            }
                        },
                        "targets": 4,
                        "data": null
                    }, {
                        "render": function (data, type, row) {
                            if (row['completeTime'] === "" || row['completeTime'] === null) {
                                return "---";
                            } else {
                                return (new Date(row['completeTime'])).Format("yyyy-MM-dd hh:mm:ss");
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
                    destroy:true, //Cannot reinitialise DataTable,解决重新加载表格内容问题
                    "language": {
                        url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
                    }
                });
            }, error: function (err) {
                console.log(err);
                $("#info").css("background", "red");
            }
        });
    });

    $("#importNcellBtn").click(function() {
        var filename = fileid.value;
        if ($("#fileid").val() === "") {
            $("#err").show();
            return false;
        }
        if(!(filename.toUpperCase().endsWith(".CSV"))){
            $("#fileDiv").html("不支持该类型文件！");
            return false;
        }
        $("#uploadMsgDiv").css("display","none");
        doUpload();
    });
    //浏览文件绑定事件
    $("#fileid").change(function(){
        var filename = fileid.value;
        $("#err").hide();
        if(!(filename.toUpperCase().endsWith(".CSV"))){
            $("#fileDiv").html("不支持该类型文件！");
            return false;
        }else {
            $("#fileDiv").html("");
        }
    });

    $("#queryNcsBtn").click(function () {
        var dataMap = {
            'areaId':$("#areaDesc").val(),
            'fileCode':$("#fileCode").val(),
            'beginTestDate':new Date($("#beginTestDate").val()),
            'endTestDate':new Date($("#endTestDate").val())
        };
        $('#queryNcsResultTab').css("line-height", "12px");
        $.ajax({
            url: '../../api/gsm-new-station-ncell/desc-query',
            dataType: 'json',
            data: dataMap,
            type: 'post',
            success:function(data){
                $("#queryNcsResultTab").DataTable({
                    "data": data,
                    "columns": [
                        {"data": "areaName"},
                        { "data": "fileCode", "render": function (data) {
                            if (data === 'LTE-NEW-STATION-DATA') {
                                return 'LTE新站数据';
                            }
                            if (data === 'GSM-NEW-STATION-DATA') {
                                return 'GSM新站数据';
                            }
                            return '--';
                        }},
                        { "data": "filename" },
                        { "data": "recordCount" },
                        { "data": "createdDate", "render": function (data) {
                            return (new Date(data)).Format("yyyy-MM-dd hh:mm:ss");
                        }}
                    ],
                    destroy:true, //Cannot reinitialise DataTable,解决重新加载表格内容问题
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
    })
});

//jquery ui 效果
function jqueryUiSet() {
    $("#tabs").tabs();
    $("#searchImportDiv").css("height","46px");
    $("#importDiv").css("height","290px");
}

// 上传
function doUpload() {
    console.log("进入doUpload");
    fileSize = 1;
    // AJAX 上传文件
    var progress = $('.upload-progress');
    var bar = $('.bar');
    var percent = $('.percent');
    $("#formImportNcs").ajaxForm({
        url: "../../api/gsm-new-station-ncell/upload-file",
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
            //showInfoInAndOut("info","文件导入成功！");
        }
    })
}

//显示导入记录的状态的详情
function showImportDetail(id) {
    $.ajax({
        url: '../../api/lte-mr-data/query-report',
        dataType: 'text',
        type:'post',
        data: {id: id},
        success:function(data){

            $("#reportDiv").css("display", "block");
            $("#listInfoDiv").css("display", "none");
            $('#reportListTab').DataTable().clear();
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
function returnToImportList(){
    $("#reportDiv").css("display","none");
    $("#listInfoDiv").css("display","block");
}

Date.prototype.Format = function(fmt){
    //author: Shf
    var o = {
        "M+" : this.getMonth()+1,                 //月份
        "d+" : this.getDate(),                    //日
        "h+" : this.getHours(),                   //小时
        "m+" : this.getMinutes(),                 //分
        "s+" : this.getSeconds(),                 //秒
        "q+" : Math.floor((this.getMonth()+3)/3), //季度
        "S"  : this.getMilliseconds()             //毫秒
    };
    if(/(y+)/.test(fmt)){
        fmt = fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    }

    for(var k in o){
        if(new RegExp("("+ k +")").test(fmt)){
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length===1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
        }
    }
    return fmt;
};
function conver(limit){
    var size = "";
    if( limit < 0.1 * 1024 ){ //如果小于0.1KB转化成B
        size = limit.toFixed(2) + "B";
    }else if(limit < 0.1 * 1024 * 1024 ){//如果小于0.1MB转化成KB
        size = (limit / 1024).toFixed(2) + "KB";
    }else if(limit < 0.1 * 1024 * 1024 * 1024){ //如果小于0.1GB转化成MB
        size = (limit / (1024 * 1024)).toFixed(2) + "MB";
    }else{ //其他转化成GB
        size = (limit / (1024 * 1024 * 1024)).toFixed(2) + "GB";
    }

    var sizestr = size + "";
    var len = sizestr.indexOf("\.");
    var dec = sizestr.substr(len + 1, 2);
    if(dec === "00"){//当小数点后为00时 去掉小数部分
        return sizestr.substring(0,len) + sizestr.substr(len + 3,2);
    }
    return sizestr;
}
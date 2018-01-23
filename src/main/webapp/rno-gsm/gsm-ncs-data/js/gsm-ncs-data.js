var fileSize = 0;
var stopQueryProgress=false;///停止查询进度
$(function () {
    // 设置导航标题
    setNavTitle("navTitle");
    // 设置jquery ui
    jqueryUiSet();
    //绑定事件
    bindEvent();
    // 执行 laydate 实例 
    laydate.render({elem: '#begUploadDate', value: new Date(new Date().getTime() - 7 * 86400000),format: 'yyyy/MM/dd'});
    laydate.render({elem: '#endUploadDate', value: new Date(), format:'yyyy/MM/dd'});
    laydate.render({elem: '#fileDate', value: new Date(),format: 'yyyy/MM/dd'});
    laydate.render({elem: '#beginTestDate', value: new Date(new Date().getTime() - 7 * 86400000),format: 'yyyy/MM/dd'});
    laydate.render({elem: '#endTestDate', value: new Date(),format: 'yyyy/MM/dd'});

    $("#queryNcsBtn").click(function () {
        var dataMap = {
            'areaId':$("#city-id").val(),
            'factory':$("#SearchFactory").val(),
            'bsc':$("#bsc").val(),
            'beginTestDate':new Date($("#beginTestDate").val()),
            'endTestDate':new Date($("#endTestDate").val())
        };
        $('#queryNcsResultTab').css("line-height", "12px");
        $.ajax({
            url: '../../api/gsm-ncs-data/gsm-ncs-data-query',
            dataType: 'json',
            data: dataMap,
            type: 'post',
            success:function(data){
                $("#queryNcsResultTab").DataTable().clear();
                $("#queryNcsResultTab").DataTable({
                    "data": data,
                    "columns": [
                        {"data": "areaName"},
                        { "data": "meaTime", "render": function (data) {
                            return (new Date(data)).Format("yyyy-MM-dd hh:mm:ss");
                        }},
                        { "data": "bsc" },
                        { "data": "recordCount" },
                        { "data": "createTime", "render": function (data) {
                            return (new Date(data)).Format("yyyy-MM-dd hh:mm:ss");
                        }}
                    ],
                    searching:false, //去掉搜索框
                    bLengthChange:false,//去掉每页多少条框体
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
    });


    //执行 区域 实例 
    $("#province-menu").change(function () {
        var cityId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, cityId, "city-menu");
        })
    });

    $("#province-id").change(function () {
        var provinceId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, provinceId, "city-id");
        })
    });


    //初始化区域
    $.ajax({
        url: "../../data/area.json",
        dataType: "json",
        async: false,
        success: function (data) {
            renderArea(data, 0, "province-id");
            renderArea(data, 0, "province-menu");
            $("#province-id").change();
            $("#province-menu").change();
        }
    });

    $("#queryBtn").click(function () {
        var dataMap = {
            'status':$("#importStatus").val(),
            'areaId':$("#city-menu").val(),
            'beginDate':new Date($("#begUploadDate").val()),
            'endDate':new Date($("#endUploadDate").val()),
            'moduleName': 'gsm-ncs-data'
        };
        $('#queryResultTab').css("line-height", "12px");
        $.ajax({
            url: '../../api/gsm-ncs-data/gsm-import-query',
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
                    searching:false, //去掉搜索框
                    bLengthChange:false,//去掉每页多少条框体
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
    });





});
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

function showDetail(jobId) {
    $("#ncsListDiv").css("display","none");
    $("#ncsDetailDiv").css("display","block");
    initFormPage("searchNcsDetailForm");
    $("#hiddenNcsDescId").val(jobId);
    queryNcsDetailData();
}

// 渲染区域
function renderArea(data, parentId, areaMenu) {
    var arr = data.filter(function (v) {
        return v.parentId === parentId;
    });
    if (arr.length > 0) {
        var areaHtml = [];
        $.each(arr, function (index) {
            var area = arr[index];
            areaHtml.push("<option value='" + area.id + "'>");
            areaHtml.push(area.name + "</option>");
        });
        $("#" + areaMenu).html(areaHtml.join(""));

    } else {
        console.log("父ID为" + parentId + "时未找到任何下级区域。");
    }

}

//显示导入记录的状态的详情
function showImportDetail(id) {
    $.ajax({
        url: '../../api/gsm-ncs-data/query-report',
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


//绑定事件
function bindEvent(){
    $("#importNcsBtn").click(function() {
        var filename = fileid.value;
        if(!(filename.toUpperCase().endsWith(".ZIP")||filename.toUpperCase().endsWith(".CSV"))){
            $("#fileDiv").html("不支持该类型文件！");
            return false;
        }
        $("#err").remove();
        if ($("#fileid").val() === "") {
            $("#fileid").parent().append('<span id="err" style="color: red; ">请选择ncs文件</span>');
            return;
        }
        $("#uploadMsgDiv").css("display","none");
        stopQueryProgress=false;
        doUpload();
    });
    //浏览文件绑定事件
    $("#fileid").change(function(){
        var filename = fileid.value;
        if(!(filename.toUpperCase().endsWith(".ZIP")||filename.toUpperCase().endsWith(".CSV"))){
            $("#fileDiv").html("不支持该类型文件！");
            return false;
        }else {
            $("#fileDiv").html("");
        }
    });

    //显示隐藏导入窗口
    $("#importTitleDiv").click(function(){
        var flag = $("#importDiv").is(":hidden");//是否隐藏
        if(flag) {
            $(".importContent").show("fast");
        } else {
            $(".importContent").hide("fast");
        }
    });
}

//jquery ui 效果
function jqueryUiSet() {
    $("#tabs").tabs();
    $("#searchImportDiv").css("height","46px");
    $("#importDiv").css("height","290px");
}

//设置formid下的page信息
//其中，当前页会加一
function setFormPageInfo(formId, page) {
    if (formId === null || formId === undefined || page === null
        || page === undefined) {
        return;
    }

    var form = $("#" + formId);
    if (!form) {
        return;
    }
    form.find("#hiddenPageSize").val(page.pageSize);
    form.find("#hiddenCurrentPage").val(Number(page.currentPage));// /
    form.find("#hiddenTotalPageCnt").val(page.totalPageCnt);
    form.find("#hiddenTotalCnt").val(page.totalCnt);

}

/**
 * 设置分页面板
 *
 * @param page
 *            分页信息
 * @param divId
 *            分页面板id
 */
function setPageView(page, divId) {
    if (page === null || page === undefined) {
        return;
    }

    var div = $("#" + divId);
    if (!div) {
        return;
    }
    var currentPage = page['currentPage'] ? page['currentPage'] : 1;
    var totalPageCnt = page['totalPageCnt'] ? page['totalPageCnt'] : 0;
    var totalCnt = page['totalCnt'] ? page['totalCnt'] : 0;

    // 设置到面板上
    $(div).find("#emTotalCnt").html(totalCnt);
    $(div).find("#showCurrentPage").val(currentPage);
    $(div).find("#emTotalPageCnt").html(totalPageCnt);
}

//初始化form下的page信息
function initFormPage(formId) {
    var form = $("#" + formId);
    if (!form) {
        return;
    }
    form.find("#hiddenCurrentPage").val(1);
    form.find("#hiddenTotalPageCnt").val(-1);
    form.find("#hiddenTotalCnt").val(-1);
}

// 上传
function doUpload() {
    console.log("进入doUpload");
    var factory=$("#formImportNcs #factory").find("option:selected").val();
    $("#formImportNcs #fileCode").val(factory);
    $("#progressNum").text('0%');
    $("#progressbar").progressbar({
        value : 0
    });
    $("#progressInfoDiv").fadeIn();
    fileSize = 1;
    $("#cityId").val($("#city-menu").val());
    var selectDate = $("#fileDate").val();
    $("#fileDate").val(new Date($("#fileDate").val()));
    // AJAX 上传文件
    var progress = $('.upload-progress');
    var bar = $('.bar');
    var percent = $('.percent');
    $("#formImportNcs").ajaxForm({
        url: "../../api/gsm-ncs-data/upload-file",
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
        complete: function () {
            var percentVal = '100%';
            bar.width(percentVal);
            percent.html(percentVal);
            $("#info").css("background","green");
            $("#fileDate").val(selectDate);
            //showInfoInAndOut("info","文件导入成功！");
        }
    })
}

function showOperTips(outerId, tipId, tips) {
    try {
        var outerIdDiv = $("#" + outerId);
        outerIdDiv.css("display", "");
        outerIdDiv.find("#" + tipId).html(tips);
    } catch (err) {
    }
}

function hideOperTips(outerId) {
    try {
        $("#" + outerId).css("display", "none");
    } catch (err) {
    }
}
/**
 * 从报告的详情返回列表页面
 */
function returnToImportList(){
    $("#reportDiv").css("display","none");
    $("#listInfoDiv").css("display","block");
}
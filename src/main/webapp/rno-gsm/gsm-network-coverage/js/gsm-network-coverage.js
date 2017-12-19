$(function () {

    // 设置导航标题
    setNavTitle("navTitle");

    // 执行 laydate 实例 
    var begDate = new Date(new Date().getTime() - 7 * 86400000);
    var endDate = new Date();
    var begCreatedDate = laydate.render({//渲染开始时间选择
        elem: '#begDate', //通过id绑定html中插入的start
        value: begDate,
        type:"datetime",
        max: endDate.toString(),//设置一个默认最大值
        btns: ['clear', 'confirm'],
        done: function (value, dates) {
            endCreatedDate.config.min = {
                year: dates.year,
                month: dates.month - 1, //关键
                date: dates.date,
                hours: dates.hours,
                minutes: dates.minutes,
                seconds: dates.seconds
            };
        }
    });
    var endCreatedDate = laydate.render({//渲染结束时间选择
        elem: '#endDate',
        value: endDate,
        min: begDate.getFullYear() + "-" + (begDate.getMonth() + 1) + "-" + begDate.getDate() + " "
        + begDate.getHours() + ":" + begDate.getMinutes() + ":" + begDate.getSeconds(),//设置min默认最小值
        btns: ['clear', 'confirm'],
        type:"datetime",
        done: function (value, dates) {
            begCreatedDate.config.max = {
                year: dates.year,
                month: dates.month - 1,//关键
                date: dates.date,
                hours: dates.hours,
                minutes: dates.minutes,
                seconds: dates.seconds
            }
        }
    });

    // 初始化区域联动
    initAreaSelectors({selectors: ["provinceId", "cityId"]});

    // 任务表单提交
    $("#networkCoverageForm").ajaxForm({
        url:"/api/gsm-network-coverage/job-query",
        success:showJobQueryResult
    });

    // ncs数据查询表单提交
    $("#NcsDataForJobForm").ajaxForm({
        url:"/api/gsm-network-coverage/ncs-data-query",
        success:showNcsData
    });

    $("#searchInterMartixDT").click(function () {
        if($("#begDate").val().trim()===''||$("#endDate").val().trim()===''){
            showInfoInAndOut("info","请选择完整的创建时间段！");
            return false;
        }
        $(".loading").css("display", "block");
    });

    $("#showNcsDataDT").click(function () {
        $("#isDateRightTip").text("");
        if($("#begDate").val().trim()===''||$("#endDate").val().trim()===''){
            showInfoInAndOut("info","请选择完整的NCS测量时间段！");
            return false;
        }
        $(".loading").css("display", "block");
    });

    $("#calculateNcsInterMartix").click(function(){
        $("#isDateRightTip").text("");
        if($("#begDate").val().trim()===''||$("#endDate").val().trim()===''){
            showInfoInAndOut("info","请选择完整的NCS测量时间段！");
            return false;
        }
        // $("#addJobForm").submit();
        var data = {};
        data["cityId"] = $("#cityId").val();
        data["begMeaDate"] = $("#begDate").val();
        data["endMeaDate"] = $("#endDate").val();
        $(".loading").text("任务提交中...");
        $(".loading").css("display", "block");
        $.ajax({
            url: '/api/gsm-network-coverage/add-job',
            data:data,
            dataType: 'text',
            type:'post',
            success: addNetCoverJobResult,
            error: function (err) {
                console.log(err);
                showInfoInAndOut("info", "后台程序错误！");
            }
        });
    });
});

// 显示任务查询结果
function showJobQueryResult(data) {
    $(".loading").css("display", "none");
    if (data == '') {
        $("#info").css("background", "red");
        showInfoInAndOut('info', '没有符合条件的覆盖分析任务');
    }
    $('#analysisResultDT').DataTable().clear();
    $('#analysisResultDT').css("line-height", "12px");
    $('#analysisResultDT').DataTable( {
        "data": data,
        "columns": [
            { "data": "cityName" },
            { "data": "createdDate" },
            {"data" : null},
            { "data": "fileNumber" },
            { "data": "status" }
        ],
        "columnDefs": [{
            "render": function(data, type, row) {
                return row['begMeaTime'] + "~"+row['endMeaTime'];
            },
            "targets": 2,
            "data": null
        },{
            "render": function(data, type, row) {
                switch (row['status']) {
                    case "正常完成":
                        return row['status']+"："+ "<input type='button' value='下载结果文件'"+
                            " onclick=\"downloadResultFiles('" + row['id'] + "')\">";
                    case "异常终止":
                        return "<span style='color: red'>"+ row['status'] + "</span>";
                    default:
                        return row['status'];
                }
            },
            "targets": 4,
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

function downloadResultFiles(id) {
    $("#jobId").val(id);
    $("#downloadDirAngleFileForm").submit();
}

// 显示ncs数据查询结果
function showNcsData(data) {
    $(".loading").css("display", "none");
    if (data == '') {
        $("#isDateRightTip").text("该时间段内无数据");
    }
    $('#ncsResultDT').DataTable().clear();
    $('#ncsResultDT').css("line-height", "12px");
    $('#ncsResultDT').DataTable( {
        "data": data,
        "columns": [
            { "data": "areaName" },
            { "data": "bsc" },
            {"data" : "name"},
            { "data": "meaDate" }
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

// 任务提交结果
function addNetCoverJobResult(flag) {
    if(flag==="true"){
        setTimeout("window.location.href = 'gsm-network-coverage.html'",2000);
    }else{
        $(".loading").css("display", "none");
        $(".loading").text("加载中...");
        $("#isDateRightTip").text("该时间段内无数据");
    }
}

function showInfoInAndOut(div, info) {
    var divSet = $("#" + div);
    divSet.html(info);
    divSet.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

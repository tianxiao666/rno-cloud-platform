
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

    $(".draggable").draggable();
    $("#trigger").css("display", "none");

    $("#interferMatrixForm").ajaxForm({
        url:"/api/gsm-interfer-matrix/job-query",
        success:showJobQueryResult
    });

    $("#queryBtn").click(function () {
        if($("#begDate").val().trim() === '' || $("#endDate").val().trim() === ''){
            showInfoInAndOut('info','请选择完整的创建时间段！');
            return false;
        }
        $(".loading").css("display", "block");
    });

    // ncs数据查询表单提交
    $("#interferMartixAddNcsForm").ajaxForm({
        url:"/api/gsm-interfer-matrix/ncs-data-query",
        success:showNcsData
    });

    $("#queryBtn1").click(function () {
        $("#isDateRightTip").text("");
        if($("#begDate").val().trim() === '' || $("#endDate").val().trim() === ''){
            showInfoInAndOut('info','请选择完整的NCS数据测量时间段！');
            return false;
        }
        $(".loading").css("display", "block");
    });

    // 添加新的干扰矩阵任务
    $("#calculateNcsInterMartix").click(function(){
        $("#isDateRightTip").text("");
        if($("#begDate").val().trim() === '' || $("#endDate").val().trim() === ''){
            showInfoInAndOut('info','请选择完整的NCS数据测量时间段！');
            return false;
        }
        var data = {};
        data["cityId"] = $("#cityId").val();
        data["begMeaDate"] = $("#begDate").val();
        data["endMeaDate"] = $("#endDate").val();
        $.ajax({
            url: '/api/gsm-interfer-matrix/add-job',
            data:data,
            dataType: 'text',
            type:'post',
            success: addInterferMatrixJobResult,
            error: function (err) {
                console.log(err);
                showInfoInAndOut("info", "后台程序错误！");
            }
        });
    });
});

// 显示干扰矩阵任务查询结果
function showJobQueryResult(data) {
    $(".loading").css("display", "none");
    if (data == '') {
        $("#info").css("background", "red");
        showInfoInAndOut('info', '没有符合条件的干扰矩阵');
    }
    $('#queryResultTab').DataTable().clear();
    $('#queryResultTab').css("line-height", "12px");
    $('#queryResultTab').DataTable( {
        "data": data,
        "columns": [
            { data: "cityName" },
            { data: "createdDate" },
            { data: null },
            { data: "recordCount" },
            { data: "dataType" },
            { data: "status" }
        ],
        "columnDefs": [{
            "render": function(data, type, row) {
                return row['begMeaTime'] + "~"+row['endMeaTime'];
            },
            "targets": 2,
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

// 显示ncs数据查询结果
function showNcsData(data) {
    $(".loading").css("display", "none");
    if (data == '') {
        $("#isDateRightTip").text("该时间段内无数据");
        // $("#info").css("background", "red");
        // showInfoInAndOut('info', '没有符合条件的覆盖分析任务');
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
function addInterferMatrixJobResult(flag) {
        if(flag==="true"){
            window.location.href = "gsm-interfer-matrix.html";
        }else{
            $("#isDateRightTip").text("该时间段内无数据");
        }
    }

function showInfoInAndOut(div, info) {
    var divSet = $("#" + div);
    divSet.html(info);
    divSet.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

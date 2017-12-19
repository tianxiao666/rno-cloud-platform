var map, tiled;
$(function () {

    // 设置导航标题
    setNavTitle("navTitle");

    // 初始化区域联动
    initAreaSelectors({selectors: ["provinceId", "cityId"]});
    initAreaSelectors({selectors: ["provinceId2", "cityId2"]});

    laydate.render({elem: '#measureDate', value: new Date()});
    //执行 laydate 实例 
    var begTime = new Date(new Date().getTime() - 7 * 86400000);
    var endTime = new Date();
    var startDate = laydate.render({//渲染开始时间选择
        elem: '#begDate', //通过id绑定html中插入的start
        type: 'datetime',
        value: begTime,
        max: endTime.toString(),//设置一个默认最大值
        btns: ['clear', 'confirm'],
        done: function (value, dates) {
            endDate.config.min = {
                year: dates.year,
                month: dates.month - 1, //关键
                date: dates.date,
                hours: dates.hours,
                minutes: dates.minutes,
                seconds: dates.seconds
            };
        }
    });
    var endDate = laydate.render({//渲染结束时间选择
        elem: '#endDate',
        value: endTime,
        type: "datetime",
        min: begTime.getFullYear() + "-" + (begTime.getMonth() + 1) + "-" + begTime.getDate() + " "
        + begTime.getHours() + ":" + begTime.getMinutes() + ":" + begTime.getSeconds(),//设置min默认最小值
        btns: ['clear', 'confirm'],
        done: function (value, dates) {
            startDate.config.max = {
                year: dates.year,
                month: dates.month - 1,//关键
                date: dates.date,
                hours: dates.hours,
                minutes: dates.minutes,
                seconds: dates.seconds
            }
        }
    });

    var begMeaTime = new Date(new Date().getTime() - 5 * 86400000);
    var begMeaDate = laydate.render({//渲染开始时间选择
        elem: '#begMeaDate', //通过id绑定html中插入的start
        type: 'datetime',
        value: begMeaTime,
        max: endTime.toString(),//设置一个默认最大值
        btns: ['clear', 'confirm'],
        done: function (value, dates) {
            endMeaDate.config.min = {
                year: dates.year,
                month: dates.month - 1, //关键
                date: dates.date,
                hours: dates.hours,
                minutes: dates.minutes,
                seconds: dates.seconds
            };
        }
    });
    var endMeaDate = laydate.render({//渲染结束时间选择
        elem: '#endMeaDate',
        value: endTime,
        type: "datetime",
        min: begMeaTime.getFullYear() + "-" + (begMeaTime.getMonth() + 1) + "-" + begMeaTime.getDate() + " "
        + begMeaTime.getHours() + ":" + begMeaTime.getMinutes() + ":" + begMeaTime.getSeconds(),//设置min默认最小值
        btns: ['clear', 'confirm'],
        done: function (value, dates) {
            begMeaDate.config.max = {
                year: dates.year,
                month: dates.month - 1,//关键
                date: dates.date,
                hours: dates.hours,
                minutes: dates.minutes,
                seconds: dates.seconds
            }
        }
    });

    // 显示任务信息页已填数据
    var taskInfoStr = localStorage.getItem("taskInfoStr");
    if (taskInfoStr != null && taskInfoStr != "") {
        var taskInfoObject = JSON.parse(taskInfoStr);
        var taskInfo = {};
        $(taskInfoObject).each(function () {
            taskInfo[this.name] = this.value;
        });
        initAreaSelectors({selectors: ["provinceId2", "cityId2"], defaultAreaId: taskInfo["cityId"]});
        $("#jobName").val(taskInfo["jobName"]);
        $("#taskDescription").val(taskInfo["taskDescription"]);
        $("#begMeaDate").val(taskInfo["begMeaDate"]);
        $("#endMeaDate").val(taskInfo["endMeaDate"]);
        if (taskInfo["useEriData"] == "on") {
            $("#useEriData").prop("checked", true);
        } else {
            $("#useEriData").prop("checked", false);
        }
        if (taskInfo["useHwData"] == "on") {
            $("#useHwData").prop("checked", true);
        } else {
            $("#useHwData").prop("checked", false);
        }
        if (taskInfo["calConCluster"] == "on") {
            $("#calConCluster").prop("checked", true);
        }
        if (taskInfo["calClusterConstrain"] == "on") {
            $("#calClusterConstrain").prop("checked", true);
        } else {
            $("#calClusterConstrain").prop("checked", false);
        }
        if (taskInfo["calClusterWeight"] == "on") {
            $("#calClusterWeight").prop("checked", true);
        } else {
            $("#calClusterWeight").prop("checked", false);
        }
        if (taskInfo["calCellRes"] == "on") {
            $("#calCellRes").prop("checked", true);
        } else {
            $("#calCellRes").prop("checked", false);
        }
        if (taskInfo["calIdealDis"] == "on") {
            $("#calIdealDis").prop("checked", true);
        } else {
            $("#calIdealDis").prop("checked", false);
        }
    }

    // 显示参数页面修改数据
    var taskParamsStr = localStorage.getItem("taskParamsStr");
    if (taskParamsStr != null && taskParamsStr != "") {
        var taskParamsObject = JSON.parse(taskParamsStr);
        var taskParams = {};
        $(taskParamsObject).each(function () {
            taskParams[this.name] = this.value;
        });
        $("#SAMEFREQINTERTHRESHOLD").text(taskParams["SAMEFREQINTERTHRESHOLD"]);
        $("#OVERSHOOTINGIDEALDISMULTIPLE").text(taskParams["OVERSHOOTINGIDEALDISMULTIPLE"]);
        $("#BETWEENCELLIDEALDISMULTIPLE").text(taskParams["BETWEENCELLIDEALDISMULTIPLE"]);
        $("#CELLCHECKTIMESIDEALDISMULTIPLE").text(taskParams["CELLCHECKTIMESIDEALDISMULTIPLE"]);
        $("#CELLDETECTCITHRESHOLD").text(taskParams["CELLDETECTCITHRESHOLD"]);
        $("#CELLIDEALDISREFERENCECELLNUM").text(taskParams["CELLIDEALDISREFERENCECELLNUM"]);
        $("#GSM900CELLFREQNUM").text(taskParams["GSM900CELLFREQNUM"]);
        $("#GSM1800CELLFREQNUM").text(taskParams["GSM1800CELLFREQNUM"]);
        $("#GSM900CELLIDEALCAPACITY").text(taskParams["GSM900CELLIDEALCAPACITY"]);
        $("#GSM1800CELLIDEALCAPACITY").text(taskParams["GSM1800CELLIDEALCAPACITY"]);
        $("#DLCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD").text(taskParams["DLCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD"]);
        $("#ULCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD").text(taskParams["ULCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD"]);
        $("#INTERFACTORMOSTDISTANT").text(taskParams["INTERFACTORMOSTDISTANT"]);
        $("#INTERFACTORSAMEANDADJFREQMINIMUMTHRESHOLD").text(taskParams["INTERFACTORSAMEANDADJFREQMINIMUMTHRESHOLD"]);
        $("#RELATIONNCELLCITHRESHOLD").text(taskParams["RELATIONNCELLCITHRESHOLD"]);

        $("#TOTALSAMPLECNTSMALL").text(taskParams["TOTALSAMPLECNTSMALL"]);
        $("#TOTALSAMPLECNTTOOSMALL").text(taskParams["TOTALSAMPLECNTTOOSMALL"]);
        $("#SAMEFREQINTERCOEFBIG").text(taskParams["SAMEFREQINTERCOEFBIG"]);
        $("#SAMEFREQINTERCOEFSMALL").text(taskParams["SAMEFREQINTERCOEFSMALL"]);
        $("#OVERSHOOTINGCOEFRFFERDISTANT").text(taskParams["OVERSHOOTINGCOEFRFFERDISTANT"]);
        $("#NONNCELLSAMEFREQINTERCOEF").text(taskParams["NONNCELLSAMEFREQINTERCOEF"]);
    }

    // 显示任务详细信息
    showTaskDetailInfo();

    $(".draggable").draggable();
    $("#trigger").css("display", "none");

    //tab选项卡
    // tab("div_tab_ncs_cell", "li", "onclick");//项目服务范围类别切换

    $(".switch").click(function () {
        $(this).hide();
        $(".switch_hidden").show();
        $(".resource_list_icon").animate({
            right: '0px'
        }, 'fast');
        $(".resource_list_box").hide("fast");
    });
    $(".switch_hidden").click(function () {
        $(this).hide();
        $(".switch").show();
        $(".resource_list_icon").animate({
            right: '286px'
        }, 'fast');
        $(".resource_list_box").show("fast");
    });
    $(".zy_show").click(function () {
        $(".search_box_alert").slideToggle("fast");
    });

    $(".searchStructureTaskDT").click(function () {
        if($("#begDate").val().trim()===''||$("#endDate").val().trim()===''){
            showInfoInAndOut("info","请选择完整的创建时间段！");
            return false;
        }
        $(".loadingDataDiv").css("display", "block");
    });

    // Ajax提交查询任务表单
    $("#structureTaskForm").ajaxForm({
        url: "/api/gsm-struct-analysis/task-query",
        success: showStructTaskResult
    });
});

// 加载任务信息页
function addTask() {
    if ($("#navTitle").length > 0) {
        localStorage.clear();
    }
    window.location.href = 'gsm-struct-analysis-info.html';
}

// 加载参数页面
function taskParams() {
    if ($("#jobName").val() === "") {
        $("span#nameErrorText").html("（请输入任务名称）");
        $("span#nameError").html("※");
        return;
    }
    if($("#begMeaDate").val()==="" || $("#endMeaDate").val()===""){
        $("span#dateErrorText").html("请填写需要使用的测量数据的时间！");
        $("span#dateError").html("※");
        return;
    }
    else if(exDateRange($.trim($("#endMeaDate").val()),$.trim($("#begMeaDate").val())) > 10) {
        //验证测试日期是否大于十天
        $("span#dateErrorText").html("（时间跨度请不要超过10天！）");
        $("span#dateError").html("※");
        return;
    }
    if (!($('#useEriData').attr('checked')) && !($('#useEriData').attr('checked'))) {
        $("span#dataTypeErrorText").html("不能均不选择，至少选择一类!");
        return;
    }
    var taskInfoArray = $("#taskInfoTab").serializeArray();
    var provinceName = {
        name: "provinceName",
        value: $("#provinceId2 option:selected").text()
    };
    var cityName = {
        name: "cityName",
        value: $("#cityId2 option:selected").text()
    };
    taskInfoArray.push(provinceName);
    taskInfoArray.push(cityName);
    var taskInfoStr = decodeURIComponent(JSON.stringify(taskInfoArray));
    console.log(taskInfoStr);
    localStorage.setItem("taskInfoStr", taskInfoStr);
    window.location.href = 'gsm-struct-analysis-params.html';
}

// 加载任务提交页
function paramsSubmit() {
    var SAMEFREQINTERTHRESHOLD = $("#SAMEFREQINTERTHRESHOLD").text().trim();
    var OVERSHOOTINGIDEALDISMULTIPLE = $("#OVERSHOOTINGIDEALDISMULTIPLE").text().trim();
    var BETWEENCELLIDEALDISMULTIPLE = $("#BETWEENCELLIDEALDISMULTIPLE").text().trim();
    var CELLCHECKTIMESIDEALDISMULTIPLE = $("#CELLCHECKTIMESIDEALDISMULTIPLE").text().trim();
    var CELLDETECTCITHRESHOLD = $("#CELLDETECTCITHRESHOLD").text().trim();
    var CELLIDEALDISREFERENCECELLNUM = $("#CELLIDEALDISREFERENCECELLNUM").text().trim();
    var GSM900CELLFREQNUM = $("#GSM900CELLFREQNUM").text().trim();
    var GSM1800CELLFREQNUM = $("#GSM1800CELLFREQNUM").text().trim();
    var GSM900CELLIDEALCAPACITY = $("#GSM900CELLIDEALCAPACITY").text().trim();
    var GSM1800CELLIDEALCAPACITY = $("#GSM1800CELLIDEALCAPACITY").text().trim();
    var DLCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD = $("#DLCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD").text().trim();
    var ULCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD = $("#ULCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD").text().trim();
    var INTERFACTORMOSTDISTANT = $("#INTERFACTORMOSTDISTANT").text().trim();
    var INTERFACTORSAMEANDADJFREQMINIMUMTHRESHOLD = $("#INTERFACTORSAMEANDADJFREQMINIMUMTHRESHOLD").text().trim();
    var RELATIONNCELLCITHRESHOLD = $("#RELATIONNCELLCITHRESHOLD").text().trim();

    var TOTALSAMPLECNTSMALL = $("#TOTALSAMPLECNTSMALL").text().trim();
    var TOTALSAMPLECNTTOOSMALL = $("#TOTALSAMPLECNTTOOSMALL").text().trim();
    var SAMEFREQINTERCOEFBIG = $("#SAMEFREQINTERCOEFBIG").text().trim();
    var SAMEFREQINTERCOEFSMALL = $("#SAMEFREQINTERCOEFSMALL").text().trim();
    var OVERSHOOTINGCOEFRFFERDISTANT = $("#OVERSHOOTINGCOEFRFFERDISTANT").text().trim();
    var NONNCELLSAMEFREQINTERCOEF = $("#NONNCELLSAMEFREQINTERCOEF").text().trim();

    var reg = /^[-+]?[0-9]+(\.[0-9]+)?$/;   //验证数字
    var reg1 = /^[0-9]*[1-9][0-9]*$/;   //正整数
    var flag = true;
    if (!reg.test(parseInt(SAMEFREQINTERTHRESHOLD))) {
        $("span#SAMEFREQINTERTHRESHOLD1").html("※请输入数字※");
        return;
    }
    else if (SAMEFREQINTERTHRESHOLD <= 0) {
        $("span#SAMEFREQINTERTHRESHOLD1").html("※值需要大于0※");
        return;
    }
    if (!reg.test(OVERSHOOTINGIDEALDISMULTIPLE)) {
        $("span#OVERSHOOTINGIDEALDISMULTIPLE1").html("※请输入数字※");
        return;
    }
    else if (OVERSHOOTINGIDEALDISMULTIPLE < 1) {
        $("span#OVERSHOOTINGIDEALDISMULTIPLE1").html("※值需要大于等于1※");
        return;
    }
    if (!reg.test(BETWEENCELLIDEALDISMULTIPLE)) {
        $("span#BETWEENCELLIDEALDISMULTIPLE1").html("※请输入数字※");
        return;
    }
    else if (BETWEENCELLIDEALDISMULTIPLE < 1) {
        $("span#BETWEENCELLIDEALDISMULTIPLE1").html("※值需要大于等于1※");
        return;
    }
    if (!reg.test(CELLCHECKTIMESIDEALDISMULTIPLE)) {
        $("span#CELLCHECKTIMESIDEALDISMULTIPLE1").html("※请输入数字※");
        return;
    }
    else if (CELLCHECKTIMESIDEALDISMULTIPLE < 1) {
        $("span#CELLCHECKTIMESIDEALDISMULTIPLE1").html("※值需要大于等于1※");
        return;
    }
    if (!reg.test(CELLDETECTCITHRESHOLD)) {
        $("span#CELLDETECTCITHRESHOLD1").html("※请输入数字※");
        return;
    }
    else if (CELLDETECTCITHRESHOLD < 0.02) {
        $("span#CELLDETECTCITHRESHOLD1").html("※值需要大于等于0.02※");
        return;
    }
    if (!reg1.test(CELLIDEALDISREFERENCECELLNUM)) {
        $("span#CELLIDEALDISREFERENCECELLNUM1").html("※请输入正整数※");
        return;
    }
    else if (CELLIDEALDISREFERENCECELLNUM >= 10 || CELLIDEALDISREFERENCECELLNUM <= 0) {
        $("span#CELLIDEALDISREFERENCECELLNUM1").html("※值需要大于0小于10※");
        return;
    }
    if (!reg.test(GSM900CELLFREQNUM)) {
        $("span#GSM900CELLFREQNUM1").html("※请输入数字※");
        return;
    }
    else if (GSM900CELLFREQNUM <= 0) {
        $("span#GSM900CELLFREQNUM1").html("※值需要大于0※");
        return;
    }
    if (!reg.test(GSM1800CELLFREQNUM)) {
        $("span#GSM1800CELLFREQNUM1").html("※请输入数字※");
        return;
    }
    else if (GSM1800CELLFREQNUM <= 0) {
        $("span#GSM1800CELLFREQNUM1").html("※值需要大于0※");
        return;
    }
    if (!reg.test(GSM900CELLIDEALCAPACITY)) {
        $("span#GSM900CELLIDEALCAPACITY1").html("※请输入数字※");
        return;
    }
    else if (GSM900CELLIDEALCAPACITY <= 0) {
        $("span#GSM900CELLIDEALCAPACITY1").html("※值需要大于0※");
        return;
    }
    if (!reg.test(GSM1800CELLIDEALCAPACITY)) {
        $("span#GSM1800CELLIDEALCAPACITY1").html("※请输入数字※");
        return;
    }
    else if (GSM1800CELLIDEALCAPACITY <= 0) {
        $("span#GSM1800CELLIDEALCAPACITY1").html("※值需要大于0※");
        return;
    }
    if (!reg.test(DLCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD)) {
        $("span#DLCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD1").html("※请输入数字※");
        return;
    }
    else if (DLCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD < -94) {
        $("span#DLCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD1").html("※值需大于等于-94※");
        return;
    }
    if (!reg.test(ULCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD)) {
        $("span#ULCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD1").html("※请输入数字※");
        return;
    }
    else if (ULCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD < -100) {
        $("span#ULCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD1").html("※值需大于等于-100※");
        return;
    }
    if (!reg.test(INTERFACTORMOSTDISTANT)) {
        $("span#INTERFACTORMOSTDISTANT1").html("※请输入数字※");
        return;
    }
    if (INTERFACTORMOSTDISTANT >= 15 || INTERFACTORMOSTDISTANT <= 0) {
        $("span#INTERFACTORMOSTDISTANT1").html("※值需大于0小于15※");
        return;
    }
    if (!reg.test(INTERFACTORSAMEANDADJFREQMINIMUMTHRESHOLD)) {
        $("span#INTERFACTORSAMEANDADJFREQMINIMUMTHRESHOLD1").html("※请输入数字※");
        return;
    }
    else if (INTERFACTORSAMEANDADJFREQMINIMUMTHRESHOLD < 0.02) {
        $("span#INTERFACTORSAMEANDADJFREQMINIMUMTHRESHOLD1").html("※值需大于0.02※");
        return;
    }

    if (!reg.test(TOTALSAMPLECNTSMALL)) {
        $("span#TOTALSAMPLECNTSMALL1").html("※请输入数字※");
        return;
    }
    else if (TOTALSAMPLECNTSMALL > 5000) {
        $("span#TOTALSAMPLECNTSMALL1").html("※值需小于5000※");
        return;
    }
    if (!reg.test(TOTALSAMPLECNTTOOSMALL)) {
        $("span#TOTALSAMPLECNTTOOSMALL1").html("※请输入数字※");
        return;
    }
    else if (TOTALSAMPLECNTTOOSMALL > 1000) {
        $("span#TOTALSAMPLECNTTOOSMALL1").html("※值需小于1000※");
        return;
    }
    if (!reg.test(SAMEFREQINTERCOEFBIG)) {
        $("span#SAMEFREQINTERCOEFBIG1").html("※请输入数字※");
        return;
    }
    else if (SAMEFREQINTERCOEFBIG < 0.5) {
        $("span#SAMEFREQINTERCOEFBIG1").html("※值需大于0.5※");
        return;
    }
    if (!reg.test(SAMEFREQINTERCOEFSMALL)) {
        $("span#SAMEFREQINTERCOEFSMALL1").html("※请输入数字※");
        return;
    }
    else if (SAMEFREQINTERCOEFSMALL > 0.00105) {
        $("span#SAMEFREQINTERCOEFSMALL1").html("※值需小于0.00105※");
        return;
    }
    if (!reg.test(OVERSHOOTINGCOEFRFFERDISTANT)) {
        $("span#OVERSHOOTINGCOEFRFFERDISTANT1").html("※请输入数字※");
        return;
    }
    else if (OVERSHOOTINGCOEFRFFERDISTANT < 3) {
        $("span#OVERSHOOTINGCOEFRFFERDISTANT1").html("※值需大于3※");
        return;
    }
    if (!reg.test(NONNCELLSAMEFREQINTERCOEF)) {
        $("span#NONNCELLSAMEFREQINTERCOEF1").html("※请输入数字※");
        return;
    }
    else if (NONNCELLSAMEFREQINTERCOEF < 0.2) {
        $("span#NONNCELLSAMEFREQINTERCOEF1").html("※值需大于0.2※");
        return;
    }
    if (RELATIONNCELLCITHRESHOLD < 0.03) {
        $("span#RELATIONNCELLCITHRESHOLD1").html("※值需大于等于0.03※");
        return;
    }
    var taskParams = [{"name": "SAMEFREQINTERTHRESHOLD", "value": SAMEFREQINTERTHRESHOLD},
        {"name": "OVERSHOOTINGIDEALDISMULTIPLE", "value": OVERSHOOTINGIDEALDISMULTIPLE},
        {"name": "BETWEENCELLIDEALDISMULTIPLE", "value": BETWEENCELLIDEALDISMULTIPLE},
        {"name": "CELLCHECKTIMESIDEALDISMULTIPLE", "value": CELLCHECKTIMESIDEALDISMULTIPLE},
        {"name": "CELLDETECTCITHRESHOLD", "value": CELLDETECTCITHRESHOLD},
        {"name": "CELLIDEALDISREFERENCECELLNUM", "value": CELLIDEALDISREFERENCECELLNUM},
        {"name": "GSM900CELLFREQNUM", "value": GSM900CELLFREQNUM},
        {"name": "GSM1800CELLFREQNUM", "value": GSM1800CELLFREQNUM},
        {"name": "GSM900CELLIDEALCAPACITY", "value": GSM900CELLIDEALCAPACITY},
        {"name": "GSM1800CELLIDEALCAPACITY", "value": GSM1800CELLIDEALCAPACITY},
        {"name": "DLCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD", "value": DLCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD},
        {"name": "ULCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD", "value": ULCOVERMINIMUMSIGNALSTRENGTHTHRESHOLD},
        {"name": "INTERFACTORMOSTDISTANT", "value": INTERFACTORMOSTDISTANT},
        {"name": "INTERFACTORSAMEANDADJFREQMINIMUMTHRESHOLD", "value": INTERFACTORSAMEANDADJFREQMINIMUMTHRESHOLD},
        {"name": "RELATIONNCELLCITHRESHOLD", "value": RELATIONNCELLCITHRESHOLD},
        {"name": "TOTALSAMPLECNTSMALL", "value": TOTALSAMPLECNTSMALL},
        {"name": "TOTALSAMPLECNTTOOSMALL", "value": TOTALSAMPLECNTTOOSMALL},
        {"name": "SAMEFREQINTERCOEFBIG", "value": SAMEFREQINTERCOEFBIG},
        {"name": "SAMEFREQINTERCOEFSMALL", "value": SAMEFREQINTERCOEFSMALL},
        {"name": "OVERSHOOTINGCOEFRFFERDISTANT", "value": OVERSHOOTINGCOEFRFFERDISTANT},
        {"name": "NONNCELLSAMEFREQINTERCOEF", "value": NONNCELLSAMEFREQINTERCOEF}];
    console.log(JSON.stringify(taskParams));
    localStorage.setItem("taskParamsStr", JSON.stringify(taskParams));
    var taskInfoStr = localStorage.getItem("taskInfoStr");
    var taskInfoObject = JSON.parse(taskInfoStr);
    var taskInfo = {};
    $(taskInfoObject).each(function () {
        taskInfo[this.name] = this.value;
    });
    $.ajax({
        url: '/api/gsm-struct-analysis/query-file-number',
        data: taskInfo,
        type: 'post',
        success: function (raw) {
            localStorage.setItem("eriFileNum", JSON.stringify(raw));
        },
        complete: function () {
            window.location.href = 'gsm-struct-analysis-job-submit.html';
        }
    });
}

// 显示结构优化任务列表
function showStructTaskResult(data) {
    $(".loadingDataDiv").css("display", "none");
    if (data == '') {
        $("#info").css("background", "red");
        showInfoInAndOut('info', '没有符合条件的结构分析任务');
    }
    $('#optimizeResultDT').DataTable().clear();
    $('#optimizeResultDT').css("line-height", "12px");
    $('#optimizeResultDT').DataTable({
        "data": data,
        "columns": [
            {"data": "jobName"},
            {"data": null},
            {"data": "cityName"},
            {"data": "fileNumber"},
            {"data": null},
            {"data": null},
            {"data": null},
            {"data": null}
        ],
        "columnDefs": [{
            "render": function (data, type, row) {
                switch (row['status']) {
                    case "正常完成":
                        return "<span>"+ row['status'] + "</span>";
                    case "异常终止":
                        return "<span style='color: red'>"+ row['status'] + "</span>";
                }
            },
            "targets": 1,
            "data": null
        }, {
            "render": function (data, type, row) {
                return new Date(row['begMeaTime']).Format("yyyy-MM-dd hh:mm:ss") + " <br>至<br> "
                    + new Date(row['endMeaTime']).Format("yyyy-MM-dd hh:mm:ss");
            },
            "targets": 4,
            "data": null
        }, {
            "render": function (data, type, row) {
                return new Date(row['startTime']).Format("yyyy-MM-dd hh:mm:ss");
            },
            "targets": 5,
            "data": null
        }, {
            "render": function (data, type, row) {
                return new Date(row['completeTime']).Format("yyyy-MM-dd hh:mm:ss");
            },
            "targets": 6,
            "data": null
        }, {
            "render": function (data, type, row) {
                switch (row['status']) {
                    case "正常完成":
                        return " <input type='button' value='下载结果文件' onclick=\"downloadResultFiles('" + row['id'] + "')\">"+
                            "<input type='button' value='查看运行报告' onclick=\"checkStructureTaskReport('" + row['id'] + "')\">";
                    case "异常终止":
                        return "<input type='button' value='查看运行报告'"+
                            " onclick=\"checkStructureTaskReport('" + row['id'] + "')\">"
                }

            },
            "targets": 7,
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
    $("#downloadStructureFileForm").submit();
}

// 提交任务
function submitTask() {
    var taskInfoStr = localStorage.getItem("taskInfoStr");
    var taskInfoObject = JSON.parse(taskInfoStr);
    var taskInfo = {};
    $(taskInfoObject).each(function () {
        taskInfo[this.name] = this.value;
    });

    var taskParamsStr = localStorage.getItem("taskParamsStr");
    var taskParamsObject = JSON.parse(taskParamsStr);
    // var taskParams = {};
    $(taskParamsObject).each(function () {
        taskInfo[this.name] = this.value;
    });
    $.ajax({
        url: '/api/gsm-struct-analysis/submit-task',
        data: taskInfo,
        type: 'post',
        success: function () {
            $("#info").css("background", "green");
            showInfoInAndOut("info", "任务提交成功！");
        },
        complete: function () {
            window.location.href = 'gsm-struct-analysis.html';
        }
    });
}

/**
 * 从报告的详情返回列表页面
 */
function returnToTaskList(){
    $("#reportDiv").css("display","none");
    $("#structureTaskDiv").css("display","block");
}

function checkStructureTaskReport(jobId) {
    $.ajax({
        url: '/api/gsm-struct-analysis/query-report',
        dataType: 'text',
        type:'post',
        data: {id: jobId},
        success:function(data){
            $("#viewReportForm").find("input#hiddenJobId").val(jobId);

            $("#reportDiv").css("display", "block");
            $('#runResultDT').DataTable().clear();
            $("#structureTaskDiv").css("display", "none");
            $("#runResultDT").css("line-height", "12px")
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
                    "paging":false,
                    "info": false,
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

// 参数编辑
function editThis(obtn) {
    editText = $(obtn).html().trim(); // 取得表格单元格的文本
    setEditHTML(editText); // 初始化控件
    $(obtn).data("oldtxt", editText);  // 将单元格原文本保存在其缓存中，便修改失败或取消时用
    $(obtn).html(editHTML); // 改变单元格内容为编辑状态
    $(obtn).removeAttr("onclick"); // 删除单元格单击事件，避免多次单击
    $("#editTd").focus();
}

//点击td转换可编辑
function setEditHTML(value) {
    editHTML = '<input id="editTd" type="text" maxlength="10" onBlur="ok(this)" value="'
        + value + '" />';
}

function showTaskDetailInfo() {
    var eriFileNum = localStorage.getItem("eriFileNum");
    console.log(eriFileNum);
    if (eriFileNum != null && eriFileNum != "") {
        var taskInfoStr = localStorage.getItem("taskInfoStr");

        if (taskInfoStr != null && taskInfoStr != "") {
            var taskInfoObject = JSON.parse(taskInfoStr);
            var taskInfo = {};
            $(taskInfoObject).each(function () {
                taskInfo[this.name] = this.value;
            });
            $("#taskNameSubmit").text(taskInfo["jobName"]);
            $("#taskAreaSubmit").text(taskInfo["provinceName"] + taskInfo["cityName"]);
            $("#taskMeaDateSubmit").text(taskInfo["begMeaDate"] + "-" + taskInfo["endMeaDate"]);
            $("#taskDescSubmit").text(taskInfo["taskDescription"]);
        }
        $("#eriDataDetailTable").DataTable().clear();
        $("#eriDataDetailTable").css("line-height", "12px");
        $("#eriDataDetailTable").DataTable({
            "data": JSON.parse(eriFileNum),
            "columns": [
                {"data": "dateTime"},
                {"data": null},
                {"data": null},
                {"data": null}
            ],
            "columnDefs": [{
                "render": function (data, type, row) {
                    if(row['bscNum']===0) {
                        return "--";
                    }else{
                        return row['bscNum'];
                    }
                },
                "targets": 1,
                "data": null
            },{
                "render": function (data, type, row) {
                    if(row['ncsNum']===0) {
                        return "--";
                    }else{
                        return row['ncsNum'];
                    }
                },
                "targets": 2,
                "data": null
            },{
                "render": function (data, type, row) {
                    if(row['mrrNum']===0) {
                        return "--";
                    }else{
                        return row['mrrNum'];
                    }
                },
                "targets": 3,
                "data": null
            }
            ],
            "lengthChange": false,
            "ordering": false,
            "searching": false,
            "paging": false,
            "info": false,
            "destroy": true,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        });
        $("#hwDataDetailTable").DataTable().clear();
        $("#hwDataDetailTable").css("line-height", "12px");
        $("#hwDataDetailTable").DataTable({
            "data": JSON.parse(eriFileNum),
            "columns": [
                {"data": "dateTime"},
                {"data": null},
            ],
            "columnDefs": [{
                "render": function (data, type, row) {
                    if(row['bscNum']===0) {
                        return "--";
                    }else{
                        return row['bscNum'];
                    }
                },
                "targets": 1,
                "data": null
            }
            ],
            "lengthChange": false,
            "ordering": false,
            "searching": false,
            "paging": false,
            "info": false,
            "destroy": true,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        });
    }
}

// 修改
function ok(obtn) {
    var $obj = $(obtn).parent(); //div
    var value = $obj.find("input:text")[0].value; // 取得文本框的值，即新数据
    $obj.data("oldtxt", value); // 设置此单元格缓存为新数据
    $obj.html($obj.data("oldtxt"));
    $obj.attr("onclick", "editThis(this)");
}

// 取消任务
function calTask() {
    localStorage.clear();
    window.location.href = 'gsm-struct-analysis.html';
}

function showInfoInAndOut(div, info) {
    var divSet = $("#" + div);
    divSet.html(info);
    divSet.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

//计算两个日期差值
function exDateRange(sDate1,sDate2){
    var iDateRange;
    if(sDate1!=""&&sDate2!=""){
        var startDate=sDate1.replace(/-/g,"/");
        var endDate=sDate2.replace(/-/g,"/");
        var S_Date=new Date(Date.parse(startDate));
        var E_Date=new Date(Date.parse(endDate));
        iDateRange=(S_Date-E_Date)/86400000;
        //alert(iDateRange);
    }
    return iDateRange;
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
Date.prototype.Format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1,                 //月份
        "d+": this.getDate(),                    //日
        "h+": this.getHours(),                   //小时
        "m+": this.getMinutes(),                 //分
        "s+": this.getSeconds(),                 //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds()             //毫秒
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        }
    }
    return fmt;
};



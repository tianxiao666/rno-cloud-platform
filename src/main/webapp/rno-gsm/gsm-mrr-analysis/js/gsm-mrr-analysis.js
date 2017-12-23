var bscToCells;
$(function () {
//默认右侧栏打开
    var switchHidden = $(".switch_hidden");
    $(".resource_list300_box").css("display", "block");
    switchHidden.hide();
    $(".resource_list_icon").css("right", "300px;");
    $(".switch").click(function () {
        $(this).hide();
        $(".switch_hidden").show();
        $(".resource_list_icon").animate({
            right: "0px"
        }, "fast");
        $(".resource_list300_box").hide("fast");
    });
    switchHidden.click(function () {
        $(this).hide();
        $(".switch").show();
        $(".resource_list_icon").animate({
            right: "300px"
        }, "fast");
        $(".resource_list300_box").show("fast");
    });
    $(".zy_show").click(function () {
        $(".search_box_alert").slideToggle("fast");
    });

    var dateBeg = new Date();
    dateBeg.setFullYear(2014, 12, 1);
    var start = {
        elem: "#mrrMeaBegDate",
        type: "datetime",
        value: dateBeg,
        done: function (value, dates) {
            endRenderDate.config.min = {
                year: dates.year,
                month: dates.month - 1,
                date: dates.date,
                hours: dates.hours,
                minutes: dates.minutes,
                seconds: dates.seconds
            };
            var mrrMeaEndDate = $("#mrrMeaEndDate");
            if (Date.parse(value) > Date.parse(mrrMeaEndDate.val())) {
                mrrMeaEndDate.val(value);
            }
        }
    };
    var end = {
        elem: "#mrrMeaEndDate",
        type: "datetime",
        value: new Date(),
        done: function (value, dates) {
            startRenderDate.config.max = {
                year: dates.year,
                month: dates.month - 1,
                date: dates.date,
                hours: dates.hours,
                minutes: dates.minutes,
                seconds: dates.seconds
            };
            var mrrMeaBegDate = $("#mrrMeaBegDate");
            if (Date.parse(value) < Date.parse(mrrMeaBegDate.val())) {
                mrrMeaBegDate.val(value);
            }
        }
    };
    var startRenderDate = laydate.render(start);
    var endRenderDate = laydate.render(end);


//tab切换
    tab("div_tab", "li", "onclick");

//初始化区域
    initAreaSelectors({selectors: ["provinceId1", "cityId1"]});

    //输入小区名，模糊查询小区，把符合条件的小区列出来，其他的隐藏
    $("#inputCell").keyup(function () {
        searchCell();
    });

    getAllBscCell();

//初始化图表
    var myChart = echarts.init(document.getElementById("main"));
    var option = {
        color: ["#E2ECF7"],
        tooltip: "none",
        title: {
            text: "标题",
            left: "center",
            subtext: "请在右侧选择查询条件,输入小区ID进行查询",
            subtextStyle: {
                color: "#F16E73"
            }
        },
        legend: {
            data: []
        },
        xAxis: {
            name: "X轴",
            type: "category",
            data: ["05:00", "12:00", "18:00", "00:00"],
            axisTick: {
                alignWithLabel: true
            }
        },
        yAxis: {
            name: "Y轴",
            type: "category",
            splitArea: {
                show: true,
                areaStyle: {color: "#E2ECF7"}
            },
            splitLine: {show: true},
            data: ["0.0", "0.2", "0.4", "0.6", "0.8", "1.0", "1.2"]
        },
        series: [{
            name: "",
            type: "bar",
            data: []
        }]
    };
    myChart.setOption(option);

    $("#searchButton").click(function () {
        var inputCell = $("#inputCell");
        if (inputCell.val().trim() === "" || inputCell.val() === null) {
            showInfoInAndOut("info", "请先输入小区ID再进行查询！");
            return false;
        }
        if ($("#mrrMeaBegDate").val().trim() === "" || $("#mrrMeaEndDate").val().trim() === "") {
            showInfoInAndOut("info", "开始时间和结束时间不能为空！");
            return false;
        }
    });
    //查询小区信息
    $("#queryGsmMrrForm").ajaxForm({
        url: "/api/gsm-mrr-analysis/query-mrr-data",
        success: showChart,
        error: showError
    });
});

function showChart(data) {
    var mrrInfoTab = $("#mrrInfoTab");
    mrrInfoTab.children().remove();
    if (data == '') {
        showInfoInAndOut("info", "没有mrr对应的指标分析数据！");
        return false;
    }else {
        var UL = new Array();//上行有序数组
        var DL = new Array();//下行有序数组
        var boundGap = false;
        var cell = data[0]["CELL_NAME"];
        var channel_group_num = data[0]["CHANNEL_GROUP_NUM"];
        var title = "";//图表子标题
        var fieldUL = "";//上行字段前缀
        var fieldDL = "";//下行字段前缀
        var serieNameUL = "";//上行系统名
        var serieNameDL = "";//下行系统名
        var xAxis_name = "";//X轴名称
        var accumuValUL = 0;//上行累积值
        var accumuValDL = 0;//下行累积值
        var dataType = $("#mrrDataType").val();
        var chartType = $("#mrrChartType").val();
        var axis =new Array();
        if (dataType === "Rxlev") {
            title = cell + (channel_group_num === null ? "全部信道组" : "信道组号" + channel_group_num) + "的接收电平分布图";
            fieldUL = "RXLEVUL";
            fieldDL = "RXLEVDL";
            serieNameUL = "上行电平";
            serieNameDL = "下行电平";
            xAxis_name = "电平值(dBm)";
            for (var key in data[0]) {
                if (key.indexOf("RXLEVUL") !== -1) {
                    UL[getNum(key)] = data[0][key];
                    axis.push(getNum(key) - 110);

                }
                if (key.indexOf("RXLEVDL") !== -1) {
                    DL[getNum(key)] = data[0][key];
                }
            }
        } else if (dataType === "RxQual") {
            title = cell + (channel_group_num === null ? "全部信道组" : "信道组号" + channel_group_num) + "的通话质量分布图";
            fieldUL = "RXQUALUL";
            fieldDL = "RXQUALDL";
            serieNameUL = "上行质量";
            serieNameDL = "下行质量";
            xAxis_name = "质量";
            boundGap = true;
            for (var key in data[0]) {
                if (key.indexOf("RXQUALUL") !== -1) {
                    UL[getNum(key)] = data[0][key];
                    axis.push(getNum(key));
                }
                if (key.indexOf("RXQUALDL") !== -1) {
                    DL[getNum(key)] = data[0][key];
                }
            }
        } else if (dataType === "POWER") {
            title = cell + (channel_group_num === null ? "全部信道组" : "信道组号" + channel_group_num) + "的发射功率分布图";
            fieldUL = "MSPOWER";
            fieldDL = "BSPOWER";
            serieNameUL = "手机功率";
            serieNameDL = "基站功率";
            xAxis_name = "功率等级(dBm)";
            for (var key in data[0]) {
                if (key.indexOf("MSPOWER") !== -1) {
                    UL[getNum(key)] = data[0][key];
                    axis.push(getNum(key));
                }
                if (key.indexOf("BSPOWER") !== -1) {
                    DL[getNum(key)] = data[0][key];
                }
            }
        } else if (dataType === "PATHLOSS") {
            boundGap = true;
            title = cell + (channel_group_num === null ? "全部信道组" : "信道组号" + channel_group_num) + "的路径损耗分布图";
            fieldUL = "PLOSSUL";
            fieldDL = "PLOSSDL";
            serieNameUL = "上行路径损耗";
            serieNameDL = "下行路径损耗";
            xAxis_name = "路径损耗(dBm)";
            for (var key in data[0]) {
                if (key.indexOf("PLOSSUL") !== -1) {
                    UL[getNum(key)] = data[0][key];
                    axis.push(getNum(key) * 2 + 30);
                }
                if (key.indexOf("PLOSSDL") !== -1) {
                    DL[getNum(key)] = data[0][key];
                }
            }
        } else if (dataType === "PLDIFF") {
            boundGap = true;
            title = cell + (channel_group_num === null ? "全部信道组" : "信道组号" + channel_group_num) + "的上下行路径损耗分布图";
            fieldUL = "PLDIFF";
            serieNameUL = "下行-上行路径损耗差";
            xAxis_name = "下行-上行路径损耗差(dBm)";
            for (var key in data[0]) {
                if (key.indexOf("PLDIFF") !== -1) {
                    UL[getNum(key)] = data[0][key];
                    axis.push(getNum(key) - 25);
                }
            }
            DL.length = 0;
        } else if (dataType === "TA") {
            title = cell + (channel_group_num === null ? "全部信道组" : "信道组号" + channel_group_num) + "的时间提前量分布图";
            fieldUL = "TAVAL";
            serieNameUL = "时间提前量";
            xAxis_name = "时间提前量";
            for (var key in data[0]) {
                if (key.indexOf("TAVAL") !== -1) {
                    UL[getNum(key)] = data[0][key];
                    axis.push(getNum(key));
                }
            }
            DL.length = 0;
        }
        axis.sort(function compare(a,b){return a-b;});
        //填充测量信息表
        $("#mrrInfoTab").append("<TR><td class='menuTd'>"+dataType+"测量信息"+"</td><td>"+cell+"</td></TR>");
        $("#mrrInfoTab").append("<TR><td class='menuTd'>CHANNCELL_GROUP_NUM</td><td>"+(typeof(channel_group_num)==="undefined"?"全部":channel_group_num)+"</td></TR>");
        //累加
        for ( var i = 0; i < UL.length; i++) {
            $("#mrrInfoTab").append("<TR><td class='menuTd'>"+fieldUL+i+"</td><td>"+UL[i]+"</td></TR>");
            if(chartType==="accumulatedVal"){
                accumuValUL+=UL[i];
                UL[i]=accumuValUL;
            }
        }
        for ( var j = 0; j < DL.length; j++) {
            $("#mrrInfoTab").append("<TR><td class='menuTd'>"+fieldDL+j+"</td><td>"+DL[j]+"</td></TR>");
            if(chartType==="accumulatedVal"){
                accumuValDL+=DL[j];
                DL[j]=accumuValDL;
            }
        }
        //加载数据至echart
        var disMode = $("#mrrDisMode").val().trim();
        var myChart = echarts.init(document.getElementById("main"));
        myChart.clear();
        var option = {
            title : {
                text: dataType+'指标',
                subtext: title,
                x: "center", //标题水平方向位置
                subtextStyle:{color: '#4A4AFF'}
            },
            tooltip : {
                trigger: 'axis'
            },
            legend: {
                data:[serieNameDL,serieNameUL] ,
                y: 'top',
                x: 'left',
            },
            toolbox: {
                show : true,
                feature : {
                    mark : {show: true},
                    saveAsImage : {show: true}
                }
            },
            calculable : true,
            xAxis : [
                {
                    name:xAxis_name,
                    type : 'category',
                    boundaryGap : boundGap,
                    data : axis
                }
            ],
            yAxis : [
                {
                    name:'采样点个数(个)',
                    type : 'value',
                    axisLabel : {
                        formatter: '{value}'
                    },
                    splitLine:{lineStyle:{type: 'dashed'}},
                    axisLine:{lineStyle:{color: '#223434'}}
                }
            ],
            series : [
                {
                    name:serieNameDL,
                    type:disMode,
                    data:DL,
                    markPoint : {
                        data : [
                            {type : 'max', name: '最大值'},
                            {type : 'min', name: '最小值'}
                        ]
                    },
                    markLine : {
                        data : [
                            {type : 'average', name : '平均值'}
                        ]
                    },
                    itemStyle :{normal :{color:"#007300"}}
                },
                {
                    name:serieNameUL,
                    type:disMode,
                    data:UL,
                    markPoint : {
                        data : [
                            {type : 'max', name: '最大值'},
                            {type : 'min', name: '最小值'}
                        ]
                    },
                    markLine : {
                        data : [
                            {type : 'average', name: '平均值'}
                        ]
                    },
                    itemStyle :{normal :{color:"#FF0000"}}
                }
            ]
        };
        // 为echarts对象加载数据
        myChart.setOption(option);
    }
}

function showError() {
    $(".loading").css("display", "none");
    $("#info").css("background", "red");
    showInfoInAndOut("info", "后台程序错误！");
}

function searchCell() {
    var cell = $.trim($("#inputCell").val());
    //清空
    $("#allBscCell").html("");

    //符合搜索条件的bsc到cell信息的映射对象
    var obj = new Object();
    var list;
    for (var key in bscToCells) {
        list = new Array();
        for (var i = 0; i < bscToCells[key].length; i++) {
            if (bscToCells[key][i]["LABEL"].indexOf(cell) >= 0) {
                list.push(bscToCells[key][i]);
            }
        }
        if (list.length > 0) {
            obj[key] = list;
        }
    }
    //填充至bsc信息栏
    showBsc(obj);
}

function getAllBscCell() {
    $("#allBscCell").html("");
    $("#inputCell").val("");
    var cityId = $("#cityId1").val();
    $.ajax({
        url: "/api/gsm-mrr-analysis/get-bsc-by-cityId",
        data: {
            "cityId": cityId
        },
        type: "get",
        dataType: "text",
        success: function (raw) {
            var data = eval("(" + raw + ")");
            bscToCells = data;
            //填充至bsc信息栏
            showBsc(data);
        }, error: function (err) {
            $("#info").css("background", "red");
            showInfoInAndOut("info", "后台程序错误！");
        }
    });
}

function showBsc(obj) {
    //排序BSC
    var bscList = new Array();
    for (var key in obj) {
        bscList.push(key);
    }
    bscList.sort();
    var bscHtml = "";
    for (var i = 0; i < bscList.length; i++) {
        bscHtml += "<li><span class='bscCls' id='" + bscList[i] + "'>" + bscList[i] + "(小区数量：" +
            obj[bscList[i]].length + ")</span><ul id='" + bscList[i] + "'></ul></li>";
    }

    $("#allBscCell").html(bscHtml);
    $("#allBscCell").treeview({
        collapsed: true
    });

    $("span.bscCls").on("click", function () {
        var bsc = $(this).attr("id");
        showBscDetail(bsc, obj);
    });
    //图标事件
    $("#allBscCell").find("div").bind("click", function (event) {
        var bsc = $(this).parent().find("span").attr("id");
        showBscDetail(bsc, obj);
    });
}

function showBscDetail(bsc, obj) {
    var cells1 = obj[bsc];
    var cellsHtml = "";
    var all;
    var part;
    for (var i = 0; i < cells1.length; i++) {
        all = cells1[i]["LABEL"] + "(" + cells1[i]["NAME"] + ")";
        part = all.length > 12 ? all.substring(0, 12) + "..." : all;
        cellsHtml += "<li><span style='cursor: pointer' class='cellCls' data='" + cells1[i]["LABEL"] +
            "'" + " bsc='" + bsc + "'" + " manufacturers='" + cells1[i]["MANUFACTURERS"] + "' " + " all='" +
            all + "' " + " part='" + part + "'>" + part + "</span></li>";
    }
    $("ul#" + bsc).html(cellsHtml);
    //单选小区至小区输入框内
    $("span.cellCls").on("click", function () {
        var EN_NAME = $(this).attr("data");
        $("#inputCell").val(EN_NAME);
    });
}

function showInfoInAndOut(div, info) {
    var divSet = $("#" + div);
    divSet.html(info);
    divSet.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

function getNum(text){
    var value = text.replace(/[^0-9]/ig,"");
    return value;
}
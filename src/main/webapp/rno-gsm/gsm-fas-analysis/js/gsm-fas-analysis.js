$(function () {

    //tab切换
    tab("div_tab", "li", "onclick");
    //默认右侧栏打开
    var switchHidden =$(".switch_hidden");
    $(".resource_list300_box").css("display", "block");
    switchHidden.hide();
    $(".resource_list_icon").css("right", "300px;");
    $(".switch").click(function () {
        $(this).hide();
        $(".switch_hidden").show();
        $(".resource_list_icon").animate({
            right: '0px'
        }, 'fast');
        $(".resource_list300_box").hide("fast");
    });
    switchHidden.click(function () {
        $(this).hide();
        $(".switch").show();
        $(".resource_list_icon").animate({
            right: '300px'
        }, 'fast');
        $(".resource_list300_box").show("fast");
    });
    $(".zy_show").click(function () {
        $(".search_box_alert").slideToggle("fast");
    });


    // 初始化区域联动
    initAreaSelectors({selectors: ["provinceId", "cityId"]});

    //执行 laydate 实例 
    var dateBeg = new Date();
    dateBeg.setFullYear(2015,7,1);
    var dateEnd = new Date();
    dateEnd.setFullYear(2015,8,30);

    var start = {
        elem: '#fasMeaBegDate',
        type: 'date',
        value: dateBeg,
        done: function (value,dates) {
            endRenderDate.config.min = {
                year: dates.year,
                month: dates.month - 1,
                date: dates.date,
                hours: dates.hours,
                minutes: dates.minutes,
                seconds: dates.seconds
            };
            var fasMeaEndDate =$("#fasMeaEndDate");
            if(Date.parse(value)  > Date.parse(fasMeaEndDate.val())){
                fasMeaEndDate.val(value);
            }
        }
    };
    var end = {
        elem: '#fasMeaEndDate',
        type: 'date',
        value: dateEnd,
        done: function (value,dates) {
            startRenderDate.config.max = {
                year: dates.year,
                month: dates.month - 1,
                date: dates.date,
                hours: dates.hours,
                minutes: dates.minutes,
                seconds: dates.seconds
            };
            // console.log(Date.parse(value)- start.value.getTime());
            var fasMeaBegDate =$("#fasMeaBegDate");
            if(Date.parse(value)  < Date.parse(fasMeaBegDate.val())){
                fasMeaBegDate.val(value);
            }
        }
    };
    var startRenderDate = laydate.render(start);
    var endRenderDate = laydate.render(end);

    //初始化图表
    var myChart=echarts.init(document.getElementById('main'));
    var option ={
        color: ['#E2ECF7'],
        tooltip : 'none',
        title: {
            text:  '标题',
            left: 'center',
            subtext:'请在右侧选择查询条件，输入小区ID进行查询',
            subtextStyle:{
                color: '#F16E73'
            }
        },
        legend: {
            data:[]
        },
        xAxis: {
            name: 'X轴',
            type : 'category',
            data: ['00:00','06:00','12:00','18:00','00:00'],
            axisTick: {
                alignWithLabel: true
            }
        },
        yAxis: {
            name : 'Y轴',
            type : 'category',
            splitArea : {
                show : true,
                areaStyle: {color: '#E2ECF7'}
            },
            splitLine:{show: true},
            data: ['0.0','0.2','0.4','0.6', '0.8','1.0','1.2']
        },
        series: [{
            name: '',
            type: 'bar',
            data: []
        }]
    };
    myChart.setOption(option);

    $("#searchButton").click(function () {
        var inputCell=$("#inputCell");
        if(inputCell.val().trim() === '' || inputCell.val() === null){
            showInfoInAndOut("info","请先输入小区ID再进行查询！");
            return false;
        }

        if($("#fasMeaBegDate").val().trim() === ''|| $("#fasMeaEndDate").val().trim() === ''){
            showInfoInAndOut('info', '开始时间和结束时间不能为空！');
            return false;
        }

        if($("#cityId").val() !=='440100'){
            showInfoInAndOut('info','无FAS测量数据！');
            return false;
        }
    });

    $("#queryFasForm").ajaxForm({
        url: "/api/gsm-fas-analysis/fas-chart-query",
        success:function (data) {
            $("#fasInfoTab").children().remove();
            myChart.setOption(option);
            showChart(data, myChart);
        },
        error: function (err) {
            console.log(err);
        }
    });
});

function showChart(data,myChart){
    console.log(data);
    if(data === null || data===''|| data.length ===0){
        showInfoInAndOut('info','无FAS测量数据');
        return false;
    }
    var oneData = data[0];
    var arfcn = oneData['ARFCN_1_150'];
    var avmedian =oneData ['AVMEDIAN_1_150'];
    var avpercentile = oneData ['AVPERCENTILE_1_150'];
    var cellName = oneData['CELL_NAME'];
    var bcch =oneData['BCCH'];
    var cell = oneData['CELL'];
    var tch = oneData['TCH'];


    var fasInfoTab =$("#fasInfoTab");
    fasInfoTab.append("<tbody><tr><td colspan=\"2\" align=\"center\">测量信息</td></tr>" +
        "<tr><td>CELL</td><td>"+cell+"</td></tr>" +
        "<tr><td>CELL_NAME</td><td>"+cellName+"</td></tr>");
    for(var b =0; b<avpercentile.split(',').length;b++){
        var bValue ="AVPERCENTILE_" +b;
        fasInfoTab.append("<tr><td >"+bValue+"</td><td>"+avpercentile.split(',')[b]+"</td></tr>");
    }
    for(var a = 0; a<avmedian.split(',').length;a++) {
        var aValue ="AVMEDIAN_"+a;
        fasInfoTab.append("<tr><td >"+aValue+"</td><td>"+avmedian.split(',')[a]+"</td></tr>");
    }
    fasInfoTab.append("</tbody>");

    var barOption ={
        color: ['#3398DB'],
        tooltip : {
            trigger: 'axis',
            axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                type : 'cross',
                crossStyle: {
                    color: '#999'
                }// 默认为直线，可选为：'line' | 'shadow'
            }
        },
        title: {
            text:  'FAS测量数据',
            left: 'center',
            subtext:cell+'的干扰情况，BCCH='+ bcch+',TCH='+tch,
            subtextStyle:{
                color: '#6C6FFD'
            }
        },
        legend: {
            right: 'right',
            data:['AVPERCENTILE','AVMEDIAN']
        },
        xAxis: {
            name: '频点',
            type : 'category',
            data: arfcn.split(','),
            axisPointer: {
                type: 'shadow'
            },
            axisTick: {
                alignWithLabel: true
            }
        },
        yAxis: {
            name : '干扰值',
            type : 'value',
            interval:5
        },
        series: [{
            name: 'AVPERCENTILE',
            type: 'bar',
            data: avpercentile.split(',')
        },{
            name: 'AVMEDIAN',
            type: 'line',
            data: avmedian.split(','),
            itemStyle:{
                normal:{
                    lineStyle:{
                        color: '#FFB53D',
                        width: 3
                    },
                    color:'#FFB53D'
                }
            }
        }]
    };
    myChart.setOption(barOption)
}


function showInfoInAndOut(div, info) {
    var divSet = $("#" + div);
    divSet.html(info);
    divSet.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

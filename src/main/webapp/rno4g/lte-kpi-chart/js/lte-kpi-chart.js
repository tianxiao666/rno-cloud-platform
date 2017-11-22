$(function () {

    $(".draggable").draggable();
    $("#trigger").css("display", "none");

    //执行 laydate 实例 
    var dateBeg = new Date();
    dateBeg.setFullYear(2015,8,1);
    var dateEnd = new Date();
    dateEnd.setFullYear(2015,8,30);

    laydate.render({elem: '#mrMeaBegDate', value: dateBeg});
    laydate.render({elem: '#mrMeaEndDate', value: dateEnd});
    //tab切换
    tab("div_tab", "li", "onclick");

    //初始化区域
    initAreaSelectors({selectors: ["provinceId1", "cityId1"]});

    //初始化图表
    var myChart=echarts.init(document.getElementById('main'));
    myChart.setOption({
        color: ['#3398DB'],
        tooltip : {
            trigger: 'axis',
            axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
            }
        },
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
            data: ['00:00','05:00','12:00','18:00','00:00'],
            axisTick: {
                alignWithLabel: true
            }
        },
        yAxis: {
            name : 'Y轴',
            type : 'value',
            data: ['0.0','0.2','0.4','0.6', '0.8','1.0','1.2']
        },
        series: [{
            name: '',
            type: 'bar',
            data: ['0']
        }]
    });
    //查询小区信息
    $("#queryKpiForm").ajaxForm({
        url: "/api/lte-kpi-chart/chart-query",
        success:function (data) {
            showChart(data, myChart);
        },
        error: function (err) {
            console.log(err);
        }
    });

});

function showChart(data,myChart) {
    var mrrInfoTab= $("#mrrInfoTab");
    mrrInfoTab.children().remove();
    console.log(data);
    if(data ===null || data === ''){
        showInfoInAndOut("info", "无符合条件的指标分布数据！");
        return false;
    }
    var chartTitle =$("#mrDataType").val();
    var cellId = $("#inputCell").val();
    var cellName = data['cellName']===null? '':data['cellName'];
    $("#tab1Title").html(chartTitle+ '测量信息');
    $("#tab1CellName").html(cellName);

    var xAxisVal =[];
    var yAxisVal =[];
    if(chartTitle==='RSRP'){
        xAxisVal =['X < -110','-110 <= X < -95','-95 <= X < -80','X >= -80'];
        yAxisVal =[data['xbelowNegative110'],data['xbetweenNegative110And95'],data['xbetweenNegative95And80'],data['xonNegative80']];
        mrrInfoTab.append("<tbody><tr><td colspan='2' style='text-align: center'>"+chartTitle+"测量信息"+"</td></tr>"
            +"<tr><td class='menuTd'>CELL NAME</td><td>"+cellName+"</td></tr>"
            +"<tr><td class='menuTd'>X < -110</td><td>"+data['xbelowNegative110']+"</td></tr>"
            +"<tr><td class='menuTd'>-110 <= X < -95</td><td>"+data['xbetweenNegative110And95']+"</td></tr>"
            +"<tr><td class='menuTd'>-95 <= X < -80</td><td>"+data['xbetweenNegative95And80']+"</td></tr>"
            +"<tr><td class='menuTd'>X >= -80</td><td>"+data['xonNegative80']+"</td></tr></tbody>"

        );
    }else if(chartTitle ==='RSRQ'){
        xAxisVal =['X < 8','8 <= X <15','X >= 15'];
        yAxisVal =[data['xbelow8'],data['xbetween8And15'],data['xon15']];
        mrrInfoTab.append("<tbody><tr><td colspan='2' style='text-align: center;'>"+chartTitle+"测量信息"+"</td></tr>"
            +"<tr><td class='menuTd'>CELL NAME</td><td>"+cellName+"</td></tr>"
            +"<tr><td class='menuTd'>X < 8</td><td>"+data['xbelow8']+"</td></tr>"
            +"<tr><td class='menuTd'>8 <= X <15</td><td>"+data['xbetween8And15']+"</td></tr>"
            +"<tr><td class='menuTd'>X >= 15</td><td>"+data['xon15']+"</td></tr></tbody>"
        );
    }else{
        xAxisVal = ['X < -110','-110 <= X < -95','-95 <= X <-85','X >= -85'];
        yAxisVal =[data['xbelowNegative110'],data['xbetweenNegative110And95'],data['xbetweenNegative95And85'],data['xonNegative85']];
        mrrInfoTab.append("<tbody><tr><td colspan='2' style='text-align: center'>"+chartTitle+"测量信息"+"</td></tr>"
            +"<tr><td class='menuTd'>CELL NAME</td><td>"+cellName+"</td></tr>"
            +"<tr><td class='menuTd'>X < -110</td><td>"+data['xbelowNegative110']+"</td></tr>"
            +"<tr><td class='menuTd'>-110 <= X < -95</td><td>"+data['xbetweenNegative110And95']+"</td></tr>"
            +"<tr><td class='menuTd'>-95 <= X < -85</td><td>"+data['xbetweenNegative95And85']+"</td></tr>"
            +"<tr><td class='menuTd'>X >= -85</td><td>"+data['xonNegative85']+"</td></tr></tbody>"

        );
    }
    var option = {
        color: ['#3398DB'],
        tooltip : {
            trigger: 'axis',
            axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
            }
        },
        title: {
            text: chartTitle + '指标',
            left: 'center',
            subtext:cellId+'('+  cellName+')信号接收功能'+ chartTitle+'分布图',
            subtextStyle:{
                color: '#6C6FFD'
            }
        },
        legend: {
            data:[]
        },
        xAxis: {
            type : 'category',
            data: xAxisVal,
            axisTick: {
                alignWithLabel: true
            }
        },
        yAxis: {
            name : '采样点个数('+ data['sampleNum']+")个",
            type : 'value'
        },
        series: [{
            name: '',
            type: 'bar',
            data: yAxisVal,
            itemStyle: {
                normal: {
                    label: {
                        show: true,
                        position: 'top',
                        textStyle: {
                            color: function(params) {
                                var colorList = ['#C33531', '#0AAF9F', '#64BD3D', '#EE9201'];
                                return colorList[params.dataIndex]
                            }
                        },
                        formatter:function(params){
                            if(params.value===0){
                                return '';
                            }else
                            {
                                return params.value+ "(占比"+parseFloat(params.value/data['sampleNum']*100).toFixed(2) +"%)";
                            }
                        }
                    },
                    color: function(params) {
                        var colorList = ['#C33531','#0AAF9F','#64BD3D','#EE9201' ];
                        return colorList[params.dataIndex]
                    }
                }
            }
        }]
    };

    // 使用刚指定的配置项和数据显示图表。
    myChart.setOption(option);
}

function showInfoInAndOut(div, info) {
    var divSet = $("#" + div);
    divSet.html(info);
    divSet.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}
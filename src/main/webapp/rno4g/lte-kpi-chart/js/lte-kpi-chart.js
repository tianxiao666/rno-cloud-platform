$(function () {

    $(".draggable").draggable();
    $("#trigger").css("display", "none");

    //执行 laydate 实例 
    var dateBeg = new Date();
    dateBeg.setFullYear(2015,8,1);
    var dateEnd = new Date();
    dateEnd.setFullYear(2015,8,30);

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

    laydate.render({elem: '#mrMeaBegDate', value: dateBeg});
    laydate.render({elem: '#mrMeaEndDate', value: dateEnd});
    //tab切换
    tab("div_tab", "li", "onclick");

    //初始化区域
    initAreaSelectors({selectors: ["provinceId1", "cityId1"]});

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

    $("#searchButton").click(function () {
        var inputCell=$("#inputCell");
        if(inputCell.val().trim() === '' || inputCell.val() === null){
            showInfoInAndOut("info","请先输入小区ID再进行查询！");
            return false;
        }
    });
    //查询小区信息
    $("#queryKpiForm").ajaxForm({
        url: "/api/lte-kpi-chart/chart-query",
        success:function (data) {
            myChart.setOption(option);
            showChart(data, myChart);
        },
        error: function (err) {
            console.log(err);
        }
    });

    myChart.setOption(option);
    var disMode=$("#mrDisMode");
    disMode.on("change",function () {
        var inputCell=$("#inputCell");
        if(inputCell.val().trim() !== '' && inputCell.val() !== null){
            //查询小区信息
            $("#queryKpiForm").submit();
        }
    });

    $("#mrDataType").on("change",function () {
        var inputCell=$("#inputCell");
        if(inputCell.val().trim() !== '' && inputCell.val() !== null){
            //查询小区信息
            $("#queryKpiForm").submit();
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
    var cellNameTitle = data['cellName']===null? '':"("+data['cellName']+")";
    $("#tab1Title").html(chartTitle+ '测量信息');
    $("#tab1CellName").html(cellName);

    var xAxisVal =[];
    var yAxisVal =[];
    var pieLegendVal=[];
    var pieVal =[];
    if(chartTitle==='RSRP'){
        xAxisVal =['小于-110','小于-95大于等于-110','小于-80大于等于-95','大于等于-80'];
        pieLegendVal=['RSRP小于-110','RSRP小于-95大于等于-110','RSRP小于-80大于等于-95','RSRP大于等于-80'];
        yAxisVal =[data['xbelowNegative110'],data['xbetweenNegative110And95'],data['xbetweenNegative95And80'],data['xonNegative80']];
        pieVal =[
            {value: data['xbelowNegative110'],name: 'RSRP小于-110'},
            {value: data['xbetweenNegative110And95'],name: 'RSRP小于-95大于等于-110'},
            {value: data['xbetweenNegative95And80'], name: 'RSRP小于-80大于等于-95'},
            {value: data['xonNegative80'],name: 'RSRP大于等于-80'}
        ];

        mrrInfoTab.append("<tbody><tr><td colspan='2' style='text-align: center'>"+chartTitle+"测量信息"+"</td></tr>"
            +"<tr><td class='menuTd'>CELL NAME</td><td>"+cellName+"</td></tr>"
            +"<tr><td class='menuTd'>X < -110</td><td>"+data['xbelowNegative110']+"</td></tr>"
            +"<tr><td class='menuTd'>-110 <= X < -95</td><td>"+data['xbetweenNegative110And95']+"</td></tr>"
            +"<tr><td class='menuTd'>-95 <= X < -80</td><td>"+data['xbetweenNegative95And80']+"</td></tr>"
            +"<tr><td class='menuTd'>X >= -80</td><td>"+data['xonNegative80']+"</td></tr></tbody>"

        );
    }else if(chartTitle ==='RSRQ'){
        xAxisVal =['小于8','小于15大于等于8','大于等于15'];
        pieLegendVal=['RSRQ小于8','RSRQ小于15大于等于8','RSRQ大于等于15'];
        yAxisVal =[data['xbelow8'],data['xbetween8And15'],data['xon15']];
        pieVal =[
            {value: data['xbelow8'],name: 'RSRQ小于8'},
            {value: data['xbetween8And15'], name: 'RSRQ小于15大于等于8'},
            {value: data['xon15'],name: 'RSRQ大于等于15'}
        ];
        mrrInfoTab.append("<tbody><tr><td colspan='2' style='text-align: center;'>"+chartTitle+"测量信息"+"</td></tr>"
            +"<tr><td class='menuTd'>CELL NAME</td><td>"+cellName+"</td></tr>"
            +"<tr><td class='menuTd'>X < 8</td><td>"+data['xbelow8']+"</td></tr>"
            +"<tr><td class='menuTd'>8 <= X <15</td><td>"+data['xbetween8And15']+"</td></tr>"
            +"<tr><td class='menuTd'>X >= 15</td><td>"+data['xon15']+"</td></tr></tbody>"
        );
    }else{
        xAxisVal = ['小于-110','小于-95大于等于-110','小于-85大于等于-95','大于等于-85'];
        pieLegendVal=['覆盖率小于-110','覆盖率小于-95大于等于-110','覆盖率小于-85大于等于-95','覆盖率大于等于-85'];
        yAxisVal =[data['xbelowNegative110'],data['xbetweenNegative110And95'],data['xbetweenNegative95And85'],data['xonNegative85']];
        pieVal =[
            {value: data['xbelowNegative110'],name:'覆盖率小于-110'},
            {value: data['xbetweenNegative110And95'],name:'覆盖率小于-95大于等于-110'},
            {value: data['xbetweenNegative95And85'],name: '覆盖率小于-85大于等于-95'},
            {value:data['xonNegative85'], name: '覆盖率大于等于-85'}
        ];

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
            subtext:cellId+  cellNameTitle+'信号接收功能'+ chartTitle+'分布图 采样点个数('+ data['sampleNum']+')个',
            subtextStyle:{
                color: '#6C6FFD'
            }
        },
        legend: {
            data:[]
        },
        xAxis: {
            name: chartTitle,
            type : 'category',
            data: xAxisVal,
            axisTick: {
                alignWithLabel: true
            }
        },
        yAxis: {
            name : '采样点个数',
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

    var pieOption={
        title : {
            text:chartTitle + '指标',
            x: 'center',
            subtext:cellId+  cellNameTitle+'信号接收功能'+ chartTitle+'分布图 采样点个数('+ data['sampleNum']+')个',
            subtextStyle:{
                color: '#6C6FFD'
            }
        },
        tooltip : {
            trigger: 'item',
            formatter: '{a} <br/> {b} : <br/>{c}'
        },
        legend: {
            orient: 'vertical',
            left: 'left',
            data: pieLegendVal
        },
        series : [{
            name: chartTitle +'指标',
            type: 'pie',
            radius : '55%',
            center: ['50%', '60%'],
            data: pieVal,
            itemStyle: {
                normal: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)',
                    label: {
                        show: true,
                        position: 'outer',
                        formatter:function(params){
                                return params.name +" ("+parseFloat(params.value/data['sampleNum']*100).toFixed(2) +"%)";
                        }
                    }
                }
            }
        }]
    };
    // 使用刚指定的配置项和数据显示图表。
    if($("#mrDisMode").val()==='柱状图'){
        myChart.clear();
        myChart.setOption(option);
    }else{
        myChart.clear();
        myChart.setOption(pieOption);
    }
}

function showInfoInAndOut(div, info) {
    var divSet = $("#" + div);
    divSet.html(info);
    divSet.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}
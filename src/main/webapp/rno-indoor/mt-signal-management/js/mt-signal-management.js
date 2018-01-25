$(function () {

    /**
     * 执行 laydate 实例
     */
    laydate.render({elem: '#beginDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endDate', value: new Date()});

    /**
     * 初始化区域联动
     */
    initAreaSelectors({ selectors: ["province", "city"] });

    /**
     * 验证MT查询条件
     */
    $("#queryBtn").click(function () {
        var reg = /^[0-9]+.?[0-9]*$/;
        var cityId = $("#city").val();
        var beginTestDate = $("#beginDate").val();
        var endTestDate = $("#endDate").val();
        if (cityId.trim() == '') {
            showInfoInAndOut("info", "城市信息为空");
            return false;
        }
        if (beginTestDate.length > 10 && beginTestDate.trim() !== '') {
            showInfoInAndOut("info", "开始日期值输入过长");
            return false;
        }
        if (endTestDate.length > 10 && endTestDate.trim() !== '') {
            showInfoInAndOut("info", "结束日期值输入过长");
            return false;
        }
        $(".loading").show();
    });

    /**
     * AJAX 提交移动终端查询条件表单
     */
    $("#conditionForm").ajaxForm({
        url: "../../api/mt-signal-mea-data/mt-signal-query",
        success: showMtSignalResult,
        error:function(data){
            $(".loading").hide();
            showInfoInAndOut('info', '后台报错，请检查！');
        }
    });

});

/**
 * 显示MT查询结果
 */
function showMtSignalResult(data) {
    $(".loading").css("display", "none");
    if (data == '') {
        showInfoInAndOut('info', '没有符合条件的移动终端信号数据');
    }

    $('#queryResultTab').css("line-height", "12px");
    $('#queryResultTab').DataTable({
        "data": data,
        "columns": [
            { "data": "cityName" },
            { "data": "buildingName" },
            { "data": "floorName" },
            { "data": "dmTopic" },
            { "data": "longitude" },
            { "data": "latitude" },
            { "data": "signal" },
            { "data": "planeX" },
            { "data": "planeY" },
            { "data": "meaDate" },
            { "data": "signalType" },
            { "data": "deviceId" }
        ],
        "columnDefs": [{

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

/**
 *渐入渐出效果
 */
function showInfoInAndOut(div, info) {
    var divSet = $("#" + div);
    divSet.html(info);
    divSet.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

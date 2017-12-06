$(function () {

    // 执行 laydate 实例 
    laydate.render({elem: '#begUploadDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endUploadDate', value: new Date()});
    laydate.render({elem: '#fileDate', value: new Date()});
    laydate.render({elem: '#beginTestDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endTestDate', value: new Date()});

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
        var a = 2781;
        $('#queryResultTab').css("line-height", "12px");
        $('#queryResultTab').DataTable( {
            "ajax": "data/gsm-mrr-data-import.json",
            "columns": [
                { "data": "area_name" },
                { "data": "uploadTime" },
                { "data": "fileName" },
                { "data": "fileSize" },
                { "data": "launchTime" },
                { "data": "completeTime" },
                { "data": "account" },
                { "data": "fileStatus" , "render": function (data, type, row) {
                    return '<a onclick="showDetail('+a+')">' + data + '</a>';
                }}
            ],
            // "lengthChange": false,
            // "ordering": false,
            // "searching": false,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        } );
    });

    $("#queryMrrBtn").click(function () {
        var dataMap = {
            'areaId':$("#city-id").val(),
            'factory':$("#SearchFactory").val(),
            'bsc':$("#bsc").val(),
            'beginTestDate':new Date($("#beginTestDate").val()),
            'endTestDate':new Date($("#endTestDate").val())
        };
        $('#queryMrrResultTab').css("line-height", "12px");
        $.ajax({
            url: '/api/gsm-mrr-data-query',
            dataType: 'json',
            data: dataMap,
            type: 'post',
            success:function(data){
                $("#queryMrrResultTab").DataTable({
                    "data": data,
                    "columns": [
                        {"data": "area.name"},
                        { "data": "meaDate", "render": function (data) {
                            return (new Date(data)).Format("yyyy-mm-dd");
                        }},
                        { "data": "bsc" },
                        { "data": "fileName", "render": function (data, type, row) {
                            return '<a onclick="showDetail('+row["id"]+')">' + data + '</a>';
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
    })


});
Date.prototype.Format = function(fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1,
        //月份
        "d+": this.getDate(),
        //日
        "h+": this.getHours(),
        //小时
        "m+": this.getMinutes(),
        //分
        "s+": this.getSeconds(),
        //秒
        "q+": Math.floor((this.getMonth() + 3) / 3),
        //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) {
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        }
    }
    return fmt;
}

function showDetail(jobId) {
    alert("jobId为："+jobId)
    /*$("#listinfoDiv").css("display","none")
    $("#reportDiv").css("display","block");*/
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

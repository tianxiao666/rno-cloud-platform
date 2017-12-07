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
        var dataMap = {
            'status':$("#importStatus").val(),
            'areaId':$("#city-menu").val(),
            'beginDate':new Date($("#begUploadDate").val()),
            'endDate':new Date($("#endUploadDate").val())
        };
        $('#queryResultTab').css("line-height", "12px");
        $.ajax({
            url: '/api/gsm-mrr-data/mrr-import-query',
            dataType: 'json',
            data: dataMap,
            type: 'post',
            success:function(data){
                $("#queryResultTab").DataTable({
                    "data": data,
                    "columns": [
                        {"data": "area.name"},
                        { "data": "createdDate", "render": function (data) {
                            return (new Date(data)).Format("yyyy-MM-dd");
                        }},
                        { "data": "originFile.filename" },
                        { "data": "originFile.fileSize", "render": function (data) {
                            return conver(data);
                        }},
                        { "data": "startTime", "render": function (data) {
                            return (new Date(data)).Format("yyyy-MM-dd");
                        }},
                        { "data": "completeTime", "render": function (data) {
                            return (new Date(data)).Format("yyyy-MM-dd");
                        }},
                        { "data": "createdUser" },
                        { "data": "status" }
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
            url: '/api/gsm-mrr-data/gsm-mrr-data-query',
            dataType: 'json',
            data: dataMap,
            type: 'post',
            success:function(data){
                $("#queryMrrResultTab").DataTable({
                    "data": data,
                    "columns": [
                        {"data": "area.name"},
                        { "data": "meaDate", "render": function (data) {
                            return (new Date(data)).Format("yyyy-MM-dd");
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

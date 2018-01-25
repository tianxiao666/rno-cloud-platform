$(function () {

    /**
     * 验证查询条件
     */
    $("#queryBtn").click(function () {
        var reg = /^[0-9]+.?[0-9]*$/;
        $(".loading").show();
        $("#conditionForm").submit();
    });

    /**
     * AJAX 提交理想AP查询条件表单
     */
    $("#conditionForm").ajaxForm({
        url: "../../api/cb-buliding-data/cb-building-query",
        success: showCbBuildingResult,
        error:function(data){
            $(".loading").hide();
            showInfoInAndOut('info', '后台报错，请检查！');
        }
    });

    /**
     * 初始化查询页面
     */
    $("#queryBtn").click();
});

function initCbBuilding() {
    $("#conditionForm").submit();
}
/**
 *显示建筑物场所查询结果
 */
function showCbBuildingResult(data) {
    $(".loading").css("display", "none");
    if (data == '') {
        showInfoInAndOut('info', '没有符合条件的建筑物场所数据');
    }

    $('#queryResultTab').css("line-height", "12px");
    $('#queryResultTab').DataTable({
        "data": data,
        "columns": [
            {"data": null},
            { "data": "buildingName" },
            { "data": "buildType" },
            { "data": "totalFloor" },
            { "data": "phone" },
            { "data": "prov" },
            { "data": "city" },
            { "data": "status" },
            {"data": null}
        ],
        "columnDefs": [{
            "render": function(data, type, row) {
                var id = row['id'];
                return "<input type=\"checkbox\" value='" + id + "' name=\"checkbox[]\">";
            },
            "targets": 0,
            "data": null,
            "orderable": false
        },{
            "render": function(data, type, row) {
                var buildType = row['buildType'];
                var val = "";
                if(buildType=='MALL_'){
                    val = "大型商场";
                }else if(buildType=='OFFIC'){
                    val = "写字楼";
                }else if(buildType=='LARGE'){
                    val = "大型场馆";
                }else if(buildType=='TRAFF'){
                    val = "交通枢纽";
                }
                return val;
            },
            "targets": 2,
            "data": null
        },{
            "render": function(data, type, row) {
                var status = row['status'];
                var val = "";
                if(status=='A'){
                    val = "<img src=\"images/lamp_on.png\"> 正常";
                }else if(status=='E'){
                    val = "编辑中";
                }else if(status=='X'){
                    val = "<img src=\"images/lamp_off.png\"> 失效";
                }
                return val;
            },
            "targets": 7,
            "data": null
        },{
            "render": function(data, type, row) {
                var id = row['id'];
                return "<a href=\"edit-place.html?buildingId="+id+"\" onclick=\"showDetail('" + id + "')\"> <img  src=\"images/s_edit.gif\"> 编辑</a> &nbsp;&nbsp;&nbsp;"
                        +"<a onclick=\"deleteBuilding('" + id + "');\"> 删除</a> &nbsp;&nbsp;&nbsp;"
            },
            "targets": -1,
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

/**
 *渐入渐出效果
 */
function showInfoInAndOut(div, info) {
    var divSet = $("#" + div);
    divSet.html(info);
    divSet.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

function showDetail(id){
    var url = "../../api/cb-buliding-data/cb-building-edit?buildingId="+id;
    // window.location.href=url;
    location.href = "../../api/cb-buliding-data/cb-building-edit?buildingId="+id;
}

/**
 *删除建筑物
 */
function deleteBuilding(id){

    if(confirm("确定要删除吗？")){
        // location.href = "../../api/cb-buliding-data/cb-building-delete?buildingId="+id;
        $.ajax({
            url: "../../api/cb-buliding-data/cb-building-delete",
            data:{
                'buildingId':id,
                _method:'DELETE'
            } ,
            type: 'POST',
            dataType: "json",
            async: false,
            success: function (data) {
                // console.log(data['area1']['id']+"    data==========="+data['area']['id']);
                showInfoInAndOut('info', '删除成功！');
                // window.location.reload();
                $("#queryBtn").click();
            },
            error:function (msg) {
                showInfoInAndOut('info', '删除失败！');
            }
        });
    }
}

function doSubmit(){
    $("#FORM_BUILDING").submit();
}

function checkboxAll(checkNode){//全选按钮触发事件
    var checkboxlist = document.getElementsByName("checkbox[]");
    for (var i = 0; i < checkboxlist.length; i++) {
        checkboxlist[i].checked = checkNode.checked;
    }
}

/**
 *
 * 批量修改楼层状态
 */
function submitChangeStatus(){
    var status = document.getElementById("changestatus").value;
    var checkboxlist = document.getElementsByName("checkbox[]");
    var statuslist = document.getElementsByName("status");
    if (status == '') {
        alert("请选择状态!");
        return false;
    }
    var building_id_list = '';
    for (var i = 0; i < checkboxlist.length; i++) {
        if (checkboxlist[i].checked) {
            building_id_list += checkboxlist[i].value + ",";
        }
    }
    if (building_id_list.length == 0) {
        alert("请选择场所!");
        return false;
    }
    // console.log(building_id_list.substr(0,building_id_list.length-1)+"---"+status);
    $.ajax({
        url: "../../api/cb-buliding-data/cb-building-status-update",
        data: {
            "buildingIds": building_id_list.substr(0,building_id_list.length-1),
            "status": status
        },
        type: "post",
        success: function(msg){
            showInfoInAndOut('info', '状态修改成功！');
            $("#queryBtn").click();
        }
    });
}
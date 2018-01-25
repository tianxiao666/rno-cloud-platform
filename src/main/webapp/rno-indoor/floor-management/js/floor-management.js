$(function () {

    /**
     * 拖动窗口
     */
    $("#editFloorDiv").draggable();
    $("#addFloorDiv").draggable();

    /**
     * 初始化场所信息
     */
    initBuildingSelectors({ selectors: ["building"],tip:["buildingNameTip"]},"queryBtn");

    /**
     * 场所改变提示
     */
    $("#building").change(function () {
        $("#buildingNameTip").text($("#building").find("option:selected").text());
    });

    /**
     * 查询事件
     */
    $("#queryBtn").click(function () {
        $(".loading").show();
        $("#conditionForm").submit();
    });

    /**
     * AJAX 提交楼层查询条件表单
     */
    $("#conditionForm").ajaxForm({
        url: "../../api/cb-floor-data/cb-floor-query",
        success: showCbFloorResult,
        error:function(data){
            $(".loading").hide();
            showInfoInAndOut('info', '后台报错，请检查！');
        }
    });

    /**
     * 添加按钮
     */
    $("#addFloorBtn").click(function () {
        if (checkAddForm()){
            $("#conditionAddForm").submit();
        }
    });

    /**
     * AJAX 提交添加楼层表单
     */
    $("#conditionAddForm").ajaxForm({
        url: "../../api/cb-floor-data/cb-floor-save",
        success: function (data) {
            // console.log(data);
            showInfoInAndOut('info', '保存成功！');
        },
        error:function(data){
            $(".loading").hide();
            showInfoInAndOut('info', '后台报错，请检查！');
        }
    });

    /**
     * 更新按钮
     */
    $("#updateBtn").click(function () {
        if (checkModForm()){
            $("#conditionModForm").submit();
        }
    });

    /**
     * AJAX 提交更改楼层表单
     */
    $("#conditionModForm").ajaxForm({
        url: "../../api/cb-floor-data/cb-floor-update",
        success: function (data) {
            // console.log(data);
            showInfoInAndOut('info', '保存成功！');
        },
        error:function(data){
            $(".loading").hide();
            showInfoInAndOut('info', '后台报错，请检查！');
        }
    });

});

/**
 * 初始化场所信息
 */
function initCbBuildings(selectors) {
    $.ajax({
        url: "../../api/cb-buliding-data/cb-building-query-all",
        data: {},
        type: "post",
        success: function(data){
            // showInfoInAndOut('info', '状态修改成功！');
            var areaHtml = [];
            $.each(data, function (index) {
                var CBB = data[index];
                $("#" + selectors).append("<option value='" + CBB.buildingId + "'>"+CBB.buildingName+"</option>");
            });
            //完成场所初始化后触发查询楼层事件
            $("#queryBtn").click();
        }
    });
}

/**
 *显示楼层查询结果
 */
function showCbFloorResult(data) {
    $(".loading").css("display", "none");
    if (data == '') {
        showInfoInAndOut('info', '没有符合条件的建筑物场所数据');
    }

    $('#queryResultTab').css("line-height", "12px");
    $('#queryResultTab').DataTable({
        "data": data,
        "columns": [
            {"data": null},
            { "data": "floorName" },
            { "data": "physicalFloor" },
            { "data": "floorType" },
            { "data": "floorNote" },
            { "data": "status" },
            {"data": null}
        ],
        "columnDefs": [{
            "render": function(data, type, row) {
                var id = row['floorId'];
                return "<input type=\"checkbox\" value='" + id + "' name=\"checkbox[]\">";
            },
            "targets": 0,
            "data": null,
            "orderable": false
        },{
            "render": function(data, type, row) {
                var floorType = row['floorType'];
                var val = "";
                if(floorType=='LOOBY'){
                    val = "大堂";
                }else if(floorType=='FUEST'){
                    val = "客房";
                }else if(floorType=='EFOOD'){
                    val = "餐饮";
                }
                return val;
            },
            "targets": 3,
            "data": null
        },{
            "render": function(data, type, row) {
                var status = row['status'];
                var val = "";
                if(status=='A'){
                    val = "<img src=\"images/lamp_on.png\"> 正常";
                }else if(status=='X'){
                    val = "<img src=\"images/lamp_off.png\"> 失效";
                }
                return val;
            },
            "targets": 5,
            "data": null
        },{
            "render": function(data, type, row) {
                var id = row['floorId'];
                return "<a onclick=\"Floor_change("+id+");\"> <img  src=\"images/s_edit.gif\"> 编辑</a> &nbsp;&nbsp;&nbsp;"
                    +"<a onclick=\"deleteFloor('" + id + "');\"> 删除</a> &nbsp;&nbsp;&nbsp;"
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

function setStyle(x){
    document.getElementById(x).style.color = "red"
}

//添加Floor
function AddFloor(){

    $("#addFloorDialog #buildingName").text($("#building").find('option:selected').text());
    $("#addFloorDialog #buildingId").val($("#building").find('option:selected').val());
    $("#addFloorDialog").css({"display": "block", "opacity": "1"});
    $("#addFloorDiv").animate({"marginTop": "-128px", "opacity": "1"}, 300);
}


//修改Floor
function Floor_change(floorId){
    $.ajax({
        url: "../../api/cb-floor-data/cb-floor-edit",
        data: {
            "floorId": floorId,
        },
        type: "post",
        success: function(data){
            var msg = "";
            $("#editFloorDialog #floorId").val(data['floorId']);
            $("#editFloorDialog #buildingId").val($("#building").find('option:selected').val());
            $("#editFloorDialog #buildingName").text($("#building").find('option:selected').text());
            $("#editFloorDialog #floorName").val(data['floorName']);
            $('#editFloorDialog #floorType option[value='+data['floorType']+']').attr('selected', 'selected');
            $("#editFloorDialog #physicalFloor").val(data['physicalFloor']);
            $('#editFloorDialog #basement option[value='+data['basement']+']').attr('selected', 'selected');
            $("#editFloorDialog #floorNote").text(data['floorNote']);
            $("#editFloorDialog").css({"display": "block", "opacity": "1"});
            $("#editFloorDiv").animate({"marginTop": "-128px", "opacity": "1"}, 300);
        }
    });

}

/**
 *全选按钮触发事件
 */
function checkboxAll(checkNode){
    var checkboxlist = document.getElementsByName("checkbox[]");
    for (var i = 0; i < checkboxlist.length; i++) {
        checkboxlist[i].checked = checkNode.checked;
    }
}

/**
 *批量修改楼层状态
 */
function submitChangeStatus(BUILDING_NAME, BUILDING_ID, NAME){
    var status = document.getElementById("changestatus").value;
    var checkboxlist = document.getElementsByName("checkbox[]");
    var statuslist = document.getElementsByName("status");
    if (status == '') {
        alert("请选择状态!");
        return false;
    }
    var floor_id_list = '';
    for (var i = 0; i < checkboxlist.length; i++) {
        if (checkboxlist[i].checked) {
            floor_id_list += checkboxlist[i].value + ",";
        }
    }
    if (floor_id_list.length == 0) {
        alert("请选择楼层!");
        return false;
    }

    $.ajax({
        url: "../../api/cb-floor-data/cb-floor-status-update",
        data: {
            "buildingIds": floor_id_list.substr(0,floor_id_list.length-1),
            "status": status
        },
        type: "post",
        success: function(msg){
            showInfoInAndOut('info', '状态修改成功！');
            $("#queryBtn").click();
        }
    });

}

/**
 * 刷新当前页面
 */
function windowRefresh(){
    $.popup({close:true});
    window.location.reload();
}

/**
 *修改信息内容检测是否合法
 */
function checkModForm(){
    var re_kong = /^\s*$/;
    var re_FLOOR_NAME = /^.{1,10}$/;
    if ($('#editFloorDialog #floorName').val() == '' || !re_FLOOR_NAME.test($('#editFloorDialog #floorName').val())||re_kong.test($('#editFloorDialog #floorName').val())) {
        alert('请填写楼层名称(10个字符以内)！');
        $('#editFloorDialog #floorName').focus();
        return false;
    }
    if ($('#editFloorDialog #basement').val() == '') {
        alert('请选择楼层为地上或地下！');
        $('#editFloorDialog #basement').focus();
        return false;
    }
    return true;
}

/**
 *添加信息内容检测是否合法
 */
function checkAddForm(){
    var re_kong = /^\s*$/;
    var re_FLOOR_NAME = /^.{1,10}$/;
    if ($('#addFloorDialog #floorName').val() == '' || !re_FLOOR_NAME.test($('#addFloorDialog #floorName').val())||re_kong.test($('#addFloorDialog #floorName').val())) {
        alert('请填写楼层名称(10个字符以内)！');
        $('#addFloorDialog #floorName').focus();
        return false;
    }
    if ($('#addFloorDialog #basement').val() == '') {
        alert('请选择楼层为地上或地下！');
        $('#addFloorDialog #basement').focus();
        return false;
    }
    return true;
}

/**
 *删除楼层
 */
function deleteFloor(id){
    if(confirm("确定要删除吗？")){
        $.ajax({
            url: "../../api/cb-floor-data/cb-floor-delete",
            data:{
                'floorId':id,
                _method:'DELETE'
            } ,
            type: 'post',
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

function editDialogClose() {
    $("#editFloorDiv").animate({"marginTop": "-188px", "opacity": "0"}, 300, function () {
        $("#editFloorDiv").css({"marginTop": "00px"});
        $("#editFloorDialog").animate({"opacity": "0"}, 300, function () {
            $("#editFloorDialog").hide();
        });
    });
}

function addDialogClose() {
    $("#addFloorDiv").animate({"marginTop": "-188px", "opacity": "0"}, 300, function () {
        $("#addFloorDiv").css({"marginTop": "00px"});
        $("#addFloorDialog").animate({"opacity": "0"}, 300, function () {
            $("#addFloorDialog").hide();
        });
    });
}

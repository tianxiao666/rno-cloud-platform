$(function () {

    /**
     * 初始化场所信息
     */
    initBuildingSelectors({ selectors: ["buildingId","floorId"],tip:["buildingNameTip"]},"queryBtn");

    /**
     * 场所改变提示
     */
    $("#buildingId").change(function () {
        $("#buildingNameTip").text($("#buildingId").find("option:selected").text());
    });

    /**
     * 查询事件
     */
    $("#queryBtn").click(function () {
        $(".loading").show();
        $("#conditionForm").submit();
    });

    /**
     * AJAX 提交楼层平面图查询条件表单
     */
    $("#conditionForm").ajaxForm({
        url: "../../api/dm-draw-map-data/dm-draw-map-query",
        success: showDrawMapResult,
        error:function(data){
            $(".loading").hide();
            showInfoInAndOut('info', '后台报错，请检查！');
        }
    });


});

/**
 * 跳转至绘图页面
 */
function toDrawing() {
    var buildingId = $("#buildingId").find("option:selected").val();
    // window.location.href="edit-drawing-plan.html?buildingId="+buildingId;
    // window.location.href="svg.html?buildingId="+buildingId;
    // window.location.href="js/svg-edit-2.6/edit-drawing-plan.html?buildingId="+buildingId;
    // window.location.href="js/editor/svg-editor.html?buildingId="+buildingId;
    window.location.href="js/svg-2.6/svg-editor.html?buildingId="+buildingId;
}

/**
 * 跳转至编辑绘图页面
 */
function toEditDrawing(buildingId,floorId,drawMapId) {
    // window.location.href="edit-drawing-plan.html?buildingId="+buildingId;
    // window.location.href="svg.html?buildingId="+buildingId;
    // window.location.href="js/svg-edit-2.6/edit-drawing-plan.html?buildingId="+buildingId;
    // window.location.href="js/editor/svg-editor.html?buildingId="+buildingId;
    window.location.href="js/svg-2.6/svg-editor.html?buildingId="+buildingId+"&floorId="+floorId+"&drawMapId="+drawMapId+"&flag=true";
}

/**
 *显示楼层平面图查询结果
 */
function showDrawMapResult(data) {
    $(".loading").css("display", "none");
    if (data == '') {
        showInfoInAndOut('info', '没有符合条件的楼层平面图数据');
    }

    $('#queryResultTab').css("line-height", "12px");
    $('#queryResultTab').DataTable({
        "data": data,
        "columns": [
            {"data": null},
            { "data": "dmTopic" },
            { "data": null },
            { "data": "floorName" },
            { "data": "width" },
            { "data": "height" },
            { "data": "地面宽（米）" },
            { "data": "地面高（米）" },
            { "data": "比例尺" },
            { "data": "status" },
            {"data": "操作"}
        ],
        "columnDefs": [{
            "render": function(data, type, row) {
                var id = row['drawMapId'];
                return "<input type=\"checkbox\" value='" + id + "' name=\"checkbox[]\">";
            },
            "targets": 0,
            "data": null,
            "orderable": false
        },{
            "render": function(data, type, row) {
                var val = "";
                return val;
            },
            "targets": 2,
            "data": null
        },{
            "render": function(data, type, row) {
                var width = row['width'];
                var val = width+" 像素";
                return val;
            },
            "targets": 4,
            "data": null
        },{
            "render": function(data, type, row) {
                var height = row['height'];
                var val = height+" 像素";
                return val;
            },
            "targets": 5,
            "data": null
        },{
            "render": function(data, type, row) {
                var width = row['width'];
                var dwScale = row['dwScale'];
                var val = parseFloat(width*dwScale).toFixed(2);
                return val;
            },
            "targets": 6,
            "data": null
        },{
            "render": function(data, type, row) {
                var height = row['height'];
                var dwScale = row['dwScale'];
                var val = parseFloat(height*dwScale).toFixed(2);
                return val;
            },
            "targets": 7,
            "data": null
        },{
            "render": function(data, type, row) {
                var dwScale = row['dwScale'];
                var dwUnit = row['dwUnit'];
                var val = "1"+dwUnit+":"+parseFloat(dwScale)*100+"cm";
                return val;
            },
            "targets": 8,
            "data": null
        },{
            "render": function(data, type, row) {
                var status = row['status'];
                var val = "";
                if(status=='A'){
                    val = "<img src=\"images/lamp_on.png\"> 正常";
                }else if(status=='X'){
                    val = "<img src=\"images/lamp_off.png\"> 失效";
                }else if(status=='E'){
                    val = "<img src=\"images/lamp_off.png\"> 过期";
                }
                return val;
            },
            "targets": 9,
            "data": null
        },{
            "render": function(data, type, row) {
                var drawMapId = row['drawMapId'];
                var buildingId = row['buildingId'];
                var floorId = row['floorId'];
                var status = row['status'];
                var val = "";
                if(status=='X'){
                    val = "<a onclick=\"toEditDrawing("+buildingId+","+floorId+","+drawMapId+");\"> <img  src=\"images/s_edit.gif\"> 编辑</a> "
                        +"<a  onclick=\"ToStatus('" + drawMapId + "','A');\"> 启用</a> &nbsp;&nbsp;&nbsp;"
                }else{
                    val = "<a  onclick=\"toEditDrawing("+buildingId+","+floorId+","+drawMapId+");\"> <img  src=\"images/s_edit.gif\"> 编辑</a> "
                        +"<a  onclick=\"ToStatus('" + drawMapId + "','X');\"> 禁用</a> &nbsp;&nbsp;&nbsp;"
                }
                return val;
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


function doBuildingPlanegraphSearch(){
    $("#FORM_SEARCHBUILDINGPLANEGRAPH").submit();
}

function appStatus(FLOOR_ID, DRAW_MAP_ID, STATUS){
    $.ajax({
        type: "POST",
        url: "ea.php?r=BuildingFloorPlanegraphMgr/AjaxHasApp",
        data: "FLOOR_ID=" + FLOOR_ID,
        dataType: 'json',
        success: function(json){
            var DRAW_MAP_ID_APP = json.DRAW_MAP_ID;
            if (DRAW_MAP_ID_APP) {
                if
                (!window.confirm("该楼层存在应用中平面图，点击确定将会将应用中平面图更改状态为编缉，此平面图将被应用！")) {
                    DRAW_MAP_ID_APP = null;
                }
            }
            else {
                if
                (window.confirm("是否确定应用？应用后此平面图无法编缉，直到新的平面图被应用！")) {
                    DRAW_MAP_ID_APP = "";
                }
            }
            if (DRAW_MAP_ID_APP != null) {
                $.ajax({
                    type: "POST",
                    url:
                        "ea.php?r=BuildingFloorPlanegraphMgr/AjaxAppStatus",
                    data: "DRAW_MAP_ID_APP=" + DRAW_MAP_ID_APP +
                    "&DRAW_MAP_ID=" + DRAW_MAP_ID,
                    dataType: 'json',
                    success: function(jsonapp){
                        if (jsonapp.error) {
                            alert(jsonapp.error);
                        }
                        else {
                            alert("操作成功！");
                            window.location.reload();
                        }
                    }
                });
            }
        }
    });
}

/**
 * 单项状态改变
 */
function ToStatus(DRAW_MAP_ID, TOSTATUS){
    $.ajax({
        type: "POST",
        url: "../../api/dm-draw-map-data/floor-planegraph-status-update",
        data: {"drawMapIds":DRAW_MAP_ID,"status":TOSTATUS},
        dataType: 'json',
        success: function(json){
            if (json.error) {
                showInfoInAndOut('info', '操作失败！');
            }
            else {
                showInfoInAndOut('info', '操作成功！');
                // window.location.reload();
                $("#queryBtn").click();
            }
        }
    });
}

function checkboxAll(checkNode){//全选按钮触发事件
    var checkboxlist = document.getElementsByName("checkbox[]");
    for (var i = 0; i < checkboxlist.length; i++) {
        checkboxlist[i].checked = checkNode.checked;
    }
}

/**
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
    var draw_map_id_list = '';
    for (var i = 0; i < checkboxlist.length; i++) {
        if (checkboxlist[i].checked) {
            draw_map_id_list += checkboxlist[i].value + ",";
        }
    }
    if (draw_map_id_list.length == 0) {
        alert("请选择图案!");
        return false;
    }
    $.ajax({
        url: "../../api/dm-draw-map-data/floor-planegraph-status-update",
        data: {
            "drawMapIds": draw_map_id_list.substr(0,draw_map_id_list.length-1),
            "status": status
        },
        type: "post",
        success: function(msg){
            showInfoInAndOut('info', '操作成功！');
            $("#queryBtn").click();
        },
        error:function (error) {
            showInfoInAndOut('info', '操作失败！');
        }
    });
}

//查看svg原图
function lookBigMap(FLOOR_WIDTH , FLOOR_HEIGHT , svgPath ,
                    SVG_DW_SCALE){
    var width = FLOOR_WIDTH/SVG_DW_SCALE+ 38;
    if(width>1000){
        width=1000+ 38;
    }
    var height = FLOOR_HEIGHT/SVG_DW_SCALE + 38;
    if(height>500){
        height=500+ 38;
    }
    var top1 = (screen.availHeight - height) / 3;
    var left1 = (screen.availWidth - width) / 2;
    $.popup({
        type: 1,
        now: true,
        url: "ea.php?r=SvgMgr/lookBigMap&svgPath=" + svgPath +
        "&width=" + width + "&height=" + height,
        width: width ,
        height: height,
        top: top1,
        left: left1,
        title: "Svg原图"
    });
}
//关闭放大弹出窗口
function closeSvg(){
    $("#divPopup").hide();
}
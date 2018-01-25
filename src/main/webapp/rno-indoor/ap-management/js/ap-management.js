$(function () {

    /**
     * 拖动窗口
     */
    $("#editApDiv").draggable();
    $("#addApDiv").draggable();

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
     * AJAX 提交楼层查询条件表单
     */
    $("#conditionForm").ajaxForm({
        url: "../../api/ap-equipment-data/ap-equipment-query",
        success: showApEqutResult,
        error: function (data) {
            $(".loading").hide();
            showInfoInAndOut('info', '后台报错，请检查！');
        }
    });

    /**
     * 添加AP定位对话窗口中选择FLOOR_ID取相应的DRAW_MAP_ID
     */
    $("#addApDialog #floorId").change(function () {
        var floorId = $("#addApDialog #floorId").find('option:selected').val();
        getFloorMapByFloorId(floorId);
        $("#addApDialog #drawMapId").trigger("change");
        $("#addApDialog #layerId").trigger("change");
    });

    /**
     * 添加AP定位对话窗口中选择drawMapId取相应的Layer_ID
     */
    $("#addApDialog #drawMapId").change(function () {
        var drawMapId = $("#addApDialog #drawMapId").find('option:selected').val();
        getPlaneLayerByDrawMapId(drawMapId);
        $("#addApDialog #layerId").trigger("change");
    });

    /**
     * 添加AP定位对话窗口中选择layerId取相应的svgId
     */
    $("#addApDialog #layerId").change(function () {
        var layerId = $("#addApDialog #layerId").find('option:selected').val();
        getLayerElementByLayerId(layerId);
    });

    /**
     * 编辑AP定位对话窗口中选择FLOOR_ID取相应的DRAW_MAP_ID
     */
    $("#editApDialog #floorId").change(function () {
        var floorId = $("#editApDialog #floorId").find('option:selected').val();
        getFloorMapByFloorIdForEdit(floorId);
        $("#editApDialog #drawMapId").trigger("change");
        $("#editApDialog #layerId").trigger("change");
    });

    /**
     * 编辑AP定位对话窗口中选择drawMapId取相应的Layer_ID
     */
    $("#editApDialog #drawMapId").change(function () {
        var drawMapId = $("#editApDialog #drawMapId").find('option:selected').val();
        getPlaneLayerByDrawMapIdForEdit(drawMapId);
        $("#editApDialog #layerId").trigger("change");
    });

    /**
     * 编辑AP定位对话窗口中选择layerId取相应的svgId
     */
    $("#editApDialog #layerId").change(function () {
        var layerId = $("#editApDialog #layerId").find('option:selected').val();
        getLayerElementByLayerIdForEdit(layerId);
    });


    /**
     * 添加按钮
     */
    $("#addApBtn").click(function () {
        if (checkAddForm()) {
            $("#conditionAddForm").submit();
        }
    });

    /**
     * AJAX 提交添加AP定位信息表单
     */
    $("#conditionAddForm").ajaxForm({
        url: "../../api/ap-equipment-data/ap-equipment-save",
        success: function (data) {
            // console.log(data);
            showInfoInAndOut('info', '保存成功！');
        },
        error: function (data) {
            $(".loading").hide();
            showInfoInAndOut('info', '后台报错，请检查！');
        }
    });

    /**
     * 更新按钮
     */
    $("#updateBtn").click(function () {

        if (checkModForm()) {
            $("#conditionModForm").submit();
        }
    });

    /**
     * AJAX 提交更改AP表单
     */
    $("#conditionModForm").ajaxForm({
        url: "../../api/ap-equipment-data/ap-equipment-save",
        success: function (data) {
            // console.log(data);
            showInfoInAndOut('info', '保存成功！');
        },
        error: function (data) {
            $(".loading").hide();
            showInfoInAndOut('info', '后台报错，请检查！');
        }
    });

    /**
     * 当SVGID改变时填充图层元素ID值
     */
    $("#addApDialog #svgId").change(function () {
        var layerMementId = $("#addApDialog #svgId").find("option:selected").attr("layerelement");
        $("#addApDialog #elementId").val(layerMementId);
        // console.log("layerMementI="+layerMementId);
    });
});

function getFloorMapByFloorId(floorId) {

    $.ajax({
        url: "../../api/dm-draw-map-data/dm-drawmap-query-by-floorid",
        data: {
            "floorId": floorId,
        },
        type: "post",
        success: function (data) {
            $("#addApDialog #drawMapId").find('option').not(':first').remove();
            $.each(data, function (index) {
                var CBB = data[index];
                $("#addApDialog #drawMapId").append("<option value='" + CBB.drawMapId + "'>" + CBB.dmTopic + "</option>");
            });
        },
        error: function (error) {
            showInfoInAndOut('info', '获取楼层平面图失败！');
        }
    });
}

function getPlaneLayerByDrawMapId(drawMapId) {

    $.ajax({
        url: "../../api/dm-plane-Layer-data/dm-plane-layer-query-by-drawmapid",
        data: {
            "drawMapId": drawMapId,
        },
        type: "post",
        success: function (data) {
            $("#addApDialog #layerId").find('option').not(':first').remove();
            $.each(data, function (index) {
                var CBB = data[index];
                $("#addApDialog #layerId").append("<option value='" + CBB.layerId + "'>" + CBB.layerTopic + "</option>");
            });
        },
        error: function (error) {
            showInfoInAndOut('info', '获取图层失败！');
        }
    });
}

function getLayerElementByLayerId(layerId) {

    $.ajax({
        url: "../../api/dm-layer-element-data/dm-layer-element-query-by-layerid",
        data: {
            "layerId": layerId,
        },
        type: "post",
        success: function (data) {
            $("#addApDialog #svgId").find('option').not(':first').remove();
            $.each(data, function (index) {
                var CBB = data[index];
                $("#addApDialog #svgId").append("<option layerElement='" + CBB.elementId + "' value='" + CBB.svgId + "'>" + CBB.elementType + "(" + CBB.svgId + ")</option>");
            });
        },
        error: function (error) {
            showInfoInAndOut('info', '获取图层元素失败！');
        }
    });
}


function getFloorMapByFloorIdForEdit(floorId) {

    $.ajax({
        url: "../../api/dm-draw-map-data/dm-drawmap-query-by-floorid",
        data: {
            "floorId": floorId,
        },
        type: "post",
        success: function (data) {
            $("#editApDialog #drawMapId").find('option').not(':first').remove();
            $.each(data, function (index) {
                var CBB = data[index];
                $("#editApDialog #drawMapId").append("<option value='" + CBB.drawMapId + "'>" + CBB.dmTopic + "</option>");
            });
        },
        error: function (error) {
            showInfoInAndOut('info', '获取楼层平面图失败！');
        }
    });
}

function getPlaneLayerByDrawMapIdForEdit(drawMapId) {

    $.ajax({
        url: "../../api/dm-plane-Layer-data/dm-plane-layer-query-by-drawmapid",
        data: {
            "drawMapId": drawMapId,
        },
        type: "post",
        success: function (data) {
            $("#editApDialog #layerId").find('option').not(':first').remove();
            $.each(data, function (index) {
                var CBB = data[index];
                $("#editApDialog #layerId").append("<option value='" + CBB.layerId + "'>" + CBB.layerTopic + "</option>");
            });
        },
        error: function (error) {
            showInfoInAndOut('info', '获取图层失败！');
        }
    });
}

function getLayerElementByLayerIdForEdit(layerId) {

    $.ajax({
        url: "../../api/dm-layer-element-data/dm-layer-element-query-by-layerid",
        data: {
            "layerId": layerId,
        },
        type: "post",
        success: function (data) {
            $("#editApDialog #svgId").find('option').not(':first').remove();
            $.each(data, function (index) {
                var CBB = data[index];
                $("#editApDialog #svgId").append("<option layerElement='" + CBB.elementId + "' value='" + CBB.svgId + "'>" + CBB.elementType + "(" + CBB.svgId + ")</option>");
            });
        },
        error: function (error) {
            showInfoInAndOut('info', '获取图层元素失败！');
        }
    });
}

/**
 *显示AP定位设备查询结果
 */
function showApEqutResult(data) {
    $(".loading").css("display", "none");
    if (data == '') {
        showInfoInAndOut('info', '没有符合条件的AP定位设备数据');
    }

    $('#queryResultTab').css("line-height", "12px");
    $('#queryResultTab').DataTable({
        "data": data,
        "columns": [
            {"data": null},
            {"data": "equtSsid"},
            {"data": "equtType"},
            {"data": "floorName"},
            {"data": "macBssid"},
            {"data": "位置"},
            {"data": "status"},
            {"data": "操作"}
        ],
        "columnDefs": [{
            "render": function (data, type, row) {
                var id = row['apId'];
                return "<input id='checkbox_select' type=\"checkbox\" value='" + id + "' name=\"checkbox[]\">";
            },
            "targets": 0,
            "data": null,
            "orderable": false
        }, {
            "render": function (data, type, row) {
                var equtType = row['equtType'];
                var val = "";
                if (equtType == 'PBX') {
                    val = "交换机";
                } else if (equtType == 'AP') {
                    val = "AP";
                }
                return val;
            },
            "targets": 2,
            "data": null
        }, {
            "render": function (data, type, row) {
                var positionX = row['positionX'];
                var positionY = row['positionY'];
                var val = "X:" + positionX + "  " + "Y:" + positionY;
                return val;
            },
            "targets": 5,
            "data": null
        }, {
            "render": function (data, type, row) {
                var status = row['status'];
                var val = "";
                if (status == 'A') {
                    val = "<img src=\"images/lamp_on.png\"> 正常";
                } else if (status == 'X') {
                    val = "<img src=\"images/lamp_off.png\"> 失效";
                } else if (status == 'E') {
                    val = "<img src=\"images/lamp_off.png\"> 编辑中";
                }
                return val;
            },
            "targets": 6,
            "data": null
        }, {
            "render": function (data, type, row) {
                var id = row['apId'];
                return "<a onclick=\"Equt_change(" + id + ");\"> <img  src=\"images/s_edit.gif\"> 编辑</a> &nbsp;&nbsp;&nbsp;"
                    + "<a onclick=\"deleteAp('" + id + "');\"> 删除</a> &nbsp;&nbsp;&nbsp;"
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

/**
 *删除AP
 */
function deleteAp(id) {
    if (confirm("确定要删除吗？")) {
        $.ajax({
            url: "../../api/ap-equipment-data/ap-equipment-delete",
            data: {
                'apId': id,
                _method:'DELETE'
            },
            type: 'post',
            dataType: "json",
            async: false,
            success: function (data) {
                // console.log(data['area1']['id']+"    data==========="+data['area']['id']);
                showInfoInAndOut('info', '删除成功！');
                window.location.reload();
            },
            error: function (msg) {
                showInfoInAndOut('info', '删除失败！');
            }
        });
    }
}

/**
 * 添加场所 AP Equt
 */
function AddEqut(BUILDING_NAME, BUILDING_ID) {
    var buildingId = $("#buildingId").find('option:selected').val();
    $("#addApDialog #buildingName").text($("#buildingId").find('option:selected').text());
    $("#addApDialog #buildingId").val($("#buildingId").find('option:selected').val());
    $("#addApDialog").css({"display": "block", "opacity": "1"});
    $("#addApDiv").animate({"marginTop": "-128px", "opacity": "1"}, 300);
    // $("#addApDialog #floorId").find('option').not(':first').remove();
    renderFloors({selectors: ["", "addApDialog #floorId"]}, buildingId, null);
    getApSysDictInfoForAdd();
}

/**
 * ap数据字典 add窗口
 */
function getApSysDictInfoForAdd() {
    $.ajax({
        url: "../../api/sys-dict-data/get-ap-sys-dict-info",
        data: {},
        type: "get",
        success: function (data) {
            $("#addApDialog #factory").find('option').not(':first').remove();
            $("#addApDialog #brands").find('option').not(':first').remove();
            $("#addApDialog #equtType").find('option').not(':first').remove();
            $("#addApDialog #status").find('option').not(':first').remove();
            $.each(data, function (key, value) {
                if ("EQUT_TYPE" == key) {
                    $.each(value, function (k, v) {
                        $("#addApDialog #equtType").append("<option value='" + k + "'>" + v + "</option>");
                    });
                } else if ("EQUT_STATUS" == key) {
                    $.each(value, function (k, v) {
                        $("#addApDialog #status").append("<option value='" + k + "'>" + v + "</option>");
                    });
                } else if ("EQUT_FACTORY" == key) {
                    $.each(value, function (k, v) {
                        $("#addApDialog #factory").append("<option value='" + k + "'>" + v + "</option>");
                    });
                } else if ("EQUT_BRANDS" == key) {
                    $.each(value, function (k, v) {
                        $("#addApDialog #brands").append("<option value='" + k + "'>" + v + "</option>");
                    });
                }
                // $("#addApDialog #svgId").append("<option value='" + CBB.elementId + "'>"+CBB.elementType+"</option>");
            });
        },
        error: function (error) {
            showInfoInAndOut('info', '获取字典数据失败！');
        }
    });
}

/**
 * ap数据字典 update窗口
 */
function getApSysDictInfoForUpdate() {
    $.ajax({
        url: "../../api/sys-dict-data/get-ap-sys-dict-info",
        data: {},
        type: "get",
        success: function (data) {
            $("#editApDialog #factory").find('option').not(':first').remove();
            $("#editApDialog #brands").find('option').not(':first').remove();
            $("#editApDialog #equtType").find('option').not(':first').remove();
            $("#editApDialog #status").find('option').not(':first').remove();
            $.each(data, function (key, value) {
                if ("EQUT_TYPE" == key) {
                    $.each(value, function (k, v) {
                        $("#editApDialog #equtType").append("<option value='" + k + "'>" + v + "</option>");
                    });
                } else if ("EQUT_STATUS" == key) {
                    $.each(value, function (k, v) {
                        $("#editApDialog #status").append("<option value='" + k + "'>" + v + "</option>");
                    });
                } else if ("EQUT_FACTORY" == key) {
                    $.each(value, function (k, v) {
                        $("#editApDialog #factory").append("<option value='" + k + "'>" + v + "</option>");
                    });
                } else if ("EQUT_BRANDS" == key) {
                    $.each(value, function (k, v) {
                        $("#editApDialog #brands").append("<option value='" + k + "'>" + v + "</option>");
                    });
                }
                // $("#addApDialog #svgId").append("<option value='" + CBB.elementId + "'>"+CBB.elementType+"</option>");
            });
        },
        error: function (error) {
            showInfoInAndOut('info', '获取字典数据失败！');
        }
    });
}


/**
 * 添加楼层Equt
 */
function Add_Floor_Equt(BUILDING_NAME, BUILDING_ID, FLOOR_ID) {
    var width1 = 670;
    var height1 = 430;
    var top1 = (screen.availHeight - height1) / 4;
    var left1 = (screen.availWidth - width1) / 2;
    $.popup({
        type: 1,
        now: true,
        url: "ea.php?r=LocationInformation/AddEqut&BUILDING_NAME=" +
        BUILDING_NAME + "&BUILDING_ID=" + BUILDING_ID + "&FLOOR_ID=" + FLOOR_ID,
        width: width1,
        height: height1,
        top: top1,
        left: left1,
        title: "添加定位信息"
    });
}

/**
 * 修改场所Equt
 */
function Equt_change(apId) {
    $("#editApDialog").css({"display": "block", "opacity": "1"});
    $("#editApDiv").animate({"marginTop": "-128px", "opacity": "1"}, 300);

    //数据字典信息
    getApSysDictInfoForUpdate();

    //楼层信息
    var buildingId = $("#buildingId").find('option:selected').val();
    renderFloors({selectors: ["", "editApDialog #floorId"]}, buildingId, null);

    //数据表信息
    $.ajax({
        url: "../../api/ap-equipment-data/ap-equipment-edit",
        data: {
            "apId": apId,
        },
        type: "post",
        success: function (apData) {
            //{"apId":1,"layerId":"159","drawMapId":"1","buildingId":"2","floorId":"1","elementId":"9868","svgId":"C5FF35F2B7200002502766491A006E40","equtSsid":"fdfdfef","macBssid":"c4:ca:d9:59:93:30","equtType":"PBX","frequency":"11","channel":"2","factory":"HUA_S","brands":"BRAN2","equtModel":"fdfdf","positionX":"11","positionY":"22","note":"fdfdfddfdf","status":"A","cbFloor":{"floorId":1,"buildingId":2,"picId":"1","floorName":"A1栋19楼","physicalFloor":"19","basement":"Y","floorType":"LOOBY","floorNote":"广东怡创科技股份有限公司","status":"A"}}
            var buildingId = apData['buildingId'];
            $('#editApDialog #apId').val(apId);
            $('#editApDialog #buildingId').val(buildingId);

            var floorId = apData['floorId'];
            var drawMapId = apData['drawMapId'];
            var elementId = apData['elementId'];
            var layerId = apData['layerId'];
            var svgId = apData['svgId'];
            $('#editApDialog #floorId option[value=' + floorId + ']').attr('selected', 'selected');

            var equtSsid = apData['equtSsid'];
            var macBssid = apData['macBssid'];
            $('#editApDialog #equtSsid').val(equtSsid);
            $('#editApDialog #macBssid').val(macBssid);

            var frequency = apData['frequency'];
            var channel = apData['channel'];
            var equtModel = apData['equtModel'];
            var positionX = apData['positionX'];
            var positionY = apData['positionY'];
            var note = apData['note'];
            $('#editApDialog #frequency').val(frequency);
            $('#editApDialog #channel').val(channel);
            $('#editApDialog #equtModel').val(equtModel);
            $('#editApDialog #positionX').val(positionX);
            $('#editApDialog #positionY').val(positionY);
            $('#editApDialog #note').val(note);


            var factory = apData['factory'];
            var brands = apData['brands'];
            var equtType = apData['equtType'];
            var status = apData['status'];
            $('#editApDialog #factory option[value=' + factory + ']').attr('selected', 'selected');
            $('#editApDialog #brands option[value=' + brands + ']').attr('selected', 'selected');
            $('#editApDialog #equtType option[value=' + equtType + ']').attr('selected', 'selected');
            $('#editApDialog #status option[value=' + status + ']').attr('selected', 'selected');

            /*         通过楼层ID选择改变 楼层平面图数据信息         */
            $.ajax({
                url: "../../api/dm-draw-map-data/dm-drawmap-query-by-floorid",
                data: {
                    "floorId": floorId,
                },
                type: "post",
                success: function (data) {
                    $("#editApDialog #drawMapId").find('option').not(':first').remove();
                    $.each(data, function (index) {
                        var CBB = data[index];
                        $("#editApDialog #drawMapId").append("<option value='" + CBB.drawMapId + "'>" + CBB.dmTopic + "</option>");
                    });
                    $('#editApDialog #drawMapId option[value=' + drawMapId + ']').attr('selected', 'selected');
                    /*         通过楼层平面图ID选择改变 平面图层数据信息         */
                    $.ajax({
                        url: "../../api/dm-plane-Layer-data/dm-plane-layer-query-by-drawmapid",
                        data: {
                            "drawMapId": drawMapId,
                        },
                        type: "post",
                        success: function (planeLayerData) {
                            $("#editApDialog #layerId").find('option').not(':first').remove();
                            $.each(planeLayerData, function (index) {
                                var CBB = planeLayerData[index];
                                $("#editApDialog #layerId").append("<option value='" + CBB.layerId + "'>" + CBB.layerTopic + "</option>");
                            });
                            $('#editApDialog #layerId option[value=' + layerId + ']').attr('selected', 'selected');
                            /*         通过平面图ID选择改变 平面图层元素数据信息         */
                            $.ajax({
                                url: "../../api/dm-layer-element-data/dm-layer-element-query-by-layerid",
                                data: {
                                    "layerId": layerId,
                                },
                                type: "post",
                                success: function (layerElementData) {
                                    $("#editApDialog #svgId").find('option').not(':first').remove();
                                    $.each(layerElementData, function (index) {
                                        var CBB = layerElementData[index];
                                        $("#editApDialog #svgId").append("<option layerElement='" + CBB.elementId + "' value='" + CBB.svgId + "'>" + CBB.elementType + "(" + CBB.svgId + ")</option>");
                                    });
                                    $('#editApDialog #svgId option[value=' + svgId + ']').attr('selected', 'selected');
                                },
                                error: function (error) {
                                    showInfoInAndOut('info', '获取图层元素失败！');
                                }
                            });

                        },
                        error: function (error) {
                            showInfoInAndOut('info', '获取图层失败！');
                        }
                    });

                },
                error: function (error) {
                    showInfoInAndOut('info', '获取楼层平面图失败！');
                }
            });
        },
        error: function (error) {
            showInfoInAndOut('info', '操作失败！');
        }
    });

}

/**
 *修改楼层Equt
 */
function Equt_change_sel(BUILDING_NAME, BUILDING_ID,
                         AP_ID, FLOOR_ID) {
    var width1 = 670;
    var height1 = 430;
    var top1 = (screen.availHeight - height1) / 4;
    var left1 = (screen.availWidth - width1) / 2;
    $.popup({
        type: 1,
        now: true,
        url: "ea.php?r=LocationInformation/EditEqut&BUILDING_NAME=" +
        BUILDING_NAME + "&BUILDING_ID=" + BUILDING_ID + "&AP_ID=" + AP_ID +
        "&FLOOR_ID=" + FLOOR_ID,
        width: width1,
        height: height1,
        top: top1,
        left: left1,
        title: "修改定位信息"
    });
}

/**
 * 全选按钮触发事件
 */
function checkboxAll(checkNode) {
    var checkboxlist = document.getElementsByName("checkbox[]");
    for (var i = 0; i < checkboxlist.length; i++) {
        checkboxlist[i].checked = checkNode.checked;
    }
}



/**
 *批量修改AP状态
 */
function submitChangeStatus(BUILDING_NAME, BUILDING_ID) {
    var status = document.getElementById("changestatus").value;
    var checkboxlist = document.getElementsByName("checkbox[]");
    var statuslist = document.getElementsByName("status");
    if (status == '') {
        alert("请选择状态!");
        return false;
    }
    var equt_id_list = '';
    for (var i = 0; i < checkboxlist.length; i++) {
        if (checkboxlist[i].checked) {
            equt_id_list += checkboxlist[i].value + ",";
        }
    }
    if (equt_id_list.length == 0) {
        alert("请选择楼层!");
        return false;
    }
    $.ajax({
        url: "../../api/ap-equipment-data/ap-equipment-status-update",
        data: {
            "apIds": equt_id_list.substr(0, equt_id_list.length - 1),
            "status": status
        },
        type: "post",
        success: function (msg) {
            showInfoInAndOut('info', '操作成功！');
            $("#queryBtn").click();
        },
        error: function (error) {
            showInfoInAndOut('info', '操作失败！');
        }
    });
}

/**
 * 刷新当前页面
 */
function windowRefresh() {
    $.popup({
        close: true
    });
    window.location.reload();
}

function editDialogClose() {
    $("#editApDiv").animate({"marginTop": "-188px", "opacity": "0"}, 300, function () {
        $("#editApDiv").css({"marginTop": "00px"});
        $("#editApDialog").animate({"opacity": "0"}, 300, function () {
            $("#editApDialog").hide();
        });
    });
}

function addDialogClose() {
    $("#addApDiv").animate({"marginTop": "-188px", "opacity": "0"}, 300, function () {
        $("#addApDiv").css({"marginTop": "00px"});
        $("#addApDialog").animate({"opacity": "0"}, 300, function () {
            $("#addApDialog").hide();
        });
    });
}

/**
 *添加AP信息内容检测是否合法
 */
function checkAddForm() {
    var re_kong = /^\s*$/;
    var re_MAC_BSSID = /^(([a-fA-F\d]{2}:[a-fA-F\d]{2}:[a-fA-F\d]{2}:[a-fA-F\d]{2}:[a-fA-F\d]{2}:[a-fA-F\d]{2})|([a-fA-F\d]{2}-[a-fA-F\d]{2}-[a-fA-F\d]{2}-[a-fA-F\d]{2}-[a-fA-F\d]{2}-[a-fA-F\d]{2}))$/;
    var re_POSITION_X = /^([0-9]{1,4})(\.[0-9]+?)?$/;
    var re_POSITION_Y = /^([0-9]{1,4})(\.[0-9]+?)?$/;
    var re_FREQUENCY = /^[0-9]{1,5}$/;
    var re_CHANNEL = /^[0-9]{1,5}$/;
    if ($('#addApDialog #equtSsid').val() == '' || re_kong.test($('#addApDialog #equtSsid').val())) {
        alert('设备别名不能为空！');
        $('#addApDialog #equtSsid').focus();
        return false;
    }
    if ($('#addApDialog #floorId').val() == '') {
        alert('请选择所属楼层！');
        $('#addApDialog #floorId').focus();
        return false;
    }
    if ($('#addApDialog #drawMapId').val() == '') {
        alert('请选择该设备所属平面图！');
        $('#addApDialog #drawMapId').focus();
        return false;
    }

    if ($('#addApDialog #layerId').val() == '') {
        alert('请选择该设备所属图层！');
        $('#addApDialog #layerId').focus();
        return false;
    }
    if ($('#addApDialog #svgId').val() == '') {
        alert('请选择该设备所属图层元素！');
        $('#addApDialog #svgId').focus();
        return false;
    }
    if ($('#addApDialog #equtType').val() == '') {
        alert('请选择类别！');
        $('#addApDialog #equtType').focus();
        return false;
    }
    if ($('#addApDialog #status').val() == '') {
        alert('请选择状态！');
        $('#addApDialog #status').focus();
        return false;
    }
    if ($('#addApDialog #positionX').val() == '' || !re_POSITION_X.test($('#addApDialog #positionX').val())) {
        alert('请填写与边界X轴距离，最多为4位(小数点最多保留2位)！');
        $('#addApDialog #positionX').focus();
        return false;
    }
    if ($('#addApDialog #macBssid').val() == '' || !re_MAC_BSSID.test($('#addApDialog #macBssid').val()) || re_kong.test($('#addApDialog #macBssid').val())) {
        alert('MAC不能为空，由数字和大小写英文字母（a-f）及冒号或“-”组成（格式一般为c4:ca:d9:59:93:30或c4-ca-d9-59-93-30）！');
        $('#addApDialog #macBssid').focus();
        return false;
    }
    if ($('#addApDialog #positionY').val() == '' || !re_POSITION_Y.test($('#addApDialog #positionY').val())) {
        alert('请填写与边界Y轴距离，最多为4位(小数点最多保留2位)！');
        $('#addApDialog #positionY').focus();
        return false;
    }
    if (($('#addApDialog #frequency').val() != '' && !re_FREQUENCY.test($('#addApDialog #frequency').val())) || ($('#addApDialog #frequency').val() != '' && $('#addApDialog #frequency').val() == 0)) {
        alert('频率最多为5位数字,不能为0！');
        $('#addApDialog #FREQUENCY').focus();
        return false;
    }
    if (($('#addApDialog #channel').val() != '' && !re_CHANNEL.test($('#addApDialog #channel').val())) || ($('#addApDialog #channel').val() != '' && $('#addApDialog #channel').val() == 0)) {
        alert('信道最多为2位数字，不能为0！');
        $('#addApDialog #channel').focus();
        return false;
    }
    return true;
}

/**
 *更新AP信息内容检测是否合法
 */
function checkModForm() {
    var re_kong = /^\s*$/;
    var re_MAC_BSSID = /^(([a-fA-F\d]{2}:[a-fA-F\d]{2}:[a-fA-F\d]{2}:[a-fA-F\d]{2}:[a-fA-F\d]{2}:[a-fA-F\d]{2})|([a-fA-F\d]{2}-[a-fA-F\d]{2}-[a-fA-F\d]{2}-[a-fA-F\d]{2}-[a-fA-F\d]{2}-[a-fA-F\d]{2}))$/;
    var re_POSITION_X = /^([0-9]{1,4})(\.[0-9]+?)?$/;
    var re_POSITION_Y = /^([0-9]{1,4})(\.[0-9]+?)?$/;
    var re_FREQUENCY = /^[0-9]{1,5}$/;
    var re_CHANNEL = /^[0-9]{1,5}$/;
    if ($('#editApDialog #equtSsid').val() == '' || re_kong.test($('#editApDialog #equtSsid').val())) {
        alert('设备别名不能为空！');
        $('#editApDialog #equtSsid').focus();
        return false;
    }
    if ($('#editApDialog #floorId').val() == '') {
        alert('请选择所属楼层！');
        $('#editApDialog #floorId').focus();
        return false;
    }
    if ($('#editApDialog #drawMapId').val() == '') {
        alert('请选择该设备所属平面图！');
        $('#editApDialog #drawMapId').focus();
        return false;
    }

    if ($('#editApDialog #layerId').val() == '') {
        alert('请选择该设备所属图层！');
        $('#editApDialog #layerId').focus();
        return false;
    }
    if ($('#editApDialog #svgId').val() == '') {
        alert('请选择该设备所属图层元素！');
        $('#editApDialog #svgId').focus();
        return false;
    }
    if ($('#editApDialog #equtType').val() == '') {
        alert('请选择类别！');
        $('#editApDialog #equtType').focus();
        return false;
    }
    if ($('#editApDialog #status').val() == '') {
        alert('请选择状态！');
        $('#editApDialog #status').focus();
        return false;
    }
    if ($('#editApDialog #positionX').val() == '' || !re_POSITION_X.test($('#editApDialog #positionX').val())) {
        alert('请填写与边界X轴距离，最多为4位(小数点最多保留2位)！');
        $('#editApDialog #positionX').focus();
        return false;
    }
    if ($('#editApDialog #macBssid').val() == '' || !re_MAC_BSSID.test($('#editApDialog #macBssid').val()) || re_kong.test($('#editApDialog #macBssid').val())) {
        alert('MAC不能为空，由数字和大小写英文字母（a-f）及冒号或“-”组成（格式一般为c4:ca:d9:59:93:30或c4-ca-d9-59-93-30）！');
        $('#editApDialog #macBssid').focus();
        return false;
    }
    if ($('#editApDialog #positionY').val() == '' || !re_POSITION_Y.test($('#editApDialog #positionY').val())) {
        alert('请填写与边界Y轴距离，最多为4位(小数点最多保留2位)！');
        $('#editApDialog #positionY').focus();
        return false;
    }
    if (($('#editApDialog #frequency').val() != '' && !re_FREQUENCY.test($('#editApDialog #frequency').val())) || ($('#editApDialog #frequency').val() != '' && $('#editApDialog #frequency').val() == 0)) {
        alert('频率最多为5位数字,不能为0！');
        $('#editApDialog #frequency').focus();
        return false;
    }
    if (($('#editApDialog #channel').val() != '' && !re_CHANNEL.test($('#editApDialog #channel').val())) || ($('#editApDialog #channel').val() != '' && $('#editApDialog #channel').val() == 0)) {
        alert('信道最多为2位数字，不能为0！');
        $('#editApDialog #channel').focus();
        return false;
    }
    return true;
}
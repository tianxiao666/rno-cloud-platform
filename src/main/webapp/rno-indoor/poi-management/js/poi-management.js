$(function () {

    /**
     * 拖动窗口
     */
    $("#editPoiDiv").draggable();
    $("#addPoiDiv").draggable();

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
     * AJAX 提交兴趣点查询条件表单
     */
    $("#conditionForm").ajaxForm({
        url: "../../api/cb-poi-data/cb-poi-query",
        success: showPoiResult,
        error: function (data) {
            $(".loading").hide();
            showInfoInAndOut('info', '后台报错，请检查！');
        }
    });

    /**
     * 当SVGID改变时填充图层元素ID值
     */
    $("#addPoiDialog #svgId").change(function () {
        var layerMementId = $("#addPoiDialog #svgId").find("option:selected").attr("layerelement");
        $("#addPoiDialog #elementId").val(layerMementId);
        // console.log("layerMementI="+layerMementId);
    });

    /**
     * 添加POI定位对话窗口中选择FLOOR_ID取相应的DRAW_MAP_ID
     */
    $("#addPoiDialog #floorId").change(function () {
        var floorId = $("#addPoiDialog #floorId").find('option:selected').val();
        getFloorMapByFloorId(floorId);
        $("#addPoiDialog #drawMapId").trigger("change");
        $("#addPoiDialog #layerId").trigger("change");
    });

    /**
     * 添加POI对话窗口中选择drawMapId取相应的Layer_ID
     */
    $("#addPoiDialog #drawMapId").change(function () {
        var drawMapId = $("#addPoiDialog #drawMapId").find('option:selected').val();
        getPlaneLayerByDrawMapId(drawMapId);
        $("#addPoiDialog #layerId").trigger("change");
    });

    /**
     * 添加POI定位对话窗口中选择layerId取相应的svgId
     */
    $("#addPoiDialog #layerId").change(function () {
        var layerId = $("#addPoiDialog #layerId").find('option:selected').val();
        getLayerElementByLayerId(layerId);
    });

    /**
     * 编辑POI定位对话窗口中选择FLOOR_ID取相应的DRAW_MAP_ID
     */
    $("#editPoiDialog #floorId").change(function () {
        var floorId = $("#editPoiDialog #floorId").find('option:selected').val();
        getFloorMapByFloorIdForEdit(floorId);
        $("#editPoiDialog #drawMapId").trigger("change");
        $("#editPoiDialog #layerId").trigger("change");
    });

    /**
     * 编辑Poi定位对话窗口中选择drawMapId取相应的Layer_ID
     */
    $("#editPoiDialog #drawMapId").change(function () {
        var drawMapId = $("#editPoiDialog #drawMapId").find('option:selected').val();
        getPlaneLayerByDrawMapIdForEdit(drawMapId);
        $("#editPoiDialog #layerId").trigger("change");
    });

    /**
     * 编辑POI对话窗口中选择layerId取相应的svgId
     */
    $("#editPoiDialog #layerId").change(function () {
        var layerId = $("#editPoiDialog #layerId").find('option:selected').val();
        getLayerElementByLayerIdForEdit(layerId);
    });

    /**
     * 添加按钮
     */
    $("#addPoiBtn").click(function () {
        var layerMementId = $("#addPoiDialog #svgId").find("option:selected").attr("layerelement");
        $("#addPoiDialog #elementId").val(layerMementId);
        if (checkAddForm()) {
            $("#conditionAddForm").submit();
        }
    });

    /**
     * AJAX 提交添加poi表单
     */
    $("#conditionAddForm").ajaxForm({
        url: "../../api/cb-poi-data/cb-poi-save",
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
     * AJAX 提交更改POI表单
     */
    $("#conditionModForm").ajaxForm({
        url: "../../api/cb-poi-data/cb-poi-save",
        success: function (data) {
            // console.log(data);
            showInfoInAndOut('info', '保存成功！');
        },
        error: function (data) {
            $(".loading").hide();
            showInfoInAndOut('info', '后台报错，请检查！');
        }
    });

});

/**
 *显示POI查询结果
 */
function showPoiResult(data) {
    $(".loading").css("display", "none");
    if (data == '') {
        showInfoInAndOut('info', '没有符合条件的POI数据');
    }

    $('#queryResultTab').css("line-height", "12px");
    $('#queryResultTab').DataTable({
        "data": data,
        "columns": [
            {"data": null},
            {"data": "poiName"},
            {"data": "poiType"},
            {"data": "floorName"},
            {"data": "phone"},
            {"data": "address"},
            {"data": "status"},
            {"data": "操作"}
        ],
        "columnDefs": [{
            "render": function (data, type, row) {
                var id = row['poiId'];
                return "<input type=\"checkbox\" value='" + id + "' name=\"checkbox[]\">";
            },
            "targets": 0,
            "data": null,
            "orderable": false
        }, {
            "render": function (data, type, row) {
                var poiType = row['poiType'];
                var val = "";
                if (poiType == 'ANTS') {
                    val = "室内天线";
                } else if (poiType == 'DING') {
                    val = "餐饮";
                } else if (poiType == 'SHOP') {
                    val = "购物";
                } else if (poiType == 'FUNC') {
                    val = "休闲娱乐";
                }
                return val;
            },
            "targets": 2,
            "data": null
        }, {
            "render": function (data, type, row) {
                var status = row['status'];
                var val = "";
                if (status == 'A') {
                    val = "<img src=\"images/lamp_on.png\"> 正常";
                } else if (status == 'X') {
                    val = "<img src=\"images/lamp_off.png\"> 禁用";
                } else if (status == 'E') {
                    val = "<img src=\"images/lamp_off.png\"> 编辑中";
                }
                return val;
            },
            "targets": 6,
            "data": null
        }, {
            "render": function (data, type, row) {
                var id = row['poiId'];
                return "<a href=\"#\" onclick=\"Poi_change(" + id + ");\"> <img  src=\"images/s_edit.gif\"> 编辑</a> &nbsp;&nbsp;&nbsp;"
                    + "<a href=\"\" onclick=\"deletePoi('" + id + "');\"> 删除</a> &nbsp;&nbsp;&nbsp;"
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


function checkForm() {
    var re_EQUT_SSID = /^.{1,64}$/;
    if ($('#NAME').val() != '' &&
        !re_EQUT_SSID.test($('#NAME').val())) {
        alert('输入的关键字不能超过64个字符！');
        $('#NAME').focus();
        return false;
    }
    return true;
}

function toSubmit() {
    if (checkForm()) {
        $("#Form").submit();
    }
}

/**
 * 场所添加POI
 */
function AddPoi(BUILDING_NAME, BUILDING_ID) {
    $("#addPoiDialog").css({"display": "block", "opacity": "1"});
    $("#addPoiDiv").animate({"marginTop": "-128px", "opacity": "1"}, 300);

    var buildingId = $("#buildingId").find('option:selected').val();
    $("#addPoiDialog #buildingName").text($("#buildingId").find('option:selected').text());
    $("#addPoiDialog #buildingId").val($("#buildingId").find('option:selected').val());
    // $("#addApDialog #floorId").find('option').not(':first').remove();
    renderFloors({selectors: ["", "addPoiDialog #floorId"]}, buildingId, null);
    getPoiSysDictInfoForAdd();
}

/**
 * 楼层添加POI
 */
function Add_Floor_Poi(BUILDING_NAME, BUILDING_ID, FLOOR_ID) {
    var width1 = 688;
    var height1 = 440;
    var top1 = (screen.availHeight - height1) / 5;
    var left1 = (screen.availWidth - width1) / 2;
    $.popup({
        type: 1,
        now: true,
        url: "ea.php?r=Poi/AddPoi&BUILDING_NAME=" + BUILDING_NAME +
        "&BUILDING_ID=" + BUILDING_ID + "&FLOOR_ID=" + FLOOR_ID,
        width: width1,
        height: height1,
        top: top1,
        left: left1,
        title: "添加POI信息"
    });
}

/**
 * 场所修改POI
 */
function Poi_change(poiId) {
    $("#editPoiDialog").css({"display": "block", "opacity": "1"});
    $("#editPoiDiv").animate({"marginTop": "-128px", "opacity": "1"}, 300);

    //数据字典信息
    getPoiSysDictInfoForUpdate();

    //楼层信息
    var buildingId = $("#buildingId").find('option:selected').val();
    renderFloors({selectors: ["", "editPoiDialog #floorId"]}, buildingId, null);

    //数据表信息
    $.ajax({
        url: "../../api/cb-poi-data/cb-poi-edit",
        data: {
            "poiId": poiId,
        },
        type: "post",
        success: function (apData) {
            //{"poiId":1,"buildingId":1,"floorId":"2","drawMapId":"2","layerId":"1","svgId":"1","elementId":"1","poiName":"poiName","poiType":"ANTS","poiNote":"POINOTE","prov":"440000","city":"440100","district":"10","address":"address","positionX":"234","positionY":"256","phone":"24561354","picId":"1","note":"note","status":"A","antLac":"11","antCid":"22","antFrequency":"33","antPower":"44","cbFloor":{"floorId":2,"buildingId":1,"picId":null,"floorName":"广东省博物馆二楼","physicalFloor":"2","basement":"Y","floorType":"LOOBY","floorNote":"广东省博物馆二楼","status":"A"}}
            var prov = apData['prov'];
            var city = apData['city'];
            $('#editPoiDialog #prov').val(prov);
            $('#editPoiDialog #city').val(city);

            var buildingId = apData['buildingId'];
            $('#editPoiDialog #poiId').val(poiId);
            $('#editPoiDialog #buildingId').val(buildingId);

            var floorId = apData['floorId'];
            var drawMapId = apData['drawMapId'];
            var elementId = apData['elementId'];
            var layerId = apData['layerId'];
            var svgId = apData['svgId'];
            $('#editPoiDialog #floorId option[value=' + floorId + ']').attr('selected', 'selected');

            var poiName = apData['poiName'];
            var poiType = apData['poiType'];
            $('#editPoiDialog #poiName').val(poiName);
            $('#editPoiDialog #poiType option[value=' + poiType + ']').attr('selected', 'selected');

            var poiNote = apData['poiNote'];
            var note = apData['note'];
            $('#editPoiDialog #poiNote').text(poiNote);
            $('#editPoiDialog #note').text(note);

            var phone = apData['phone'];
            var positionX = apData['positionX'];
            var positionY = apData['positionY'];
            $('#editPoiDialog #phone').val(phone);
            $('#editPoiDialog #positionX').val(positionX);
            $('#editPoiDialog #positionY').val(positionY);


            var address = apData['address'];
            $('#editPoiDialog #address').val(address);;
            var status = apData['status'];
            $('#editPoiDialog #status option[value=' + status + ']').attr('selected', 'selected');

            /*         通过楼层ID选择改变 楼层平面图数据信息         */
            $.ajax({
                url: "../../api/dm-draw-map-data/dm-drawmap-query-by-floorid",
                data: {
                    "floorId": floorId,
                },
                type: "post",
                success: function (data) {
                    $("#editPoiDialog #drawMapId").find('option').not(':first').remove();
                    $.each(data, function (index) {
                        var CBB = data[index];
                        $("#editPoiDialog #drawMapId").append("<option value='" + CBB.drawMapId + "'>" + CBB.dmTopic + "</option>");
                    });
                    $('#editPoiDialog #drawMapId option[value=' + drawMapId + ']').attr('selected', 'selected');
                    /*         通过楼层平面图ID选择改变 平面图层数据信息         */
                    $.ajax({
                        url: "../../api/dm-plane-Layer-data/dm-plane-layer-query-by-drawmapid",
                        data: {
                            "drawMapId": drawMapId,
                        },
                        type: "post",
                        success: function (planeLayerData) {
                            $("#editPoiDialog #layerId").find('option').not(':first').remove();
                            $.each(planeLayerData, function (index) {
                                var CBB = planeLayerData[index];
                                $("#editPoiDialog #layerId").append("<option value='" + CBB.layerId + "'>" + CBB.layerTopic + "</option>");
                            });
                            // console.log("layerid="+layerId);
                            $('#editPoiDialog #layerId option[value=' + layerId + ']').attr('selected', 'selected');
                            /*         通过平面图ID选择改变 平面图层元素数据信息         */
                            $.ajax({
                                url: "../../api/dm-layer-element-data/dm-layer-element-query-by-layerid",
                                data: {
                                    "layerId": layerId,
                                },
                                type: "post",
                                success: function (layerElementData) {
                                    $("#editPoiDialog #svgId").find('option').not(':first').remove();
                                    $.each(layerElementData, function (index) {
                                        var CBB = layerElementData[index];
                                        $("#editPoiDialog #svgId").append("<option layerElement='" + CBB.elementId + "' value='" + CBB.svgId + "'>" + CBB.elementType + "(" + CBB.svgId + ")</option>");
                                    });
                                    // console.log("svgId="+svgId);
                                    $('#editPoiDialog #svgId option[value=' + svgId + ']').attr('selected', 'selected');
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
 * 楼层修改楼层POI
 */
function Poi_change_sel(BUILDING_NAME, BUILDING_ID,
                        POI_ID, FLOOR_ID) {
    var width1 = 688;
    var height1 = 440;
    var top1 = (screen.availHeight - height1) / 5;
    var left1 = (screen.availWidth - width1) / 2;
    $.popup({
        type: 1,
        now: true,
        url: "ea.php?r=Poi/EditPoi&BUILDING_NAME=" + BUILDING_NAME +
        "&BUILDING_ID=" + BUILDING_ID + "&POI_ID=" + POI_ID + "&FLOOR_ID=" +
        FLOOR_ID,
        width: width1,
        height: height1,
        top: top1,
        left: left1,
        title: "修改POI信息"
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
 * 批量修改POI状态
 */
function submitChangeStatus(BUILDING_NAME, BUILDING_ID) {
    var status = document.getElementById("changestatus").value;
    var checkboxlist = document.getElementsByName("checkbox[]");
    var statuslist = document.getElementsByName("status");
    if (status == '') {
        alert("请选择状态!");
        return false;
    }
    var poi_id_list = '';
    for (var i = 0; i < checkboxlist.length; i++) {
        if (checkboxlist[i].checked) {
            poi_id_list += checkboxlist[i].value + ",";
        }
    }
    if (poi_id_list.length == 0) {
        alert("请选择POI!");
        return false;
    }
    $.ajax({
        url: "../../api/cb-poi-data/cb-poi-status-update",
        data: {
            "poiIds": poi_id_list.substr(0, poi_id_list.length - 1),
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


function testEdit() {

    /*$("#addFloorDialog #buildingName").text($("#building").find('option:selected').text());
     $("#addFloorDialog #buildingId").val($("#building").find('option:selected').val());*/
    $("#editPoiDialog").css({"display": "block", "opacity": "1"});
    $("#editPoiDiv").animate({"marginTop": "-128px", "opacity": "1"}, 300);
}

function editDialogClose() {
    $("#editPoiDiv").animate({"marginTop": "-188px", "opacity": "0"}, 300, function () {
        $("#editPoiDiv").css({"marginTop": "00px"});
        $("#editPoiDialog").animate({"opacity": "0"}, 300, function () {
            $("#editPoiDialog").hide();
        });
    });
}

function testAdd() {

    /*$("#addFloorDialog #buildingName").text($("#building").find('option:selected').text());
     $("#addFloorDialog #buildingId").val($("#building").find('option:selected').val());*/
    $("#addPoiDialog").css({"display": "block", "opacity": "1"});
    $("#addPoiDiv").animate({"marginTop": "-128px", "opacity": "1"}, 300);
}

function addDialogClose() {
    $("#addPoiDiv").animate({"marginTop": "-188px", "opacity": "0"}, 300, function () {
        $("#addPoiDiv").css({"marginTop": "00px"});
        $("#addPoiDialog").animate({"opacity": "0"}, 300, function () {
            $("#addPoiDialog").hide();
        });
    });
}

/**
 * poi数据字典 add窗口
 */
function getPoiSysDictInfoForAdd() {
    $.ajax({
        url: "../../api/sys-dict-data/get-poi-sys-dict-info",
        data: {},
        type: "get",
        success: function (data) {
            $("#addPoiDialog #poiType").find('option').not(':first').remove();
            $("#addPoiDialog #status").find('option').not(':first').remove();
            $.each(data, function (key, value) {
                if ("POI_TYPE" == key) {
                    $.each(value, function (k, v) {
                        $("#addPoiDialog #poiType").append("<option value='" + k + "'>" + v + "</option>");
                    });
                } else if ("POI_STATUS" == key) {
                    $.each(value, function (k, v) {
                        $("#addPoiDialog #status").append("<option value='" + k + "'>" + v + "</option>");
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
 * poi数据字典 update窗口
 */
function getPoiSysDictInfoForUpdate() {
    $.ajax({
        url: "../../api/sys-dict-data/get-poi-sys-dict-info",
        data: {},
        type: "get",
        success: function (data) {
            $("#editPoiDialog #poiType").find('option').not(':first').remove();
            $("#editPoiDialog #status").find('option').not(':first').remove();
            $.each(data, function (key, value) {
                if ("POI_TYPE" == key) {
                    $.each(value, function (k, v) {
                        $("#editPoiDialog #poiType").append("<option value='" + k + "'>" + v + "</option>");
                    });
                } else if ("POI_STATUS" == key) {
                    $.each(value, function (k, v) {
                        $("#editPoiDialog #status").append("<option value='" + k + "'>" + v + "</option>");
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

function getFloorMapByFloorId(floorId) {

    $.ajax({
        url: "../../api/dm-draw-map-data/dm-drawmap-query-by-floorid",
        data: {
            "floorId": floorId,
        },
        type: "post",
        success: function (data) {
            $("#addPoiDialog #drawMapId").find('option').not(':first').remove();
            $.each(data, function (index) {
                var CBB = data[index];
                $("#addPoiDialog #drawMapId").append("<option value='" + CBB.drawMapId + "'>" + CBB.dmTopic + "</option>");
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
            $("#addPoiDialog #layerId").find('option').not(':first').remove();
            $.each(data, function (index) {
                var CBB = data[index];
                $("#addPoiDialog #layerId").append("<option value='" + CBB.layerId + "'>" + CBB.layerTopic + "</option>");
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
            $("#addPoiDialog #svgId").find('option').not(':first').remove();
            $.each(data, function (index) {
                var CBB = data[index];
                $("#addPoiDialog #svgId").append("<option layerElement='" + CBB.elementId + "' value='" + CBB.svgId + "'>" + CBB.elementType + "(" + CBB.svgId + ")</option>");
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
            $("#editPoiDialog #drawMapId").find('option').not(':first').remove();
            $.each(data, function (index) {
                var CBB = data[index];
                $("#editPoiDialog #drawMapId").append("<option value='" + CBB.drawMapId + "'>" + CBB.dmTopic + "</option>");
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
            $("#editPoiDialog #layerId").find('option').not(':first').remove();
            $.each(data, function (index) {
                var CBB = data[index];
                $("#editPoiDialog #layerId").append("<option value='" + CBB.layerId + "'>" + CBB.layerTopic + "</option>");
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
            $("#editPoiDialog #svgId").find('option').not(':first').remove();
            $.each(data, function (index) {
                var CBB = data[index];
                $("#editPoiDialog #svgId").append("<option layerElement='" + CBB.elementId + "' value='" + CBB.svgId + "'>" + CBB.elementType + "(" + CBB.svgId + ")</option>");
            });
        },
        error: function (error) {
            showInfoInAndOut('info', '获取图层元素失败！');
        }
    });
}

/**
 *添加POI信息内容检测是否合法
 */
function checkAddForm(){
    var re_kong = /^\s*$/;
    var re_LOCAT_X = /^([0-9]{1,4})(\.[0-9]+?)?$/;
    var re_LOCAT_Y = /^([0-9]{1,4})(\.[0-9]+?)?$/;
    if ($('#addPoiDialog #poiName').val() == ''||re_kong.test($('#addPoiDialog #poiName').val())) {
        alert('POI名称不能为空,最多64个字符！');
        $('#addPoiDialog #poiName').focus();
        return false;
    }
    if ($('#addPoiDialog #floorId').val() == '') {
        alert('请选择所属楼层！');
        $('#addPoiDialog #floorId').focus();
        return false;
    }
    if ($('#addPoiDialog #drawMapId').val() == '') {
        alert('请选择该设备所属平面图！');
        $('#addPoiDialog #drawMapId').focus();
        return false;
    }

    if ($('#addPoiDialog #layerId').val() == '') {
        alert('请选择该设备所属图层！');
        $('#addPoiDialog #layerId').focus();
        return false;
    }
    if ($('#addPoiDialog #svgId').val() == '') {
        alert('请选择该设备所属图层元素！');
        $('#addPoiDialog #svgId').focus();
        return false;
    }
    if ($('#addPoiDialog #poiType').val() == '') {
        alert('请选择类别！');
        $('#addPoiDialog #poiType').focus();
        return false;
    }
    if ($('#addPoiDialog #status').val() == '') {
        alert('请选择状态！');
        $('#addPoiDialog #status').focus();
        return false;
    }
    if ($('#addPoiDialog #positionX').val() == '' || !re_LOCAT_X.test($('#addPoiDialog #positionX').val())) {
        alert('请填写与边界X轴距离，最多为4位(小数点最多保留2位)！');
        $('#addPoiDialog #positionX').focus();
        return false;
    }
    if ($('#addPoiDialog #positionY').val() == '' || !re_LOCAT_Y.test($('#addPoiDialog #positionY').val())) {
        alert('请填写与边界Y轴距离，最多为4位(小数点最多保留2位)！');
        $('#addPoiDialog #positionY').focus();
        return false;
    }
    if ($('#addPoiDialog #poiNote').val() == ''||re_kong.test($('#addPoiDialog #poiNote').val())) {
        alert('POI简介不能为空！');
        $('#addPoiDialog #poiNote').focus();
        return false;
    }
    return true;
}

/**
 *编辑POI信息内容检测是否合法
 */
function checkModForm(){
    var re_kong = /^\s*$/;
    var re_LOCAT_X = /^([0-9]{1,4})(\.[0-9]+?)?$/;
    var re_LOCAT_Y = /^([0-9]{1,4})(\.[0-9]+?)?$/;
    var re_PHONE = /^(([0-9]+(\-[0-9]+)*)|(((\([0-9]{3,4}\))|([0-9]{3,4}\-))?[0-9]{7,8}))$/;
    if ($('#editPoiDialog #poiName').val() == ''||re_kong.test($('#editPoiDialog #poiName').val())) {
        alert('POI名称不能为空,最多64个字符！');
        $('#editPoiDialog #poiName').focus();
        return false;
    }
    if ($('#editPoiDialog #floorId').val() == '') {
        alert('请选择所属楼层！');
        $('#editPoiDialog #floorId').focus();
        return false;
    }
    if ($('#editPoiDialog #drawMapId').val() == '') {
        alert('请选择该设备所属平面图！');
        $('#editPoiDialog #drawMapId').focus();
        return false;
    }

    if ($('#editPoiDialog #layerId').val() == '') {
        alert('请选择该设备所属图层！');
        $('#editPoiDialog #layerId').focus();
        return false;
    }
    if ($('#editPoiDialog #svgId').val() == '') {
        alert('请选择该设备所属图层元素！');
        $('#editPoiDialog #svgId').focus();
        return false;
    }
    if ($('#editPoiDialog #poiType').val() == '') {
        alert('请选择类别！');
        $('#editPoiDialog #poiType').focus();
        return false;
    }
    if ($('#editPoiDialog #status').val() == '') {
        alert('请选择状态！');
        $('#editPoiDialog #status').focus();
        return false;
    }
    if ($('#editPoiDialog #positionX').val() == '' || !re_LOCAT_X.test($('#editPoiDialog #positionX').val())) {
        alert('请填写与边界X轴距离，最多为4位(小数点最多保留2位)！');
        $('#editPoiDialog #positionX').focus();
        return false;
    }
    if ($('#editPoiDialog #positionY').val() == '' || !re_LOCAT_Y.test($('#editPoiDialog #positionY').val())) {
        alert('请填写与边界Y轴距离，最多为4位(小数点最多保留2位)！');
        $('#editPoiDialog #positionY').focus();
        return false;
    }
    if ($('#editPoiDialog #phone').val() != '' && !re_PHONE.test($('#editPoiDialog #phone').val())) {
        alert('请填写正确电话号码格式（如020-33333333）！');
        $('#editPoiDialog #phone').focus();
        return false;
    }
    if ($('#editPoiDialog #poiNote').val() == ''||re_kong.test($('#editPoiDialog #poiNote').val())) {
        alert('POI简介不能为空！');
        $('#editPoiDialog #poiNote').focus();
        return false;
    }
    return true;
}

/**
 *删除poi
 */
function deletePoi(id) {
    if (confirm("确定要删除吗？")) {
        $.ajax({
            url: "../../api/cb-poi-data/cb-poi-delete",
            data: {
                'poiId': id,
                _method: 'DELETE'
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
 *检测文件是否为图片
 */
function onUploadImgChangeForAdd(sender){
    var file = sender.files[0];
    if (file.type.indexOf("image") != -1) {
        return true;
    }else {
        alert('图片格式无效！');
        $('#addPoiDialog #file').val('');
        return false;
    }
}

/**
 *检测文件是否为图片
 */
function onUploadImgChangeForMod(sender){
    var file = sender.files[0];
    if (file.type.indexOf("image") != -1) {
        return true;
    }else {
        alert('图片格式无效！');
        $('#editPoiDialog #file').val('');
        return false;
    }
}
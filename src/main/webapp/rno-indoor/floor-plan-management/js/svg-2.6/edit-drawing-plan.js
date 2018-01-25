/**
 * 初始化颜色选择器插件
 * @param {Object} "#BACKGROUD_COLOR"
 */
$(document).ready(function(){

    $("#DRAW_MAP_ID").val(env.DRAW_MAP_ID);
    rePositionSvgEdit();
    if (!!env.include) {
        init_embed();
        init_fullscreen();
    }
    $("#BUILDING_NAME").text(env.BUILDING_NAME);
    $("#FLOOR_ID").find('option').not(':first').remove();
    $.each(env.floorData, function (index) {
        var CBB = env.floorData[index];
        if (env.FLOOR_ID==CBB.floorId){
            $("#FLOOR_ID").append("<option value='" + CBB.floorId + "' selected='selected'>" + CBB.floorName + "</option>");
        }else {
            $("#FLOOR_ID").append("<option value='" + CBB.floorId + "'>" + CBB.floorName + "</option>");
        }
    });
});

function onFullscreenChange(){
    var fullscreenElement = document.fullscreenElement ||
        document.mozFullScreenElement || document.webkitFullscreenElement;
    if (fullscreenElement) {
        env.isFullScreen = true;
        $("#fullscreenbutton").val("还原");
    }
    else {
        env.isFullScreen = false;
        $("#fullscreenbutton").val("全屏");
    }
}

function init_fullscreen(){
    var elem = document.getElementById("fullscreenbutton");
    if (elem) {
        if (supportFullscreen()) {
            elem.style.display = "";
            document.addEventListener("fullscreenchange",
                function(){
                    onFullscreenChange();
                }, false);

            document.addEventListener("mozfullscreenchange",
                function(){
                    onFullscreenChange();
                }, false);

            document.addEventListener("webkitfullscreenchange",
                function(){
                    onFullscreenChange();
                }, false);

            document.addEventListener("msfullscreenchange",
                function(){
                    onFullscreenChange();
                }, false);
        }
        else {
            elem.style.display = "none";
        }
    }
}

function togglefullscreen(){
    if (env.isFullScreen) {
        exitFullscreen();
        //$("#fullscreenbutton").val("全屏");
        //env.isFullScreen = false;
    }
    else {
        launchFullscreen(document.getElementById("svgeditdiv"));
        //$("#fullscreenbutton").val("还原");
        //env.isFullScreen = true;
    }
    //rePositionSvgEdit();
}

function supportFullscreen(){
    return (document.fullscreenEnabled ||
    document.mozFullScreenEnabled || document.webkitFullscreenEnabled ||
    document.msFullscreenEnabled);
}

function launchFullscreen(element){
    if (element.requestFullscreen) {
        element.requestFullscreen();
    }
    else {
        if (element.mozRequestFullScreen) {
            element.mozRequestFullScreen();
        }
        else {
            if (element.webkitRequestFullscreen) {
                element.webkitRequestFullscreen();
            }
            else {
                if (element.msRequestFullscreen) {
                    element.msRequestFullscreen();
                }
            }
        }
    }
}

function exitFullscreen(){
    if (document.exitFullscreen) {
        document.exitFullscreen();
    }
    else {
        if (document.mozCancelFullScreen) {
            document.mozCancelFullScreen();
        }
        else {
            if (document.webkitExitFullscreen) {
                document.webkitExitFullscreen();
            }
        }
    }
}

function fullscreenElement(){
    return (document.fullscreenElement ||
    document.mozFullScreenElement || document.webkitFullscreenElement ||
    document.msFullscreenElement)
}

/**
 * 重新设定svgeditor的位置
 */
function rePositionSvgEdit(){
    env.svgeditoffset = $("#svgeditdiv").offset().top;
    var h = (window.screen.availHeight - 123) - (env.svgeditoffset) -
        (60 + 20);
    var MinHeight = 71 + $("#sidepanels").height() +
        $("#tools_bottom").height();
    if (h < MinHeight) {
        h = MinHeight;
    }
    $("#svgeditdiv").css({
        height: (h + "px"),
        top: (env.svgeditoffset + "px")
    });
    $("#svgeditbgdiv").css({
        height: (h + "px")
    });
}

/**
 * 设置背景颜色选择框的位置
 */
function setBackgroundcolorpickerDivPosition(){
    var pos = $("#BACKGROUD_COLOR").position();
    $("#backgroundcolorpicker_div").css({
        'left': pos.left,
        'top': pos.top + $("#BACKGROUD_COLOR").height() + 7
    });
}

/**
 * 获取svgedit的contentWindow
 */
function getSvgeditContentWindow(){
    if (!!!env.include) {
        return ($("#svgedit")[0].contentWindow);
    }
    return (window);
}

var svgCanvas = null;
function init_embed(){
    if (!!!env.include) {
        var frame = document.getElementById('svgedit');
        svgCanvas = new embedded_svg_edit(frame);
        // Hide main button, as we will be controlling new/load/save
       // etc from the host document
        var doc;
        doc = frame.contentDocument;
        if (!doc) {
            doc = frame.contentWindow.document;
        }
        var mainButton = doc.getElementById('main_button');
        //mainButton.style.display = 'none';
    }
    else {
        svgCanvas = getSvgeditContentWindow().svgCanvas;
    }
    getSvgeditContentWindow().setEmbed();

    if (env.FLAG) {
        var DRAW_MAP_ID = env.DRAW_MAP_ID;
        loadSvg(DRAW_MAP_ID);
    }
}


/**
 * 判断ID的值是否为空
 * @param {Object} idinfo
 */
function isIdValueEmpty(idinfo){
    var id = idinfo[0];
    if ($('#' + id).val() == "") {
        var tips = "请";
        if (idinfo[1] == "1") {
            tips = tips + "选择";
        }
        else {
            tips = tips + "填写";
        }
        tips = tips + idinfo[2] + "!";
        alert(tips);
        $('#' + id).focus();
        return (true);
    }
    return (false);
}

/**
 *检测表单
 */
function checkForm(){
    var paramsJson = {
        "BUILDING_ID": env.BUILDING_ID,
        "DRAW_MAP_ID": $("#DRAW_MAP_ID").val(),
        "FLOOR_WIDTH": env.FLOOR_WIDTH,
        "FLOOR_HEIGHT": env.FLOOR_HEIGHT
    };
    var notEmptyIdList = new Array(new Array("DM_TOPIC", 0, "标题"),
        new Array("FLOOR_ID", "1", "楼层"));
    var i = 0;
    while (i < notEmptyIdList.length) {
        if (isIdValueEmpty(notEmptyIdList[i])) {
            return (null);
        }
        console.log(notEmptyIdList[i][0]+"="+$("#" +
                notEmptyIdList[i][0]).val());
        paramsJson[notEmptyIdList[i][0]] = $("#" +
            notEmptyIdList[i][0]).val();
        ++i;
    }
    console.log("checkForm paramsJson=" +paramsJson);
    return (paramsJson);
}

var viewbox_width = null;
var viewbox_height = null;
/**
 * 更新画布大小
 */
function setViewBoxSize(){
    var unit = $("#DW_UNIT").val();
    var w = null;
    var h = null;
    if (unit == "px") {
        unit = "";
    }
    if (viewbox_width != null) {
        w = viewbox_width + unit;
    }
    if (viewbox_height != null) {
        h = viewbox_height + unit;
    }
    getSvgeditContentWindow().$("#SVG_VIEWBOX_WIDTH").val(w);
    getSvgeditContentWindow().$("#SVG_VIEWBOX_HEIGHT").val(h);
    getSvgeditContentWindow().$("#SVG_VIEWBOX_WIDTH").click();
}

/**
 * 场所变化时,修改相应的变化
 */
function onBuildingChanged(){
    getFloorList();
    var BUILDING_ID = $("#BUILDING_ID").val();
    if (BUILDING_ID != "") {
        var BUILDING_NAME = $("#BUILDING_ID option:selected").text();
        $("#BUILDING_NAME").html(BUILDING_NAME);
        $("#BuildingFloorMrg_ID").attr("href",
            "ea.php?r=BuildingFloor&BUILDING_ID=" + BUILDING_ID + "&BUILDING_NAME=" +
            BUILDING_NAME);
        $("#BuildingFloorPlanegraphMgr_ID").attr("href",
            "ea.php?r=BuildingFloorPlanegraphMgr&BUILDING_ID=" + BUILDING_ID +
            "&BUILDING_NAME=" + BUILDING_NAME);
    }
    else {
        $("#BUILDING_NAME").html("无所场");
        $("#BuildingFloorMrg_ID").removeAttr("href");
        $("#BuildingFloorPlanegraphMgr_ID").removeAttr("href");
    }
}

/**
 * 获取楼层列表
 */
function getFloorList(){
    var floorListcls = $(".floorListcls");
    floorListcls.empty();
    floorListcls.append("<option value=\"\">--请选择--</option>");
    $.ajax({
        type: "POST",
        url: "ea.php?r=SvgMgr/AjaxFloorList",
        data: "BUILDING_ID=" + $("#BUILDING_ID").val(),
        dataType: 'json',
        success: function(json){
            floorListcls.empty();
            floorListcls.append(json.html);
        }
    });
}

/**
 * ajax加载楼层svg源码
 */
function getFloorSVG(needconfirm){
    var FLOOR_ID = $("#FLOOR_ID").val();
    $.ajax({
        type: "POST",
        url: "ea.php?r=SvgMgr/AjaxHasSvg",
        data: "FLOOR_ID=" + FLOOR_ID,
        dataType: 'json',
        success: function(json){
            if (json.DRAW_MAP_ID) {
                if (!needconfirm ||
                    window.confirm("该图层存在平面图，是否需要加载？")) {
                    $("#DRAW_MAP_ID").val(json.DRAW_MAP_ID);
                    loadSvg(json.DRAW_MAP_ID);
                }
                else {
                    $("#DRAW_MAP_ID").val("");
                }
            }
            else {
                $("#DRAW_MAP_ID").val("");
            }
        }
    });
}

/**
 * rgb颜色转rgb
 * @param {Object} rgb
 */
function ColorToRGB(color){
    if (color.length == 7) {
        return [parseInt('0x' + color.substring(1, 3)) / 255,
            parseInt('0x' + color.substring(3, 5)) / 255, parseInt('0x' +
                color.substring(5, 7)) / 255];
    }
    else {
        if (color.length == 4) {
            return [parseInt('0x' + color.substring(1, 2)) / 15,
                parseInt('0x' + color.substring(2, 3)) / 15, parseInt('0x' +
                    color.substring(3, 4)) / 15];
        }
    }
    return ([0, 0, 0]);
}

/**
 * rgb转hsl
 * @param {Object} rgb
 */
function RGBToHSL(rgb){
    var min, max, delta, h, s, l;
    var r = rgb[0], g = rgb[1], b = rgb[2];
    min = Math.min(r, Math.min(g, b));
    max = Math.max(r, Math.max(g, b));
    delta = max - min;
    l = (min + max) / 2;
    s = 0;
    if (l > 0 && l < 1) {
        s = delta / (l < 0.5 ? (2 * l) : (2 - 2 * l));
    }
    h = 0;
    if (delta > 0) {
        if (max == r && max != g)
            h += (g - b) / delta;
        if (max == g && max != b)
            h += (2 + (b - r) / delta);
        if (max == b && max != r)
            h += (4 + (r - g) / delta);
        h /= 6;
    }
    return [h, s, l];
}

/**
 * 将rgb颜色转换成格式 rgb(123, 111, 222)
 * @param {Object} Color
 */
function ColorToRGBStr(Color){
    var rgb = ColorToRGB(Color);
    return ("rgb(" + rgb[0] * 255 + ", " + rgb[1] * 255 + ", " +
    rgb[2] * 255 + ")");
}

/**
 * 加载svg源码
 */
function loadSvg(DRAW_MAP_ID){
    lockScreen("lockscreen", "正在加载......");
    var BUILDING_ID = $("#BUILDING_ID").val();
    var FLOOR_ID = $("#FLOOR_ID").val();
    $.ajax({
        type: "POST",
        url: "../../../../api/dm-plane-Layer-data/ajax-download-svg",
        data: {
            "buildingId" :env.BUILDING_ID ,
            "floorId": env.FLOOR_ID,
            "drawMapId" : env.DRAW_MAP_ID,
            "buildingName" : env.BUILDING_NAME
        },
        dataType: 'json',
        success: function(json){
            if (json.error) {
                unlockScreen("lockscreen");
                alert(json.error);
            }
            else {
                svgCanvas.setSvgString(json.SVGSRC, true);
                $("#DM_TOPIC").val(json.DM_TOPIC);
                unlockScreen("lockscreen");
            }
        },
        error: function(err){
            unlockScreen("lockscreen");
            if (err) {
                if (err.responseText != "") {
                    console.log("出现错误");
                    alert(err.responseText);
                }
                else {
                    alert("ajax:加载中断或出错！");
                }
            }
            else {
                alert("ajax:加载出错！");
            }
        }
    });
}

function exportPng(svgsrcinfo){
    if (!$('#export_canvas').length) {
        $('<canvas>', {
            id: 'export_canvas'
        }).hide().appendTo('body');
    }
    var c = $('#export_canvas')[0];
    c.width = svgsrcinfo.w;
    c.height = svgsrcinfo.h;
    canvg(c, svgsrcinfo.svgsrc, {
        renderCallback: function(){
            var datauri = "";
            try {
                datauri = c.toDataURL('image/png');
            }
            catch (error) {
                alert("获取PNG原图错误," + error.toString());
            }
            svgsrcinfo.callback(datauri);
        }
    });
}

function getApFormListJsonStr(){
    return (JSON.stringify(env.ApFormListJson));
}

function getPoiFormListJsonStr(){
    return (JSON.stringify(env.PoiFormListJson));
}

function getSvgPicListJsonStr(){
    return (JSON.stringify(env.SvgPicListJson));
}

var params = null;
/**
 * 保存svg源码
 */
function saveSvg(){
    paramsJson = checkForm();
    if (paramsJson != null) {
        lockScreen("lockscreen", "正在保存......");
        var SVGSRC = svgCanvas.getSvgString();
        console.log("SVGSRC="+SVGSRC);
        exportPng({
            w: svgCanvas.contentW,
            h: svgCanvas.contentH,
            svgsrc: SVGSRC,
            callback: function(PNGBASE64){
                //PNGBASE64 = datauri.replace(/\+/g, "%2B");
                //PNGBASE64 = PNGBASE64.replace(/\&/g, "%26");
                //SVGSRC = SVGSRC.replace(/\+/g, "%2B");
                //SVGSRC = SVGSRC.replace(/\&/g, "%26");
                if (paramsJson != null) {
                    paramsJson["APFORMLISTJSONSTR"] =
                        getApFormListJsonStr();
                    paramsJson["POIFORMLISTJSONSTR"] =
                        getPoiFormListJsonStr();
                    paramsJson["SVGPICLISTJSONSTR"] =
                        getSvgPicListJsonStr();
                    paramsJson["SVGSRC"] = SVGSRC;
                    paramsJson["PNGBASE64"] = PNGBASE64;

                    $("#PARAMSJSON").val(JSON.stringify(paramsJson));
                    $("#SELECTIMAGEFORM").ajaxSubmit({
                        //$.ajax({
                        //async: false,
                        type: "POST",
                        url:
                            "../../../../api/dm-plane-Layer-data/upload-svg-and-save-png",
                        dataType: 'json',
                        // data: paramsJson,
                        success: function(json){
                            if (json.DRAW_MAP_ID) {

                                $("#DRAW_MAP_ID").val(json.DRAW_MAP_ID);
                                env.SvgPicListJson = eval('(' +
                                    json.SVGPICLISTJSONSTR + ')');
                            }
                            $("#PARAMSJSON").val("");
                            $(".inputFileName").remove();
                            unlockScreen("lockscreen");
                            if (json.error) {
                                alert(json.error);
                            }
                            else {
                                if (json.warning) {
                                    alert(json.warning);
                                }
                                else {
                                    alert("保存成功！");
                                }
                            }
                        },
                        error: function(err){
                            $("#PARAMSJSON").val("");
                            $(".inputFileName").remove();
                            unlockScreen("lockscreen");
                            if (err) {
                                alert(err.responseText);
                            }
                            else {
                                "ajax:保存出错！"
                            }
                        }
                    });
                }
                else {
                    unlockScreen("lockscreen");
                }
            }
        });
    }
}

/**
 * 米转换成像素
 * @param {Object} m
 */
function MToPix(m){
    var cm = m * 100;
    var typemap =
        getSvgeditContentWindow().svgedit.units.getTypeMap();
    return (cm * typemap["cm"]);
}

var MAXVIEWBOX_WIDTH_INPIX = 2000;
var MINVIEWBOX_WIDTH_INPIX = 10;
var MAXVIEWBOX_HEIGHT_INPIX = 2000;
var MINVIEWBOX_HEIGHT_INPIX = 10;
var MAXSCALE = 999999;
var MINSCALE = 1;
var MAXSCALECOUNT = 50;
/**
 * 根据楼层长宽得出最大最小比例尺
 * @param {Object} FloorWidth
 * @param {Object} FloorHeight
 */
function getScaleRangeByFloorSizeInPix(FLOOR_WIDTH_PIX,
                                       FLOOR_HEIGHT_PIX){
    var MinScaleWid = null;
    var MaxScaleWid = null;
    if (FLOOR_WIDTH_PIX != null) {
        MinScaleWid = FLOOR_WIDTH_PIX / MAXVIEWBOX_WIDTH_INPIX;
        MaxScaleWid = FLOOR_WIDTH_PIX / MINVIEWBOX_WIDTH_INPIX;
    }
    var MinScaleHei = null;
    var MaxScaleHei = null;
    if (FLOOR_HEIGHT_PIX != null) {
        MinScaleHei = FLOOR_HEIGHT_PIX / MAXVIEWBOX_HEIGHT_INPIX;
        MaxScaleHei = FLOOR_HEIGHT_PIX / MINVIEWBOX_HEIGHT_INPIX;
    }
    var MinScale = null;
    if (MinScaleWid != null) {
        if (MinScaleHei != null) {
            MinScale = (MinScaleWid < MinScaleHei) ? MinScaleHei :
                MinScaleWid;
        }
        else {
            MinScale = MinScaleWid;
        }
    }
    else {
        MinScale = MinScaleHei;
    }
    var MaxScale = null;
    if (MaxScaleWid != null) {
        if (MaxScaleHei != null) {
            MaxScale = (MaxScaleWid < MaxScaleHei) ? MaxScaleWid :
                MaxScaleHei;
        }
        else {
            MaxScale = MaxScaleWid;
        }
    }
    else {
        MaxScale = MaxScaleHei;
    }
    if ((MinScale != null) && (MaxScale != null)) {
        var MinScaleTemp = parseInt(MinScale);
        if (MinScale > MinScaleTemp) {
            MinScale = MinScaleTemp + 1;
        }
        MaxScale = parseInt(MaxScale);
        if (MinScale < MINSCALE) {
            MinScale = MINSCALE;
        }
        if (MaxScale < MINSCALE) {
            MaxScale = MINSCALE;
        }
        if (MinScale > MAXSCALE) {
            MinScale = MAXSCALE;
        }
        if (MaxScale > MAXSCALE) {
            MaxScale = MAXSCALE;
        }
        if (MinScale <= MaxScale) {
            return (new Array(MinScale, MaxScale));
        }
    }
    return (null);
}

/**
 * 根据楼层长宽得出最大最小比例尺
 * @param {Object} FloorWidth
 * @param {Object} FloorHeight
 */
function getScaleOptions(ScaleRange, scale, FLOOR_WIDTH_PIX,
                         FLOOR_HEIGHT_PIX){
    var MinScale = ScaleRange[0];
    var MaxScale = ScaleRange[1];
    var ScaleOptions = "";
    var FloorPix = FLOOR_WIDTH_PIX > FLOOR_HEIGHT_PIX ?
        FLOOR_WIDTH_PIX : FLOOR_HEIGHT_PIX;
    var maxFloorPix = FloorPix / MinScale;
    var minFloorPix = FloorPix / MaxScale;
    var stepPix = 0;
    var scalecount = 1;
    if (MAXSCALECOUNT > 1) {
        scalecount = MAXSCALECOUNT;
        stepPix = (maxFloorPix - minFloorPix) / (MAXSCALECOUNT - 1);
    }
    if ((scale < MinScale) || (scale > MaxScale)) {
        scale = null;
    }
    var iScalePix = 0;
    var iScale = 0;
    var iScalelast = null;
    var i = scalecount - 1;
    while (i >= 0) {
        iScalePix = minFloorPix + i * stepPix;
        iScale = Math.round(FloorPix / iScalePix);
        if ((iScalelast == null) || (iScalelast != iScale)) {
            if ((scale != null) && (scale <= iScale)) {
                ScaleOptions = ScaleOptions + "<option value=\"" +
                    scale + "\" selected=\"selected\">" + scale + "</option>";
                if (scale != iScale) {
                    ScaleOptions = ScaleOptions + "<option value=\""
                        + iScale + "\">" + iScale + "</option>";
                }
                scale = MAXSCALE + 1;
            }
            else {
                ScaleOptions = ScaleOptions + "<option value=\"" +
                    iScale + "\">" + iScale + "</option>";
            }
            iScalelast = iScale;
        }
        --i;
    }
    return (ScaleOptions);
}

/**
 * 根据楼层长宽设置画布大小
 * @param {Object} FLOOR_WIDTH_PIX
 * @param {Object} FLOOR_HEIGHT_PIX
 */
function setViewBoxSizeByFloorSize(setscale){
    viewbox_width = null;
    viewbox_height = null;
    var FLOOR_WIDTH_PIX = null;
    var FLOOR_HEIGHT_PIX = null;
    var FLOOR_WIDTH = parseFloat($("#FLOOR_WIDTH").val());
    if (!isNaN(FLOOR_WIDTH)) {
        FLOOR_WIDTH_PIX = MToPix(FLOOR_WIDTH);
    }
    var FLOOR_HEIGHT = parseFloat($("#FLOOR_HEIGHT").val());
    if (!isNaN(FLOOR_HEIGHT)) {
        FLOOR_HEIGHT_PIX = MToPix(FLOOR_HEIGHT);
    }
    if ((FLOOR_WIDTH_PIX != null) || (FLOOR_HEIGHT_PIX != null)) {
        var DW_SCALE_CLS = $("#DW_SCALE");
        var DW_SCALE_STR = DW_SCALE_CLS.val();
        var scale = parseInt(DW_SCALE_STR);
        if (!setscale) {
            var ScaleRange =
                getScaleRangeByFloorSizeInPix(FLOOR_WIDTH_PIX, FLOOR_HEIGHT_PIX);
            if (ScaleRange != null) {
                if (isNaN(scale)) {
                    scale = null;
                }
                var ScaleOptions = getScaleOptions(ScaleRange,
                    scale, FLOOR_WIDTH_PIX, FLOOR_HEIGHT_PIX);
                DW_SCALE_CLS.empty();
                DW_SCALE_CLS.append(ScaleOptions);
                DW_SCALE_STR = DW_SCALE_CLS.val();
                scale = parseInt(DW_SCALE_STR);
            }
            else {
                scale = null;
                alert("楼层长和宽不成比例，请重新输入楼层长和宽！");
                DW_SCALE_CLS.empty();
            }
        }
        if (scale != null) {
            if (!isNaN(scale)) {
                if (FLOOR_HEIGHT_PIX != null) {
                    var VIEWBOX_WIDTH = FLOOR_HEIGHT_PIX / scale;
                    VIEWBOX_WIDTH =
                        getSvgeditContentWindow().svgedit.units.convertUnit(VIEWBOX_WIDTH,
                            $("#DW_UNIT").val());
                    VIEWBOX_WIDTH =
                        getSvgeditContentWindow().svgedit.units.shortFloat(VIEWBOX_WIDTH);
                    $("#VIEWBOX_WIDTH").html(VIEWBOX_WIDTH);
                    viewbox_width = VIEWBOX_WIDTH;
                }
                if (FLOOR_WIDTH_PIX != null) {
                    var VIEWBOX_HEIGHT = FLOOR_WIDTH_PIX / scale;
                    VIEWBOX_HEIGHT =
                        getSvgeditContentWindow().svgedit.units.convertUnit(VIEWBOX_HEIGHT,
                            $("#DW_UNIT").val());
                    VIEWBOX_HEIGHT =
                        getSvgeditContentWindow().svgedit.units.shortFloat(VIEWBOX_HEIGHT);
                    $("#VIEWBOX_HEIGHT").html(VIEWBOX_HEIGHT);
                    viewbox_height = VIEWBOX_HEIGHT;
                }
                setViewBoxSize();
            }
            else {
                alert("获取比例尺失败！");
            }
        }
    }
    if (viewbox_width == null) {
        $("#VIEWBOX_WIDTH").html("");
    }
    if (viewbox_height == null) {
        $("#VIEWBOX_HEIGHT").html("");
    }
}

/**
 * 楼层宽、长变化时,判断比例尺是否是小于等于999999.99小数,并计肯更新画布大小
 */
function onFloorSizeChange(FLOOR_SIZE_ID_A){
    var FLOOR_SIZE_ID_A_OBJ = $("#" + FLOOR_SIZE_ID_A);
    var FLOOR_SIZE_ID_A_STR = FLOOR_SIZE_ID_A_OBJ.val();
    if (FLOOR_SIZE_ID_A_STR != "") {
        var FLOOR_SIZE_ID_B = "FLOOR_WIDTH";
        var FLOOR_SIZE_NAME_A = "长";
        var FLOOR_SIZE_NAME_B = "宽";
        var VIEWBOX_SIZE_NAME_A = "宽";
        var VIEWBOX_SIZE_NAME_B = "高";
        var MAXVIEWBOX_SIZE_A_INPIX = MAXVIEWBOX_HEIGHT_INPIX;
        var MINVIEWBOX_SIZE_A_INPIX = MINVIEWBOX_HEIGHT_INPIX;
        if (FLOOR_SIZE_ID_A == FLOOR_SIZE_ID_B) {
            FLOOR_SIZE_ID_B = "FLOOR_HEIGHT";
            FLOOR_SIZE_NAME_A = "宽";
            FLOOR_SIZE_NAME_B = "长";
            VIEWBOX_SIZE_NAME_A = "高";
            VIEWBOX_SIZE_NAME_B = "宽";
            MAXVIEWBOX_SIZE_A_INPIX = MAXVIEWBOX_WIDTH_INPIX;
            MINVIEWBOX_SIZE_A_INPIX = MINVIEWBOX_WIDTH_INPIX;
        }
        var reg = new RegExp("^[0-9]{1,6}(\\.[0-9]{1,2})?$");
        if (!reg.test(FLOOR_SIZE_ID_A_STR)) {
            alert("根据库表结构，楼层" + FLOOR_SIZE_NAME_A +
                "只能为小于等于999999.99的数,且只能带两位小数！");
            FLOOR_SIZE_ID_A_OBJ.val("");
            FLOOR_SIZE_ID_A_OBJ.focus();
        }
        else {
            var FLOOR_SIZE_A = parseFloat(FLOOR_SIZE_ID_A_STR);
            if (isNaN(FLOOR_SIZE_A)) {
                alert("楼层" + FLOOR_SIZE_NAME_A + "输入不正确,请重新输入！");
                FLOOR_SIZE_ID_A_OBJ.val("");
                FLOOR_SIZE_ID_A_OBJ.focus();
            }
            else {
                var FLOOR_SIZE_A_PIX = MToPix(FLOOR_SIZE_A);
                var FLOOR_SIZE_B = parseFloat($("#" +
                    FLOOR_SIZE_ID_B).val());
                var MAX_FLOOR_SIZE_A_INPIX = MAXVIEWBOX_SIZE_A_INPIX
                    * MAXSCALE;
                var MIN_FLOOR_SIZE_A_INPIX = MINVIEWBOX_SIZE_A_INPIX
                    * MINSCALE;
                var maxchanged = false;
                var minchanged = false;
                if (!isNaN(FLOOR_SIZE_B)) {
                    var FLOOR_SIZE_B_PIX = MToPix(FLOOR_SIZE_B);
                    var ScaleRange =
                        getScaleRangeByFloorSizeInPix(FLOOR_SIZE_A_PIX, FLOOR_SIZE_B_PIX);
                    if (ScaleRange == null) {
                        if (FLOOR_SIZE_ID_A == "FLOOR_WIDTH") {
                            ScaleRange =
                                getScaleRangeByFloorSizeInPix(null, FLOOR_SIZE_B_PIX);
                        }
                        else {
                            ScaleRange =
                                getScaleRangeByFloorSizeInPix(FLOOR_SIZE_B_PIX, null);
                        }
                        var MAX_FLOOR_SIZE_A_INPIX_TEMP =
                            MAXVIEWBOX_SIZE_A_INPIX * ScaleRange[1];
                        var MIN_FLOOR_SIZE_A_INPIX_TEMP =
                            MINVIEWBOX_SIZE_A_INPIX * ScaleRange[0];
                        if (MAX_FLOOR_SIZE_A_INPIX >
                            MAX_FLOOR_SIZE_A_INPIX_TEMP) {
                            MAX_FLOOR_SIZE_A_INPIX =
                                MAX_FLOOR_SIZE_A_INPIX_TEMP;
                            maxchanged = true;
                        }
                        if (MIN_FLOOR_SIZE_A_INPIX <
                            MIN_FLOOR_SIZE_A_INPIX_TEMP) {
                            MIN_FLOOR_SIZE_A_INPIX =
                                MIN_FLOOR_SIZE_A_INPIX_TEMP;
                            minchanged = true;
                        }
                    }
                }
                if (FLOOR_SIZE_A_PIX > MAX_FLOOR_SIZE_A_INPIX) {
                    var MAX_FLOOR_SIZE_A_M_temp =
                        getSvgeditContentWindow().svgedit.units.convertUnit(MAX_FLOOR_SIZE_A_INPIX,
                            "cm") / 100;
                    var MAX_FLOOR_SIZE_A_M =
                        MAX_FLOOR_SIZE_A_M_temp.toFixed(2) - 0;
                    if (MAX_FLOOR_SIZE_A_M >
                        MAX_FLOOR_SIZE_A_M_temp) {
                        MAX_FLOOR_SIZE_A_M = (MAX_FLOOR_SIZE_A_M -
                        0.01).toFixed(2);
                    }
                    if (maxchanged) {
                        alert("根据楼层的" + FLOOR_SIZE_NAME_B + "，楼层" +
                            FLOOR_SIZE_NAME_A + "最大只能为" + MAX_FLOOR_SIZE_A_M + "米！");
                    }
                    else {
                        alert("根据画布" + VIEWBOX_SIZE_NAME_A + "最大只能为"
                            + MAXVIEWBOX_SIZE_A_INPIX + "像素，楼层" + FLOOR_SIZE_NAME_A + "最大只能为" +
                            MAX_FLOOR_SIZE_A_M + "米！");
                    }
                    FLOOR_SIZE_ID_A_OBJ.val(MAX_FLOOR_SIZE_A_M);
                    FLOOR_SIZE_ID_A_OBJ.focus();
                }
                if (FLOOR_SIZE_A_PIX < MIN_FLOOR_SIZE_A_INPIX) {
                    var MIN_FLOOR_SIZE_A_M_temp =
                        getSvgeditContentWindow().svgedit.units.convertUnit(MIN_FLOOR_SIZE_A_INPIX,
                            "cm") / 100;
                    var MIN_FLOOR_SIZE_A_M =
                        MIN_FLOOR_SIZE_A_M_temp.toFixed(2) - 0;
                    if (MIN_FLOOR_SIZE_A_M <
                        MIN_FLOOR_SIZE_A_M_temp) {
                        MIN_FLOOR_SIZE_A_M = (MIN_FLOOR_SIZE_A_M +
                        0.01).toFixed(2);
                    }
                    if (minchanged) {
                        alert("根据楼层的" + FLOOR_SIZE_NAME_B + "，楼层" +
                            FLOOR_SIZE_NAME_A + "最小只能为" + MIN_FLOOR_SIZE_A_M + "米！");
                    }
                    else {
                        alert("根据画布" + VIEWBOX_SIZE_NAME_A + "最小只能为"
                            + MINVIEWBOX_SIZE_A_INPIX + "像素，楼层" + FLOOR_SIZE_NAME_A + "最小只能为" +
                            MIN_FLOOR_SIZE_A_M + "米！");
                    }
                    FLOOR_SIZE_ID_A_OBJ.val(MIN_FLOOR_SIZE_A_M);
                    FLOOR_SIZE_ID_A_OBJ.focus();
                }
            }
        }
    }
    setViewBoxSizeByFloorSize(false);
}

/**
 * 比例尺变化时,判断比例尺是否是小于等于999999位的整数,并计肯更新画布大小
 */
function onScaleChange(){
    var DW_SCALE_CLS = $("#DW_SCALE");
    var DW_SCALE_STR = DW_SCALE_CLS.val();
    if (DW_SCALE_STR != "") {
        var reg = new RegExp("^[0-9]{1,6}$");
        if (!reg.test(DW_SCALE_STR)) {
            alert("比例尺只能小于7位的整数！");
            DW_SCALE_CLS.val("");
            DW_SCALE_CLS.focus();
        }
        else {
            var scale = parseInt(DW_SCALE_STR);
            if (isNaN(scale)) {
                alert("输入不正确,请重新选择！");
                DW_SCALE_CLS.val("");
                DW_SCALE_CLS.focus();
            }
        }
    }
    setViewBoxSizeByFloorSize(true);
}

var backup_BACKGROUD_COLOR_Value = "";
var backup_BACKGROUD_COLOR_Style = "";
var BackgroundColorBoxFadeIn = false;

/**
 * 显示颜色选择器
 */
function showSetBackgroundColorBox(){
    if (!BackgroundColorBoxFadeIn) {
        backup_BACKGROUD_COLOR_Value = $("#BACKGROUD_COLOR").val();
        backup_BACKGROUD_COLOR_Style =
            $("#BACKGROUD_COLOR").attr("style");
        setBackgroundcolorpickerDivPosition();
        $("#backgroundcolorpicker_div").fadeIn();
        BackgroundColorBoxFadeIn = true;
        var BACKGROUD_COLOR_OBJ = $("#BACKGROUD_COLOR");
        BACKGROUD_COLOR_OBJ.bind("keydown", "return", function(){
            $("#confimbackgroundcolorchange").click();
        });
        BACKGROUD_COLOR_OBJ.bind("keydown", "esc", function(){
            $("#cancelbackgroundcolorchange").click();
        });
    }
}

/**
 * 设置画布背景色
 * @param {Object} backgroundColor
 */
function setBackgroundColor(backgroundColor){

    getSvgeditContentWindow().$("#SVG_VIEWBOX_BACKGROUNDCOLOR").val(backgroundColor);


    getSvgeditContentWindow().$("#SVG_VIEWBOX_BACKGROUNDCOLOR").click();
}

/**
 * 确定颜色选择器选择的颜色
 */
function confimbackgroundcolorchange(){
    var BACKGROUD_COLOR = $("#BACKGROUD_COLOR").val();
    if (BACKGROUD_COLOR != "") {
        var reg = new
        RegExp("^#(([A-Fa-f0-9]{3})|([A-Fa-f0-9]{6}))$");
        if (!reg.test(BACKGROUD_COLOR)) {
            alert("画布背景色格式不正确！格式只能为#XXX或#XXXXXX（X为数字）");
            return (false);
        }
    }
    setBackgroundColor(ColorToRGBStr(BACKGROUD_COLOR));
    $("#backgroundcolorpicker_div").fadeOut();
    BackgroundColorBoxFadeIn = false;
    return (true);
}

/**
 * 取消颜色选择器选择的颜色
 */
function cancelbackgroundcolorchange(){
    $("#backgroundcolorpicker_div").fadeOut();
    BackgroundColorBoxFadeIn = false;
    $("#BACKGROUD_COLOR").val(backup_BACKGROUD_COLOR_Value);
    $("#BACKGROUD_COLOR").attr("style",
        backup_BACKGROUD_COLOR_Style);
}

/**
 * 设置度量单位(绘图单位)
 */
function setUnit(){

    getSvgeditContentWindow().$("#SVG_DW_UNIT").val($("#DW_UNIT").val());
    getSvgeditContentWindow().$("#SVG_DW_UNIT").click();
    var DW_SCALE_CLS = $("#DW_SCALE");
    var DW_SCALE_STR = DW_SCALE_CLS.val();
    viewbox_width = null;
    viewbox_height = null;
    if (DW_SCALE_STR != "") {
        var scale = parseInt(DW_SCALE_STR);
        var FLOOR_HEIGHT_CLS = $("#FLOOR_HEIGHT");
        var FLOOR_HEIGHT_STR = FLOOR_HEIGHT_CLS.val();
        if (FLOOR_HEIGHT_STR != "") {
            var FLOOR_WIDTH = parseFloat(FLOOR_HEIGHT_STR);
            var VIEWBOX_WIDTH = FLOOR_WIDTH / scale;
            VIEWBOX_WIDTH = MToPix(VIEWBOX_WIDTH);
            VIEWBOX_WIDTH =
                getSvgeditContentWindow().svgedit.units.convertUnit(VIEWBOX_WIDTH,
                    $("#DW_UNIT").val());
            VIEWBOX_WIDTH =
                getSvgeditContentWindow().svgedit.units.shortFloat(VIEWBOX_WIDTH);
            $("#VIEWBOX_WIDTH").html(VIEWBOX_WIDTH);
            viewbox_width = VIEWBOX_WIDTH;
        }
        var FLOOR_WIDTH_CLS = $("#FLOOR_WIDTH");
        var FLOOR_WIDTH_STR = FLOOR_WIDTH_CLS.val();
        if (FLOOR_WIDTH_STR != "") {
            var FLOOR_HEIGHT = parseFloat(FLOOR_WIDTH_STR);
            var VIEWBOX_HEIGHT = FLOOR_HEIGHT / scale;
            VIEWBOX_HEIGHT = MToPix(VIEWBOX_HEIGHT);
            VIEWBOX_HEIGHT =
                getSvgeditContentWindow().svgedit.units.convertUnit(VIEWBOX_HEIGHT,
                    $("#DW_UNIT").val());
            VIEWBOX_HEIGHT =
                getSvgeditContentWindow().svgedit.units.shortFloat(VIEWBOX_HEIGHT);
            $("#VIEWBOX_HEIGHT").html(VIEWBOX_HEIGHT);
            viewbox_height = VIEWBOX_HEIGHT;
        }
    }
    if (viewbox_width == null) {
        $("#VIEWBOX_WIDTH").html("");
    }
    if (viewbox_height == null) {
        $("#VIEWBOX_HEIGHT").html("");
    }
    if ((viewbox_width != null) || (viewbox_height != null)) {
        setViewBoxSize();
    }
}

/**
 * 删除元素
 * @param {Object} id
 */
function deleteElement(id){
    var id_OBJ = $("#" + id);
    if (id_OBJ.length > 0) {
        id_OBJ.css('display', 'none');
        id_OBJ.html("");
        id_OBJ.remove();
    }
}

/**
 * 解锁屏幕
 * @param {Object} divId
 */
function unlockScreen(divId){
    deleteElement(divId);
    deleteElement(divId + "content");
}

/**
 * 锁定屏幕
 * @param {Object} divId
 */
function lockScreen(divId, msg){
    unlockScreen(divId);
    var lockScreenDiv = document.createElement("div");
    lockScreenDiv.id = divId;
    lockScreenDiv.style.backgroundColor = "#000000";
    lockScreenDiv.style.position = "absolute";
    lockScreenDiv.style.top = 0;
    lockScreenDiv.style.left = 0;
    lockScreenDiv.style.opacity = 0.5;
    lockScreenDiv.style.width = document.body.clientWidth + "px";
    lockScreenDiv.style.height = document.body.clientHeight + "px";
    lockScreenDiv.style.filter = "alpha(Opacity=20)";
    lockScreenDiv.style.zIndex = 200;
    document.body.appendChild(lockScreenDiv);
    if (msg) {
        var lockScreenContentDiv = document.createElement("div");
        lockScreenContentDiv.id = divId + "content";
        lockScreenContentDiv.style.position = "absolute";
        lockScreenContentDiv.style.top = 0;
        lockScreenContentDiv.style.left = 0;
        lockScreenContentDiv.style.width = document.body.clientWidth
            + "px";
        lockScreenContentDiv.style.height =
            document.body.clientHeight + "px";
        lockScreenContentDiv.style.zIndex = 201;
        var msgDiv = document.createElement("div");
        msgDiv.style.position = "absolute";
        msgDiv.style.top = document.body.clientHeight / 2 - 50 +
            "px";
        msgDiv.style.left = document.body.clientWidth / 2 - 50 +
            "px";
        msgDiv.style.width = 200 + "px";
        msgDiv.style.height = 50 + "px";
        msgDiv.style.filter = "alpha(Opacity=20)";
        document.body.appendChild(msgDiv);
        var msgSpan = document.createElement("span");
        msgSpan.innerHTML = msg;
        msgSpan.style.fontSize = "25px";
        msgDiv.appendChild(msgSpan);
        lockScreenContentDiv.appendChild(msgDiv);
        document.body.appendChild(lockScreenContentDiv);
    }
}

function resetPoiInfo(){
    var PoiFormList = env.PoiFormListJson[env.elementId];
    if (PoiFormList != null) {
        $('#POI_NAME').val(PoiFormList['POI_NAME']);
        $('#POI_PHONE').val(PoiFormList['PHONE']);
        $('#POI_ADDRESS').val(PoiFormList['ADDRESS']);
        $('#POI_NOTE').val(PoiFormList['POI_NOTE']);
        $('#POI_REMARK').val(PoiFormList['NOTE']);
        $('#POI_STATUS').val(PoiFormList['STATUS']);
        $('#POI_TYPE').val(PoiFormList['POI_TYPE']);
        $('#ANT_LAC').val(PoiFormList['ANT_LAC']);
        $('#ANT_CID').val(PoiFormList['ANT_CID']);
        $('#ANT_FREQUENCY').val(PoiFormList['ANT_FREQUENCY']);
        $('#ANT_POWER').val(PoiFormList['ANT_POWER']);
    }
    else {
        $('#POI_NAME').val("");
        $('#POI_PHONE').val("");
        $('#POI_ADDRESS').val("");
        $('#POI_NOTE').val("");
        $('#POI_REMARK').val("");
        $('#POI_STATUS').val("");
        $('#POI_TYPE').val("");
        $('#ANT_LAC').val("");
        $('#ANT_CID').val("");
        $('#ANT_FREQUENCY').val("");
        $('#ANT_POWER').val("");
    }
    $("#inputFileName" + env.elementId).val("");
    removeSvgNewPic();
    var SvgPicList = env.SvgPicListJson[env.elementId];
    $("#uploadpoipic").html((((SvgPicList != null) &&
        (SvgPicList["PIC_ID"] != null)) ? "更新" : "上传") + "图片:");
    checkPoiType();
}

//var POI_TYPE_option = null;
function ShowPoiInfo(elementId, layertype, ismodify){
    env.elementId = elementId;
    var inputFileName = env.elementId;
    $("#POIINFO_TITLE").html((ismodify ? "修改" : "添加") + "POI信息");
    var inputFile = $("#inputFileName" + inputFileName);
    var SELECTIMAGEFORMHOLDER = $("#SELECTIMAGEFORM");
    if (inputFile.length == 0) {
        inputFile = $('<input class="inputFileName" type="file" onchange="onUploadImgChange(this)" style="font-size:12px;height:20px;width:95%" id="inputFileName' + inputFileName + '" name="' + inputFileName + '"></input>');
        inputFile.appendTo(SELECTIMAGEFORMHOLDER)
    }
    $(".inputFileName").css("display", "none");
    inputFile.css("display", "block");
    //if (POI_TYPE_option == null) {
    //   POI_TYPE_option = $('#POI_TYPE').html();
    // }
    //        if (layertype == env.LAYERTYPE_AP) {
    //            var ap_POI_TYPE = "<option
    //value='AP'>AP设备</option>";
    //            $('#POI_TYPE').html(ap_POI_TYPE);
    //            $('#POI_TYPE').attr("disabled", "true");
    //        }
    //        else {
    //            $('#POI_TYPE').html(POI_TYPE_option);
    //            $('#POI_TYPE').removeAttr("disabled");
    //        }
    resetPoiInfo();
    var popupbgdiv = document.getElementById("popupbgdiv");
    popupbgdiv.style.backgroundColor = "#000000";
    popupbgdiv.style.position = "absolute";
    popupbgdiv.style.top = 0;
    popupbgdiv.style.left = 0;
    popupbgdiv.style.opacity = 0.5;
    popupbgdiv.style.width = document.body.clientWidth + "px";
    popupbgdiv.style.height = document.body.clientHeight + "px";
    popupbgdiv.style.filter = "alpha(Opacity=20)";
    popupbgdiv.style.zIndex = 200;
    popupbgdiv.style.display = "block";
    var popuppoidiv = document.getElementById("popuppoidiv");
    popuppoidiv.style.position = "absolute";
    popuppoidiv.style.top = 250 + "px";
    popuppoidiv.style.left = 230 + "px";
    //popuppoidiv.style.top = document.body.clientWidth / 2 - 650 / 2
    + "px";
    //popuppoidiv.style.left = document.body.clientHeight / 2 - 349 /
    2 + "px";
    //popuppoidiv.style.width = 688 + "px";
    //popuppoidiv.style.height = 500 + "px";
    //popuppoidiv.style.height = 349 + "px";
    popuppoidiv.style.zIndex = 201;
    popuppoidiv.style.display = "block";
    env.elementId = elementId;
}

function hidePoiInfo(){
    var popupbgdiv = document.getElementById("popupbgdiv");
    popupbgdiv.style.display = "none";
    var popuppoidiv = document.getElementById("popuppoidiv");
    popuppoidiv.style.display = "none";
    var element = svgedit.utilities.getElem(env.elementId);
    if (element.tagName == "text") {
        svgCanvas.textActions.start(element);
    }
}

function closePoiInfo(){
    $("#inputFileName" + env.elementId).val("");
    removeSvgNewPic();
    hidePoiInfo();
}


function removeSvgNewPic(){
    if ((env.SvgPicListJson[env.elementId] != null) &&
        (env.SvgPicListJson[env.elementId]["NEW_PIC"] != null)) {
        var picid = env .SvgPicListJson[env.elementId]["PIC_ID"];
        if (picid != null) {
            env.SvgPicListJson[env.elementId] = {
                "PIC_ID": picid
            };
        }
        else {
            env.SvgPicListJson[env.elementId] = {};
        }
    }
}

function resetApInfo(){
    var ApFormList = env.ApFormListJson[env.elementId];
    if (ApFormList != null) {
        $('#AP_EQUT_SSID').val(ApFormList["EQUT_SSID"]);
        $('#AP_MAC_BSSID').val(ApFormList["MAC_BSSID"]);
        $('#AP_EQUT_TYPE').val(ApFormList["EQUT_TYPE"]);
        $('#AP_FREQUENCY').val(ApFormList["FREQUENCY"]);
        $('#AP_CHANNEL').val(ApFormList["CHANNEL"]);
        $('#AP_FACTORY').val(ApFormList["FACTORY"]);
        $('#AP_BRANDS').val(ApFormList["BRANDS"]);
        $('#AP_EQUT_MODEL').val(ApFormList["EQUT_MODEL"]);
        $('#AP_NOTE').val(ApFormList["NOTE"]);
        $('#AP_STATUS').val(ApFormList["STATUS"]);
    }
    else {
        $('#AP_EQUT_SSID').val("");
        $('#AP_MAC_BSSID').val("");
        $('#AP_EQUT_TYPE').val("");
        $('#AP_FREQUENCY').val("");
        $('#AP_CHANNEL').val("");
        $('#AP_FACTORY').val("");
        $('#AP_BRANDS').val("");
        $('#AP_EQUT_MODEL').val("");
        $('#AP_NOTE').val("");
        $('#AP_STATUS').val("");
    }
    $("#inputFileName" + env.elementId).val("");
    removeSvgNewPic();
    var SvgPicList = env.SvgPicListJson[env.elementId];
    $("#uploadpoipic").html((((SvgPicList != null) &&
        (SvgPicList["PIC_ID"] != null)) ? "更新" : "上传") + "图片:");
}

function ShowApInfo(elementId, layertype, ismodify){
    env.elementId = elementId;
    $("#APINFO_TITLE").html((ismodify ? "修改" : "添加") + "AP信息");
    resetApInfo();
    var popupbgdiv = document.getElementById("popupbgdiv");
    popupbgdiv.style.backgroundColor = "#000000";
    popupbgdiv.style.position = "absolute";
    popupbgdiv.style.top = 0;
    popupbgdiv.style.left = 0;
    popupbgdiv.style.opacity = 0.5;
    popupbgdiv.style.width = document.body.clientWidth + "px";
    popupbgdiv.style.height = document.body.clientHeight + "px";
    popupbgdiv.style.filter = "alpha(Opacity=20)";
    popupbgdiv.style.zIndex = 200;
    popupbgdiv.style.display = "block";
    var popupapdiv = document.getElementById("popupapdiv");
    popupapdiv.style.position = "absolute";
    popupapdiv.style.top = 250 + "px";
    popupapdiv.style.left = 230 + "px";
    //popupapdiv.style.top = document.body.clientWidth / 2 - 650 / 2
    + "px";
    //popupapdiv.style.left = document.body.clientHeight / 2 - 349 /
    2 + "px";
    //popupapdiv.style.width = 688 + "px";
    //popupapdiv.style.height = 500 + "px";
    //popupapdiv.style.height = 349 + "px";
    popupapdiv.style.zIndex = 201;
    popupapdiv.style.display = "block";
    env.elementId = elementId;
}

function hideApInfo(){
    var popupbgdiv = document.getElementById("popupbgdiv");
    popupbgdiv.style.display = "none";
    var popupapdiv = document.getElementById("popupapdiv");
    popupapdiv.style.display = "none";
    var element = svgedit.utilities.getElem(env.elementId);
    if (element.tagName == "text") {
        svgCanvas.textActions.start(element);
    }
}
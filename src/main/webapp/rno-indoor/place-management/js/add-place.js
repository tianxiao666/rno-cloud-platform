$(function () {

    /**
     * 初始化区域联动
     */
    initAreaSelectors({ selectors: ["provinceId", "cityId","areaId"]});

    /**
     * 保存触发事件
     */
    $("#saveBtn").click(function () {
         if (checkForm()){
             $("#conditionForm").submit();
         }
    });

    /**
     * AJAX 提交增加建筑物场所表单
     */
    $("#conditionForm").ajaxForm({
        url: "../../api/cb-buliding-data/cb-building-save",
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
 *渐入渐出效果
 */
function showInfoInAndOut(div, info) {
    var divSet = $("#" + div);
    divSet.html(info);
    divSet.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

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

function isIdValueMath(idinfo){
    var id = idinfo[0];
    var idval = $('#' + id).val();
    if (idval != "") {
        var reg = new RegExp(idinfo[2]);
        if (!reg.test(idval)) {
            alert(idinfo[1]);
            $('#' + id).focus();
            return (false);
        }
    }
    return (true);
}

function checkForm(){
    var re_kong = /^\s*$/;
    if ($('#BUILDING_NAME').val() == ''
        ||re_kong.test($('#BUILDING_NAME').val())) {
        alert('请填写名称');
        $('#BUILDING_NAME').focus();
        return false;
    }
    if ($('#NOTE').val() == '' ||re_kong.test($('#NOTE').val())) {
        alert('请填写简介');
        $('#NOTE').focus();
        return false;
    }
    var notEmptyIdList = new Array(new Array("BUILDING_NAME", "0",
        "名称"), new Array("CONTINENT", "1", "洲"), new Array("COUNTRY", "1",
        "国家"), new Array("PROV", "1", "省份"), new Array("CITY", "1", "城市"), new
    Array("TOTAL_FLOOR", "0", "总楼层数"), new Array("STATUS", "1", "状态"), new
    Array("BUILD_TYPE", "1", "类型"), new Array("ADDRESS", "0", "详细地址"), new
    Array("LT_LONGITUDEL", "0", "左上经度"), new Array("LT_LATITUDEL", "0",
        "左上纬度"), new Array("RB_LONGITUDEL", "0", "右下经度"), new
    Array("RB_LATITUDEL", "0", "右下纬度"), new Array("NOTE", "0", "简介"));
    var i = 0;
    while (i < notEmptyIdList.length) {
        if (isIdValueEmpty(notEmptyIdList[i])) {
            return (false);
        }
        ++i;
    }
    var onlyDigitalIdList = new Array(new Array("POSTALCODE",
        "邮编只能为小于11位的数字！", "^[0-9]{1,10}$"), new Array("LT_LONGITUDEL",
        "左上经度只能为小于180的数字！",
        "^(((([0-9]{0,2})|(0[0-9]{2})|(1[0-7][0-9]))(\\.[0-9]+)?)|180(\\.0+)?)$"),
        new Array("LT_LATITUDEL", "左上纬度只能为小于90的数字！",
            "^(((([0-8][0-9])|([0-9]))(\\.[0-9]+)?))$"), new Array("RB_LONGITUDEL",
            "右下经度只能为小于180的数字！",
            "^(((([0-9]{0,2})|(0[0-9]{2})|(1[0-7][0-9]))(\\.[0-9]+)?)|180(\\.0+)?)$"),
        new Array("RB_LATITUDEL", "右下纬度只能为小于90的数字！",
            "^(((([0-8][0-9]|([0-9])))(\\.[0-9]+)?))$"), new Array("TOTAL_FLOOR",
            "总楼层数只能为小于6位的整数！", "^[0-9]{1,5}$"), new Array("PHONE", "请输入正确的电话号码格式！",
            "^(([0-9]+(\\-[0-9]+)*)|(((\\([0-9]{3,4}\\))|([0-9]{3,4}\\-))?[0-9]{7,8}))$"));

    i = 0;
    while (i < onlyDigitalIdList.length) {
        if (!isIdValueMath(onlyDigitalIdList[i])) {
            return (false);
        }
        ++i;
    }
    return (true);
}

function doSubmit(){
    if (checkForm()) {
        $("#FORM_BUILDING").submit();
    }
}

function ajaxPost(url, params, classArray){
    $.ajax({
        type: "POST",
        url: url,
        data: params,
        dataType: "json",
        success: function(result){
            ajaxPostResult(result);
        }
    });
}
function onUploadImgChange(sender){
    var file = sender.files[0];
    if (file.type.indexOf("image") != -1) {
        return true;
    }else {
        alert('图片格式无效！');
        $('#file').val('');
        return false;
    }
}
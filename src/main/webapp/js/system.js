var editHTML;
var submitStatus=true;
var appId;
var appCode;
var appNames;
function editThis(obtn){
    if ($(obtn).text() === "rno") {
        alert("禁止修改rno！");
        return false;
    }
    editText = $(obtn).html().trim(); // 取得表格单元格的文本
    setEditHTML(editText,obtn); // 初始化控件
    $(obtn).data("oldtxt", editText);  // 将单元格原文本保存在其缓存中，便修改失败或取消时用
    $(obtn).html(editHTML); // 改变单元格内容为编辑状态
    $(obtn).removeAttr("onclick"); // 删除单元格单击事件，避免多次单击
    $("#editTd").focus();
}

//点击td转换可编辑
function setEditHTML(value, obtn) {
    if ($(obtn).attr("id") === "appDescription"){
        editHTML = '<textarea id="editTd" type="text" style="width: 100%; height: 240px;" onBlur="ok(this)" value="'
            + value + '" >' + value + '</textarea>';
    } else {
        editHTML = '<input id="editTd" type="text" onBlur="ok(this)" value="'
            + value + '" />';
    }
}

// 修改
function ok(obtn) {
    var value;
    var $obj = $(obtn).parent(); //div
    value = $(obtn).val();
    if (value === "") {
        value = "   ";
    }
    $obj.data("oldtxt", value); // 设置此单元格缓存为新数据
    $obj.html($obj.data("oldtxt"));
    $obj.attr("onclick","editThis(this)");
}

$(document).ready(function() {
    // 设置jquery ui
    //获取场景名列表
    //绑定事件
    bindEvent();
    getAppNameList();
    appId=$("#appNameList").val();
    getAppById(appId);
});

function bindEvent(){
    $("#flashtable").click(function() {
        //添加记录
       $("#addApp").bind("click",function(){
            initAppTable();
        });
        submitStatus=true;
        clearAll();
        getAppNameList();
        getAppById(appId);
    });
    //提交记录
    $("#submitalter").bind("click",function(){
        //console.log(submitStatus);
        var flag = confirm("将新增以及修改的数据提交？");
        if (flag === false){
            return false;
        }
        submitUpdataData();
    });
    //删除记录
    $("#deleteApp").click(function(){
        if (appCode === "rno"){
            alert("禁止删除rno！");
            return false;
        }
        if(submitStatus){
            var appNameListDiv = $("#appNameList");
            var flag=confirm("确定要删除《"+appNameListDiv.find("option:selected").text()+"》的场景信息吗？");
            if(flag===false) return false;
            appId=appNameListDiv.val();
            deleteAppInfo(appId);
        }
    });
    //添加记录
    $("#addApp").bind("click",function(){
        initAppTable();
    });
}

/**
 * 初始化新增界面
 */
function initAppTable(){
    var editboxDiv = $(".editbox");
    editboxDiv.html("");
    $("#appInfoTable").find("caption font").html("");
    var select=$("#appNameTd");
    select.empty();
    editboxDiv.removeAttr("onclick");
    editboxDiv.attr("onclick","editThis(this)");
    editboxDiv.html(" ");
    $("#nameInfo").html(" ");
    submitStatus=false;
    $("#addApp").unbind("click");
    $("#statusCheck").attr("checked",true);
}

/**
 * 删除系统记录
 */
function deleteAppInfo(appId){
    showOperTips("loadingDataDiv", "loadContentId", "正在删除");
    $.ajax({
        type:'delete',
        url : 'api/delete-app-by-id?appId=' + appId,
        dataType : 'text',
        success : function() {
            alert("成功删除数据！");
            getAppNameList();
            //返回第一个系统选项，id为1
            getAppById(1);
        },
        complete : function() {
            hideOperTips("loadingDataDiv");
        }
    });
}

function clearAll(){
    submitStatus=true;
    $(".oldDataTip").html("");
    $(".errTip").html("");
    $(".editbox").html("");
    var check_tdDiv = $("#check_td");
    check_tdDiv.html("");
    check_tdDiv.html('<input type="checkbox" checked id="statusCheck">');
}

/**
 * 初始化系统名称
 */
function getAppNameList(){
    $.ajax({
        type:'get',
        url : 'api/list-app-names',
        dataType : 'text',
        success : function(raw) {
            //console.log(raw);
            fillSelectList("appNameTd","appNameList",raw);
            appId=$("#appNameList").val();
        },
        complete : function() {
            appId=$("#appNameList").val();
        }
    });
}
$(document).on("change","#appNameList",function(){
    appId=$("#appNameList").val();
    getAppById(appId);
});

/**
*   根据系统名称查找系统信息
* */
function getAppById(appId){
    clearAll();
    $("#menu").attr("src","menu.html?appId="+appId);
    var data={
        'appId':appId
    };
    $.ajax({
        type:'get',
        url : 'api/get-app-by-id',
        data:data,
        dataType : 'text',
        success : function(raw) {
            showAppInfo(raw);
        }
    });
}

/**
 * 显示系统信息
 * @param raw
 */
function showAppInfo(raw){
    if (raw) {
        var data = eval("(" + raw + ")");
        appId = data['appId'];
        appCode = data['appCode'];
        if (data === null || data === undefined) {
            return;
        }
        $(".editbox").css({"width": "100%"});
        var one = data;
        $("#appName").html(getValidValue(one['appName']));
        var appCodeDiv = $("#appCode");
        appCodeDiv.html(getValidValue(one['appCode']));
        $("#appVersion").html(getValidValue(one['appVersion']));
        $("#appLogo").html(getValidValue(one['appLogo']));
        $("#appDescription").html(getValidValue(one['appDescription']));
        if (one['appStyle'] === 0){
            $("#appStyle").val(0);
        }else if (one['appStyle'] === 1){
            $("#appStyle").val(1);
        }
        if (one['appStatus'] === 0){
            $("#statusCheck").attr('checked',false);
        }else if (one['appStatus'] === 1){
            $("#statusCheck").attr("checked",true);
        }
        if (appCodeDiv.text() === 'rno'){
            $("#statusCheck").attr("disabled",true);
        }else{
            $("#statusCheck").attr("disabled",false);
        }
    }
}

/**
 * 填充下拉框
 * @param selectParentId
 * @param selectId
 * @param raw
 */
function fillSelectList(selectParentId,selectId,raw){
    var selectParent=$("#"+selectParentId);
    //清空下拉框
    selectParent.empty();
    if(raw){
        var data = eval("(" + raw + ")");
        //console.log(data);
        if (data === null || data === undefined) {
            return;
        }
        appNames = data;
        var selectStr="<select 	name='cond[appNameList]' id='"+selectId+"'></select>";
        selectParent.append(selectStr);
        var select=$("#"+selectId);
        for ( var i = 0; i < data.length; i++) {
            //console.log(data.length);
            var str = "";
            var one = "";
            one = data[i];
            str="<option value='"+getValidValue(one['appId'], '')+"'>"+getValidValue(one['appName'], '')+"</option>";
            select.append(str);
        }
        select.val(appId);
    }
}

function submitUpdataData(){
    var appDataMap;
    appDataMap={
        'name':$("#appName").html().trim(),
        'code':$("#appCode").html().trim(),
        'version':$("#appVersion").html().trim(),
        'logo':$("#appLogo").html().trim(),
        'description':$("#appDescription").html().trim(),
        'style':$("#appStyle").val()
    };
    if (submitStatus){
        appDataMap.id = appId;
    }
    if ($("#statusCheck").is(':checked')){
        appDataMap.status = 1;
    }else {
        appDataMap.status = 0;
    }
    updateAppInfo(appDataMap);
}

/**
 * 更新系统信息
 * @param appDataMap
 */
function updateAppInfo(appDataMap){
    showOperTips("loadingDataDiv", "loadContentId", "正在更新");
    $.ajax({
        type:'post',
        url : 'api/update-app',
        data:appDataMap,
        dataType : 'text',
        success : function(raw) {
            if(raw){
                alert("成功修改数据！");
                getAppNameList();
                appId = raw;
                getAppById(appId);
            }
        },
        error:function(XMLResponse){
            alert(XMLResponse.responseText);
        },
        complete : function() {
            hideOperTips("loadingDataDiv");

        }
    });
}

function showOperTips(outerId, tipId, tips) {
    try {
        var outerIdDiv = $("#" + outerId);
        outerIdDiv.css("display", "");
        outerIdDiv.find("#" + tipId).html(tips);
    } catch (err) {
    }
}

function hideOperTips(outerId) {
    try {
        $("#" + outerId).css("display", "none");
    } catch (err) {
    }
}

// ---------------------//
function getValidValue(v, defaultValue, precision) {
    if (v === null || v === undefined || v === "null" || v === "NULL"
        || v === "undefined" || v === "UNDEFINED") {
        if (defaultValue !== null && defaultValue !== undefined)
            return defaultValue;
        else
            return "";
    }

    if (typeof v === "number") {
        try {
            v = Number(v).toFixed(precision);
        } catch (err) {
            // console.error("v=" + v + "," + err);
        }
    }
    return v;
}
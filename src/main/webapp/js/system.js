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
    }else {
        editHTML = '<input id="editTd" type="text" onBlur="ok(this)" value="'
            + value + '" />';
    }
}

// 修改
function ok(obtn) {
    var value;
    var $obj = $(obtn).parent(); //div
    var spanvalue=$(obtn).parent().parent().find("span").html();
    value = $(obtn).val();
    if (value === "") {
        value = "   ";
    }
    if(value!==spanvalue){

    }else{

    }
    // alert("success");
    $obj.data("oldtxt", value); // 设置此单元格缓存为新数据
    $obj.html($obj.data("oldtxt"));
    $obj.attr("onclick","editThis(this)");
}

$(document).ready(function() {
    // 设置jquery ui
    //获取场景名列表
    //绑定事件
    bindEvent();
    //clearAll();
    getAppNameList();
    appId=$("#appNameList").val();
    getAppById(appId);

});

function bindEvent(){
    $("#flashtable").click(function() {
        /*var flag = confirm("确定刷新页面？你将放弃所有未提交的修改。");
        if (flag === false){
            return false;
        }*/
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
        chooseTask();
    });




    //删除记录
    $("#deleteApp").click(function(){
        if (appCode === "rno"){
            alert("禁止删除rno！");
            return false;
        }
        if(submitStatus){
            var flag=confirm("确定要删除《"+$("#appNameList").find("option:selected").text()+"》的场景信息吗？");
            if(flag===false) return false;
            appId=$("#appNameList").val();
            deleteAppInfo(appId);
        }
    });

    //添加记录
    $("#addApp").bind("click",function(){
        initAppTable();
    });

}

function chooseTask(){
    //console.log(submitStatus);
    //if(submitStatus){
        submitUpdataData();
    /*}else{
        //	console.log(submitStatus);
        submitInsertData();
    }*/
}


/**
 * 初始化新增界面
 */
function initAppTable(){
    $(".editbox").html("");
    $("#appInfoTable caption font").html("");

    var select=$("#appNameTd");
    select.empty();
    $(".editbox").removeAttr("onclick");
    $(".editbox").attr("onclick","editThis(this)");
    $(".editbox").html(" ");
    $("#nameInfo").html(" ");
    submitStatus=false;
    $("#addApp").unbind("click");
}


/**
 * 删除系统记录
 */
function deleteAppInfo(appId){
    showOperTips("loadingDataDiv", "loadContentId", "正在删除");
    $.ajax({
        type:'post',
        url : '/api/deleteAppById',
        data:{
            'appId':appId
        },
        dataType : 'text',
        success : function(raw) {
            if(raw){
                if(raw==="success"){
                    getAppNameList();
                    //返回第一个系统选项，id为1
                    getAppById(1);
                }
            }
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
}


/**
 * 初始化系统名称
 */
function getAppNameList(){
    /*$("#addSys").bind("click",function(){
        initSysTable();
    });*/
    //showOperTips("loadingDataDiv", "loadContentId", "正在查询");
    $.ajax({
        type:'post',
        url : '/api/get-app-names',
        dataType : 'text',
        success : function(raw) {
            //console.log(raw);
            fillSelectList("appNameTd","appNameList",raw);
            appId=$("#appNameList").val();
        },
        complete : function() {
            appId=$("#appNameList").val();
            //hideOperTips("loadingDataDiv");
        }
    });
}
//系统名称选择事件
$("#appNameList").live("change",function(){
    appId=$("#appNameList").val();
    getAppById(appId);
});

/*
*   根据系统名称查找系统信息
* */
function getAppById(appId){
    clearAll();
    var data={
        'appId':appId
    };
    $.ajax({
        type:'post',
        url : '/api/getAppById',
        data:data,
        dataType : 'text',
        success : function(raw) {
            //	console.log(raw);
            showAppInfo(raw);

        },
        complete : function() {
            //addEdit();
            //hideOperTips("loadingDataDiv");
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
        $("#appCode").html(getValidValue(one['appCode']));
        $("#appVersion").html(getValidValue(one['appVersion']));
        $("#appLogo").html(getValidValue(one['appLogo']));
        $("#appDescription").html(getValidValue(one['appDescription']));
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
        $("#"+selectId).val(appId);
    }
}

function submitUpdataData(){
//检查参数是否可以上传
    /*if(!checkSceneParam()){
        return
    }*/
    var appDataMap;
    if (submitStatus){
        appDataMap={
            'id' : appId,
            'name':$("#appName").html().trim(),
            'code':$("#appCode").html().trim(),
            'version':$("#appVersion").html().trim(),
            'logo':$("#appLogo").html().trim(),
            'description':$("#appDescription").html().trim()
        };
    }else{
        appDataMap={
            'name':$("#appName").html().trim(),
            'code':$("#appCode").html().trim(),
            'version':$("#appVersion").html().trim(),
            'logo':$("#appLogo").html().trim(),
            'description':$("#appDescription").html().trim()
        };
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
        url : '/api/updateApp',
        data:appDataMap,
        dataType : 'text',
        success : function(raw) {
            if(raw){
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

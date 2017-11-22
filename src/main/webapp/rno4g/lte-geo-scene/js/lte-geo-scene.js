//点击td转换可编辑
var editHTML;
var editText;
var submitStatus=true;
var sceneNames; //保存场景名列表


$(document).ready(function() {
    // 设置jquery ui
    //获取场景名列表
    //绑定事件
    bindEvent();
    clearAll();
    getSceneNameListTask();


});

//绑定事件
function bindEvent(){

    $("#flashtable").click(function() {
        var flag = confirm("确定刷新页面？你将放弃所有未提交的修改。");
        if (flag === false){
            return false;
        }
        $("#sceneNameTip").html("");
        //添加记录
        $("#addScene").bind("click",function(){
            initSceneTable();
        });

        submitStatus=true;
        clearOldDataTip();
        clearErrTip();
        clearAll();
        getSceneNameListTask();
    });

    //更新记录
    $("#submitalter").bind("click",function(){
        //console.log(submitStatus);
        chooseTask();
    });

    //场景名选择事件
    $("#sceneNameList").live("change",function(){
        var sceneId=$("#sceneNameList").val();
        getSceneInfoTask(sceneId);
    });
    //删除记录
    $("#deleteScene").click(function(){
        var sceneId=$("#sceneNameList").val();
        if(submitStatus){
            var flag=confirm("确定要删除《"+$("#sceneNameList").find("option:selected").text()+"》的场景信息吗？");
            if(flag===false) return false;
            deleteSceneInfoTask(sceneId);
        }
    });

    //添加记录
    $("#addScene").bind("click",function(){
        initSceneTable();
    });
}

function chooseTask(){
    //console.log(submitStatus);
    if(submitStatus){
        submitUpdataData();
    }else{
        //	console.log(submitStatus);
        submitInsertData();
    }
}

/**
 * 初始化场景名称列表
 */
function getSceneNameListTask(){
    $("#addScene").bind("click",function(){
        initSceneTable();
    });
    showOperTips("loadingDataDiv", "loadContentId", "正在查询");
    $.ajax({
        type:'post',
        url : '/api/lte-geo-scene/get-all-name',
        dataType : 'text',
        success : function(raw) {
            //console.log(raw);
            fillSelectList("sceneNameTd","sceneNameList",raw);
        },
        complete : function() {
            var sceneId=$("#sceneNameList").val();
            getSceneInfoTask(sceneId);
            hideOperTips("loadingDataDiv");
        }
    });
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
        sceneNames = data;
        var selectStr="<select 	name='cond[sceneNameList]' id='"+selectId+"'></select>";
        selectParent.append(selectStr);
        var select=$("#"+selectId);
        for ( var i = 0; i < data.length; i++) {
            //console.log(data.length);
            var str = "";
            var one = "";
            one = data[i];
            str="<option value='"+getValidValue(one['id'], '')+"'>"+getValidValue(one['name'], '')+"</option>";
            select.append(str);
        }
    }
}

/**
 * 根据场景ID取得场景详情
 * @param sceneId
 */
function getSceneInfoTask(sceneId){
    $("#scenceInfoTable caption font").html($("#sceneNameList").find("option:selected").text());
    clearAll();
    //console.log(sceneId);
    showOperTips("loadingDataDiv", "loadContentId", "正在查询");
    var data={
        'sceneId':sceneId
    };

    $.ajax({
        type:'post',
        url : '/api/lte-geo-scene/get-scene-by-id',
        data:data,
        dataType : 'text',
        success : function(raw) {
            //	console.log(raw);
            showSceneInfo(raw);
        },
        complete : function() {
            /*			doTableLock();*/
            addEdit();
            hideOperTips("loadingDataDiv");
        }
    });
}

/**
 * 显示场景信息
 * @param raw
 */
function showSceneInfo(raw){
    //console.log(raw);
    if (raw) {
        var data = eval("(" + raw + ")");
        //	console.log(data);
        if (data === null || data === undefined) {
            return;
        }
        //oldData=data;
        displaySceneinfo(data);
    }
}

/**
 * 呈现场景信息
 * @param data
 */
function displaySceneinfo(data){

    $("span.oldDataTip").css({"display":"none"});
    $(".editbox").css({"width":"100%"});
    var one = data;
    $("#interrathoa2thdrsrp").html(getValidValue(one['interrathoa2thdrsrp']));
    $("#interrathoa1thdrsrp").html(getValidValue(one['interrathoa1thdrsrp']));
    $("#interrathoutranb1hyst").html(getValidValue(one['interrathoutranb1hyst']));
    $("#interrathoutranb1thdrscp").html(getValidValue(one['interrathoutranb1thdrscp']));
    $("#interrathoa1a2timetotrig").html(getValidValue(one['interrathoa1a2timetotrig']));
    $("#interrathoa1a2hyst").html(getValidValue(one['interrathoa1a2hyst']));
    $("#blindhoa1a2thdrsrp").html(getValidValue(one['blindhoa1a2thdrsrp']));
    $("#interfreqhoa1a2timetotrig").html(getValidValue(one['interfreqhoa1a2timetotrig']));
    $("#a3interfreqhoa1thdrsrp").html(getValidValue(one['a3interfreqhoa1thdrsrp']));
    $("#a3interfreqhoa2thdrsrp").html(getValidValue(one['a3interfreqhoa2thdrsrp']));
    $("#interfreqhoa3offset").html(getValidValue(one['interfreqhoa3offset']));
    $("#interfreqhoa1a2hyst").html(getValidValue(one['interfreqhoa1a2hyst']));
    $("#qrxlevmin").html(getValidValue(one['qrxlevmin']));
    $("#snonintrasearch").html(getValidValue(one['snonintrasearch']));
    $("#thrshservlow").html(getValidValue(one['thrshservlow']));
    $("#treseleutran").html(getValidValue(one['treseleutran']));
    $("#cellreselpriority").html(getValidValue(one['cellreselpriority']));

    $("#interrathoa2thdrsrp_old").html(getValidValue(one['interrathoa2thdrsrp']));
    $("#interrathoa1thdrsrp_old").html(getValidValue(one['interrathoa1thdrsrp']));
    $("#interrathoutranb1hyst_old").html(getValidValue(one['interrathoutranb1hyst']));
    $("#interrathoutranb1thdrscp_old").html(getValidValue(one['interrathoutranb1thdrscp']));
    $("#interrathoa1a2timetotrig_old").html(getValidValue(one['interrathoa1a2timetotrig']));
    $("#interrathoa1a2hyst_old").html(getValidValue(one['interrathoa1a2hyst']));
    $("#blindhoa1a2thdrsrp_old").html(getValidValue(one['blindhoa1a2thdrsrp']));
    $("#interfreqhoa1a2timetotrig_old").html(getValidValue(one['interfreqhoa1a2timetotrig']));
    $("#a3interfreqhoa1thdrsrp_old").html(getValidValue(one['a3interfreqhoa1thdrsrp']));
    $("#a3interfreqhoa2thdrsrp_old").html(getValidValue(one['a3interfreqhoa2thdrsrp']));
    $("#interfreqhoa3offset_old").html(getValidValue(one['interfreqhoa3offset']));
    $("#interfreqhoa1a2hyst_old").html(getValidValue(one['interfreqhoa1a2hyst']));
    $("#qrxlevmin_old").html(getValidValue(one['qrxlevmin']));
    $("#snonintrasearch_old").html(getValidValue(one['snonintrasearch']));
    $("#thrshservlow_old").html(getValidValue(one['thrshservlow']));
    $("#treseleutran_old").html(getValidValue(one['treseleutran']));
    $("#cellreselpriority_old").html(getValidValue(one['cellreselpriority']));

}
//点击td转换可编辑
function setEditHTML(value) {
    editHTML = '<input id="editTd" type="text" maxlength="10" onBlur="ok(this)" value="'
        + value + '" />';

}

// 修改
function ok(obtn) {
    var $obj = $(obtn).parent(); //div
    var spanvalue=$(obtn).parent().parent().find("span").html();
    var value = $obj.find("input:text")[0].value; // 取得文本框的值，即新数据
    if (value === "") {
        value = "   ";
    }
    if(value!==spanvalue){
        $(obtn).parent().parent().find("span.oldDataTip").css({"display":"inline-block","width":"49%"});
        $(obtn).parent().css({"width":"49%"});
    }else{
        $(obtn).parent().parent().find("span.oldDataTip").css({"display":"none"});
        $(obtn).parent().css({"width":"100%"});
    }
    // alert("success");
    $obj.data("oldtxt", value); // 设置此单元格缓存为新数据
    $obj.html($obj.data("oldtxt"));
    $obj.attr("onclick","editThis(this)");
}
function editThis(obtn){
    editText = $(obtn).html().trim(); // 取得表格单元格的文本
    setEditHTML(editText); // 初始化控件
    $(obtn).data("oldtxt", editText);  // 将单元格原文本保存在其缓存中，便修改失败或取消时用
    $(obtn).html(editHTML); // 改变单元格内容为编辑状态
    $(obtn).removeAttr("onclick"); // 删除单元格单击事件，避免多次单击
    $("#editTd").focus();
}

function addEdit(){
    $("#scenceInfoTable div.editbox").attr("onclick","editThis(this)");
}

/*function doTableLock(){
	  $(function(){
	      $.fn.TableLock({table:'scenceInfoTable',lockRow:1,lockColumn:1,width:'100%',height:'300px'});
	  });
}
*/

function submitUpdataData(){
//检查参数是否可以上传
    if(!checkSceneParam()){
        return
    }

    var interrathoa2thdrsrp=$("#interrathoa2thdrsrp").html().trim();
    var interrathoa1thdrsrp=$("#interrathoa1thdrsrp").html().trim();
    var interrathoutranb1hyst=$("#interrathoutranb1hyst").html().trim();
    var interrathoutranb1thdrscp=$("#interrathoutranb1thdrscp").html().trim();
    var interrathoa1a2timetotrig=$("#interrathoa1a2timetotrig").html().trim();
    var interrathoa1a2hyst=$("#interrathoa1a2hyst").html().trim();
    var blindhoa1a2thdrsrp=$("#blindhoa1a2thdrsrp").html().trim();
    var interfreqhoa1a2timetotrig=$("#interfreqhoa1a2timetotrig").html().trim();
    var a3interfreqhoa1thdrsrp=$("#a3interfreqhoa1thdrsrp").html().trim();
    var a3interfreqhoa2thdrsrp=$("#a3interfreqhoa2thdrsrp").html().trim();
    var interfreqhoa3offset=$("#interfreqhoa3offset").html().trim();
    var interfreqhoa1a2hyst=$("#interfreqhoa1a2hyst").html().trim();
    var qrxlevmin=$("#qrxlevmin").html().trim();
    var snonintrasearch=$("#snonintrasearch").html().trim();
    var thrshservlow=$("#thrshservlow").html().trim();
    var treseleutran=$("#treseleutran").html().trim();
    var cellreselpriority=$("#cellreselpriority").html().trim();

    var sceneDataMap={
        'id':$("#sceneNameList").val(),
        'name':$("#sceneNameList").find("option:selected").text(),
        'interrathoa2thdrsrp':interrathoa2thdrsrp,
        'interrathoa1thdrsrp':interrathoa1thdrsrp,
        'interrathoutranb1hyst':interrathoutranb1hyst,
        'interrathoutranb1thdrscp':interrathoutranb1thdrscp,
        'interrathoa1a2timetotrig':interrathoa1a2timetotrig,
        'interrathoa1a2hyst':interrathoa1a2hyst,
        'blindhoa1a2thdrsrp':blindhoa1a2thdrsrp,
        'interfreqhoa1a2timetotrig':interfreqhoa1a2timetotrig,
        'a3interfreqhoa1thdrsrp':a3interfreqhoa1thdrsrp,
        'a3interfreqhoa2thdrsrp':a3interfreqhoa2thdrsrp,
        'interfreqhoa3offset':interfreqhoa3offset,
        'interfreqhoa1a2hyst':interfreqhoa1a2hyst,
        'qrxlevmin':qrxlevmin,
        'snonintrasearch':snonintrasearch,
        'thrshservlow':thrshservlow,
        'treseleutran':treseleutran,
        'cellreselpriority':cellreselpriority
    };
    updateSceneInfo(sceneDataMap);
}
/**
 * 更新场景
 * @param sceneDataMap
 */
function updateSceneInfo(sceneDataMap){
    showOperTips("loadingDataDiv", "loadContentId", "正在更新");
    $.ajax({
        type:'post',
        url : '/api/lte-geo-scene/update-scene-by-id',
        data:sceneDataMap,
        dataType : 'text',
        success : function(raw) {
            if(raw){
                if(raw==="success"){
                    //var sceneType=$("#sceneTypeList").val();
                    var sceneId=$("#sceneNameList").val();
                    getSceneInfoTask(sceneId);
                }
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
/**
 * 删除场景记录
 */
function deleteSceneInfoTask(sceneId){
    deleteSceneInfo(sceneId);
}
function deleteSceneInfo(sceneId){
    showOperTips("loadingDataDiv", "loadContentId", "正在删除");
    $.ajax({
        type:'post',
        url : '/api/lte-geo-scene/delete-scene-by-id',
        data:{
            'sceneId':sceneId
        },
        dataType : 'text',
        success : function(raw) {
            if(raw){
                if(raw==="success"){
                    getSceneNameListTask();
                }
            }
        },
        complete : function() {
            hideOperTips("loadingDataDiv");
        }
    });
}
/**
 * 初始化新增界面
 */
function initSceneTable(){
    $(".oldDataTip").html("");
    $(".errTip").html("");
    $(".editbox").html("");
    $("#sceneNameTip").html("");
    $("#scenceInfoTable caption font").html("");

    var select=$("#sceneNameTd");
    select.empty();
    var input;
    input="<input id='sceneNameInput' type='text' value='请输入场景名' onfocus='inputFocus(this)' onBlur='inputBlur(this)' />";
    select.append(input);
    $(".editbox").parent().find("span.oldDataTip").css({"display":"none"});
    $(".editbox").removeAttr("onclick");
    $(".editbox").attr("onclick","insertEdit(this)");
    $(".editbox").html(" ");
    submitStatus=false;
    $("#addScene").unbind("click");
}


function submitInsertData(){

    //检查参数是否可以上传
    if(!checkSceneParam()){
        return
    }

    var interrathoa2thdrsrp=$("#interrathoa2thdrsrp").html().trim();
    var interrathoa1thdrsrp=$("#interrathoa1thdrsrp").html().trim();
    var interrathoutranb1hyst=$("#interrathoutranb1hyst").html().trim();
    var interrathoutranb1thdrscp=$("#interrathoutranb1thdrscp").html().trim();
    var interrathoa1a2timetotrig=$("#interrathoa1a2timetotrig").html().trim();
    var interrathoa1a2hyst=$("#interrathoa1a2hyst").html().trim();
    var blindhoa1a2thdrsrp=$("#blindhoa1a2thdrsrp").html().trim();
    var interfreqhoa1a2timetotrig=$("#interfreqhoa1a2timetotrig").html().trim();
    var a3interfreqhoa1thdrsrp=$("#a3interfreqhoa1thdrsrp").html().trim();
    var a3interfreqhoa2thdrsrp=$("#a3interfreqhoa2thdrsrp").html().trim();
    var interfreqhoa3offset=$("#interfreqhoa3offset").html().trim();
    var interfreqhoa1a2hyst=$("#interfreqhoa1a2hyst").html().trim();
    var qrxlevmin=$("#qrxlevmin").html().trim();
    var snonintrasearch=$("#snonintrasearch").html().trim();
    var thrshservlow=$("#thrshservlow").html().trim();
    var treseleutran=$("#treseleutran").html().trim();
    var cellreselpriority=$("#cellreselpriority").html().trim();

    var sceneDataMap={
        'name':$("#sceneNameInput").val().trim(),
        //'sceneParam.sceneType':$("#sceneTypeList").val(),
        'interrathoa2thdrsrp':interrathoa2thdrsrp,
        'interrathoa1thdrsrp':interrathoa1thdrsrp,
        'interrathoutranb1hyst':interrathoutranb1hyst,
        'interrathoutranb1thdrscp':interrathoutranb1thdrscp,
        'interrathoa1a2timetotrig':interrathoa1a2timetotrig,
        'interrathoa1a2hyst':interrathoa1a2hyst,
        'blindhoa1a2thdrsrp':blindhoa1a2thdrsrp,
        'interfreqhoa1a2timetotrig':interfreqhoa1a2timetotrig,
        'a3interfreqhoa1thdrsrp':a3interfreqhoa1thdrsrp,
        'a3interfreqhoa2thdrsrp':a3interfreqhoa2thdrsrp,
        'interfreqhoa3offset':interfreqhoa3offset,
        'interfreqhoa1a2hyst':interfreqhoa1a2hyst,
        'qrxlevmin':qrxlevmin,
        'snonintrasearch':snonintrasearch,
        'thrshservlow':thrshservlow,
        'treseleutran':treseleutran,
        'cellreselpriority':cellreselpriority
    };
    insertSceneInfo(sceneDataMap);
}
function insertSceneInfo(sceneDataMap){
    showOperTips("loadingDataDiv", "loadContentId", "正在插入");
    $.ajax({
        type:'post',
        url : '/api/lte-geo-scene/insert-scene',
        data:sceneDataMap,
        dataType : 'text',
        success : function(raw) {
            if(raw){
                var status = raw;
                if(status==="success"){
                    getSceneNameListTask();
                }else if(status==="success"){

                }
            }
        },
        complete : function() {
            /*			doTableLock();*/
            hideOperTips("loadingDataDiv");
        }
    });
}

function checkSceneParam(){

    $("span.errTip").css({"display":"inline-block","width":"49%"});
    $(".editbox").css({"display":"inline-block","width":"49%"});
    clearErrTip();
    var interrathoa2thdrsrp=$("#interrathoa2thdrsrp").html().trim();
    var interrathoa1thdrsrp=$("#interrathoa1thdrsrp").html().trim();
    var interrathoutranb1hyst=$("#interrathoutranb1hyst").html().trim();
    var interrathoutranb1thdrscp=$("#interrathoutranb1thdrscp").html().trim();
    var interrathoa1a2timetotrig=$("#interrathoa1a2timetotrig").html().trim();
    var interrathoa1a2hyst=$("#interrathoa1a2hyst").html().trim();
    var blindhoa1a2thdrsrp=$("#blindhoa1a2thdrsrp").html().trim();
    var interfreqhoa1a2timetotrig=$("#interfreqhoa1a2timetotrig").html().trim();
    var a3interfreqhoa1thdrsrp=$("#a3interfreqhoa1thdrsrp").html().trim();
    var a3interfreqhoa2thdrsrp=$("#a3interfreqhoa2thdrsrp").html().trim();
    var interfreqhoa3offset=$("#interfreqhoa3offset").html().trim();
    var interfreqhoa1a2hyst=$("#interfreqhoa1a2hyst").html().trim();
    var qrxlevmin=$("#qrxlevmin").html().trim();
    var snonintrasearch=$("#snonintrasearch").html().trim();
    var thrshservlow=$("#thrshservlow").html().trim();
    var treseleutran=$("#treseleutran").html().trim();
    var cellreselpriority=$("#cellreselpriority").html().trim();

    var reg = /^[-+]?[0-9]+(\.[0-9]+)?$/; // 验证数字
    var flag = true;
    if(!submitStatus){
        var sceneName = $("#sceneNameInput").val().trim();
        $("#sceneNameTip").html("");
        if(sceneName==="请输入场景名"){
            $("#sceneNameTip").html("<span style='color: red;font-weight: bold;'>请输入场景名！<span>");
            flag=false;
        }else{
            for(var i = 0; i<sceneNames.length;i++){
                if(sceneNames[i].NAME===sceneName){
                    $("#sceneNameTip").html("<span style='color: red;font-weight: bold;'>场景名已存在！<span>");
                    flag=false;
                }
            }
        }
    }

    if (!reg.test(interrathoa2thdrsrp)) {
        $("#interrathoa2thdrsrp_err").html("※请输入数字※");
        flag = false;
    }else{
        $("#interrathoa2thdrsrp_err").html("");
        $("#interrathoa2thdrsrp_err").css({"display":"none"});
        $("#interrathoa2thdrsrp").css({"width":"100%"});
    }
    if (!reg.test(interrathoa1thdrsrp)) {
        $("#interrathoa1thdrsrp_err").html("※请输入数字※");
        flag = false;
    }else{
        $("span#interrathoa1thdrsrp_err").html("");
        $("#interrathoa1thdrsrp_err").css({"display":"none"});
        $("#interrathoa1thdrsrp").css({"width":"100%"});
    }
    if (!reg.test(interrathoutranb1hyst)) {
        $("span#interrathoutranb1hyst_err").html("※请输入数字※");
        flag = false;
    }else{
        $("span#interrathoutranb1hyst_err").html("");
        $("#interrathoutranb1hyst_err").css({"display":"none"});
        $("#interrathoutranb1hyst").css({"width":"100%"});
    }
    if (!reg.test(interrathoutranb1thdrscp)) {
        $("span#interrathoutranb1thdrscp_err").html("※请输入数字※");
        flag = false;
    }else{
        $("span#interrathoutranb1thdrscp_err").html("");
        $("#interrathoutranb1thdrscp_err").css({"display":"none"});
        $("#interrathoutranb1thdrscp").css({"width":"100%"});
    }
    if (!reg.test(interrathoa1a2timetotrig)) {
        $("span#interrathoa1a2timetotrig_err").html("※请输入数字※");
        flag = false;
    }else{
        $("span#interrathoa1a2timetotrig_err").html("");
        $("#interrathoa1a2timetotrig_err").css({"display":"none"});
        $("#interrathoa1a2timetotrig").css({"width":"100%"});
    }
    if (!reg.test(interrathoa1a2hyst)) {
        $("span#interrathoa1a2hyst_err").html("※请输入数字※");
        flag = false;
    }else{
        $("span#interrathoa1a2hyst_err").html("");
        $("#interrathoa1a2hyst_err").css({"display":"none"});
        $("#interrathoa1a2hyst").css({"width":"100%"});
    }
    if (!reg.test(blindhoa1a2thdrsrp)) {
        $("span#blindhoa1a2thdrsrp_err").html("※请输入数字※");
        flag = false;
    }else{
        $("span#blindhoa1a2thdrsrp_err").html("");
        $("#blindhoa1a2thdrsrp_err").css({"display":"none"});
        $("#blindhoa1a2thdrsrp").css({"width":"100%"});
    }
    if (!reg.test(interfreqhoa1a2timetotrig)) {
        $("span#interfreqhoa1a2timetotrig_err").html("※请输入数字※");
        flag = false;
    }else{
        $("span#interfreqhoa1a2timetotrig_err").html("");
        $("#interfreqhoa1a2timetotrig_err").css({"display":"none"});
        $("#interfreqhoa1a2timetotrig").css({"width":"100%"});
    }
    if (!reg.test(a3interfreqhoa1thdrsrp)) {
        $("span#a3interfreqhoa1thdrsrp_err").html("※请输入数字※");
        flag = false;
    }else{
        $("span#a3interfreqhoa1thdrsrp_err").html("");
        $("#a3interfreqhoa1thdrsrp_err").css({"display":"none"});
        $("#a3interfreqhoa1thdrsrp").css({"width":"100%"});
    }
    if (!reg.test(a3interfreqhoa2thdrsrp)) {
        $("span#a3interfreqhoa2thdrsrp_err").html("※请输入数字※");
        flag = false;
    }else{
        $("span#a3interfreqhoa2thdrsrp_err").html("");
        $("#a3interfreqhoa2thdrsrp_err").css({"display":"none"});
        $("#a3interfreqhoa2thdrsrp").css({"width":"100%"});
    }
    if (!reg.test(interfreqhoa3offset)) {
        $("span#interfreqhoa3offset_err").html("※请输入数字※");
        flag = false;
    }else{
        $("span#interfreqhoa3offset_err").html("");
        $("#interfreqhoa3offset_err").css({"display":"none"});
        $("#interfreqhoa3offset").css({"width":"100%"});
    }
    if (!reg.test(interfreqhoa1a2hyst)) {
        $("span#interfreqhoa1a2hyst_err").html("※请输入数字※");
        flag = false;
    }else{
        $("span#interfreqhoa1a2hyst_err").html("");
        $("#interfreqhoa1a2hyst_err").css({"display":"none"});
        $("#interfreqhoa1a2hyst").css({"width":"100%"});
    }
    if (!reg.test(qrxlevmin)) {
        $("span#qrxlevmin_err").html("※请输入数字※");
        flag = false;
    }else{
        $("span#qrxlevmin_err").html("");
        $("#qrxlevmin_err").css({"display":"none"});
        $("#qrxlevmin").css({"width":"100%"});
    }
    if (!reg.test(snonintrasearch)) {
        $("span#snonintrasearch_err").html("※请输入数字※");
        flag = false;
    }else{
        $("span#snonintrasearch_err").html("");
        $("#snonintrasearch_err").css({"display":"none"});
        $("#snonintrasearch").css({"width":"100%"});
    }
    if (!reg.test(thrshservlow)) {
        $("span#thrshservlow_err").html("※请输入数字※");
        flag = false;
    }else{
        $("span#thrshservlow_err").html("");
        $("#thrshservlow_err").css({"display":"none"});
        $("#thrshservlow").css({"width":"100%"});
    }
    if (!reg.test(treseleutran)) {
        $("span#treseleutran_err").html("※请输入数字※");
        flag = false;
    }else{
        $("span#treseleutran_err").html("");
        $("#treseleutran_err").css({"display":"none"});
        $("#treseleutran").css({"width":"100%"});
    }
    if (!reg.test(cellreselpriority)) {
        $("span#cellreselpriority_err").html("※请输入数字※");
        flag = false;
    }else{
        $("span#cellreselpriority_err").html("");
        $("#cellreselpriority_err").css({"display":"none"});
        $("#cellreselpriority").css({"width":"100%"});
    }
    if(!flag)alert("请按提示输入");
    return flag;
}
function clearErrTip(){
    $(".errTip").html("");
}
function clearOldDataTip(){
    $(".oldDataTip").html("");
}
function inputFocus(obtn){
    if($(obtn).val()==="请输入场景名"){
        $(obtn).val("");
    }
}
function inputBlur(obtn){
    if($(obtn).val().trim()===""){
        $(obtn).val("请输入场景名");
    }
}
function clearAll(){
    submitStatus=true;
    $(".oldDataTip").html("");
    $(".errTip").html("");
    $(".editbox").html("");
    $("#sceneNameTip").html("");
}


function showOperTips(outerId, tipId, tips) {
    try {
        $("#" + outerId).css("display", "");
        $("#" + outerId).find("#" + tipId).html(tips);
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

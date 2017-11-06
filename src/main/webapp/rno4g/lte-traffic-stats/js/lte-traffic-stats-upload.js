var fileSize = 0;
var stopQueryProgress=false;///停止查询进度
$(function () {
    //浏览文件绑定事件
    $("#fileid").change(function(){
        var filename = fileid.value;
        if(!(filename.toUpperCase().endsWith(".CSV")||filename.toUpperCase().endsWith(".XLS")
                || filename.toUpperCase().endsWith(".XLSX") || filename.toUpperCase().endsWith(".XML")
                || filename.toUpperCase().endsWith(".TXT") || filename.toUpperCase().endsWith(".ZIP"))){
            $("#fileDiv").html("不支持该类型文件！");
            return false;
        }else {
            $("#fileDiv").html("");
        }
    });
    //导入
    $("#importBtn").click(function() {
        var filename = fileid.value;
        if ($("#fileid").val() === ""||(!$("#fileid").val())) {
            //$("#fileid").parent().append("<font id='err' color='red'>请选择网络统计文件</font>");
            $("#fileDiv").html("请选择网络统计文件！");
            return;
        }
        if(!(filename.toUpperCase().endsWith(".CSV")||filename.toUpperCase().endsWith(".XLS")
                ||filename.toUpperCase().endsWith(".XLSX")||filename.toUpperCase().endsWith(".XML")
                ||filename.toUpperCase().endsWith(".TXT")||filename.toUpperCase().endsWith(".ZIP"))){
            return false;
        }
        var flag = confirm("是否导入文件？");
        if (flag == false){
            return false;
        }
        $("#uploadMsgDiv").css("display","none");
        stopQueryProgress=false;
        doFileUpload();
    });
    //切换区域时，赋值给uploadCityId
    $("#city-menu-1").change(function() {
        $("#uploadCityId").val($("#city-menu-1").val());
    });
});

// 上传
function doFileUpload() {
    $.ajax({
        url: "/api/lte-traffic-stats/uploadFile",
        type: "POST",
        data: new FormData($("#formImportData")[0]),
        enctype: 'multipart/form-data',
        processData: false,
        contentType: false,
        cache: false,
        success: function () {
            // Handle upload success
            $("#upload-file-message").html("上传成功");
        },
        error: function () {
            // Handle upload error
            $("#upload-file-message").html("上传失败");
        }
    });
}
var createDate;
var updateDate;
var userId;
$(document).ready(function () {
    /**
     * 绑定账户和密码界面的点击功能切换
     */
    $("#toUser").click(function () {
        $("#toUser").addClass("active");
        $("#toPassword").removeClass("active");
        $("#headName").html("编辑账户");
        $("#userInfoPage").attr("hidden",false);
        $("#passwordPage").attr("hidden",true);
    });
    $("#toPassword").click(function () {
        $("#toPassword").addClass("active");
        $("#toUser").removeClass("active");
        $("#headName").html("修改密码");
        $("#passwordPage").attr("hidden",false);
        $("#userInfoPage").attr("hidden",true);
    });
    //初始化区域联动
    initAreaSelectors({selectors: ["province", "city","district"]});
    //展示用户信息
    getCurrentUserInfo();


    $("#passwordPage").validate({
        rules: {
            oldPassword: "required",
            newPassword: "required",
            againPassword: {
                required: true,
                equalTo: '#newPassword'
            }
        },
        messages: {
            oldPassword: {
                required: '<em style=\"color:red\">请输入原密码</em>',
                equalTo: '<em style=\"color:red\">两次密码不一致</em>'
            },
            newPassword: '<em style=\"color:red\">请输入新密码</em>',
            againPassword: {
                required: '<em style=\"color:red\">请再次输入密码</em>',
                equalTo: '<em style=\"color:red\">两次密码不一致</em>'
            }
        },
        submitHandler : function(form) {
            $(form).ajaxSubmit({
                success:function(message){
                    if (message === true){
                        $("#successChangeMsg").show();
                    }else {
                        $("#oldPasswordMessage").show();
                        $("#oldPassword").focus();
                    }


                }
            });
        }
    });

    $("passwordPage").submit(function(){
        alert("Submitted");
    });

});

function hideErrorPasswordMsg() {
    $("#oldPasswordMessage").hide();
}
function saveUser(){
    var userDataMap;
    userDataMap={
        'id':userId,
        'username':$("#username").val(),
        'fullName':$("#fullName").val(),
        'email':$("#email").val(),
        'phoneNumber':$("#phoneNumber").val(),
        'createdUser':$("#createdUser").val(),
        'createdDate':new Date(createDate),
        'lastModifiedUser':$("#lastModifiedUser").val(),
        'lastModifiedDate':new Date(updateDate),
        'defaultArea':$("#district").val()
    };
    updateUserInfo(userDataMap);

}

function cancelChangePassword() {
    $("#oldPassword").val('');
    $("#newPassword").val('');
    $("#againPassword").val('');
}

function updateUserInfo(user){
    $.ajax({
        type:'post',
        url : '/api/update-user',
        data:user,
        dataType : 'text',
        success : function(raw) {
            if (!raw) {
                alert("保存失败！");
                getCurrentUserInfo();
            } else {
                alert("保存成功！");
                getCurrentUserInfo();
            }
        }
    });
}

function getCurrentUserInfo(){
    $.ajax({
        type:'get',
        url : '/api/get-current-user',
        dataType : 'text',
        success : function(raw) {
            showUserInfo(raw);
        },
        complete : function() {
        }
    });
}
function showUserInfo(raw) {
    if (raw) {
        var data = eval("(" + raw + ")");
        if (data === null || data === undefined) {
            return;
        }
        var one = data;
        userId = one['id'];
        createDate = one['createdDate'];
        updateDate = one['lastModifiedDate'];
        $("#username").val(one['username']);
        $("#fullName").val(one['fullName']);
        $("#email").val(one['email']);
        $("#phoneNumber").val(one['phoneNumber']);
        $("#createdUser").val(one['createdUser']);
        $("#createdDate").val((new Date(one['createdDate'])).Format("yyyy-MM-dd hh:mm"));
        $("#lastModifiedUser").val(one['lastModifiedUser']);
        $("#lastModifiedDate").val((new Date(one['lastModifiedDate'])).Format("yyyy-MM-dd hh:mm"));
        var districtInfo = eval("(" + getAreaById(one['defaultArea']) + ")");
        var cityInfo = eval("(" + getAreaById(districtInfo['parentId']) + ")");
        var provinceDiv = $("#province");
        provinceDiv.val(cityInfo['parentId']);
        renderArea(provinceDiv.val(),"city",false);
        var cityDiv = $("#city");
        cityDiv.val(cityInfo['id']);
        renderArea(cityDiv.val(),"district",false);
        $("#district").val(one['defaultArea']);

        /*var cityDiv = $("#city");
        cityDiv.val(districtInfo['parentId']);
        renderArea(cityDiv.val(),"district",false);
        $("#district").val(one['defaultArea']);*/


      /* var cityInfo = eval("(" + getAreaById(one['defaultArea']) + ")");
       var provinceDiv = $("#province");
        provinceDiv.val(cityInfo['parentId']);
        renderArea(provinceDiv.val(),"city",false);
       $("#city").val(one['defaultArea']);*/
    }


}
Date.prototype.Format = function(fmt){
    //author: Shf
    var o = {
        "M+" : this.getMonth()+1,                 //月份
        "d+" : this.getDate(),                    //日
        "h+" : this.getHours(),                   //小时
        "m+" : this.getMinutes(),                 //分
        "s+" : this.getSeconds(),                 //秒
        "q+" : Math.floor((this.getMonth()+3)/3), //季度
        "S"  : this.getMilliseconds()             //毫秒
    };
    if(/(y+)/.test(fmt)){
        fmt = fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    }

    for(var k in o){
        if(new RegExp("("+ k +")").test(fmt)){
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length===1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
        }
    }
    return fmt;
};
function reload() {
    getCurrentUserInfo();
}

function getAreaById(id) {
    var result = null;
    $.ajax({
        type:'get',
        async: false,
        url : '/api/get-area-by-id?id=' + id,
        dataType : 'text',
        success : function(raw) {
            result = raw;
        }
    });
    return result;
}




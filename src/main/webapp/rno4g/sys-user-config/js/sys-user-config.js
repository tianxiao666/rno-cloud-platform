$(document).ready(function () {
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
    })
});
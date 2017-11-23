$(function(){
    var userMessage = $("#userMessage");
    userMessage.addClass("active");
	$("#changePasswordForm").validate({
		rules:{
		   oldPassword:"required",
		   newPassword:"required",
		   again:{
			   required:true,
			   equalTo:'#pwd1'
		   }
		},
        messages: {
            oldPassword: '<em style=\"color:red\">请输入原密码</em>',
            newPassword: '<em style=\"color:red\">请输入新密码</em>',
            again:{
                required : '<em style=\"color:red\">请再次输入密码</em>',
                equalTo : '<em style=\"color:red\">两次密码不一致</em>'
			}
        },

		submitHandler : function(form) {
			$(form).ajaxSubmit({
				success:function(d){
					var data=eval("("+d+")");
					form.reset();
					if(data['flag']===true){
						alert("修改成功");
					}else{
						alert("修改失败！原因："+data['msg']);
					}
				}
			});
		}
	});
    userMessage.click(function () {
        $("#changePasswordForm").hide();
        $("#personalInfoForm").show();
        $("#userMessage").addClass("active");
        $("#userPassword").removeClass("active");
    });
    $("#userPassword").click(function () {
        $("#changePasswordForm").show();
        $("#personalInfoForm").hide();
        $("#userMessage").removeClass("active");
        $("#userPassword").addClass("active");
    });



    $(".modify_personal_info").click(function() {
        $(".modified_person_info").hide();
        $(".now_person_info").show();
        //保存、取消按钮可用
        $("#saveBtn").removeAttr("disabled");
        $("#cancelBtn").removeAttr("disabled");
        $("#backUpEmailAddress").show();
        $("#cellPhoneNumber").show();
        //拷贝值 2015-8-24 cc修改 去掉不再提供修改的项
        //$("#emailAddress").val($("#hidden_emailAddress").val());
        $("#backUpEmailAddress").val($("#hidden_backUpEmailAddress").val());
        $("#cellPhoneNumber").val($("#hidden_cellPhoneNumber").val());
        //$("#mobileEmailAddress").val($("#hidden_mobileEmailAddress").val());
    });
    $(".cancel_personal_info").click(function() {
        $(".now_person_info").hide();
        $(".modified_person_info").show();
    });

    $("#personalInfoForm").validate( {
        debug : true,
        focusCleanup : false,
        errorClass : "error",
        validClass : "success",
        success : function(label) {
            label.addClass("repeat_info_right").text("Ok!");
        },
        submitHandler : function(form) {
            $(form).ajaxSubmit({
                success:function(d){
                    var data=eval("("+d+")");
                    if(data['flag']===true){
                        alert("修改成功");
                        window.location.href="loadSelfServiceInfoViewAndEditAction";
                    }else{
                        alert("修改失败！");
                    }
                }
            });
        }
    });
});

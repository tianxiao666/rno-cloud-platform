
var interval = 2000;// 周期性获取文件导出进度情况的

$(document).ready(function() {

	//数据导出点击事件
	$("#exportBtn").click(function(){
		exportData();
	});

});

/**
 * 导出爱立信参数一致性结果数据
 */
function exportData() {
	
	$("#progressDesc").html("");
	//获取所选择的菜单项
	var items = "";
	$('input:checkbox[name="twoChk"]:checked').each(function(){  
		items += $(this).val() + ",";
	});
	items = items.substr(0, items.length - 1);
	if(items.length == 0) {
		alert("请选择要查询的项目！");
		return;
	}
	
	//获取bsc
	var bscStr = $("#bscStr").val();
	if(bscStr == "") {
		alert("请选择BSC！");
		return;
	}
	//获取日期
	var date1 = $("#date1").val();
	if(date1 == "") {
		alert("请选择日期！");
		return;
	}
	//获取cityId
	var cityId = $("#cityId").val();
	//获取设置
	var numTest = /^([0-9]+)$/; //检查数字
	
	var isCheckMaxChgr = false;
	if($("#isCheckMaxChgr").is(':checked')) { 
		isCheckMaxChgr = true;
	}
	
	var isCheckBaNum = false;
	if($("#isCheckBaNum").is(':checked')) { 
		isCheckBaNum = true;
	}
	var maxNum = $("#MAXNUM").val();
	var minNum = $("#MINNUM").val();
	if(isCheckBaNum) {
		if(maxNum == null || minNum == null || !numTest.test(maxNum) 
			|| !numTest.test(minNum) || Number(minNum)>Number(maxNum)) {
			alert("BA表个数检查的查询设置不符合要求,请重新设置");
			return;
		}
	}
	
	var isCheckCoBsic = false;
	if($("#isCheckCoBsic").is(':checked')) { 
		isCheckCoBsic = true;
	}
	var distance = $("#DISTANCE").val();
	if(isCheckCoBsic) {
		if(distance == null || !numTest.test(distance)) {
			alert("同频同bsic检查的查询设置不符合要求,请重新设置");
			return;
		}
	}
	
	var isCheckNcellNum = false;
	if($("#isCheckNcellNum").is(':checked')) { 
		isCheckNcellNum = true;
	}
	var ncell_maxNum = $("#NCELL_MAXNUM").val();
	var ncell_minNum = $("#NCELL_MINNUM").val();
	if(isCheckNcellNum) {
		if(ncell_minNum == null || ncell_minNum == null || !numTest.test(ncell_maxNum) 
			|| !numTest.test(ncell_maxNum)) {
			alert("邻区过多过少检查的查询设置不符合要求,请重新设置");
			return;
		}
	}
	
	showOperTips("loadingDataDiv", "loadContentId", "正在执行导出");
	$.ajax({
		url : 'exportEriCellCheckDataAjaxForAction',
		data : {
			'bscStr' : bscStr,
			'date1' : date1,
			'items' : items,
			'cityId' : cityId,
			'settings.isCheckMaxChgr' : isCheckMaxChgr,
			'settings.isCheckBaNum' : isCheckBaNum,
			'settings.MAXNUM' : maxNum,
			'settings.MINNUM' : minNum,
			'settings.isCheckCoBsic' : isCheckCoBsic,
			'settings.DISTANCE' : distance,
			'settings.isCheckNcellNum' : isCheckNcellNum,
			'settings.NCELL_MAXNUM' : ncell_maxNum,
			'settings.NCELL_MINNUM' : ncell_minNum
		},
		type : 'post',
		dataType : 'text',
		success : function(raw) {
			var data = eval("("+raw+")");
			var msg = data["msg"];
			var token = data["token"];
			//console.log(token);
			if(token != "-1") {
				//轮询进度
				queryExportProgress(token);
			} else {
				alert(msg);	
				hideOperTips("loadingDataDiv");
				$("#progressDesc").html("");
			}
		},
		complete:function(){
			
		}
	})
}

/**
 * 查询导出进度
 */
function queryExportProgress(token) {
	$.ajax({
		url : 'queryExportProgressAjaxForAction',
		data : {
			'token' : token
		},
		type : 'post',
		dataType : 'text',
		success : function(raw) {
			var obj = eval("(" + raw + ")");
			if(obj['fail']) {
				//导出失败
				alert(obj['msg']);
				hideOperTips("loadingDataDiv");
				$("#progressDesc").html("");
				return;
			}
			if (obj['finished']) {
				//完成
				$("#progressDesc").html("生成文件完成");
				downloadDataFile(token);
				hideOperTips("loadingDataDiv");
				$("#progressDesc").html("");
			} else {
				//未完成
				//console.log(obj['msg']);
				$("#progressDesc").html(obj['msg']);
				window.setTimeout(function() {
					queryExportProgress(token);
				}, interval);
			}
		},
		complete:function(){
		}
	});
}

/**
 * 下载爱立信小区参数对比结果文件
 */
function downloadDataFile(token) {
	var form = $("#downloadEriCellAnalysisDataFileForm");
	form.find("input#token").val(token);
	form.submit();
}

/*
 * 加载参数树形菜单
 */


/**
 * 显示需要检查的tab
 */

/**
 * tab加载数据事件
 */


/**
 * 缓存数据
 */

/**
 * 建立table的标题
 */


/**
 * 加载table的tr
 * @param checkType 检查类型
 * @param isAll 是否加载全部
 * @param start 开始数
 * @param end 结束数
 */






/**
 * 加载BSC
 */



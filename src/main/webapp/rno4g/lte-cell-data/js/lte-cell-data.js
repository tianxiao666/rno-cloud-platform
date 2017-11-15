var chineseToCode = [{
    'code': 'cellId',
    'name': '小区ID'
}, {
    'code': 'eci',
    'name': 'ECI'
}, {
    'code': 'manufacturer',
    'name': '制造商'
}, {
    'code': 'tac',
    'name': 'TAC'
}, {
    'code': 'bandType',
    'name': '频带类型'
}, {
    'code': 'bandWidth',
    'name': '带宽'
}, {
    'code': 'earfcn',
    'name': '下行频点'
}, {
    'code': 'pci',
    'name': 'PCI'
}, {
    'code': 'coverType',
    'name': '覆盖类型'
}, {
    'code': 'coverScene',
    'name': '覆盖场景'
}, {
    'code': 'longitude',
    'name': '经度'
}, {
    'code': 'latitude',
    'name': '纬度'
}, {
    'code': 'azimuth',
    'name': '方位角'
}, {
    'code': 'eDowntilt',
    'name': '电子下倾角'
}, {
    'code': 'mDowntilt',
    'name': '机械下倾角'
}, {
    'code': 'totalDowntilt',
    'name': '总下倾角'
}, {
    'code': 'antennaHeight',
    'name': '天线挂高'
}, {
    'code': 'remoteCell',
    'name': '拉远小区'
}, {
    'code': 'relatedParam',
    'name': '关联状态库工参'
}, {
    'code': 'relatedResoure',
    'name': '关联状态库资源'
}, {
    'code': 'stationSpace',
    'name': '站间距'
}];

//显示同一站的小区
var currentCellArray = null;
var currentIndex = 0;
//校验修改小区
var submitOK = true;

$(function () {
    //tab选项卡
    $("#tabs").tabs();//项目服务范围类别切换
    //可拖拽对话框
    $(".dialog2").draggable();
    //查询前输入校验
    $("#queryBtn").click(function () {
        $(".loading").show();
        var reg = /^[0-9]+.?[0-9]*$/;
        var pci = $("#pci").val();
        if (!reg.test(pci) && pci.trim() !== '') {
            showInfoInAndOut("info", "PCI值必须为数字");
            return false;
        }
        if (pci.length > 10 && pci.trim() !== '') {
            showInfoInAndOut("info", "PCI值输入过长");
            return false;
        }
    });

    //查询小区信息
    $("#conditionForm").ajaxForm({
        url: "/api/lte-cell-data/cell-query",
        success: showQueryList,
        error: function (err) {
            console.log(err);
        }
    });

    //初始化区域
    initAreaSelectors({selectors: ["provinceId", "cityId"]});
    initAreaSelectors({selectors: ["provinceId2", "cityId2"]});
    initAreaSelectors({selectors: ["provinceId3", "cityId3"]});

    // 执行 laydate 实例 
    laydate.render({elem: '#begUploadDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endUploadDate', value: new Date()});
    laydate.render({elem: '#beginJobDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endJobDate', value: new Date()});

    //显示隐藏导入窗口
    $("#importTitleDiv").click(function () {
        var flag = $("#importDiv").is(":hidden");//是否隐藏
        if (flag) {
            $(".importContent").show("fast");
        } else {
            $(".importContent").hide("fast");
        }
    });

    //下一小区详情
    $("#nextCellDetailBtn").click(function () {
        ++currentIndex;
        if (currentIndex >= currentCellArray.length) {
            currentIndex = 0;
        }
        showCellDetail(currentIndex);
    });

    //重置当前编辑lte小区详情
    $("#btnReset").click(function () {
        var cellId = $("#lteCellIdForEdit").val();
        //加载到页面重置数据
        showEditor(cellId);
    });

    $("#btnUpdate").click(function () {
        $(".loading").show();
        //提交前再做一次全局检验
        var length = chineseToCode.length;
        for (var i = 0; i < length; i++) {
            var oneKey = chineseToCode[i];
            var code = oneKey['code'];      //元素id值
            var value = $("#" + code).val(); //元素value值
            //如果submitOK为true，继续验证下一个元素，反之则不再循环
            if (submitOK === true) {
                checkDateType(code, value);
            } else {
                break;
            }
        }
        updateLteCellDetail(submitOK);
    });

    // AJAX 上传文件
    var progress = $('.upload-progress');
    var bar = $('.bar');
    var percent = $('.percent');

    //导入文件类型判断
    $("#importBtn").click(function () {
        var path =$("#file").val();
        var cityId = $("#cityId2").val();
        $("#areaId").val(cityId);
        var format = path.substring(path.lastIndexOf("."), path.length).toLowerCase();
        if (format !== '.csv' && format !=='.zip') {
            showInfoInAndOut("info", "请上传csv或zip格式的小区文件");
            return false;
        }
    });

    // 当上传文件域改变时，隐藏进度条
    $("input[name='file']").change(function () {
        progress.css("display", "none");
    });

    //上传
    $("#file-upload-form").ajaxForm({
        url: "/api/lte-cell-data/upload-file",
        beforeSend: function () {
            progress.css("display", "block");
            var percentVal = '0%';
            bar.width(percentVal);
            percent.html(percentVal);
        },
        uploadProgress: function (event, position, total, percentComplete) {
            var percentVal = percentComplete + '%';
            bar.width(percentVal);
            percent.html(percentVal);
        },
        success: function () {
            var percentVal = '100%';
            bar.width(percentVal);
            percent.html(percentVal);
            $("#info").css("background","green");
            showInfoInAndOut("info","文件导入成功！");
            $("#import-query-form").submit();
        }
    });

    // AJAX 查询导入记录
    $("#import-query-form").ajaxForm({
        url: "/api/lte-cell-data/query-import",
        success: showImportRecord
    });

    //查询数据记录
    $("#searchRecordForm").ajaxForm({
       url: "/api/lte-cell-data/query-record",
        success: showRecord
    });

});

function showQueryList(data) {
    $(".loading").css("display", "none");
    if (data.length === 0) {
        showInfoInAndOut('info', '没有符合条件的小区数据');
        return false;
    }
    console.log(data);
    $('#queryResultTab').css("line-height", "12px").DataTable({
        "data": data,
        // /api/lte-cell-data/cell-query
        "columns": [
            {"data": "areaName"},
            {"data": "cellId"},
            {"data": "cellName"},
            {"data": "pci"},
            {"data": "bandWidth"},
            {"data": "earfcn"},
            {"data": "azimuth"},
            {"data": null}
        ],
        "columnDefs": [{
            "render": function (row) {
                var cellId = row['cellId'];
                return "<a onclick=\"showDetail('" + cellId + "')\">查询详情</a>&nbsp;&nbsp;&nbsp;"
                    + "<a onclick=\"showEditor('" + cellId + "')\">编辑</a>&nbsp;&nbsp;&nbsp;"
                    + "<a onclick=\"deleteCell('" + cellId + "')\">删除</a>";
            },
            "targets": -1,
            "data": null
        }
        ],
        // "lengthChange": false,
        // "ordering": false,
        // "searching": false,
        "destroy": true,
        "language": {
            url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
        }
    });
}

function showDetail(cellId) {
    $.ajax({
        url: '/api/lte-cell-data/cell-detail-Id',
        dataType: 'text',
        data: {cellId: cellId},
        success: showCellAndCoSiteCell,
        error: function (err) {
            console.log(err);
        }
    });
}

function showCellAndCoSiteCell(data) {
    currentCellArray = eval("(" + data + ")");
    currentIndex = 0;
    showCellDetail(0);
}

function showEditor(cellId) {	//加载lte小区id保存到隐藏域
    $("#lteCellIdForEdit").val(cellId);
    //显示编辑框
    var editLteCellMessage = $("#editLteCellMessage");
    editLteCellMessage.css({
        "top": (32) + "%",
        "left": (25) + "%",
        "width": (800) + "px",
        "z-index": (30)
    });
    editLteCellMessage.show();
    //加载需要编辑的lte小区数据到页面
    $(".loading").css("display", "block");
    $.ajax({
        url: '/api/lte-cell-data/cell-detail-edit',
        data: {
            'cellId': cellId
        },
        dataType: 'text',
        success: function (data) {
            console.log(data);
            //目标小区信息
            var oneLteCellDetail = eval("(" + data + ")");
            if (!oneLteCellDetail) {
                return;
            }
            loadCellForEdit(oneLteCellDetail);
        },
        complete: function () {
            $(".loading").css("display", "none");
        }
    });

}

function loadCellForEdit(oneLteCellDetail) {
    // 循环设置
    var html = '';
    var size = chineseToCode.length;
    var onekey;
    for (var i = 0; i < size; i++) {
        if (i % 3 === 0) {
            // 换行
            if (i > 0) {
                html += "</tr>";
            }
            html += "<tr>";
        }
        onekey = chineseToCode[i];
        var oneValue = oneLteCellDetail[onekey['code']] === undefined || oneLteCellDetail[onekey['code']] === null ? "" : oneLteCellDetail[onekey['code']];
        //加载select标签
        if (onekey['code'] === 'cellId') {
            html += "<td class='menuTd'>" + onekey['name'] + " : " +
                "<input id='" + onekey['code'] + "' " +
                "type='text' readonly style='height: 21px;width: 146px' " +
                "name='" + onekey['code'] + "' value='" + oneValue + "'/>" +
                "</br> <span id='span" + onekey['code'] + "'  style='font-family:华文中宋; color:red;width:100px;display:block;'></span></td>";
        } else if (onekey['code'] === 'remoteCell'
            || onekey['code'] === 'relatedParam'
            || onekey['code'] === 'relatedResoure') {
            html += "<td class='menuTd'>" + onekey['name'] + " : " +
                "<select style='width: 146px' id='" + onekey['code'] + "' name='" + onekey['code'] + "'> ";
            if (oneLteCellDetail[onekey['code']] === "是") {
                html += "<option value='是' selected='selected'>是</option> " +
                    "<option value='否'>否</option> ";
            } else {
                html += "<option value='是'>是</option> " +
                    "<option value='否' selected='selected'>否</option> ";
            }
            html += "</select> " +
                "</td> ";
        } else {
            html += "<td class='menuTd'>" + onekey['name'] + " : " +
                "<input id='" + onekey['code'] + "' " +
                "name='" + onekey['code'] + "' " +
                "value='" + oneValue + "' " +
                "onkeyup='checkOnkeyupDateType(this)'/>" +
                "</br><span id='span" + onekey['code'] + "'  style='font-family:华文中宋; color:red;width:100px;display:block;'></span>" +
                "</td>";
        }
    }
    if (size % 3 > 0) {
        for (var j = 0; j < 3 - size % 3; j++) {
            html += "<td class='menuTd'></td>";
        }
    }
    html += "</tr>";
    //console.log(html);
    $("span#editVIEW_ENODEB_NAME").html(oneLteCellDetail['enodebId']);
    $("span#editVIEW_CELL_NAME").html(oneLteCellDetail['cellName']);
    $("#editCellDetailTable").html(html);
}

function showInfoInAndOut(div, info) {
    var divSet = $("#" + div);
    divSet.html(info);
    divSet.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

function showCellDetail(index) {
    if (!currentCellArray) {
        return;
    }
    if (index < 0) {
        return;
    }
    if (currentCellArray.length <= index) {
        index = 0;
    }
    var cell = currentCellArray[index];
    console.log(cell);
    // 设置值
    var lteCellDetailDiv = $("#lteCellDetailDiv");
    lteCellDetailDiv.find("span#VIEW_ENODEB_NAME").html(
        cell['enodebId']);
    lteCellDetailDiv.find("span#VIEW_CELL_NAME").html(
        cell['cellName']);
    var html = '';
    var size = chineseToCode.length;
    var onekey;
    for (var i = 0; i < size; i++) {
        if (i % 3 === 0) {
            // 换行
            if (i > 0) {
                html += "</tr>";
            }
            html += "<tr>";
        }
        onekey = chineseToCode[i];
        var value = cell[onekey['code']] === undefined ||cell[onekey['code']] === null ? ' ' : cell[onekey['code']];
        html += "<td class='menuTd'>" + onekey['name']
            + " : " + value + "</td>";
    }
    if (size % 3 > 0) {
        for (var j = 0; i < j - size % 3; j++) {
            html += "<td class='menuTd'></td>";
        }
    }
    html += "</tr>";
    $("#viewCellDetailTable").html(html);
    lteCellDetailDiv.css("display", "block");
}

function deleteCell(cellId) {
    var r = confirm("删除该条小区信息？");
    if(r === true){
        $.ajax({
            url: '/api/lte-cell-data/cell-delete',
            dataType: 'text',
            data: {cellId: cellId},
            success: function () {
                showInfoInAndOut("info", "删除小区成功！");
                $("#conditionForm").submit();
            }, error: function (err) {
                console.log(err);
                showInfoInAndOut("info", "后台程序错误！");
            }
        })
    }
}

/*
 *输入框输入完成后触发验证
 */
function checkOnkeyupDateType(e) {
    var id = e.id;  //获取元素的id
    var value = e.value;  //获取元素的值
    checkDateType(id, value);
}

/*
 * 对单个输入框输入完成后的数据验证
 * @param 元素id值
 * @param 元素value值
 */
function checkDateType(id, value) {

    //验证需要的正则表达式
    //var vali1 = /[^A-Za-z0-9]/g;   //输入数字和英文字母
    var vali2 = /[^\d.]/g; 		  //输入数字和小数点
    // var vali3 = /[^\d:]/g;		   //输入数字和：
    //var vali4 = /[^\d-]/g;    /*/^[-+]?[0-9]*\.?[0-9]+$/;*/	//输入正负浮点数
    var vali5 = /[^\d-.]/g;    //输入数字,负号，小数点

    //----------需要验证的参数 start-------------//
    //经度的验证
    if (id === 'longitude') {
        if (135.04166666667 < value || value < 73.666666666667) {
            $("span#span" + id).html("中国经度范围东经73.666666666667-<br>135.04166666667");
            submitOK = false;
        } else if (vali2.test(value)) {
            //console.log("不合法");
            $("span#span" + id).html("请输入数字或者小数点");
            submitOK = false;
        } else {
            //console.log("合法");
            $("span#span" + id).html("");
            submitOK = true;
        }
    }
    //纬度的验证
    if (id === 'latitude') {
        if (53.55 < value || value < 3.8666666666667) {
            $("span#span" + id).html("中国纬度范围北纬3.8666666666667-<br>53.55");
            submitOK = false;
        } else if (vali2.test(value)) {
            //console.log("不合法");
            $("span#span" + id).html("请输入数字或者小数点");
            submitOK = false;
        } else {
            //console.log("合法");
            $("span#span" + id).html("");
            submitOK = true;
        }
    }

    //覆盖类型的验证
    if (id === 'coverType') {
        if (value === "室内" || value === "室外") {
            $("span#span" + id).html("");
            submitOK = true;
        } else {
            $("span#span" + id).html("覆盖类型请填写“室内”或者“室外”");
            submitOK = false;
        }
    }
    //数字输入的验证
    if (id === 'pci' || id === 'antennaHeight' || id === 'azimuth'
        || id === 'tac' || id === 'mDowntilt' || id === 'eDowntilt'
        || id === 'totalDowntilt' || id === 'stationSpace'
        || id === 'earfcn' || id === 'brandWidth' || id === 'eci') {
        if (vali5.test(value)) {
            //console.log("不合法");
            $("span#span" + id).html("请输入正确的格式");
            submitOK = false;
        } else {
            //console.log("合法");
            $("span#span" + id).html("");
            submitOK = true;
        }
    }
    //----------需要验证的参数 end-------------//
}

function updateLteCellDetail(submitOK) {
    if (submitOK) {
        $("#lteCellDetailForm")
            .ajaxSubmit({
                url: "/api/lte-cell-data/cell-detail-update",
                dataType: 'text',
                success: function (flag) {
                    console.log(flag);
                    if (flag === "true") {
                        showInfoInAndOut("info", "更新成功！");
                        $("#conditionForm").ajaxForm({
                            url: "/api/lte-cell-data/cell-query",
                            success: showQueryList,
                            error: function (err) {
                                console.log(err);
                            }
                        });
                        $("#editLteCellMessage").hide();
                    } else {
                        showInfoInAndOut("info", "更新失败！");
                    }
                },
                complete: function () {
                    //alert("更新完成");
                    $(".loading").css("display", "none");
                }
            })
    }
}

function showImportRecord(data) {
    $('#queryRecordResTab').css("line-height", "12px")
        .DataTable({
            "data": data,
            "columns": [
                {"data": "areaName"},
                {"data": "uploadTime"},
                {"data": "filename"},
                {"data": "fileSize"},
                {"data": null},
                {"data": null},
                {"data": "createdUser"},
                {"data": null}
            ],
            "columnDefs": [{
                "render": function (data, type, row) {
                    switch (row['status']) {
                        case "部分成功":
                            return "<a style='color: red' onclick='showImportDetail()'>" + row['status'] + "</a>";
                        case "全部失败":
                            return "<a style='color: red' onclick='showImportDetail()'>" + row['status'] + "</a>";
                        case "全部成功":
                            return "<a onclick='showImportDetail()'>" + row['status'] + "</a>";
                        case "正在处理":
                            return "<a onclick='showImportDetail()'>" + row['status'] + "</a>";
                        case "等待处理":
                            return "<a onclick='showImportDetail()'>" + row['status'] + "</a>";
                    }
                },
                "targets": -1,
                "data": null
            },
                {
                    "render": function(data, type, row) {
                        if(row['startTime']===""||row['startTime']===null){
                            return " --- ";
                        }else {
                            return row['startTime'];
                        }
                    },
                    "targets": 4,
                    "data": "startTime"
                },{
                    "render": function(data, type, row) {
                        if(row['completeTime']===""||row['completeTime']===null){
                            return " --- ";
                        }else {
                            return row['completeTime'];
                        }
                    },
                    "targets": 5,
                    "data": "completeTime"
                }],
            "lengthChange": false,
            "ordering": false,
            "searching": false,
            "destroy": true,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        });
}

function showRecord(data) {
    $('#recordResult').css("line-height", "12px")
        .DataTable({
            "data": data,
            "columns": [
                {"data": "areaName"},
                {"data": "createdDate"},
                {"data": "dataType"},
                {"data": "filename"},
                {"data": "dataNum"},
                {"data": "sysTime"}
            ],
            "lengthChange": true,
            "ordering": false,
            "searching": true,
            "destroy": true,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        });
}

function showImportDetail() {
    
}

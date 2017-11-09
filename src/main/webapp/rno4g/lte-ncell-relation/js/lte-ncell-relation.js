$(function () {
    //tab选项卡
    tab("div_tab", "li", "onclick");//项目服务范围类别切换

    $("#queryBtn").click(function () {
        $(".loading").show();
        var reg = /^[0-9]+.?[0-9]*$/;
        var cellPci =$("#cellPci").val();
        var ncellPci =$("#ncellPci").val();
        if(!reg.test(cellPci) && cellPci.trim() !==''){
            $(".loading").css("display","none");
            showInfoInAndOut("info", "主小区PCI值必须为数字");
            return false;
        }
        if(cellPci.length >10 && cellPci.trim() !==''){
            $(".loading").css("display","none");
            showInfoInAndOut("info","主小区PCI值输入过长");
            return false;
        }
        if(!reg.test(ncellPci) && ncellPci.trim() !==''){
            $(".loading").css("display","none");
            showInfoInAndOut("info", "邻小区PCI值必须为数字");
            return false;
        }
        if(ncellPci.length >10 && ncellPci.trim() !==''){
            $(".loading").css("display","none");
            showInfoInAndOut("info","邻小区PCI值输入过长");
            return false;
        }
    });

    $("#conditionForm").ajaxForm({
        url: "/api/lte-ncell-relation/ncell-query",
        success: showNcellRelationResult
    });
});

function showNcellRelationResult(data) {
    $(".loading").css("display","none");
    if (data == '') {
        showInfoInAndOut('info', '没有符合条件的邻区关系');
        return false;
    }

    $('#queryResultTab').css("line-height", "12px");
    $('#queryResultTab').DataTable({
        "data": data,
        "columns": [
            {"data": "cellName"},
            {"data": "ncellName"},
            {"data": "cellId"},
            {"data": "cellEnodebId"},
            {"data": "ncellId"},
            {"data": "ncellEnodebId"},
            {"data": "cellPci"},
            {"data": "ncellPci"},
            {"data": null}
        ],
        "columnDefs": [{
            "render": function (data, type, row) {
                var id = row['id'];
                return "<a onclick=\"deleteCell('"+id+"')\">删除</a>";
            },
            "targets": -1,
            "data": null
        }
        ],
        // "lengthChange": false,
        // "ordering": false,
        // "searching": false,
        "destroy":true,
        "language": {
            url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
        }
    });
}

function showInfoInAndOut(div, info) {
    var divSet = $("#" + div);
    divSet.html(info);
    divSet.fadeIn(2000);
    setTimeout("$('#" + div + "').fadeOut(2000)", 1000);
}

function deleteCell(id) {
    $.ajax({
        url: '/api/lte-ncell-relation/deleteByCellIdAndNcellId',
        dataType: 'text',
        data: {id:id},
        success: function () {
            showInfoInAndOut("info","删除邻区关系成功！");
            $("#conditionForm").ajaxForm({
                url: "/api/lte-ncell-relation/ncell-query",
                success: showNcellRelationResult,
                error: function (err) {
                    console.log(err);
                }
            });
        },error: function (err) {
            console.log(err);
            showInfoInAndOut("info","后台程序错误！");
        }
    })
}
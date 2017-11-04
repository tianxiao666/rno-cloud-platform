$(function () {

    //tab选项卡
    tab("div_tab", "li", "onclick");//项目服务范围类别切换

    // $("#queryBtn").click(function () {
    $("#conditionForm").ajaxForm(function () {

        $('#queryResultTab').DataTable().clear();
        $('#queryResultTab').DataTable().destroy();

        $('#queryResultTab').css("line-height", "12px");
        $('#queryResultTab').DataTable({
            "ajax": "data/lte-cell-data.json",
            "columns": [
                {"data": "AREA_NAME"},
                {"data": "ENODEB_NAME"},
                {"data": "CELL_NAME"},
                {"data": "PCI"},
                {"data": "BAND"},
                {"data": "EARFCN"},
                {"data": "POWER"},
                {"data": null}
            ],
            "columnDefs": [{
                "render": function (data, type, row) {
                    return "<a onclick=\"showDetail()\">查询详情</a>&nbsp;&nbsp;&nbsp;"
                        + "<a onclick=\"showEditor()\">编辑</a>&nbsp;&nbsp;&nbsp;"
                        + "<a onclick=\"alert('删除小区PCI: " + row["PCI"] + "')\">删除</a>";
                },
                "targets": -1,
                "data": null
            }
            ],
            // "lengthChange": false,
            // "ordering": false,
            // "searching": false,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        });
    });

    //初始化区域
    initAreaSelector("province-id", "city-id");

});

function showDetail() {
    $("#lteCellDetailDiv").css("display", "block");
}

function showEditor() {
    $("#editLteCellMessage").css("display", "block");
}

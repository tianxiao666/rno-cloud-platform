$(function () {
    //tab选项卡
    tab("div_tab", "li", "onclick");//项目服务范围类别切换

    $("#conditionForm").ajaxForm(function () {

        $('#queryResultTab').DataTable().clear();
        $('#queryResultTab').DataTable().destroy();

        $('#queryResultTab').css("line-height", "12px");
        $('#queryResultTab').DataTable( {
            "ajax": "data/lte-ncell-relation-data.json",
            "columns": [
                { "data": "LTE_CELL_NAME" },
                { "data": "LTE_NCELL_NAME" },
                { "data": "LTE_CELL_ID" },
                { "data": "LTE_CELL_ENODEB_ID" },
                { "data": "LTE_NCELL_ID" },
                { "data": "LTE_NCELL_ENODEB_ID" },
                { "data": "LTE_CELL_SITE_ID" },
                { "data": "LTE_NCELL_SITE_ID" },
                { "data": null }
            ],
            "columnDefs": [ {
                "render": function(data, type, row) {
                    return "<a onclick=\"alert('删除LTE_NCELL_ID: " + row["LTE_NCELL_ID"] + "')\">删除</a>";
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
        } );
    });
});
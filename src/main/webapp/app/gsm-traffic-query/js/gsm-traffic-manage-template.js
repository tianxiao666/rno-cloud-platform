$(function () {

    $(".draggable").draggable();
    $("#trigger").css("display", "none");

    //报表模板查询
    $("#searchReportTempBtn").click(function () {
        $('#queryReportTemplateDT').css("line-height", "12px");
        $('#queryReportTemplateDT').DataTable( {
            "ajax": "data/gsm-traffic-report-template.json",
            "columns": [
                { "data": "reportName" },
                { "data": "reportEnglishName" },
                { "data": "author" },
                { "data": "createTime" },
                { "data": "modifyTime" },
                { "data": null }
            ],
            "columnDefs": [ {
                "render": function(data, type, row) {
                    return "<a onclick=\"alert('查看: " + row["reportName"] + "')\">查看</a>";
                },
                "targets": -1,
                "data": null
            }
            ],
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        });
    });
});

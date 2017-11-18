$(function () {
    //执行 laydate 实例 
    laydate.render({elem: '#begUploadDate', value: new Date(new Date().getTime() - 7 * 86400000)});
    laydate.render({elem: '#endUploadDate', value: new Date()});

    $("#queryImportBtn").click(function () {
        $('#importListDT').css("line-height", "12px");
        $('#importListDT').DataTable( {
            "ajax": "data/gsm-new-station-ncell-import-list.json",
            "columns": [
                { "data": "uploadTime" },
                { "data": "fileName" },
                { "data": "fileSize" },
                { "data": "launchTime" },
                { "data": "completeTime" },
                { "data": "account" },
                { "data": "fileStatus" }
            ],
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        });
    });
});
$(function () {

    //执行 区域 实例 
    $("#province-menu").change(function () {
        var cityId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, cityId, "city-menu");
        })
    });

    $("#province-id").change(function () {
        var provinceId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, provinceId, "city-id");
        })
    });

    $("#province-name").change(function () {
        var provinceId = parseInt($(this).find("option:checked").val());
        $.getJSON("../../data/area.json", function (data) {
            renderArea(data, provinceId, "city-name");
        })
    });

    $("#city-menu").change(function () {
        var area1 = $("#city-menu option:selected").text();
        $("#area1").val(area1);
    })


    //初始化区域
    $.ajax({
        url: "../../data/area.json",
        dataType: "json",
        async: false,
        success: function (data) {
            renderArea(data, 0, "province-id");
            renderArea(data, 0, "province-menu");
            renderArea(data, 0, "province-name");
            $("#province-name").change();
            $("#province-id").change();
            $("#province-menu").change();
        }
    });

    $("#queryBtn").click(function () {
        $('#queryResultTab').css("line-height", "12px");
        $('#queryResultTab').DataTable( {
            "ajax": "data/gsm-param-query-cell-query.json",
            "columns": [
                { data: "MEA_DATE" },
                { data: "MSC" },
                { data: "BSC" },
                { data: "CELL" },
                { data: "ACCMIN" },
                { data: "ACSTATE" },
                { data: "ACTIVE_32" },
                { data: "AGBLK" },
                { data: "CB" },
                { data: "CBCHD" },
                { data: "CBQ" },
                { data: "CCHPWR" },
                { data: "CELLQ" },
                { data: "CELL_DIR" }
            ],
            "lengthChange": false,
            "ordering": true,
            "searching": true,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        } );
    });

    $("#queryBtn1").click(function () {
        $('#queryResultTab1').css("line-height", "12px");
        $('#queryResultTab1').DataTable( {
            "ajax": "data/gsm-param-query-channel-query.json",
            "columns": [
                { data: "MEA_DATE" },
                { data: "MSC" },
                { data: "BSC" },
                { data: "CELL" },
                { data: "CH_GROUP" },
                { data: "CHGR_STATE" },
                { data: "CHGR_TG" },
                { data: "HSN" },
                { data: "NUMREQBPC" },
                { data: "NUMREQEGPRSBPC" },
                { data: "ODPDCHLIMIT" },
                { data: "SAS" },
                { data: "SCTYPE" },
                { data: "SDCCH" },
                { data: "DCHNO_32" }
            ],
            "lengthChange": false,
            "ordering": true,
            "searching": true,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        } );
    });

    $("#queryBtn2").click(function () {
        $('#queryResultTab2').css("line-height", "12px");
        $('#queryResultTab2').DataTable( {
            "ajax": "data/gsm-param-query-ncell-query.json",
            "columns": [
                { data: "MEA_DATE" },
                { data: "MSC" },
                { data: "BSC" },
                { data: "CELL" },
                { data: "N_BSC" },
                { data: "N_CELL" },
                { data: "AWOFFSET" },
                { data: "BQOFFSET" },
                { data: "BQOFFSETAFR" },
                { data: "BQOFFSETAWB" },
                { data: "CAND" },
                { data: "CS" },
                { data: "GPRSVALID" },
                { data: "HIHYST" },
                { data: "KHYST" },
                { data: "KOFFSET" }

            ],
            "lengthChange": false,
            "ordering": true,
            "searching": true,
            "language": {
                url: '../../lib/datatables/1.10.16/i18n/Chinese.json'
            }
        } );
    });


});

// 渲染区域
function renderArea(data, parentId, areaMenu) {
    var arr = data.filter(function (v) {
        return v.parentId === parentId;
    });
    if (arr.length > 0) {
        var areaHtml = [];
        $.each(arr, function (index) {
            var area = arr[index];
            areaHtml.push("<option value='" + area.id + "'>");
            areaHtml.push(area.name + "</option>");
        });
        $("#" + areaMenu).html(areaHtml.join(""));
        var area1 = $("#city-menu option:selected").text();
        $("#area1").val(area1);
    } else {
        console.log("父ID为" + parentId + "时未找到任何下级区域。");
    }

}

package com.hgicreate.rno.service.gsm;

import java.util.List;
import java.util.Map;

public interface RnoNcsDynaCoverageDao {

    //检查小区是华为还是爱立信
    String checkCellIsHwOrEri(String cell);

    //查询爱立信ncs数据，并整理得到需要结果
    List<Map<String, Object>> queryEriDataFromOracle(long cityId, String enName,String cellId,
                                                     String startDate, String endDate, String RELSS);

    //查询华为ncs数据，并整理得到需要结果
    List<Map<String, Object>> queryHwDataFromOracle(long cityId, String enName,String cellId,
                                                    String startDate, String endDate, String RELSS);
}

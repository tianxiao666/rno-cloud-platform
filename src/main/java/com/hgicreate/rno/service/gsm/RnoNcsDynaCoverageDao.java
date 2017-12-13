package com.hgicreate.rno.service.gsm;

import java.util.List;
import java.util.Map;

public interface RnoNcsDynaCoverageDao {

    /**
     * 检查小区是华为还是爱立信
     *
     * @return
     * @title
     * @author peng.jm
     * @date 2015年3月10日10:59:56
     * @company 怡创科技
     */
    String checkCellIsHwOrEri(String cell);

    /**
     * 查询爱立信ncs数据，并整理得到需要结果
     *
     * @return
     * @title
     * @author peng.jm
     * @date 2015年3月10日10:59:56
     * @company 怡创科技
     */
    List<Map<String, Object>> queryEriDataFromOracle(long cityId, String cell,
                                                     String startDate, String endDate, String RELSS);

    /**
     * 查询华为ncs数据，并整理得到需要结果
     *
     * @return
     * @title
     * @author peng.jm
     * @date 2015年3月10日10:59:56
     * @company 怡创科技
     */
    List<Map<String, Object>> queryHwDataFromOracle(long cityId, String cell,
                                                    String startDate, String endDate, String RELSS);
}

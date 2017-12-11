package com.hgicreate.rno.mapper.gsm;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface GsmMrrDetailMapper {

    /**
     * 获取爱立信mrr文件的6,7级信号上行质量占比
     * @param mrrDescId
     * @return
     * @author peng.jm
     * @date 2014-9-4下午02:24:42
     */
    List<Map<String, Object>> queryEriMrrUlQua6t7RateByDescId(@Param("mrrDescId") final long mrrDescId,
                                                              @Param("cityId")final long cityId, @Param("meaTime")final Date meaTime);

    /**
     * 获取爱立信mrr文件的6,7级信号下行质量占比
     * @param mrrDescId
     * @return
     * @author peng.jm
     * @date 2014-9-4下午02:24:42
     */
    List<Map<String, Object>> queryEriMrrDlQua6t7RateByDescId(@Param("mrrDescId") final long mrrDescId,
                                                              @Param("cityId")final long cityId, @Param("meaTime")final Date meaTime);

    /**
     * 获取爱立信mrr文件的上行平均信号强度
     * @param mrrDescId
     * @return
     * @author peng.jm
     * @date 2014-9-4下午02:24:42
     */
    List<Map<String, Object>> queryEriMrrUlStrenRateByDescId(@Param("mrrDescId") final long mrrDescId,
                                                             @Param("cityId")final long cityId, @Param("meaTime")final Date meaTime);

    /**
     * 获取爱立信mrr文件的下行平均信号强度
     * @param mrrDescId
     * @return
     * @author peng.jm
     * @date 2014-9-4下午02:24:42
     */
    List<Map<String, Object>> queryEriMrrDlStrenRateByDescId(@Param("mrrDescId") final long mrrDescId,
                                                             @Param("cityId")final long cityId, @Param("meaTime")final Date meaTime);

    /**
     * 获取爱立信mrr文件的下行弱信号比例
     * @param mrrDescId
     * @return
     * @author peng.jm
     * @date 2014-9-4下午02:24:42
     */
    List<Map<String, Object>> queryEriMrrDlWeekSignalByDescId(@Param("mrrDescId") final long mrrDescId,
                                                              @Param("cityId")final long cityId, @Param("meaTime")final Date meaTime);

    /**
     * 获取爱立信mrr文件的平均TA
     * @param mrrDescId
     * @return
     * @author peng.jm
     * @date 2014-9-4下午02:24:42
     */
    List<Map<String, Object>> queryEriMrrAverTaByDescId(@Param("mrrDescId") final long mrrDescId,
                                                        @Param("cityId")final long cityId, @Param("meaTime")final Date meaTime);

    /**
     * 获取爱立信mrr文件的最大TA
     * @param mrrDescId
     * @return
     * @author peng.jm
     * @date 2014-9-4下午02:24:42
     */
    List<Map<String, Object>> queryEriMrrMaxTaByDescId(@Param("mrrDescId") final long mrrDescId,
                                                       @Param("cityId")final long cityId, @Param("meaTime")final Date meaTime);

    /**
     * 获取爱立信mrr文件的上行通好率
     * @param mrrDescId
     * @return
     * @author peng.jm
     * @date 2014-9-4下午02:24:42
     */
    List<Map<String, Object>> queryEriMrrUlQua0t5RateByDescId(@Param("mrrDescId") final long mrrDescId,
                                                              @Param("cityId")final long cityId, @Param("meaTime")final Date meaTime);

    /**
     * 获取爱立信mrr文件的下行通好率
     * @param mrrDescId
     * @return
     * @author peng.jm
     * @date 2014-9-4下午02:24:42
     */
    List<Map<String, Object>> queryEriMrrDlQua0t5RateByDescId(@Param("mrrDescId") final long mrrDescId,
                                                              @Param("cityId")final long cityId, @Param("meaTime")final Date meaTime);

    /**
     * 获取爱立信mrr文件的小区总数
     * @param mrrDescId
     * @return
     * @author peng.jm
     * @date 2014-9-4下午02:23:27
     */
    long getEriMrrCellAndBscCntByDescId(@Param("mrrDescId") final long mrrDescId,
                                        @Param("cityId")final long cityId, @Param("meaTime")final Date meaTime);

    /**
     * 获取爱立信mrr文件的小区和对应的BSC
     * @param mrrDescId
     * @return
     * @author peng.jm
     * @date 2014-9-4下午02:24:09
     */
    List<Map<String, Object>> queryEriMrrCellAndBscByDescId(@Param("mrrDescId") final long mrrDescId,
                                                            @Param("cityId")final long cityId, @Param("meaTime")final Date meaTime);
}

package com.hgicreate.rno.mapper.gsm;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author ke_weixu
 */
@Mapper
public interface GsmMrrDetailMapper {

    /**
     * 获取爱立信mrr文件的6,7级信号上行质量占比
     * @param mrrDescId mrrDescId
     * @param cityId cityId
     * @param meaTime 测试时间
     * @return UlQua6t7Rate
     */
    List<Map<String, Object>> queryEriMrrUlQua6t7RateByDescId(@Param("mrrDescId") final long mrrDescId,
                                                              @Param("cityId")final long cityId, @Param("meaTime")final Date meaTime);

    /**
     * 获取爱立信mrr文件的6,7级信号下行质量占比
     * @param mrrDescId mrrDescId
     * @param cityId cityId
     * @param meaTime 测试时间
     * @return DlQua6t7Rate
     */
    List<Map<String, Object>> queryEriMrrDlQua6t7RateByDescId(@Param("mrrDescId") final long mrrDescId,
                                                              @Param("cityId")final long cityId, @Param("meaTime")final Date meaTime);

    /**
     * 获取爱立信mrr文件的上行平均信号强度
     * @param mrrDescId mrrDescId
     * @param cityId cityId
     * @param meaTime 测试时间
     * @return UlStrenRate
     */
    List<Map<String, Object>> queryEriMrrUlStrenRateByDescId(@Param("mrrDescId") final long mrrDescId,
                                                             @Param("cityId")final long cityId, @Param("meaTime")final Date meaTime);

    /**
     * 获取爱立信mrr文件的下行平均信号强度
     * @param mrrDescId mrrDescId
     * @param cityId cityId
     * @param meaTime 测试时间
     * @return DlStrenRate
     */
    List<Map<String, Object>> queryEriMrrDlStrenRateByDescId(@Param("mrrDescId") final long mrrDescId,
                                                             @Param("cityId")final long cityId, @Param("meaTime")final Date meaTime);

    /**
     * 获取爱立信mrr文件的下行弱信号比例
     * @param mrrDescId mrrDescId
     * @param cityId cityId
     * @param meaTime 测试时间
     * @return DlWeekSignal
     */
    List<Map<String, Object>> queryEriMrrDlWeekSignalByDescId(@Param("mrrDescId") final long mrrDescId,
                                                              @Param("cityId")final long cityId, @Param("meaTime")final Date meaTime);

    /**
     * 获取爱立信mrr文件的平均TA
     * @param mrrDescId mrrDescId
     * @param cityId cityId
     * @param meaTime 测试时间
     * @return AverTa
     */
    List<Map<String, Object>> queryEriMrrAverTaByDescId(@Param("mrrDescId") final long mrrDescId,
                                                        @Param("cityId")final long cityId, @Param("meaTime")final Date meaTime);

    /**
     * 获取爱立信mrr文件的最大TA
     * @param mrrDescId mrrDescId
     * @param cityId cityId
     * @param meaTime 测试时间
     * @return MaxTa
     */
    List<Map<String, Object>> queryEriMrrMaxTaByDescId(@Param("mrrDescId") final long mrrDescId,
                                                       @Param("cityId")final long cityId, @Param("meaTime")final Date meaTime);

    /**
     * 获取爱立信mrr文件的上行通好率
     * @param mrrDescId mrrDescId
     * @param cityId cityId
     * @param meaTime 测试时间
     * @return UlQua0t5Rate
     */
    List<Map<String, Object>> queryEriMrrUlQua0t5RateByDescId(@Param("mrrDescId") final long mrrDescId,
                                                              @Param("cityId")final long cityId, @Param("meaTime")final Date meaTime);

    /**
     * 获取爱立信mrr文件的下行通好率
     * @param mrrDescId mrrDescId
     * @param cityId cityId
     * @param meaTime 测试时间
     * @return DlQua0t5Rate
     */
    List<Map<String, Object>> queryEriMrrDlQua0t5RateByDescId(@Param("mrrDescId") final long mrrDescId,
                                                              @Param("cityId")final long cityId, @Param("meaTime")final Date meaTime);

    /**
     * 获取爱立信mrr文件的小区总数
     * @param mrrDescId mrrDescId
     * @param cityId cityId
     * @param meaTime 测试时间
     * @return 小区总数
     */
    long getEriMrrCellAndBscCntByDescId(@Param("mrrDescId") final long mrrDescId,
                                        @Param("cityId")final long cityId, @Param("meaTime")final Date meaTime);

    /**
     * 获取爱立信mrr文件的小区和对应的BSC
     * @param mrrDescId mrrDescId
     * @param cityId cityId
     * @param meaTime 测试时间
     * @return 爱立信mrr文件的小区和对应的BSC
     */
    List<Map<String, Object>> queryEriMrrCellAndBscByDescId(@Param("mrrDescId") final long mrrDescId,
                                                            @Param("cityId")final long cityId, @Param("meaTime")final Date meaTime);
}

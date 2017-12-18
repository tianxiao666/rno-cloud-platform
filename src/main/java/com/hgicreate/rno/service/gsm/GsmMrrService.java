package com.hgicreate.rno.service.gsm;

import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.domain.gsm.GsmMrrDesc;
import com.hgicreate.rno.mapper.gsm.GsmMrrDetailMapper;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.repository.gsm.GsmMrrDescRepository;
import com.hgicreate.rno.web.rest.gsm.vm.GsmMrrDescQueryVM;
import com.hgicreate.rno.web.rest.gsm.vm.GsmImportQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author ke_weixu
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class GsmMrrService {
    private final GsmMrrDescRepository gsmMrrDescRepository;
    private final DataJobRepository dataJobRepository;
    private final GsmMrrDetailMapper gsmMrrDetailMapper;

    public GsmMrrService(GsmMrrDescRepository gsmMrrDescRepository, DataJobRepository dataJobRepository, GsmMrrDetailMapper gsmMrrDetailMapper) {
        this.gsmMrrDescRepository = gsmMrrDescRepository;
        this.dataJobRepository = dataJobRepository;
        this.gsmMrrDetailMapper = gsmMrrDetailMapper;
    }

    public List<GsmMrrDesc> mrrDataQuery(GsmMrrDescQueryVM vm) {
        Area area = new Area();
        area.setId(vm.getAreaId());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(vm.getEndTestDate());
        calendar.add(Calendar.DATE, 1);
        Date endDate = calendar.getTime();
        if (vm.getBsc() == null || Objects.equals(vm.getBsc().trim(), "")) {
            return gsmMrrDescRepository.findTop1000ByAreaAndFactoryAndMeaDateBetween(area, vm.getFactory(), vm.getBeginTestDate(), endDate);
        }
        return gsmMrrDescRepository.findTop1000ByAreaAndFactoryAndBscLikeAndMeaDateBetween(area, vm.getFactory(), "%" + vm.getBsc().trim() + "%", vm.getBeginTestDate(), endDate);

    }

    public List<DataJob> gsmImportQuery(GsmImportQueryVM vm) {
        Area area = new Area();
        area.setId(vm.getAreaId());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(vm.getEndDate());
        calendar.add(Calendar.DATE, 1);
        Date endDate = calendar.getTime();
        if ("全部".equals(vm.getStatus())) {
            return dataJobRepository.findTop1000ByAreaAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(area, vm.getBeginDate(), endDate, vm.getModuleName());
        }
        return dataJobRepository.findTop1000ByAreaAndStatusAndOriginFile_CreatedDateBetweenAndOriginFile_DataTypeOrderByOriginFile_CreatedDateDesc(area, vm.getStatus(), vm.getBeginDate(), endDate, vm.getModuleName());
    }

    /**
     * 查询爱立信Mrr详情信息
     *
     * @return
     * @author peng.jm
     * @date 2014-9-2下午05:17:42
     */
    public List<Map<String, Object>> queryEriMrrDetailByPage(
            Long mrrDescId) {


        GsmMrrDesc mrrDescDetails = gsmMrrDescRepository.findOne(mrrDescId);
        long cityId;
        Date meaTime;
        if (mrrDescDetails != null) {
            if (mrrDescDetails.getArea() != null
                    && mrrDescDetails.getMeaDate() != null) {
                cityId = mrrDescDetails.getArea().getId();
                meaTime = mrrDescDetails.getMeaDate();
            } else {
                log.warn("mrrDescId=" + mrrDescId + "的描述信息中，cityId或者meaTime为空");
                return Collections.emptyList();
            }
        } else {
            log.warn("mrrDescId=" + mrrDescId + ",不存在对应描述信息");
            return Collections.emptyList();
        }

        //long totalCnt = page.getTotalCnt();
        /*if (totalCnt < 0) {
            totalCnt = gsmMrrDetailMapper.getEriMrrCellAndBscCntByDescId(
                    mrrDescId, cityId, meaTime);
            page.setTotalCnt((int) totalCnt);
        }*/
        //int startIndex = page.calculateStart();
        int startIndex = 1;
        int cnt = 50;
        List<Map<String, Object>> resCellAndBsc = gsmMrrDetailMapper
                .queryEriMrrCellAndBscByDescId(mrrDescId, cityId, meaTime);
        List<Map<String, Object>> resUlQua6t7Rate = gsmMrrDetailMapper
                .queryEriMrrUlQua6t7RateByDescId(mrrDescId, cityId, meaTime);
        List<Map<String, Object>> resDlQua6t7Rate = gsmMrrDetailMapper
                .queryEriMrrDlQua6t7RateByDescId(mrrDescId, cityId, meaTime);
        List<Map<String, Object>> resUlStrenRate = gsmMrrDetailMapper
                .queryEriMrrUlStrenRateByDescId(mrrDescId, cityId, meaTime);
        List<Map<String, Object>> resDlStrenRate = gsmMrrDetailMapper
                .queryEriMrrDlStrenRateByDescId(mrrDescId, cityId, meaTime);
        List<Map<String, Object>> resDlWeekSignal = gsmMrrDetailMapper
                .queryEriMrrDlWeekSignalByDescId(mrrDescId, cityId, meaTime);
        List<Map<String, Object>> resAverTa = gsmMrrDetailMapper
                .queryEriMrrAverTaByDescId(mrrDescId, cityId, meaTime);
        List<Map<String, Object>> resMaxTa = gsmMrrDetailMapper
                .queryEriMrrMaxTaByDescId(mrrDescId, cityId, meaTime);
        List<Map<String, Object>> resUlQua0t5Rate = gsmMrrDetailMapper
                .queryEriMrrUlQua0t5RateByDescId(mrrDescId, cityId, meaTime);
        List<Map<String, Object>> resDlQua0t5Rate = gsmMrrDetailMapper
                .queryEriMrrDlQua0t5RateByDescId(mrrDescId, cityId, meaTime);

        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        String cell = "";
        for (int i = 0; i < resCellAndBsc.size(); i++) {
            map = new HashMap<String, Object>();
            cell = resCellAndBsc.get(i).get("CELL_NAME").toString();
            map.put("CELL_NAME", cell);
            map.put("BSC", resCellAndBsc.get(i).get("BSC").toString());
            if (resUlQua6t7Rate.size() == 0 || resUlQua6t7Rate.get(i).get("UL_QUA6T7_RATE") == null) {
                map.put("UL_QUA6T7_RATE", "--");
            } else {
                if (cell.equals(resUlQua6t7Rate.get(i).get("CELL_NAME")
                        .toString())) {
                    map.put("UL_QUA6T7_RATE",
                            resUlQua6t7Rate.get(i).get("UL_QUA6T7_RATE"));
                } else {
                    map.put("UL_QUA6T7_RATE", "--");
                }
            }
            if (resDlQua6t7Rate.size() == 0 || resDlQua6t7Rate.get(i).get("DL_QUA6T7_RATE") == null) {
                map.put("DL_QUA6T7_RATE", "--");
            } else {
                if (cell.equals(resDlQua6t7Rate.get(i).get("CELL_NAME")
                        .toString())) {
                    map.put("DL_QUA6T7_RATE",
                            resDlQua6t7Rate.get(i).get("DL_QUA6T7_RATE"));
                } else {
                    map.put("DL_QUA6T7_RATE", "--");
                }
            }
            if (resUlStrenRate.size() == 0 || resUlStrenRate.get(i).get("UL_STREN_RATE") == null) {
                map.put("UL_STREN_RATE", "--");
            } else {
                if (cell.equals(resUlStrenRate.get(i).get("CELL_NAME")
                        .toString())) {
                    map.put("UL_STREN_RATE",
                            resUlStrenRate.get(i).get("UL_STREN_RATE"));
                } else {
                    map.put("UL_STREN_RATE", "--");
                }
            }
            if (resDlStrenRate.size() == 0 || resDlStrenRate.get(i).get("DL_STREN_RATE") == null) {
                map.put("DL_STREN_RATE", "--");
            } else {
                if (cell.equals(resDlStrenRate.get(i).get("CELL_NAME")
                        .toString())) {
                    map.put("DL_STREN_RATE",
                            resDlStrenRate.get(i).get("DL_STREN_RATE"));
                } else {
                    map.put("DL_STREN_RATE", "--");
                }
            }
            if (resDlWeekSignal.size() == 0 || resDlWeekSignal.get(i).get("DL_WEEK_SIGNAL") == null) {
                map.put("DL_WEEK_SIGNAL", "--");
            } else {
                if (cell.equals(resDlWeekSignal.get(i).get("CELL_NAME")
                        .toString())) {
                    map.put("DL_WEEK_SIGNAL",
                            resDlWeekSignal.get(i).get("DL_WEEK_SIGNAL"));
                } else {
                    map.put("DL_WEEK_SIGNAL", "--");
                }
            }
            if (resAverTa.size() == 0 || resAverTa.get(i).get("AVER_TA") == null) {
                map.put("AVER_TA", "--");
            } else {
                if (cell.equals(resAverTa.get(i).get("CELL_NAME").toString())) {
                    map.put("AVER_TA", resAverTa.get(i).get("AVER_TA"));
                } else {
                    map.put("AVER_TA", "--");
                }
            }
            if (resMaxTa.size() == 0 || resMaxTa.get(i).get("MAX_TA") == null) {
                map.put("MAX_TA", "--");
            } else {
                if (cell.equals(resMaxTa.get(i).get("CELL_NAME").toString())) {
                    map.put("MAX_TA", resMaxTa.get(i).get("MAX_TA"));
                } else {
                    map.put("MAX_TA", "--");
                }
            }
            if (resUlQua0t5Rate.size() == 0 || resUlQua0t5Rate.get(i).get("UL_QUA0T5_RATE") == null) {
                map.put("UL_QUA0T5_RATE", "--");
            } else {
                if (cell.equals(resUlQua0t5Rate.get(i).get("CELL_NAME")
                        .toString())) {
                    map.put("UL_QUA0T5_RATE",
                            resUlQua0t5Rate.get(i).get("UL_QUA0T5_RATE"));
                } else {
                    map.put("UL_QUA0T5_RATE", "--");
                }
            }
            if (resDlQua0t5Rate.size() == 0 ||resDlQua0t5Rate.get(i).get("DL_QUA0T5_RATE") == null) {
                map.put("DL_QUA0T5_RATE", "--");
            } else {
                if (cell.equals(resDlQua0t5Rate.get(i).get("CELL_NAME")
                        .toString())) {
                    map.put("DL_QUA0T5_RATE",
                            resDlQua0t5Rate.get(i).get("DL_QUA0T5_RATE"));
                } else {
                    map.put("DL_QUA0T5_RATE", "--");
                }
            }
            result.add(map);
        }
        return result;
    }
}

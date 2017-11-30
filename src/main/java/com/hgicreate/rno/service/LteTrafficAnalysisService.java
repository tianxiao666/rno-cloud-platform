package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.LteTrafficData;
import com.hgicreate.rno.repository.LteTrafficDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
public class LteTrafficAnalysisService {

    private final LteTrafficDataRepository lteTrafficDataRepository;

    public LteTrafficAnalysisService(LteTrafficDataRepository lteTrafficDataRepository) {
        this.lteTrafficDataRepository = lteTrafficDataRepository;
    }

    public Map<String, Object> getCellIndex(String beginTime, String cellId) {
        Map<String, Object> resMap = new LinkedHashMap<>();
        LteTrafficData trafficData = lteTrafficDataRepository
                .findByLteTrafficDesc_BeginTimeAndCellId(
                        LocalDateTimeToUdate(beginTime), cellId).get(0);
        resMap.put("小区名", trafficData.getPmUserLabel());
        resMap.put("测量开始时间", trafficData.getLteTrafficDesc().getBeginTime());
        resMap.put("测量结束时间", trafficData.getLteTrafficDesc().getEndTime());

        float rrc_ConnEstabSucc;
        if (trafficData.getRrcAttconnestab() == 0) {
            rrc_ConnEstabSucc = 100;
        } else {
            rrc_ConnEstabSucc = 100 * trafficData.getRrcSuccconnestab() / trafficData.getRrcAttconnestab();
        }
        resMap.put("RRC连接建立成功率", formatData(rrc_ConnEstabSucc));
        float erab_EstabSucc;
        if (trafficData.getErabNbrattestab() == 0) {
            erab_EstabSucc = 100;
        } else {
            erab_EstabSucc = 100 * (trafficData.getErabNbrsuccestab()) / (trafficData.getErabNbrattestab());
        }
        resMap.put("E-RAB建立成功率", formatData(erab_EstabSucc));
        float wireConn;
        if ((trafficData.getErabNbrattestab()) * (trafficData.getRrcAttconnestab()) == 0 || (trafficData.getRrcAttconnestab()) == 0) {
            wireConn = 100;
        } else {
            wireConn = 100 * (trafficData.getErabNbrsuccestab()) / (trafficData.getErabNbrattestab()) * (trafficData.getRrcSuccconnestab()) / (trafficData.getRrcAttconnestab());
        }
        resMap.put("无线接通率", formatData(wireConn));

        float erab_Drop_CellLevel;
        if ((trafficData.getErabNbrsuccestab()) == 0) {
            erab_Drop_CellLevel = 0;
        } else {
            erab_Drop_CellLevel = 100 * ((trafficData.getErabNbrreqrelenb()) - (trafficData.getErabNbrreqrelenbNormal()) + (trafficData.getErabHofail()))
                    / ((trafficData.getErabNbrsuccestab()) + (trafficData.getHoSuccoutinterenbs1()) + (trafficData.getHoSuccoutinterenbx2()) + (trafficData.getHoAttoutintraenb()));
        }
        resMap.put("E-RAB掉线率(小区级)", formatData(erab_Drop_CellLevel));
        float rrc_ConnRebuild;
        if (((trafficData.getRrcAttconnreestab()) + (trafficData.getRrcAttconnestab())) == 0) {
            rrc_ConnRebuild = 0;
        } else {
            rrc_ConnRebuild = 100 * (trafficData.getRrcAttconnreestab()) / ((trafficData.getRrcAttconnreestab()) + (trafficData.getRrcAttconnestab()));
        }
        resMap.put("RRC连接重建比率", formatData(rrc_ConnRebuild));
        float switchSucc;
        if (((trafficData.getHoAttoutinterenbs1()) + (trafficData.getHoAttoutinterenbx2()) + (trafficData.getHoAttoutintraenb())) == 0) {
            switchSucc = 100;
        } else {
            switchSucc = 100 * ((trafficData.getHoSuccoutinterenbs1()) + (trafficData.getHoSuccoutinterenbx2()) + (trafficData.getHoSuccoutintraenb()))
                    / ((trafficData.getHoAttoutinterenbs1()) + (trafficData.getHoAttoutinterenbx2()) + (trafficData.getHoAttoutintraenb()));
        }
        resMap.put("切换成功率", formatData(switchSucc));
        float emUplinkSerBytes;
        emUplinkSerBytes = (trafficData.getPdcpUpoctul());
        resMap.put("空口上行业务字节数", emUplinkSerBytes);
        float emDownlinkSerBytes;
        emDownlinkSerBytes = (trafficData.getPdcpUpoctdl());
        resMap.put("空口下行业务字节数", emDownlinkSerBytes);
        float erab_ConnSuccQCI1;
        if ((trafficData.getErabNbrattestab_1()) == 0) {
            erab_ConnSuccQCI1 = 100;
        } else {
            erab_ConnSuccQCI1 = 100 * (trafficData.getErabNbrsuccestab_1()) / (trafficData.getErabNbrattestab_1());
        }
        resMap.put("E-RAB建立成功率(QCI=1)", formatData(erab_ConnSuccQCI1));

        float erab_ConnSuccQCI2;
        if ((trafficData.getErabNbrattestab_2()) == 0) {
            erab_ConnSuccQCI2 = 100;
        } else {
            erab_ConnSuccQCI2 = 100 * (trafficData.getErabNbrsuccestab_2()) / (trafficData.getErabNbrattestab_2());
        }
        resMap.put("E-RAB建立成功率(QCI=2)", formatData(erab_ConnSuccQCI2));
        float erab_ConnSuccQCI3;
        if ((trafficData.getErabNbrattestab_3()) == 0) {
            erab_ConnSuccQCI3 = 100;
        } else {
            erab_ConnSuccQCI3 = 100 * (trafficData.getErabNbrsuccestab_3()) / (trafficData.getErabNbrattestab_3());
        }
        resMap.put("E-RAB建立成功率(QCI=3)", formatData(erab_ConnSuccQCI3));
        float erab_ConnSuccQCI4;
        if ((trafficData.getErabNbrattestab_4()) == 0) {
            erab_ConnSuccQCI4 = 100;
        } else {
            erab_ConnSuccQCI4 = 100 * (trafficData.getErabNbrsuccestab_4()) / (trafficData.getErabNbrattestab_4());
        }
        resMap.put("E-RAB建立成功率(QCI=4)", formatData(erab_ConnSuccQCI4));
        float erab_ConnSuccQCI5;
        if ((trafficData.getErabNbrattestab_5()) == 0) {
            erab_ConnSuccQCI5 = 100;
        } else {
            erab_ConnSuccQCI5 = 100 * (trafficData.getErabNbrsuccestab_5()) / (trafficData.getErabNbrattestab_5());
        }
        resMap.put("E-RAB建立成功率(QCI=5)", formatData(erab_ConnSuccQCI5));
        float erab_ConnSuccQCI6;
        if ((trafficData.getErabNbrattestab_6()) == 0) {
            erab_ConnSuccQCI6 = 100;
        } else {
            erab_ConnSuccQCI6 = 100 * (trafficData.getErabNbrsuccestab_6()) / (trafficData.getErabNbrattestab_6());
        }
        resMap.put("E-RAB建立成功率(QCI=6)", formatData(erab_ConnSuccQCI6));
        float erab_ConnSuccQCI7;
        if ((trafficData.getErabNbrattestab_7()) == 0) {
            erab_ConnSuccQCI7 = 100;
        } else {
            erab_ConnSuccQCI7 = 100 * (trafficData.getErabNbrsuccestab_7()) / (trafficData.getErabNbrattestab_7());
        }
        resMap.put("E-RAB建立成功率(QCI=7)", formatData(erab_ConnSuccQCI7));
        float erab_ConnSuccQCI8;
        if ((trafficData.getErabNbrattestab_8()) == 0) {
            erab_ConnSuccQCI8 = 100;
        } else {
            erab_ConnSuccQCI8 = 100 * (trafficData.getErabNbrsuccestab_8()) / (trafficData.getErabNbrattestab_8());
        }
        resMap.put("E-RAB建立成功率(QCI=8)", formatData(erab_ConnSuccQCI8));

        float erab_ConnSuccQCI9;
        if ((trafficData.getErabNbrattestab_9()) == 0) {
            erab_ConnSuccQCI9 = 100;
        } else {
            erab_ConnSuccQCI9 = 100 * (trafficData.getErabNbrsuccestab_9()) / (trafficData.getErabNbrattestab_9());
        }
        resMap.put("E-RAB建立成功率(QCI=9)", formatData(erab_ConnSuccQCI9));
        float wireConnQCI1;
        if ((trafficData.getErabNbrattestab_1()) == 0
                || (trafficData.getRrcAttconnestab()) == 0) {
            wireConnQCI1 = 100;
        } else {
            wireConnQCI1 = 100 * (trafficData.getErabNbrsuccestab_1()) / (trafficData.getErabNbrattestab_1()) * (trafficData.getRrcSuccconnestab()) / (trafficData.getRrcAttconnestab());
        }
        resMap.put("无线接通率(QCI=1)", formatData(wireConnQCI1));
        float wireConnQCI2;
        if ((trafficData.getErabNbrattestab_2()) == 0 || (trafficData.getRrcAttconnestab()) == 0) {
            wireConnQCI2 = 100;
        } else {
            wireConnQCI2 = 100 * (trafficData.getErabNbrsuccestab_2()) / (trafficData.getErabNbrattestab_2()) * (trafficData.getRrcSuccconnestab()) / (trafficData.getRrcAttconnestab());
        }
        resMap.put("无线接通率(QCI=2)", formatData(wireConnQCI2));
        float wireConnQCI3;
        if ((trafficData.getErabNbrattestab_3()) == 0 || (trafficData.getRrcAttconnestab()) == 0) {
            wireConnQCI3 = 100;
        } else {
            wireConnQCI3 = 100 * (trafficData.getErabNbrsuccestab_3()) / (trafficData.getErabNbrattestab_3()) * (trafficData.getRrcSuccconnestab()) / (trafficData.getRrcAttconnestab());
        }
        resMap.put("无线接通率(QCI=3)", formatData(wireConnQCI3));
        float wireConnQCI4;
        if ((trafficData.getErabNbrattestab_4()) == 0 || (trafficData.getRrcAttconnestab()) == 0) {
            wireConnQCI4 = 100;
        } else {
            wireConnQCI4 = 100 * (trafficData.getErabNbrsuccestab_4()) / (trafficData.getErabNbrattestab_4())
                    * (trafficData.getRrcSuccconnestab()) / (trafficData.getRrcAttconnestab());
        }
        resMap.put("无线接通率(QCI=4)", formatData(wireConnQCI4));
        float wireConnQCI5;
        if ((trafficData.getErabNbrattestab_5()) == 0 || (trafficData.getRrcAttconnestab()) == 0) {
            wireConnQCI5 = 100;
        } else {
            wireConnQCI5 = 100 * (trafficData.getErabNbrsuccestab_5()) / (trafficData.getErabNbrattestab_5())
                    * (trafficData.getRrcSuccconnestab()) / (trafficData.getRrcAttconnestab());
        }
        resMap.put("无线接通率(QCI=5)", formatData(wireConnQCI5));
        float wireConnQCI6;
        if ((trafficData.getErabNbrattestab_6()) == 0 || (trafficData.getRrcAttconnestab()) == 0) {
            wireConnQCI6 = 100;
        } else {
            wireConnQCI6 = 100 * (trafficData.getErabNbrsuccestab_6()) / (trafficData.getErabNbrattestab_6())
                    * (trafficData.getRrcSuccconnestab()) / (trafficData.getRrcAttconnestab());
        }
        resMap.put("无线接通率(QCI=6)", formatData(wireConnQCI6));
        float wireConnQCI7;
        if ((trafficData.getErabNbrattestab_7()) == 0 || (trafficData.getRrcAttconnestab()) == 0) {
            wireConnQCI7 = 100;
        } else {
            wireConnQCI7 = 100 * (trafficData.getErabNbrsuccestab_7()) / (trafficData.getErabNbrattestab_7())
                    * (trafficData.getRrcSuccconnestab()) / (trafficData.getRrcAttconnestab());
        }
        resMap.put("无线接通率(QCI=7)", formatData(wireConnQCI7));
        float wireConnQCI8;
        if ((trafficData.getErabNbrattestab_8()) == 0 || (trafficData.getRrcAttconnestab()) == 0) {
            wireConnQCI8 = 100;
        } else {
            wireConnQCI8 = 100 * (trafficData.getErabNbrsuccestab_8()) / (trafficData.getErabNbrattestab_8())
                    * (trafficData.getRrcSuccconnestab()) / (trafficData.getRrcAttconnestab());
        }
        resMap.put("无线接通率(QCI=8)", formatData(wireConnQCI8));
        float wireConnQCI9;
        if ((trafficData.getErabNbrattestab_9()) == 0 || (trafficData.getRrcAttconnestab()) == 0) {
            wireConnQCI9 = 100;
        } else {
            wireConnQCI9 = 100 * (trafficData.getErabNbrsuccestab_9()) / (trafficData.getErabNbrattestab_9())
                    * (trafficData.getRrcSuccconnestab()) / (trafficData.getRrcAttconnestab());
        }
        resMap.put("无线接通率(QCI=9)", formatData(wireConnQCI9));
        float erab_DropQCI1_CellLevel;
        if (((trafficData.getErabNbrleft_1()) + (trafficData.getErabNbrsuccestab_1()) + (trafficData.getErabNbrhoinc_1())) == 0) {
            erab_DropQCI1_CellLevel = 100;
        } else {
            erab_DropQCI1_CellLevel = 100 * ((trafficData.getErabNbrreqrelenb_1()) - (trafficData.getErabNbrreqrelenbNormal_1()) + (trafficData.getErabHofail_1()))
                    / ((trafficData.getErabNbrleft_1()) + (trafficData.getErabNbrsuccestab_1()) + (trafficData.getErabNbrhoinc_1()));
        }
        resMap.put("E-RAB掉线率(QCI=1)(小区级)", formatData(erab_DropQCI1_CellLevel));
        float erab_DropQCI2_CellLevel;
        if (((trafficData.getErabNbrleft_2()) + (trafficData.getErabNbrsuccestab_2()) + (trafficData.getErabNbrhoinc_2())) == 0) {
            erab_DropQCI2_CellLevel = 100;
        } else {
            erab_DropQCI2_CellLevel = 100 * ((trafficData.getErabNbrreqrelenb_2()) - (trafficData.getErabNbrreqrelenbNormal_2()) + (trafficData.getErabHofail_2()))
                    / ((trafficData.getErabNbrleft_2()) + (trafficData.getErabNbrsuccestab_2()) + (trafficData.getErabNbrhoinc_2()));
        }
        resMap.put("E-RAB掉线率(QCI=2)(小区级)", formatData(erab_DropQCI2_CellLevel));
        float erab_DropQCI3_CellLevel;
        if (((trafficData.getErabNbrleft_3()) + (trafficData.getErabNbrsuccestab_3()) + (trafficData.getErabNbrhoinc_3())) == 0) {
            erab_DropQCI3_CellLevel = 100;
        } else {
            erab_DropQCI3_CellLevel = 100 * ((trafficData.getErabNbrreqrelenb_3()) - (trafficData.getErabNbrreqrelenbNormal_3()) + (trafficData.getErabHofail_3()))
                    / ((trafficData.getErabNbrleft_3()) + (trafficData.getErabNbrsuccestab_3()) + (trafficData.getErabNbrhoinc_3()));
        }
        resMap.put("E-RAB掉线率(QCI=3)(小区级)", formatData(erab_DropQCI3_CellLevel));
        float erab_DropQCI4_CellLevel;
        if (((trafficData.getErabNbrleft_4()) + (trafficData.getErabNbrsuccestab_4()) + (trafficData.getErabNbrhoinc_4())) == 0) {
            erab_DropQCI4_CellLevel = 100;
        } else {
            erab_DropQCI4_CellLevel = 100 * ((trafficData.getErabNbrreqrelenb_4()) - (trafficData.getErabNbrreqrelenbNormal_4()) + (trafficData.getErabHofail_4()))
                    / ((trafficData.getErabNbrleft_4()) + (trafficData.getErabNbrsuccestab_4()) + (trafficData.getErabNbrhoinc_4()));
        }
        resMap.put("E-RAB掉线率(QCI=4)(小区级)", formatData(erab_DropQCI4_CellLevel));
        float erab_DropQCI5_CellLevel;
        if (((trafficData.getErabNbrleft_5()) + (trafficData.getErabNbrsuccestab_5()) + (trafficData.getErabNbrhoinc_5())) == 0) {
            erab_DropQCI5_CellLevel = 100;
        } else {
            erab_DropQCI5_CellLevel = 100 * ((trafficData.getErabNbrreqrelenb_5()) - (trafficData.getErabNbrreqrelenbNormal_5()) + (trafficData.getErabHofail_5()))
                    / ((trafficData.getErabNbrleft_5()) + (trafficData.getErabNbrsuccestab_5()) + (trafficData.getErabNbrhoinc_5()));
        }
        resMap.put("E-RAB掉线率(QCI=5)(小区级)", formatData(erab_DropQCI5_CellLevel));
        float erab_DropQCI6_CellLevel;
        if (((trafficData.getErabNbrleft_6()) + (trafficData.getErabNbrsuccestab_6()) + (trafficData.getErabNbrhoinc_6())) == 0) {
            erab_DropQCI6_CellLevel = 100;
        } else {
            erab_DropQCI6_CellLevel = 100 * ((trafficData.getErabNbrreqrelenb_6()) - (trafficData.getErabNbrreqrelenbNormal_6()) + (trafficData.getErabHofail_6()))
                    / ((trafficData.getErabNbrleft_6()) + (trafficData.getErabNbrsuccestab_6()) + (trafficData.getErabNbrhoinc_6()));
        }
        resMap.put("E-RAB掉线率(QCI=6)(小区级)", formatData(erab_DropQCI6_CellLevel));
        float erab_DropQCI7_CellLevel;
        if (((trafficData.getErabNbrleft_7()) + (trafficData.getErabNbrsuccestab_7()) + (trafficData.getErabNbrhoinc_7())) == 0) {
            erab_DropQCI7_CellLevel = 100;
        } else {
            erab_DropQCI7_CellLevel = 100 * ((trafficData.getErabNbrreqrelenb_7()) - (trafficData.getErabNbrreqrelenbNormal_7()) + (trafficData.getErabHofail_7()))
                    / ((trafficData.getErabNbrleft_7()) + (trafficData.getErabNbrsuccestab_7()) + (trafficData.getErabNbrhoinc_7()));
        }
        resMap.put("E-RAB掉线率(QCI=7)(小区级)", formatData(erab_DropQCI7_CellLevel));
        float erab_DropQCI8_CellLevel;
        if (((trafficData.getErabNbrleft_8()) + (trafficData.getErabNbrsuccestab_8()) + (trafficData.getErabNbrhoinc_8())) == 0) {
            erab_DropQCI8_CellLevel = 100;
        } else {
            erab_DropQCI8_CellLevel = 100 * ((trafficData.getErabNbrreqrelenb_8()) - (trafficData.getErabNbrreqrelenbNormal_8()) + (trafficData.getErabHofail_8()))
                    / ((trafficData.getErabNbrleft_8()) + (trafficData.getErabNbrsuccestab_8()) + (trafficData.getErabNbrhoinc_8()));
        }
        resMap.put("E-RAB掉线率(QCI=8)(小区级)", formatData(erab_DropQCI8_CellLevel));
        float erab_DropQCI9_CellLevel;
        if (((trafficData.getErabNbrleft_9()) + (trafficData.getErabNbrsuccestab_9()) + (trafficData.getErabNbrhoinc_9())) == 0) {
            erab_DropQCI9_CellLevel = 100;
        } else {
            erab_DropQCI9_CellLevel = 100 * ((trafficData.getErabNbrreqrelenb_9()) - (trafficData.getErabNbrreqrelenbNormal_9()) + (trafficData.getErabHofail_9()))
                    / ((trafficData.getErabNbrleft_9()) + (trafficData.getErabNbrsuccestab_9()) + (trafficData.getErabNbrhoinc_9()));
        }
        resMap.put("E-RAB掉线率(QCI=9)(小区级)", formatData(erab_DropQCI9_CellLevel));
        float erab_DropQCI1;
        if ((trafficData.getErabNbrsuccestab_1()) == 0) {
            erab_DropQCI1 = 100;
        } else {
            erab_DropQCI1 = ((trafficData.getErabNbrreqrelenb_1()) - (trafficData.getErabNbrreqrelenbNormal_1()) + (trafficData.getErabHofail_1())) / (trafficData.getErabNbrsuccestab_1());
        }
        resMap.put("E-RAB掉线率(QCI=1)", formatData(erab_DropQCI1));
        float erab_DropQCI2;
        if ((trafficData.getErabNbrsuccestab_2()) == 0) {
            erab_DropQCI2 = 100;
        } else {
            erab_DropQCI2 = ((trafficData.getErabNbrreqrelenb_2()) - (trafficData.getErabNbrreqrelenbNormal_2()) + (trafficData.getErabHofail_2())) / (trafficData.getErabNbrsuccestab_2());
        }
        resMap.put("E-RAB掉线率(QCI=2)", formatData(erab_DropQCI2));
        float erab_DropQCI3;
        if ((trafficData.getErabNbrsuccestab_3()) == 0) {
            erab_DropQCI3 = 100;
        } else {
            erab_DropQCI3 = ((trafficData.getErabNbrreqrelenb_3()) - (trafficData.getErabNbrreqrelenbNormal_3()) + (trafficData.getErabHofail_3())) / (trafficData.getErabNbrsuccestab_3());
        }
        resMap.put("E-RAB掉线率(QCI=3)", formatData(erab_DropQCI3));
        float erab_DropQCI4;
        if ((trafficData.getErabNbrsuccestab_4()) == 0) {
            erab_DropQCI4 = 100;
        } else {
            erab_DropQCI4 = ((trafficData.getErabNbrreqrelenb_4()) - (trafficData.getErabNbrreqrelenbNormal_4()) + (trafficData.getErabHofail_4())) / (trafficData.getErabNbrsuccestab_4());
        }
        resMap.put("E-RAB掉线率(QCI=4)", formatData(erab_DropQCI4));
        float erab_DropQCI5;
        if ((trafficData.getErabNbrsuccestab_5()) == 0) {
            erab_DropQCI5 = 100;
        } else {
            erab_DropQCI5 = ((trafficData.getErabNbrreqrelenb_5()) - (trafficData.getErabNbrreqrelenbNormal_5()) + (trafficData.getErabHofail_5())) / (trafficData.getErabNbrsuccestab_5());
        }
        resMap.put("E-RAB掉线率(QCI=5)", formatData(erab_DropQCI5));
        float erab_DropQCI6;
        if ((trafficData.getErabNbrsuccestab_6()) == 0) {
            erab_DropQCI6 = 100;
        } else {
            erab_DropQCI6 = ((trafficData.getErabNbrreqrelenb_6()) - (trafficData.getErabNbrreqrelenbNormal_6()) + (trafficData.getErabHofail_6())) / (trafficData.getErabNbrsuccestab_6());
        }
        resMap.put("E-RAB掉线率(QCI=6)", formatData(erab_DropQCI6));
        float erab_DropQCI7;
        if ((trafficData.getErabNbrsuccestab_7()) == 0) {
            erab_DropQCI7 = 100;
        } else {
            erab_DropQCI7 = ((trafficData.getErabNbrreqrelenb_7()) - (trafficData.getErabNbrreqrelenbNormal_7()) + (trafficData.getErabHofail_7())) / (trafficData.getErabNbrsuccestab_7());
        }
        resMap.put("E-RAB掉线率(QCI=7)", formatData(erab_DropQCI7));
        float erab_DropQCI8;
        if ((trafficData.getErabNbrsuccestab_8()) == 0) {
            erab_DropQCI8 = 100;
        } else {
            erab_DropQCI8 = ((trafficData.getErabNbrreqrelenb_8()) - (trafficData.getErabNbrreqrelenbNormal_8()) + (trafficData.getErabHofail_8())) / (trafficData.getErabNbrsuccestab_8());
        }
        resMap.put("E-RAB掉线率(QCI=8)", formatData(erab_DropQCI8));
        float erab_DropQCI9;
        if ((trafficData.getErabNbrsuccestab_9()) == 0) {
            erab_DropQCI9 = 100;
        } else {
            erab_DropQCI9 = ((trafficData.getErabNbrreqrelenb_9()) - (trafficData.getErabNbrreqrelenbNormal_9()) + (trafficData.getErabHofail_9())) / (trafficData.getErabNbrsuccestab_9());
        }
        resMap.put("E-RAB掉线率(QCI=9)", formatData(erab_DropQCI9));
        float emUplinkSerBytesQCI1;
        emUplinkSerBytesQCI1 = (trafficData.getPdcpUpoctul_1());
        resMap.put("空口业务上行字节数(QCI=1)", emUplinkSerBytesQCI1);
        float emUplinkSerBytesQCI2;
        emUplinkSerBytesQCI2 = (trafficData.getPdcpUpoctul_2());
        resMap.put("空口业务上行字节数(QCI=2)", emUplinkSerBytesQCI2);
        float emUplinkSerBytesQCI3;
        emUplinkSerBytesQCI3 = (trafficData.getPdcpUpoctul_3());
        resMap.put("空口业务上行字节数(QCI=3)", emUplinkSerBytesQCI3);
        float emUplinkSerBytesQCI4;
        emUplinkSerBytesQCI4 = (trafficData.getPdcpUpoctul_4());
        resMap.put("空口业务上行字节数(QCI=4)", emUplinkSerBytesQCI4);
        float emUplinkSerBytesQCI5;
        emUplinkSerBytesQCI5 = (trafficData.getPdcpUpoctul_5());
        resMap.put("空口业务上行字节数(QCI=5)", emUplinkSerBytesQCI5);
        float emUplinkSerBytesQCI6;
        emUplinkSerBytesQCI6 = (trafficData.getPdcpUpoctul_6());
        resMap.put("空口业务上行字节数(QCI=6)", emUplinkSerBytesQCI6);
        float emUplinkSerBytesQCI7;
        emUplinkSerBytesQCI7 = (trafficData.getPdcpUpoctul_7());
        resMap.put("空口业务上行字节数(QCI=7)", emUplinkSerBytesQCI7);
        float emUplinkSerBytesQCI8;
        emUplinkSerBytesQCI8 = (trafficData.getPdcpUpoctul_8());
        resMap.put("空口业务上行字节数(QCI=8)", emUplinkSerBytesQCI8);
        float emUplinkSerBytesQCI9;
        emUplinkSerBytesQCI9 = (trafficData.getPdcpUpoctul_9());
        resMap.put("空口业务上行字节数(QCI=9)", emUplinkSerBytesQCI9);
        float emDownlinkSerBytesQCI1;
        emDownlinkSerBytesQCI1 = (trafficData.getPdcpUpoctdl_1());
        resMap.put("空口业务下行字节数(QCI=1)", emDownlinkSerBytesQCI1);
        float emDownlinkSerBytesQCI2;
        emDownlinkSerBytesQCI2 = (trafficData.getPdcpUpoctdl_2());
        resMap.put("空口业务下行字节数(QCI=2)", emDownlinkSerBytesQCI2);
        float emDownlinkSerBytesQCI3;
        emDownlinkSerBytesQCI3 = (trafficData.getPdcpUpoctdl_3());
        resMap.put("空口业务下行字节数(QCI=3)", emDownlinkSerBytesQCI3);
        float emDownlinkSerBytesQCI4;
        emDownlinkSerBytesQCI4 = (trafficData.getPdcpUpoctdl_4());
        resMap.put("空口业务下行字节数(QCI=4)", emDownlinkSerBytesQCI4);
        float emDownlinkSerBytesQCI5;
        emDownlinkSerBytesQCI5 = (trafficData.getPdcpUpoctdl_5());
        resMap.put("空口业务下行字节数(QCI=5)", emDownlinkSerBytesQCI5);
        float emDownlinkSerBytesQCI6;
        emDownlinkSerBytesQCI6 = (trafficData.getPdcpUpoctdl_6());
        resMap.put("空口业务下行字节数(QCI=6)", emDownlinkSerBytesQCI6);
        float emDownlinkSerBytesQCI7;
        emDownlinkSerBytesQCI7 = (trafficData.getPdcpUpoctdl_7());
        resMap.put("空口业务下行字节数(QCI=7）", emDownlinkSerBytesQCI7);
        float emDownlinkSerBytesQCI8;
        emDownlinkSerBytesQCI8 = (trafficData.getPdcpUpoctdl_8());
        resMap.put("空口业务下行字节数(QCI=8)", emDownlinkSerBytesQCI8);
        float emDownlinkSerBytesQCI9;
        emDownlinkSerBytesQCI9 = (trafficData.getPdcpUpoctdl_9());
        resMap.put("空口业务下行字节数(QCI=9)", emDownlinkSerBytesQCI9);
        float wireDrop_CellLevel;
        if ((trafficData.getContextSuccinitalsetup()) == 0) {
            wireDrop_CellLevel = 0;
        } else {
            wireDrop_CellLevel = ((trafficData.getContextAttrelenb()) - (trafficData.getContextAttrelenbNormal())) / (trafficData.getContextSuccinitalsetup()) * 100;
        }
        resMap.put("无线掉线率(小区级)", formatData(wireDrop_CellLevel));
        float erab_EstabSucc_SuccTimes;
        erab_EstabSucc_SuccTimes = (trafficData.getErabNbrsuccestab());
        resMap.put("E-RAB建立成功次数", erab_EstabSucc_SuccTimes);
        float erab_EstabSucc_ReqTimes;
        erab_EstabSucc_ReqTimes = (trafficData.getErabNbrattestab());
        resMap.put("E-RAB建立请求次数", erab_EstabSucc_ReqTimes);
        float erab_Drop_ReqTimes_CellLevel;
        erab_Drop_ReqTimes_CellLevel = (trafficData.getErabNbrsuccestab()) + (trafficData.getHoSuccoutinterenbs1()) + (trafficData.getHoSuccoutinterenbx2()) + (trafficData.getHoAttoutintraenb());
        resMap.put("E-RAB请求次数(小区级)", erab_Drop_ReqTimes_CellLevel);
        float switchSucc_SuccTimes;
        switchSucc_SuccTimes = (trafficData.getHoSuccoutinterenbs1()) + (trafficData.getHoSuccoutinterenbx2()) + (trafficData.getHoSuccoutintraenb());
        resMap.put("切换成功次数", switchSucc_SuccTimes);
        float switchSucc_ReqTimes;
        switchSucc_ReqTimes = (trafficData.getHoAttoutinterenbs1()) + (trafficData.getHoAttoutinterenbx2()) + (trafficData.getHoAttoutintraenb());
        resMap.put("切换请求次数", switchSucc_ReqTimes);
        float wireDrop_ReqTimes_CellLevel;
        wireDrop_ReqTimes_CellLevel = (trafficData.getContextSuccinitalsetup());
        resMap.put("无线请求次数(小区级)", wireDrop_ReqTimes_CellLevel);
        float wireConn_ReqTimes;
        wireConn_ReqTimes = (trafficData.getErabNbrattestab()) * (trafficData.getRrcAttconnestab());
        resMap.put("无线接通请求次数", wireConn_ReqTimes);
        float erab_Drop_DropTimes_CellLevel;
        erab_Drop_DropTimes_CellLevel = (trafficData.getErabNbrreqrelenb()) - (trafficData.getErabNbrreqrelenbNormal()) + (trafficData.getErabHofail());
        resMap.put("E-RAB掉线次数(小区级)", erab_Drop_DropTimes_CellLevel);
        float wireConn_SuccTimes;
        wireConn_SuccTimes = (trafficData.getErabNbrsuccestab()) * (trafficData.getRrcSuccconnestab());
        resMap.put("无线接通成功次数", wireConn_SuccTimes);
        float rrc_ConnEstabSucc_SuccTimes;
        rrc_ConnEstabSucc_SuccTimes = (trafficData.getRrcSuccconnestab());
        resMap.put("RRC连接建立成功次数", rrc_ConnEstabSucc_SuccTimes);
        float rrc_ConnEstabSucc_ReqTimes;
        rrc_ConnEstabSucc_ReqTimes = (trafficData.getRrcAttconnestab());
        resMap.put("RRC连接建立请求次数", rrc_ConnEstabSucc_ReqTimes);
        float wireDrop_DropTimes_CellLevel;
        wireDrop_DropTimes_CellLevel = (trafficData.getContextAttrelenb()) - (trafficData.getContextAttrelenbNormal());
        resMap.put("无线掉线次数(小区级)", wireDrop_DropTimes_CellLevel);
        log.debug("res={}", resMap);
        return resMap;
    }

    public Map<String, Object> getCellRecord(String cellIds) {
        Map<String, Object> res = new HashMap<>();
        List<String> list = new ArrayList<>();
        List<LteTrafficData> data = lteTrafficDataRepository.findByCellIdInOrderByLteTrafficDesc_BeginTimeDesc(
                cellIds.split(","));

        int count;
        float erab_Drop_CellLevel;
        float erab_EstabSucc;
        float rrc_ConnEstabSucc;
        float wireConn;
        float wireDrop_CellLevel;
        float switchSucc;
        for (LteTrafficData trafficData : data) {
            count = 0;

            if ((trafficData.getErabNbrsuccestab()) == 0) {
                erab_Drop_CellLevel = 0;
            } else {
                erab_Drop_CellLevel = 100 * ((trafficData.getErabNbrreqrelenb()) - (trafficData.getErabNbrreqrelenbNormal()) + (trafficData.getErabHofail()))
                        / ((trafficData.getErabNbrsuccestab()) + (trafficData.getHoSuccoutinterenbs1()) + (trafficData.getHoSuccoutinterenbx2()) + (trafficData.getHoAttoutintraenb()));
            }

            if (trafficData.getErabNbrattestab() == 0) {
                erab_EstabSucc = 100;
            } else {
                erab_EstabSucc = 100 * (trafficData.getErabNbrsuccestab()) / (trafficData.getErabNbrattestab());
            }

            if (trafficData.getRrcAttconnestab() == 0) {
                rrc_ConnEstabSucc = 100;
            } else {
                rrc_ConnEstabSucc = 100 * trafficData.getRrcSuccconnestab() / trafficData.getRrcAttconnestab();
            }

            if ((trafficData.getErabNbrattestab()) * (trafficData.getRrcAttconnestab()) == 0 || (trafficData.getRrcAttconnestab()) == 0) {
                wireConn = 100;
            } else {
                wireConn = 100 * (trafficData.getErabNbrsuccestab()) / (trafficData.getErabNbrattestab()) * (trafficData.getRrcSuccconnestab()) / (trafficData.getRrcAttconnestab());
            }

            if ((trafficData.getContextSuccinitalsetup()) == 0) {
                wireDrop_CellLevel = 0;
            } else {
                wireDrop_CellLevel = ((trafficData.getContextAttrelenb()) - (trafficData.getContextAttrelenbNormal())) / (trafficData.getContextSuccinitalsetup()) * 100;
            }

            if (((trafficData.getHoAttoutinterenbs1()) + (trafficData.getHoAttoutinterenbx2()) + (trafficData.getHoAttoutintraenb())) == 0) {
                switchSucc = 100;
            } else {
                switchSucc = 100 * ((trafficData.getHoSuccoutinterenbs1()) + (trafficData.getHoSuccoutinterenbx2()) + (trafficData.getHoSuccoutintraenb()))
                        / ((trafficData.getHoAttoutinterenbs1()) + (trafficData.getHoAttoutinterenbx2()) + (trafficData.getHoAttoutintraenb()));
            }

            if (erab_Drop_CellLevel >= 10 && trafficData.getErabNbrsuccestab() > 20) {
                count++;
            }

            if (erab_EstabSucc <= 85 && trafficData.getErabNbrattestab() > 20) {
                count++;
            }

            if (rrc_ConnEstabSucc <= 85 && trafficData.getRrcAttconnestab() > 20) {
                count++;
            }

            if (trafficData.getRrcAttconnestab() > 3 && (trafficData.getPdcpUpoctul() + trafficData.getPdcpUpoctdl() == 0)) {
                count++;
            }

            if (trafficData.getRrcAttconnestab() > 100 && wireConn <= 70) {
                count++;
            }

            if (rrc_ConnEstabSucc < 70 && trafficData.getRrcAttconnestab() - trafficData.getRrcSuccconnestab() > 200) {
                count++;
            }

            if (wireDrop_CellLevel > 50 && (trafficData.getContextAttrelenb() - trafficData.getContextAttrelenbNormal() > 200)) {
                count++;
            }

            if (trafficData.getRrcAttconnestab() - trafficData.getRrcSuccconnestab() > 100) {
                count++;
            }

            if (trafficData.getContextAttrelenb() - trafficData.getContextAttrelenbNormal() > 150) {
                count++;
            }

            if (trafficData.getRrcAttconnestab() - trafficData.getRrcSuccconnestab() +
                    trafficData.getErabNbrattestab() - trafficData.getErabNbrsuccestab() > 200) {
                count++;
            }

            if (switchSucc <= 60 && trafficData.getHoAttoutinterenbs1() + trafficData.getHoAttoutinterenbx2() + trafficData.getHoAttoutintraenb() > 100) {
                count++;
            }

            if ((trafficData.getHoAttoutinterenbs1() + trafficData.getHoAttoutinterenbx2() + trafficData.getHoAttoutintraenb()) -
                    (trafficData.getHoSuccoutinterenbs1() + trafficData.getHoSuccoutinterenbx2() + trafficData.getHoSuccoutintraenb()) >= 150) {
                count++;
            }

            if (wireDrop_CellLevel >= 20 && trafficData.getRrcAttconnestab() + trafficData.getErabNbrattestab() > 100) {
                count++;
            }

            if (count > 0) {
                list.add(trafficData.getLteTrafficDesc().getBeginTime() + trafficData.getPmUserLabel());
            }

        }
        res.put("res", lteTrafficDataRepository.findByCellIdInOrderByLteTrafficDesc_BeginTimeDesc(
                cellIds.split(",")).stream().collect(groupingBy(LteTrafficData::getPmUserLabel)));
        res.put("problem", list);
        return res;
    }

    public List<Map<String, Object>> getProblemCell(long areaId) {
        List<Map<String, Object>> res = new ArrayList<>();
        Map<String, Object> resMap;
        List<LteTrafficData> data = lteTrafficDataRepository.findByLteTrafficDesc_AreaId(areaId);
        int count;
        float erab_Drop_CellLevel;
        float erab_EstabSucc;
        float rrc_ConnEstabSucc;
        float wireConn;
        float wireDrop_CellLevel;
        float switchSucc;
        for (LteTrafficData trafficData : data) {
            count = 0;
            resMap = new LinkedHashMap<>();

            if ((trafficData.getErabNbrsuccestab()) == 0) {
                erab_Drop_CellLevel = 0;
            } else {
                erab_Drop_CellLevel = 100 * ((trafficData.getErabNbrreqrelenb()) - (trafficData.getErabNbrreqrelenbNormal()) + (trafficData.getErabHofail()))
                        / ((trafficData.getErabNbrsuccestab()) + (trafficData.getHoSuccoutinterenbs1()) + (trafficData.getHoSuccoutinterenbx2()) + (trafficData.getHoAttoutintraenb()));
            }

            if (trafficData.getErabNbrattestab() == 0) {
                erab_EstabSucc = 100;
            } else {
                erab_EstabSucc = 100 * (trafficData.getErabNbrsuccestab()) / (trafficData.getErabNbrattestab());
            }

            if (trafficData.getRrcAttconnestab() == 0) {
                rrc_ConnEstabSucc = 100;
            } else {
                rrc_ConnEstabSucc = 100 * trafficData.getRrcSuccconnestab() / trafficData.getRrcAttconnestab();
            }

            if ((trafficData.getErabNbrattestab()) * (trafficData.getRrcAttconnestab()) == 0 || (trafficData.getRrcAttconnestab()) == 0) {
                wireConn = 100;
            } else {
                wireConn = 100 * (trafficData.getErabNbrsuccestab()) / (trafficData.getErabNbrattestab()) * (trafficData.getRrcSuccconnestab()) / (trafficData.getRrcAttconnestab());
            }

            if ((trafficData.getContextSuccinitalsetup()) == 0) {
                wireDrop_CellLevel = 0;
            } else {
                wireDrop_CellLevel = ((trafficData.getContextAttrelenb()) - (trafficData.getContextAttrelenbNormal())) / (trafficData.getContextSuccinitalsetup()) * 100;
            }

            if (((trafficData.getHoAttoutinterenbs1()) + (trafficData.getHoAttoutinterenbx2()) + (trafficData.getHoAttoutintraenb())) == 0) {
                switchSucc = 100;
            } else {
                switchSucc = 100 * ((trafficData.getHoSuccoutinterenbs1()) + (trafficData.getHoSuccoutinterenbx2()) + (trafficData.getHoSuccoutintraenb()))
                        / ((trafficData.getHoAttoutinterenbs1()) + (trafficData.getHoAttoutinterenbx2()) + (trafficData.getHoAttoutintraenb()));
            }

            resMap.put("小区名", trafficData.getPmUserLabel() != null ? trafficData.getPmUserLabel() : "未知小区名字");
            resMap.put("小区ID", trafficData.getCellId());
            resMap.put("测量开始时间", trafficData.getLteTrafficDesc().getBeginTime());
            resMap.put("测量结束时间", trafficData.getLteTrafficDesc().getEndTime());

            if (erab_Drop_CellLevel >= 10 && trafficData.getErabNbrsuccestab() > 20) {
                count++;
                resMap.put("E-RAB掉线率", "异常");
            } else {
                resMap.put("E-RAB掉线率", "正常");
            }

            if (erab_EstabSucc <= 85 && trafficData.getErabNbrattestab() > 20) {
                count++;
                resMap.put("E-RAB建立成功率", "异常");
            } else {
                resMap.put("E-RAB建立成功率", "正常");
            }

            if (rrc_ConnEstabSucc <= 85 && trafficData.getRrcAttconnestab() > 20) {
                count++;
                resMap.put("RRC连接建立成功率", "异常");
            } else {
                resMap.put("RRC连接建立成功率", "正常");
            }

            if (trafficData.getRrcAttconnestab() > 3 && (trafficData.getPdcpUpoctul() + trafficData.getPdcpUpoctdl() == 0)) {
                count++;
                resMap.put("零流量零话务", "异常");
            } else {
                resMap.put("零流量零话务", "正常");
            }

            if (trafficData.getRrcAttconnestab() > 100 && wireConn <= 70) {
                count++;
                resMap.put("无线接通率", "异常");
            } else {
                resMap.put("无线接通率", "正常");
            }

            if (rrc_ConnEstabSucc < 70 && trafficData.getRrcAttconnestab() - trafficData.getRrcSuccconnestab() > 200) {
                count++;
                resMap.put("CellBar_RRC连接失败次数", "异常");
            } else {
                resMap.put("CellBar_RRC连接失败次数", "正常");
            }

            if (wireDrop_CellLevel > 50 && (trafficData.getContextAttrelenb() - trafficData.getContextAttrelenbNormal() > 200)) {
                count++;
                resMap.put("CellBar_掉线次数", "异常");
            } else {
                resMap.put("CellBar_掉线次数", "正常");
            }

            if (trafficData.getRrcAttconnestab() - trafficData.getRrcSuccconnestab() > 100) {
                count++;
                resMap.put("RRC连接失败次数", "异常");
            } else {
                resMap.put("RRC连接失败次数", "正常");
            }

            if (trafficData.getContextAttrelenb() - trafficData.getContextAttrelenbNormal() > 150) {
                count++;
                resMap.put("掉线次数", "异常");
            } else {
                resMap.put("掉线次数", "正常");
            }

            if (trafficData.getRrcAttconnestab() - trafficData.getRrcSuccconnestab() +
                    trafficData.getErabNbrattestab() - trafficData.getErabNbrsuccestab() > 200) {
                count++;
                resMap.put("接通失败次数", "异常");
            } else {
                resMap.put("接通失败次数", "正常");
            }

            if (switchSucc <= 60 && trafficData.getHoAttoutinterenbs1() + trafficData.getHoAttoutinterenbx2() + trafficData.getHoAttoutintraenb() > 100) {
                count++;
                resMap.put("切换成功率", "异常");
            } else {
                resMap.put("切换成功率", "正常");
            }

            if ((trafficData.getHoAttoutinterenbs1() + trafficData.getHoAttoutinterenbx2() + trafficData.getHoAttoutintraenb()) -
                    (trafficData.getHoSuccoutinterenbs1() + trafficData.getHoSuccoutinterenbx2() + trafficData.getHoSuccoutintraenb()) >= 150) {
                count++;
                resMap.put("切换失败次数", "异常");
            } else {
                resMap.put("切换失败次数", "正常");
            }

            if (wireDrop_CellLevel >= 20 && trafficData.getRrcAttconnestab() + trafficData.getErabNbrattestab() > 100) {
                count++;
                resMap.put("无线掉线率", "异常");
            } else {
                resMap.put("无线掉线率", "正常");
            }

            if (count > 0) {
                res.add(resMap);
            }

        }

        return res;
    }

    public Map<String, Object> getOneProblemCell(String cellId, String beginTime) {
        Map<String, Object> resMap = new LinkedHashMap<>();
        LteTrafficData trafficData = lteTrafficDataRepository
                .findByCellIdAndLteTrafficDesc_BeginTime(cellId, LocalDateTimeToUdate(beginTime)).get(0);
        float erab_Drop_CellLevel;
        float erab_EstabSucc;
        float rrc_ConnEstabSucc;
        float wireConn;
        float wireDrop_CellLevel;
        float switchSucc;

        if ((trafficData.getErabNbrsuccestab()) == 0) {
            erab_Drop_CellLevel = 0;
        } else {
            erab_Drop_CellLevel = 100 * ((trafficData.getErabNbrreqrelenb()) - (trafficData.getErabNbrreqrelenbNormal()) + (trafficData.getErabHofail()))
                    / ((trafficData.getErabNbrsuccestab()) + (trafficData.getHoSuccoutinterenbs1()) + (trafficData.getHoSuccoutinterenbx2()) + (trafficData.getHoAttoutintraenb()));
        }

        if (trafficData.getErabNbrattestab() == 0) {
            erab_EstabSucc = 100;
        } else {
            erab_EstabSucc = 100 * (trafficData.getErabNbrsuccestab()) / (trafficData.getErabNbrattestab());
        }

        if (trafficData.getRrcAttconnestab() == 0) {
            rrc_ConnEstabSucc = 100;
        } else {
            rrc_ConnEstabSucc = 100 * trafficData.getRrcSuccconnestab() / trafficData.getRrcAttconnestab();
        }

        if ((trafficData.getErabNbrattestab()) * (trafficData.getRrcAttconnestab()) == 0 || (trafficData.getRrcAttconnestab()) == 0) {
            wireConn = 100;
        } else {
            wireConn = 100 * (trafficData.getErabNbrsuccestab()) / (trafficData.getErabNbrattestab()) * (trafficData.getRrcSuccconnestab()) / (trafficData.getRrcAttconnestab());
        }

        if ((trafficData.getContextSuccinitalsetup()) == 0) {
            wireDrop_CellLevel = 0;
        } else {
            wireDrop_CellLevel = ((trafficData.getContextAttrelenb()) - (trafficData.getContextAttrelenbNormal())) / (trafficData.getContextSuccinitalsetup()) * 100;
        }

        if (((trafficData.getHoAttoutinterenbs1()) + (trafficData.getHoAttoutinterenbx2()) + (trafficData.getHoAttoutintraenb())) == 0) {
            switchSucc = 100;
        } else {
            switchSucc = 100 * ((trafficData.getHoSuccoutinterenbs1()) + (trafficData.getHoSuccoutinterenbx2()) + (trafficData.getHoSuccoutintraenb()))
                    / ((trafficData.getHoAttoutinterenbs1()) + (trafficData.getHoAttoutinterenbx2()) + (trafficData.getHoAttoutintraenb()));
        }

        resMap.put("小区名", trafficData.getPmUserLabel());
        resMap.put("小区ID", trafficData.getCellId());
        resMap.put("测量开始时间", trafficData.getLteTrafficDesc().getBeginTime());
        resMap.put("测量结束时间", trafficData.getLteTrafficDesc().getEndTime());

        if (erab_Drop_CellLevel >= 10 && trafficData.getErabNbrsuccestab() > 20) {
            resMap.put("E-RAB掉线率", "异常");
        } else {
            resMap.put("E-RAB掉线率", "正常");
        }

        if (erab_EstabSucc <= 85 && trafficData.getErabNbrattestab() > 20) {
            resMap.put("E-RAB建立成功率", "异常");
        } else {
            resMap.put("E-RAB建立成功率", "正常");
        }

        if (rrc_ConnEstabSucc <= 85 && trafficData.getRrcAttconnestab() > 20) {
            resMap.put("RRC连接建立成功率", "异常");
        } else {
            resMap.put("RRC连接建立成功率", "正常");
        }

        if (trafficData.getRrcAttconnestab() > 3 && (trafficData.getPdcpUpoctul() + trafficData.getPdcpUpoctdl() == 0)) {
            resMap.put("零流量零话务", "异常");
        } else {
            resMap.put("零流量零话务", "正常");
        }

        if (trafficData.getRrcAttconnestab() > 100 && wireConn <= 70) {
            resMap.put("无线接通率", "异常");
        } else {
            resMap.put("无线接通率", "正常");
        }

        if (rrc_ConnEstabSucc < 70 && trafficData.getRrcAttconnestab() - trafficData.getRrcSuccconnestab() > 200) {
            resMap.put("CellBar_RRC连接失败次数", "异常");
        } else {
            resMap.put("CellBar_RRC连接失败次数", "正常");
        }

        if (wireDrop_CellLevel > 50 && (trafficData.getContextAttrelenb() - trafficData.getContextAttrelenbNormal() > 200)) {
            resMap.put("CellBar_掉线次数", "异常");
        } else {
            resMap.put("CellBar_掉线次数", "正常");
        }

        if (trafficData.getRrcAttconnestab() - trafficData.getRrcSuccconnestab() > 100) {
            resMap.put("RRC连接失败次数", "异常");
        } else {
            resMap.put("RRC连接失败次数", "正常");
        }

        if (trafficData.getContextAttrelenb() - trafficData.getContextAttrelenbNormal() > 150) {
            resMap.put("掉线次数", "异常");
        } else {
            resMap.put("掉线次数", "正常");
        }

        if (trafficData.getRrcAttconnestab() - trafficData.getRrcSuccconnestab() +
                trafficData.getErabNbrattestab() - trafficData.getErabNbrsuccestab() > 200) {
            resMap.put("接通失败次数", "异常");
        } else {
            resMap.put("接通失败次数", "正常");
        }

        if (switchSucc <= 60 && trafficData.getHoAttoutinterenbs1() + trafficData.getHoAttoutinterenbx2() + trafficData.getHoAttoutintraenb() > 100) {
            resMap.put("切换成功率", "异常");
        } else {
            resMap.put("切换成功率", "正常");
        }

        if ((trafficData.getHoAttoutinterenbs1() + trafficData.getHoAttoutinterenbx2() + trafficData.getHoAttoutintraenb()) -
                (trafficData.getHoSuccoutinterenbs1() + trafficData.getHoSuccoutinterenbx2() + trafficData.getHoSuccoutintraenb()) >= 150) {
            resMap.put("切换失败次数", "异常");
        } else {
            resMap.put("切换失败次数", "正常");
        }

        if (wireDrop_CellLevel >= 20 && trafficData.getRrcAttconnestab() + trafficData.getErabNbrattestab() > 100) {
            resMap.put("无线掉线率", "异常");
        } else {
            resMap.put("无线掉线率", "正常");
        }

        return resMap;
    }

    private Date LocalDateTimeToUdate(String time) {
        LocalDateTime localDateTime = LocalDateTime.parse(time,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    private String formatData(float data) {
        return new DecimalFormat("0.##%").format(data / 100);
    }

}

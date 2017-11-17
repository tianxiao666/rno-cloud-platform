package com.hgicreate.rno.service;

import com.hgicreate.rno.config.Constants;
import com.hgicreate.rno.domain.DtData;
import com.hgicreate.rno.domain.DtNcell;
import com.hgicreate.rno.repository.DtDataRepository;
import com.hgicreate.rno.repository.DtDescRepository;
import com.hgicreate.rno.repository.DtNcellRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
public class LteDtAnalysisService {
    private final DtDataRepository dtDataRepository;
    private final DtNcellRepository dtNcellRepository;
    private final DtDescRepository dtDescRepository;

    public LteDtAnalysisService(DtDataRepository dtDataRepository, DtNcellRepository dtNcellRepository,
                                DtDescRepository dtDescRepository) {
        this.dtDataRepository = dtDataRepository;
        this.dtNcellRepository = dtNcellRepository;
        this.dtDescRepository = dtDescRepository;
    }

    public List<Long> getWeakCoverage(Long[] descId) {
        List<Long> res = new ArrayList<>();
        Set<Long> tmp = new HashSet<>();
        List<DtData> dtData;
        double minDistance = 0;
        DtData firstPoint = new DtData();
        DtData previousPoint = new DtData();
        for (Long id: descId) {
            switch (dtDescRepository.findById(id).getAreaType()) {
                case "城区":
                    minDistance = 50;
                    break;
                case "非城区":
                    minDistance = 120;
                    break;
                case "高速":
                    minDistance = 240;
                    break;
            }

            dtData = dtDataRepository.findByDescIdAndRsrpLessThanOrDescIdAndRsSinrLessThanOrderByIdAsc(
                    id, Constants.WEAK_COVERAGE_RSRP_THRESHOLD, id,
                    Constants.WEAK_COVERAGE_RS_SINR_THRESHOLD);
            tmp.clear();
            for (DtData d: dtData) {
                if(tmp.isEmpty()) {
                    tmp.add(d.getId());
                    previousPoint.setId(d.getId());
                    previousPoint.setLatitude(d.getLatitude());
                    previousPoint.setLongitude(d.getLongitude());
                    firstPoint.setId(d.getId());
                    firstPoint.setLatitude(d.getLatitude());
                    firstPoint.setLongitude(d.getLongitude());
                }else if (d.getId() - previousPoint.getId() > 1) {
                    if(calDistanceByLonLat(previousPoint.getLongitude(), previousPoint.getLatitude(),
                            firstPoint.getLongitude(), firstPoint.getLatitude()) >= minDistance) {
                        res.addAll(tmp);
                    }
                    tmp.clear();
                    tmp.add(d.getId());
                    previousPoint.setId(d.getId());
                    previousPoint.setLatitude(d.getLatitude());
                    previousPoint.setLongitude(d.getLongitude());
                    firstPoint.setId(d.getId());
                    firstPoint.setLatitude(d.getLatitude());
                    firstPoint.setLongitude(d.getLongitude());
                }else {
                    tmp.add(d.getId());
                    previousPoint.setId(d.getId());
                    previousPoint.setLatitude(d.getLatitude());
                    previousPoint.setLongitude(d.getLongitude());
                }
            }
        }
        return res;
    }

    public List<Long> getOverlapCoverage(Long[] descId) {
        List<Long> res = new ArrayList<>();
        List<DtData> dtData;
        List<DtNcell> dtNcell;
        Set<Long> tmp = new HashSet<>();
        int count;
        long lastId = -2;
        for (Long id: descId) {
            dtData = dtDataRepository.findAllByDescIdOrderByIdAsc(id);
            dtNcell = dtNcellRepository.findByDataIdInOrderByDataIdAsc(id);
            tmp.clear();
            for (DtData d: dtData) {
                count = 0;
                for (DtNcell n: dtNcell) {
                    if(n.getDataId().equals(d.getId()) && d.getRsrp() - n.getRsrp() <= 6) {
                        if(count >= 4) {
                            if(!tmp.isEmpty() && d.getId() - lastId > 1) {
                                if(tmp.size() >= 10) {
                                    res.addAll(tmp);
                                }
                                tmp.clear();
                                tmp.add(d.getId());
                                lastId = d.getId();
                            }else {
                                tmp.add(d.getId());
                                lastId = d.getId();
                            }
                        }
                        count++;
                    }
                }
            }
        }
        return res;
    }


    private static double calDistanceByLonLat(double lng1, double lat1, double lng2, double lat2) {
        long R = 6371000; // 地球半径单位为米
        double dLat = (lat2 - lat1) * Math.PI / 180;
        double dLon = (lng2 - lng1) * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

}

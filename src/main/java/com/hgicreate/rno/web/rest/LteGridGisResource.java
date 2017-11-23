package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.GridCoord;
import com.hgicreate.rno.domain.GridData;
import com.hgicreate.rno.repository.AreaRepository;
import com.hgicreate.rno.repository.GridCoordRepository;
import com.hgicreate.rno.repository.GridDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/lte-grid-gis")
public class LteGridGisResource {

    private final GridDataRepository gridDataRepository;
    private final GridCoordRepository gridCoordRepository;
    private final AreaRepository areaRepository;

    public LteGridGisResource(GridDataRepository gridDataRepository,
                              GridCoordRepository gridCoordRepository,
                              AreaRepository areaRepository) {
        this.gridDataRepository = gridDataRepository;
        this.gridCoordRepository = gridCoordRepository;
        this.areaRepository = areaRepository;
    }

    @GetMapping("/grid-data")
    public Map<String, Object> getGridData(String gridType, Long areaId) {
        log.debug("gridType={}, areaId={}", gridType, areaId);
        Map<String, Object> res = new HashMap<>();
        List<GridData> gridData = gridDataRepository.findByGridTypeInAndAreaIdOrderByIdAsc
                (gridType.split(","), areaId);
        res.put("gridData", gridData);
        res.put("gridCoords", gridCoordRepository.findByGridIdInOrderByGridIdAsc(
                gridData.stream().map(GridData::getId).collect(Collectors.toList())));
        return res;
    }

    @GetMapping("/download-grid-data")
    @ResponseBody
    public ResponseEntity<byte[]> download(String type, Long areaId) {

        log.debug("gridType={}, areaId={}", type, areaId);
        String areaName = areaRepository.findById(areaId).getName();
        String now = LocalDate.now().toString();
        File file = new File(now + "-" + areaName + "-"
                + type.replace(",", "-") + "-grid.csv");
        log.debug("filename={}", file.getName());

        List<GridData> gridData = gridDataRepository.findByGridTypeInAndAreaIdOrderByIdAsc
                (type.split(","), areaId);
        List<GridCoord> gridCoords = gridCoordRepository.findByGridIdInOrderByGridIdAsc(
                gridData.stream().map(GridData::getId).collect(Collectors.toList()));

        // 标题头
        String line = "序号,网格类型,网格编码,中心点,网格坐标";

        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw);
            // 写入标题头
            bw.write(line);
            bw.newLine();

            StringBuilder str;
            int count = 0;
            for(GridData data : gridData){
                count++;
                str = new StringBuilder();
                for (GridCoord gridCoord : gridCoords) {
                    if(gridCoord.getGridId().equals(data.getId()))
                        str.append(gridCoord.getLongitude()).append(" ")
                                .append(gridCoord.getLatitude()).append(";");
                }
                bw.write(count + "," + data.getGridType() + "," +
                         data.getGridCode() + "," +
                        data.getCenterCoord().replace(",", " ") + "," + str);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } finally {
            try {
                if (bw != null) {
                    bw.flush();
                }
                if (fw != null) {
                    fw.flush();
                }
                if (bw != null) {
                    bw.close();
                }
                if (fw != null) {
                    fw.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            String fileName = new String(file.getName().getBytes("UTF-8"),
                    "iso-8859-1");// 为了解决中文名称乱码问题
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(FileUtils.readFileToByteArray(file),
                    headers, HttpStatus.CREATED);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}

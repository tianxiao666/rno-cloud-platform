package com.hgicreate.rno.web.rest.gsm;

import com.hgicreate.rno.service.gsm.GsmTrafficService;
import com.hgicreate.rno.service.gsm.dto.GsmTrafficQueryDTO;
import com.hgicreate.rno.web.rest.gsm.vm.GsmTrafficQueryVM;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * @author ke_weixu
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class GsmTrafficResource {
    private final GsmTrafficService gsmTrafficService;

    public GsmTrafficResource(GsmTrafficService gsmTrafficService) {
        this.gsmTrafficService = gsmTrafficService;
    }

    @PostMapping("/gsm-traffic-query")
    public List<GsmTrafficQueryDTO> gsmTrafficQuery(GsmTrafficQueryVM vm){
        return gsmTrafficService.gsmTrafficQuery(vm);
    }

    @PostMapping("/gsm-traffic-downLoad")
    @ResponseBody
    public ResponseEntity<byte[]> gsmTrafficDownLoad(GsmTrafficQueryVM vm) throws ParseException {
        File file = gsmTrafficService.downloadData(vm);
        try {
            HttpHeaders headers = new HttpHeaders();
            String fileName = new String(file.getName().getBytes("UTF-8"),
                    "iso-8859-1");
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } finally {
            if (file.delete()) {
                log.debug("临时文件删除成功。");
            } else {
                log.debug("临时文件删除失败。");
            }
        }
    }
        /*Map<String, List<Map<String, Object>>> map = new LinkedHashMap<>();
        List<Map<String,Object>> res = new ArrayList<Map<String,Object>>();
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<GsmTrafficQueryDTO> list = gsmTrafficService.gsmTrafficQuery(vm);
        for (int i = 0; i < list.size(); i ++) {
            Map<String, Object> map1 = Maps.newHashMap();
            if (list.get(i) != null) {
                BeanMap beanMap = BeanMap.create(list.get(i));
                for (Object key : beanMap.keySet()) {
                    map1.put(key+"", beanMap.get(key));
                }
                res.add(map1);
            }
        }
        String fileName = vm.getSearchType() + sdf.format(today) + "-话务性能指标.csv";
        try {
            fileName = new String(fileName.getBytes("UTF-8"),
                    "iso-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        resp.setContentType("application/x.ms-excel");
        resp.setHeader("Content-disposition", "attachment;filename=" + fileName);
        map.put("话务性能指标", res);
        ExcelFileTool.createExcel(resp, map);
        return res;*/

}

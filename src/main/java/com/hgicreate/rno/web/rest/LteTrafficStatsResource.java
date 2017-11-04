package com.hgicreate.rno.web.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/api/lte-traffic-stats")
public class LteTrafficStatsResource {

    private String importDate ;

    @Autowired
    private Environment env;

    @RequestMapping(method = RequestMethod.POST, value = "/uploadFile")
    @ResponseBody
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile uploadfile) {
        try {
            // Get the filename and build the local file path
            String filename = uploadfile.getOriginalFilename();
            log.info("上传文件："+filename);
            String directory = env.getProperty("spring.http.multipart.location");
            /**根据本地路径创建目录**/
            File fullPathFile = new File(directory);
            if (!fullPathFile.exists())
                fullPathFile.mkdirs();
            /** 获取文件的后缀* */
            String suffix = uploadfile.getOriginalFilename().substring(
                    uploadfile.getOriginalFilename().lastIndexOf("."));
            /** 使用UUID生成文件名称* */
            String fileName = "import-"+UUID.randomUUID().toString() + suffix;
            /** 拼成完整的文件保存路径加文件* */
            String filePath = fullPathFile + File.separator + fileName;
            /** 文件输出流* */
            File file = new File(filePath);
            try (FileOutputStream fileOutputStream = new FileOutputStream(file);
                 BufferedOutputStream stream = new BufferedOutputStream(fileOutputStream)) {
                stream.write(uploadfile.getBytes());
            }
            log.info("上传文件成功");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}

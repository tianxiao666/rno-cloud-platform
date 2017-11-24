package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.App;
import com.hgicreate.rno.repository.AppRepository;
import com.hgicreate.rno.service.AppService;
import com.hgicreate.rno.service.dto.AppDTO;
import com.hgicreate.rno.service.dto.AppNameDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author ke_weixu
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class AppResource {

    @Value("${rno.app-code:rno}")
    private String code;

    private final AppRepository appRepository;

    private final AppService appService;

    public AppResource(AppRepository appRepository, AppService appService) {
        this.appRepository = appRepository;
        this.appService = appService;
    }

    @GetMapping("/app-info")
    public App getAppInfo(HttpServletRequest request) {
        String url = request.getServerName();
        log.debug("URL : {}", url);
        String[] array = url.split("\\.");

        // 查询数据是否有和url前缀同名的应用
        List<App> list = appRepository.findAllByCode(array[0]);

        // 如果有则以返回url前缀的应用
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return appRepository.findAllByCode(code).get(0);
        }
    }

    @GetMapping("/list-app-names")
    public List<AppNameDTO> listAppNames(){

        return appService.getAllName();
    }

    @GetMapping("/get-app-by-id")
    public AppDTO getAppById(Long appId){
        return appService.getAppById(appId);
    }

    @PostMapping("/update-app")
    public Long updateApp(App app){
        return appService.updateApp(app);
    }

    @DeleteMapping("/delete-app-by-id")
    public String deleteAppById(Long appId){
        System.out.println("appId = " + appId);
        return appService.deleteAppById(appId);
    }
}

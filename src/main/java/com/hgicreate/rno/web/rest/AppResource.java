package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.App;
import com.hgicreate.rno.repository.AppRepository;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.AppService;
import com.hgicreate.rno.service.dto.AppDTO;
import com.hgicreate.rno.service.dto.AppNameDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class AppResource {

    private final Environment env;
    private final AppRepository appRepository;
    private final AppService appService;

    @GetMapping("/app-info")
    public Map<String, Object> getAppInfo(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();

        String code = env.getProperty("rno.app-code", "rno");
        // 是否检查前缀来使用不同的应用菜单，缺省为true
        boolean checkUrlPrefix = Boolean.valueOf(env.getProperty("rno.check-url-prefix", "true"));

        // 获取登录名
        map.put("username", SecurityUtils.getCurrentUserLogin());
        map.put("fullName", SecurityUtils.getFullName());
        map.put("roles", SecurityUtils.getRoles());
        map.put("envName", getEnvName());
        map.put("showEnvRibbon", env.getProperty("rno.show-env-ribbon", "true"));

        String url = request.getServerName();
        log.debug("URL : {}", url);
        String[] array = url.split("\\.");

        // 查询数据是否有和url前缀同名的应用
        List<App> list = appRepository.findAllByCode(array[0]);

        // 如果有则以返回url前缀的应用
        if (checkUrlPrefix && list.size() > 0) {
            map.put("app", list.get(0));
        } else {
            map.put("app", appRepository.findAllByCode(code).get(0));
        }

        return map;
    }

    /**
     * 获取环境配置，对可识别的环境返回中文名称，否则显示为未知环境
     */
    private String getEnvName() {
        String envName = "未知环境";

        if (env.getActiveProfiles().length > 0) {
            List<String> list = Arrays.asList(env.getActiveProfiles());
            if (list.contains("dev")) {
                envName = "开发环境";
            } else if (list.contains("ci")) {
                envName = "持续集成环境";
            } else if (list.contains("test")) {
                envName = "测试环境";
            } else if (list.contains("uat")) {
                envName = "用户验收环境";
            } else if (list.contains("prod")) {
                envName = "生产环境";
            }
        }

        return envName;
    }

    @GetMapping("/list-app-names")
    public List<AppNameDTO> listAppNames() {
        return appService.getAllName();
    }

    @GetMapping("/get-app-by-id")
    public AppDTO getAppById(Long appId) {
        return appService.getAppById(appId);
    }

    @PostMapping("/update-app")
    public Long updateApp(App app) {
        return appService.updateApp(app);
    }

    @DeleteMapping("/delete-app-by-id")
    public void deleteAppById(Long appId) {
        appService.deleteAppById(appId);
    }
}

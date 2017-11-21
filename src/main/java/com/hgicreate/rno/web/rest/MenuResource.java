package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.App;
import com.hgicreate.rno.domain.Menu;
import com.hgicreate.rno.repository.AppRepository;
import com.hgicreate.rno.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class MenuResource {

    private final MenuService menuService;
    private final AppRepository appRepository;

    @Value("${rno.app-code}")
    private String code;

    public MenuResource(MenuService menuService, AppRepository appRepository) {
        this.menuService = menuService;
        this.appRepository = appRepository;
    }

    @GetMapping("/query-menus")
    public Map<String, Object> searchAll(HttpServletRequest request) {
        String url = request.getServerName();
        log.debug("URL : {}", url);
        String[] array = url.split("\\.");
        String prefix = array[0];
        // 查询数据是否有和url前缀同名的应用
        List<App> list = appRepository.findAllByCode(prefix);

        Map<String, Object> map = new HashMap<>();

        // 如果有则以返回url前缀的应用的菜单
        if (list.size() > 0) {
            map.put("appId", list.get(0).getId());
            map.put("menus", menuService.getAllInfo(array[0]));
        } else {
            map.put("appId", appRepository.findAllByCode(code).get(0).getId());
            map.put("menus", menuService.getAllInfo(code));
        }

        return map;
    }

    @PostMapping("/submit-menu")
    public String submitMenu(@RequestBody List<Menu> menus, Long appId){
        menuService.delelteAllByAppId(appId);
        for(Menu menuNode: menus){
            menuService.saveData(menuNode);
        }
        return "ok";
    }
}

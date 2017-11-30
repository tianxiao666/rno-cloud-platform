package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.App;
import com.hgicreate.rno.domain.Menu;
import com.hgicreate.rno.repository.AppRepository;
import com.hgicreate.rno.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class MenuResource {

    private final MenuService menuService;
    private final AppRepository appRepository;

    @Value("${rno.app-code}")
    private String appCode;

    public MenuResource(MenuService menuService, AppRepository appRepository) {
        this.menuService = menuService;
        this.appRepository = appRepository;
    }

    @GetMapping("/app-menu")
    public List<Menu> getAppMenu(HttpServletRequest request) {
        // 查询应用程序表是否有和url前缀同名的应用
        String prefix = request.getServerName().split("\\.")[0];
        List<App> list = appRepository.findAllByCode(prefix);

        // 如果有则返回url前缀的应用的菜单，否则返回系统配置的缺省菜单
        return menuService.listMenuByAppCode(list.size() > 0 ? prefix : appCode);
    }

    @GetMapping("/get-menu")
    public List<Menu> getMenu(Long appId) {
        // 如果有则返回url前缀的应用的菜单
        return menuService.listMenuByAppId(appId);
    }

    @PostMapping("/submit-menu")
    public String submitMenu(@RequestBody List<Menu> menus, Long appId){
        menuService.deleteAllByAppId(appId);
        for(Menu menuNode: menus){
            menuService.saveMenu(menuNode);
        }
        return "ok";
    }
}

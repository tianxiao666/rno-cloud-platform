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

    public MenuResource(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/app-menu")
    public List<Menu> getAppMenu(String appCode) {
        // 根据前端传进来的参数appCode返回相应的系统菜单
        return menuService.listMenuByAppCode(appCode);
    }

    @GetMapping("/get-menu")
    public List<Menu> getMenu(Long appId) {
        // 如果有则返回url前缀的应用的菜单
        return menuService.listMenuByAppId(appId);
    }

    @PostMapping("/submit-menu")
    public String submitMenu(@RequestBody List<Menu> menus, Long appId) {
        menuService.deleteAllByAppId(appId);
        for (Menu menuNode : menus) {
            menuService.saveMenu(menuNode);
        }
        return "ok";
    }
}

package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.Menu;
import com.hgicreate.rno.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class MenuResource {

    @Autowired
    private MenuService menuService;

    @Value("${rno.app-code}")
    private String app_code;

    @GetMapping("/query-menus")
    public List<Menu> searchAll(){
        return menuService.getAllInfo();
    }

    @GetMapping("/query-appId")
    public Long searchAppId(){
        return menuService.getAllByAppCode().get(0).getId();
    }


    @PostMapping("/submit-menu")
    public String submitMenu(@RequestBody List<Menu> Menus){
        menuService.delAll();
        for(Menu menuNode: Menus){
            menuService.saveData(menuNode);
        }
        return "ok";
    }

}

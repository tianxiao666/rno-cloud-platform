package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.Menu;
import com.hgicreate.rno.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class MenuResource {

    @Autowired
    private MenuService menuService;

    @GetMapping("/query-menus")
    public List<Menu> searchAll(){
        return menuService.getAllInfo();
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

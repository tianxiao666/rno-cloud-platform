package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.Menu;
import com.hgicreate.rno.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@Slf4j
@RestController
public class MenuResource {

    @Autowired
    private MenuService menuService;

    @GetMapping("/searchall")
    public List<Menu> searchall(){
        return menuService.getAllInfo();
    }

    @PostMapping("/submit-menu")
    public String submitMenu(@RequestBody List<Menu> Menus){
        menuService.delAll();
        for(Menu menunode: Menus){
            menuService.saveData(menunode);
        }
        return "ok";
    }


}

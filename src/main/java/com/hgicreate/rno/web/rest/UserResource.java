package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.User;
import com.hgicreate.rno.security.SecurityUtils;
import com.hgicreate.rno.service.KeycloakAdminCliService;
import com.hgicreate.rno.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ke_weixu
 */
@Slf4j
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class UserResource {

    private final UserService userService;
    private final KeycloakAdminCliService keycloakAdminCliService;

    @GetMapping("/get-current-user")
    public User getUserById(){
        return userService.getCurrentUser();
    }

    @PostMapping("/update-user")
    public boolean updateUser(User user){
        return userService.saveUser(user);
    }

    @GetMapping("/check-user-info")
    public String checkUserInfo(){
        return userService.checkUser();
    }

    @PostMapping("/reset-password")
    public boolean resetPassword(String newPassword, String oldPassword){
        boolean verifyPassword = SecurityUtils.verifyPassword(oldPassword);
        if (verifyPassword){
            keycloakAdminCliService.resetPassword(newPassword);
            return true;
        }else {
            return false;
        }

    }

    /**
     * 检验当前用户下，被校验的密码是否匹配
     */
    @GetMapping("/verify-password")
    Boolean verifyPassword(String password) {
        return SecurityUtils.verifyPassword(password);
    }

    /**
     * 此 API 仅用于在用户及密码基础认证通过时返回 true
     */
    @GetMapping("/verify-password-stub")
    String verifyPasswordStub() {
        return "true";
    }
}

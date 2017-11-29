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

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.stream.Collectors;

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
    public void resetPassword(String newPassword){
        keycloakAdminCliService.resetPassword(newPassword);
    }

    /**
     * 检验当前用户下，被校验的密码是否匹配。系统需要允许基础认证，如果基础认证关闭，则无法进行密码校验。
     * @param password 被校验的密码
     * @return 密码匹配返回 true，密码不匹配返回 false
     */
    @GetMapping("/verify-password")
    Boolean verifyPassword(HttpServletRequest request, String password) {
        boolean passwordMatching;
        try {
            String stringUrl = request.getRequestURL() + "-stub";
            URLConnection urlConnection = new URL(stringUrl).openConnection();
            urlConnection.setRequestProperty("X-Requested-With", "Curl");

            String userAndPass = SecurityUtils.getCurrentUserLogin() + ":" + password;
            String basicAuth = "Basic " + Base64.getEncoder().encodeToString(userAndPass.getBytes());
            urlConnection.setRequestProperty("Authorization", basicAuth);

            String result = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));

            // 判断密码是否匹配
            passwordMatching = "true".equals(result);

        } catch (Exception e) {
            // 错误不匹配，返回 HTTP 错误 401 - 未经授权，会抛出 IOException
            passwordMatching = false;
        }
        return passwordMatching;
    }

    /**
     * 此 API 仅用于在用户及密码基础认证通过时返回 true
     */
    @GetMapping("/verify-password-stub")
    String verifyPasswordStub() {
        return "true";
    }
}

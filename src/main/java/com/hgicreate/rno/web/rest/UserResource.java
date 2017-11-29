package com.hgicreate.rno.web.rest;

import com.hgicreate.rno.domain.User;
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
    @GetMapping("/get-user-by-id")
    public User getUserById(Long userId){
        return userService.getUserById(userId);
    }

    @PostMapping("/update-user")
    public String updateUser(User user){
        return userService.saveUser(user);
    }
}

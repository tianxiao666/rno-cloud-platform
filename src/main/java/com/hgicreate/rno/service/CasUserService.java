package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.User;
import org.springframework.stereotype.Service;

/**
 * @author wuhb
 */
@Service
public class CasUserService {

    /**
     * 更新CAS服务器中的用户信息
     * @param user 用户
     * @return 是否成功
     */
    public boolean updateAccount(User user) {
        // TODO 更新CAS服务器中的用户信息
        return true;
    }

    public boolean resetPassword(String newPassword, String oldPassword) {
        // TODO 更新CAS服务器中的密码信息
        return true;
    }
}

package com.hgicreate.rno.security;

import com.hgicreate.rno.config.Constants;
import com.hgicreate.rno.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.validation.AssertionImpl;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

/**
 * 安全工具类
 */
@Slf4j
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * 获取当前登录用户
     *
     * @return 当前登录用户
     */
    public static String getCurrentUserLogin() {
        return auth() == null ? Constants.ANONYMOUS_USER : auth().getName();
    }

    public static String getFullName() {
        if (auth() != null) {
            CasAuthenticationToken casToken = (CasAuthenticationToken) auth();
            AssertionImpl assertion = (AssertionImpl) casToken.getAssertion();
            return assertion.getPrincipal().getAttributes()
                    .getOrDefault("full_name", getCurrentUserLogin()).toString();
        } else {
            return Constants.ANONYMOUS_NAME;
        }
    }

    private static Authentication auth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static String getEmail() {
        if (auth() != null) {
            CasAuthenticationToken casToken = (CasAuthenticationToken) auth();
            AssertionImpl assertion = (AssertionImpl) casToken.getAssertion();
            return assertion.getPrincipal().getAttributes()
                    .getOrDefault("email", "").toString();
        } else {
            return "";
        }
    }

    public static List<String> getRoles() {
        List<String> list = new ArrayList<>();
        if (auth() != null) {
            CasAuthenticationToken casToken = (CasAuthenticationToken) auth();
            casToken.getUserDetails().getAuthorities().forEach(r -> {
                list.add(r.getAuthority());
            });
        }
        return list;
    }
}

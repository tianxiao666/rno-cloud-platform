package com.hgicreate.rno.security;

import com.hgicreate.rno.config.Constants;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 安全工具类
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * 获取当前登录用户
     *
     * @return 当前登录用户
     */
    public static String getCurrentUserLogin() {
        return getAccessToken().getPreferredUsername();
    }

    public static AccessToken getAccessToken() {
        if (getKeycloakSecurityContext() == null) {
            AccessToken token = new AccessToken();
            token.setPreferredUsername(Constants.ANONYMOUS_USER);
            token.setName(Constants.ANONYMOUS_NAME);
            return token;
        } else {
            return getKeycloakSecurityContext().getToken();
        }
    }

    public static KeycloakSecurityContext getKeycloakSecurityContext() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) request.getUserPrincipal();
        if (keycloakPrincipal == null) {
            return null;
        } else {
            return keycloakPrincipal.getKeycloakSecurityContext();
        }
    }

    public static String getTokenString() {
        return getKeycloakSecurityContext() != null ? getKeycloakSecurityContext().getTokenString() : "";
    }
}

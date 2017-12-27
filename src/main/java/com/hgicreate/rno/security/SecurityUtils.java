package com.hgicreate.rno.security;

import com.hgicreate.rno.config.Constants;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.stream.Collectors;

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
        return getAccessToken().getPreferredUsername();
    }

    public static AccessToken getAccessToken() {
        if (getKeycloakSecurityContext() == null) {
            AccessToken token = new AccessToken();
            token.setPreferredUsername(Constants.ANONYMOUS_USER);
            token.setGivenName(Constants.ANONYMOUS_NAME);
            return token;
        } else {
            return getKeycloakSecurityContext().getToken();
        }
    }

    public static KeycloakSecurityContext getKeycloakSecurityContext() {
        KeycloakPrincipal keycloakPrincipal = (KeycloakPrincipal) getRequest().getUserPrincipal();
        if (keycloakPrincipal == null) {
            return null;
        } else {
            return keycloakPrincipal.getKeycloakSecurityContext();
        }
    }

    public static String getTokenString() {
        return getKeycloakSecurityContext() != null ? getKeycloakSecurityContext().getTokenString() : "";
    }

    /**
     * 被校验的密码是否匹配当前用户的密码。系统需要允许基础认证，如果基础认证关闭，则无法进行密码校验。
     * @param password 被校验的密码
     * @return 密码匹配返回 true，密码不匹配返回 false
     */
    public static boolean verifyPassword(String password) {
        boolean passwordMatching;
        try {
            String stringUrl = "http://" + getRequest().getServerName() +
                    ":" + getRequest().getServerPort() + "/api/verify-password-stub";

            URLConnection urlConnection = new URL(stringUrl).openConnection();
            urlConnection.setRequestProperty("X-Requested-With", "Curl");

            String userAndPass = getCurrentUserLogin() + ":" + password;
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

    private static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }
}

package springboot.nuxt.google.auth.backend.security;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public class AuthFilter extends AbstractPreAuthenticatedProcessingFilter {
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return "";
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        final var token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (Objects.isNull(token) || !token.startsWith(TOKEN_PREFIX)) {
            // 先頭が「Bearer 」で開始されていない場合
            return "";
        }

        // 「Bearer 」以降の文字列を返却
        return token.substring(TOKEN_PREFIX.length());
    }

}

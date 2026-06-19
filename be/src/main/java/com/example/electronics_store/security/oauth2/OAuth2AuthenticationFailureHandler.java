package com.example.electronics_store.security.oauth2;

import com.example.electronics_store.util.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) 
        throws IOException, ServletException {
        exception.printStackTrace();
        String targetUrl = CookieUtils.getCookie(request, HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
            .map(Cookie::getValue)
            .orElse(("/"));
         String errorMessage = "Đăng nhập bằng mạng xã hội thất bại";
        if (exception.getMessage().contains("email")) {
                errorMessage = "Email không được cung cấp từ nhà cung cấp dịch vụ";
        }
        String encodedError = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString());
        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", encodedError)
                .build(false)
                .toUriString();
                httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
                getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}

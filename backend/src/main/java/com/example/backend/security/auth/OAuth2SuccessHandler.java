package com.example.backend.security.auth;

import com.example.backend.model.User;
import com.example.backend.security.jwt.JwtUtils;
import com.example.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * packageName : com.example.backend.security.auth
 * fileName : OAuth2SuccessHandler
 * author : hyuk
 * date : 2023/01/26
 * description :
 * ===========================================================
 * DATE            AUTHOR             NOTE
 * —————————————————————————————
 * 2023/01/26         hyuk          최초 생성
 */
@Slf4j
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();

        String targetUrl;

        String jwt = "";
        String username = "";
        String email = "";

        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;

        switch (oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()) {
            case "google":
                username = ((String) oAuth2User.getAttributes().get("email")).split("@")[0];
                email = (String) oAuth2User.getAttributes().get("email");
                break;

            case "naver":
                Map<String, Object> naver = (Map<String, Object>) oAuth2User.getAttributes().get("naver_account");
                username = ((String) naver.get("email")).split("@")[0];
                email = (String) naver.get("email");
                break;

            case "kakao":
                Map<String, Object> kakao = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
                username = ((String) kakao.get("email")).split("@")[0];
                email = (String) kakao.get("email");
                break;
        }

        jwt = jwtUtils.generateJwtToken(email);

//        FIXME: 지금 이거 UserRepository에 Pageable 되어있어서 에러 뜨는거임 -> 고칠 예정
        Optional<User> optionalUser = userService.findByEmail(email);

        targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/auth-redirect")
                .queryParam("accessToken", jwt)
                .queryParam("id", optionalUser.get().getId())
                .queryParam("username", username)
                .queryParam("email", email)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}

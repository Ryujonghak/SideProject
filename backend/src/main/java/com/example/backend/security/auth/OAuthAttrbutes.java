package com.example.backend.security.auth;

import com.example.backend.model.ERole;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import lombok.Builder;
import lombok.Getter;
import net.minidev.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * packageName : com.example.backend.security.auth
 * fileName : OAuthAttrbutes
 * author : hyuk
 * date : 2023/01/26
 * description :
 * ===========================================================
 * DATE            AUTHOR             NOTE
 * —————————————————————————————
 * 2023/01/26         hyuk          최초 생성
 */
@Getter
public class OAuthAttrbutes {

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String username;
    private String email;
    private String name;

    @Builder
    public OAuthAttibutes(Map<String, Object> attributes, String nameAttributeKey, String username,
                          String email, String name) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.username = username;
        this.email = email;
        this.name = name;
    }

    public static OAuthAttrbutes of(String registrationId,
                                    String userNameAttributeName,
                                    Map<String, Object> attributes) {
        switch (registrationId) {
            case "google":
                return ofGoogle(userNameAttributeName, attributes);
            case "naver":
                return ofNaver(userNameAttributeName, attributes);
            case "kakao":
                return ofKakao(userNameAttributeName, attributes);
            default:
                return ofGoogle(userNameAttributeName, attributes);
        }
    }

//    TODO: 구글 로그인
    private static OAuthAttrbutes ofGoogle(String userNameAttributeName,
                                           Map<String, Object> attributes) {
        return OAuthAttrbutes.builder()
                .username(((String) attributes.get("email")).split("@")[0])
                .email((String) attributes.get("email"))
                .name((String) attributes.get("family_name") + (String) attributes.get("given_name"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

//    TODO: 네이버 로그인
    private static OAuthAttrbutes ofNaver(String userNameAttributeName,
                                          Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttrbutes.builder()
                .username(((String) response.get("email")).split("@")[0])
                .email((String) response.get("email"))
                .name((String) response.get("name"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();

    }

//    TODO: 카카오 로그인
    private static OAuthAttrbutes ofKakao(String userNameAttributeName,
                                          Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        JSONObject jsonObject = new JSONObject((Map<String, ?>) kakaoAccount.get("profile"));
        String nickname = (String) jsonObject.get("nickname");

        return OAuthAttrbutes.builder()
                .username(((String) kakaoAccount.get("email")).split("@")[0])
                .email((String) kakaoAccount.get("email"))
                .name(nickname)
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public User toEntity() {
        Set<Role> roles = new HashSet<>();
        Role userRole = new Role();
        userRole.setRid(1);
        userRole.setRoleName(ERole.ROLE_USER);

        roles.add(userRole);

        return
    }
}

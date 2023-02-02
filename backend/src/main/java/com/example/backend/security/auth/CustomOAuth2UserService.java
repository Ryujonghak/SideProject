package com.example.backend.security.auth;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * packageName : com.example.backend.security.auth
 * fileName : CustomOAuth2UserService
 * author : hyuk
 * date : 2023/01/26
 * description :
 * ===========================================================
 * DATE            AUTHOR             NOTE
 * —————————————————————————————
 * 2023/01/26         hyuk          최초 생성
 */
@Slf4j
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest, Pageable pageable)
            throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(oAuthAttributes, pageable);


        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"),
                        oAuthAttributes.getAttributes(),
                        oAuthAttributes.getNameAttributeKey());
//                FIXME: 일단 여기까지 작성했는데 어디가문제인지는 좀 더 찾아봐야할듯
        )
    }

    private User saveOrUpdate(OAuthAttributes attributes, Pageable pageable) {
        User user = userRepository.findByEmail(attributes.getEmail(), pageable)
                .map(entity -> entity.update(attributes.getUsername()))
                .orElse(attributes.toEntity());
        return userRepository.save(user);
    }
}

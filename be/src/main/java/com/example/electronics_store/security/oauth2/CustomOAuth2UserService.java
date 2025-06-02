package com.example.electronics_store.security.oauth2;

import com.example.electronics_store.model.User;
import com.example.electronics_store.repository.UserRepository;
import com.example.electronics_store.security.oauth2.user.OAuth2UserInfo;
import com.example.electronics_store.security.oauth2.user.OAuth2UserInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());
        
        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        
        if (userOptional.isPresent()) {
            user = userOptional.get();
            
            // Update existing user with OAuth2 info
            user = updateExistingUser(user, oAuth2UserInfo, registrationId);
        } else {
            // Create new user
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo, registrationId);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo, String provider) {
        User user = new User();

        user.setUserName(oAuth2UserInfo.getEmail().split("@")[0] + "_" + provider);
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setSurName(oAuth2UserInfo.getFirstName() != null ? oAuth2UserInfo.getFirstName() : "");
        user.setLastName(oAuth2UserInfo.getLastName() != null ? oAuth2UserInfo.getLastName() : "");
        user.setPicture(oAuth2UserInfo.getImageUrl());
        user.setRole(false); // Regular user
        user.setActive(1); // Active by default for OAuth2 users
        user.setLoginTimes(1);
        // Update login provider code
        user.setLoginBy(provider.equals("google") ? 1 :
                (provider.equals("facebook") ? 2 :
                        (provider.equals("discord") ? 3 : 0)));
        user.setLockFail(0);
        user.setPassword(UUID.randomUUID().toString()); // Random password as it's not used
        user.setHash(UUID.randomUUID().toString());

        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo, String provider) {
        // Update user information if needed
        if (existingUser.getSurName() == null || existingUser.getSurName().isEmpty() || oAuth2UserInfo.getFirstName() != null && !oAuth2UserInfo.getFirstName().isEmpty()) {
            existingUser.setSurName(oAuth2UserInfo.getFirstName());
        }

        if (existingUser.getLastName() == null || existingUser.getLastName().isEmpty() || oAuth2UserInfo.getLastName() != null && !oAuth2UserInfo.getLastName().isEmpty()) {
            existingUser.setLastName(oAuth2UserInfo.getLastName());
        }

        // Kiểm tra xem ảnh hiện tại có phải từ OAuth2 không
        boolean isCurrentImageFromOAuth = existingUser.getPicture() != null && (existingUser.getPicture().contains("googleusercontent.com") || existingUser.getPicture().contains("cdn.discordapp.com"));
    
        // Nếu chưa có ảnh hoặc ảnh hiện tại là từ OAuth2, thì mới cập nhật
        if (existingUser.getPicture() == null || existingUser.getPicture().isEmpty() || isCurrentImageFromOAuth) {
        if (oAuth2UserInfo.getImageUrl() != null && !oAuth2UserInfo.getImageUrl().isEmpty()) {
            existingUser.setPicture(oAuth2UserInfo.getImageUrl());
        }
        }

        // Increment login times
        existingUser.setLoginTimes(existingUser.getLoginTimes() + 1);

        // Set login by if not already set
        if (existingUser.getLoginBy() == 0) {
            if (provider.equals("google")) {
                existingUser.setLoginBy(1);
            } else if (provider.equals("facebook")) {
                existingUser.setLoginBy(2);
            } else if (provider.equals("discord")) {
                existingUser.setLoginBy(3);
            } else {
                existingUser.setLoginBy(0); // Default value for unknown providers
            }
        }

        return userRepository.save(existingUser);
    }
}

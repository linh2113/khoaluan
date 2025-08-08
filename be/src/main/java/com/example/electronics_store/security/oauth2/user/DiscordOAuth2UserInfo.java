package com.example.electronics_store.security.oauth2.user;

import java.util.Map;

public class DiscordOAuth2UserInfo extends OAuth2UserInfo {

    public DiscordOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getName() {
        return (String) attributes.get("username");
    }
    
    @Override
    public String getFirstName() {
        // Discord doesn't provide first name separately
        return (String) attributes.get("username");
    }
    
    @Override
    public String getLastName() {
        // Discord doesn't provide last name
        return "";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        String id = attributes.get("id").toString();
        String avatar = (String) attributes.get("avatar");
        
        if (avatar != null) {
            return "https://cdn.discordapp.com/avatars/" + id + "/" + avatar + ".png";
        }
        
        return null;
    }
}
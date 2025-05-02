package com.myong.backend.oauth2.exception;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

public class CustomOAuth2AuthenticationException extends OAuth2AuthenticationException {

    private final String email;
    private final String name;

    public CustomOAuth2AuthenticationException(String email, String name) {
        super("NEW_USER");
        this.email = email;
        this.name = name;
    }

    public String getEmail(){
        return email;
    }

    public String getName(){
        return name;
    }
}

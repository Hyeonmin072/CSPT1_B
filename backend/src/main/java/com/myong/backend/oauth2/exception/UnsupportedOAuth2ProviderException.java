package com.myong.backend.oauth2.exception;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

public class UnsupportedOAuth2ProviderException extends OAuth2AuthenticationException {

  public UnsupportedOAuth2ProviderException(String provider) {
    super("지원하지 않는 소셜 로그인입니다: " + provider);
  }
}

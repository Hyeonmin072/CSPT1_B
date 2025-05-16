package com.myong.backend.jwttoken.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserDetailsDto implements UserDetails, OAuth2User {
    private String name;
    private String username; //이메일
    private String password;
    private List<GrantedAuthority> authorities;
    private Map<String, Object> attributes; // OAuth2 정보 저장

    public UserDetailsDto (String username, String role, String name, String password){
        this.name = name;
        this.username = username;
        this.password = password;
        this.authorities = new ArrayList<>();
        this.authorities.add(new SimpleGrantedAuthority("ROLE_"+role));
    }

    public UserDetailsDto (String username, String role, String name){
        this.name = name;
        this.username = username;
        this.authorities = new ArrayList<>();
        this.authorities.add(new SimpleGrantedAuthority("ROLE_"+role));
    }

    // OAuth2 로그인용 생성자
    public UserDetailsDto(String username, String role, String name, Map<String, Object> attributes) {
        this.name = name;
        this.username = username;
        this.authorities = new ArrayList<>();
        this.authorities.add(new SimpleGrantedAuthority("ROLE_"+role));
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // OAuth2User 관련 메서드 추가
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    @Override
    public String getName() {
        return username;
    }
}

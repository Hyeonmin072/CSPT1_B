package com.myong.backend.service;


import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.jwttoken.dto.UserDetailsDto;
import com.myong.backend.repository.DesignerRepository;
import com.myong.backend.repository.ShopRepository;
import com.myong.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final DesignerRepository designerRepository;
    private final ShopRepository shopRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try{
            String user = username.split(",")[0];
            String who = username.split(",")[1];
            switch (who){
                case "USER" -> {
                    return userRepository.findByEmail(user)
                            .map(this::createUserDetails)
                            .orElseThrow(() -> new UsernameNotFoundException("해당 유저가 존재하지 않습니다"));
                }
                case "DESIGNER" -> {
                    return designerRepository.findByEmail(user)
                            .map(this::createDesignerDetails)
                            .orElseThrow(() -> new UsernameNotFoundException("해당 디자이너가 존재하지 않습니다"));
                }
                case "SHOP" -> {
                    return shopRepository.findByEmail(user)
                            .map(this::createShopDetails)
                            .orElseThrow(() -> new UsernameNotFoundException("해당 가게가 존재하지 않습니다"));
                }
                default -> throw new UsernameNotFoundException("잘못된 유형의 사용자입니다.");
            }
        } catch (Exception e){
            throw new Error(e);
        }


    }

    private UserDetailsDto createUserDetails(User user) {
        return new UserDetailsDto(
                user.getEmail(),
                "USER",
                user.getName(),
                user.getPwd()
        );
    }

    private UserDetailsDto createDesignerDetails(Designer designer){
        return new UserDetailsDto(
                designer.getEmail(),
                designer.getName(),
                "DESIGNER",
                designer.getPassword()
        );
    }

    private UserDetailsDto createShopDetails(Shop shop){
        return new UserDetailsDto(
                shop.getEmail(),
                shop.getName(),
                "SHOP",
                shop.getPwd()

        );
    }
}

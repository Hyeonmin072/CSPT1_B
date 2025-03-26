package com.myong.backend.service;

import com.myong.backend.domain.dto.designer.*;
import com.myong.backend.domain.dto.designer.SignUpRequestDto;
import com.myong.backend.domain.dto.designer.UpdateProfileRequestDto;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.designer.Resume;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.usershop.Review;
import com.myong.backend.repository.DesignerRepository;
import com.myong.backend.repository.ShopRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DesignerService {
    private final DesignerRepository designerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ShopRepository shopRepository;
    private final ResumeService resumeService;

    public void signUp(SignUpRequestDto request) {
        // 이메일 중복 체크
        if (checkEmailDuplication(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 닉네임 중복 체크
        if (checkNicknameDuplication(request.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        LocalDate birthday;

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            birthday = LocalDate.parse(request.getBirth(), formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("invalid birth date format : yyyy-MM-dd으로 형태를 맞춰주세요");
        }


        Designer designer = Designer.builder()
                        .id(UUID.randomUUID())
                        .name(request.getName())
                        .nickName(request.getNickname())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .tel(request.getTel())
                        .birth(birthday)
                        .gender(request.getGender())
                        .rating(0.0)
                        .like(0)
                        .totalRating(0.0)
                        .reviewCount(0)
                        .build();

        designerRepository.save(designer);

        //이력서 생성
        Resume resume = resumeService.createResume(designer.getEmail());
        log.info("resume : {}", resume);
    }

    //프로필 가져오기
    public ProfileResponseDto getProfile(String email) {
        Designer designer = designerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("디자이너를 찾을 수 없습니다"));


        Shop shop = designer.getShop();
        String shopName = (shop != null && shop.getName() != null) ? shop.getName() : "소속없음";

        List<Review> reviews = designer.getReviews();

        int currentYear = java.time.LocalDate.now().getYear();
        int birth = Integer.parseInt(designer.getBirth().toString().substring(0, 4));
        int age = currentYear - birth;

        return ProfileResponseDto.builder()
                .name(designer.getName())
                .nickName(designer.getNickName())
                .email(designer.getEmail())
                .tel(designer.getTel())
                .age(age)
                .gender(designer.getGender())
                .reviews(reviews)
                .shopName(shopName)
                .like(designer.getLike())
                .description(designer.getDesc())
                .build();
    }


    @Transactional
    public Designer updateProfile(String email, UpdateProfileRequestDto updateProfileRequest) {
        Designer designer = designerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("디자이너를 찾을 수 없습니다"));

        // 닉네임 변경 시 중복 체크
        if (updateProfileRequest.getUpdateNickName() != null && !updateProfileRequest.getUpdateNickName().equals(email)) {
            if (checkNicknameDuplication(updateProfileRequest.getUpdateNickName())) {
                throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
            }
            designer.updateNickName(updateProfileRequest.getUpdateNickName());
            log.info("updateNickName : {}", updateProfileRequest.getUpdateNickName());
        }

        //디자이너 소개 수정
        designer.updateDesc(updateProfileRequest.getUpdateDesc());
        log.info("updateDesc : {}", updateProfileRequest.getUpdateDesc());

        //비밀번호 변경
        if (updateProfileRequest.getNewPwd() != null) {
            if (updateProfileRequest.getOldPwd() == null || !designer.getPassword().equals(updateProfileRequest.getOldPwd())) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
            if (updateProfileRequest.getOldPwd().equals(updateProfileRequest.getNewPwd())) {
                throw new IllegalArgumentException("이전 비밀번호와 동일합니다. 다른 비밀번호를 사용해주세요.");
            }
            if (!updateProfileRequest.getNewPwd().equals(updateProfileRequest.getCheckPwd())) {
                throw new IllegalArgumentException("수정된 비밀번호가 일치하지 않습니다.");
            }
            designer.updatePwd(updateProfileRequest.getNewPwd());
            log.info("updatePwd : {}", updateProfileRequest.getNewPwd());
        }

        //전화번호 변경
        if (updateProfileRequest.getUpdateTel() != null) {
            designer.updateTel(updateProfileRequest.getUpdateTel());
            log.info("updateTel : {}", updateProfileRequest.getUpdateTel());
        }


        //이미지 변경
        if (updateProfileRequest.getUpdateImage() != null) {
            designer.updateImage(updateProfileRequest.getUpdateImage());
            log.info("updateImage : {}", updateProfileRequest.getUpdateImage());
        }

        // 명시적으로 save 호출하지 않아도 됨 (@Transactional 때문)
        return designerRepository.save(designer);
    }

    //이메일 중복검사 매서드
    public Boolean checkEmailDuplication(String email) {
        return designerRepository.existsByEmail(email);
    }

    //닉네임 중복검사 매서드
    public Boolean checkNicknameDuplication(String nickName) {
        return designerRepository.existsByNickName(nickName);
    }

    //이력서 불러오기
    public Resume getResume(String email) {
        return resumeService.getResume(email);
    }
}
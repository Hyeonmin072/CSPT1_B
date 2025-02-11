package com.myong.backend.service;


import com.myong.backend.domain.dto.designer.SignUpRequest;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.repository.DesignerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@Service
@Transactional
public class DesignerService {

    private final DesignerRepository designerRepository;

    @Autowired
    public DesignerService(DesignerRepository designerRepository) {
        this.designerRepository = designerRepository;
    }
    public void signUp(SignUpRequest request) {

        LocalDate birthday;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            birthday = LocalDate.parse(request.getBirth(), formatter);
        }catch (DateTimeParseException e){
            throw new IllegalArgumentException("invalid birth date format : yyyyMMdd으로 형태를 맞춰주세요");
        }

        Designer designer = Designer.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .nickName(request.getNickname())
                .pwd(request.getPwd())
                .email(request.getEmail())
                .tel(request.getTel())
                .gender(request.getGender())
                .birth(birthday)
                .rating(0.0)
                .like(0L)
                .build();

        designerRepository.save(designer);
    }

    //이메일 중복검사
    public Boolean checkEmailDuplication(String email) {
        return designerRepository.existsByEmail(email);
    }


    //닉네임 중복검사
    public Boolean checkNicknameDuplication(String nickName) {
        return designerRepository.existsByNickName(nickName);
    }
}


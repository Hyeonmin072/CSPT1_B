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

        //날짜 form을 yyyyMMdd의 형식으로 변환
        LocalDate birthday;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            birthday = LocalDate.parse(request.getBirth(), formatter);
        }catch (DateTimeParseException e){
            throw new IllegalArgumentException("invalid birth date format : yyyyMMdd으로 형태를 맞춰주세요");
        }

        Designer designer = new Designer(
                request.getName(),
                request.getNickname(),
                request.getEmail(),
                request.getPwd(),
                request.getTel(),
                birthday,
                request.getGender()
                );


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


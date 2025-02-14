package com.myong.backend.service;

import com.myong.backend.domain.dto.designer.SignUpRequest;
import com.myong.backend.domain.dto.designer.UpdateProfileRequest;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.repository.DesignerRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
@Service
@Transactional
public class DesignerService {
    private final DesignerRepository designerRepository;

    @Autowired
    public DesignerService(DesignerRepository designerRepository) {
        this.designerRepository = designerRepository;
    }

    public void signUp(SignUpRequest request) {
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            birthday = LocalDate.parse(request.getBirth(), formatter);
        } catch (DateTimeParseException e) {
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

    public Designer getProfile(String email) {
        return designerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("디자이너를 찾을 수 없습니다"));
    }

    @Transactional
    public Designer updateProfile(String email, UpdateProfileRequest updateProfileRequest) {
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


        designer.updateDesc(updateProfileRequest.getUpdateDesc());
        log.info("updateDesc : {}", updateProfileRequest.getUpdateDesc());

        if (updateProfileRequest.getNewPwd() != null) {
            if (updateProfileRequest.getOldPwd() == null || !designer.getPwd().equals(updateProfileRequest.getOldPwd())) {
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

        if (updateProfileRequest.getUpdateTel() != null) {
            designer.updateTel(updateProfileRequest.getUpdateTel());
            log.info("updateTel : {}", updateProfileRequest.getUpdateTel());
        }

        if (updateProfileRequest.getUpdateImage() != null) {
            designer.updateImage(updateProfileRequest.getUpdateImage());
            log.info("updateImage : {}", updateProfileRequest.getUpdateImage());
        }

        // 명시적으로 save 호출하지 않아도 됨 (@Transactional 때문)
        return designerRepository.save(designer);
    }

    public Boolean checkEmailDuplication(String email) {
        return designerRepository.existsByEmail(email);
    }

    public Boolean checkNicknameDuplication(String nickName) {
        return designerRepository.existsByNickName(nickName);
    }
}
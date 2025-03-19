package com.myong.backend.service;

import com.myong.backend.domain.dto.designer.ResumeRequestDto;
import com.myong.backend.domain.dto.designer.SignUpRequestDto;
import com.myong.backend.domain.dto.designer.UpdateProfileRequestDto;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.designer.Resume;
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
    private final ResumeService resumeService;

    @Autowired
    public DesignerService(DesignerRepository designerRepository, ResumeService resumeService) {
        this.designerRepository = designerRepository;
        this.resumeService = resumeService;
    }


    //회원가입
    public void signUp(SignUpRequestDto signUpRequest, ResumeRequestDto resumeRequest) {
        // 이메일 중복 체크
        if (checkEmailDuplication(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 닉네임 중복 체크
        if (checkNicknameDuplication(signUpRequest.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }

        LocalDate birthday;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            birthday = LocalDate.parse(signUpRequest.getBirth(), formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("invalid birth date format : yyyyMMdd으로 형태를 맞춰주세요");
        }

        Designer designer = new Designer(
                signUpRequest.getName(),
                signUpRequest.getNickname(),
                signUpRequest.getEmail(),
                signUpRequest.getPassword(),
                signUpRequest.getTel(),
                birthday,
                signUpRequest.getGender()
        );
        designerRepository.save(designer);

        //이력서 생성
        Resume resume = resumeService.createResume(designer.getEmail());
        log.info("Resume created {}:" , resume);
    }

    //프로필 가져오기
    public Designer getProfile(String email) {
        return designerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("디자이너를 찾을 수 없습니다"));
    }


    //프로필 수정하기
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


        //소개 변경
        designer.updateDesc(updateProfileRequest.getUpdateDesc());
        log.info("updateDesc : {}", updateProfileRequest.getUpdateDesc());

        //비밀번호 변경
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

    //이메일 중복 체크 매서드
    public Boolean checkEmailDuplication(String email) {
        return designerRepository.existsByEmail(email);
    }

    //닉네임 중복체크 매서드
    public Boolean checkNicknameDuplication(String nickName) {
        return designerRepository.existsByNickName(nickName);
    }

    //이력서 불러오기
    public Resume getResume(String email) {
        return resumeService.getResume(email);
    }
}
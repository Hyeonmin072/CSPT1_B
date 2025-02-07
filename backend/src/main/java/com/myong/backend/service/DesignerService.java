package com.myong.backend.service;


import com.myong.backend.domain.dto.designer.SignUpRequest;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.repository.DesignerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        Designer designer = Designer.builder()
                .id(UUID.randomUUID())
                .name(request.getName())
                .nickName(request.getNickname())
                .pwd(request.getPwd())
                .email(request.getEmail())
                .tel(request.getTel())
                .gender(request.getGender())
                .birth(LocalDate.parse(request.getBirth()))
                .rating(0.0)
                .like(0L)
                .build();

        designerRepository.save(designer);
    }
}


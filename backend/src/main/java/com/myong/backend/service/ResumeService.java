package com.myong.backend.service;

import com.myong.backend.domain.dto.designer.*;
import com.myong.backend.domain.entity.designer.*;
import com.myong.backend.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final DesignerRepository designerRepository;
    private final CareerRepository careerRepository;
    private final CertificationRepository certificationRepository;
    private final DesignerWantedDayRepository designerWantedDayRepository;




    public ResumeService(ResumeRepository resumeRepository, DesignerRepository designerRepository, CareerRepository careerRepository, CertificationRepository certificationRepository, DesignerWantedDayRepository designerWantedDayRepository) {
        this.resumeRepository = resumeRepository;
        this.designerRepository = designerRepository;
        this.careerRepository = careerRepository;
        this.certificationRepository = certificationRepository;
        this.designerWantedDayRepository = designerWantedDayRepository;
    }




    //이력서 생성
    public Resume createResume(String email) {
        Designer designer = FindDesignerByEmail(email);

        Resume resume = new Resume();
        resume.connectDesigner(designer);
        resumeRepository.save(resume);

        return resume;
    }

    @Transactional
    //이력서 수정
    public Resume updateResume(String email, ResumeRequestDto resumeDto) {
        Designer designer = FindDesignerByEmail(email);
        Resume resume = FindResumeByEmail(email);

        boolean isUpdate = false;
        //소개 변경
        if(!Objects.equals(resumeDto.getContent(), resume.getContent())) {
            resume.updateContent(resumeDto.getContent());
            isUpdate = true;
        }

        //경력 변경
        if(!Objects.equals(resumeDto.getExp(), resume.getExp())) {
            resume.updateExp(resumeDto.getExp());
            isUpdate = true;
        }

        //이미지 변경
        if(!Objects.equals(resumeDto.getImage(), resume.getImage())) {
            resume.updateImage(resumeDto.getImage());
            isUpdate = true;
        }

        //포토폴리오 변경
        if(!Objects.equals(resumeDto.getPortfolio(), resume.getPortfolio())) {
            resume.updatePortfolio(resumeDto.getPortfolio());
            isUpdate = true;
        }

        //희망근무요일 변경
        if(!Objects.equals(resumeDto.getWantedDays(), resume.getWantedDays())) {
            updateWantedDay(resume,resumeDto);
            isUpdate = true;
        }

        resume.connectDesigner(designer);

        //경력 추가
        if(!Objects.equals(resumeDto.getCareers(), resume.getCareers())) {
            updateCareer(resume,resumeDto);
            isUpdate = true;
        }


        //자격증 추가
        if(!Objects.equals(resumeDto.getCertificates(), resume.getCertifications())){
            updateCertificates(resume,resumeDto);
            isUpdate = true;
        }

        if(isUpdate == true){
            resumeRepository.save(resume);
        }
        return resume;
    }

    //이력서 불러오기
    public ResumeResponseDto getResume(String email) {
        Resume resume = resumeRepository.findByDesignerEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("이력서를 찾을 수 없습니다."));

        Designer designer = designerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("디자이너를 찾을 수 없습니다."));

        int currentYear = java.time.LocalDate.now().getYear();
        int birth = Integer.parseInt(designer.getBirth().toString().substring(0,4));
        int age = currentYear - birth;

        return ResumeResponseDto.builder()
                .name(designer.getName())
                .email(designer.getEmail())
                .tel(designer.getTel())
                .gender(designer.getGender())
                .age(age)
                .exp(resume.getExp())
                .image(resume.getImage())
                .content(resume.getContent())
                .careers(resume.getCareers())
                .certifications(resume.getCertifications())
                .wantedDays(resume.getWantedDays())
                .build();
    }


    //이메일로 디자이너 찾기 매서드
    private Designer FindDesignerByEmail(String email) {
        return designerRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("Designer not found with email: " + email));
    }

    //이메일로 이력서 찾기 매서드
    private Resume FindResumeByEmail(String email) {
        return resumeRepository.findByDesignerEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("Resume not found with email: " + email));
    }

    @Transactional
    public void updateWantedDay(Resume resume, ResumeRequestDto resumeDto) {
        if (resumeDto.getWantedDays() != null) {

            designerWantedDayRepository.deleteAll(designerWantedDayRepository.findByResume(resume));

            for (DesignerWantedDayRequestDto request : resumeDto.getWantedDays()) {
                DesignerWantedDay designerWantedDay = new DesignerWantedDay();
                designerWantedDay.updateWantedDay(DayFormatting(request.getWantedDay()));
                designerWantedDay.updateResume(resume);
                designerWantedDayRepository.save(designerWantedDay);
            }
        }
    }

    //경력 추가 매서드
    @Transactional
    public void updateCareer(Resume resume, ResumeRequestDto resumeDto) {
        if (resume.getExp().equals(Exp.EXP) && resumeDto.getCareers() != null) {
            log.info("resumeDto.getCareers() : {}", resumeDto.getCareers());
            careerRepository.deleteAll(careerRepository.findByResume(resume));
            log.info("삭제성공");

            for(CareerRequestDto request : resumeDto.getCareers()){
                Career career = new Career();
                career.updateShopName(request.getShopName());
                career.updateJoinDate(dateFormatting(request.getJoinDate()));
                career.updateOutDate(dateFormatting(request.getOutDate()));
                career.updatePosition(request.getPosition());
                career.updateResume(resume);
                careerRepository.save(career);
            }
            log.info("추가성공");
        }
    }


    //자격증 추가
    public void updateCertificates(Resume resume, ResumeRequestDto resumeDto) {
        if(resumeDto.getCertificates() != null){

            certificationRepository.deleteAll(certificationRepository.findByResume(resume));

            for(CertificationRequestDto request : resumeDto.getCertificates()){
                Certification certification = new Certification();
                certification.updateName(request.getName());
                certification.updateResume(resume);
                certificationRepository.save(certification);
            }
        }
    }

    //디자이너 근무희망요일 DayOfWeek 형으로 형변화하는 매서드
    private DayOfWeek DayFormatting(String day) {
        switch (day) {
            case "MON":
                return DayOfWeek.MONDAY;
            case "TUE":
                return DayOfWeek.TUESDAY;
            case "WED":
                return DayOfWeek.WEDNESDAY;
            case "THU":
                return DayOfWeek.THURSDAY;
            case "FRI":
                return DayOfWeek.FRIDAY;
            case "SAT":
                return DayOfWeek.SATURDAY;
            case "SUN":
                return DayOfWeek.SUNDAY;
            default:
                throw new IllegalArgumentException("유효하지 않은 요일: " + day);
        }
    }

    private LocalDate dateFormatting(String day) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(day, formatter);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid day format: yyyy-MM-dd로 형태를 맞춰주세요." );
        }
    }
}

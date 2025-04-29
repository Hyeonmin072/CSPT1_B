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

    //희망근무날짜추가 매서드
    private void updateWantedDay(Resume resume, ResumeRequestDto resumeDto) {
        if (resumeDto.getWantedDays() != null) {
            Map<DayOfWeek, DesignerWantedDay> existingWantedDays = designerWantedDayRepository.findByResume(resume)
                    .stream()
                    .collect(Collectors.toMap(DesignerWantedDay::getWantedDay, wantedDate -> wantedDate));//형 변환

            log.info("existingWantedDays : {}", existingWantedDays);

            for (DesignerWantedDayRequestDto wantedDayDto : resumeDto.getWantedDays()) {
                DayOfWeek wantedDay = DayFormatting(wantedDayDto.getWantedDay());

                //입력값이 존재하는 날에 있는 경우 상태코드를 false로 변환
                if (existingWantedDays.containsKey(wantedDay)) {
                    DesignerWantedDay existWantedDay = existingWantedDays.get(wantedDay);
                    log.info("existWantedDay : {}", existWantedDay);
                    designerWantedDayRepository.delete(existWantedDay);
                }
                //없는 경우 새로운 데이터 생성
                else{
                    DesignerWantedDay designerWantedDay = new DesignerWantedDay();
                    designerWantedDay.updateWantedDay(wantedDay);
                    designerWantedDay.updateResume(resume);
                    designerWantedDayRepository.save(designerWantedDay);
                }
            }
        }
    }

    //경력 추가 매서드
    private void updateCareer(Resume resume, ResumeRequestDto resumeDto) {
        if(resume.getExp().equals(Exp.EXP) && resumeDto.getCareers() != null) {
            log.info("resumeDto.getCareers() : {}", resumeDto.getCareers());
            //중복방지용 존재하는 경력 집합만들기
            Set<String> existingCareers = careerRepository.findByResume(resume)
                    .stream()
                    .map(career -> career.getName() + "_" + career.getJoinDate())
                    .collect(Collectors.toSet());

            log.info("existingCareers : {}", existingCareers);

            for (CareerRequestDto careerDto : resumeDto.getCareers()) {

                LocalDate joinDate = dateFormatting(careerDto.getJoinDate());
                LocalDate outDate = Optional.ofNullable(dateFormatting(careerDto.getOutDate())).orElse(null);

                log.info("joinDate : {}", joinDate);
                log.info("outDate : {}", outDate);

                String crKey = careerDto.getShopName() + "_" + joinDate;
                log.info("crKey : {}", crKey);



                if(!existingCareers.contains(crKey)) {
                    Career career = new Career();

                    career.updateShopName(careerDto.getShopName());
                    career.updateJoinDate(joinDate);
                    career.updateOutDate(outDate);
                    career.updatePosition(careerDto.getPosition());
                    career.updateResume(resume);

                    careerRepository.save(career);
                    existingCareers.add(crKey);

                }else{
                    if(existingCareers.contains(crKey)) {
                        Optional<Career>existingCareer = careerRepository.findByResumeAndNameAndJoinDate(
                                resume, careerDto.getShopName(), joinDate);

                        if(existingCareer.isPresent()) {
                            Career career = existingCareer.get();
                            career.updateOutDate(outDate);
                            career.updatePosition(careerDto.getPosition());
                            careerRepository.save(career);
                        }
                    }

                }
            }
        }
    }


    //자격증 추가
    private void updateCertificates(Resume resume, ResumeRequestDto resumeDto) {
        if(resumeDto.getCertificates() != null){
            Set<String> existingCertificates = certificationRepository.findByResume(resume)//이력서로 경력 찾기
                    .stream()
                    .map(Certification :: getName)
                    .collect(Collectors.toSet());

            for(CertificationRequestDto certificationRequest : resumeDto.getCertificates()){
                Certification certification = new Certification();
                if(!existingCertificates.contains(certificationRequest.getName())) {
                    certification.updateName(certificationRequest.getName());
                    certification.updateResume(resume);
                    certificationRepository.save(certification);
                }
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            return LocalDate.parse(day, formatter);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid day format: yyyyMMdd로 형태를 맞춰주세요." );
        }
    }
}

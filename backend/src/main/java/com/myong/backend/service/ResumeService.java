package com.myong.backend.service;

import com.myong.backend.domain.dto.designer.CareerRequestDto;
import com.myong.backend.domain.dto.designer.CertificationRequestDto;
import com.myong.backend.domain.dto.designer.DesignerWantedDayRequestDto;
import com.myong.backend.domain.dto.designer.ResumeRequestDto;
import com.myong.backend.domain.entity.designer.*;
import com.myong.backend.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


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

    //이력서 수정
    public Resume updateResume(String email, ResumeRequestDto resumeDto) {
        Designer designer = FindDesignerByEmail(email);
        Resume resume = FindResumeByEmail(email);

        resume.updateContent(resumeDto.getContent());
        resume.updateExp(resumeDto.getExp());
        resume.updateImage(resumeDto.getImage());
        resume.updatePortfolio(resumeDto.getPortfolio());
        resume.connectDesigner(designer);

        //희망근무요일 추가
        updateWantedDay(resume,resumeDto);

        //경력 추가
        updateCareer(resume,resumeDto);

        //자격증 추가
        updateCertificates(resume,resumeDto);

        resumeRepository.save(resume);
        return resume;
    }

    //이력서 불러오기
    public Resume getResume(String email) {
        return resumeRepository.findByDesignerEmail(email).orElseThrow(()-> new IllegalArgumentException("이력서를 찾을 수 없습니다."));
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

    //이력서 추가 매서드
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
                String crKey = careerDto.getShopName() + "_" + careerDto.getJoinDate();
                log.info("crKey : {}", crKey);

                LocalDate outDate = Optional.ofNullable(careerDto.getOutDate()).orElse(null);

                if(!existingCareers.contains(crKey)) {
                    Career career = new Career();

                    career.updateShopName(careerDto.getShopName());
                    career.updateJoinDate(careerDto.getJoinDate());
                    career.updateOutDate(outDate);
                    career.updateResume(resume);

                    careerRepository.save(career);
                    existingCareers.add(crKey);

                }else{
                    if(existingCareers.contains(crKey)) {
                        Optional<Career>existingCareer = careerRepository.findByResumeAndNameAndJoinDate(
                                resume, careerDto.getShopName(), careerDto.getJoinDate());

                        if(existingCareer.isPresent()) {
                            Career career = existingCareer.get();
                            career.updateOutDate(outDate);

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
            Set<String> existingCertificates = certificationRepository.findByResume(resume)
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
            case "월":
                return DayOfWeek.MONDAY;
            case "화":
                return DayOfWeek.TUESDAY;
            case "수":
                return DayOfWeek.WEDNESDAY;
            case "목":
                return DayOfWeek.THURSDAY;
            case "금":
                return DayOfWeek.FRIDAY;
            case "토":
                return DayOfWeek.SATURDAY;
            case "일":
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

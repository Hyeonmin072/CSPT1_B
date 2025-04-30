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
            // 기존 희망 근무 날짜 가져오기
            List<DesignerWantedDay> existingWantedDays = designerWantedDayRepository.findByResume(resume);

            // 새로운 희망 근무 날짜 목록 생성
            Set<DayOfWeek> newWantedDays = resumeDto.getWantedDays().stream()
                    .map(dayDto -> DayFormatting(dayDto.getWantedDay()))
                    .collect(Collectors.toSet());

            log.info("existingWantedDays before processing: {}", existingWantedDays);
            log.info("newWantedDays: {}", newWantedDays);

            // 기존 데이터 중 새로운 목록에 없는 항목 삭제
            List<DesignerWantedDay> daysToDelete = existingWantedDays.stream()
                    .filter(existing -> !newWantedDays.contains(existing.getWantedDay()))
                    .collect(Collectors.toList());

            designerWantedDayRepository.deleteAll(daysToDelete);
            log.info("Deleted days: {}", daysToDelete);

            // 새로운 데이터 추가
            for (DayOfWeek wantedDay : newWantedDays) {
                boolean exists = existingWantedDays.stream()
                        .anyMatch(existing -> existing.getWantedDay().equals(wantedDay));

                if (!exists) {
                    DesignerWantedDay designerWantedDay = new DesignerWantedDay();
                    designerWantedDay.updateWantedDay(wantedDay);
                    designerWantedDay.updateResume(resume);
                    designerWantedDayRepository.save(designerWantedDay);
                }
            }
        }
    }

    //경력 추가 매서드
    @Transactional
    public void updateCareer(Resume resume, ResumeRequestDto resumeDto) {
        if (resume.getExp().equals(Exp.EXP) && resumeDto.getCareers() != null) {
            log.info("resumeDto.getCareers() : {}", resumeDto.getCareers());

            // 기존 경력 데이터 가져오기 (name + joinDate 기준)
            List<Career> existingCareers = careerRepository.findByResume(resume);
            Set<String> newCareerKeys = resumeDto.getCareers().stream()
                    .map(career -> career.getShopName() + "_" + dateFormatting(career.getJoinDate()))
                    .collect(Collectors.toSet());

            log.info("existingCareers before processing: {}", existingCareers);

            // 기존 데이터 중 새로운 목록에 없는 항목 삭제
            List<Career> careersToDelete = new ArrayList<>();
            for (Career career : existingCareers) {
                String crKey = career.getName() + "_" + career.getJoinDate();
                if (!newCareerKeys.contains(crKey)) {
                    careersToDelete.add(career);
                    log.info("careersToDelete : {}", careersToDelete);
                }
            }
            careerRepository.deleteAll(careersToDelete);

            log.info("Deleted careers: {}", careersToDelete);

            // 새로운 데이터 추가 또는 기존 데이터 업데이트
            for (CareerRequestDto careerDto : resumeDto.getCareers()) {
                LocalDate joinDate = dateFormatting(careerDto.getJoinDate());
                LocalDate outDate = Optional.ofNullable(dateFormatting(careerDto.getOutDate())).orElse(null);
                String crKey = careerDto.getShopName() + "_" + joinDate;

                // 기존 데이터 조회 또는 새 객체 생성
                Career career = existingCareers.stream()
                        .filter(c -> (c.getName() + "_" + c.getJoinDate()).equals(crKey))
                        .findFirst()
                        .orElseGet(() -> new Career());

                career.updateShopName(careerDto.getShopName());
                career.updateJoinDate(joinDate);
                career.updateOutDate(outDate);
                career.updatePosition(careerDto.getPosition());
                career.updateResume(resume);
                careerRepository.save(career);
            }
        }
    }


    //자격증 추가
    @Transactional
    public void updateCertificates(Resume resume, ResumeRequestDto resumeDto) {
        if(resumeDto.getCertificates() != null){
            //기존 자격증 데이터 가져오기(name기준)

            Map<String, Certification>existingCertificates = certificationRepository.findByResume(resume)
                    .stream()
                    .collect(Collectors.toMap(Certification::getName, Function.identity()));

            //업데이트된 자격증 목록 생성
            Set<String> newCertificateName = resumeDto.getCertificates().stream()
                    .map(CertificationRequestDto::getName)
                    .collect(Collectors.toSet());

            // 기존 정보와 비교하여 새로운 목록에 없는 자격증 삭제
            for (String existingName : new HashSet<>(existingCertificates.keySet())) {
                log.info("existingCertificates: {}", existingCertificates.keySet());
                if (!newCertificateName.contains(existingName)) {
                    log.info("newCertificateName : {}", newCertificateName);
                    log.info("existingName : {}", existingName);

                    certificationRepository.deleteByName(existingName); // 삭제 수행

                    existingCertificates.remove(existingName);
                }
            }

            // 새로운 정보 추가 또는 기존 정보 업데이트
            for (CertificationRequestDto request : resumeDto.getCertificates()) {
                String certificateName = request.getName();

                if (existingCertificates.containsKey(certificateName)) {
                    // 기존 데이터와 비교하여 변경된 경우에만 업데이트
                    Certification existingCertification = existingCertificates.get(certificateName);
                    if (!Objects.equals(existingCertification.getName(), certificateName)) {
                        existingCertification.updateName(certificateName);
                        certificationRepository.save(existingCertification);
                    }
                } else {
                    // 새로운 자격증 추가
                    Certification certification = new Certification();
                    certification.updateName(certificateName);
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(day, formatter);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid day format: yyyyMMdd로 형태를 맞춰주세요." );
        }
    }
}

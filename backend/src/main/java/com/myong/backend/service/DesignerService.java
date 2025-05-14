package com.myong.backend.service;

import com.myong.backend.domain.dto.designer.*;
import com.myong.backend.domain.dto.designer.SignUpRequestDto;
import com.myong.backend.domain.dto.designer.UpdateProfileRequestDto;
import com.myong.backend.domain.dto.designer.data.ReviewData;
import com.myong.backend.domain.dto.user.response.DesignerReviewImageResponseDto;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.designer.Resume;
import com.myong.backend.domain.entity.shop.JobPost;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.exception.ResourceNotFoundException;
import com.myong.backend.repository.*;
import com.myong.backend.repository.ReviewRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@Builder
public class DesignerService {
    private final DesignerRepository designerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ShopRepository shopRepository;
    private final ResumeService resumeService;
    private final RedisTemplate<String,Object> redisTemplate;
    private final ReviewRepository reviewRepository;
    private final FileUploadService fileUploadService;
    private final SearchService searchService;
    private final JobPostRepository jobPostRepository;


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

        Designer designer = DesignerService.signupDesigner(request, birthday, passwordEncoder);


        designerRepository.save(designer);
        // 엘라스틱 써치 도큐멘트 저장
        searchService.designerSave(designer);

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

        List<ReviewData> reviews = reviewRepository.findAllByDesignerEmail(designer.getEmail());

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
                .image(designer.getImage())
                .backgroundImage(designer.getBackgroundImage())
                .build();
    }


    //디자이너 수정페이지
    public UpdateProfileResponseDto getUpdateProfile(String email) {

        Designer designer = designerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("디자이너를 찾을 수 없습니다"));

        return UpdateProfileResponseDto.builder()
                .name(designer.getName())
                .nickname(designer.getNickName())
                .email(designer.getEmail())
                .tel(designer.getTel())
                .description(designer.getDesc())
                .image(designer.getImage())
                .backgroundImage(designer.getBackgroundImage())
                .build();
    }

    //프로필 업데이트 하기
    @Transactional
    public Designer updateProfile(String email, UpdateProfileRequestDto updateProfileRequest, MultipartFile updateImage, MultipartFile updateBackgroundImage) {
        Designer designer = designerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("디자이너를 찾을 수 없습니다"));

        // 닉네임 변경 시 중복 체크
        if (updateProfileRequest.getUpdateNickName() != null && !updateProfileRequest.getUpdateNickName().equals(designer.getNickName())) {
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
        if (updateImage != null) {

            String route = "designer" + "/" + designer.getEmail() + "/" +"profile" + "/";
            // S3에 저장하고 저장된 url 반환
            String url = fileUploadService.uploadFile(updateImage,route);
            // 기존 이미지가 있다면 삭제
            if(designer.getImage() != null){
                fileUploadService.deleteFile(designer.getImage());
            }
            designer.updateImage(url);
            log.info("updateImage : {}", updateImage);
        }

        //백그라운드이미지 변경
        if (updateBackgroundImage != null) {

            String route = "designer" + "/" + designer.getEmail() + "/" +"profile" + "/";
            // S3에 저장하고 저장된 url 반환
            String url = fileUploadService.uploadFile(updateBackgroundImage,route);
            // 기존 이미지가 있다면 삭제
            if(designer.getBackgroundImage() != null){
                fileUploadService.deleteFile(designer.getBackgroundImage());
            }
            designer.updateBackgroundImage(url);
            log.info("updateImage : {}",updateBackgroundImage);
        }

        // 엘라스틱 써치 도큐멘트 업데이트
        searchService.designerSave(designer);
        // 명시적으로 save 호출하지 않아도 됨 (@Transactional 때문)
        return designerRepository.save(designer);
    }

//    @Transactional
//    public List<DesignerReservationResponseDto> getReservations(String email, LocalDate date) {
//        Designer designer = designerRepository
//                .findByEmail(email)
//                .orElseThrow(()->new IllegalArgumentException("디자이너를 찾을 수 없습니다."));
//
//        // 입력받은 날짜의 시작 월요일과 끝나는 일요일 찾기
//        LocalDate startOfWeek = date.with(DayOfWeek.MONDAY);
//        LocalDate endOfWeek = date.with(DayOfWeek.SUNDAY);
//
//
//        return designer.getReservations().stream()
//                .filter(reservation -> {
//                    LocalDateTime serviceDate = reservation.getServiceDate();
//                    return  reservation.getStatus() == ReservationStatus.SUCCESS &&
//                            !serviceDate.toLocalDate().isBefore(startOfWeek)//시작요일보다 빠른거 제외
//                            && !serviceDate.toLocalDate().isAfter(endOfWeek);//끝요일보다 늦은거 제외
//                })
//                .map(reservation -> DesignerReservationResponseDto.builder()
//                        .userName(reservation.getUser().getName())
//                        .menu(reservation.getMenu())
//                        .serviceDate(reservation.getServiceDate())
//                        .dayOfWeek(reservation.getServiceDate().getDayOfWeek())
//                        .build()
//                ).collect(Collectors.toList());
//    }

    //디자이너 헤더 로딩
    public DesignerLoadHeaderResponseDto loadHeader(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String designerEmail = authentication.getName();

        Designer designer = designerRepository.findByEmail(designerEmail)
                .orElseThrow(() -> new NoSuchElementException("해당 디자이너를 찾지 못했습니다."));

        return new DesignerLoadHeaderResponseDto(designer.getName());
    }


    /***
    구인페이지 서비스
    ***/
    @Transactional
    public JobPostListResponseDto getJopPostList(int page) {
        int size = 10; // 페이지당 불러오는 리스트의 수 10개 고정

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<JobPost> jobPosts = jobPostRepository.findAll(pageRequest);

        List<ResponseJobPostDetailDto> postsResponse = jobPosts.getContent().stream()
                .map(jobPost -> ResponseJobPostDetailDto.builder()
                        .postId(jobPost.getId()) // 구인 게시물 아이디
                        .title(jobPost.getTitle()) // 구인 게시물 제목
                        .content(jobPost.getContent()) // 구인 게시물 내용
                        .work(jobPost.getWork()) // 구인 종류(파트타임/ 풀타임)
                        .salary(jobPost.getSalary()) // 구인 임금
                        .shopName(jobPost.getShop().getName()) // 가게이름
                        .address(jobPost.getShop().getAddress()) // 가게주소
                        .postedAgo(getTimeAgo(jobPost.getCreateDate())) // 몇 분 전 작성 정보
                        .imageUrl(jobPost.getShop().getThumbnail()) // 가게 썸네일 이미지 주소
                        .build())
                .collect(Collectors.toList());

        return JobPostListResponseDto.builder()
                .jobPosts(postsResponse)
                .total((int) jobPosts.getTotalElements())
                .page(page)
                .pageSize(size)
                .build();
    }
    /***
     구인 게시판 상세페이지
    ***/

    public ResponseJobPostDetailDto getJobDetail(UUID id) {
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("해당 공고가 존재하지 않습니다."));

        return ResponseJobPostDetailDto.builder()
                .shopName(jobPost.getShop().getName())
                .title(jobPost.getTitle())
                .content(jobPost.getContent())
                .salary(jobPost.getSalary())
                .work(jobPost.getWork())
                .gender(jobPost.getGender())
                .workTime(jobPost.getWorkTime())
                .leaveTime(jobPost.getLeaveTime())
                .file(jobPost.getFile())
                .address(jobPost.getShop().getAddress())
                .build();
    }

    //디자이너 로그아웃

    /*
     *  유저 로그아웃
     */
    public ResponseEntity<String> Signout(HttpServletResponse response) {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        String designerEmail = auth.getName();

        try {

            if (redisTemplate.hasKey(designerEmail)) {
                redisTemplate.delete(designerEmail);
            }

            SecurityContextHolder.clearContext();


            ResponseCookie deleteCookie = ResponseCookie.from("accessToken",null)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(0)
                    .sameSite("Lax")
                    .build();

            response.addHeader("Set-Cookie",deleteCookie.toString());

        } catch (Exception e) {
            return ResponseEntity.status(400).body("로그아웃 요청 중 오류가 발생했습니다.");
        }

        return ResponseEntity.ok("로그아웃에 성공하셨습니다");

    }

    /**
     * 디자이너 리뷰 이미지 불러오기
     *
     * @param
     * @return 이미지 리스트
     */
    public List<DesignerReviewImageResponseDto> getDesignerReviewImage(String email){
        Designer designer = designerRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("해당 디자이너를 찾지 못했습니다."));
        return reviewRepository.findReviewImages(designer,PageRequest.of(0,10));
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
    public ResumeResponseDto getResume(String email) {
        return resumeService.getResume(email);
    }

    public static Designer signupDesigner(SignUpRequestDto request, LocalDate birthday, PasswordEncoder passwordEncoder){
        return Designer.builder()
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
                .workTime(LocalTime.of(0,0))
                .leaveTime(LocalTime.of(0,0))
                .build();
    }

    // 시간 구하기(ex:몇 분전, 몇 시간전, 몇 일전) 매서드
    public static String getTimeAgo(LocalDateTime time){
        LocalDateTime now = LocalDateTime.now();
        log.info("now : {}" , now);
        log.info("time : {}" , time);
        //ChronoUnit은 두 시간 사의 값을 가져옴
        long minutes = ChronoUnit.MINUTES.between(time, now);

        log.info("minutes : {}" , minutes);
        long hours = ChronoUnit.HOURS.between(time, now);
        long days = ChronoUnit.DAYS.between(time, now);

        if(minutes < 1){
            return "방금 전"; // 1분 미만일 때 방금 전으로 전달
        } else if (hours < 1) {
            return minutes + "분 전"; // 1시간 미만일 때 몇 분 전으로 전달
        } else if (days < 1) {
            return hours + "시간 전"; // 1일 미만일 때 몇 시간 전으로 전달
        } else if (days <= 15) {
            return days + "일 전";  //15일 미만일 때 몇 일 전으로 전달
        }else {
            return time.toLocalDate().toString(); // 15일이 지나면 날짜형식으로 전달(YYYY-MM-DD)
        }
    }

}
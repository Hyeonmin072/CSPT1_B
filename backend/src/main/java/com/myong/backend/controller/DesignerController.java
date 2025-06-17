package com.myong.backend.controller;


import com.myong.backend.domain.dto.chatting.response.ChatRoomMessageResponseDto;
import com.myong.backend.domain.dto.chatting.response.ChatRoomResponseDto;
import com.myong.backend.domain.dto.chatting.response.ChatUserInfoResponseDto;
import com.myong.backend.domain.dto.designer.*;
import com.myong.backend.domain.dto.email.EmailCheckDto;
import com.myong.backend.domain.dto.email.EmailRequestDto;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.designer.Resume;
import com.myong.backend.jwttoken.dto.UserDetailsDto;
import com.myong.backend.service.DesignerService;
import com.myong.backend.service.EmailSendService;
import com.myong.backend.service.ResumeService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/designer")
public class DesignerController {

    private final DesignerService designerService;
    private final ResumeService resumeService;


    @PostMapping("/signup")
    //회원가입
    public ResponseEntity<SignUpRequestDto> signup(
            @Valid
            @RequestBody
            SignUpRequestDto request){
        log.info("signup request: {}", request); //디버깅용 로그찍기

        designerService.signUp(request);

        ResponseEntity<SignUpRequestDto> response = ResponseEntity.status(HttpStatus.OK).body(request);

        return response;
    }

    //이메일 중복검사
    @GetMapping("/checkemail/{email}")
    public ResponseEntity<Boolean> checkedEmailDuplicate(@PathVariable(name = "email") String email){
        log.info("checked email duplicate: {}", email);
        //중복되면 true, 중복안되면 false
        return ResponseEntity.ok(designerService.checkEmailDuplication(email));
    }

    //닉네임 중복검사
    @GetMapping("/nickname/{nickname}/exists")
    public ResponseEntity<Boolean> checkedNicknameDuplicate(@PathVariable(value = "nickname") String nickname){
        log.info("checked nickname duplicate: {}", nickname);
        //중복되면 true, 중복안되면 false
        return ResponseEntity.ok(designerService.checkNicknameDuplication(nickname));
    }

    private final EmailSendService emailSendService;

    //Send Email: 이메일 전송 버튼 클릭시
    @PostMapping("/sendemail")
    public Map<String, String> mailSend(
            @RequestBody EmailRequestDto emailRequestDto
    ){
        String code = emailSendService.joinEmail(emailRequestDto.getEmail());
        //response를 Json으로 변환
        Map<String, String> response = new HashMap<>();
        response.put("code", code);

        return response;
    }

    //이메일 인증

    @PostMapping("/verifyemail")
    public String authCheck(@RequestBody @Valid EmailCheckDto emailCheckDto){
        Boolean checked = emailSendService.checkAuthNum(emailCheckDto.getEmail(), emailCheckDto.getAuthNum());

        if(checked){
            return "이메일 인증 성공!!";
        }else {
            throw new NullPointerException("이메일 인증 실패");
        }
    }

    //디자이너 프로필 불러오기
    @GetMapping("/profile")
    public ResponseEntity<DesignerProfileResponseDto> profile(){

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String designerEmail = authentication.getName();//토큰에서 디자이너 이메일을 추출

        DesignerProfileResponseDto responseDto = designerService.getProfile(designerEmail);
        return ResponseEntity.ok(responseDto);
    }

    // 디자이너 헤더 로딩
    @GetMapping("/loadheader")
    public ResponseEntity<DesignerLoadHeaderResponseDto> loadHeader(){
        return ResponseEntity.ok(designerService.loadHeader());
    }

    //로그아웃 요청
    @PostMapping("/signout")
    public ResponseEntity<String> Signout(HttpServletResponse response){
        return designerService.Signout(response);
    }


    //디자이너 프로필 수정 페이지
    @GetMapping("/profile/update")
    public ResponseEntity<UpdateProfileResponseDto> updateProfile(
    ){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String designerEmail = authentication.getName();//토큰에서 디자이너 이메일을 추출
        return ResponseEntity.ok(designerService.getUpdateProfile(designerEmail));
    }


    //디자이너 프로필 수정
    @PostMapping("/profile/update")
    public ResponseEntity<Designer> updateProfile(
            @Valid @RequestPart(name = "request") UpdateProfileRequestDto request,
            @RequestParam(name = "updateImage") MultipartFile updateImage,
            @RequestParam(name = "updateBackgroundImage") MultipartFile updateBackgroundImage
            ){
            log.info("update profile: {}", request);

            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            String designerEmail = authentication.getName();//토큰에서 디자이너 이메일을 추출

            Designer updatedesigner = designerService.updateProfile(designerEmail, request, updateImage, updateBackgroundImage);
            return ResponseEntity.ok(updatedesigner);
    }



    //디자이너 이력서 수정
    @PostMapping("/resume/update")
    public ResponseEntity<Resume> updateResume(
            @Valid  @RequestBody ResumeRequestDto resumeDto ){

            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            String designerEmail = authentication.getName();//토큰에서 디자이너 이메일을 추출
            log.info("update resume: {}", resumeDto);

        Resume resume = resumeService.updateResume(designerEmail, resumeDto);
        return ResponseEntity.ok(resume);
    }



    //디자이너 이력서 가져오기
    @GetMapping("/resume")
    public ResponseEntity<ResumeResponseDto> getResume(){

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String designerEmail = authentication.getName();//토큰에서 디자이너 이메일을 추출

        ResumeResponseDto responseDto = designerService.getResume(designerEmail);



        return ResponseEntity.ok(responseDto);
    }

    //구인포스트 가져오기
    @GetMapping("/job/posts")
    public ResponseEntity<JobPostListResponseDto> getJobPostList(
            @RequestParam(defaultValue = "0") int page
    ){
        JobPostListResponseDto response = designerService.getJobPostList(page);

        return ResponseEntity.ok(response);
    }

    //구인 포스터 상세페이지
    @GetMapping("/job/post/{postId}")
    public ResponseEntity<ResponseJobPostDetailDto> getJobPost(@PathVariable UUID postId) {
        ResponseJobPostDetailDto response = designerService.getJobDetail(postId);
        return ResponseEntity.ok(response);
    }

    /**
     *  나의 채팅방 조회
     */
    @GetMapping("/chatroom")
    public ResponseEntity<List<ChatRoomResponseDto>> loadChatRoom(@AuthenticationPrincipal UserDetailsDto user){
        return ResponseEntity.ok(designerService.loadChatRoom(user));
    }

    /**
     *  채팅방 입장
     */
    @PostMapping("/chatroom/join/{chatRoomId}")
    public ResponseEntity<List<ChatRoomMessageResponseDto>> loadChatRoomMessages(@PathVariable(name = "chatRoomId")UUID chatRoomId, @AuthenticationPrincipal UserDetailsDto user){
        return ResponseEntity.ok(designerService.loadChatRoomMessages(chatRoomId,user));
    }

    /**
     *  채팅방 입장시 디자이너 정보 로딩
     */
    @PostMapping("/info")
    public ResponseEntity<ChatUserInfoResponseDto> loadUserInfo(@AuthenticationPrincipal UserDetailsDto requestUser){
        return ResponseEntity.ok(designerService.loadUserInfo(requestUser));
    }

    /**
     * 디자이너 이메일로 디자이너 프로필 조회
     */
    @GetMapping("/profile/{designerEmail}")
    public ResponseEntity<DesignerProfileResponseDto> getProfileByEmail(@PathVariable(name = "designerEmail") String designerEmail, @AuthenticationPrincipal UserDetailsDto requestUser){
        return ResponseEntity.ok(designerService.getProfileByEmail(designerEmail,requestUser)); // 성공적으로 로직이 수행될 경우 디자이너 프로필 정보 반환

    }

    /** 게시글 전체 조회 **/
    @GetMapping("/notices")
    public ResponseEntity<List<NoticeResponseDto>> getAllNotices() {
        return ResponseEntity.ok(designerService.getAllNotices());
    }

    /** 게시글 단건 조회 **/
    @GetMapping("/notice/{noticeId}")
    public ResponseEntity<NoticeDetailResponseDto> getNoticeDetail(@PathVariable UUID noticeId) {
        return ResponseEntity.ok(designerService.getNoticeDetail(noticeId));
    }


    //디자이너 예약일 가져오기
//    @GetMapping("/reservation")
//    public List<DesignerReservationResponseDto> getReservation(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date){
//        Authentication authentication =
//                SecurityContextHolder.getContext().getAuthentication();
//
//        String designerEmail = authentication.getName();//토큰에서 디자이너 이메일을 추출
//
//        return designerService.getReservations(designerEmail, date);
//    }
}

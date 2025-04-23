package com.myong.backend.controller;

import com.myong.backend.domain.dto.coupon.CouponResponseDto;
import com.myong.backend.domain.dto.coupon.CouponRequestDto;
import com.myong.backend.domain.dto.event.EventResponseDto;
import com.myong.backend.domain.dto.event.EventRequestDto;
import com.myong.backend.domain.dto.job.JobPostRequestDto;
import com.myong.backend.domain.dto.job.JobPostResponseDto;
import com.myong.backend.domain.dto.job.JobPostDetailResponseDto;
import com.myong.backend.domain.dto.menu.MenuRequestDto;
import com.myong.backend.domain.dto.menu.MenuResponseDto;
import com.myong.backend.domain.dto.menu.MenuDetailResponseDto;
import com.myong.backend.domain.dto.reservation.response.ShopReservationDetailResponseDto;
import com.myong.backend.domain.dto.reservation.request.ShopReservationRequestDto;
import com.myong.backend.domain.dto.reservation.response.ShopReservationResponseDto;
import com.myong.backend.domain.dto.shop.*;
import com.myong.backend.service.DesignerService;
import com.myong.backend.service.ShopService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
@Slf4j
public class ShopController {
    private final ShopService shopService;
    private final DesignerService designerService;

    /**
     * 사업자 이메일 중복확인
     */
    @GetMapping("/checkemail/{email}")
    public ResponseEntity<Boolean> checkEmail(@Valid @PathVariable(name = "email") String email) {
        return ResponseEntity.ok(shopService.checkEmail(email)); // 성공적으로 로직이 수행될 경우 성공 구문 반환
    }

    /**
     * 사업자 전화번호 인증코드 보내기
     */
    @PostMapping("/certification/tel")
    public SingleMessageSentResponse sendVerifyCode(@Valid @RequestBody ShopTelRequestDto request) {
        return shopService.sendOne(request); // 성공적으로 로직이 수행될 경우 정보 반환
    }

    /**
     * 사업자 전화번호 인증코드 확인하기
     */
    @GetMapping("/certification/tel")
    public ResponseEntity<String> verifyCode(@Valid @RequestBody ShopVerifyCodeRequestDto request) {
        return ResponseEntity.ok(shopService.checkVerifyCode(request)); // 성공적으로 로직이 수행될 경우 성공 구문 반환
    }

    /**
     * 사업자번호 인증 및 중복확인
     */
    @GetMapping("/bizid")
    public ResponseEntity<String> checkBiz(@Validated @RequestBody ShopBizRequestDto request) {
        return ResponseEntity.ok(shopService.checkBiz(request)); // 성공적으로 로직이 수행될 경우 성공 구문 반환
    }

    /**
     * 사업자 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<String> shopSignUp(@Valid @RequestBody ShopSignUpRequestDto request) {
        return ResponseEntity.ok(shopService.shopSignUp(request)); // 성공적으로 로직이 수행될 경우 성공을 알리는 구문 반환
    }

    /**
     * 로그인후 표시될 사업자 이름 (헤더 반환)
     */
    @GetMapping("/loadheader")
    public ResponseEntity<String> loadHeader() {
        return ResponseEntity.ok(shopService.loadHeader()); // 성공적으로 로직이 수행될 경우 사업자 이름 반환
    }


    /**
     * 로그아웃 요청
     */
    @PostMapping("/signout")
    public ResponseEntity<String> signOut(HttpServletResponse response) {
        return shopService.signOut(response);
    }

    /**
     * 쿠폰 등록
     */
    @PostMapping("/coupon")
    public ResponseEntity<String> addCoupon(@Valid @RequestBody CouponRequestDto request) {
        return ResponseEntity.ok(shopService.addCoupon(request)); // 성공적으로 로직이 수행될 경우 성공을 알리는 구문 반환
    }

    /**
     * 등록한 쿠폰목록 조회
     */
    @GetMapping("/coupons")
    public ResponseEntity<List<CouponResponseDto>> getCoupons() {
        return ResponseEntity.ok(shopService.getCoupons()); // 성공적으로 로직이 수행될 경우 쿠폰 목록 반환
    }

    /**
     * 이벤트 등록
     */
    @PostMapping("/event")
    public ResponseEntity<String> addEvent(@Valid @RequestBody EventRequestDto request) {
        return ResponseEntity.ok(shopService.addEvent(request)); // 성공적으로 로직이 수행될 경우 성공을 알리는 구문 반환
    }

    /**
     * 등록한 이벤트목록 조회
     */
    @GetMapping("/events")
    public ResponseEntity<List<EventResponseDto>> getEvents() {
        return ResponseEntity.ok(shopService.getEvents()); // 성공적으로 로직이 수행될 경우 이벤트 목록 반환
    }

    /**
     * 사업자 프로필 조회
     */
    @GetMapping("/profile")
    public ResponseEntity<ShopProfileResponseDto> getProfile() {
        return ResponseEntity.ok(shopService.getProfile()); // 성공적으로 로직이 수행될 경우 프로필 정보 반환
    }

    /**
     * 사업자 프로필 수정
     */
    @PatchMapping("/profile")
    public ResponseEntity<String> updateProfile(@Valid @RequestBody ShopProfileRequestDto request) {
        return ResponseEntity.ok(shopService.updateProflie(request)); // 성공적으로 로직이 수행될 경우 프로필 정보 반환
    }

    /**
     * 사업자 메뉴 등록
     */
    @PostMapping("/menu")
    public ResponseEntity<String> addMenu(@Valid @RequestBody MenuRequestDto request) {
        return ResponseEntity.ok(shopService.addMenu(request)); // 성공적으로 로직이 수행될 경우 성공을 알리는 구문 반환
    }

    /**
     * 사업자 메뉴 목록 조회
     */
    @GetMapping("/menus")
    public ResponseEntity<List<MenuResponseDto>> getMenu() {
        return ResponseEntity.ok(shopService.getMenus()); // 성공적으로 로직이 수행될 경우 메뉴 정보 반환
    }

    /**
     * 사업자 메뉴 단건 조회
     */
    @GetMapping("/menu/{menuId}")
    public ResponseEntity<MenuDetailResponseDto> getMenu(@PathVariable("menuId") String id) {
        return ResponseEntity.ok(shopService.getMenu(id)); // 성공적으로 로직이 수행될 경우 메뉴 정보 반환
    }

    /**
     * 사업자 메뉴 수정
     */
    @PatchMapping("/menu")
    public ResponseEntity<String> updateMenu(@Valid @RequestBody MenuRequestDto request) {
        return ResponseEntity.ok(shopService.updateMenu(request)); // 성공적으로 로직이 수행될 경우 성공을 알리는 구문 반환
    }

    /**
     * 사업자 메뉴 삭제
     */
    @DeleteMapping("/menu")
    public ResponseEntity<String> deleteMenu(@Valid @RequestBody MenuRequestDto request) {
        return ResponseEntity.ok(shopService.deleteMenu(request)); // 성공적으로 로직이 수행될 경우 성공을 알리는 구문 반환
    }

    /**
     * 사업자 구인글 등록
     */
    @PostMapping("/jobpost")
    public ResponseEntity<String> addJobPost(@Validated @RequestBody JobPostRequestDto request) {
        return ResponseEntity.ok(shopService.addJobPost(request));
    }

    /**
     * 사업자 구인글 목록 조회
     */
    @GetMapping("/jobposts")
    public ResponseEntity<List<JobPostResponseDto>> getJobPosts() {
        return ResponseEntity.ok(shopService.getJobPosts());
    }

    /**
     * 사업자 구인글 단건 조회
     */
    @GetMapping("/jobpost/{jobpostId}")
    public ResponseEntity<JobPostDetailResponseDto> getJobPost(@PathVariable("jobpostId") String id) {
        return ResponseEntity.ok(shopService.getJobPost(id));
    }

    /**
     * 사업자 구인글 수정
     */
    @PatchMapping("/jobpost")
    public ResponseEntity<String> updateJobPost(@Validated @RequestBody JobPostRequestDto request) {
        return ResponseEntity.ok(shopService.updateJobPost(request));
    }

    /**
     * 사업자 구인글 마감
     */
    @DeleteMapping("/jobpost")
    public ResponseEntity<String> deleteJobPost(@Validated @RequestBody JobPostRequestDto request) {
        return ResponseEntity.ok(shopService.deleteJobPost(request));
    }
    
    /**
     * 사업자 소속 디자이너 추가
     */
    @PostMapping("/designer")
    public ResponseEntity<String> joinDesigner(@Validated @RequestBody ShopDesignerRequestDto request) {
        return ResponseEntity.ok(shopService.joinDesigner(request));
    }

    /**
     * 사업자 추가할 디자이너 정보 조회
     */
    @PostMapping("/designer/search")
    public ResponseEntity<ShopDesignerDetailResponseDto> searchDesigner(@Validated @RequestBody ShopDesignerRequestDto request) {
        return ResponseEntity.ok(shopService.searchDesigner(request));
    }

    /**
     * 사업자 소속 디자이너의 휴일 추가
     */
    @PostMapping("/designer/holiday")
    public ResponseEntity<String> postDesignerHoliday(@Validated @RequestBody ShopDesignerHolidayRequestDto request) {
        return ResponseEntity.ok(shopService.createDesignerHoliday(request));
    }

    /**
     * 사업자 소속 디자이너 목록 조회
     */
    @GetMapping("/designers")
    public ResponseEntity<List<ShopDesignerResponseDto>> getDesigners() {
        return ResponseEntity.ok(shopService.getDesigners());
    }

    /**
     * 사업자 소속 디자이너 단건 조회
     */
    @GetMapping("/designer")
    public ResponseEntity<ShopDesignerDetailResponseDto> getDesigner(@Validated @RequestBody ShopDesignerRequestDto request) {
        return ResponseEntity.ok(shopService.getDesignerDetail(request));
    }

    /**
     * 사업자 소속 디자이너 수정(출퇴근, 정기휴무일)
     */
    @PatchMapping("/designer")
    public ResponseEntity<String> postDesignser(@Validated @RequestBody ShopDesignerUpdateRequestDto request) {
        return ResponseEntity.ok(shopService.updateDesigner(request));
    }

    /**
     * 사업자 소속 디자이너 삭제
     */
    @DeleteMapping("/designer")
    public ResponseEntity<String> deleteDesigner(@Validated @RequestBody ShopDesignerRequestDto request) {
        return ResponseEntity.ok(shopService.deleteDesigner(request));
    }

    /**
     * 사업자 블랙리스트 추가
     */
    @PostMapping("/blacklist")
    public ResponseEntity<String> createBlackList(@Validated @RequestBody BlackListRequestDto request) {
        return ResponseEntity.ok(shopService.createBlackList(request));
    }

    /**
     * 사업자 블랙리스트 목록 조회
     */
    @GetMapping("/blacklists")
    public ResponseEntity<List<BlackListResponseDto>> getBlackLists() {
        return ResponseEntity.ok(shopService.getBlackLists());
    }

    /**
     * 사업자 블랙리스트 단건 조회
     */
    @GetMapping("/blacklist/{blacklistId}")
    public ResponseEntity<BlackListResponseDto> getBlackList(@PathVariable("blacklistId") String id) {
        return ResponseEntity.ok(shopService.getBlackList(id));
    }

    /**
     * 사업자 블랙리스트 삭제
     */
    @DeleteMapping("/blacklist")
    public ResponseEntity<String> deleteBlackList(@Validated @RequestBody List<BlackListRequestDto> requests) {
        return ResponseEntity.ok(shopService.deleteBlackList(requests));
    }

    /**
     * 사업자 예약 관리(조회)
     */
    @GetMapping("/reservations")
    public ResponseEntity<List<ShopReservationResponseDto>> getReservations(@Validated @RequestBody ShopReservationRequestDto request) {
        return ResponseEntity.ok(shopService.getReservations(request));
    }

    /**
     * 사업자 예약 상세 관리(상세 조회)
     */
    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<ShopReservationDetailResponseDto> getReservation(@PathVariable UUID reservationId) {
        return ResponseEntity.ok(shopService.getReservation(reservationId));
    }

    /**
     * 사업자 오늘 남은 예약 개수 조회
     */
    @GetMapping("/reservations/today")
    public ResponseEntity<Long> getReservationsToday() {
        return ResponseEntity.ok(shopService.getReservationsToday());
    }

    /**
     * 사업자 근태 관리(조회)
     */
    @GetMapping("/attendances")
    public ResponseEntity<List<ShopAttendanceResponseDto>> getAttendance(@Validated @RequestBody ShopAttendanceRequestDto request) {
        return ResponseEntity.ok(shopService.getAttendance(request));
    }

    /**
     * 사업자 공지사항 생성
     */
    @PostMapping("/notice")
    public ResponseEntity<String> createNotice(@Validated @RequestBody ShopNoticeRequestDto request) {
        return ResponseEntity.ok(shopService.createNotice(request));
    }

    /**
     * 사업자 공지사항 전체 조회
     */
    @GetMapping("/notices")
    public ResponseEntity<List<ShopNoticeResponseDto>> getNotices() {
        return ResponseEntity.ok(shopService.getNotices());
    }

    /**
     * 사업자 공지사항 단건 조회
     */
    @GetMapping("/notice/{noticeId}")
    public ResponseEntity<ShopNoticeDetailResponseDto> getNotice(@PathVariable("noticeId") String id) {
        return ResponseEntity.ok(shopService.getNotice(id));
    }

    /**
     * 사업자 가장 최신의 공지사항 단건 조회
     */
    @GetMapping("/notice/latest")
    public ResponseEntity<ShopNoticeDetailResponseDto> getNoticeLatest() {
        return ResponseEntity.ok(shopService.getNoticeLatest());
    }

    /**
     * 사업자 공지사항 수정
     */
    @PatchMapping("/notice/{noticeId}")
    public ResponseEntity<String> updateNotice(@PathVariable("noticeId") String id,
                                               @Validated @RequestBody ShopNoticeRequestDto request) {
        return ResponseEntity.ok(shopService.updateNotice(id, request));
    }

    /**
     * 사업자 공지사항 삭제
     */
    @DeleteMapping("/notice/{noticeId}")
    public ResponseEntity<String> deleteNotice(@PathVariable("noticeId") String id) {
        return ResponseEntity.ok(shopService.deleteNotice(id));
    }
}

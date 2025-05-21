package com.myong.backend.controller;

import com.myong.backend.domain.dto.coupon.CouponRequestDto;
import com.myong.backend.domain.dto.coupon.CouponResponseDto;
import com.myong.backend.domain.dto.event.EventRequestDto;
import com.myong.backend.domain.dto.event.EventResponseDto;
import com.myong.backend.domain.dto.job.JobPostDetailResponseDto;
import com.myong.backend.domain.dto.job.JobPostRequestDto;
import com.myong.backend.domain.dto.job.JobPostResponseDto;
import com.myong.backend.domain.dto.menu.MenuCreateRequestDto;
import com.myong.backend.domain.dto.menu.MenuDetailResponseDto;
import com.myong.backend.domain.dto.menu.MenuResponseDto;
import com.myong.backend.domain.dto.menu.MenuUpdateRequestDto;
import com.myong.backend.domain.dto.payment.DesignerSalesDetailResponseDto;
import com.myong.backend.domain.dto.payment.DesignerSalesResponseDto;
import com.myong.backend.domain.dto.payment.ShopSalesResponseDto;
import com.myong.backend.domain.dto.reservation.request.ShopReservationRequestDto;
import com.myong.backend.domain.dto.reservation.response.ShopReservationDetailResponseDto;
import com.myong.backend.domain.dto.reservation.response.ShopReservationJPAResponseDto;
import com.myong.backend.domain.dto.reservation.response.ShopReservationMyBatisResponseDto;
import com.myong.backend.domain.dto.shop.*;
import com.myong.backend.domain.entity.Period;
import com.myong.backend.service.ReservationService;
import com.myong.backend.service.ShopService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
@Slf4j
public class ShopController {
    private final ShopService shopService;
    private final ReservationService reservationService;

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
     * 등록한 이벤트 목록 조회
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
    public ResponseEntity<String> updateProfile(@Validated @RequestPart(name = "request") ShopProfileRequestDto request,
                                                @RequestPart(name = "thumbnail", required = false) MultipartFile thumbnail,
                                                @RequestPart(name = "banner", required = false) List<MultipartFile> banner) {
        return ResponseEntity.ok(shopService.updateProflie(request, thumbnail, banner)); // 성공적으로 로직이 수행될 경우 프로필 정보 반환
    }

    /**
     * 사업자 배너 삭제
     */
    @DeleteMapping("/banner/{url}")
    public ResponseEntity<String> deleteBanner(@PathVariable String url){
        return ResponseEntity.ok(shopService.deleteBanner(url));
    }

    /**
     * 사업자 메뉴 등록
     */
    @PostMapping("/menu")
    public ResponseEntity<String> createMenu(@Valid @RequestBody MenuCreateRequestDto request) {
        return ResponseEntity.ok(shopService.createMenu(request)); // 성공적으로 로직이 수행될 경우 성공을 알리는 구문 반환
    }

    /**
     * 사업자 소속 디자이너의 메뉴 조회
     */
    @GetMapping("/{designerEmail}/menus")
    public ResponseEntity<List<MenuResponseDto>> getMenus(@PathVariable String designerEmail) {
        return ResponseEntity.ok(shopService.getMenus(designerEmail)); // 성공적으로 로직이 수행될 경우 메뉴 정보 반환
    }

    /**
     * 사업자 메뉴 단건 조회
     */
    @GetMapping("/menus/{menuId}")
    public ResponseEntity<MenuDetailResponseDto> getMenu(@PathVariable("menuId") String id) {
        return ResponseEntity.ok(shopService.getMenu(id)); // 성공적으로 로직이 수행될 경우 메뉴 정보 반환
    }

    /**
     * 사업자 메뉴 수정
     */
    @PatchMapping("/menus/{menuId}")
    public ResponseEntity<String> updateMenu(@PathVariable("menuId") String id,
                                             @Validated @RequestBody MenuUpdateRequestDto request) {
        return ResponseEntity.ok(shopService.updateMenu(id, request)); // 성공적으로 로직이 수행될 경우 성공을 알리는 구문 반환
    }

    /**
     * 사업자 메뉴 삭제
     */
    @DeleteMapping("/menus/{menuId}")
    public ResponseEntity<String> deleteMenu(@PathVariable("menuId") String id) {
        return ResponseEntity.ok(shopService.deleteMenu(id)); // 성공적으로 로직이 수행될 경우 성공을 알리는 구문 반환
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
    @GetMapping("/jobposts/{jobpostId}")
    public ResponseEntity<JobPostDetailResponseDto> getJobPost(@PathVariable("jobpostId") String id) {
        return ResponseEntity.ok(shopService.getJobPost(id));
    }

    /**
     * 사업자 구인글 수정
     */
    @PatchMapping("/jobposts/{jobpostId}")
    public ResponseEntity<String> updateJobPost(@PathVariable("jobpostId") String id,
                                                @Validated @RequestBody JobPostRequestDto request) {
        return ResponseEntity.ok(shopService.updateJobPost(id, request));
    }

    /**
     * 사업자 구인글 마감
     */
    @DeleteMapping("/jobposts/{jobpostId}")
    public ResponseEntity<String> deleteJobPost(@PathVariable("jobpostId") String id) {
        return ResponseEntity.ok(shopService.deleteJobPost(id));
    }

    /**
     * 사업자 소속 디자이너 추가
     */
    @PostMapping("/designers")
    public ResponseEntity<String> joinDesigner(@Validated @RequestBody ShopDesignerRequestDto request) {
        return ResponseEntity.ok(shopService.joinDesigner(request));
    }

    /**
     * 사업자 소속 디자이너의 휴일 추가
     */
    @PostMapping("/designers/{designerEmail}/holidays")
    public ResponseEntity<String> postDesignerHoliday(@PathVariable("designerEmail") String designerEmail,
                                                      @Validated @RequestBody ShopDesignerHolidayRequestDto request) {
        return ResponseEntity.ok(shopService.createDesignerHoliday(designerEmail, request));
    }

    /**
     * 사업자 추가할 디자이너 정보 조회
     */
    @GetMapping("/designers/search")
    public ResponseEntity<ShopDesignerDetailResponseDto> searchDesigner(@RequestParam("designerEmail") String designerEmail) {
        return ResponseEntity.ok(shopService.searchDesigner(designerEmail));
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
    @GetMapping("/designers/{designerEmail}")
    public ResponseEntity<ShopDesignerDetailResponseDto> getDesigner(@PathVariable("designerEmail") String designerEmail) {
        return ResponseEntity.ok(shopService.getDesignerDetail(designerEmail));
    }

    /**
     * 사업자 소속 디자이너 수정(출퇴근, 정기휴무일)
     */
    @PatchMapping("/designers/{designerEmail}")
    public ResponseEntity<String> postDesignser(@PathVariable("designerEmail") String designerEmail,
                                                @Validated @RequestBody ShopDesignerUpdateRequestDto request) {
        return ResponseEntity.ok(shopService.updateDesigner(designerEmail, request));
    }

    /**
     * 사업자 소속 디자이너 삭제
     */
    @DeleteMapping("/designers/{designerEmail}")
    public ResponseEntity<String> fireDesigner(@PathVariable("designerEmail") String designerEmail) {
        return ResponseEntity.ok(shopService.fireDesigner(designerEmail));
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
    @GetMapping("/blacklists/{blacklistId}")
    public ResponseEntity<BlackListResponseDto> getBlackList(@PathVariable("blacklistId") String id) {
        return ResponseEntity.ok(shopService.getBlackList(id));
    }

    /**
     * 사업자 블랙리스트 삭제
     */
    @DeleteMapping("/blacklist")
    public ResponseEntity<String> deleteBlackList(@Validated @RequestBody List<String> userEmails) {
        return ResponseEntity.ok(shopService.deleteBlackList(userEmails));
    }

    /**
     * 사업자 예약 관리(조회)
     */
    @GetMapping("/reservations")
    public ResponseEntity<List<ShopReservationMyBatisResponseDto>> getReservations(@Validated @RequestBody ShopReservationRequestDto request) {
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
     * 지난 7일 간의 예약 조회 (블랙리스트 추가를 위한 조회 시 사용)
     */
    @GetMapping("/reservations/seven")
    public ResponseEntity<List<ShopReservationJPAResponseDto>> getLastSevenDaysReservation() {
        return ResponseEntity.ok(shopService.getLastSevenDaysReservation());
    }

    /**
     * 사업자 예약 거절
     */
    @PostMapping("/reservation/refuse")
    public ResponseEntity<Map> refuseReservation(@RequestParam String paymentKey,
                                                 @RequestParam String cancelReason) {
        return ResponseEntity.ok().body(reservationService.refuseReservation(paymentKey, cancelReason));
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

    /**
     * 사업자 가게 매출 조회
     */
    @GetMapping("/sales")
    public ResponseEntity<ShopSalesResponseDto> getShopSales(@RequestParam(name = "period") Period period) {
        return ResponseEntity.ok(shopService.getShopSales(period));
    }

    /**
     * 사업자 소속 디자이너 매출 목록 조회
     */
    @GetMapping("/sales/designers")
    public ResponseEntity<List<DesignerSalesResponseDto>> getDesignersSales() {
        return ResponseEntity.ok(shopService.getDesignersSales());
    }

    /**
     * 사업자 소속 디자이너 중 단건의 매출 조회(캘린더)
     */
    @GetMapping("/sales/designers/{designerEmail}")
    public ResponseEntity<Map<Integer, Long>> getDesignerSales(@PathVariable String designerEmail,
                                                               @RequestParam Integer year,
                                                               @RequestParam Integer month) {
        return ResponseEntity.ok(shopService.getDesignerSales(designerEmail, year, month));
    }

    /**
     * 사업자 소속 디자이너 중 단건의 매출 조회(날짜)
     */
    @GetMapping("/sales/designers/{designerEmail}/detail")
    public ResponseEntity<List<DesignerSalesDetailResponseDto>> getDesignerSales(@PathVariable String designerEmail,
                                                                                 @RequestParam Integer year,
                                                                                 @RequestParam Integer month,
                                                                                 @RequestParam Integer day) {
        return ResponseEntity.ok(shopService.getDesignerSale(designerEmail, year, month, day));
    }

    /**
     * 사업자 메인 페이지(오늘 남은 예약 인원, 이번 달 매출, 이번 달 매출 우수 디자이너, 이번 달 좋아요 우수 디자이너, 가게 평점과 리뷰개수)
     */
    @GetMapping("/main")
    public ResponseEntity<ShopMainResponseDto> getShopMain() {
        return ResponseEntity.ok(shopService.getShopMain());
    }
}

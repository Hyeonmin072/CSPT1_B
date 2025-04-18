package com.myong.backend.service;

import com.myong.backend.api.KakaoMapApi;
import com.myong.backend.domain.dto.user.data.*;
import com.myong.backend.domain.dto.user.request.ShopDetailsResponseDto;
import com.myong.backend.domain.dto.user.request.UserUpdateLocationRequestDto;
import com.myong.backend.domain.dto.user.response.*;
import com.myong.backend.domain.dto.user.request.UserSignUpDto;
import com.myong.backend.domain.entity.Advertisement;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.user.*;
import com.myong.backend.domain.entity.userdesigner.UserDesignerLike;
import com.myong.backend.domain.entity.usershop.Review;
import com.myong.backend.exception.DuplicateResourceException;
import com.myong.backend.exception.ResourceNotFoundException;
import com.myong.backend.jwttoken.JwtService;
import com.myong.backend.jwttoken.dto.UserDetailsDto;
import com.myong.backend.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.Optional;
import java.util.stream.Collectors;


import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisTemplate<String,Object> redisTemplate;
    private final KakaoMapApi kakaoMapApi;
    private final ShopRepository shopRepository;
    private final AdvertisementRepository advertisementRepository;
    private final UserDesignerLikeRepository userDesignerLikeRepository;
    private final DesignerRepository designerRepository;
    private final MemberShipRepository memberShipRepository;
    private final UserCouponRepository userCouponRepository;
    private final ReviewRepository reviewRepository;


    /**
     유저 회원가입 + 엘라스틱써치
     **/
    public ResponseEntity<String> SingUp(UserSignUpDto userSignUpDto){

        Optional<User> ouser =  userRepository.findByEmail(userSignUpDto.getEmail());

        // 이메일 중복 체크
        if (checkEmailDuplication(userSignUpDto.getEmail())) {
            throw new DuplicateResourceException("이미 존재하는 이메일입니다.");
        }

        if(!ouser.isPresent()){

            String result = kakaoMapApi.getCoordinatesFromAddress(userSignUpDto.getAddress());
            System.out.println("위도와 경도:"+result);
            String latitude = result.split(" ")[0];
            String longitude = result.split(" ")[1];

            User user = new User(
                    userSignUpDto.getName(),
                    userSignUpDto.getEmail(),
                    passwordEncoder.encode(userSignUpDto.getPassword()),
                    userSignUpDto.getTel(),
                    userSignUpDto.getBirth(),
                    userSignUpDto.getGender(),
                    userSignUpDto.getAddress(),
                    userSignUpDto.getPost(),
                    Double.parseDouble(longitude),
                    Double.parseDouble(latitude),
                    userSignUpDto.getAddress()
            );

            MemberShip memberShip = MemberShip.builder()
                    .user(user)
                    .grade(Grade.NONE)
                    .build();

            userRepository.save(user);
            memberShipRepository.save(memberShip);
            return ResponseEntity.ok("회원 가입에 성공하셨습니다.");
        }
        return ResponseEntity.status(400).body("회원가입에 실패하셨습니다.");
    }


    /**
     *  유저 로그아웃
     **/
    public ResponseEntity<String> Signout(HttpServletResponse response) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        try {

            // 리프레시토큰 삭제
            if (redisTemplate.hasKey(userEmail)) {
                redisTemplate.delete(userEmail);
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
     * 이메일 중복 체크
     *
     * @param email
     * @return 트루 펄스값
     */
    public Boolean checkEmailDuplication(String email) {
        return userRepository.existsByEmail(email);
    }


    /**
     * 헤어샵 페이지 로드
     * 2km 반경가까운 헤어샵 평점순 리스트 제공,
     * 2km가 없을시 ex"대구" 시 기준 리스트제공
     * 없으면 빈 리스트 제공
     *
     * @return 유저위치, 가게리스트, 등록된가게갯수,등록된디자이너갯수,리뷰갯수,
     */
    public UserHairShopPageResponseDto loadHairShopPage(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        Optional<User> findUser = userRepository.findByEmail(userEmail);

        if(!findUser.isPresent()){
            throw new ResourceNotFoundException("해당 유저가 존재하지않습니다");
        }

        User user = findUser.get();

        // 2km 반경 디비에서 조회
        List<Shop> shopsIn2km =  shopRepository.findShopWithinRadius(user.getLongitude(), user.getLatitude());

        // 등록된 데이터 갯수 조회
        long registeredShopCnt = shopRepository.count();
        long registeredDesignerCnt = designerRepository.count();
        long registeredReviewCnt = reviewRepository.count();


        // 2km 반경 헤어샵이 없으면 "시" 기준 디비에서 조회
        if(shopsIn2km.size() < 1){

            // ex("대구") 로 시작하는 가게 조회
            String location = user.getLocation().split(" ")[0];
            List<Shop> shopsInLocation = shopRepository.findShopWithAddress(location);

            Collections.sort(shopsInLocation,(shop1,shop2) -> Double.compare(shop2.getRating(),shop1.getRating()));

            List<ShopListData> shopListData =
                    shopsInLocation.stream()
                            .map(ShopListData::from)
                            .collect(Collectors.toList());

            return UserHairShopPageResponseDto.builder()
                    .location(user.getLocation())
                    .shops(shopListData)
                    .registeredShopCnt(registeredShopCnt)
                    .registeredDesignerCnt(registeredDesignerCnt)
                    .registeredReviewCnt(registeredReviewCnt)
                    .build();

        }

        // 2km 반경 평점순 정렬
        Collections.sort(shopsIn2km,(shop1, shop2) -> Double.compare(shop2.getRating(),shop1.getRating()));

        List<ShopListData> shopListData =
                shopsIn2km.stream()
                        .map(ShopListData::from)
                        .collect(Collectors.toList());

        return UserHairShopPageResponseDto.builder()
                .location(user.getLocation())
                .shops(shopListData)
                .registeredShopCnt(registeredShopCnt)
                .registeredDesignerCnt(registeredDesignerCnt)
                .registeredReviewCnt(registeredReviewCnt)
                .build();

    }

    /**
     * 헤어샵 데이터 최신순 정렬
     *
     * @return 최신순 가게 데이터
     */
    public List<ShopListData> hairshopSortNewest(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("해당 유저를 찾지 못했습니다."));

        List<Shop> shopsIn2km = shopRepository.findShopWithinRadius(user.getLongitude(),user.getLatitude());

        // 최신순 정렬
        Collections.sort(shopsIn2km,(shop1,shop2) -> shop2.getCreateDate().compareTo(shop1.getCreateDate()));

        return shopsIn2km.stream()
                .map(ShopListData::from)
                .collect(Collectors.toList());
    }

    /**
     * 홈페이지 로드
     *
     * @return 평점기준 탑3가게, 탑4디자이너, 광고
     */
    public UserHomePageResponseDto loadHomePage(){

        List<Shop> top3Shops = shopRepository.findTopShops(PageRequest.of(0,3));

        List<Designer> top4Designers = designerRepository.findTopDesigners(PageRequest.of(0,4));

        List<Advertisement> adList = advertisementRepository.findAll();

        // 디자이너 리스트 반환
        List<DesignerTop4ListData> designerTop4ListData =
                top4Designers.stream().map(designer -> DesignerTop4ListData.builder()
                        .designerName(designer.getName())
                        .designerDesc(designer.getDesc())
                        .designerImage(designer.getImage())
                        .designerRating(designer.getRating())
                        .build()).collect(Collectors.toList());

        // 샵리스트 변환
        List<ShopTop3ListData> shopTop3ListData =
                top3Shops.stream().map(shop ->  ShopTop3ListData.builder()
                        .shopName(shop.getName())
                        .shopDesc(shop.getDesc())
                        .shopRating(shop.getRating())
                        .shopReviewCount(shop.getReviewCount())
                        .shopThumbnail(shop.getThumbnail())
                    .build()).collect(Collectors.toList());

        return  UserHomePageResponseDto.builder()
                .top4Designers(designerTop4ListData)
                .top3Shops(shopTop3ListData)
                .advertisements(adList)
                .build();
    }

    /**
     * 헤더 컴포넌트 로드
     *
     * @return 유저이름
     */
    public UserHeaderResponseDto loadHeader(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("해당 유저를 찾지 못했습니다."));

        return new UserHeaderResponseDto(user.getName());
    }

    /**
     *
     *
     * @param email
     * @return 가게리스트, 디자이너리스트, 할인이가장크게되는쿠폰값, 리뷰데이터, 리뷰이미지
     */
    public ShopDetailsResponseDto loadHairShopDetailsPage (String email){
        Shop shop = shopRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("해당 가게를 찾을 수 없습니다"));

        //가게 데이터 정리
        ShopListData shopListData = ShopListData.from(shop);

        // 디자이너 데이터 정리
        List<Designer> designers = shop.getDesigners();
        List<DesignerListData> designerListDtos =
                designers.stream().map(designer -> new DesignerListData(
                    designer.getName(),
                    designer.getDesc(),
                    designer.getLike(),
                    designer.getRating()
                )).collect(Collectors.toList());


        // 리뷰 데이터 정리
        List<Review> reviews = shop.getReviews();
        List<ReviewListData> reviewListDtos =
                reviews.stream().map(review -> new ReviewListData(
                        review.getReservation().getMenu().getName(),
                        review.getDesigner().getName(),
                        review.getUser().getName(),
                        review.getContent(),
                        review.getRating()
                )).collect(Collectors.toList());

        List<Coupon> coupons = shop.getCoupons();
        String highestPriceCoupon = "";

        if(coupons.size() > 0){
            // 유저 쿠폰 중 가장 할인율이 많은 값 선출
            Collections.sort(coupons,(o1,o2) -> o2.getPrice() - o1.getPrice());
            highestPriceCoupon = coupons.get(0).getPrice()+""+(coupons.get(0).getType().equals(DiscountType.FIXED) ? "원" : "%");
        }

        // 리뷰 별 이미지 데이터 가져오기
        List<String> reviewImageUrls = new ArrayList<>();
        for(Review review : reviews){
            if(review.getImage().equals("")){continue;}
            reviewImageUrls.add(review.getImage());
        }


        return new ShopDetailsResponseDto(
                shopListData,
                designerListDtos,
                reviewListDtos,
                highestPriceCoupon,
                reviewImageUrls
        );
    }

    /**
     * 좋아요 누른 디자이너 페이지 로드
     *
     * @return 디자이너 이름,설명,가게명,이미지
     */
    public List<DesignerPageResponseDto> loadDesignerPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("해당 유저를 찾을 수 없습니다."));

        List<UserDesignerLike> designers = user.getUserDesignerLikes();

        List<DesignerPageResponseDto> responseDtos = designers.stream().map(
                UserDesignerLike -> new DesignerPageResponseDto(
                        UserDesignerLike.getDesigner().getName(),
                        UserDesignerLike.getDesigner().getDesc(),
                        UserDesignerLike.getDesigner().getShop().getName(),
                        UserDesignerLike.getDesigner().getImage()
                )).collect(Collectors.toList());

        return responseDtos;
    }

    /**
     * 디자이너 좋아요 토글처리
     *
     * @param designerEmail
     * @return true , false
     */
    public boolean requestLikeForDesigner(String designerEmail){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        Designer designer = designerRepository.findByEmail(designerEmail).orElseThrow(() -> new ResourceNotFoundException("해당 디자이너를 찾지 못했습니다."));

        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("해당 유저를 찾지 못했습니다."));

        UserDesignerLike findUserDesignerLike = userDesignerLikeRepository.findByDesignerAndUser(designer,user).orElse(null);


        UserDesignerLike.UserDesignerLikeId id = UserDesignerLike.UserDesignerLikeId.builder()
                        .designerId(designer.getId())
                        .userId(user.getId())
                        .build();

        if(findUserDesignerLike == null){
            UserDesignerLike userDesignerLike = UserDesignerLike.builder()
                    .id(id)
                    .designer(designer)
                    .user(user)
            .build();
            userDesignerLikeRepository.save(userDesignerLike);
            return true;
        }
        userDesignerLikeRepository.delete(findUserDesignerLike);
        return false;
    }

    /**
     * 유저 프로필 페이지 로드
     *
     * @return 이름,이메일,주소,전화번호,등급
     */
    public UserProfileResponseDto loadUserProfilePage(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("해당 유저를 찾지 못했습니다."));


        return UserProfileResponseDto.builder()
                .userName(user.getName())
                .userEmail(user.getEmail())
                .userAdress(user.getAddress())
                .userTel(user.getTel())
                .userGrade(user.getMemberShip().getGrade())
                .build();
    }

    /**
     * 유저 쿠폰함 조회
     *
     * @return 쿠폰 데이터
     * @throws NotFoundException
     */
    public List<UserGetAllCouponsResponseDto> getAllCoupons() throws NotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("해당 유저를 찾지 못했습니다."));

        List<UserCoupon> userCoupons = userCouponRepository.findAllByUser(user);

        if(userCoupons.size() < 1){
            throw new ResourceNotFoundException("쿠폰이 존재하지 않아요");
        }

        List<UserGetAllCouponsResponseDto> userGetAllCouponsResponseDtos =
                userCoupons.stream()
                        .filter(userCoupon -> userCoupon.getCoupon().getStatus() != CouponStatus.USED)
                        .map(
                        userCoupon -> UserGetAllCouponsResponseDto.builder()
                                .price(userCoupon.getCoupon().getPrice())
                                .discountType(userCoupon.getCoupon().getType())
                                .shopName(userCoupon.getCoupon().getShop().getName())
                                .expireDate(userCoupon.getExpireDate())
                                .build()
                        ).collect(Collectors.toList());

        return userGetAllCouponsResponseDtos;

    }

    /**
     * 유저 위치 업데이트
     *
     * @param requestDto
     * @return 메세지
     */
    public String updateLocation (UserUpdateLocationRequestDto requestDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("해당 유저를 찾을 수 없습니다."));

        User updateUser = user.toBuilder()
                .location(requestDto.getAddress())
                .latitude(requestDto.getLat())
                .longitude(requestDto.getLng())
                .build();

        userRepository.save(updateUser);
        return "위치를 성공적으로 변경하였습니다!";
    }


    /**
     * 유저 위치 조회
     *
     * @return 유저 좌표값 반환
     */
    public UserGetLocationResponseDto getUserLocation () {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("해당 유저를 찾지 못했습니다."));

        return UserGetLocationResponseDto.builder()
                .lat(user.getLatitude())
                .lng(user.getLongitude())
                .build();
    }
}

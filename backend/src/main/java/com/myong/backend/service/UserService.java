package com.myong.backend.service;

import com.myong.backend.api.KakaoMapApi;
import com.myong.backend.domain.dto.user.common.DesignerCommonDto;
import com.myong.backend.domain.dto.user.common.ReviewCommonDto;
import com.myong.backend.domain.dto.user.ShopDetailsResponseDto;
import com.myong.backend.domain.dto.user.UserHairShopPageResponseDto;
import com.myong.backend.domain.dto.user.UserHomePageResponseDto;
import com.myong.backend.domain.dto.user.common.ShopCommonDto;
import com.myong.backend.domain.dto.user.UserSignUpDto;
import com.myong.backend.domain.entity.Advertisement;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.user.Coupon;
import com.myong.backend.domain.entity.user.DiscountType;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.domain.entity.usershop.Review;
import com.myong.backend.jwttoken.JwtService;
import com.myong.backend.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
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
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;
    private final DesignerRepository designerRepository;

    public ResponseEntity<String> SingUp(UserSignUpDto userSignUpDto){

        Optional<User> ouser =  userRepository.findByEmail(userSignUpDto.getEmail());

        // 이메일 중복 체크
        if (checkEmailDuplication(userSignUpDto.getEmail())) {
            throw new NoSuchElementException("이미 존재하는 이메일입니다.");
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

            userRepository.save(user);
            return ResponseEntity.ok("회원 가입에 성공하셨습니다.");
        }
        return ResponseEntity.status(400).body("회원가입에 실패하셨습니다.");

    }

    public ResponseEntity<String> Signout(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION);

        // 헤더가 없거나 Bearer 토큰이 아닌 경우 다음 필터로 전달
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(400).body("잘못된 토큰입니다.");
        }

        try {
            // "Bearer " 이후의 실제 토큰 값 추출
            String token = authorization.split(" ")[1];

            String userName = jwtService.getUserName(token);

            if (redisTemplate.hasKey(userName)) {
                redisTemplate.delete(userName);
            }

            SecurityContextHolder.clearContext();

        } catch (Exception e) {
            return ResponseEntity.status(400).body("로그아웃 요청 중 오류가 발생했습니다.");
        }

        return ResponseEntity.ok("로그아웃에 성공하셨습니다");

    }
//
    public Boolean checkEmailDuplication(String email) {
        return userRepository.existsByEmail(email);
    }


    //유저 홈페이지 로딩

    public UserHairShopPageResponseDto loadHairShopPage(String email){
        Optional<User> findUser = userRepository.findByEmail(email);
        if(!findUser.isPresent()){
            throw new NoSuchElementException("해당 유저가 존재하지않습니다");
        }

        User user = findUser.get();

        // 2km 반경 디비에서 조회
        List<Shop> shopsIn2km =  shopRepository.findShopWithinRadius(user.getLongitude(), user.getLatitude());


        // 2km 반경 헤어샵이 없으면 "시" 기준 디비에서 조회
        if(shopsIn2km.size() < 1){

            // ex("대구광역시") 로 시작하는 가게 조회
            String location = user.getLocation().split(" ")[0];
            List<Shop> shopsInLocation = shopRepository.findShopWithAddress(location);

            Collections.sort(shopsInLocation,(shop1,shop2) -> Double.compare(shop2.getRating(),shop1.getRating()));

            List<ShopCommonDto> shopCommonDto =
                    shopsInLocation.stream().map(shop -> new ShopCommonDto(
                            shop.getName(),
                            shop.getEmail(),
                            shop.getTel(),
                            shop.getDesc(),
                            shop.getRating(),
                            shop.getReviewCount(),
                            shop.getOpenTime(),
                            shop.getCloseTime(),
                            shop.getAddress(),
                            shop.getPost(),
                            shop.getLongitude(),
                            shop.getLatitude()
                    )).collect(Collectors.toList());

            return new UserHairShopPageResponseDto(
                    user.getLocation(),
                    shopCommonDto
            );

        }

        // 2km 반경 평점순 정렬
        Collections.sort(shopsIn2km,(shop1, shop2) -> Double.compare(shop2.getRating(),shop1.getRating()));

        List<ShopCommonDto> shopCommonDto =
                shopsIn2km.stream().map(shop -> new ShopCommonDto(
                        shop.getName(),
                        shop.getEmail(),
                        shop.getTel(),
                        shop.getDesc(),
                        shop.getRating(),
                        shop.getReviewCount(),
                        shop.getOpenTime(),
                        shop.getCloseTime(),
                        shop.getAddress(),
                        shop.getPost(),
                        shop.getLongitude(),
                        shop.getLatitude()
                )).collect(Collectors.toList());

        return new UserHairShopPageResponseDto(
                user.getLocation(),
                shopCommonDto
        );

    }


    // 유저 홈페이지 로딩
    public UserHomePageResponseDto loadHomePage(String email){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다."));

        List<Shop> popularShops = shopRepository.findTop10ByOrderByLikeDesc();

        List<Advertisement> adList = advertisementRepository.findAll();

        List<ShopCommonDto> shopCommonDto =
                popularShops.stream().map(shop -> new ShopCommonDto(
                        shop.getName(),
                        shop.getEmail(),
                        shop.getTel(),
                        shop.getDesc(),
                        shop.getRating(),
                        shop.getReviewCount(),
                        shop.getOpenTime(),
                        shop.getCloseTime(),
                        shop.getAddress(),
                        shop.getPost(),
                        shop.getLongitude(),
                        shop.getLatitude()
                )).collect(Collectors.toList());

        return new UserHomePageResponseDto(
                user.getLocation(),
                shopCommonDto,
                adList
        );
    }

    public ShopDetailsResponseDto loadHairShopDetailsPage (String email){
        Shop shop = shopRepository.findByEmail(email).orElseThrow(() -> new NoSuchElementException("해당 가게를 찾을 수 없습니다"));

        //가게 데이터 정리
        ShopCommonDto shopCommonDto = new ShopCommonDto(
                shop.getName(),
                shop.getEmail(),
                shop.getTel(),
                shop.getDesc(),
                shop.getRating(),
                shop.getReviewCount(),
                shop.getOpenTime(),
                shop.getCloseTime(),
                shop.getAddress(),
                shop.getPost(),
                shop.getLongitude(),
                shop.getLatitude()
        );

        // 디자이너 데이터 정리
        List<Designer> designers = shop.getDesigners();
        List<DesignerCommonDto> designerListDtos =
                designers.stream().map(designer -> new DesignerCommonDto(
                    designer.getName(),
                    designer.getDesc(),
                    designer.getLike(),
                    designer.getRating()
                )).collect(Collectors.toList());


        // 리뷰 데이터 정리
        List<Review> reviews = shop.getReviews();
        List<ReviewCommonDto> reviewListDtos =
                reviews.stream().map(review -> new ReviewCommonDto(
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
                shopCommonDto,
                designerListDtos,
                reviewListDtos,
                highestPriceCoupon,
                reviewImageUrls
        );
    }



}

package com.myong.backend.service;

import com.myong.backend.api.KakaoMapApi;
import com.myong.backend.domain.dto.shop.ShopRegisterReviewRequestDto;
import com.myong.backend.domain.dto.user.UserHomePageResponseDto;
import com.myong.backend.domain.dto.user.UserHomePageShopListDto;
import com.myong.backend.domain.dto.user.UserSignUpDto;
import com.myong.backend.domain.entity.Advertisement;
import com.myong.backend.domain.entity.Gender;
import com.myong.backend.domain.entity.business.Reservation;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.domain.entity.usershop.Review;
import com.myong.backend.jwttoken.JwtService;
import com.myong.backend.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.swing.text.html.Option;
import java.util.*;
import java.util.Optional;
import java.util.stream.Collector;
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
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
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

    public ResponseEntity<String> Signout(HttpServletRequest request){
        String authorization = request.getHeader(AUTHORIZATION);

        // 헤더가 없거나 Bearer 토큰이 아닌 경우 다음 필터로 전달
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.status(400).body("잘못된 토큰입니다.");
        }

        try{
            // "Bearer " 이후의 실제 토큰 값 추출
            String token = authorization.split(" ")[1];

            String userName = jwtService.getUserName(token);

            if(redisTemplate.hasKey(userName)){
                redisTemplate.delete(userName);
            }

            SecurityContextHolder.clearContext();

        } catch (Exception e){
            return ResponseEntity.status(400).body("로그아웃 요청 중 오류가 발생했습니다.");
        }

        return ResponseEntity.ok("로그아웃에 성공하셨습니다");

    }

    public Boolean checkEmailDuplication(String email) {
        return userRepository.existsByEmail(email);
    }


    //유저 홈페이지 로딩
    public UserHomePageResponseDto LoadHomePage(String email){
        Optional<User> findUser = userRepository.findByEmail(email);
        if(!findUser.isPresent()){
            throw new NoSuchElementException("해당 유저가 존재하지않습니다");
        }

        User user = findUser.get();
        //광고 가져오는거
        List<Advertisement> adList =  advertisementRepository.findAll();

        // 2km 반경 디비에서 조회
        List<Shop> shopsIn2km =  shopRepository.findShopWithinRadius(user.getLongitude(), user.getLatitude());


        // 2km 반경 헤어샵이 없으면 "시" 기준 디비에서 조회
        if(shopsIn2km.size() < 1){

            // ex("대구광역시") 로 시작하는 가게 조회
            String location = user.getLocation().split(" ")[0];
            List<Shop> shopsInLocation = shopRepository.findShopWithAddress(location);

            Collections.sort(shopsInLocation,(shop1,shop2) -> Double.compare(shop2.getRating(),shop1.getRating()));

            List<UserHomePageShopListDto> shopListDto =
                    shopsInLocation.stream().map(shop -> new UserHomePageShopListDto(
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
                    shopListDto,
                    adList
            );

        }

        // 2km 반경 평점순 정렬
        Collections.sort(shopsIn2km,(shop1, shop2) -> Double.compare(shop2.getRating(),shop1.getRating()));

        List<UserHomePageShopListDto> shopListDto =
                shopsIn2km.stream().map(shop -> new UserHomePageShopListDto(
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
                shopListDto,
                adList
        );

    }



}

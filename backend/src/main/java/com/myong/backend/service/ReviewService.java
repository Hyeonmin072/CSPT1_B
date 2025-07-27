package com.myong.backend.service;

import com.myong.backend.domain.dto.review.ReviewRemoveRequestDto;
import com.myong.backend.domain.dto.shop.ShopRegisterReviewRequestDto;
import com.myong.backend.domain.dto.user.response.UserReviewPageResponseDto;
import com.myong.backend.domain.entity.business.Reservation;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.domain.entity.usershop.Review;
import com.myong.backend.exception.ResourceNotFoundException;
import com.myong.backend.jwttoken.dto.UserDetailsDto;
import com.myong.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final DesignerRepository designerRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;
    private final RedisTemplate<String,Object> redisTemplate;
    private final FileUploadService fileUploadService;


    /*
    *  리뷰 생성
    */
    @Transactional
    public String registerReview(ShopRegisterReviewRequestDto request, MultipartFile reviewImg){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Reservation reservation = reservationRepository.findById(request.getReservationId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Shop shop = reservation.getShop();
        Designer designer = reservation.getDesigner();
        String url = "";
        if(reviewImg != null){
            String route = "shop" + "/" + shop.getEmail() + "/" + "review" + "/" + request.getReservationId() + "/";
            url = fileUploadService.uploadFile(reviewImg,route);
        }

        Review review = new Review(
                request.getReviewContent(),
                request.getReviewRating(),
                url.equals("") ? "" : url,
                reservation,
                shop,
                designer,
                user
        );

        reviewRepository.save(review);
        updateGlobalAverageRating();

        //리뷰 등록시 해당 가게 평점 등록
        double totalRating = shop.getTotalRating()+request.getReviewRating();   // 토탈 평점 점수
        int reviewCount = shop.getReviewCount()+1;                              // 리뷰 카운터
        double shopRating = getReviewRating(totalRating,reviewCount);           // 저장 될 평점
        shop.updateRating(shopRating,totalRating,reviewCount);
        shop.updateScore(calculateBayesianAvg(shopRating,reviewCount));

        totalRating = designer.getTotalRating()+request.getReviewRating();
        reviewCount = designer.getReviewCount()+1;
        double desingerRating = getReviewRating(totalRating,reviewCount);
        designer.updateRating(desingerRating,totalRating,reviewCount);
        designer.updateScore(calculateBayesianAvg(desingerRating,reviewCount));

        return "리뷰가 성공적으로 등록되었습니다.";
    }



    @Transactional
    public ResponseEntity<String> reviewRemove(ReviewRemoveRequestDto request){

        Review review = reviewRepository.findById(UUID.fromString(request.getReviewId())).orElseThrow(() -> new ResourceNotFoundException("해당 리뷰가 존재하지 않습니다."));

        Shop shop = review.getShop();
        Designer designer = review.getDesigner();

        updateGlobalAverageRating();
        // 가게 평점 업데이트
        double totalRating = shop.getTotalRating()-review.getRating();
        int reviewCount = shop.getReviewCount()-1;
        double shopRating = getReviewRating(totalRating,reviewCount);
        shop.updateRating(shopRating,totalRating,reviewCount);
        shop.updateScore(calculateBayesianAvg(shopRating,reviewCount));

        // 디자이너 평점 업데이트
        totalRating = designer.getTotalRating()-review.getRating();
        reviewCount = designer.getReviewCount()-1;
        double designerRating = getReviewRating(totalRating,reviewCount);
        designer.updateRating(designerRating,totalRating,reviewCount);
        designer.updateScore(calculateBayesianAvg(designerRating,reviewCount));

        reviewRepository.deleteById(UUID.fromString(request.getReviewId()));

        return ResponseEntity.ok("리뷰가 성공적으로 삭제되었습니다.");
    }

    /**
     * 리뷰 페이지 로딩,
     * 최근 5개만
     * @param requestUser
     * @return 리뷰평점,리뷰내용,리뷰이미지,가게이메일,가게이름,디자이너이름,디자이너이메일
     */
    public List<UserReviewPageResponseDto> getReviewPage(UserDetailsDto requestUser){
        User user = userRepository.findByEmail(requestUser.getUsername()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"해당 유저를 찾을 수 없습니다."));
        return reviewRepository.findByUserOrderByCreateDateDesc(user,PageRequest.of(0,5));
    }

    public double getReviewRating(double totalRating, int count){;
        return totalRating/count;
    }
    // 베이지안 평균 값 적용을 위한 가게 총 평점 평균 레이팅 계산
    @Scheduled(cron = "0 0 3 * * ?") // 매일 새벽 3시
    public void updateGlobalAverageRating() {
        if(redisTemplate.hasKey("global_avg_rating")){return;}
        Double average = shopRepository.calculateAvgRating();
        System.out.println("총 가게 평점 평균 점수:"+average);
        redisTemplate.opsForValue().set("global_avg_rating", average);
    }

    // 베이지안 평균 계산
    public double calculateBayesianAvg(double rating, int reviewCount){
        Double avg = (Double) redisTemplate.opsForValue().get("global_avg_rating");
        double c = avg != null ? avg : 3.5;
        double v = reviewCount;
        return (v / (v + 30)) * rating + (30 / (v + 30)) * c;

    }


}

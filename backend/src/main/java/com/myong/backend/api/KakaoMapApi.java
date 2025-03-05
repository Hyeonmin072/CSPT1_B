package com.myong.backend.api;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.util.HashMap;


public class KakaoMapApi {
    @Value("${kakao.api-key}")
    private String apikey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public HashMap<String, String> getCoordinatesFromAddress(String addr){
        HashMap<String,String> result = new HashMap<>();
        try{

            //UTF인코딩은 만약을위한 방지
            String encodedAddress = URLEncoder.encode(addr,"UTF-8");

            String apiUrl = "https://dapi.kakao.com/v2/local/search/address.json?query="+encodedAddress;

            // 인증키 헤더 생성
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization","KakaoAK "+apikey);
            HttpEntity<String> entity = new HttpEntity<>(headers);


            // 실제로 get요청으로 요청
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET,entity,String.class);


            // 응답 본문을 JSON으로 변환
            String responseBody = responseEntity.getBody();
            JsonNode rootNode = objectMapper.readTree(responseBody); // Jackson을 사용하여 JSON 파싱
            JsonNode documents = rootNode.path("documents"); // "documents" 배열 추출

            // 첫 번째 결과에서 위도(y)와 경도(x) 추출
            if (documents.isArray() && documents.size() > 0) {
                JsonNode location = documents.get(0);
                result.put("y",location.path("y").asText()); // 위도
                result.put("x",location.path("x").asText()); // 경도
                return result;
            }else{
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}

package com.myong.backend.service;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.myong.backend.domain.dto.user.data.ShopListData;
import com.myong.backend.domain.entity.business.Reservation;
import com.myong.backend.domain.entity.business.ReservationDocument;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.designer.DesignerDocument;
import com.myong.backend.domain.entity.shop.Shop;
import com.myong.backend.domain.entity.shop.ShopDocument;
import com.myong.backend.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticsearchClient elasticsearchClient;
    private final ShopRepository shopRepository;


    /**
     * 예약 도큐멘트 세이브
     */
    public void reservationSave(Reservation reservation){
        elasticsearchOperations.save(ReservationDocument.from(reservation));
    }

    /**
     * 예약 도큐멘트 삭제
     */
    public void reservationDelete(UUID reservationId){
        try{
            DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest.Builder()
                    .index("designer")
                    .query(Query.of(q ->
                            q.term(TermQuery.of(t -> t
                                    .field("_id")
                                    .value(reservationId.toString())
                            ))
                    )).build();

            // 삭제 요청
            DeleteByQueryResponse deleteByQueryResponse = elasticsearchClient.deleteByQuery(deleteByQueryRequest);

            // 삭제 응답 처리
            System.out.println(deleteByQueryResponse);
        } catch (ElasticsearchException e){
            System.err.println("ElasticsearchException 발생: " + e.getMessage());
        } catch (IOException e){
            System.err.println("IOException 발생: " + e.getMessage());
        }

    }


    /**
     * 디자이너 도큐멘트 세이브
     */
    public void designerSave(Designer designer){
        elasticsearchOperations.save(DesignerDocument.from(designer));
    }

    /**
     * 디자이너 도큐멘트 삭제
     */
    public void designerDelete(UUID designerId) {
        try {
            DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest.Builder()
                    .index("designer")
                    .query(Query.of(q -> q
                            .term(TermQuery.of(t -> t
                                    .field("_id")
                                    .value(designerId.toString())
                            ))
                    ))
                    .build();

            // 삭제 요청 실행
            DeleteByQueryResponse deleteByQueryResponse = elasticsearchClient.deleteByQuery(deleteByQueryRequest);

            // 삭제 응답 처리
            System.out.println(deleteByQueryResponse);

        } catch (ElasticsearchException e) {
            System.err.println("ElasticsearchException 발생: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("IOException 발생: " + e.getMessage());
        }
    }


    /**
     * 가게 도큐멘트 세이브
     */
    public void shopSave(Shop shop){
        elasticsearchOperations.save(ShopDocument.from(shop));
    }

    /**
     *  마이그레이션용
     */
    public void migrateAllShopsToElasticsearch() {

        List<Shop> shopList = shopRepository.findAll();

        for (Shop shop : shopList) {
            ShopDocument doc = ShopDocument.from(shop);
            elasticsearchOperations.save(doc);
        }
    }

    /**
     * 가게 도큐멘트 삭제
     */
    public void ShopDelete(UUID shopId) throws IOException, ElasticsearchException {
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest.Builder()
                .index("shop")
                .query(Query.of(q -> q
                        .term(TermQuery.of(t -> t
                                .field("_id")
                                .value(shopId.toString())
                        ))
                ))
                .build();
        // 삭제 요청 실행
        DeleteByQueryResponse deleteByQueryResponse = elasticsearchClient.deleteByQuery(deleteByQueryRequest);

        // 삭제 응답 처리 (필요 시 응답 확인)
        System.out.println(deleteByQueryResponse);
    }


    /**
     * 가게 검색
     * @param searchText
     * @Return shopListData 가게 정보
     */
    public List<ShopListData> searchHairShops(String searchText){
        try{
            System.out.println("검색어:"+searchText);
            // 검색 진행  (shop 인덱스를 가진 도큐먼트)
            // 검색 범위 (이름,주소)
            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("shop")
                    .query(Query.of(q -> q
                            .multiMatch(m -> m
                                    .fields("name","address")
                                    .query(searchText)
                            )
                    ))
                    .build();
            SearchResponse<ShopDocument> searchResponse = elasticsearchClient.search(searchRequest, ShopDocument.class);

            System.out.println("총 hit 개수: " + searchResponse.hits().total().value());
            System.out.println("hit list size: " + searchResponse.hits().hits().size());

            // 검색된 결과 리스트 반환
            //source == ShopDocument
            return searchResponse.hits().hits().stream()
                    .map(Hit::source)
                    .map(ShopListData::searchFrom)
                    .collect(Collectors.toList());

        }catch(ElasticsearchException e){
            log.error("Elasticsearch 검색 중 오류 발생", e);
            throw new RuntimeException("검색 중 문제가 발생했습니다.");
        }catch (IOException e){
            log.error("Elasticsearch 검색 중 오류 발생", e);
            throw new RuntimeException("검색 중 문제가 발생했습니다.");
        }

    }
}

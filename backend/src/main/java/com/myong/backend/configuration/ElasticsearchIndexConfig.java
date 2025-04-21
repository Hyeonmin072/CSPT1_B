//package com.myong.backend.configuration;
//
//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
//import co.elastic.clients.elasticsearch.indices.GetIndexRequest;
//
//import org.reflections.Reflections;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.elasticsearch.annotations.Document;
//
//import java.util.Set;
//
//@Configuration
//public class ElasticsearchIndexConfig {
//
//    @Bean
//    public CommandLineRunner createIndices(ElasticsearchClient client) {
//        return args -> {
//            // Reflections 사용하여 @Document 애너테이션이 붙은 클래스 찾기
//            Reflections reflections = new Reflections("com.myong.backend.domain"); // 정확한 경로로 수정
//
//            Set<Class<?>> documentClasses = reflections.getTypesAnnotatedWith(Document.class);
//
//            for (Class<?> docClass : documentClasses) {
//                Document document = docClass.getAnnotation(Document.class);
//                String indexName = document.indexName();
//
//                boolean exists = client.indices().exists(e -> e.index(indexName)).value();
//                if (!exists) {
//                    client.indices().create(c -> c.index(indexName));
//                    System.out.println("✅ 인덱스 생성됨: " + indexName);
//                } else {
//                    System.out.println("ℹ️ 인덱스 이미 존재함: " + indexName);
//                }
//            }
//        };
//    }
//}
//
//

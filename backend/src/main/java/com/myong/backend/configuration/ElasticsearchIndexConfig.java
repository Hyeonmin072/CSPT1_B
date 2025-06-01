package com.myong.backend.configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import org.reflections.Reflections;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Set;

@Configuration
public class ElasticsearchIndexConfig {

    @Bean
    public CommandLineRunner createIndices(ElasticsearchClient client) {
        return args -> {
            // 도메인 경로에 있는 @Document 클래스 자동 탐색
            Reflections reflections = new Reflections("com.myong.backend.domain");
            Set<Class<?>> documentClasses = reflections.getTypesAnnotatedWith(Document.class);

            for (Class<?> docClass : documentClasses) {
                Document document = docClass.getAnnotation(Document.class);
                String indexName = document.indexName();

                boolean exists = client.indices().exists(e -> e.index(indexName)).value();
                if (!exists) {
                    client.indices().create(c -> c
                            .index(indexName)
                            .settings(s -> s
                                    .analysis(a -> a
                                            .analyzer("korean_analyzer", an -> an
                                                    .custom(ca -> ca
                                                    .tokenizer("nori_tokenizer"))
                                            )
                                    )
                            )
                            .mappings(m -> m
                                    .properties("name", p -> p
                                            .text(t -> t.analyzer("korean_analyzer"))
                                    )
                                    .properties("address", p -> p
                                            .text(t -> t.analyzer("korean_analyzer"))
                                    )
                                    .properties("desc", p -> p
                                            .text(t -> t.analyzer("korean_analyzer"))
                                    )
                            )
                    );
                    System.out.println("✅ 인덱스 생성됨: " + indexName);
                } else {
                    System.out.println("ℹ️ 인덱스 이미 존재함: " + indexName);
                }
            }
        };
    }
}

package com.myong.backend.controller;

import com.myong.backend.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/migrate")
public class MigrationController {
    private final SearchService searchService;

    @PostMapping("/shops")
    public ResponseEntity<String> migrateShops(){
        searchService.migrateAllShopsToElasticsearch();
        return ResponseEntity.ok("마이그레이션이 성공적으로 됐습니다.");
    }
}

package com.mimawiki.api.controller;

import com.mimawiki.api.dto.req.ArticleReq;
import com.mimawiki.api.dto.res.ArticleRes;
import com.mimawiki.api.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mima.wiki/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/write")
    public ResponseEntity<ArticleRes> createArticle(
            @RequestParam Long memberId,
            @RequestBody ArticleReq dto
    ) {
        return ResponseEntity.ok(articleService.createArticle(memberId, dto));
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<ArticleRes> editArticle(
            @PathVariable Long id,
            @RequestBody ArticleReq dto
    ) {
        return ResponseEntity.ok(articleService.editArticle(id, dto));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<String> toggleLike(
            @PathVariable Long id,
            @RequestParam Long memberId
    ) {
        boolean liked = articleService.toggleLike(id, memberId);
        return ResponseEntity.ok(liked ? "liked" : "unliked");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleRes> getArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getArticle(id));
    }
}


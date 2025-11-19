package com.mimawiki.api.repository;

import com.mimawiki.api.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    // ✅ 추가: keyword로 조회
    Optional<Article> findByKeyword(String keyword);

    // 키워드 검색 (LIKE)
    Page<Article> findByKeywordContaining(String keyword, Pageable pageable);

    // 인기 차트용 (좋아요 많은 순)
    List<Article> findTop10ByOrderByLikeCountDesc();
}
package com.mimawiki.api.repository;

import com.mimawiki.api.entity.Article;
import com.mimawiki.api.entity.Member;
import com.mimawiki.api.entity.Suggestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {

    // 내 게시글에 달린 제안 목록 조회 (최신순)
    @Query("SELECT s FROM Suggestion s WHERE s.article.author = :author ORDER BY s.regTime DESC")
    Page<Suggestion> findSuggestionsOnMyArticles(
            @Param("author") Member author,
            Pageable pageable
    );

    // 내 게시글에 달린 읽지 않은 제안 개수
    @Query("SELECT COUNT(s) FROM Suggestion s WHERE s.article.author = :author AND s.isRead = false")
    long countUnreadSuggestionsOnMyArticles(@Param("author") Member author);

    // 특정 게시글에 달린 제안 목록
    Page<Suggestion> findByArticleOrderByRegTimeDesc(Article article, Pageable pageable);

    // 특정 게시글의 제안 개수
    long countByArticle(Article article);
}
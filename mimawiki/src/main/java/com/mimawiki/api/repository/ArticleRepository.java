package com.mimawiki.api.repository;

import com.mimawiki.api.entity.Article;
import com.mimawiki.api.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Optional<Article> findByKeyword(String keyword);

    // 인기글 조회
    List<Article> findTop10ByOrderByLikeCountDesc();

    // 태그 클릭 시 검색
    Page<Article> findByTags_Name(String tagName, Pageable pageable);

    // 통합 검색
    @Query("SELECT DISTINCT a FROM Article a " +
            "LEFT JOIN a.tags t " +
            "WHERE a.keyword LIKE %:searchWord% " +
            "OR a.content LIKE %:searchWord% " +
            "OR t.name LIKE %:searchWord%")
    Page<Article> searchByComplex(@Param("searchWord") String searchWord, Pageable pageable);

    // ✅ [추가] 특정 회원이 작성한 글 개수
    long countByAuthor(Member author);

    // ✅ [추가] 특정 회원이 작성한 글 목록 (페이징)
    Page<Article> findByAuthor(Member author, Pageable pageable);


}
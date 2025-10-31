package com.mimawiki.api.repository;

import com.mimawiki.api.entity.Article;
import com.mimawiki.api.entity.ArticleLike;
import com.mimawiki.api.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    boolean existsByArticleAndMember(Article article, Member member);

    Optional<ArticleLike> findByArticleAndMember(Article article, Member member);

    int countByArticle(Article article);
}

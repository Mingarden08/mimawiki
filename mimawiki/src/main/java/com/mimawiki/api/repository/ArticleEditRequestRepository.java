package com.mimawiki.api.repository;

import com.mimawiki.api.entity.Article;
import com.mimawiki.api.entity.ArticleEditRequest;
import com.mimawiki.api.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArticleEditRequestRepository extends JpaRepository<ArticleEditRequest, Long> {

    List<ArticleEditRequest> findByArticleAndStatus(Article article, ArticleEditRequest.RequestStatus status);

    Page<ArticleEditRequest> findByArticle_AuthorAndStatus(Member author, ArticleEditRequest.RequestStatus status, Pageable pageable);

    Page<ArticleEditRequest> findByRequester(Member requester, Pageable pageable);

    Page<ArticleEditRequest> findByArticle(Article article, Pageable pageable);
}
package com.mimawiki.api.service;

import com.mimawiki.api.dto.req.ArticleEditRequestReq;
import com.mimawiki.api.dto.req.ReviewEditRequestReq;
import com.mimawiki.api.dto.res.ArticleEditRequestRes;
import com.mimawiki.api.entity.Article;
import com.mimawiki.api.entity.ArticleEditRequest;
import com.mimawiki.api.entity.Member;
import com.mimawiki.api.repository.ArticleEditRequestRepository;
import com.mimawiki.api.repository.ArticleRepository;
import com.mimawiki.api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleEditRequestService {

    private final ArticleEditRequestRepository editRequestRepository;
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;

    /**
     * 수정 요청 생성
     */
    @Transactional
    public ArticleEditRequestRes createEditRequest(String keyword, Long requesterId, ArticleEditRequestReq dto) {
        Article article = articleRepository.findByKeyword(keyword)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Member requester = memberRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        ArticleEditRequest editRequest = ArticleEditRequest.builder()
                .article(article)
                .requester(requester)
                .markdown(dto.getMarkdown())
                .content(dto.getContent())
                .requestComment(dto.getRequestComment())
                .status(ArticleEditRequest.RequestStatus.PENDING)
                .build();

        ArticleEditRequest saved = editRequestRepository.save(editRequest);
        return ArticleEditRequestRes.fromEntity(saved);
    }

    /**
     * 수정 요청 승인/거절 (작성자 또는 관리자만 가능)
     */
    @Transactional
    public ArticleEditRequestRes reviewEditRequest(Long requestId, Long reviewerId, ReviewEditRequestReq dto) {
        ArticleEditRequest editRequest = editRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("수정 요청을 찾을 수 없습니다."));

        Member reviewer = memberRepository.findById(reviewerId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (editRequest.getStatus() != ArticleEditRequest.RequestStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }

        Article article = editRequest.getArticle();
        boolean isAuthor = article.getAuthor().getId().equals(reviewerId);
        boolean isAdmin = reviewer.getRole() == Member.Role.ADMIN;

        if (!isAuthor && !isAdmin) {
            throw new IllegalStateException("글 작성자 또는 관리자만 수정 요청을 처리할 수 있습니다.");
        }

        if (dto.isApprove()) {
            editRequest.setStatus(ArticleEditRequest.RequestStatus.APPROVED);

            article.setMarkdown(editRequest.getMarkdown());
            article.setContent(editRequest.getContent());
            article.setModifiedBy(reviewer.getEmail());
        } else {
            editRequest.setStatus(ArticleEditRequest.RequestStatus.REJECTED);
        }

        editRequest.setReviewer(reviewer);
        editRequest.setReviewedAt(LocalDateTime.now());
        editRequest.setReviewComment(dto.getReviewComment());

        return ArticleEditRequestRes.fromEntity(editRequest);
    }

    /**
     * 내가 받은 수정 요청 조회 (작성자가 본인 글에 대한 수정 요청 조회)
     */
    public Page<ArticleEditRequestRes> getMyArticleEditRequests(Long authorId, Pageable pageable) {
        Member author = memberRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Page<ArticleEditRequest> requests = editRequestRepository
                .findByArticle_AuthorAndStatus(author, ArticleEditRequest.RequestStatus.PENDING, pageable);

        return requests.map(ArticleEditRequestRes::fromEntity);
    }

    /**
     * 내가 요청한 수정 요청 조회
     */
    public Page<ArticleEditRequestRes> getMyEditRequests(Long requesterId, Pageable pageable) {
        Member requester = memberRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Page<ArticleEditRequest> requests = editRequestRepository.findByRequester(requester, pageable);
        return requests.map(ArticleEditRequestRes::fromEntity);
    }

    /**
     * 특정 게시글의 모든 수정 요청 조회
     */
    public Page<ArticleEditRequestRes> getArticleEditRequests(String keyword, Pageable pageable) {
        Article article = articleRepository.findByKeyword(keyword)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Page<ArticleEditRequest> requests = editRequestRepository.findByArticle(article, pageable);
        return requests.map(ArticleEditRequestRes::fromEntity);
    }
}

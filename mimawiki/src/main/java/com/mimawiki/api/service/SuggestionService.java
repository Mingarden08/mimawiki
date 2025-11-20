package com.mimawiki.api.service;

import com.mimawiki.api.dto.req.SuggestionCreateReq;
import com.mimawiki.api.dto.res.SuggestionRes;
import com.mimawiki.api.entity.Member;
import com.mimawiki.api.entity.Article;
import com.mimawiki.api.entity.Suggestion;
import com.mimawiki.api.repository.ArticleRepository;
import com.mimawiki.api.repository.MemberRepository;
import com.mimawiki.api.repository.SuggestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SuggestionService {

    private final SuggestionRepository suggestionRepository;
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;

    /**
     * 게시글에 제안 작성
     */
    @Transactional
    public SuggestionRes createSuggestion(
            String keyword,
            SuggestionCreateReq request,
            String memberEmail
    ) {
        Article article = articleRepository.findByKeyword(keyword)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 자신의 게시글에는 제안할 수 없음
        if (article.getAuthor().getId().equals(member.getId())) {
            throw new IllegalArgumentException("자신의 게시글에는 제안할 수 없습니다.");
        }

        Suggestion suggestion = Suggestion.builder()
                .article(article)
                .member(member)
                .content(request.getContent())
                .build();

        Suggestion saved = suggestionRepository.save(suggestion);
        return SuggestionRes.from(saved);
    }

    /**
     * 내 게시글에 달린 제안 목록 조회
     */
    public Page<SuggestionRes> getReceivedSuggestions(
            String memberEmail,
            Pageable pageable
    ) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        return suggestionRepository.findSuggestionsOnMyArticles(member, pageable)
                .map(SuggestionRes::from);
    }

    /**
     * 내 게시글에 달린 읽지 않은 제안 개수
     */
    public long getUnreadSuggestionCount(String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        return suggestionRepository.countUnreadSuggestionsOnMyArticles(member);
    }

    /**
     * 제안 읽음 처리
     */
    @Transactional
    public SuggestionRes markAsRead(Long suggestionId, String memberEmail) {
        Suggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new IllegalArgumentException("제안을 찾을 수 없습니다."));

        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 게시글 작성자만 읽음 처리 가능
        if (!suggestion.getArticle().getAuthor().getId().equals(member.getId())) {
            throw new IllegalArgumentException("게시글 작성자만 읽음 처리할 수 있습니다.");
        }

        suggestion.markAsRead();
        return SuggestionRes.from(suggestion);
    }

    /**
     * 제안 삭제 (제안 작성자만 가능)
     */
    @Transactional
    public void deleteSuggestion(Long suggestionId, String memberEmail) {
        Suggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new IllegalArgumentException("제안을 찾을 수 없습니다."));

        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 제안 작성자만 삭제 가능
        if (!suggestion.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("제안 작성자만 삭제할 수 있습니다.");
        }

        suggestionRepository.delete(suggestion);
    }

    /**
     * 특정 게시글의 제안 목록 조회 (모든 사용자가 볼 수 있음)
     */
    public Page<SuggestionRes> getSuggestionsByArticle(
            String keyword,
            Pageable pageable
    ) {
        Article article = articleRepository.findByKeyword(keyword)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        return suggestionRepository.findByArticleOrderByRegTimeDesc(article, pageable)
                .map(SuggestionRes::from);
    }
}
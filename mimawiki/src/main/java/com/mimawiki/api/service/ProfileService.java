package com.mimawiki.api.service;

import com.mimawiki.api.dto.req.ProfileUpdateReq;
import com.mimawiki.api.dto.res.ArticleRes;
import com.mimawiki.api.dto.res.ProfileRes;
import com.mimawiki.api.entity.Article;
import com.mimawiki.api.entity.Member;
import com.mimawiki.api.repository.ArticleRepository;
import com.mimawiki.api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;

    /**
     * 프로필 조회 (이름 + 글 개수 + 글 목록)
     */
    public ProfileRes getProfile(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 1. 내가 쓴 글 개수 조회
        long count = articleRepository.countByAuthor(member);

        // 2. 내가 쓴 글 목록 조회 (페이징 처리)
        Page<Article> articlePage = articleRepository.findByAuthor(member, pageable);

        List<ArticleRes> articleResList = articlePage.stream()
                .map(ArticleRes::fromEntity)
                .collect(Collectors.toList());

        return ProfileRes.of(member, count, articleResList);
    }

    /**
     * 프로필 수정 (이름 변경)
     */
    @Transactional
    public void updateProfile(Long memberId, ProfileUpdateReq req) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 엔티티의 비즈니스 메서드 호출
        member.updateName(req.getName());
    }
}
package com.mimawiki.api.dto.res;

import com.mimawiki.api.entity.Member;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProfileRes {

    private Long memberId;
    private String email;
    private String name;            // 사용자 이름
    private long totalArticleCount; // 총 작성 글 수
    private List<ArticleRes> myArticles; // 작성 글 목록

    public static ProfileRes of(Member member, long count, List<ArticleRes> articles) {
        return ProfileRes.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .totalArticleCount(count)
                .myArticles(articles)
                .build();
    }
}
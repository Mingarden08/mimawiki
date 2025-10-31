package com.mimawiki.api.dto.res;

import com.mimawiki.api.entity.Article;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
public class ArticleRes {
    private Long id;
    private String keyword;
    private String markdown;
    private String content;
    private String authorName;
    private Integer viewCount;
    private Integer likeCount;
    private LocalDateTime regTime;
    private LocalDateTime updateTime;

    public static ArticleRes fromEntity(Article article) {
        return ArticleRes.builder()
                .id(article.getId())
                .keyword(article.getKeyword())
                .markdown(article.getMarkdown())
                .content(article.getContent())
                .authorName(article.getAuthor().getName())
                .viewCount(article.getViewCount())
                .likeCount(article.getLikes() != null ? article.getLikes().size() : 0)
                .regTime(article.getRegTime())
                .updateTime(article.getUpdateTime())
                .build();
    }
}

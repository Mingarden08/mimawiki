package com.mimawiki.api.dto.req;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleEditRequestReq {
    private String markdown;
    private String content;
    private String requestComment;
}
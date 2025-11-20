package com.mimawiki.api.dto.req;

import lombok.*;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleReq {
    private String keyword;
    private String markdown; // 사용자가 입력한 마크다운 (optional)
    private String content;  // 프론트가 변환한 HTML (optional) -> 서버에서 정제 후 저장
    private List<String> tags;
}

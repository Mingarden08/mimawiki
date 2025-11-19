package com.mimawiki.api.dto.req;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewEditRequestReq {
    private boolean approve; // true: 승인, false: 거절
    private String reviewComment;
}
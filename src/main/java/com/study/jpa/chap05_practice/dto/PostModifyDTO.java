package com.study.jpa.chap05_practice.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Setter @Getter
@ToString @EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostModifyDTO {

    private String title;

    private String content;

    @NotNull // 공백이나 빈 문자열이 들어올 수 없는 타입은 NotNull로 선언
    @Builder.Default
    private Long postNo = 0L; //Long타입은 빈 문자열 공백이 들어올수 없다
}

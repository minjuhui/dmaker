package com.fastcampus.programming.dmaker.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DMakerErrorCode {
    // 비즈니스 검증
    NO_DEVELOPER("해당되는 개발자 없음"),
    DUPLICATED_MEMEBER_ID("중복되는 MEMBERID"),
    LEVEL_EXPERIENCE_YEARS_NOT_MATCHED("개발자 레벨과 연차가 맞지 않습니다."),

    INTERNAL_SERVER_ERROR("서버에 오류가 발생했습니다."),
    INVALID_REQUEST("잘못된 요청")
    ;

    private final String message;
}

package com.fastcampus.programming.dmaker.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusCode {
    EMPLOYED("고용"),
    RETIRED("은퇴");

    private final String description;
}

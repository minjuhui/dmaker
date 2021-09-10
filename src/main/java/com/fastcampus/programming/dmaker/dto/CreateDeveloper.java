package com.fastcampus.programming.dmaker.dto;

import com.fastcampus.programming.dmaker.entity.Developer;
import com.fastcampus.programming.dmaker.type.DeveloperLevel;
import com.fastcampus.programming.dmaker.type.DeveloperSkillType;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateDeveloper {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class Request{
        // 데이터 검증
        @NotNull
        private DeveloperLevel developerLevel;

        @NotNull
        private DeveloperSkillType developerSkillType;

        @NotNull
        @Min(0)
        @Max(20)
        private Integer experienceYears;

        @NotNull
        @Size(min=3, max=50, message="memberId size must 3-50")
        private String memberId;

        @NotNull
        @Size(min=3, max=20, message="name size must 3-20")
        private String name;

        @Min(18)
        private Integer age;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response{
        private DeveloperLevel developerLevel;
        private DeveloperSkillType developerSkillType;
        private Integer experienceYears;
        private String memberId;

        // developer entity를 가져와서
        // 해당 내용들을 새로운 response 객체에 저장함
        public static Response fromEntity(Developer developer){
            return Response.builder()
            .developerLevel(developer.getDeveloperLevel())
            .developerSkillType(developer.getDeveloperSkillType())
            .experienceYears(developer.getExperienceYears())
            .memberId(developer.getMemberId())
            .build();
        }

    }
}

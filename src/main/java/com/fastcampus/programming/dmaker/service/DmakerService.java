package com.fastcampus.programming.dmaker.service;

import com.fastcampus.programming.dmaker.dto.CreateDeveloper;
import com.fastcampus.programming.dmaker.dto.DeveloperDetailDto;
import com.fastcampus.programming.dmaker.dto.DeveloperDto;
import com.fastcampus.programming.dmaker.dto.EditDeveloper;
import com.fastcampus.programming.dmaker.entity.Developer;
import com.fastcampus.programming.dmaker.entity.RetiredDeveloper;
import com.fastcampus.programming.dmaker.entity.StatusCode;
import com.fastcampus.programming.dmaker.exception.DMakerErrorCode;
import com.fastcampus.programming.dmaker.exception.DMakerException;
import com.fastcampus.programming.dmaker.repository.DeveloperRepository;
import com.fastcampus.programming.dmaker.repository.RetiredDeveloperRepository;
import com.fastcampus.programming.dmaker.type.DeveloperLevel;
import com.fastcampus.programming.dmaker.type.DeveloperSkillType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.fastcampus.programming.dmaker.exception.DMakerErrorCode.*;

@Service
@RequiredArgsConstructor
public class DmakerService {

    private final DeveloperRepository developerRepository;
    private final RetiredDeveloperRepository retiredDeveloperRepository;

    // ACID
    // Atomic : 실패하거나 성공하거나 둘중에 하나
    // Consistency : 일관성, 모든 데이터는 정해진 규칙에 맞춰 저장되어야 함,
    //               commit 지점에서는 일관정이 맞춰져야함
    // Isolation : 고립성
    // Durability : 지속성

    // Transactional은 국소적으로 사용
   @Transactional
    public CreateDeveloper.Response createdDeveloper(CreateDeveloper.Request request){
            validateCreateDeveloperRequest(request);

            //business logic start
            // request 요청을 저장함
            Developer developer = Developer.builder()
                    .developerLevel(request.getDeveloperLevel())
                    .developerSkillType(request.getDeveloperSkillType())
                    .experienceYears(request.getExperienceYears())
                    .memberId(request.getMemberId())
                    .statusCode(StatusCode.EMPLOYED)
                    .name(request.getName())
                    .age(request.getAge())
                    .build();

            // db에 저장
            developerRepository.save(developer);
            //business logic end

            return CreateDeveloper.Response.fromEntity(developer);


    }

    // buisness validation
    // 비즈니스에서의 예외적인 상황
    // ex. 시니어 개발자인데 연차가 10년이 안될때
    private void validateCreateDeveloperRequest(CreateDeveloper.Request request) {

//        DeveloperLevel developerLevel = request.getDeveloperLevel();
//        Integer experienceYears = request.getExperienceYears();
//
//        validateDeveloperLevel(developerLevel, experienceYears);
//
        validateDeveloperLevel(request.getDeveloperLevel(),request.getExperienceYears());


        // 중복된 memberid 검색
//        Optional<Developer> developer = developerRepository.findByMemberId(request.getMemberId());
//        if (developer.isPresent()){
//            throw new DMakerException(DUPLICATED_MEMEBER_ID);
//        }

        // java 8
        developerRepository.findByMemberId(request.getMemberId())
                .ifPresent((developer -> {
                    throw new DMakerException(DUPLICATED_MEMEBER_ID);
                }));

    }

    public List<DeveloperDto> getAllEmployedDevelopers() {
       return developerRepository.findDevelopersByStatusCodeEquals(StatusCode.EMPLOYED)
               .stream().map(DeveloperDto::fromEntity)
               .collect(Collectors.toList());
    }

    public DeveloperDetailDto getDeveloperDetail(String memberId) {
       return developerRepository.findByMemberId(memberId)
               .map(DeveloperDetailDto::fromEntity)
               .orElseThrow(() -> new DMakerException(NO_DEVELOPER));
       // 값이 없으면 exception, 값이 있으면 해당 값 리턴
    }

    @Transactional      // db에 반영시킴
    public DeveloperDetailDto editDeveloper(String memberId, EditDeveloper.Request request) {
       // 유효성 검증
       validateEditDeveloperRequest(request, memberId);

        // 수정을 위해서는 해당 멤버아이디로 develop가 있어야만 함
        Developer developer = developerRepository.findByMemberId(memberId).orElseThrow(
                () -> new DMakerException(NO_DEVELOPER)
        );

        developer.setDeveloperLevel(request.getDeveloperLevel());
        developer.setDeveloperSkillType(request.getDeveloperSkillType());
        developer.setExperienceYears(request.getExperienceYears());

        return DeveloperDetailDto.fromEntity(developer);

    }

    private void validateEditDeveloperRequest(EditDeveloper.Request request,
                                              String memberId) {
        validateDeveloperLevel(request.getDeveloperLevel(),request.getExperienceYears());


    }

    private void validateDeveloperLevel(DeveloperLevel developerLevel, Integer experienceYears) {
        if (developerLevel == DeveloperLevel.SENIOR
                && experienceYears < 10) {
            // static import
            throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
        }

        if (developerLevel == DeveloperLevel.JUNGNIOR &&
                (experienceYears < 4 || experienceYears > 10)) {
            throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
        }

        if (developerLevel == DeveloperLevel.JUNIOR && experienceYears > 4) {
            throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
        }
    }

    @Transactional
    public DeveloperDetailDto deleteDeveloper(String memberId) {
        // transactional : db 조작이 있으면 넣어주는게 좋음
        // atomic

        // 1. emplyed -> retired status code 변경
        // 유효성 검증
        Developer developer = developerRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DMakerException(NO_DEVELOPER));

        developer.setStatusCode(StatusCode.RETIRED);

        // 2. save into RetiredDeveloper
        RetiredDeveloper retiredDeveloper =RetiredDeveloper.builder()
                .memberId(developer.getMemberId())
                .name(developer.getName())
                .build();

        retiredDeveloperRepository.save(retiredDeveloper);

        return DeveloperDetailDto.fromEntity(developer);

    }
}

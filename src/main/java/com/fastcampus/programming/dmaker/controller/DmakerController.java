package com.fastcampus.programming.dmaker.controller;

import com.fastcampus.programming.dmaker.dto.CreateDeveloper;
import com.fastcampus.programming.dmaker.dto.DeveloperDetailDto;
import com.fastcampus.programming.dmaker.dto.DeveloperDto;
import com.fastcampus.programming.dmaker.dto.EditDeveloper;
import com.fastcampus.programming.dmaker.service.DmakerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 사용사의 입력이 처음으로 받아들여지는 위치
 */

// DmakerController를 RestController라는 타입의 빈(Component)으로 등록
// ResponseBody를 자동으로 달아줌
@RestController  // Controller + ResponseBody (return 할 때 json으로 응답)
@Slf4j
@RequiredArgsConstructor
public class DmakerController {

    private final DmakerService dmakerService;

    @GetMapping("/developers")
    public List<DeveloperDto> getAllDevelopers() {
        // dto를 만들어서 응답할 수 있도록
        // entity를 그대로 쓰면 불필요한 정보가 유출될수도 있고
        // transaction이 없는 상태로 정보를 접근하게 되면 문제가 발생할 수 있음

        return dmakerService.getAllEmployedDevelopers();
    }

    @GetMapping("/developers/{memberId}")
    public DeveloperDetailDto getDeveloperDetail(@PathVariable String memberId){

        return dmakerService.getDeveloperDetail(memberId);

    }

    // 새로운 리소스를 만든다 -> post
    @PostMapping("/create-developer")
    public CreateDeveloper.Response createDevelopers(
            // @Valid : dto에 달아둔 유효성 검증
            @Valid @RequestBody CreateDeveloper.Request request) {

        log.info("request : {}", request);

        return dmakerService.createdDeveloper(request);
    }


   // put 모두 path 해당데이터만 수정
    @PutMapping("/developer/{memberId}")
    public DeveloperDetailDto editDeveloper(
            @PathVariable String memberId,
            @Valid @RequestBody EditDeveloper.Request request){
        return dmakerService.editDeveloper(memberId, request);
    }

    @DeleteMapping("/developer/{memberId}")
        public DeveloperDetailDto deletDeveloper( @PathVariable String memberId){

            return dmakerService.deleteDeveloper(memberId);


    }


}

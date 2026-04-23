package com.example.sool.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sool.dto.CommonCodeDto;
import com.example.sool.dto.DrinkDto;
import com.example.sool.dto.DrinkSearchDto;
import com.example.sool.security.CustomUserDetails;
import com.example.sool.service.CommonCodeService;
import com.example.sool.service.DrinkService;

@RestController
@RequestMapping("/api/drinks")
public class DrinkController {

    private final DrinkService drinkService;
    private final CommonCodeService commonCodeService;

    public DrinkController(DrinkService drinkService, CommonCodeService commonCodeService) {
        this.drinkService = drinkService;
        this.commonCodeService = commonCodeService;
    }

    //주류 목록 조회
    @GetMapping("")
    public ResponseEntity<?> getDrinkList(DrinkSearchDto dto, Authentication authentication) {

        //로그인 사용자 확인
        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {

            //로그인 상태이면 userId 세팅
            dto.setUserId(userDetails.getUserId());
 
        } else {
            //비회원일 경우 null 처리
            dto.setUserId(null);
        }

        System.out.println("dto = " + dto);

        //서비스 호출
        Map<String, Object> result = drinkService.getDrinkList(dto);

        //응답 반환
        return ResponseEntity.ok(result);
    }

    //목록의 사이드바 카테고리
    @GetMapping("/categories")
    public List<CommonCodeDto> selectCategoryList() {

        return commonCodeService.selectCategoryList();
    }

    //주류 디테일
    @GetMapping("/{drinkId}")
    public DrinkDto getDrinkDetail(@PathVariable Integer drinkId){
        return drinkService.findByDrinkId(drinkId);
    }
}

package com.example.sool.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sool.dto.CommonCodeDto;
import com.example.sool.dto.DrinkDto;
import com.example.sool.dto.DrinkSearchDto;
import com.example.sool.service.CommonCodeService;
import com.example.sool.service.DrinkService;

@RestController
public class DrinkController {

    private final DrinkService drinkService;
    private final CommonCodeService commonCodeService;

    public DrinkController(DrinkService drinkService, CommonCodeService commonCodeService) {
        this.drinkService = drinkService;
        this.commonCodeService = commonCodeService;
    }

    @GetMapping("/api/drinks")
    public List<DrinkDto> getDrinkList(DrinkSearchDto dto){
        
        System.out.println("keyword = " + dto.getKeyword());
        return drinkService.searchDrinkList(dto);
    }

    @GetMapping("/api/drinks/categories")
    public List<CommonCodeDto> getDrinkCategoryList() {

        return commonCodeService.getDrinkCategoryList();
    }
}

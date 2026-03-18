package com.example.sool.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sool.dto.CommonCodeDto;
import com.example.sool.dto.DrinkDto;
import com.example.sool.dto.DrinkSearchDto;
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

    @GetMapping("")
    public Map<String, Object> getDrinkList(DrinkSearchDto dto){

        System.out.println("dto = " + dto);

        List<DrinkDto> list;
        int totalCount;

        if (dto.getKeyword() != null && !dto.getKeyword().trim().isEmpty()) {
            //검색어 있는 경우
            list = drinkService.searchDrinkList(dto);
            totalCount = drinkService.searchDrinkCount(dto);
        } else {
            //없는 경우 필터
            list = drinkService.getFilterList(dto);
            totalCount = drinkService.getFilterCount(dto);
        }

        //페이지수 올림 처리
        int totalPage = (int) Math.ceil((double) totalCount / dto.getSize());

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("totalCount", totalCount);
        result.put("page", dto.getPage());
        result.put("size", dto.getSize());
        result.put("totalPage", totalPage);

        return result;
    }

    @GetMapping("/categories")
    public List<CommonCodeDto> selectCategoryList() {

        return commonCodeService.selectCategoryList();
    }

    @GetMapping("/{drinkId}")
    public DrinkDto getDrinkDetail(@PathVariable Integer drinkId){
        return drinkService.findByDrinkId(drinkId);
    }
}

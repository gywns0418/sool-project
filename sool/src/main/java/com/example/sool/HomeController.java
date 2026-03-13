package com.example.sool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sool.dto.CommonCodeDto;
import com.example.sool.dto.DrinkDto;
import com.example.sool.service.CommonCodeService;
import com.example.sool.service.DrinkService;


@RestController
public class HomeController {

    private final DrinkService drinkService;
    private final CommonCodeService commonCodeService;

    public HomeController(DrinkService drinkService,CommonCodeService commonCodeService) {
        this.drinkService = drinkService;
        this.commonCodeService = commonCodeService;
    }

    @GetMapping("/api/home")
    public Map<String,Object> home() {

        Map<String,Object> map = new HashMap<>();

        List<DrinkDto> drinks = drinkService.drinkAll();
        List<CommonCodeDto> catelist = commonCodeService.selectCategoryList();

        map.put("drinks", drinks);
        map.put("catelist", catelist);
        
        return map;
    }
}
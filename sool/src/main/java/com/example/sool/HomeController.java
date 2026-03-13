package com.example.sool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sool.dto.DrinkDto;
import com.example.sool.service.DrinkService;


@RestController
public class HomeController {

    private final DrinkService drinkService;

    public HomeController(DrinkService drinkService) {
        this.drinkService = drinkService;
    }

    @GetMapping("/api/home")
    public Map<String,Object> home() {

        Map<String,Object> map = new HashMap<>();

        List<DrinkDto> drinks = drinkService.drinkAll();

        map.put("drinks", drinks);
        
        return map;
    }
}
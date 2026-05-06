package com.example.sool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sool.dto.CommonCodeDto;
import com.example.sool.dto.DrinkDto;
import com.example.sool.dto.TastingNoteDto;
import com.example.sool.service.CommonCodeService;
import com.example.sool.service.DrinkService;
import com.example.sool.service.TastingNoteService;


@RestController
public class HomeController {

    private final DrinkService drinkService;
    private final CommonCodeService commonCodeService;
    private final TastingNoteService tastingNoteService;

    public HomeController(DrinkService drinkService,CommonCodeService commonCodeService,
                            TastingNoteService tastingNoteService) {
        this.drinkService = drinkService;
        this.commonCodeService = commonCodeService;
        this.tastingNoteService = tastingNoteService;

    }

    @GetMapping("/api/home")
    public Map<String,Object> home() {
        // BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // String encoded = encoder.encode("111111");
        // System.out.println(encoded);

        Map<String,Object> map = new HashMap<>();

        List<CommonCodeDto> cateList = commonCodeService.selectCategoryList();
        List<DrinkDto> drinkTop = drinkService.drinkTop();
        List<TastingNoteDto> recentNote = tastingNoteService.recentNote();

        map.put("cateList", cateList);
        map.put("drinkTop", drinkTop);
        map.put("recentNote", recentNote);
        
        return map;
    }
}
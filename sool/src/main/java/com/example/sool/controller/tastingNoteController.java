package com.example.sool.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sool.dto.CommonCodeDto;
import com.example.sool.dto.DrinkDto;
import com.example.sool.dto.NoteSearchDto;
import com.example.sool.dto.TastingNoteDto;
import com.example.sool.service.DrinkService;
import com.example.sool.service.TastingNoteService;

@RestController
@RequestMapping("/api")
public class TastingNoteController {

    private final TastingNoteService tastingNoteService;
    private final DrinkService drinkService;

    public TastingNoteController(TastingNoteService tastingNoteService, DrinkService drinkService){
        this.tastingNoteService = tastingNoteService;
        this.drinkService = drinkService;
    }

    @GetMapping("/drinks/{drinkId}/notes")
    public Map<String, Object> findNoteAll(@PathVariable Integer drinkId, NoteSearchDto dto) {

        dto.setDrinkId(drinkId);

        List<TastingNoteDto> list = tastingNoteService.findNoteByDrinkId(dto);
        int totalCount = tastingNoteService.countNoteByDrinkId(dto);

        int totalPage = (int) Math.ceil((double) totalCount / dto.getSize());

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("totalCount", totalCount);
        result.put("page", dto.getPage());
        result.put("size", dto.getSize());
        result.put("totalPage", totalPage);

        return result;
    }

    @GetMapping("/notes/{noteId}")
    public ResponseEntity<?> getNoteDetail(@PathVariable("noteId") Integer noteId) {
        TastingNoteDto noteDetail = tastingNoteService.getNoteDetail(noteId);

        if (noteDetail == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("존재하지 않는 테이스팅 노트입니다.");
        }

        return ResponseEntity.ok(noteDetail);
    }


    @GetMapping("/notes/write/{drinkId}")
    public ResponseEntity<Map<String, Object>> getNoteWriteForm(@PathVariable int drinkId) {
        DrinkDto drink = drinkService.findByDrinkId(drinkId);

        if (drink == null) {
            throw new IllegalArgumentException("존재하지 않는 주류입니다.");
        }

        List<CommonCodeDto> metricList = tastingNoteService.getMetricCode(drinkId);

        Map<String, Object> result = new HashMap<>();
        result.put("drink", drink);
        result.put("metricList", metricList);

        return ResponseEntity.ok(result);
    }
}

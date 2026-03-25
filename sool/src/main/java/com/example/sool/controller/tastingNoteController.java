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

import com.example.sool.dto.NoteSearchDto;
import com.example.sool.dto.TastingNoteDto;
import com.example.sool.service.TastingNoteService;

@RestController
@RequestMapping("/api")
public class TastingNoteController {

    private final TastingNoteService tastingNoteService;

    public TastingNoteController(TastingNoteService tastingNoteService){
        this.tastingNoteService = tastingNoteService;
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


}

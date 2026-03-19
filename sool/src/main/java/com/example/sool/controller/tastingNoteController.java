package com.example.sool.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public List<TastingNoteDto> findNoteAll(
            @PathVariable Integer drinkId,
            @RequestParam(required = false) String sort
    ) {
        return tastingNoteService.findNoteAll(drinkId, sort);
    }
}

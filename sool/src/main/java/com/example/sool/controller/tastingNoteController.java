package com.example.sool.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sool.dto.CommonCodeDto;
import com.example.sool.dto.DrinkDto;
import com.example.sool.dto.ImageDto;
import com.example.sool.dto.NoteSearchDto;
import com.example.sool.dto.TastingNoteDto;
import com.example.sool.dto.TastingNoteMetricDto;
import com.example.sool.security.CustomUserDetails;
import com.example.sool.service.DrinkService;
import com.example.sool.service.ImageService;
import com.example.sool.service.TastingNoteService;

@RestController
@RequestMapping("/api")
public class TastingNoteController {

    private final TastingNoteService tastingNoteService;
    private final DrinkService drinkService;
    private final ImageService imageService;

    public TastingNoteController(TastingNoteService tastingNoteService, DrinkService drinkService,ImageService imageService){
        this.tastingNoteService = tastingNoteService;
        this.drinkService = drinkService;
        this.imageService = imageService;
    }

    //노트 목록
    @GetMapping("/drinks/{drinkId}/notes")
    public Map<String, Object> findNoteAll(@PathVariable Integer drinkId, NoteSearchDto dto) {

        dto.setDrinkId(drinkId);

        List<TastingNoteDto> list = tastingNoteService.findNoteByDrinkId(dto);
        int totalCount = tastingNoteService.countNoteByDrinkId(dto);

        int totalPage = (int) Math.ceil((double) totalCount / dto.getSize());

        List<TastingNoteMetricDto> avgMetric = tastingNoteService.findAvgMetricByDrinkId(drinkId);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("totalCount", totalCount);
        result.put("page", dto.getPage());
        result.put("size", dto.getSize());
        result.put("totalPage", totalPage);
        result.put("avgMetric",avgMetric);

        return result;
    }

    //노트 디테일
    @GetMapping("/notes/{noteId}")
    public ResponseEntity<?> getNoteDetail(@PathVariable("noteId") Integer noteId) {
        TastingNoteDto noteDetail = tastingNoteService.getNoteDetail(noteId);

        if (noteDetail == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("존재하지 않는 테이스팅 노트입니다.");
        }

        List<TastingNoteMetricDto> metriclist = tastingNoteService.findMetricByNoteId(noteId);
        noteDetail.setMetricList(metriclist);

        ImageDto image = imageService.selectImageByNoteId(noteId);
        noteDetail.setImage(image);

        return ResponseEntity.ok(noteDetail);
    }


    //노트 작성 정보
    @GetMapping("/notes/write/{drinkId}")
    public ResponseEntity<Map<String, Object>> getNoteWriteForm(@PathVariable int drinkId) {
        System.out.println(drinkId);

        //주류 정보
        DrinkDto drink = drinkService.findByDrinkId(drinkId);

        if (drink == null) {
            throw new IllegalArgumentException("존재하지 않는 주류입니다.");
        }

        //카테고리별 맛 프로파일 항목
        List<CommonCodeDto> metricList = tastingNoteService.getMetricCode(drinkId);

        Map<String, Object> result = new HashMap<>();
        result.put("drink", drink);
        result.put("metricList", metricList);

        return ResponseEntity.ok(result);
    }

    //노트 작성
    @PostMapping("/notes/writeform")
    public ResponseEntity<?> createNote(@RequestBody TastingNoteDto dto, Authentication authentication) {
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "로그인이 필요합니다."));
            }

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            dto.setUserId(userDetails.getUserId());

            Integer noteId = tastingNoteService.createNote(dto);

            Map<String, Object> result = new HashMap<>();
            result.put("noteId", noteId);
            result.put("message", "노트가 저장되었습니다.");

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    //노트 수정 기본 정보 
    @GetMapping("/notes/edit/{noteId}")
    public ResponseEntity<Map<String, Object>> getNoteUpdateForm(@PathVariable int noteId) {
        
        //노트수정 주류 기본 정보 
        DrinkDto drink = drinkService.findDrinkByNoteId(noteId);
        
        //노트 수정 노트 기본 정보
        TastingNoteDto note = tastingNoteService.findByNoteId(noteId);

        //저장된 카테고리별 맛 프로파일 항목
        List<TastingNoteMetricDto> metricList = tastingNoteService.findMetricByNoteId(noteId);

        ImageDto image = imageService.selectImageByNoteId(noteId);

        Map<String, Object> result = new HashMap<>();
        result.put("drink", drink);
        result.put("note", note);
        result.put("metricList", metricList);
        result.put("image",image);

        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/notes/edit/{noteId}")
    public ResponseEntity<?> updateNote(@PathVariable int noteId, 
                    @RequestBody TastingNoteDto dto, Authentication authentication) {
        
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        dto.setUserId(userDetails.getUserId());

        dto.setNoteId(noteId);
        tastingNoteService.updateNote(dto);

        return ResponseEntity.ok("수정 완료");
    }

    @PostMapping("/notes/delete/{noteId}")
    public ResponseEntity<?> deleteNote(@PathVariable int noteId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        try {
            tastingNoteService.deleteNote(noteId, userDetails.getUserId());
            return ResponseEntity.ok("노트가 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

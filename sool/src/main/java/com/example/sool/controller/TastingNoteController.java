package com.example.sool.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.sool.dto.CommonCodeDto;
import com.example.sool.dto.DrinkDto;
import com.example.sool.dto.ImageDto;
import com.example.sool.dto.NoteSearchDto;
import com.example.sool.dto.TastingNoteDto;
import com.example.sool.dto.TastingNoteMetricDto;
import com.example.sool.mapper.ImageMapper;
import com.example.sool.mapper.ReportMapper;
import com.example.sool.security.CustomUserDetails;
import com.example.sool.service.DrinkService;
import com.example.sool.service.TastingNoteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TastingNoteController {

    private final TastingNoteService tastingNoteService;
    private final DrinkService drinkService;
    private final ImageMapper imageMapper;
    private final ReportMapper reportMapper;

    //노트 목록
    @GetMapping("/drinks/{drinkId}/notes")
    public ResponseEntity<?> findNoteAll(@PathVariable Integer drinkId, NoteSearchDto dto, Authentication authentication) {

        Integer userId = null;

        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
            userId = user.getUserId();
        }

        return ResponseEntity.ok(
            tastingNoteService.getDrinkNoteList(drinkId, dto, userId)
        );
    }

    //노트 디테일
    @GetMapping("/notes/{noteId}")
    public ResponseEntity<?> getNoteDetail(@PathVariable("noteId") Integer noteId) {
        TastingNoteDto noteDetail = tastingNoteService.getNoteDetail(noteId);

        if (noteDetail == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("존재하지 않는 테이스팅 노트입니다.");
        }

        return ResponseEntity.ok(noteDetail);
    }


    //노트 작성 정보
    @GetMapping("/notes/write/{drinkId}")
    public ResponseEntity<?> getNoteWriteForm(@PathVariable int drinkId, Authentication authentication
    ) {
        if (authentication == null 
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        return ResponseEntity.ok(
                tastingNoteService.getNoteWriteForm(drinkId, userDetails.getUserId())
        );
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
    public ResponseEntity<Map<String, Object>> getNoteUpdateForm(
            @PathVariable int noteId,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        TastingNoteDto note = tastingNoteService.findByNoteId(noteId);

        if (note == null || "Y".equals(note.getIsDeleted())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 테이스팅 노트입니다.");
        }

        if (!Objects.equals(note.getUserId(), userDetails.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 노트만 수정할 수 있습니다.");
        }

        if (reportMapper.existsCompletedReportByNoteId(noteId) > 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "신고 처리된 노트는 수정할 수 없습니다.");
        }

        DrinkDto drink = drinkService.findDrinkByNoteId(noteId);

        List<TastingNoteMetricDto> metricList = tastingNoteService.findMetricByNoteId(noteId);

        List<CommonCodeDto> defaultMetricList = tastingNoteService.getMetricCode(note.getDrinkId());

        ImageDto image = new ImageDto();
        image.setObjId(note.getNoteId());
        image.setObjType("NOTE");
        image = imageMapper.selectImage(image);

        Map<String, Object> result = new HashMap<>();
        result.put("drink", drink);
        result.put("note", note);
        result.put("metricList", metricList);
        result.put("defaultMetricList", defaultMetricList);
        result.put("image", image);

        return ResponseEntity.ok(result);
    }
    
    //노트 수정
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

    //노트 삭제
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

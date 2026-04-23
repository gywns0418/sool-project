package com.example.sool.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.sool.dto.CommonCodeDto;
import com.example.sool.dto.ReportDto;
import com.example.sool.security.CustomUserDetails;
import com.example.sool.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // 신고 사유 목록 조회
    @GetMapping("/reasons")
    public ResponseEntity<List<CommonCodeDto>> getReportReasons() {

        List<CommonCodeDto> list = reportService.getReportReasons();

        return ResponseEntity.ok(list);
    }

    // 신고 등록
    @PostMapping("")
    public ResponseEntity<Map<String, Object>> insertReport(@RequestBody ReportDto dto,Authentication authentication) {

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        dto.setUserId(userDetails.getUserId());

        //신고 정보 확인
        validateReportRequest(dto);

        reportService.insertReport(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "신고가 접수되었습니다.");

        return ResponseEntity.ok(result);
    }

    //신고 목록 조회
    @GetMapping("/myReport")
    public ResponseEntity<List<ReportDto>> myReport(Authentication authentication){

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        int userId = userDetails.getUserId();
        
        List<ReportDto> reportlist = reportService.getReportList(userId);
        
        return ResponseEntity.ok(reportlist);
    }

    //신고 정보 확인
    private void validateReportRequest(ReportDto dto) {
        if (dto.getObjType() == null || dto.getObjType().trim().isEmpty()) {
            throw new IllegalArgumentException("신고 대상 타입이 없습니다.");
        }

        if (dto.getObjId() == null || dto.getObjId() <= 0) {
            throw new IllegalArgumentException("신고 대상 번호가 올바르지 않습니다.");
        }

        if (dto.getReasonCode() == null || dto.getReasonCode().trim().isEmpty()) {
            throw new IllegalArgumentException("신고 사유를 선택해주세요.");
        }
    }

    //에러 메세지 전달
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
    public Map<String, Object> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", e.getMessage());
        return result;
    }
}
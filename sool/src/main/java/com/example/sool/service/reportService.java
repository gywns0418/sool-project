package com.example.sool.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sool.dto.CommonCodeDto;
import com.example.sool.dto.ReportDto;
import com.example.sool.mapper.CommonCodeMapper;
import com.example.sool.mapper.ReportMapper;

@Service
public class ReportService {

    private final ReportMapper reportMapper;
    private final CommonCodeMapper commonCodeMapper;

    public ReportService(ReportMapper reportMapper, CommonCodeMapper commonCodeMapper) {
        this.reportMapper = reportMapper;
        this.commonCodeMapper = commonCodeMapper;
    }

    //공통 코드에서 신고 사유 가져오기 
    public List<CommonCodeDto> getReportReasons(){
        return commonCodeMapper.getReportReasons();
    }

    //신고 등록 (이미 등록 되어있으면 실패)
    public int insertReport(ReportDto reportDto) {
        int count = reportMapper.existsReport(reportDto);

        if (count > 0) {
            throw new IllegalArgumentException("이미 신고한 노트입니다.");
        }

        return reportMapper.insertReport(reportDto);
    }

    //마이페이지 신고 목록
    public List<ReportDto> getReportList(int userId) {
        return reportMapper.selectReportByUserid(userId);
    }
}
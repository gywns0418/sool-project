package com.example.sool.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sool.dto.CommonCodeDto;
import com.example.sool.dto.ReportDto;
import com.example.sool.mapper.ReportMapper;

@Service
public class ReportService {

    private final ReportMapper reportMapper;

    public ReportService(ReportMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    public List<CommonCodeDto> getReportReasons(){
        return reportMapper.getReportReasons();
    }

    //신고 등록 (이미 등록 되어있으면 실패)
    public int insertReport(ReportDto reportDto) {
        int count = reportMapper.existsReport(reportDto);

        if (count > 0) {
            throw new IllegalArgumentException("이미 신고한 노트입니다.");
        }

        return reportMapper.insertReport(reportDto);
    }


    public List<ReportDto> getReportList(int userId) {
        return reportMapper.selectReportByUserid(userId);
    }

    //아직 사용 X


    public List<ReportDto> selectReportList() {
        return reportMapper.selectReportList();
    }

    public int updateReportStatus(ReportDto reportDto) {
        return reportMapper.updateReportStatus(reportDto);
    }

    public int deleteReport(int report_id) {
        return reportMapper.deleteReport(report_id);
    }
}
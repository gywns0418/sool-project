package com.example.sool.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sool.dto.ReportDto;
import com.example.sool.mapper.ReportMapper;

@Service
public class ReportService {

    private final ReportMapper reportMapper;

    public ReportService(ReportMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    public int insertReport(ReportDto reportDto) {
        return reportMapper.insertReport(reportDto);
    }

    public ReportDto selectReportById(int report_id) {
        return reportMapper.selectReportById(report_id);
    }

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
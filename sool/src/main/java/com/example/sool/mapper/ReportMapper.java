package com.example.sool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.ReportDto;

@Mapper
public interface ReportMapper {

    int insertReport(ReportDto reportDto);

    ReportDto selectReportById(int report_id);

    List<ReportDto> selectReportList();

    int updateReportStatus(ReportDto reportDto);

    int deleteReport(int report_id);
}
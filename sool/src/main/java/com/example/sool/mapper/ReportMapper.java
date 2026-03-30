package com.example.sool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.CommonCodeDto;
import com.example.sool.dto.ReportDto;

@Mapper
public interface ReportMapper {

    List<CommonCodeDto> getReportReasons();

    int insertReport(ReportDto reportDto);

    int existsReport(ReportDto reportDto);

    List<ReportDto> selectReportByUserid(int userId);

    List<ReportDto> selectReportList();

    int updateReportStatus(ReportDto reportDto);

    int deleteReport(int report_id);
}
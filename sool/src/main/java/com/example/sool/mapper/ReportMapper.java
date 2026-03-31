package com.example.sool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.CommonCodeDto;
import com.example.sool.dto.ReportDto;

@Mapper
public interface ReportMapper {

    //신고 사유 가져오기
    List<CommonCodeDto> getReportReasons();

    //신고 등록
    int insertReport(ReportDto reportDto);

    //신고 존재 확인
    int existsReport(ReportDto reportDto);

    //마이페이지 신고 조회
    List<ReportDto> selectReportByUserid(int userId);
}
package com.example.sool.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sool.dto.CommentDto;
import com.example.sool.dto.CommonCodeDto;
import com.example.sool.dto.ReportDto;
import com.example.sool.dto.TastingNoteDto;
import com.example.sool.mapper.CommentMapper;
import com.example.sool.mapper.CommonCodeMapper;
import com.example.sool.mapper.ReportMapper;
import com.example.sool.mapper.TastingNoteMapper;

@Service
public class ReportService {

    private final ReportMapper reportMapper;
    private final CommonCodeMapper commonCodeMapper;
    private final TastingNoteMapper tastingNoteMapper;
    private final CommentMapper commentMapper;

    public ReportService(ReportMapper reportMapper, CommonCodeMapper commonCodeMapper, 
                                TastingNoteMapper tastingNoteMapper, CommentMapper commentMapper) {
        this.reportMapper = reportMapper;
        this.commonCodeMapper = commonCodeMapper;
        this.tastingNoteMapper = tastingNoteMapper;
        this.commentMapper = commentMapper;
    }

    //공통 코드에서 신고 사유 가져오기 
    public List<CommonCodeDto> getReportReasons(){
        return commonCodeMapper.getReportReasons();
    }

    //신고 등록 (이미 등록 되어있으면 실패)
    public int insertReport(ReportDto reportDto) {
        validateReport(reportDto);

        int count = reportMapper.existsReport(reportDto);

        if (count > 0) {
            throw new IllegalArgumentException("이미 신고한 항목입니다.");
        }

        return reportMapper.insertReport(reportDto);
    }

    private void validateReport(ReportDto reportDto) {
        if (reportDto == null) {
            throw new IllegalArgumentException("신고 정보가 없습니다.");
        }

        if (reportDto.getUserId() == null) {
            throw new IllegalArgumentException("회원 정보가 없습니다.");
        }

        if (reportDto.getObjType() == null || reportDto.getObjType().trim().isEmpty()) {
            throw new IllegalArgumentException("신고 대상 타입이 없습니다.");
        }

        if (!"NOTE".equals(reportDto.getObjType()) && !"COMMENT".equals(reportDto.getObjType())) {
            throw new IllegalArgumentException("신고 대상 타입이 올바르지 않습니다.");
        }

        if (reportDto.getObjId() == null || reportDto.getObjId() <= 0) {
            throw new IllegalArgumentException("신고 대상 번호가 올바르지 않습니다.");
        }

        if (reportDto.getReasonCode() == null || reportDto.getReasonCode().trim().isEmpty()) {
            throw new IllegalArgumentException("신고 사유가 없습니다.");
        }

        if ("NOTE".equals(reportDto.getObjType())) {
            TastingNoteDto note = tastingNoteMapper.findByNoteId(reportDto.getObjId());

            if (note == null || "Y".equals(note.getIsDeleted())) {
                throw new IllegalArgumentException("삭제되었거나 존재하지 않는 테이스팅 노트입니다.");
            }
        }

        if ("COMMENT".equals(reportDto.getObjType())) {
            CommentDto comment = commentMapper.findByCommentId(reportDto.getObjId());

            if (comment == null || "Y".equals(comment.getIsDeleted())) {
                throw new IllegalArgumentException("삭제되었거나 존재하지 않는 댓글입니다.");
            }
        }
    }

    //마이페이지 신고 목록
    public List<ReportDto> getReportList(int userId) {
        return reportMapper.selectReportByUserid(userId);
    }
}
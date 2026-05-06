package com.example.sool.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sool.dto.CommentDto;
import com.example.sool.dto.ReportDto;
import com.example.sool.mapper.CommentMapper;
import com.example.sool.mapper.ReportMapper;
import com.example.sool.mapper.TastingNoteMapper;

@Service
public class CommentService {

    private final CommentMapper commentMapper;
    private final TastingNoteMapper tastingNoteMapper;
    private final ReportMapper reportMapper;

    public CommentService(CommentMapper commentMapper, TastingNoteMapper tastingNoteMapper, ReportMapper reportMapper) {
        this.commentMapper = commentMapper;
        this.tastingNoteMapper = tastingNoteMapper;
        this.reportMapper = reportMapper;
    }

    @Transactional
    public void createComment(CommentDto dto) {
        validateNote(dto.getNoteId());
        validateContent(dto.getContent());

        commentMapper.insertComment(dto);
        commentMapper.updateGroupId(dto.getCommentId());
    }

    @Transactional
    public void createReply(CommentDto dto) {
        validateNote(dto.getNoteId());
        validateParentCommentId(dto.getParentCommentId());
        validateContent(dto.getContent());

        CommentDto parent = commentMapper.findActiveByCommentId(dto.getParentCommentId());

        if (parent == null) {
            throw new IllegalArgumentException("삭제되었거나 존재하지 않는 부모 댓글입니다.");
        }

        if (!parent.getNoteId().equals(dto.getNoteId())) {
            throw new IllegalArgumentException("잘못된 댓글 요청입니다.");
        }

        dto.setGroupId(parent.getGroupId());
        dto.setDepth(1);

        commentMapper.insertReplyComment(dto);
    }

    public List<CommentDto> findByNoteId(Integer noteId) {
        validateNote(noteId);
        return commentMapper.findByNoteId(noteId);
    }
    
    @Transactional 
    public int updateComment(CommentDto commentDto) {
        validateCommentId(commentDto.getCommentId());
        validateContent(commentDto.getContent());

        CommentDto savedComment = commentMapper.findByCommentId(commentDto.getCommentId());

        if (savedComment == null || "Y".equals(savedComment.getIsDeleted())) {
            throw new IllegalArgumentException("삭제되었거나 존재하지 않는 댓글입니다.");
        }

        validateNote(savedComment.getNoteId());

        commentDto.setNoteId(savedComment.getNoteId());

        return commentMapper.updateComment(commentDto);
    }

    //댓글 삭제
    @Transactional
    public int deleteComment(Integer commentId) {
        validateCommentId(commentId);

        CommentDto savedComment = commentMapper.findByCommentId(commentId);

        if (savedComment == null || "Y".equals(savedComment.getIsDeleted())) {
            throw new IllegalArgumentException("삭제되었거나 존재하지 않는 댓글입니다.");
        }

        validateNote(savedComment.getNoteId());

        ReportDto rDto = new ReportDto();
        rDto.setObjType("COMMENT");
        rDto.setObjId(commentId);
        reportMapper.deleteReport(rDto);

        int result = commentMapper.deleteComment(commentId);

        return result;
    }

    //노트 정보 확인
    private void validateNote(Integer noteId) {
        if (noteId == null) {
            throw new IllegalArgumentException("노트 정보가 올바르지 않습니다.");
        }

        int count = tastingNoteMapper.existsNote(noteId);

        if (count == 0) {
            throw new IllegalArgumentException("삭제되었거나 존재하지 않는 테이스팅 노트입니다.");
        }
    }

    //부모 댓글 정보 확인
    private void validateParentCommentId(Integer parentCommentId) {
        if (parentCommentId == null) {
            throw new IllegalArgumentException("부모 댓글 정보가 올바르지 않습니다.");
        }
    }

    //댓글 정보 확인
    private void validateCommentId(Integer commentId) {
        if (commentId == null) {
            throw new IllegalArgumentException("댓글 정보가 올바르지 않습니다.");
        }
    }

    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용을 입력해주세요.");
        }
    }

    private CommentDto validateActiveComment(Integer commentId) {
        CommentDto comment = commentMapper.findActiveByCommentId(commentId);

        if (comment == null) {
            throw new IllegalArgumentException("삭제되었거나 존재하지 않는 댓글입니다.");
        }

        return comment;
    }
}
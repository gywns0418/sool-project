package com.example.sool.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sool.dto.CommentDto;
import com.example.sool.mapper.CommentMapper;
import com.example.sool.mapper.TastingNoteMapper;

@Service
public class CommentService {

    private final CommentMapper commentMapper;
    private final TastingNoteMapper tastingNoteMapper;

    public CommentService(CommentMapper commentMapper,TastingNoteMapper tastingNoteMapper) {
        this.commentMapper = commentMapper;
        this.tastingNoteMapper = tastingNoteMapper;
    }

    //댓글 작성
    @Transactional
    public void createComment(CommentDto dto) {
        validateNote(dto.getNoteId());

        commentMapper.insertComment(dto);

        commentMapper.updateGroupId(dto.getCommentId());
    }

    //대댓글 작성
    @Transactional
    public void createReply(CommentDto dto){
        validateNote(dto.getNoteId());

        CommentDto parent = commentMapper.findByCommentId(dto.getParentCommentId());

        dto.setGroupId(parent.getGroupId());

        //대댓글 깊이 1 설정(나중에 교체 가능)
        dto.setDepth(1);

        commentMapper.insertReplyComment(dto);
    }


    //특정 노트의 댓글 목록 조회
    public List<CommentDto> findByNoteId(Integer noteId) {
        return commentMapper.findByNoteId(noteId);
    }

    //댓글 수정
    public int updateComment(CommentDto commentDto) {
        validateNote(commentDto.getNoteId());

        return commentMapper.updateComment(commentDto);
    }

    //댓글 삭제
    public int deleteComment(Integer commentId) {

        return commentMapper.deleteComment(commentId);
    }

    private void validateNote(Integer noteId) {
    int count = tastingNoteMapper.existsNote(noteId);

    if (count == 0) {
        throw new IllegalArgumentException("삭제되었거나 존재하지 않는 테이스팅 노트입니다.");
    }
}
}
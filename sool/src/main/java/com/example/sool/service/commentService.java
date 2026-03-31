package com.example.sool.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sool.dto.CommentDto;
import com.example.sool.mapper.CommentMapper;

@Service
public class CommentService {

    private final CommentMapper commentMapper;

    public CommentService(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    //댓글 작성
    @Transactional
    public void createComment(CommentDto dto) {
        commentMapper.insertComment(dto);

        commentMapper.updateGroupId(dto.getCommentId());
    }

    //대댓글 작성
    @Transactional
    public void createReply(CommentDto dto){
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
        return commentMapper.updateComment(commentDto);
    }

    //댓글 삭제
    public int deleteComment(Integer commentId) {
        return commentMapper.deleteComment(commentId);
    }
}
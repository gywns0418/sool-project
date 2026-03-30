package com.example.sool.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sool.dto.CommentDto;
import com.example.sool.mapper.CommentMapper;

@Service
public class CommentService {

    private final CommentMapper commentMapper;

    public CommentService(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    //댓글 작성
    public int insertComment(CommentDto commentDto) {
        return commentMapper.insertComment(commentDto);
    }

    //특정 노트의 댓글 목록 조회
    public List<CommentDto> findByNoteId(Integer noteId) {
        return commentMapper.findByNoteId(noteId);
    }

    public List<CommentDto> findByParentCommentId(Integer parentCommentId) {
        return commentMapper.findByParentCommentId(parentCommentId);
    }

    public int updateComment(CommentDto commentDto) {
        return commentMapper.updateComment(commentDto);
    }

    public int deleteComment(Integer commentId) {
        return commentMapper.deleteComment(commentId);
    }
}
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

    public int insertComment(CommentDto commentDto) {
        return commentMapper.insertComment(commentDto);
    }

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

    public List<CommentDto> findByUserId(Integer userId) {
        return commentMapper.findByUserId(userId);
    }
}
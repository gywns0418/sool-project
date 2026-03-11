package com.example.sool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.CommentDto;

@Mapper
public interface CommentMapper {

    int insertComment(CommentDto commentDto);

    List<CommentDto> findByNoteId(Integer noteId);

    List<CommentDto> findByParentCommentId(Integer parentCommentId);

    int updateComment(CommentDto commentDto);

    int deleteComment(Integer commentId);

    List<CommentDto> findByUserId(Integer userId);
}
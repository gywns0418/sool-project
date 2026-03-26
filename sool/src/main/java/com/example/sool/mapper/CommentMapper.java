package com.example.sool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.CommentDto;

@Mapper
public interface CommentMapper {

    //댓글 작성
    int insertComment(CommentDto commentDto);

    //특정 노트의 댓글 목록 조회
    List<CommentDto> findByNoteId(Integer noteId);


    List<CommentDto> findByParentCommentId(Integer parentCommentId);

    int updateComment(CommentDto commentDto);

    int deleteComment(Integer commentId);

    List<CommentDto> findByUserId(Integer userId);
}
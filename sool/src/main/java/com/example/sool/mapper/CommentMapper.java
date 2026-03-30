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

    //댓글 삭제
    int deleteComment(Integer commentId);

    //노트 삭제시 댓글 전체 삭제
    int deleteAllComment(Integer noteId);

    //회원 탈퇴 시 댓글 삭제
    int deleteByUserId(Integer userId);


    List<CommentDto> findByParentCommentId(Integer parentCommentId);

    int updateComment(CommentDto commentDto);

    List<CommentDto> findByUserId(Integer userId);



}
package com.example.sool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.CommentDto;

@Mapper
public interface CommentMapper {

    //댓글 작성
    int insertComment(CommentDto commentDto);
    //댓글 작성후 그룹아이디 설정
    int updateGroupId(Integer commentId);

    //특정 노트의 댓글 목록 조회
    List<CommentDto> findByNoteId(Integer noteId);

    //대댓글 작성
    int insertReplyComment(CommentDto commentDto);

    //대댓글 작성을 위한 부모 댓글 찾기
    CommentDto findByCommentId(Integer commentId);

    //댓글
    int updateComment(CommentDto commentDto);

    //댓글 삭제
    int deleteComment(Integer commentId);

    //노트 삭제시 댓글 전체 삭제
    int deleteAllComment(Integer noteId);

    //회원 탈퇴 시 댓글 삭제
    int deleteByUserId(Integer userId);

}
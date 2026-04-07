package com.example.sool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.NoteSearchDto;
import com.example.sool.dto.TastingNoteDto;

@Mapper
public interface TastingNoteMapper {
    //최근 노트 3개 조회
    List<TastingNoteDto> recentNote();

    //주류 디테일 노트 목록 
    List<TastingNoteDto> findNoteByDrinkId(NoteSearchDto noteSearchDto);

    //주류 노트 총 갯수
    int countNoteByDrinkId(NoteSearchDto noteSearchDto);

    //노트 상세
    TastingNoteDto getNoteDetail(Integer noteId);

    //노트 등록
    int insertTastingNote(TastingNoteDto tastingNoteDto);

    //노트 수정 노트 정보 가져오기
    TastingNoteDto findByNoteId(Integer noteId);

    //노트 수정
    int updateTastingNote(TastingNoteDto tastingNoteDto);

    //노트 삭제
    int deleteTastingNote(Integer noteId);

    //사용자의 테이스팅 노트
    List<TastingNoteDto> findByUserId(Integer userId);

    //회원 삭제 시 노트 삭제
    int deleteByUserId(Integer userId);

    //노트 작성여부 확인
    int existsMyNoteByDrinkId(int userId, int drinkId);
}
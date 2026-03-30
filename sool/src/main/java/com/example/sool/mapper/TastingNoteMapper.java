package com.example.sool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.NoteSearchDto;
import com.example.sool.dto.TastingNoteDto;

@Mapper
public interface TastingNoteMapper {

    int insertTastingNote(TastingNoteDto tastingNoteDto);

    TastingNoteDto getNoteDetail(Integer noteId);

    //주류 디테일 노트 목록 
    List<TastingNoteDto> findNoteByDrinkId(NoteSearchDto noteSearchDto);

    int countNoteByDrinkId(NoteSearchDto noteSearchDto);


    //최근 노트 3개 조회
    List<TastingNoteDto> recentNote();

    //노트 수정 노트 정보 가져오기
    TastingNoteDto findByNoteId(Integer noteId);

    //노트 수정
    int updateTastingNote(TastingNoteDto tastingNoteDto);
    //노트 삭제
    int deleteTastingNote(Integer noteId);

    //회원 삭제 시 노트 삭제
    int deleteByUserId(Integer userId);





    List<TastingNoteDto> orderByRatingDesc(Integer drinkId);



    List<TastingNoteDto> findByUserId(Integer userId);
}
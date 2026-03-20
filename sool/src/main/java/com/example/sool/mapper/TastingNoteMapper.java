package com.example.sool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.NoteSearchDto;
import com.example.sool.dto.TastingNoteDto;

@Mapper
public interface TastingNoteMapper {

    int insertTastingNote(TastingNoteDto tastingNoteDto);

    TastingNoteDto findByNoteId(Integer noteId);

    //주류 디테일 노트 목록 
    List<TastingNoteDto> findNoteByDrinkId(NoteSearchDto noteSearchDto);

    int countNoteByDrinkId(NoteSearchDto noteSearchDto);


    //최근 노트 3개 조회
    List<TastingNoteDto> recentNote();

    List<TastingNoteDto> orderByRatingDesc(Integer drinkId);

    int updateTastingNote(TastingNoteDto tastingNoteDto);

    int deleteTastingNote(Integer noteId);

    List<TastingNoteDto> findByUserId(Integer userId);
}
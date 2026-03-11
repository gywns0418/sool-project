package com.example.sool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.TastingNoteDto;

@Mapper
public interface TastingNoteMapper {

    int insertTastingNote(TastingNoteDto tastingNoteDto);

    TastingNoteDto findByNoteId(Integer noteId);

    List<TastingNoteDto> findNoteAll(Integer drinkId);

    List<TastingNoteDto> orderByRatingDesc(Integer drinkId);

    int updateTastingNote(TastingNoteDto tastingNoteDto);

    int deleteTastingNote(Integer noteId);

    List<TastingNoteDto> findByUserId(Integer userId);
}
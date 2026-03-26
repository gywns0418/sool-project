package com.example.sool.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sool.dto.CommonCodeDto;
import com.example.sool.dto.NoteSearchDto;
import com.example.sool.dto.TastingNoteDto;
import com.example.sool.dto.TastingNoteMetricDto;
import com.example.sool.mapper.TastingNoteMapper;
import com.example.sool.mapper.TastingNoteMetricMapper;

@Service
public class TastingNoteService {
    private final TastingNoteMapper tastingNoteMapper;
    private final TastingNoteMetricMapper tastingNoteMetricMapper;

    public TastingNoteService(TastingNoteMapper tastingNoteMapper,
                                TastingNoteMetricMapper tastingNoteMetricMapper){
        this.tastingNoteMapper = tastingNoteMapper;
        this.tastingNoteMetricMapper = tastingNoteMetricMapper;
    }

    //테이스팅 노트

    //최근 노트 3개 조회
    public List<TastingNoteDto> recentNote(){
        return tastingNoteMapper.recentNote();
    }

    //주류 디테일 노트 목록
    public List<TastingNoteDto> findNoteByDrinkId(NoteSearchDto noteSearchDto){ 
        return tastingNoteMapper.findNoteByDrinkId(noteSearchDto);
    }

    public int countNoteByDrinkId(NoteSearchDto noteSearchDto){
        return tastingNoteMapper.countNoteByDrinkId(noteSearchDto);
    }

    public int insertTastingNote(TastingNoteDto tastingNoteDto){
        return tastingNoteMapper.insertTastingNote(tastingNoteDto);
    }

    public TastingNoteDto getNoteDetail(Integer noteId){
        return tastingNoteMapper.getNoteDetail(noteId);
    }



    public int updateTastingNote(TastingNoteDto tastingNoteDto){
        return tastingNoteMapper.updateTastingNote(tastingNoteDto);
    }

    public int deleteTastingNote(Integer noteId){
        return tastingNoteMapper.deleteTastingNote(noteId);
    }

    public List<TastingNoteDto> findByUserId(Integer userId){
        return tastingNoteMapper.findByUserId(userId);
    }

    //테이스팅 노트 맛 점수

    //카테고리 별 지표 조회
    public List<CommonCodeDto> getMetricCode(int drinkId){
        return tastingNoteMetricMapper.getMetricCode(drinkId);
    }

    public int insertMetric(TastingNoteMetricDto tastingNoteMetricDto) {
        return tastingNoteMetricMapper.insertMetric(tastingNoteMetricDto);
    }

    public List<TastingNoteMetricDto> findMetricByNoteId(Integer noteId) {
        return tastingNoteMetricMapper.findByNoteId(noteId);
    }

    public int updateMetric(TastingNoteMetricDto tastingNoteMetricDto) {
        return tastingNoteMetricMapper.updateMetric(tastingNoteMetricDto);
    }

    public int deleteMetricByNoteId(Integer noteId) {
        return tastingNoteMetricMapper.deleteByNoteId(noteId);
    }

    
}

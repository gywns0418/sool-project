package com.example.sool.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

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

    public List<TastingNoteDto> findNoteAll(Integer drinkId, String sort){
        
            Map<String, Object> param = new HashMap<>();
            param.put("drinkId", drinkId);
            param.put("sort", sort);

        return tastingNoteMapper.findNoteAll(param);
    }

    public int insertTastingNote(TastingNoteDto tastingNoteDto){
        return tastingNoteMapper.insertTastingNote(tastingNoteDto);
    }

    public TastingNoteDto findByNoteId(Integer noteId){
        return tastingNoteMapper.findByNoteId(noteId);
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

    public List<TastingNoteMetricDto> findByMetricCode(String metricCode){
        return tastingNoteMetricMapper.findByMetricCode(metricCode);
    }
}

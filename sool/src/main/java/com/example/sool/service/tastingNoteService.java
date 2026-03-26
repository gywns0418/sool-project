package com.example.sool.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sool.dto.CommonCodeDto;
import com.example.sool.dto.DrinkDto;
import com.example.sool.dto.NoteSearchDto;
import com.example.sool.dto.TastingNoteDto;
import com.example.sool.dto.TastingNoteMetricDto;
import com.example.sool.mapper.TastingNoteMapper;
import com.example.sool.mapper.TastingNoteMetricMapper;

@Service
public class TastingNoteService {
    private final TastingNoteMapper tastingNoteMapper;
    private final TastingNoteMetricMapper tastingNoteMetricMapper;
    private final DrinkService drinkService;

    public TastingNoteService(TastingNoteMapper tastingNoteMapper,TastingNoteMetricMapper tastingNoteMetricMapper,
            DrinkService drinkService){
        this.tastingNoteMapper = tastingNoteMapper;
        this.tastingNoteMetricMapper = tastingNoteMetricMapper;
        this.drinkService = drinkService;
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

    //맛 프로파일 정보 가져오기
    public List<CommonCodeDto> getMetricCode(int drinkId){
        return tastingNoteMetricMapper.getMetricCode(drinkId);
    }

    //노트 디테일 맛 프로파일
    public List<TastingNoteMetricDto> findMetricByNoteId(Integer noteId) {
        return tastingNoteMetricMapper.findByNoteId(noteId);
    }


    public int insertMetric(TastingNoteMetricDto tastingNoteMetricDto) {
        return tastingNoteMetricMapper.insertMetric(tastingNoteMetricDto);
    }



    public int updateMetric(TastingNoteMetricDto tastingNoteMetricDto) {
        return tastingNoteMetricMapper.updateMetric(tastingNoteMetricDto);
    }

    public int deleteMetricByNoteId(Integer noteId) {
        return tastingNoteMetricMapper.deleteByNoteId(noteId);
    }




    @Transactional
    public Integer createNote(TastingNoteDto dto) {
        validateCreateNote(dto);

        DrinkDto drink = drinkService.findByDrinkId(dto.getDrinkId());
        if (drink == null) {
            throw new IllegalArgumentException("존재하지 않는 주류입니다.");
        }

        dto.setTitle(dto.getTitle().trim());
        dto.setContent(dto.getContent().trim());

        tastingNoteMapper.insertTastingNote(dto);
        System.out.println("insert 후 noteId = " + dto.getNoteId());

        if (dto.getMetricList() != null && !dto.getMetricList().isEmpty()) {
            for (TastingNoteMetricDto metric : dto.getMetricList()) {
                validateMetric(metric);
                metric.setNoteId(dto.getNoteId());
                System.out.println(dto.getMetricList());
                tastingNoteMetricMapper.insertMetric(metric);
            }
        }

        return dto.getNoteId();
    }

    private void validateCreateNote(TastingNoteDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("요청 데이터가 없습니다.");
        }

        if (dto.getDrinkId() == null || dto.getDrinkId() <= 0) {
            throw new IllegalArgumentException("주류 정보가 올바르지 않습니다.");
        }

        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("노트 제목을 입력해주세요.");
        }

        if (dto.getTitle().trim().length() > 100) {
            throw new IllegalArgumentException("노트 제목은 100자 이하로 입력해주세요.");
        }

        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("노트 내용을 입력해주세요.");
        }

        if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
            throw new IllegalArgumentException("별점은 1점부터 5점까지 입력해주세요.");
        }
    }

    private void validateMetric(TastingNoteMetricDto metric) {
        if (metric == null) {
            throw new IllegalArgumentException("맛 프로파일 정보가 올바르지 않습니다.");
        }

        if (metric.getMetricCode() == null || metric.getMetricCode().trim().isEmpty()) {
            throw new IllegalArgumentException("맛 프로파일 코드가 없습니다.");
        }

        if (metric.getScore() == null || metric.getScore() < 1 || metric.getScore() > 5) {
            throw new IllegalArgumentException("맛 프로파일 점수는 1점부터 5점까지 입력해주세요.");
        }
    }
}

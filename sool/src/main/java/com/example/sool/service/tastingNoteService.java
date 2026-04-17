package com.example.sool.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sool.dto.CommonCodeDto;
import com.example.sool.dto.DrinkDto;
import com.example.sool.dto.ImageDto;
import com.example.sool.dto.LikeDto;
import com.example.sool.dto.NoteSearchDto;
import com.example.sool.dto.TastingNoteDto;
import com.example.sool.dto.TastingNoteMetricDto;
import com.example.sool.mapper.CommentMapper;
import com.example.sool.mapper.CommonCodeMapper;
import com.example.sool.mapper.ImageMapper;
import com.example.sool.mapper.LikeMapper;
import com.example.sool.mapper.TastingNoteMapper;
import com.example.sool.mapper.TastingNoteMetricMapper;

@Service
public class TastingNoteService {
    private final TastingNoteMapper tastingNoteMapper;
    private final TastingNoteMetricMapper tastingNoteMetricMapper;
    private final CommonCodeMapper commonCodeMapper;
    private final DrinkService drinkService;
    private final LikeMapper likeMapper;
    private final CommentMapper commentMapper;
    private final ImageMapper imageMapper;
    private final S3Service s3Service;

    public TastingNoteService(TastingNoteMapper tastingNoteMapper,TastingNoteMetricMapper tastingNoteMetricMapper,CommonCodeMapper commonCodeMapper,
            DrinkService drinkService, LikeMapper likeMapper, CommentMapper commentMapper,ImageMapper imageMapper,S3Service s3Service){
        this.tastingNoteMapper = tastingNoteMapper;
        this.tastingNoteMetricMapper = tastingNoteMetricMapper;
        this.commonCodeMapper = commonCodeMapper;
        this.drinkService = drinkService;
        this.likeMapper = likeMapper;
        this.commentMapper = commentMapper;
        this.imageMapper = imageMapper;
        this.s3Service = s3Service;
    }

    //테이스팅 노트

    //최근 노트 3개 조회
    public List<TastingNoteDto> recentNote(){
        List<TastingNoteDto> list = tastingNoteMapper.recentNote();
        for (TastingNoteDto note : list) {
            ImageDto image = imageMapper.selectImageByDrinkId(note.getDrinkId());
            note.setImage(image);
        }
        return list;
    }

    //주류 디테일 노트 목록
    public List<TastingNoteDto> findNoteByDrinkId(NoteSearchDto noteSearchDto){ 
        List<TastingNoteDto> list = tastingNoteMapper.findNoteByDrinkId(noteSearchDto);
        for (TastingNoteDto note : list) {
            ImageDto image = imageMapper.selectImageByNoteId(note.getNoteId());
            note.setImage(image);
        }
        return list;
    }

    //페이지네이션 용 노트 수
    public int countNoteByDrinkId(NoteSearchDto noteSearchDto){
        return tastingNoteMapper.countNoteByDrinkId(noteSearchDto);
    }

    //노트 디테일
    public TastingNoteDto getNoteDetail(Integer noteId) {
        TastingNoteDto noteDetail = tastingNoteMapper.getNoteDetail(noteId); //노트의 저장 정보

        if (noteDetail == null) {
            return null;
        }

        //해당하는 노트의 맛 프로파일
        List<TastingNoteMetricDto> metricList = tastingNoteMetricMapper.findByNoteId(noteId);
        noteDetail.setMetricList(metricList);

        //노트 이미지
        ImageDto image = imageMapper.selectImageByNoteId(noteId);
        noteDetail.setImage(image);

        return noteDetail;
    }
    
    //노트 기본 정보
    public TastingNoteDto findByNoteId(Integer noteId){
        return tastingNoteMapper.findByNoteId(noteId);
    }

    //마이페이지 테이스팅 노트
    public List<TastingNoteDto> findByUserId(Integer userId){

        List<TastingNoteDto> list = tastingNoteMapper.findByUserId(userId);
        //이미지 추가
        for (TastingNoteDto note : list) {
            ImageDto image = imageMapper.selectImageByDrinkId(note.getDrinkId());
            note.setImage(image);
        }
        return list;
    }

    //****************************테이스팅 노트 맛 점수************************************

    //맛 프로파일 정보 가져오기
    public List<CommonCodeDto> getMetricCode(int drinkId){
        return commonCodeMapper.getMetricCode(drinkId);
    }

    //노트 디테일 맛 프로파일
    public List<TastingNoteMetricDto> findMetricByNoteId(Integer noteId) {
        return tastingNoteMetricMapper.findByNoteId(noteId);
    }


    //주류 디테일 평균 맛 프로파일
    public List<TastingNoteMetricDto> findAvgMetricByDrinkId(Integer drinkId){
        return tastingNoteMetricMapper.findAvgMetricByDrinkId(drinkId);
    }

    //노트 작성 기본 정보 ( 작성여부 확인 )
    public boolean existsMyNoteByDrinkId(int userId, int drinkId) {
        return tastingNoteMapper.existsMyNoteByDrinkId(userId, drinkId) > 0;
    }

    //노트 삽입
    @Transactional  //트랜잭션 관리 어노테이션
    public int createNote(TastingNoteDto dto) {
        validateCreateNote(dto);

        DrinkDto drink = drinkService.findByDrinkId(dto.getDrinkId());
        if (drink == null) {
            throw new IllegalArgumentException("존재하지 않는 주류입니다.");
        }

        dto.setTitle(dto.getTitle().trim());
        dto.setContent(dto.getContent().trim());

        tastingNoteMapper.insertTastingNote(dto);

        if (dto.getMetricList() != null && !dto.getMetricList().isEmpty()) {
            for (TastingNoteMetricDto metric : dto.getMetricList()) {
                validateMetric(metric);
                metric.setNoteId(dto.getNoteId());
                tastingNoteMetricMapper.insertMetric(metric);
            }
        }

        if (dto.getImage() != null) {
            ImageDto image = dto.getImage();
            image.setObjId(dto.getNoteId());
            image.setObjType("NOTE");
            imageMapper.insertImage(image);
        }

        return dto.getNoteId();
    }

    //노트 수정
    @Transactional
    public int updateNote(TastingNoteDto dto) {
        validateCreateNote(dto);

        TastingNoteDto savedNote = tastingNoteMapper.findByNoteId(dto.getNoteId());
        if (savedNote == null || "Y".equals(savedNote.getIsDeleted())) {
            throw new IllegalArgumentException("존재하지 않는 테이스팅 노트입니다.");
        }

        dto.setTitle(dto.getTitle().trim());
        dto.setContent(dto.getContent().trim());

        tastingNoteMapper.updateTastingNote(dto);

        tastingNoteMetricMapper.deleteByNoteId(dto.getNoteId());

        if (dto.getMetricList() != null && !dto.getMetricList().isEmpty()) {
            for (TastingNoteMetricDto metric : dto.getMetricList()) {
                validateMetric(metric);
                metric.setNoteId(dto.getNoteId());
                tastingNoteMetricMapper.insertMetric(metric);
            }
        }

        if (dto.getImage() != null) {
            ImageDto image = dto.getImage();
            image.setObjId(dto.getNoteId());
            image.setObjType("NOTE");
            if(imageMapper.selectImageByNoteId(dto.getNoteId())==null){
                imageMapper.insertImage(image);
            }else{
                imageMapper.updateImage(image);
            }   
        }

        return dto.getNoteId();
    }

    //노트 삭제
   @Transactional
    public void deleteNote(int noteId, int userId) {

        TastingNoteDto note = tastingNoteMapper.findByNoteId(noteId);

        if (note == null || "Y".equals(note.getIsDeleted())) {
            throw new IllegalArgumentException("존재하지 않는 테이스팅 노트입니다.");
        }

        if (!note.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인이 작성한 노트만 삭제할 수 있습니다.");
        }

        ImageDto image = imageMapper.selectImageByNoteId(noteId);

        if (image != null) {
            imageMapper.deleteImage(image.getImageId());
        }

        LikeDto lDto = new LikeDto();
        lDto.setObjType("NOTE");
        lDto.setObjId(noteId);
        likeMapper.deleteLike(lDto);

        commentMapper.deleteAllComment(noteId);

        tastingNoteMetricMapper.deleteByNoteId(noteId);

        tastingNoteMapper.deleteTastingNote(noteId);

        if (image != null) {
            s3Service.delete(image.getFileKey());
        }
    }

    //노트 유효성 검사
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
        
        if (dto.getContent().trim().length() > 500) {
            throw new IllegalArgumentException("노트 내용은 500자 이하로 입력해주세요.");
        }

        if (dto.getRating() == null || dto.getRating() < 1 || dto.getRating() > 5) {
            throw new IllegalArgumentException("별점은 1점부터 5점까지 입력해주세요.");
        }
        if (dto.getImage() == null) {
            throw new IllegalArgumentException("이미지는 필수입니다.");
        }
    }

    //맛 프로파일 유효성 검사
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

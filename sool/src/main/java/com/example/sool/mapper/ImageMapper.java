package com.example.sool.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.ImageDto;

@Mapper
public interface ImageMapper {

    int insertImage(ImageDto imageDto);

    ImageDto selectImageById(int image_id);

    List<ImageDto> selectImageListByUserId(int user_id);

    List<ImageDto> selectImageListByNoteId(int note_id);

    List<ImageDto> selectImageListByDrinkId(int drink_id);

    int deleteImage(int image_id);
}
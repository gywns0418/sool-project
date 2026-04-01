package com.example.sool.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.ImageDto;

@Mapper
public interface ImageMapper {

    int insertImage(ImageDto imageDto);

    ImageDto selectImageByNoteId(int objId);

    ImageDto selectImageByDrinkId(int objId);

    ImageDto selectImageById(int imageId);

    int updateImage(ImageDto dto);

    int deleteImage(int imageId);

    int deleteImageByUserId(int userId);
}
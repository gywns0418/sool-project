package com.example.sool.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.example.sool.dto.ImageDto;

@Mapper
public interface ImageMapper {

    int insertImage(ImageDto imageDto);

    ImageDto selectImage(ImageDto dto);

    int updateImage(ImageDto dto);

    int deleteImage(ImageDto dto);
}
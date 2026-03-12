package com.example.sool.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.sool.dto.ImageDto;
import com.example.sool.mapper.ImageMapper;

@Service
public class ImageService {

    private final ImageMapper imageMapper;

    public ImageService(ImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

    public int insertImage(ImageDto imageDto) {
        return imageMapper.insertImage(imageDto);
    }

    public ImageDto selectImageById(int image_id) {
        return imageMapper.selectImageById(image_id);
    }

    public List<ImageDto> selectImageListByUserId(int user_id) {
        return imageMapper.selectImageListByUserId(user_id);
    }

    public List<ImageDto> selectImageListByNoteId(int note_id) {
        return imageMapper.selectImageListByNoteId(note_id);
    }

    public List<ImageDto> selectImageListByDrinkId(int drink_id) {
        return imageMapper.selectImageListByDrinkId(drink_id);
    }


}
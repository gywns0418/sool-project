package com.example.sool.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.sool.dto.ImageDto;
import com.example.sool.dto.S3UploadResultDto;
import com.example.sool.mapper.ImageMapper;

@Service
public class ImageService {

    private final S3Service s3Service;
    private final ImageMapper imageMapper;

    public ImageService(S3Service s3Service, ImageMapper imageMapper) {
        this.s3Service = s3Service;
        this.imageMapper = imageMapper;
    }

    public ImageDto saveImage(MultipartFile file, String objType, int objId, String dirName) throws IOException {
        S3UploadResultDto uploadResult = s3Service.upload(file, dirName);

        ImageDto imageDto = new ImageDto();
        imageDto.setObjType(objType);
        imageDto.setObjId(objId);
        imageDto.setFileKey(uploadResult.getFileKey());
        imageDto.setFileUrl(uploadResult.getFileUrl());

        imageMapper.insertImage(imageDto);

        return imageDto;
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
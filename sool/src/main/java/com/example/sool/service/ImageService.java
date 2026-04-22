package com.example.sool.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.sool.dto.ImageDto;
import com.example.sool.dto.S3UploadResultDto;
import com.example.sool.mapper.ImageMapper;
import com.example.sool.mapper.TastingNoteMapper;

@Service
public class ImageService {

    private final S3Service s3Service;
    private final ImageMapper imageMapper;
    private final TastingNoteMapper tastingNoteMapper;

    public ImageService(S3Service s3Service, ImageMapper imageMapper,TastingNoteMapper tastingNoteMapper) {
        this.s3Service = s3Service;
        this.imageMapper = imageMapper;
        this.tastingNoteMapper = tastingNoteMapper;
    }

    public ImageDto saveImage(MultipartFile file, String objType, int objId, String dirName) throws IOException {
        int count = tastingNoteMapper.existsNote(objId);

        if (count == 0) {
            throw new IllegalArgumentException("삭제되었거나 존재하지 않는 테이스팅 노트입니다.");
        }

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


    
}
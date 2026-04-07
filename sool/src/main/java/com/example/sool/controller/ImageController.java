package com.example.sool.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.sool.dto.S3UploadResultDto;
import com.example.sool.service.S3Service;

@RestController
@RequestMapping("/api/image")
public class ImageController {

    private final S3Service s3Service;

    public ImageController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/upload")
    public ResponseEntity<S3UploadResultDto> uploadImage( 
        @RequestParam("file") MultipartFile file, @RequestParam("dirName") String dirName) throws IOException {
        
        //S3에 파일 업로드 및 fileKey, fileUrl 반환
        S3UploadResultDto result = s3Service.upload(file, dirName);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteImage(@RequestBody Map<String, String> param){
        String fileKey = param.get("fileKey");

        //S3 파일 삭제 및 성공 결과 반환
        boolean result = s3Service.delete(fileKey);
        System.out.println(result);
        
        return ResponseEntity.ok(result);
    }
}
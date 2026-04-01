package com.example.sool.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.sool.dto.S3UploadResultDto;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.region}")
    private String region;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;  //5MB

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "png", "jpg", "jpeg", "webp"
    );

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    //s3 파일 업로드
    public S3UploadResultDto upload(MultipartFile file, String dirName) throws IOException {
        validateFile(file);

        String originalName = file.getOriginalFilename();       //파일 이름
        String savedName = UUID.randomUUID() + "_" + originalName;      //저장 파일 이름 ex) ********_filename
        String fileKey = dirName + "/" + savedName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(
                putObjectRequest,
                RequestBody.fromBytes(file.getBytes())
        );

        String encodedKey = URLEncoder.encode(fileKey, StandardCharsets.UTF_8).replace("+", "%20");
        String fileUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + encodedKey;

        return new S3UploadResultDto(fileKey, fileUrl);
    }

    //s3 파일 삭제
    public boolean delete(String fileKey) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            return true;

        } catch (Exception e) {
            System.out.println("S3 파일 삭제 실패 : " + fileKey);
            e.printStackTrace();
            return false;
        }
    }

    //업로드 파일 형식 확인
    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기는 5MB 이하만 가능합니다.");
        }

        String fileName = file.getOriginalFilename();

        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("파일 확장자를 확인할 수 없습니다.");
        }

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. (png, jpg, jpeg, webp만 사용가능)");
        }
    }
}
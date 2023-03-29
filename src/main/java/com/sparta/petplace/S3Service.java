package com.sparta.petplace;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.petplace.exception.CustomException;
import com.sparta.petplace.exception.Error;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @PostConstruct
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }

    public List<String> upload(List<MultipartFile> multipartFile) {
        List<String> imgUrlList = new ArrayList<>();

        // forEach 구문을 통해 multipartFile로 넘어온 파일들 하나씩 fileNameList에 추가
        for (MultipartFile file : multipartFile) {
            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

             try (InputStream inputStream = file.getInputStream()) {
                s3Client.putObject(new PutObjectRequest(bucket + "/post/image", fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                imgUrlList.add(s3Client.getUrl(bucket + "/post/image", fileName).toString());
            } catch (IOException e) {
                throw new CustomException(Error.FAIL_S3_SAVE);
            }
        }
        return imgUrlList;
    }


    public void deleteFile(String url) {
        String key = extractKeyFromUrl(url);
        try {
            s3Client.deleteObject(bucket, key);
        } catch (AmazonServiceException e) {
            throw new CustomException(Error.FAIL_S3_DELETE);
        }
    }

    private String extractKeyFromUrl(String url) {
        // URL에서 파일 키 추출
        return url.substring(59);
    }

    // 이미지파일명 중복 방지
    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    // 파일 유효성 검사
    private String getFileExtension(String fileName) {
        if (fileName.length() == 0) {
            throw new CustomException(Error.FAIL_S3_SAVE);
        }
        ArrayList<String> fileValidate = new ArrayList<>();
        fileValidate.add(".jpg");
        fileValidate.add(".jpeg");
        fileValidate.add(".png");
        fileValidate.add(".jfif");
        fileValidate.add(".jif");
        fileValidate.add(".jpe");
        fileValidate.add(".JPG");
        fileValidate.add(".JPEG");
        fileValidate.add(".PNG");
        fileValidate.add(".JFIF");
        fileValidate.add(".JIF");
        fileValidate.add(".JPE");
        String idxFileName = fileName.substring(fileName.lastIndexOf("."));
        if (!fileValidate.contains(idxFileName)) {
            throw new CustomException(Error.WRONG_IMAGE_FORMAT);
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    public String uploadMypage(MultipartFile file) {
        String imgUrlList;
        String fileName = createFileName(file.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(new PutObjectRequest(bucket + "/post/image", fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            imgUrlList = (s3Client.getUrl(bucket + "/post/image", fileName).toString());
        } catch (IOException e) {
            throw new CustomException(Error.FAIL_S3_SAVE);
        }

        return imgUrlList;
    }


}
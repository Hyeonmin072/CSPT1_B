package com.myong.backend.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    /**
     *  파일 업로드 후 url 리턴
     */
    public String uploadFile(MultipartFile file,String role, String email){
        if(file.isEmpty())return null;

        try{
            String fileName = file.getOriginalFilename();
            String dir = role + "/" + email + "/" + fileName;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            // S3 풋 오브젝트 저장
            amazonS3.putObject(bucketName,dir,file.getInputStream(),metadata);

            // 디비에 저장될 url 리턴 ex)https://hairism-bucket.s3.{region}.amazons.com/{dir}
            return amazonS3.getUrl(bucketName,dir).toString();


        } catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * S3 파일 삭제
     */
    public boolean deleteFile(String url){
        try{
            String urlPrefix = "https://"+bucketName+".s3."+region+".amazonaws.com/";
            if(!url.startsWith(urlPrefix)){
                System.out.println("제공받은 url:"+url+" 해당 형식이 잘못되었습니다.");
                return false;
            }

            String fileUrl = url.substring(urlPrefix.length());

            if(!amazonS3.doesObjectExist(bucketName,fileUrl)){
                System.out.println("삭제 실패: 파일이 존재하지 않습니다");
                return false;
            }
            amazonS3.deleteObject(bucketName,fileUrl);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}

package com.mefi.backend.api.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.mefi.backend.api.response.FileListResponseDto;
import com.mefi.backend.common.exception.ErrorCode;
import com.mefi.backend.common.exception.Exceptions;
import com.mefi.backend.db.entity.Conference;
import com.mefi.backend.db.entity.MeetingFile;
import com.mefi.backend.db.entity.MeetingFileType;
import com.mefi.backend.db.repository.ConferenceRepository;
import com.mefi.backend.db.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileServiceImpl implements FileService {

    private final AmazonS3Client amazonS3Client;
    private final FileRepository fileRepository;
    private final ConferenceRepository conferenceRepository;
    private final String DIRECTORY = "CONFERENCE";

    @Value("${cloud.aws.s3.bucket}")
    private String bucket; // 버킷 이름

    // 회의록 또는 첨부파일을 저장
    @Transactional
    public void createFile(MultipartFile multipartFile, MeetingFileType type, Long conferenceId) {
        try{

            // 파일 메타 데이터 설정
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentEncoding(multipartFile.getContentType());
            objectMetadata.setContentLength(multipartFile.getSize());

            // 파일 이름 중복을 피하기 위해 원본 파일명에 현재 시간을 반영하여 수정
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HHmmSS"));
            String fileName = time + "_" + multipartFile.getOriginalFilename();
            System.out.println(fileName);

            // S3에 저장될 파일명 생성
            String key =  "CONFERENCE/" + conferenceId + "/" + fileName;

            // S3에 오브젝트 저장
            amazonS3Client.putObject(bucket, key, multipartFile.getInputStream(), objectMetadata);

            // 파일 엔티티 생성 및 연관관계 맺어줌
            MeetingFile meetingFile = new MeetingFile(fileName, type);
            Conference conference = conferenceRepository.findById(conferenceId).get();
            meetingFile.setConference(conference);
            fileRepository.save(meetingFile);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // 파일명을 이용하여 회의록 또는 첨부파일을 삭제
    @Transactional
    public void deleteFile(String fileName, Long conferenceId){
        try{
            // 삭제하고자 하는 파일 메타 데이터 조회
            MeetingFile meetingFile = fileRepository.findByFileName(fileName).orElseThrow(()-> new Exceptions(ErrorCode.FILE_NOT_EXIST));

            // DeleteObjectRequest 생성
            String key = "CONFERENCE/" + conferenceId  + "/" + fileName;

            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, key);

            // AWS S3에서 해당되는 파일 객체 삭제
            amazonS3Client.deleteObject(deleteObjectRequest);

            // 로컬 DB에서 파일 메타 데이터 삭제
            fileRepository.delete(meetingFile);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // 회의록 또는 첨부파일을 다운로드
    public byte[] downloadFile(Long conferenceId, String fileName){
        // KEY 생성
        String key;
        if(conferenceId < 0) key = "PROFILE" + "/" + fileName; // 프로필 이미지
        else key = "CONFERENCE" + "/" + conferenceId + "/" + fileName; // 회의록 또는 첨부파일

        // GetObjectRequest 생성
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, key);

        try{
            // AWS S3에서 해당되는 파일 다운로드
            S3Object s3Object = amazonS3Client.getObject(getObjectRequest);

            // S3 객체를 바이트 배열로 변환
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(inputStream);
            return bytes;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // 특정 회의와 관련된 회의록 및 첨부파일 리스트 조회
    public List<FileListResponseDto> getFiles(Long conferenceId){
        // 회의와 관련된 파일 목록을 조회
        List<MeetingFile> files = fileRepository.findByConferenceId(conferenceId);


        // 파일 엔티티를 DTO로 변환
        List<FileListResponseDto> result  = files.stream()
                .map(f -> new FileListResponseDto((f)))
                .collect(Collectors.toList());
        return result;
    }

    // 프로필 이미지를 업로드
    public String createProfile(MultipartFile profileImage) throws IOException {
        // 파일 메타 데이터 설정
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentEncoding(profileImage.getContentType());
        objectMetadata.setContentLength(profileImage.getSize());

        // S3에 저장될 파일명 생성
        String fileName = profileImage.getOriginalFilename();
        int index = fileName.lastIndexOf(".");
        String ext = fileName.substring(index+1);
        String storeFileName = UUID.randomUUID() + "." + ext;
        String key = "PROFILE/" + storeFileName;

        try{
            // S3에 프로필 이미지 저장
            amazonS3Client.putObject(bucket, key, profileImage.getInputStream(), objectMetadata);
        }catch(Exception e){
            e.printStackTrace();
            throw new IOException("파일 업로드를 실패하였습니다.", e);
        }

        // S3에 저장된 프로필 이미지의 URL 반환
        String fileUrl = amazonS3Client.getUrl(bucket, key).toString();
        return fileUrl;
    }

    // 프로필 이미지를 삭제
    public void deleteProfile(String imageUrl) throws IOException {
       // DeleteObjectRequest 생성
       int idx = imageUrl.indexOf("PROFILE");
       String key = imageUrl.substring(idx);
       DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, key);

       try{
           // AWS S3에서 해당되는 파일 객체 삭제
           amazonS3Client.deleteObject(bucket, key);
       }catch(Exception e){
           e.printStackTrace();
           throw new IOException("프로필 이미지 삭제가 실패하였습니다.", e);
       }
    }

}

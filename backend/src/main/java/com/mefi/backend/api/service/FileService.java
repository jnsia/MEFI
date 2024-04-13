package com.mefi.backend.api.service;

import com.mefi.backend.api.response.FileListResponseDto;
import com.mefi.backend.db.entity.MeetingFileType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {

    // 회의록 또는 첨부파일 업로드
    public void createFile(MultipartFile multipartFile, MeetingFileType type, Long conferenceId);

    // 회의록 또는 첨부파일 삭제
    public void deleteFile(String fileName, Long conferenceId);

    // 회의록 또는 첨부파일을 다운로드
    public byte[] downloadFile(Long conferenceId, String fileName);

    // 특정 회의와 관련된 회의록 및 첨부파일 리스트 조회
    public List<FileListResponseDto> getFiles(Long conferenceId);

    // 프로필 이미지 업로드
    public String createProfile(MultipartFile profileImage) throws IOException;

    // 프로필 이미지를 삭제
    public void deleteProfile(String imageUrl) throws IOException;
}
package com.mefi.backend.api.response;

import com.mefi.backend.db.entity.MeetingFile;
import lombok.Getter;

@Getter
public class FileListResponseDto {

    private String fileName;

    public FileListResponseDto(MeetingFile meetingFile){ // 엔티티를 DTO로 변환
        fileName = meetingFile.getFileName();
    }
}

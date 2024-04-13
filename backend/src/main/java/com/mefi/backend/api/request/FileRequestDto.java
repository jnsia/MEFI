package com.mefi.backend.api.request;

import com.mefi.backend.db.entity.MeetingFileType;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class FileRequestDto {

    // 회의를 진행한 팀 ID
    Long teamId;

    // 회의 ID
    Long conferenceId;

    // 원본 파일명
    String fileName;

    // 파일의 타입
    MeetingFileType type;
}

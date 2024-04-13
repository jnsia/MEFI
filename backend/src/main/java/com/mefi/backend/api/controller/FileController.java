package com.mefi.backend.api.controller;

import com.mefi.backend.api.request.FileRequestDto;
import com.mefi.backend.api.response.FileListResponseDto;
import com.mefi.backend.api.service.FileServiceImpl;
import com.mefi.backend.common.model.BaseResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file")
public class FileController {

    private final FileServiceImpl fileService;

    @PostMapping
    @Operation(summary = "회의록, 첨부파일 업로드", description = "회의록 또는 첨부파일을 S3 서버에 업로드한다.")
    @ApiResponse(responseCode = "200", description = "파일 업로드 성공")
    public ResponseEntity<? extends BaseResponseBody> createFile(
            @RequestPart(value="file") @Parameter(name = "file", description = "업로드 하고자 하는 파일") MultipartFile file,
            @RequestPart(value="fileRequestDto") @Parameter(name = "fileUploadRequestDto", description = "파일 및 회의 타입") FileRequestDto fileRequestDto){

        log.info("Conference ID : {}", fileRequestDto.getConferenceId());
        log.info("File Type : {}", fileRequestDto.getType());

        fileService.createFile(file,fileRequestDto.getType(),fileRequestDto.getConferenceId());
        return ResponseEntity.status(200).body(BaseResponseBody.of(0, "SUCCESS"));
    }

    @DeleteMapping("/{conferenceId}")
    @Operation(summary = "회의록, 첨부파일 삭제", description = "원본 파일명과 회의 ID로 회의록 또는 첨부파일을 삭제한다.")
    @ApiResponse(responseCode = "200", description = "파일 삭제 성공")
    public ResponseEntity<? extends BaseResponseBody> deleteFile(
            @RequestParam(name = "fileName") @Parameter(name = "fileName", description = "삭제하려는 파일의 원본 파일명") String fileName,
            @PathVariable(name = "conferenceId") @Parameter(name = "conferenceId", description = "삭제하려는 파일이 속한 회의ID") Long conferenceId){
        fileService.deleteFile(fileName, conferenceId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 회의록, 첨부파일 다운로드
    @GetMapping("/download/{conferenceId}")
    @Operation(summary = "회의록, 첨부파일 다운로드", description = "원본 파일명과 회의 ID로 회의록 또는 첨부파일을 다운로드.")
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable(name = "conferenceId") @Parameter(name="conferenceId", description = "다운로드 하려는 파일이 속한 회의 ID") Long conferenceId,
            @RequestParam(value = "fileName") @Parameter(name = "fileName", description = "다운로드 하려는 파일명") String fileName) throws IOException {
        // 파일을 바이트 배열로 다운로드
        byte[] filebytes = fileService.downloadFile(conferenceId, fileName);

        // HTTP 헤더 값 설정
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentLength(filebytes.length);

        fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+","%20");
        httpHeaders.setContentDispositionFormData("attachment", fileName);

        return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body(filebytes);
    }

    @GetMapping("/all/{conferenceId}")
    @Operation(summary = "회의 관련 문서 리스트 조회", description = "회의와 관련된 회의록 또는 첨부파일 리스트를 가져온다.")
    @ApiResponse(responseCode = "200", description = "문서 리스트 반환 성공")
    public ResponseEntity<? extends BaseResponseBody> getFileList(
            @PathVariable("conferenceId") @Parameter(name = "conferenceId", description = "조회하려는 회의 ID") Long conferenceId){
        List<FileListResponseDto> files = fileService.getFiles(conferenceId);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseBody.of(0,files));
    }

}

package com.mefi.backend.db.repository;

import com.mefi.backend.db.entity.Conference;
import com.mefi.backend.db.entity.MeetingFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<MeetingFile, Long> {

    // 파일명으로 메타 데이터 조회
    Optional<MeetingFile> findByFileName(String fileName);

    // 파일에 대한 메타 데이터 정보 삭제
    void delete(MeetingFile meetingFile);

    // 특정 회의와 관련된 파일 메타 데이터 조회
    @Query("select m from MeetingFile m where m.conference.id = :conferenceId")
    List<MeetingFile> findByConferenceId(@Param("conferenceId") Long conferenceId);
}

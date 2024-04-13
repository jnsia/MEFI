package com.mefi.backend.api.service;

import com.mefi.backend.api.response.NotiResponseDto;
import com.mefi.backend.db.entity.Noti;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface NotiService {

    // SSE 연결 생성
    SseEmitter createSseConnection(Long userId, String lastEventId);

    // 특정 사용자의 알림 전체 조회
    List<NotiResponseDto> getNotis(Long userId);

    // 특정 알림의 읽음 처리
    NotiResponseDto readNoti(Long alarmId);

    // 특정 Emitter로 알림 전송
    void sendNoti(SseEmitter emitter, String eventId, String emitterId, Noti noti);

    // 특정 사용자에게 알림 전송
    @Transactional
    void sendNotiForUser(Long userId, String sender, String message);

    // 팀에 소속된 사용자 모두에게 알림 전송
    void sendNotiForTeam(Long teamId, String sender, String message);

    int readNotiAll(Long userId);
}

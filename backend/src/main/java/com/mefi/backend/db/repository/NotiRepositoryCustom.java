package com.mefi.backend.db.repository;

import com.mefi.backend.db.entity.Noti;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface NotiRepositoryCustom {
    // SSE Emitter 저장
    public SseEmitter saveEmitter(String emitterId, SseEmitter emitter);

    // SSE Emitter 삭제
    public void deleteEmitterById(String emitterId);

    // 특정 유저의 SSE Emitter 모두 삭제
    public void deleteEmittersByUserId(String userId);

    // 특정 유저의 EventCache 모두 삭제
    public void deleteEventCacheByUserId();

    // 특정 유저의 SSE Emitter 모두 조회
    public Map<String, SseEmitter> findSseEmittersById(String userId);

    // 특정 유저의 EventCache 모두 조회
    public Map<String, Noti> findEventCachesByUserId(String userId);

    // 신규 EventCache 저장
    public void saveEventCache(String key, Noti noti);

}

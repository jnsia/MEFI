package com.mefi.backend.db.repository;

import com.mefi.backend.db.entity.Noti;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class NotiRepositoryCustomImpl implements NotiRepositoryCustom {

    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>(); // 실시간 알림을 전송하기 위해 서버에서 SSE Emitter 관리
    private final ConcurrentHashMap<String, Noti> eventCaches = new ConcurrentHashMap<>(); // 네트워크 오류 등으로 인한 알림 유실을 위한 캐시


    @Override
    public SseEmitter saveEmitter(String emitterId, SseEmitter emitter) {
        emitters.put(emitterId, emitter);
        log.info("Emitter Count : {}", emitters.size());
        return emitter;
    }

    @Override
    public void deleteEmitterById(String emitterId) {
        emitters.remove(emitterId);
    }

    @Override
    public void deleteEmittersByUserId(String userId) {
        emitters.forEach(
                (key, emitter)->{
                    if(key.startsWith(userId)){
                        emitters.remove(key);
                    }
                }
        );
    }

    @Override
    public void deleteEventCacheByUserId() {
        // 3개월이 지난 EventCache는 삭제
        eventCaches.forEach(
                (key, noti) ->{
                    LocalDateTime createdTime = noti.getCreatedTime().plusMonths(3L); // 생성 후 3시간이 지난 알림
                    LocalDateTime now = LocalDateTime.now(); // 현재 시간
                    if(now.compareTo(createdTime) <= 0){ // 생성한 지 3개월이 지난 알림은 캐시에서 제거
                        eventCaches.remove(noti);
                    }
                }
        );
    }

    @Override
    public Map<String, SseEmitter> findSseEmittersById(String userId) {
        return emitters.entrySet().stream()
                .filter(entry->entry.getKey().startsWith(userId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, Noti> findEventCachesByUserId(String userId) {
        return eventCaches.entrySet().stream()
                .filter(entry->entry.getKey().startsWith(userId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void saveEventCache(String key, Noti noti){
        eventCaches.put(key, noti);
    }
}

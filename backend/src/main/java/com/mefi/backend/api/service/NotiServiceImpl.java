package com.mefi.backend.api.service;

import com.mefi.backend.api.response.MemberResDto;
import com.mefi.backend.api.response.NotiResponseDto;
import com.mefi.backend.common.exception.ErrorCode;
import com.mefi.backend.common.exception.Exceptions;
import com.mefi.backend.db.entity.*;
import com.mefi.backend.db.repository.NotiRepository;
import com.mefi.backend.db.repository.TeamUserRepository;
import com.mefi.backend.db.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NotiServiceImpl implements NotiService{

    private final NotiRepository notiRepository;
    private final TeamUserRepository teamUserRepository;
    private final UserRepository userRepository;
    private static final Long DEFAULT_TIMEOUT = Long.MAX_VALUE; // SSE 최대 연결 시간

    // SSE 연결 생성
    @Override
    public SseEmitter createSseConnection(Long userId, String lastEventId) {
        // SSE Emitter ID 생성
        String emitterId = makeTimeIncludeEventId(String.valueOf(userId));

        // SSE Emitter 객체 생성
        SseEmitter sseEmitter = notiRepository.saveEmitter(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        // SSE 콜백 함수 지정
        sseEmitter.onCompletion(()->deleteEmittersForUser(userId));  // 비동기 처리 완료
        sseEmitter.onTimeout(()->deleteEmittersForUser(userId));  // 타임 아웃
        sseEmitter.onError(e->deleteEmittersForUser(userId)); // 오류

        // 503 에러 방지를 위해 더미 이벤트 전송
        String eventId = makeTimeIncludeEventId(String.valueOf(userId));
        sendNoti(sseEmitter, eventId, emitterId, Noti.builder().message("[Created] Event Stream : userID="+userId).status(false).build());

        // 네트워크 오류 등으로 인한 미수신 알림이 있다면 클라이언트에 전송
        if(!lastEventId.isEmpty()){
            Map<String, Noti> events = notiRepository.findEventCachesByUserId(String.valueOf(userId));
            events.entrySet().stream()
                    .filter(entry->lastEventId.compareTo(entry.getKey())<0) // 미수신 알림 판별
                    .forEach(entry->sendNoti(sseEmitter, entry.getKey(), emitterId, entry.getValue())); // 알림 전송
        }

        // SSE Emitter 객체 반환
        return sseEmitter;
    }

    // 알림 전송
    @Override
    public void sendNoti(SseEmitter emitter, String eventId, String emitterId, Noti noti) {
        try{
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .name("sse")
                    .data(new NotiResponseDto(noti))
                    .build());
        }catch(IOException e){
            e.printStackTrace();
            log.info("알림 전송 중 오류가 발생하였습니다");
            notiRepository.deleteEmitterById(emitterId);
        }
    }

    // 특정 유저에게 알림 전송
    @Override
    @Transactional
    public void sendNotiForUser(Long userId, String sender, String message) {
        // Event ID 생성
        String eventId = makeTimeIncludeEventId(String.valueOf(userId));

        // 해당 유저 조회
        User user = userRepository.findById(userId).orElseThrow(()-> new Exceptions(ErrorCode.USER_NOT_EXIST));

        // DB 저장
        Noti noti = Noti.builder()
                .user(user)
                .message(message)
                .createdTime(LocalDateTime.now())
                .status(false)
                .sender(sender)
                .build();
        notiRepository.save(noti);

        // 로그인한 특정 사용자에 대한 모든 SSE Emitter 조회 (여러 탭, 디바이스 고려)
        Map<String, SseEmitter> emitters = notiRepository.findSseEmittersById(String.valueOf(userId));

        // 모든 SSE Emitter에 대해 알림 전송
        emitters.forEach((key, emitter) -> {
            // 이벤트 캐시 저장
            notiRepository.saveEventCache(key, noti);
            // 알림 전송
            sendNoti(emitter, eventId, key, noti);
        });


    }

    // 팀에 소속된 모든 유저에게 알림 전송
    @Override
    @Transactional
    public void sendNotiForTeam(Long teamId, String sender, String message) {
        // 팀원 목록 조회
        List<UserTeam> users = teamUserRepository.findAllByTeamId(teamId);

        // 각 팀원에 대해 알림 전송
        for(UserTeam user : users){
            // 리더는 제외
            if(user.getRole().equals(UserRole.LEADER)) continue;
            log.info("user : {}", user.getUser().getId());
            sendNotiForUser(user.getUser().getId(), sender, message);
        }
    }

    // 해당 유저가 읽지 않은 모든 알림 조회
    @Override
    public List<NotiResponseDto> getNotis(Long userId) {
        // 유저 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new Exceptions(ErrorCode.USER_NOT_EXIST));

        // 특정 사용자의 알림 전체 조회
        List<Noti> notis = notiRepository.findNotiByUserAndStatusIsFalse(user);
        log.info("Alarm Size : {}", notis.size());

        // 엔티티를 DTO로 변환하여 리턴
       return notis.stream()
                .map(n -> new NotiResponseDto(n))
                .collect(Collectors.toList());
    }

    // 특정 알림 읽음 처리
    @Override
    @Transactional
    public NotiResponseDto readNoti(Long alarmId) {
        Noti noti = notiRepository.findNotiById(alarmId).orElseThrow(()->new Exceptions(ErrorCode.USER_NOT_EXIST));
        noti.read();
        return new NotiResponseDto(noti);
    }

    // 전체 알림 읽음 처리 메소드
    @Transactional
    public int readNotiAll(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new Exceptions(ErrorCode.USER_NOT_EXIST));
        return notiRepository.readNotiAllByUser(user);
    }

    // 이벤트 ID 생성 메소드
    private String makeTimeIncludeEventId(String userId){
        return userId + "_" + System.currentTimeMillis();
    }

    // 특정 유저의 Emitter 삭제 메소드
    private void deleteEmittersForUser(Long userId){
        log.info("[Removed] All Emitters of User : {}", userId);
        notiRepository.deleteEmittersByUserId(String.valueOf(userId));
    }

    // 매일 새벽 5시마다 3개월 지난 이벤트 캐시를 제거하는 메소드
    @Scheduled(cron="0 0 5 * * ?")
    protected void deleteEventCaches(){
        notiRepository.deleteEventCacheByUserId();
        log.info("[Deleted]" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "기준으로 3개월 지난 EventCache 삭제 완료");
    }
}

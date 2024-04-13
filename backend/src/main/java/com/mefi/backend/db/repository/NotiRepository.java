package com.mefi.backend.db.repository;

import com.mefi.backend.db.entity.Noti;
import com.mefi.backend.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotiRepository extends JpaRepository<Noti, Long>, NotiRepositoryCustom {

    // 새로운 알림 생성
    Noti save(Noti noti);

    // 유저가 읽지 않은 알림 모두 읽음 처리
    @Modifying(clearAutomatically = true)
    @Query("update Noti n set n.status=true where n.status=false and n.user=:user")
    int readNotiAllByUser(@Param("user") User user);

    // 알림 ID로 조회
    Optional<Noti> findNotiById(Long alarmId);

    // 유저 ID로 읽지 않은 알림 모두 조회
    List<Noti> findNotiByUserAndStatusIsFalse(User user);

}

package com.mefi.backend.db.repository;

import com.mefi.backend.api.response.MemberResDto;
import com.mefi.backend.api.response.QMemberResDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.mefi.backend.db.entity.QUser.user;

public class UserRepositoryImpl implements UserRepositoryCustom {

    // JPAQueryFactory : Querydsl을 사용하여 JPA에서 타입-세이프한 쿼리를 생성하고 실행하기 위한 클래스
    private final JPAQueryFactory queryFactory;

    // EntityManager 인스턴스를 매개변수로 받아, 이를 사용하여 JPAQueryFactory 인스턴스를 초기화
    public UserRepositoryImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<MemberResDto> findByKeyWord(Long userId,String keyword) {

        // Querydsl 반환
        return queryFactory
                .select(new QMemberResDto(user.id, user.email, user.name, user.position, user.dept))
                .from(user)
                .where(
                        (user.email.like("%"+keyword+"%").or(user.name.like("%"+keyword+"%")))
                        .and(user.id.ne(userId))
                )
                .fetch();
    }
}
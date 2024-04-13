package com.mefi.backend.db.repository;

import com.mefi.backend.api.response.MemberResDto;

import java.util.List;

public interface UserRepositoryCustom {

    // 검색어로 유저 조회
    List<MemberResDto> findByKeyWord(Long userId,String keyword);
}

package com.mefi.backend.api.service;

import com.mefi.backend.api.request.*;
import com.mefi.backend.api.response.MemberResDto;
import com.mefi.backend.api.response.UserModifyAllResDto;
import com.mefi.backend.db.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {

    // 회원가입
    void join(JoinReqDto joinReqDto);

    // 회원탈퇴
    void withdraw(User user, UserWithdrawReqDto userWithdrawReqDto);

    // 식별 ID로 유저 조회
    User findById(Long id);

    // 검색어로 회원 조회
    List<MemberResDto> getSearchUsers(Long userId, String keyword);

    // 회원 정보 전체 수정
    UserModifyAllResDto modifyUserInfoAll(Long userId, UserModifyAllReqDto userModifyAllReqDto, MultipartFile profileImg) throws IOException;
    
    // 회원 정보 부분 수정
    void modifyUserInfo(Long userId, UserModifyReqDto userModifyReqDto);

    // 회원 기본 비밀번호 수정
    void modifyUserPassword(Long id, UserModifyPasswordReqDto userModifyPasswordReqDto);

    // 찾기 인증 후 비밀번호 수정
    void recoveryUserPassword(UserPasswordRecoveryReqDto userPasswordRecoveryReqDto);
}

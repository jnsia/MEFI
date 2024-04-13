package com.mefi.backend.common.auth;

import com.mefi.backend.db.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.ArrayList;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    // 사용자에게 부여된 권한 목록 반환 (직책을 반환)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {
                return user.getPosition();
            }
        });

        return collection;
    }

    // 암호 반환
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 아이디 반환
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // 계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {

        // 만료 X
        return true;
    }

    // 계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked() {

        // 잠금 X
        return true;
    }

    // 자격 증명 유효 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {

        // 자격 증명 유효
        return true;
    }

    // 사용 가능 여부 반환
    @Override
    public boolean isEnabled() {

        // 사용 가능
        return true;
    }

    // 식별 아이디 반환
    public Long getUserId() {
        return user.getId();
    }
}
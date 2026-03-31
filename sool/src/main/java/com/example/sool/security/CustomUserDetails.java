package com.example.sool.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.sool.dto.UserDto;

import lombok.Getter;

@Getter
public class CustomUserDetails implements UserDetails {

    private final UserDto user;

    public CustomUserDetails(UserDto user) {
        this.user = user;
    }

    public Integer getUserId() {
        return user.getUserId();
    }

    public String getLoginId() {
        return user.getLoginId();
    }

    public String getName() {
        return user.getName();
    }

    public String getRoleCode() {
        return user.getRoleCode();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRoleCode()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getLoginId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !"Y".equals(user.getIsDeleted());
    }
}
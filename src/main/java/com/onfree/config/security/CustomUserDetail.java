package com.onfree.config.security;

import com.onfree.core.entity.user.Role;
import com.onfree.core.entity.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;

public class CustomUserDetail extends org.springframework.security.core.userdetails.User {
    User user;

    public User getUser() {
        return user;
    }

    public CustomUserDetail(User user) {
        super(user.getEmail(), user.getPassword(), getAuthority(user.getRole()));
        this.user=user;
    }

    private static Collection<? extends GrantedAuthority> getAuthority(Role role) {
        return Set.of(new SimpleGrantedAuthority(role.getValue()));
    }
}

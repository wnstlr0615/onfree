package com.onfree.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "jwt_refresh_token")
public class JWTRefreshToken {
    @Id
    private String userName;

    @Column(nullable = false, unique = true)
    private String refreshToken;

    public void updateToken(String newRefreshToken) {
        refreshToken=newRefreshToken;
    }
}

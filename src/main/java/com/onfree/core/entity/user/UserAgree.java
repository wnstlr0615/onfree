package com.onfree.core.entity.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAgree {
    @Column(nullable = false)
    private Boolean service; // 서비스 동의

    @Column(nullable = false)
    private Boolean policy; // 정책 동의

    @Column(nullable = false)
    private Boolean personalInfo; // 개인정보 동의

    @Column(nullable = false)
    private Boolean advertisement; // 광고 동의
}

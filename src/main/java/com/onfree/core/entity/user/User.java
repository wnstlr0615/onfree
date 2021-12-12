package com.onfree.core.entity.user;

import com.onfree.core.model.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.lang.annotation.Inherited;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public abstract class User extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String name; //회원이름

    @Column(nullable = false, length = 100)
    private String email; // 회원이메일(아이디)

    @Column(nullable = false, length = 100)
    private String password; //비밀번호

    @Column(nullable = false, length = 10)
    private String newsAgency; //통신사

    @Column(nullable = false, length = 14)
    private String phoneNumber; // 핸드폰번호

    @Embedded
    private BankInfo bankInfo; // 은행정보

    @Embedded
    private UserAgree userAgree; // 사용자동의정보

    @Column(nullable = false)
    private Boolean adultCertification; // 성인인증

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 5)
    private Gender gender; // 성별

    @Column(nullable = false)
    private String profileImage; //프로필이미지

    @Column(nullable = false)
    private Boolean deleted;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role; // 권한

    public void encryptPassword(String encryptPassword){
        this.password=encryptPassword;
    }

    protected void setDeleted() {
        this.deleted=true;
    }
}

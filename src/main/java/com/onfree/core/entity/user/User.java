package com.onfree.core.entity.user;

import com.onfree.common.model.BaseTimeEntity;
import com.onfree.core.dto.user.artist.MobileCarrier;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class User extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String name; //회원이름

    @Column(nullable = false, length = 100)
    private String nickname; //회원이름

    @Column(nullable = false, length = 100)
    private String email; // 회원이메일(아이디)

    @Column(nullable = false, length = 100)
    private String password; //비밀번호

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private MobileCarrier mobileCarrier; //통신사

    @Column(nullable = false, length = 14)
    private String phoneNumber; // 핸드폰번호

    @Embedded
    private BankInfo bankInfo; // 은행정보

    @Embedded
    private UserAgree userAgree; // 사용자동의정보

    @Embedded
    @Column(nullable = false)
    private UserNotification userNotification;

    @Column(nullable = false)
    private Boolean adultCertification; // 성인인증

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 5)
    private Gender gender; // 성별

    @Column(nullable = false)
    private String profileImage; //프로필이미지

    @Column(nullable = false)
    private Boolean deleted;

    @Column
    private LocalDateTime deletedTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role; // 권한

    //== 생성 메서드 ==//
    public User(Long userId, String name, String nickname, String email, String password, MobileCarrier mobileCarrier, String phoneNumber, BankInfo bankInfo, UserAgree userAgree, Boolean adultCertification, Gender gender, String profileImage, Boolean deleted, Role role) {
        this.userId = userId;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.mobileCarrier = mobileCarrier;
        this.phoneNumber = phoneNumber;
        this.bankInfo = bankInfo;
        this.userAgree = userAgree;
        this.adultCertification = adultCertification;
        this.gender = gender;
        this.profileImage = profileImage;
        this.deleted = deleted;
        this.role = role;
        this.userNotification = UserNotification.allTrueUserNotification();
    }

    //== 비즈니스 메서드 ==//
    public void encryptPassword(String encryptPassword){
        this.password=encryptPassword;
    }


    public boolean isEqualsUserId(User otherUser){
        return this.getUserId().equals(otherUser.getUserId());
    }
    protected void setDeleted() {
        this.deleted=true;
        this.deletedTime=LocalDateTime.now();
    }

    protected void update(BankInfo bankInfo, boolean adultCertification, String nickname, MobileCarrier mobileCarrier, String phoneNumber, String profileImage) {
        this.bankInfo= bankInfo;
        this.adultCertification=adultCertification;
        this.nickname= nickname;
        this.mobileCarrier= mobileCarrier;
        this.phoneNumber= phoneNumber;
        this.profileImage= profileImage;
    }



    public void resetPassword(String bcryptPassword) {
        password=bcryptPassword;
    }

    public void updateNotification(UserNotification userNotification) {
        this.userNotification = userNotification;
    }

    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }
}

package com.onfree.core.entity.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@NoArgsConstructor
@DiscriminatorValue(value = "N")
public class NormalUser extends User{
    @Builder
    public NormalUser(Long userId, String name, String email, String password, String newsAgency, String phoneNumber, BankInfo bankInfo, UserAgree userAgree, Boolean adultCertification, Gender gender, String profileImage, Role role) {
        super(userId, name, email, password, newsAgency, phoneNumber, bankInfo, userAgree, adultCertification, gender, profileImage, role);
    }
    public void encryptPassword(String encryptPassword){
        super.encryptPassword(
                encryptPassword
        );
    }
}

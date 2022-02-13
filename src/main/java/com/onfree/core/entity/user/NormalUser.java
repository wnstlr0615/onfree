package com.onfree.core.entity.user;

import com.onfree.core.dto.user.normal.UpdateNormalUserDto;
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
    public NormalUser(Long userId, String name, String nickname, String email, String password, String newsAgency, String phoneNumber, BankInfo bankInfo, UserAgree userAgree, Boolean adultCertification, Gender gender, String profileImage, Boolean deleted, Role role) {
        super(userId, name, nickname, email, password, newsAgency, phoneNumber, bankInfo, userAgree, adultCertification, gender, profileImage, deleted, role);
    }

    public void encryptPassword(String encryptPassword){
        super.encryptPassword(
                encryptPassword
        );
    }

    public void setDeleted() {
        super.setDeleted();
    }

    public void update(UpdateNormalUserDto.Request request) {
        BankInfo bankInfo= BankInfo.builder()
                .bankName(request.getBankName())
                .accountNumber(request.getAccountNumber())
                .build();
        super.update(bankInfo, request.getAdultCertification(), request.getNickname(), request.getNewsAgency(), request.getPhoneNumber(), request.getProfileImage());
    }
}

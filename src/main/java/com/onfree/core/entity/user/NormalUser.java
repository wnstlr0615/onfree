package com.onfree.core.entity.user;

import com.onfree.core.dto.user.artist.MobileCarrier;
import com.onfree.core.dto.user.normal.UpdateNormalUserDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@NoArgsConstructor
@DiscriminatorValue(value = "N")
public class NormalUser extends User{

    @Builder
    public NormalUser(Long userId, String name, String nickname, String email, String password, MobileCarrier mobileCarrier, String phoneNumber, BankInfo bankInfo, UserAgree userAgree, Boolean adultCertification, Gender gender, String profileImage, Boolean deleted, Role role) {
        super(userId, name, nickname, email, password, mobileCarrier, phoneNumber, bankInfo, userAgree, adultCertification, gender, profileImage, deleted, role);
    }

    public void encryptPassword(String encryptPassword){
        super.encryptPassword(
                encryptPassword
        );
    }

    public void setDeleted() {
        super.setDeleted();
    }

    public void update(BankInfo bankInfo, Boolean adultCertification, String nickname, MobileCarrier mobileCarrier, String phoneNumber, String profileImage) {
        super.update(bankInfo, adultCertification, nickname, mobileCarrier, phoneNumber, profileImage);
    }
}

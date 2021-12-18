package com.onfree.core.dto.user;

import com.onfree.core.entity.user.NormalUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NormalUserDetail {
    @ApiModelProperty(value = "사용자 이름", example = "김모씨")
    private final String name;

    @ApiModelProperty(value = "사용자 닉네임", example = "온프리프리")
    private final String nickname;

    @ApiModelProperty(value = "이메일주소(사용자 아이디)", example = "jun@naver.com")
    private final String email;

    @ApiModelProperty(value = "통신사", example = "SKT", allowableValues = "KT,SKT,LG")
    private final String newsAgency; //통신사


    @ApiModelProperty(value = "핸드폰번호", example = "010-0000-0000")
    private final String phoneNumber;

    @ApiModelProperty(value = "은행명", example = "BUSAN_BANK" ,allowableValues = "${BankName.joinString()}" )
    private final String bankName;

    @ApiModelProperty(value = "계좌번호", example = "123456-456789-12")
    private final String accountNumber;

    @ApiModelProperty(value = "서비스동의", example = "true")
    private final Boolean serviceAgree;

    @ApiModelProperty(value = "정책동의", example = "true")
    private final Boolean policyAgree;

    @ApiModelProperty(value = "개인정보동의", example = "true")
    private final Boolean personalInfoAgree;

    @ApiModelProperty(value = "광고동의", example = "true")
    private final Boolean advertisementAgree;

    @ApiModelProperty(value = "성인인증", example = "true")
    private final Boolean adultCertification;

    @ApiModelProperty(value = "성별", example = "${Gender.joinString()}")
    private final String gender;

    @ApiModelProperty(value = "프로필 URL", example = "http://onfree.io/images/546456498")
    private final String profileImage;

    public static NormalUserDetail fromEntity(NormalUser entity) {
        return NormalUserDetail.builder()
                .adultCertification(entity.getAdultCertification())
                .email(entity.getEmail())
                .gender(entity.getGender().getName())
                .name(entity.getName())
                .nickname(entity.getNickname())
                .newsAgency(entity.getNewsAgency())
                .phoneNumber(entity.getPhoneNumber())
                .bankName(entity.getBankInfo().getBankName().getBankName())
                .accountNumber(entity.getBankInfo().getAccountNumber())
                .advertisementAgree(entity.getUserAgree().getAdvertisement())
                .personalInfoAgree(entity.getUserAgree().getPersonalInfo())
                .policyAgree(entity.getUserAgree().getPolicy())
                .serviceAgree(entity.getUserAgree().getService())
                .profileImage(entity.getProfileImage())
                .build();
    }
}

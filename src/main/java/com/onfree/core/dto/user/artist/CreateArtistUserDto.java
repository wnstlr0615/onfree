package com.onfree.core.dto.user.artist;

import com.fasterxml.jackson.annotation.JsonValue;
import com.onfree.core.entity.user.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.*;

public class CreateArtistUserDto {
    @Getter
    @Builder
    @ApiModel(value = "CreateArtistUserDto_Request")
    public static class Request {
        @ApiModelProperty(value = "이메일주소(사용자 아이디)", example = "jun@naver.com")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @NotBlank(message = "이메일은 공백일 수 없습니다.")
        private final String email;

        @ApiModelProperty(value = "사용자 이름", example = "김모씨")
        @NotBlank(message = "이름은 공백일수 없습니다.")
        private final String name;

        @ApiModelProperty(value = "사용자 닉네임", example = "온프리대박기원")
        @NotBlank(message = "닉네임은 공백일수 없습니다.")
        private final String nickname;

        @ApiModelProperty(value = "사용자 비밀번호", example = "!abcdefghijk123")
        @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
        private final String password;

        @ApiModelProperty(value = "통신사", example = "SKT", allowableValues = "KT,SKT,LG")
        @NotNull(message = "통신사는 공백일 수 없습니다.")
        private final MobileCarrier mobileCarrier; //통신사

        @ApiModelProperty(value = "핸드폰번호", example = "010-0000-0000")
        @NotBlank(message = "핸드폰번호는 공백일 수 없습니다.")
        @Pattern(regexp = "^01(0|1|[6-9])-(\\d{3}|\\d{4})-\\d{4}$", message="핸드폰번호 패턴이 올바르지 않습니다.")
        private final String phoneNumber;

        @ApiModelProperty(value = "은행명", example = "BUSAN_BANK" ,allowableValues = " ${BankName.joinString()}" )
        @NotNull(message = "은행명은 공백일 수 없습니다.")
        private final BankName bankName;

        @ApiModelProperty(value = "계좌번호", example = "123456-456789-12")
        @NotBlank(message = "계좌번호는 공백일 수 없습니다.")
        private final String accountNumber;
        //TODO 계좌번호 패턴 검사

        @AssertTrue(message = "서비스 동의를 하지 않으면 회원가입이 불가합니다.")
        @ApiModelProperty(value = "서비스동의", example = "true")
        @NotNull(message = "서비스 동의 체크는 필수 입니다.")
        private final Boolean serviceAgree;

        @ApiModelProperty(value = "정책동의", example = "true")
        @AssertTrue(message = "정책 동의를 하지 않으면 회원가입이 불가합니다.")
        @NotNull(message = "정책 동의 체크는 필수 입니다.")
        private final Boolean policyAgree;

        @ApiModelProperty(value = "개인정보동의", example = "true")
        @AssertTrue(message = "개인정보 동의를 하지않으면 회원가입이 불가합니다.")
        @NotNull(message = "개인정보 동의 체크는 필수 입니다.")
        private final Boolean personalInfoAgree;

        @ApiModelProperty(value = "광고동의", example = "true")
        @NotNull(message = "광고 동의 체크는 필수 입니다.")
        private final Boolean advertisementAgree;

        @ApiModelProperty(value = "성인인증", example = "true")
        @NotNull(message = "성인인증 체크는 필수 입니다.")
        private final Boolean adultCertification;

        @ApiModelProperty(value = "성별", example = "MAN", allowableValues = "${Gender.joinString()}")
        @NotNull(message = "성별입력은 필수 입니다.")
        private final Gender gender;

        @ApiModelProperty(value = "프로필 URL", example = "http://onfree.io/images/546456498")
        @NotBlank(message = "프로필 URL 은 필수입니다.")
        private final String profileImage;

        @ApiModelProperty(value = "포트폴리오 개인룸 URL", example = "http://onfree.io/portfoliourl/546456498")
        @NotBlank(message = "포트폴리오 개인룸 URL 은 필수입니다.")
        private final String portfolioUrl;

        public ArtistUser toEntity() {
            BankInfo bankInfo = BankInfo.builder()
                    .bankName(bankName)
                    .accountNumber(accountNumber)
                    .build();
            UserAgree userAgree = UserAgree.builder()
                    .advertisement(advertisementAgree)
                    .personalInfo(personalInfoAgree)
                    .service(serviceAgree)
                    .policy(policyAgree)
                    .build();
            return ArtistUser.builder()
                    .adultCertification(adultCertification)
                    .email(email)
                    .nickname(nickname)
                    .password(password)
                    .gender(gender)
                    .name(name)
                    .mobileCarrier(mobileCarrier)
                    .phoneNumber(phoneNumber)
                    .bankInfo(bankInfo)
                    .userAgree(userAgree)
                    .adultCertification(adultCertification)
                    .profileImage(profileImage)
                    .portfolioUrl(portfolioUrl)
                    .deleted(false)
                    .role(Role.ARTIST)
                    .build();
        }
    }

    @Getter
    @Builder
    @ApiModel(value = "CreateArtistUserDto_Response")
    public static class Response extends RepresentationModel<Response> {
        @ApiModelProperty(value = "사용자 이름", example = "김모씨")
        private final String name;

        @ApiModelProperty(value = "사용자 닉네임", example = "온프리대박기원")
        @NotBlank(message = "닉네임은 공백일수 없습니다.")
        private final String nickname;

        @ApiModelProperty(value = "이메일주소(사용자 아이디)", example = "jun@naver.com")
        private final String email;

        @ApiModelProperty(value = "통신사", example = "SKT", allowableValues = "KT,SKT,LG")
        private final MobileCarrier mobileCarrier; //통신사


        @ApiModelProperty(value = "핸드폰번호", example = "010-0000-0000")
        private final String phoneNumber;

        @ApiModelProperty(value = "은행명", example = "BUSAN_BANK" ,allowableValues = "${BankName.joinString()}" )
        private final BankName bankName;

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
        private final Gender gender;

        @ApiModelProperty(value = "프로필 URL", example = "http://onfree.io/images/546456498")
        private final String profileImage;

        @ApiModelProperty(value = "포트폴리오 개인룸 URL", example = "http://onfree.io/portfoliourl/546456498")
        @NotBlank(message = "포트폴리오 개인룸 URL 은 필수입니다.")
        private final String portfolioUrl;

        public static Response fromEntity(ArtistUser entity) {
            return Response.builder()
                    .adultCertification(entity.getAdultCertification())
                    .email(entity.getEmail())
                    .gender(entity.getGender())
                    .name(entity.getName())
                    .nickname(entity.getNickname())
                    .mobileCarrier(entity.getMobileCarrier())
                    .phoneNumber(entity.getPhoneNumber())
                    .bankName(entity.getBankInfo().getBankName())
                    .accountNumber(entity.getBankInfo().getAccountNumber())
                    .advertisementAgree(entity.getUserAgree().getAdvertisement())
                    .personalInfoAgree(entity.getUserAgree().getPersonalInfo())
                    .policyAgree(entity.getUserAgree().getPolicy())
                    .serviceAgree(entity.getUserAgree().getService())
                    .profileImage(entity.getProfileImage())
                    .portfolioUrl(entity.getPortfolioRoom().getPortfolioRoomURL())
                    .build();
        }
    }
}

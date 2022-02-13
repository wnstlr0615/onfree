package com.onfree.common.aop;

import com.amazonaws.services.s3.AmazonS3Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onfree.anotation.WithAdminUser;
import com.onfree.anotation.WithArtistUser;
import com.onfree.anotation.WithNormalUser;
import com.onfree.common.error.code.ErrorCode;
import com.onfree.common.error.code.GlobalErrorCode;
import com.onfree.core.dto.user.UpdateUserNotificationDto;
import com.onfree.core.dto.notice.CreateNoticeDto;
import com.onfree.core.dto.notice.UpdateNoticeDto;
import com.onfree.core.dto.question.CreateQuestionDto;
import com.onfree.core.dto.question.UpdateQuestionDto;
import com.onfree.core.dto.user.artist.CreateArtistUserDto;
import com.onfree.core.dto.user.artist.UpdateArtistUserDto;
import com.onfree.core.dto.user.normal.CreateNormalUserDto;
import com.onfree.core.dto.user.normal.UpdateNormalUserDto;
import com.onfree.core.entity.user.*;
import com.onfree.core.repository.UserRepository;
import com.onfree.core.service.LoginService;
import com.onfree.utils.JWTUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.onfree.common.constant.SecurityConstant.BEARER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties =
        "spring.config.location=" +
        "classpath:application.yml" +
        ",classpath:aws.yml")
@ActiveProfiles("test")
@AutoConfigureMockMvc(print = MockMvcPrint.LOG_DEBUG)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ValidateAopTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LoginService loginService;

    @Autowired
    AmazonS3Client amazonS3Client;

    @Autowired
    JWTUtil jwtUtil;

    String normalUserAccessToken;
    String artistUserAccessToken;

    @BeforeAll
    void init(){
        saveNormalUser(createNormalUser());
        saveArtistUser(createArtistUser());
    }

    private void saveNormalUser(NormalUser user) {
        userRepository.save(user);
        normalUserAccessToken = jwtUtil.createAccessToken(user);
        loginService.saveRefreshToken(user.getEmail(), jwtUtil.createRefreshToken(user));

    }

    private void saveArtistUser(ArtistUser user) {
        userRepository.save(user);
        artistUserAccessToken = jwtUtil.createAccessToken(user);
        loginService.saveRefreshToken(user.getEmail(), jwtUtil.createRefreshToken(user));

    }

    public NormalUser createNormalUser(){
        final BankInfo bankInfo = BankInfo.builder()
                .accountNumber("010-0000-0000")
                .bankName(BankName.IBK_BANK)
                .build();
        UserAgree userAgree = UserAgree.builder()
                .advertisement(true)
                .personalInfo(true)
                .service(true)
                .policy(true)
                .build();
        return NormalUser.builder()
                .nickname("joon")
                .adultCertification(Boolean.TRUE)
                .email("joon@naver.com")
                .password("{bcrypt}onfree")
                .gender(Gender.MAN)
                .name("joon")
                .newsAgency("SKT")
                .phoneNumber("010-0000-0000")
                .bankInfo(bankInfo)
                .userAgree(userAgree)
                .adultCertification(true)
                .profileImage("http://www.onfree.co.kr/images/dasdasfasd")
                .deleted(false)
                .role(Role.NORMAL)
                .build();
    }

    public ArtistUser createArtistUser(){
        final BankInfo bankInfo = BankInfo.builder()
                .accountNumber("010-0000-0000")
                .bankName(BankName.IBK_BANK)
                .build();
        UserAgree userAgree = UserAgree.builder()
                .advertisement(true)
                .personalInfo(true)
                .service(true)
                .policy(true)
                .build();
        return ArtistUser.builder()
                .nickname("joon")
                .adultCertification(Boolean.TRUE)
                .email("joon1@naver.com")
                .password("{bcrypt}onfree")
                .gender(Gender.MAN)
                .name("joon")
                .newsAgency("SKT")
                .phoneNumber("010-0000-0000")
                .bankInfo(bankInfo)
                .userAgree(userAgree)
                .adultCertification(true)
                .profileImage("http://www.onfree.co.kr/images/dasdasfasd")
                .deleted(false)
                .role(Role.ARTIST)
                .portfolioUrl("http://www.onfree.co.kr/folioUrl/dasdasfasd")
                .build();
    }




    @Test
    @WithAnonymousUser
    @DisplayName("[실패][POST] 회원가입 요청 - 잘못된 데이터 입력")
    public void givenCreateUserReq_whenCreateNormalUserWithLoginUser_thenCreateUserRes() throws Exception{
        //given
        CreateNormalUserDto.Request request = givenWrongCreateNormalUserReq();
        final GlobalErrorCode errorCode = GlobalErrorCode.NOT_VALIDATED_REQUEST;
        //when //then
        mvc.perform(post("/api/users/normal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                request
                        )
                )
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
                .andExpect(jsonPath("$.errors[0]").isNotEmpty())
        ;
    }
    private CreateNormalUserDto.Request givenWrongCreateNormalUserReq() {
        return CreateNormalUserDto.Request
                .builder()
                .adultCertification(Boolean.TRUE)
                .email("jun@naver.com")
                .password("!Abcderghijk112")
                .gender(Gender.MAN)
                .nickname("")
                .name("")
                .newsAgency("SKT")
                .phoneNumber("010-8888-9999")
                .bankName(BankName.IBK_BANK)
                .accountNumber("010-8888-9999")
                .advertisementAgree(true)
                .personalInfoAgree(true)
                .policyAgree(true)
                .serviceAgree(true)
                .profileImage("http://onfree.io/images/123456789")
                .build();
    }

    @Test
    @WithNormalUser
    @DisplayName("[실패][PUT] 사용자 정보 수정 - 잘못된 데이터 입력 ")
    public void givenWrongUpdateUserInfo_whenModifiedNormalUser_thenNotValidRequestParametersError() throws Exception{
        //given
        final long userId = 1L;
        final ErrorCode errorCode = GlobalErrorCode.NOT_VALIDATED_REQUEST;

        //when then
        mvc.perform(put("/api/users/normal/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                givenWrongUpdateNormalUserReq()
                        )
                )
                .header(HttpHeaders.AUTHORIZATION, BEARER +normalUserAccessToken)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
                .andExpect(jsonPath("$.errors[0]").isNotEmpty())
        ;
    }

    private UpdateNormalUserDto.Request givenWrongUpdateNormalUserReq() {
        return UpdateNormalUserDto.Request.builder()
                .nickname("온프리프리")
                .accountNumber("010-0000-0000")
                .newsAgency("SKT")
                .phoneNumber("010-0000-0000")
                .adultCertification(Boolean.TRUE)
                .profileImage("http://onfree.io/images/aaa123")
                .build();
    }


    @Test
    @WithAnonymousUser
    @DisplayName("[실패][POST] 회원가입 요청 -  잘못된 데이터 입력")
    public void givenWrongCreateUserReq_whenCreateArtistUser_thenParameterValidError() throws Exception{
        //given
        CreateArtistUserDto.Request request = givenWrongCreateArtistUserReq();
        ErrorCode errorCode=GlobalErrorCode.NOT_VALIDATED_REQUEST;

        //when //then
        mvc.perform(post("/api/users/artist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                request
                        )

                )
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
                .andExpect(jsonPath("$.errors[0]").isNotEmpty())
        ;
    }

    private CreateArtistUserDto.Request givenWrongCreateArtistUserReq() {
        return CreateArtistUserDto.Request
                .builder()
                .adultCertification(Boolean.TRUE)
                .email("")
                .password("!Abcderghijk112")
                .gender(Gender.MAN)
                .name("")
                .newsAgency("SKT")
                .phoneNumber("010-8888-9999")
                .bankName(null)
                .accountNumber("010-8888-9999")
                .advertisementAgree(true)
                .personalInfoAgree(true)
                .policyAgree(true)
                .serviceAgree(false)
                .profileImage("http://onfree.io/images/123456789")
                .portfolioUrl("http://onfree.io/portfolioUrl/123456789")
                .build();
    }

    @Test
    @WithArtistUser
    @DisplayName("[실패][PUT] 사용자 정보 수정 - 잘못된 데이터 입력 ")
    public void givenWrongUpdateUserInfo_whenModifiedArtistUser_thenNotValidRequestParametersError() throws Exception{
        //given
        final long userId = 2L;
        final ErrorCode errorCode = GlobalErrorCode.NOT_VALIDATED_REQUEST;
        //when then
        mvc.perform(put("/api/users/artist/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                givenWrongUpdateArtistUserReq()
                        )
                )
                .header(HttpHeaders.AUTHORIZATION, BEARER + artistUserAccessToken)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
                .andExpect(jsonPath("$.errors[0]").isNotEmpty())
        ;
    }

    private UpdateArtistUserDto.Request givenWrongUpdateArtistUserReq() {
        return UpdateArtistUserDto.Request.builder()
                .nickname("온프리프리")
                .accountNumber("010-0000-0000")
                .newsAgency("SKT")
                .phoneNumber("010-0000-0000")
                .adultCertification(Boolean.TRUE)
                .profileImage("http://onfree.io/images/aaa123")
                .portfolioUrl("http://onfree.io/portfolioUrl/123456789")
                .build();
    }


    @Test
    @DisplayName("[실패][POST] 공지 글 생성 - 잘못된 데이터 입력")
    @WithAdminUser
    public void givenWrongCreateNoticeDtoReq_whenCreateNotice_thenCreateNoticeDtoRes() throws Exception{
        //given

        final ErrorCode errorCode = GlobalErrorCode.NOT_VALIDATED_REQUEST;
        //when //then
        mvc.perform(post("/admin/api/notices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                givenWrongCreateNoticeDtoRequest()
                        )
                )
        )
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
                .andExpect(jsonPath("$.errors[0]").isNotEmpty())

        ;
    }

    private CreateNoticeDto.Request givenWrongCreateNoticeDtoRequest() {
        return CreateNoticeDto.Request.builder()
                .title("")
                .content("내용")
                .top(true)
                .build();
    }

    @Test
    @DisplayName("[실패][PUT] 공지 글 수정 - 잘못된 데이터 입력")
    @WithAdminUser
    public void givenUpdateNoticeDtoReq_whenUpdateNotice_thenUpdateNoticeDtoRes() throws Exception{
        //given
        final long noticeId = 1L;
        final ErrorCode errorCode = GlobalErrorCode.NOT_VALIDATED_REQUEST;

        //when//then
        mvc.perform(put("/admin/api/notices/{noticeId}", noticeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsBytes(
                                getUpdateNoticeDtoRequest()
                        )
                )
        )
                .andDo(print())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
                .andExpect(jsonPath("$.errors[0]").isNotEmpty())
        ;
    }

    private UpdateNoticeDto.Request getUpdateNoticeDtoRequest() {
        return UpdateNoticeDto.Request.builder()
                .title("")
                .content("내용")
                .top(true)
                .disabled(false)
                .build();
    }

    @Test
    @DisplayName("[실패][POST] 자주하는 질문 생성 - 잘못된 데이터 입력")
    @WithAdminUser
    public void givenWrongCreateQuestionDtoReq_whenCreateQuestion_thenCreateQuestionDtoRes() throws Exception{
        //given
        final ErrorCode errorCode = GlobalErrorCode.NOT_VALIDATED_REQUEST;

        //when//then
        mvc.perform(post("/admin/api/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                givenWrongCreateQuestionDtoRequest()
                        )
                )
        )
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
                .andExpect(jsonPath("$.errors[0]").isNotEmpty())
        ;

    }

    private CreateQuestionDto.Request givenWrongCreateQuestionDtoRequest() {
        return CreateQuestionDto.Request.builder()
                .title("")
                .content("내용")
                .top(true)
                .build();
    }

    @Test
    @DisplayName("[실패][PUT] 자주하는 질문 수정 - 잘못된 데이터 입력")
    @WithAdminUser
    public void givenUpdateQuestionDtoReq_whenUpdateQuestion_thenUpdateQuestionDtoRes() throws Exception{
        //given
        final long questionId = 1L;
        final ErrorCode errorCode = GlobalErrorCode.NOT_VALIDATED_REQUEST;

        //when//then
        mvc.perform(put("/admin/api/questions/{questionId}", questionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                givenWrongUpdateQuestionDtoRequest()
                        )
                )
        )
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
                .andExpect(jsonPath("$.errors[0]").isNotEmpty())
        ;
    }
    private UpdateQuestionDto.Request givenWrongUpdateQuestionDtoRequest() {
        return UpdateQuestionDto.Request.builder()
                .title("")
                .content("내용")
                .top(true)
                .disabled(false)
                .build();
    }


    @Test
    @WithNormalUser
    @DisplayName("[실패][PUT] 사용자 알림설정 변경 - 잘못된 데이터 입력")
    public void givenOtherUserIdAndUpdateUserNotificationDto_whenUpdateUserNotification_thenAccessDeniedError() throws Exception{
        //given
        final long userId = 1L;
        final GlobalErrorCode errorCode = GlobalErrorCode.NOT_VALIDATED_REQUEST;
        //when
        //then
        mvc.perform(put("/api/users/{userId}/notifications", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, BEARER +normalUserAccessToken)
                .content(
                        mapper.writeValueAsString(
                                givenWrongUpdateUserNotificationDto()
                        )
                )
        )
                .andDo(print())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
                .andExpect(jsonPath("$.errors[0]").isNotEmpty())

        ;
    }

    private UpdateUserNotificationDto givenWrongUpdateUserNotificationDto() {
        return UpdateUserNotificationDto.builder()
                .emailNewsNotification(null)
                .emailRequestNotification(true)
                .kakaoNewsNotification(false)
                .kakaoRequestNotification(false)
                .pushRequestNotification(true)
                .build();
    }
}
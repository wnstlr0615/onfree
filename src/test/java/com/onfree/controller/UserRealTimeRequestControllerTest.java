package com.onfree.controller;


import com.onfree.anotation.WithArtistUser;
import com.onfree.anotation.WithNormalUser;
import com.onfree.common.ControllerBaseTest;
import com.onfree.core.dto.realtimerequest.SimpleRealtimeRequestDto;
import com.onfree.core.dto.user.artist.MobileCarrier;
import com.onfree.core.entity.realtimerequset.RequestStatus;
import com.onfree.core.entity.user.*;
import com.onfree.core.service.UserRealTimeRequestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest( controllers = UserRealTimeRequestController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class UserRealTimeRequestControllerTest extends ControllerBaseTest {
    @MockBean
    UserRealTimeRequestService userRealTimeRequestService;

    @Test
    @WithArtistUser
    @DisplayName("[성공][GET] 실시간 의뢰 페이징 테스트 - 작가유저 조회")
    public void givenPageAndPageSize_whenMyRealTimeRequestListByArtistUser_thenPagingList() throws Exception{
        //given
        int page = 0;
        int size = 10;
        int total = 6;

        PageRequest pageRequest = PageRequest.of(page, size);
        ArtistUser artistUser = getArtistUser();
        List<SimpleRealtimeRequestDto> simpleRealtimeRequestDtos =  List.of(
                createSimpleRealTimeRequestDto(1L, "제목1", RequestStatus.REQUEST_RECRUITING.getDisplayStatus()),
                createSimpleRealTimeRequestDto(2L, "제목2", RequestStatus.REQUEST_RECRUITING.getDisplayStatus()),
                createSimpleRealTimeRequestDto(3L, "제목3", RequestStatus.REQUEST_RECRUITING.getDisplayStatus()),
                createSimpleRealTimeRequestDto(4L, "제목4", RequestStatus.REQUEST_FINISH.getDisplayStatus()),
                createSimpleRealTimeRequestDto(5L, "제목5", RequestStatus.REQUEST_FINISH.getDisplayStatus()),
                createSimpleRealTimeRequestDto(6L, "제목6", RequestStatus.REQUEST_FINISH.getDisplayStatus())
        );

        PageImpl<SimpleRealtimeRequestDto> dtoPage = new PageImpl<>(simpleRealtimeRequestDtos, pageRequest, total);


        when(userRealTimeRequestService.findAllRealTimeRequestByUserId(any(User.class), anyInt(), anyInt()))
                .thenReturn(
                        dtoPage
                );

        //when //then
        mvc.perform(get("/api/v1/users/me/real-time-requests")
                .with(
                        authentication(
                                new UsernamePasswordAuthenticationToken(artistUser,  null, getAuthorities("ROLE_ARTIST"))
                        )
                )
                .queryParam("page", "0")
                .queryParam("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.items[0].realTimeRequestId").value(1L))
                .andExpect(jsonPath("$._embedded.items[0].title").value("제목1"))
                .andExpect(jsonPath("$._embedded.items[0].status").value(RequestStatus.REQUEST_RECRUITING.getDisplayStatus()))
                .andExpect(jsonPath("$._embedded.items[0].nickname").isNotEmpty())
                .andExpect(jsonPath("$._embedded.items[0].startDate").isNotEmpty())
                .andExpect(jsonPath("$._embedded.items[0].endDate").isNotEmpty())
                .andExpect(jsonPath("$._embedded.items[0].createDate").isNotEmpty())
                .andExpect(jsonPath("$._embedded.items[0]._links.self").isNotEmpty())
                .andExpect(jsonPath("$._links.self").isNotEmpty())
                .andExpect(jsonPath("$._links.profile").isNotEmpty())

        ;
        verify(userRealTimeRequestService).findAllRealTimeRequestByUserId(any(User.class), eq(page), eq(size));
    }

    private SimpleRealtimeRequestDto createSimpleRealTimeRequestDto(long requestId, String title, String status) {
        LocalDate startDate = LocalDate.of(2022,3,3);
        LocalDate endDate = LocalDate.of(2022,3,6);
        LocalDate createDate = LocalDate.of(2022,3,2);
        String nickname = "닉네임";
        return SimpleRealtimeRequestDto.createSimpleRealtimeRequestDto(requestId, title, nickname, status, startDate, endDate, createDate);
    }

    private ArtistUser getArtistUser() {
        final BankInfo bankInfo = BankInfo.builder()
                .accountNumber("010-0000-0000")
                .bankName(BankName.IBK)
                .build();
        UserAgree userAgree = UserAgree.builder()
                .advertisement(true)
                .personalInfo(true)
                .service(true)
                .policy(true)
                .build();
        return ArtistUser.builder()
                .userId(1L)
                .nickname("joon")
                .adultCertification(Boolean.TRUE)
                .email("joon@naver.com")
                .password("{bcrypt}onfree")
                .gender(Gender.MAN)
                .name("joon")
                .mobileCarrier(MobileCarrier.SKT)
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
    @WithNormalUser
    @DisplayName("[성공][GET] 실시간 의뢰 페이징 테스트 - 일반유저 조회")
    public void givenPageAndPageSize_whenMyRealTimeRequestListByNormalUser_thenPagingList() throws Exception{
        //given
        int page = 0;
        int size = 10;
        int total = 6;

        PageRequest pageRequest = PageRequest.of(page, size);
        long userId = 1L;
        NormalUser normalUser = getNormalUser(userId);
        List<SimpleRealtimeRequestDto> simpleRealtimeRequestDtos =  List.of(
                createSimpleRealTimeRequestDto(1L, "제목1", RequestStatus.REQUEST_RECRUITING.getDisplayStatus()),
                createSimpleRealTimeRequestDto(2L, "제목2", RequestStatus.REQUEST_RECRUITING.getDisplayStatus()),
                createSimpleRealTimeRequestDto(3L, "제목3", RequestStatus.REQUEST_RECRUITING.getDisplayStatus()),
                createSimpleRealTimeRequestDto(4L, "제목4", RequestStatus.REQUEST_FINISH.getDisplayStatus()),
                createSimpleRealTimeRequestDto(5L, "제목5", RequestStatus.REQUEST_FINISH.getDisplayStatus()),
                createSimpleRealTimeRequestDto(6L, "제목6", RequestStatus.REQUEST_FINISH.getDisplayStatus())
        );

        PageImpl<SimpleRealtimeRequestDto> dtoPage = new PageImpl<>(simpleRealtimeRequestDtos, pageRequest, total);


        when(userRealTimeRequestService.findAllRealTimeRequestByUserId(any(User.class), anyInt(), anyInt()))
                .thenReturn(
                        dtoPage
                );

        //when //then
        mvc.perform(get("/api/v1/users/me/real-time-requests")
                .with(
                        authentication(
                                new UsernamePasswordAuthenticationToken(normalUser,  null, getAuthorities("ROLE_NORMAL"))
                        )
                )
                .queryParam("page", "0")
                .queryParam("size", "10")
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.items[0].realTimeRequestId").value(1L))
                .andExpect(jsonPath("$._embedded.items[0].title").value("제목1"))
                .andExpect(jsonPath("$._embedded.items[0].status").value(RequestStatus.REQUEST_RECRUITING.getDisplayStatus()))
                .andExpect(jsonPath("$._embedded.items[0].nickname").isNotEmpty())
                .andExpect(jsonPath("$._embedded.items[0].startDate").isNotEmpty())
                .andExpect(jsonPath("$._embedded.items[0].endDate").isNotEmpty())
                .andExpect(jsonPath("$._embedded.items[0].createDate").isNotEmpty())
                .andExpect(jsonPath("$._embedded.items[0]._links.self").isNotEmpty())
                .andExpect(jsonPath("$._links.self").isNotEmpty())
                .andExpect(jsonPath("$._links.profile").isNotEmpty())

        ;
        verify(userRealTimeRequestService).findAllRealTimeRequestByUserId(any(User.class), eq(page), eq(size));

    }

    private List<SimpleGrantedAuthority> getAuthorities(String role) {
        return List.of(new SimpleGrantedAuthority(role));
    }

    public NormalUser getNormalUser(long userId){
        return NormalUser.builder()
                .userId(userId)
                .adultCertification(Boolean.TRUE)
                .email("jun@naver.com")
                .password("!Abcderghijk112")
                .gender(Gender.MAN)
                .name("준식")
                .mobileCarrier(MobileCarrier.SKT)
                .phoneNumber("010-8888-9999")
                .bankInfo(
                        BankInfo.createBankInfo(BankName.IBK, "010-8888-9999")
                )
                .userAgree(
                        UserAgree.createUserAgree(true,true,true,true)
                )
                .profileImage("http://onfree.io/images/123456789")
                .build();
    }
}
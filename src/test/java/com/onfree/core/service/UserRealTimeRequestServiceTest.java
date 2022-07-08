package com.onfree.core.service;

import com.onfree.core.dto.realtimerequest.SimpleRealtimeRequestDto;
import com.onfree.core.dto.user.artist.MobileCarrier;
import com.onfree.core.entity.realtimerequset.RealTimeRequest;
import com.onfree.core.entity.realtimerequset.RequestStatus;
import com.onfree.core.entity.realtimerequset.UseType;
import com.onfree.core.entity.user.*;
import com.onfree.core.repository.RealTimeRequestRepository;
import com.onfree.core.service.realtimerequest.UserRealTimeRequestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRealTimeRequestServiceTest {
    @Mock
    RealTimeRequestRepository realTimeRequestRepository;
    @InjectMocks
    UserRealTimeRequestService realTimeRequestService;

    @Test
    @DisplayName("[성공] 실시간 의뢰 페이징 처리로 조회 - 작가유저 ")
    public void givenPageAndSize_whenFindAllRealTimeRequestByArtistUser_thenReturnPagingList(){
        //given
        long userId = 1L;
        ArtistUser artistUser = getArtistUser(userId);

        List<RealTimeRequest> realTimeRequests = List.of(
                createRealTimeRequest(1L, "제목1 모집중 / 상업용", RequestStatus.REQUEST_RECRUITING, UseType.COMMERCIAL, artistUser, true),
                createRealTimeRequest(2L, "제목2 모집중 / 비 상업용", RequestStatus.REQUEST_RECRUITING, UseType.NOT_COMMERCIAL, artistUser, true),
                createRealTimeRequest(3L, "제목3 모집중 / 상업용", RequestStatus.REQUEST_RECRUITING, UseType.COMMERCIAL, artistUser, true),
                createRealTimeRequest(4L, "제목4 마감중 / 비 상업용", RequestStatus.REQUEST_FINISH, UseType.NOT_COMMERCIAL, artistUser, true),
                createRealTimeRequest(5L, "제목5 마감중 / 상업용", RequestStatus.REQUEST_FINISH, UseType.COMMERCIAL, artistUser, true),
                createRealTimeRequest(6L, "제목6 마감중 / 비 상업용", RequestStatus.REQUEST_FINISH, UseType.NOT_COMMERCIAL, artistUser, true)
        );
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size);
        int total = 6;

        when(realTimeRequestRepository.findAllByUserAndStatusNot(any(User.class), any(RequestStatus.class), any(Pageable.class)))
                .thenReturn(
                        new PageImpl<>(realTimeRequests, pageRequest, total)
                );
        //when
        Page<SimpleRealtimeRequestDto> requestDtoPage = realTimeRequestService.findAllRealTimeRequestByUserId(artistUser, page, size);
        SimpleRealtimeRequestDto requestDto = requestDtoPage.getContent().get(0);
        //then
        assertThat(requestDto)
                .hasFieldOrPropertyWithValue("realTimeRequestId",1L)
                .hasFieldOrPropertyWithValue("title", "제목1 모집중 / 상업용")
                .hasFieldOrPropertyWithValue("nickname", artistUser.getNickname())
                .hasFieldOrPropertyWithValue("status", RequestStatus.REQUEST_RECRUITING.getDisplayStatus())
                .hasFieldOrProperty("startDate")
                .hasFieldOrProperty("endDate")
                .hasFieldOrProperty("createDate")
        ;

        verify(realTimeRequestRepository).findAllByUserAndStatusNot(any(User.class), any(RequestStatus.class), any(Pageable.class));
    }

    private RealTimeRequest createRealTimeRequest(long realTimeRequestId, String title, RequestStatus status, UseType useType, User user, boolean adult) {
        String content = "실시간 의뢰 내용";
        LocalDate startDate = LocalDate.of(2022,3,2);
        LocalDate endDate = LocalDate.of(2022,3,5);
        String referenceLink = "http://naver.com";
        LocalDateTime createdDate = LocalDateTime.of(2022, 3 ,2, 0, 0);

        String referenceFiles = UUID.randomUUID() + ".txt," + UUID.randomUUID() + ".png";
        return RealTimeRequest.createRealTimeRequest(realTimeRequestId, title, content, user, startDate, endDate, useType, referenceLink, referenceFiles, adult, status, createdDate);
    }

    private ArtistUser getArtistUser(long userId) {
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
                .userId(userId)
                .nickname("joon")
                .adultCertification(Boolean.TRUE)
                .email("joon1@naver.com")
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
    @DisplayName("[성공] 실시간 의뢰 페이징 처리로 조회 -  일반 유저 ")
    public void givenPageAndSize_whenFindAllRealTimeRequestByNormalUser_thenReturnPagingList(){
        //given
        long userId = 1L;
        NormalUser normalUser = getNormalUser(userId);

        List<RealTimeRequest> realTimeRequests = List.of(
                createRealTimeRequest(1L, "제목1 모집중 / 상업용", RequestStatus.REQUEST_RECRUITING, UseType.COMMERCIAL, normalUser, true),
                createRealTimeRequest(2L, "제목2 모집중 / 비 상업용", RequestStatus.REQUEST_RECRUITING, UseType.NOT_COMMERCIAL, normalUser, true),
                createRealTimeRequest(3L, "제목3 모집중 / 상업용", RequestStatus.REQUEST_RECRUITING, UseType.COMMERCIAL, normalUser, true),
                createRealTimeRequest(4L, "제목4 마감중 / 비 상업용", RequestStatus.REQUEST_FINISH, UseType.NOT_COMMERCIAL, normalUser, true),
                createRealTimeRequest(5L, "제목5 마감중 / 상업용", RequestStatus.REQUEST_FINISH, UseType.COMMERCIAL, normalUser, true),
                createRealTimeRequest(6L, "제목6 마감중 / 비 상업용", RequestStatus.REQUEST_FINISH, UseType.NOT_COMMERCIAL, normalUser, true)
        );
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size);
        int total = 6;

        when(realTimeRequestRepository.findAllByUserAndStatusNot(any(User.class), any(RequestStatus.class), any(Pageable.class)))
                .thenReturn(
                        new PageImpl<>(realTimeRequests, pageRequest, total)
                );
        //when
        Page<SimpleRealtimeRequestDto> requestDtoPage = realTimeRequestService.findAllRealTimeRequestByUserId(normalUser, page, size);
        SimpleRealtimeRequestDto requestDto = requestDtoPage.getContent().get(0);
        //then
        assertThat(requestDto)
                .hasFieldOrPropertyWithValue("realTimeRequestId",1L)
                .hasFieldOrPropertyWithValue("title", "제목1 모집중 / 상업용")
                .hasFieldOrPropertyWithValue("nickname", normalUser.getNickname())
                .hasFieldOrPropertyWithValue("status", RequestStatus.REQUEST_RECRUITING.getDisplayStatus())
                .hasFieldOrProperty("startDate")
                .hasFieldOrProperty("endDate")
                .hasFieldOrProperty("createDate")
        ;

        verify(realTimeRequestRepository).findAllByUserAndStatusNot(any(User.class), any(RequestStatus.class), any(Pageable.class));
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
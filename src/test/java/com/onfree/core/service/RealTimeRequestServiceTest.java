package com.onfree.core.service;

import com.onfree.common.error.code.RealTimeRequestErrorCode;
import com.onfree.common.error.exception.RealTimeRequestException;
import com.onfree.core.dto.realtimerequest.CreateRealTimeRequestDto;
import com.onfree.core.dto.realtimerequest.RealTimeRequestDetailDto;
import com.onfree.core.dto.realtimerequest.SimpleRealtimeRequestDto;
import com.onfree.core.dto.realtimerequest.UpdateRealTimeRequestDto;
import com.onfree.core.dto.user.artist.MobileCarrier;
import com.onfree.core.entity.realtimerequset.RealTimeRequest;
import com.onfree.core.entity.realtimerequset.RequestStatus;
import com.onfree.core.entity.realtimerequset.UseType;
import com.onfree.core.entity.user.*;
import com.onfree.core.repository.RealTimeRequestRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RealTimeRequestServiceTest {
    @Mock
    RealTimeRequestRepository realTimeRequestRepository;

    @InjectMocks
    RealTimeRequestService realTimeRequestService;

    @Test
    @DisplayName("[성공] 실시간 의뢰 페이징 처리로 조회")
    public void givenPageAndSize_whenFindAllRealTimeRequest_thenReturnPagingList(){
        //given
        ArtistUser artistUser = getArtistUser(1L);
        NormalUser otherNormalUser = getNormalUser(2L);

        List<RealTimeRequest> realTimeRequests = List.of(
                createRealTimeRequest(1L, "제목1 모집중 / 상업용", RequestStatus.REQUEST_RECRUITING, UseType.COMMERCIAL, artistUser, true),
                createRealTimeRequest(2L, "제목2 모집중 / 비 상업용", RequestStatus.REQUEST_RECRUITING, UseType.NOT_COMMERCIAL, artistUser, true),
                createRealTimeRequest(3L, "제목3 모집중 / 상업용", RequestStatus.REQUEST_RECRUITING, UseType.COMMERCIAL, artistUser, true),
                createRealTimeRequest(4L, "제목4 마감중 / 비 상업용", RequestStatus.REQUEST_FINISH, UseType.NOT_COMMERCIAL, otherNormalUser, true),
                createRealTimeRequest(5L, "제목5 마감중 / 상업용", RequestStatus.REQUEST_FINISH, UseType.COMMERCIAL, otherNormalUser, true),
                createRealTimeRequest(6L, "제목6 마감중 / 비 상업용", RequestStatus.REQUEST_FINISH, UseType.NOT_COMMERCIAL, otherNormalUser, true)
        );
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size);
        int total = 6;

        when(realTimeRequestRepository.findAllByStatusNot(any(Pageable.class), eq(RequestStatus.REQUEST_DELETED)))
                    .thenReturn(
                            new PageImpl<>(realTimeRequests, pageRequest, total)
                    );
        //when
        Page<SimpleRealtimeRequestDto> requestDtoPage = realTimeRequestService.findAllRealTimeRequest(page, size);
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

        verify(realTimeRequestRepository).findAllByStatusNot(eq(pageRequest), eq(RequestStatus.REQUEST_DELETED));
    }

    private RealTimeRequest createRealTimeRequest(long realTimeRequestId, String title, RequestStatus status, UseType useType, User user, boolean adult) {
        String content = "실시간 의뢰 내용";
        LocalDate startDate = LocalDate.of(2022,3,2);
        LocalDate endDate = LocalDate.of(2022,3,5);
        String referenceLink = "http://naver.com";
        LocalDateTime createdDate = LocalDateTime.of(2022, 3 ,2, 0, 0);
        return getRealTimeRequest(realTimeRequestId, title, content, user, useType, adult, status, startDate, endDate, referenceLink, createdDate);
    }

    private ArtistUser getArtistUser(long userId) {
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
                        BankInfo.createBankInfo(BankName.IBK_BANK, "010-8888-9999")
                )
                .userAgree(
                        UserAgree.createUserAgree(true,true,true,true)
                )
                .profileImage("http://onfree.io/images/123456789")
                .build();
    }

    @Test
    @DisplayName("[성공] 실시간 의뢰 상세  조회")
    public void givenRequestId_whenFindOneRealTimeRequest_thenReturnRealTimeRequestDetailDto(){
        //given
        long realTimeRequestId = 1L;
        String title = "실시간 의뢰 제목";
        String content = "실시간 의뢰 내용";
        User user = getArtistUser(realTimeRequestId);
        UseType useType = UseType.COMMERCIAL;
        boolean adult = false;
        RequestStatus status = RequestStatus.REQUEST_REQUESTING;
        when(realTimeRequestRepository.findByRealTimeRequestId(realTimeRequestId))
                .thenReturn(
                        Optional.of(
                                getRealTimeRequest(realTimeRequestId, title, content, user, useType, adult, status)
                        )
                );
        //when
        RealTimeRequestDetailDto realTimeRequestDetailDto = realTimeRequestService.findOneRealTimeRequest(realTimeRequestId);

        //then
        assertThat(realTimeRequestDetailDto)
                .hasFieldOrPropertyWithValue("realTimeRequestId", realTimeRequestId)
                .hasFieldOrPropertyWithValue("title", title)
                .hasFieldOrPropertyWithValue("content", content)
                .hasFieldOrPropertyWithValue("nickname", user.getNickname())
                .hasFieldOrPropertyWithValue("adult", adult)
                .hasFieldOrPropertyWithValue("useType", useType)
                .hasFieldOrPropertyWithValue("status", status)
                .hasFieldOrProperty("startDate")
                .hasFieldOrProperty("endDate")
                .hasFieldOrProperty("referenceLink")
                .hasFieldOrProperty("createDate")
        ;
        verify(realTimeRequestRepository).findByRealTimeRequestId(eq(realTimeRequestId));
    }

    private RealTimeRequest getRealTimeRequest(long realTimeRequestId, String title, String content, User user, UseType useType, boolean adult, RequestStatus status) {
        LocalDate startDate = LocalDate.of(2022, 3, 7);
        return getRealTimeRequest(realTimeRequestId, title, content, user, useType, adult, status, startDate, LocalDate.of(2022, 3, 9), "referenceLink", LocalDateTime.of(2022, 3, 7, 0, 0));
    }

    @Test
    @DisplayName("[실패] 없는 실시간 의뢰 상세 조회 - NOT_FOUND_REAL_TIME_REQUEST " )
    public void givenRequestId_whenFindOneRealTimeRequest_thenNorFoundRealTimeRequestError(){
        //given
        RealTimeRequestErrorCode errorCode = RealTimeRequestErrorCode.NOT_FOUND_REAL_TIME_REQUEST;
        long notFoundRequestId = 1L;
        when(realTimeRequestRepository.findByRealTimeRequestId(notFoundRequestId))
                .thenReturn(
                        Optional.empty()
                );
        //when

        //then
        RealTimeRequestException exception = assertThrows(RealTimeRequestException.class,
                () -> realTimeRequestService.findOneRealTimeRequest(notFoundRequestId)
        );
        assertThat(exception)
            .hasFieldOrPropertyWithValue("errorCode", errorCode)
            .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription())
        ;
        verify(realTimeRequestRepository).findByRealTimeRequestId(eq(notFoundRequestId));
    }

    @Test
    @DisplayName("[실패] 삭제된 실시간 의뢰 상세 조회 - REAL_TIME_REQUEST_DELETED " )
    public void givenDeletedRequestId_whenFindOneRealTimeRequest_thenRealTimeRequestDeletedError(){
        //given
        RealTimeRequestErrorCode errorCode = RealTimeRequestErrorCode.REAL_TIME_REQUEST_DELETED;
        long deletedRequestId = 1L;

        RequestStatus status = RequestStatus.REQUEST_DELETED;
        when(realTimeRequestRepository.findByRealTimeRequestId(deletedRequestId))
                .thenReturn(
                        Optional.of(
                                getRealTimeRequest(deletedRequestId, status)
                        )
                );
        //when

        //then
        RealTimeRequestException exception = assertThrows(RealTimeRequestException.class,
                () -> realTimeRequestService.findOneRealTimeRequest(deletedRequestId)
        );
        assertThat(exception)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription())
        ;
        verify(realTimeRequestRepository).findByRealTimeRequestId(eq(deletedRequestId));
    }

    private RealTimeRequest getRealTimeRequest(Long requestId, RequestStatus status) {
        String title = "실시간 의뢰 제목";
        String content = "실시간 의뢰 내용";
        User user = getArtistUser(requestId);
        UseType useType = UseType.COMMERCIAL;
        boolean adult = false;
        return getRealTimeRequest(requestId, title, content, user, useType, adult, status);
    }


    @Test
    @DisplayName("[성공] 실시간 의뢰 추가하기 - 작가 유저")
    public void givenCreateRealTimeRequestDto_whenAddRealTimeRequestByArtistUser_thenCreateRealTimeRequestResponse(){
        //given
        long userId = 1L;
        ArtistUser artistUser = getArtistUser(userId);
        String title = "실시간 의뢰 제목";
        String content = "실시간 의뢰 내용";
        UseType useType = UseType.COMMERCIAL;
        boolean adult = false;

        CreateRealTimeRequestDto.Request request = createRealTimeRequestDtoRequest(title, content, useType, adult);

        long requestId = 1L;
        when(realTimeRequestRepository.save(any(RealTimeRequest.class)))
                .thenReturn(
                        getRealTimeRequest(requestId, title, content, artistUser, useType, adult, RequestStatus.REQUEST_REQUESTING)
                );

        //when
        CreateRealTimeRequestDto.Response response = realTimeRequestService.addRealTimeRequest(artistUser, request);

        //then
        assertThat(response)
                .hasFieldOrPropertyWithValue("realTimeRequestId", requestId)
                .hasFieldOrPropertyWithValue("title", title)
                .hasFieldOrPropertyWithValue("content", content)
                .hasFieldOrPropertyWithValue("useType", useType)
                .hasFieldOrPropertyWithValue("adult", adult)
                .hasFieldOrProperty("startDate")
                .hasFieldOrProperty("endDate")
                .hasFieldOrProperty("referenceLink")
                ;
        verify(realTimeRequestRepository).save(any(RealTimeRequest.class));
    }

    private CreateRealTimeRequestDto.Request createRealTimeRequestDtoRequest(String title, String content, UseType useType, boolean adult) {
        LocalDate startDate = LocalDate.of(2022, 3, 7);
        LocalDate endDate = LocalDate.of(2022, 3, 9);
        String referenceLink = "referenceLink";
        return CreateRealTimeRequestDto.Request.createRealTimeRequestDtoRequest(title, content, startDate, endDate, useType, referenceLink, adult);
    }

    @Test
    @DisplayName("[성공] 실시간 의뢰 추가하기 - 일반 유저")
    public void givenCreateRealTimeRequestDto_whenAddRealTimeRequestByNormalUser_thenCreateRealTimeRequestResponse(){
        //given
        long userId = 1L;
        NormalUser normalUser = getNormalUser(userId);
        String title = "실시간 의뢰 제목";
        String content = "실시간 의뢰 내용";
        UseType useType = UseType.COMMERCIAL;
        boolean adult = false;

        CreateRealTimeRequestDto.Request request = createRealTimeRequestDtoRequest(title, content, useType, adult);

        long requestId = 1L;
        when(realTimeRequestRepository.save(any(RealTimeRequest.class)))
                .thenReturn(
                        getRealTimeRequest(requestId, title, content, normalUser, useType, adult, RequestStatus.REQUEST_REQUESTING)
                );

        //when
        CreateRealTimeRequestDto.Response response = realTimeRequestService.addRealTimeRequest(normalUser, request);

        //then
        assertThat(response)
                .hasFieldOrPropertyWithValue("realTimeRequestId", requestId)
                .hasFieldOrPropertyWithValue("title", title)
                .hasFieldOrPropertyWithValue("content", content)
                .hasFieldOrPropertyWithValue("useType", useType)
                .hasFieldOrPropertyWithValue("adult", adult)
                .hasFieldOrProperty("startDate")
                .hasFieldOrProperty("endDate")
                .hasFieldOrProperty("referenceLink")
        ;
        verify(realTimeRequestRepository).save(any(RealTimeRequest.class));
    }

    @Test
    @DisplayName("[성공] 실시간 의뢰 수정하기")
    public void givenRequestIdAndUpdateRequestDto_whenModifyRealTimeRequest_thenNothing(){
        //given
        long requestId = 1L;
        long userId = 1L;
        NormalUser normalUser = getNormalUser(userId);
        LocalDate startDate = LocalDate.of(2022, 3, 7);
        LocalDate endDate = LocalDate.of(2022, 3, 9);
        RealTimeRequest realTimeRequest = getRealTimeRequest(
                requestId, "실시간 의뢰 제목", "실시간 의뢰 내용", normalUser, UseType.COMMERCIAL, false,
                RequestStatus.REQUEST_RECRUITING, startDate, endDate, "referenceLink", LocalDateTime.of(2022, 3, 7, 0, 0));
        when(realTimeRequestRepository.findByRealTimeRequestIdAndUser(anyLong(), any(User.class)))
                    .thenReturn(
                            Optional.of(
                                    realTimeRequest
                            )
                    );

        LocalDate updateStartDate = LocalDate.of(2022, 3, 8);
        LocalDate updateEndDate = LocalDate.of(2022, 3, 10);
        UpdateRealTimeRequestDto updateRequestDto = createUpdateRealTimeRequestDto(
                "변경된 제목", "변경된 내용", UseType.NOT_COMMERCIAL, true,
                updateStartDate, updateEndDate, "new Link"
        );
        //when

        realTimeRequestService.modifyRealTimeRequest(requestId, normalUser, updateRequestDto);

        //then
        assertThat(realTimeRequest)
                .hasFieldOrPropertyWithValue("realTimeRequestId", requestId)
                .hasFieldOrPropertyWithValue("title", "변경된 제목")
                .hasFieldOrPropertyWithValue("content", "변경된 내용")
                .hasFieldOrPropertyWithValue("startDate", updateStartDate)
                .hasFieldOrPropertyWithValue("endDate", updateEndDate)
                .hasFieldOrPropertyWithValue("useType", UseType.NOT_COMMERCIAL)
                .hasFieldOrPropertyWithValue("adult", true)
                .hasFieldOrPropertyWithValue("referenceLink", "new Link")
                .hasFieldOrPropertyWithValue("status", RequestStatus.REQUEST_RECRUITING)
                .hasFieldOrProperty("user")
        ;
        verify(realTimeRequestRepository).findByRealTimeRequestIdAndUser(anyLong(), any(User.class));
    }

    private RealTimeRequest getRealTimeRequest(long realTimeRequestId, String title, String content, User user, UseType useType, boolean adult, RequestStatus status, LocalDate startDate, LocalDate endDate, String referenceLink, LocalDateTime createDateTime) {

        return RealTimeRequest.createRealTimeRequest(
                realTimeRequestId, title, content, user, startDate, endDate,
                useType, referenceLink, adult, status, createDateTime
        );
    }

    private UpdateRealTimeRequestDto createUpdateRealTimeRequestDto(String title, String content, UseType useType, boolean adult, LocalDate startDate, LocalDate endDate, String referenceLink) {
        return UpdateRealTimeRequestDto.createUpdateRealTimeRequestDto(title, content, startDate, endDate, useType, referenceLink, adult);
    }


    @Test
    @DisplayName("[실패] 삭제된 실시간 의뢰 수정 요청 - REAL_TIME_REQUEST_DELETED")
    public void givenDeletedRequestIdAndUpdateRequestDto_whenModifyRealTimeRequest_thenRealTimeRequestDeletedError(){
        //given
        RealTimeRequestErrorCode errorCode = RealTimeRequestErrorCode.REAL_TIME_REQUEST_DELETED;
        long deletedRequestId = 1L;
        long userId = 1L;
        NormalUser normalUser = getNormalUser(userId);
        LocalDate startDate = LocalDate.of(2022, 3, 7);
        LocalDate endDate = LocalDate.of(2022, 3, 9);
        RealTimeRequest realTimeRequest = getRealTimeRequest(
                deletedRequestId, "실시간 의뢰 제목", "실시간 의뢰 내용", normalUser, UseType.COMMERCIAL, false,
                RequestStatus.REQUEST_DELETED, startDate, endDate, "referenceLink", LocalDateTime.of(2022, 3, 7, 0, 0));
        when(realTimeRequestRepository.findByRealTimeRequestIdAndUser(anyLong(), any(User.class)))
                .thenReturn(
                        Optional.of(
                                realTimeRequest
                        )
                );

        LocalDate updateStartDate = LocalDate.of(2022, 3, 8);
        LocalDate updateEndDate = LocalDate.of(2022, 3, 10);
        UpdateRealTimeRequestDto updateRequestDto = createUpdateRealTimeRequestDto(
                "변경된 제목", "변경된 내용", UseType.NOT_COMMERCIAL, true,
                updateStartDate, updateEndDate, "new Link"
        );
        //when
        RealTimeRequestException exception = assertThrows(RealTimeRequestException.class,
                () -> realTimeRequestService.modifyRealTimeRequest(deletedRequestId, normalUser, updateRequestDto)
        );

        //then
        assertThat(exception)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription())
        ;
        verify(realTimeRequestRepository).findByRealTimeRequestIdAndUser(anyLong(), any(User.class));
    }

    @Test
    @DisplayName("[실패] 마감된 실시간 의뢰 수정하기 - FINISH_REQUEST_CAN_NOT_UPDATE")
    public void givenFinishRequestIdAndUpdateRequestDto_whenModifyRealTimeRequest_thenFinishRequestCanNotUpdateError(){
        //given
        RealTimeRequestErrorCode errorCode = RealTimeRequestErrorCode.FINISH_REQUEST_CAN_NOT_UPDATE;
        long finishRequestId = 1L;
        long userId = 1L;
        NormalUser normalUser = getNormalUser(userId);
        LocalDate startDate = LocalDate.of(2022, 3, 7);
        LocalDate endDate = LocalDate.of(2022, 3, 9);
        RealTimeRequest realTimeRequest = getRealTimeRequest(
                finishRequestId, "실시간 의뢰 제목", "실시간 의뢰 내용", normalUser, UseType.COMMERCIAL, false,
                RequestStatus.REQUEST_FINISH, startDate, endDate, "referenceLink", LocalDateTime.of(2022, 3, 7, 0, 0));
        when(realTimeRequestRepository.findByRealTimeRequestIdAndUser(anyLong(), any(User.class)))
                .thenReturn(
                        Optional.of(
                                realTimeRequest
                        )
                );

        LocalDate updateStartDate = LocalDate.of(2022, 3, 8);
        LocalDate updateEndDate = LocalDate.of(2022, 3, 10);
        UpdateRealTimeRequestDto updateRequestDto = createUpdateRealTimeRequestDto(
                "변경된 제목", "변경된 내용", UseType.NOT_COMMERCIAL, true,
                updateStartDate, updateEndDate, "new Link"
        );
        //when
        RealTimeRequestException exception = assertThrows(RealTimeRequestException.class,
                () -> realTimeRequestService.modifyRealTimeRequest(finishRequestId, normalUser, updateRequestDto)
        );

        //then
        assertThat(exception)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription())
        ;

        verify(realTimeRequestRepository).findByRealTimeRequestIdAndUser(anyLong(), any(User.class));
    }

    @Test
    @DisplayName("[실패] 생성 시간 보다 시작 시간이전으로 실시간 의뢰 수정 요청 - UPDATE_START_TIME_MUST_BE_AFTER_CREATE_DATE")
    public void givenRequestIdAndUpdateRequestDto_whenModifyRealTimeRequestButNotValid_thenUpdateStartTimeMustBeAfterCreateDateError(){
        //given
        RealTimeRequestErrorCode errorCode = RealTimeRequestErrorCode.UPDATE_START_TIME_MUST_BE_AFTER_CREATE_DATE;
        long finishRequestId = 1L;
        long userId = 1L;
        NormalUser normalUser = getNormalUser(userId);
        LocalDate startDate = LocalDate.of(2022, 3, 7);
        LocalDate endDate = LocalDate.of(2022, 3, 9);
        LocalDateTime createDateTime = LocalDateTime.of(2022, 3, 7, 0, 0);
        RealTimeRequest realTimeRequest = getRealTimeRequest(
                finishRequestId, "실시간 의뢰 제목", "실시간 의뢰 내용", normalUser, UseType.COMMERCIAL, false,
                RequestStatus.REQUEST_RECRUITING, startDate, endDate, "referenceLink", createDateTime);
        when(realTimeRequestRepository.findByRealTimeRequestIdAndUser(anyLong(), any(User.class)))
                .thenReturn(
                        Optional.of(
                                realTimeRequest
                        )
                );

        LocalDate updateStartDate = LocalDate.of(2022, 3, 6);
        LocalDate updateEndDate = LocalDate.of(2022, 3, 10);
        UpdateRealTimeRequestDto updateRequestDto = createUpdateRealTimeRequestDto(
                "변경된 제목", "변경된 내용", UseType.NOT_COMMERCIAL, true,
                updateStartDate, updateEndDate, "new Link"
        );
        //when
        RealTimeRequestException exception = assertThrows(RealTimeRequestException.class,
                () -> realTimeRequestService.modifyRealTimeRequest(finishRequestId, normalUser, updateRequestDto)
        );

        //then
        assertThat(exception)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription())
        ;
        verify(realTimeRequestRepository).findByRealTimeRequestIdAndUser(anyLong(), any(User.class));
    }

    @Test
    @DisplayName("[성공] 실시간 의뢰 마감 설정")
    public void givenRequestId_whenModifyStatus_thenNothing(){
        //given
        long requestId = 1L;
        long userId = 1L;
        NormalUser normalUser = getNormalUser(userId);

        RealTimeRequest realTimeRequest = getRealTimeRequest(requestId, RequestStatus.REQUEST_RECRUITING);
        RequestStatus beforeStatus = realTimeRequest.getStatus();

        when(realTimeRequestRepository.findByRealTimeRequestIdAndUser(anyLong(), any(User.class)))
                .thenReturn(
                        Optional.of(
                                realTimeRequest
                        )
                );

        //when
        realTimeRequestService.modifyRequestStatus(requestId, normalUser);

        //then
        assertAll(
                () -> assertThat(beforeStatus).isEqualTo(RequestStatus.REQUEST_RECRUITING),
                () -> assertThat(realTimeRequest.getStatus()).isEqualTo(RequestStatus.REQUEST_FINISH)
        );
        verify(realTimeRequestRepository).findByRealTimeRequestIdAndUser(anyLong(), any(User.class));
    }

    @Test
    @DisplayName("[실패] 삭제된 의뢰 마감 설정 요청 - REAL_TIME_REQUEST_DELETED")
    public void givenDeletedRequestId_whenModifyStatus_thenRealTimeRequestDeletedError(){
        //given
        long requestId = 1L;
        long userId = 1L;
        NormalUser normalUser = getNormalUser(userId);
        RealTimeRequestErrorCode errorCode = RealTimeRequestErrorCode.REAL_TIME_REQUEST_DELETED;

        when(realTimeRequestRepository.findByRealTimeRequestIdAndUser(anyLong(), any(User.class)))
                .thenReturn(
                        Optional.of(
                                getRealTimeRequest(requestId, RequestStatus.REQUEST_DELETED)
                        )
                );

        //when
        RealTimeRequestException exception = assertThrows(RealTimeRequestException.class,
                () -> realTimeRequestService.modifyRequestStatus(requestId, normalUser)
        );

        //then
        assertThat(exception)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription())
        ;

        verify(realTimeRequestRepository).findByRealTimeRequestIdAndUser(anyLong(), any(User.class));
    }

    @Test
    @DisplayName("[실패] 마감된 의뢰 마감 설정 요청 - REAL_TIME_REQUEST_STATUS_ALREADY_FINISH")
    public void givenFinishedRequestId_whenModifyStatus_thenRealTimeRequestStatusAlreadyFinishError(){
        //given
        long requestId = 1L;
        long userId = 1L;
        NormalUser normalUser = getNormalUser(userId);
        RealTimeRequestErrorCode errorCode = RealTimeRequestErrorCode.REAL_TIME_REQUEST_STATUS_ALREADY_FINISH;

        when(realTimeRequestRepository.findByRealTimeRequestIdAndUser(anyLong(), any(User.class)))
                .thenReturn(
                        Optional.of(
                                getRealTimeRequest(requestId, RequestStatus.REQUEST_FINISH)
                        )
                );

        //when
        RealTimeRequestException exception = assertThrows(RealTimeRequestException.class,
                () -> realTimeRequestService.modifyRequestStatus(requestId, normalUser)
        );

        //then
        assertThat(exception)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription())
        ;

        verify(realTimeRequestRepository).findByRealTimeRequestIdAndUser(anyLong(), any(User.class));
    }
}
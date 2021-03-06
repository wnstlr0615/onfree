package com.onfree.controller;

import com.onfree.anotation.WithArtistUser;
import com.onfree.anotation.WithNormalUser;
import com.onfree.common.ControllerBaseTest;
import com.onfree.common.error.code.RealTimeRequestErrorCode;
import com.onfree.common.error.exception.RealTimeRequestException;
import com.onfree.controller.realtimerequest.RealTimeRequestController;
import com.onfree.core.dto.realtimerequest.CreateRealTimeRequestDto;
import com.onfree.core.dto.realtimerequest.RealTimeRequestDetailDto;
import com.onfree.core.dto.realtimerequest.SimpleRealtimeRequestDto;
import com.onfree.core.dto.realtimerequest.UpdateRealTimeRequestDto;
import com.onfree.core.dto.user.artist.MobileCarrier;
import com.onfree.core.entity.realtimerequset.RequestStatus;
import com.onfree.core.entity.realtimerequset.UseType;
import com.onfree.core.entity.user.*;
import com.onfree.core.service.realtimerequest.RealTimeRequestService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest( controllers = RealTimeRequestController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class RealTimeRequestControllerTest extends ControllerBaseTest {
    @MockBean
    RealTimeRequestService realTimeRequestService;

    @Test
    @DisplayName("[??????][GET] ????????? ?????? ????????? ?????????")
    public void givenPageAndPageSize_whenRealTimeRequestList_thenPagingList() throws Exception{
        //given
        int page = 0;
        int size = 10;
        int total = 6;

        PageRequest pageRequest = PageRequest.of(page, size);

        List<SimpleRealtimeRequestDto> simpleRealtimeRequestDtos =  List.of(
                createSimpleRealTimeRequestDto(1L, "??????1", RequestStatus.REQUEST_RECRUITING.getDisplayStatus()),
                createSimpleRealTimeRequestDto(2L, "??????2", RequestStatus.REQUEST_RECRUITING.getDisplayStatus()),
                createSimpleRealTimeRequestDto(3L, "??????3", RequestStatus.REQUEST_RECRUITING.getDisplayStatus()),
                createSimpleRealTimeRequestDto(4L, "??????4", RequestStatus.REQUEST_FINISH.getDisplayStatus()),
                createSimpleRealTimeRequestDto(5L, "??????5", RequestStatus.REQUEST_FINISH.getDisplayStatus()),
                createSimpleRealTimeRequestDto(6L, "??????6", RequestStatus.REQUEST_FINISH.getDisplayStatus())
        );

        PageImpl<SimpleRealtimeRequestDto> dtoPage = new PageImpl<>(simpleRealtimeRequestDtos, pageRequest, total);

        when(realTimeRequestService.findAllRealTimeRequest(page, size))
                .thenReturn(
                        dtoPage
                );
        //when //then
        mvc.perform(get("/api/v1/real-time-requests")
            .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.items[0].realTimeRequestId").value(1L))
            .andExpect(jsonPath("$._embedded.items[0].title").value("??????1"))
            .andExpect(jsonPath("$._embedded.items[0].status").value(RequestStatus.REQUEST_RECRUITING.getDisplayStatus()))
            .andExpect(jsonPath("$._embedded.items[0].nickname").isNotEmpty())
            .andExpect(jsonPath("$._embedded.items[0].startDate").isNotEmpty())
            .andExpect(jsonPath("$._embedded.items[0].endDate").isNotEmpty())
            .andExpect(jsonPath("$._embedded.items[0].createDate").isNotEmpty())
            .andExpect(jsonPath("$._embedded.items[0]._links.self").isNotEmpty())
            .andExpect(jsonPath("$._links.self").isNotEmpty())
            .andExpect(jsonPath("$._links.profile").isNotEmpty())

        ;
        verify(realTimeRequestService).findAllRealTimeRequest(anyInt(), anyInt());
    }

    private SimpleRealtimeRequestDto createSimpleRealTimeRequestDto(long requestId, String title, String status) {
        LocalDate startDate = LocalDate.of(2022,3,3);
        LocalDate endDate = LocalDate.of(2022,3,6);
        LocalDate createDate = LocalDate.of(2022,3,2);
        String nickname = "?????????";
        return SimpleRealtimeRequestDto.createSimpleRealtimeRequestDto(requestId, title, nickname, status, startDate, endDate, createDate);
    }

    @Test
    @DisplayName("[??????][GET]????????? ?????? ?????? ??????")
    public void givenRequestId_whenRealTimeRequestDetails_thenReturnDetailDto() throws Exception{
        //given
        long requestId = 1L;
        String title = "??????";
        UseType useType = UseType.COMMERCIAL;

        when(realTimeRequestService.findOneRealTimeRequest(anyLong()))
                .thenReturn(
                        getRealTimeRequestDetailDto(1L, title, useType)
                );

        //when //then
        mvc.perform(get("/api/v1/real-time-requests/{requestId}", requestId)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.realTimeRequestId").value(requestId))
            .andExpect(jsonPath("$.title").value(title))
            .andExpect(jsonPath("$.content").isNotEmpty())
            .andExpect(jsonPath("$.nickname").isNotEmpty())
            .andExpect(jsonPath("$.startDate").isNotEmpty())
            .andExpect(jsonPath("$.endDate").isNotEmpty())
            .andExpect(jsonPath("$.useType").value(useType.name()))
            .andExpect(jsonPath("$.referenceLink").isNotEmpty())
            .andExpect(jsonPath("$.adult").isNotEmpty())
            .andExpect(jsonPath("$.status").isNotEmpty())
            .andExpect(jsonPath("$.createDate").isNotEmpty())
            .andExpect(jsonPath("$._links.self").isNotEmpty())
            .andExpect(jsonPath("$._links.profile").isNotEmpty())
        ;
        verify(realTimeRequestService).findOneRealTimeRequest(eq(requestId));
    }

    private RealTimeRequestDetailDto getRealTimeRequestDetailDto(long requestId, String title, UseType useType) {
        String content = "??????";
        String nickname = "nickname";
        LocalDate startDate = LocalDate.of(2202, 3, 7);
        LocalDate endDate = LocalDate.of(2202, 3, 8);
        String referenceLink = "referenceLink";
        boolean adult = false;
        LocalDate createDate = LocalDate.of(2022, 3, 7);
        return RealTimeRequestDetailDto
                .createRealTimeRequestDetail(requestId, title, content, nickname, startDate, endDate, useType, referenceLink, adult, RequestStatus.REQUEST_RECRUITING, createDate);
    }

    @Test
    @DisplayName("[??????][GET]?????? ????????? ?????? ?????? ?????? - NOT_FOUND_REAL_TIME_REQUEST")
    public void givenNotFoundRequestId_whenRealTimeRequestDetails_thenNotFoundRealTimeRequestError() throws Exception{
        //given
        long notFoundRequestId = 9999L;
        RealTimeRequestErrorCode errorCode = RealTimeRequestErrorCode.NOT_FOUND_REAL_TIME_REQUEST;
        when(realTimeRequestService.findOneRealTimeRequest(anyLong()))
                .thenThrow(
                        new RealTimeRequestException(errorCode)
                );

        //when //then
        mvc.perform(get("/api/v1/real-time-requests/{requestId}", notFoundRequestId)
        )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))

        ;
        verify(realTimeRequestService).findOneRealTimeRequest(eq(notFoundRequestId));
    }

    @Test
    @DisplayName("[??????][GET]????????? ????????? ?????? ?????? ?????? - REAL_TIME_REQUEST_DELETED")
    public void givenDeletedRequestId_whenRealTimeRequestDetails_thenRealTimeRequestDeleted() throws Exception{
        //given
        long deletedRequestId = 555L;
        RealTimeRequestErrorCode errorCode = RealTimeRequestErrorCode.REAL_TIME_REQUEST_DELETED;
        when(realTimeRequestService.findOneRealTimeRequest(anyLong()))
                .thenThrow(
                        new RealTimeRequestException(errorCode)
                );

        //when //then
        mvc.perform(get("/api/v1/real-time-requests/{requestId}", deletedRequestId)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))

        ;
        verify(realTimeRequestService).findOneRealTimeRequest(eq(deletedRequestId));
    }

    @Test
    @WithArtistUser
    @DisplayName("[??????][POST]?????? ?????? ????????? ?????? ????????????")
    public void givenCreateRequestDto_whenRealTimeRequestAddByArtistUser_thenReturnCreateResponseDto() throws Exception{
        //given
        ArtistUser artistUser = getArtistUser(1L);

        long realTimeRequestId = 1L;
        String title = "????????? ?????? ??????";
        UseType useType = UseType.COMMERCIAL;
        boolean adult = false;
        String jsonData = mapper.writeValueAsString(
                createRealTimeRequestDto(title, useType, adult)
        );

        MockMultipartFile  data = new MockMultipartFile("data", "data", MediaType.APPLICATION_JSON_VALUE, jsonData.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile imageFile = new MockMultipartFile("files", "referenceImageFile", MediaType.IMAGE_PNG_VALUE, "image".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile textFile = new MockMultipartFile("files", "referenceTextFile", MediaType.TEXT_PLAIN_VALUE, "text".getBytes(StandardCharsets.UTF_8));

        when(realTimeRequestService.addRealTimeRequest(any(User.class), any(), any()))
                .thenReturn(
                        getCreateRealTimeRequestResponse(realTimeRequestId, title, useType, adult)
                );
        //when //then
        mvc.perform(multipart("/api/v1/real-time-requests")
                .file(data)
                .file(imageFile)
                .file(textFile)
                .with(authentication(new UsernamePasswordAuthenticationToken(artistUser, null, getAuthority("ROLE_ARTIST"))))
        )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(header().string("location", Matchers.containsString("/api/v1/real-time-requests/" + realTimeRequestId)))
            .andExpect(jsonPath("$.realTimeRequestId").value(realTimeRequestId))
            .andExpect(jsonPath("$.title").value(title))
            .andExpect(jsonPath("$.content").isNotEmpty())
            .andExpect(jsonPath("$.startDate").isNotEmpty())
            .andExpect(jsonPath("$.endDate").isNotEmpty())
            .andExpect(jsonPath("$.useType").value(useType.name()))
            .andExpect(jsonPath("$.referenceLink").isNotEmpty())
            .andExpect(jsonPath("$.adult").isNotEmpty())
            .andExpect(jsonPath("$._links.self").isNotEmpty())
            .andExpect(jsonPath("$._links.profile").isNotEmpty())
        ;
        verify(realTimeRequestService).addRealTimeRequest(any(User.class), any(), any());

    }

    private CreateRealTimeRequestDto.Response getCreateRealTimeRequestResponse(long realTimeRequestId, String title, UseType useType, Boolean adult) {
        String content = "????????? ?????? ???????????????.";
        LocalDate startDate = LocalDate.of(2022, 3, 5);
        LocalDate endDate = LocalDate.of(2022, 3, 7);
        String referenceLink = "referenceLink";
        return CreateRealTimeRequestDto.Response.createRealTimeRequestDtoResponse(realTimeRequestId, title, content, startDate, endDate, useType, referenceLink, adult);
    }

    private CreateRealTimeRequestDto.Request createRealTimeRequestDto(String title, UseType useType, boolean adult) {
        String content = "????????? ?????? ???????????????.";
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(3);
        String referenceLink = "referenceLink";

        return CreateRealTimeRequestDto.Request.createRealTimeRequestDtoRequest(title, content, startDate, endDate, useType, referenceLink, adult);
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
    private List<SimpleGrantedAuthority> getAuthority(String role) {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Test
    @WithNormalUser
    @DisplayName("[??????][POST]?????? ?????? ????????? ?????? ????????????")
    public void givenCreateRequestDto_whenRealTimeRequestAddByNormalUser_thenReturnCreateResponseDto() throws Exception{
        //given
        NormalUser normalUser = getNormalUser(1L);

        long realTimeRequestId = 1L;
        String title = "????????? ?????? ??????";
        UseType useType = UseType.COMMERCIAL;
        boolean adult = false;

        String jsonData = mapper.writeValueAsString(
                createRealTimeRequestDto(title, useType, adult)
        );

        MockMultipartFile  data = new MockMultipartFile("data", "data", MediaType.APPLICATION_JSON_VALUE, jsonData.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile imageFile = new MockMultipartFile("files", "referenceImageFile", MediaType.IMAGE_PNG_VALUE, "image".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile textFile = new MockMultipartFile("files", "referenceTextFile", MediaType.TEXT_PLAIN_VALUE, "text".getBytes(StandardCharsets.UTF_8));

        when(realTimeRequestService.addRealTimeRequest(any(User.class), any(), any()))
                .thenReturn(
                        getCreateRealTimeRequestResponse(realTimeRequestId, title, useType, adult)
                );
        //when //then
        mvc.perform(multipart("/api/v1/real-time-requests")
                .file(data)
                .file(imageFile)
                .file(textFile)
                .with(authentication(new UsernamePasswordAuthenticationToken(normalUser, null, getAuthority("ROLE_NORMAL"))))
                .content(
                        mapper.writeValueAsString(
                                createRealTimeRequestDto(title, useType, adult)
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("location", Matchers.containsString("/api/v1/real-time-requests/" + realTimeRequestId)))
                .andExpect(jsonPath("$.realTimeRequestId").value(realTimeRequestId))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.startDate").isNotEmpty())
                .andExpect(jsonPath("$.endDate").isNotEmpty())
                .andExpect(jsonPath("$.useType").value(useType.name()))
                .andExpect(jsonPath("$.referenceLink").isNotEmpty())
                .andExpect(jsonPath("$.adult").isNotEmpty())
                .andExpect(jsonPath("$._links.self").isNotEmpty())
                .andExpect(jsonPath("$._links.profile").isNotEmpty())
        ;
        verify(realTimeRequestService).addRealTimeRequest(any(User.class), any(), any());

    }
    public NormalUser getNormalUser(long userId){
        return NormalUser.builder()
                .userId(userId)
                .adultCertification(Boolean.TRUE)
                .email("jun@naver.com")
                .password("!Abcderghijk112")
                .gender(Gender.MAN)
                .name("??????")
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



    @Test
    @DisplayName("[??????][PUT] ?????? ?????? ????????? ?????? ??????")
    public void givenRequestIdAndUpdateRealTimeRequestDto_whenRealTimRequestModifyByArtistUser_thenNothing() throws Exception{
        //given
        long requestId = 1L;
        ArtistUser artistUser = getArtistUser(requestId);
        doNothing().when(realTimeRequestService)
                .modifyRealTimeRequest(anyLong(), any(User.class), any(UpdateRealTimeRequestDto.class), any());
        String jsonData = mapper.writeValueAsString(
                createUpdateRealTimeRequestDto("????????? ??????", UseType.NOT_COMMERCIAL, true)
        );
        MockMultipartFile jsonFile = createMultipartFile("data", "image.png", MediaType.APPLICATION_JSON_VALUE, jsonData);
        MockMultipartFile imageFile = createMultipartFile("files", "image.png", MediaType.IMAGE_PNG_VALUE, "imageFile");
        MockMultipartFile textFile = createMultipartFile("files", "text.txt", MediaType.TEXT_PLAIN_VALUE, "textFile");
        //when //then



        mvc.perform(multipart("/api/v1/real-time-requests/{requestId}", requestId)
                .file(jsonFile)
                .file(imageFile)
                .file(textFile)
                .with(authentication(new UsernamePasswordAuthenticationToken(artistUser, null, getAuthority("ROLE_ARTIST"))))
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(true))
            .andExpect(jsonPath("$.message").value("??????????????? ????????? ?????????????????????."))
            .andExpect(jsonPath("$._links.self").isNotEmpty())
            .andExpect(jsonPath("$._links.profile").isNotEmpty())
        ;
        verify(realTimeRequestService).modifyRealTimeRequest(eq(requestId), any(User.class), any(UpdateRealTimeRequestDto.class), any());

    }

    private UpdateRealTimeRequestDto createUpdateRealTimeRequestDto(String title, UseType useType, boolean adult) {
        String content = "????????? ?????? ????????? ???????????????.";
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(3);
        String referenceLink = "referenceLink";
        return UpdateRealTimeRequestDto.createUpdateRealTimeRequestDto(title, content, startDate, endDate, useType, referenceLink, adult);
    }

    @Test
    @DisplayName("[??????][POST]?????? ?????? ????????? ?????? ??????")
    public void givenRequestIdAndUpdateRealTimeRequestDto_whenRealTimRequestModifyByNormalUser_thenNothing() throws Exception{
        //given
        long requestId = 1L;
        NormalUser normalUser = getNormalUser(requestId);
        doNothing().when(realTimeRequestService)
                .modifyRealTimeRequest(anyLong(), any(User.class), any(UpdateRealTimeRequestDto.class), any());

        //when //then
        String jsonData = mapper.writeValueAsString(
                createUpdateRealTimeRequestDto("????????? ??????", UseType.NOT_COMMERCIAL, true)
        );

        MockMultipartFile jsonFile = createMultipartFile("data", "image.png", MediaType.APPLICATION_JSON_VALUE, jsonData);
        MockMultipartFile updateTextFile = createMultipartFile("files","updateFile.txt", MediaType.TEXT_PLAIN_VALUE, "update");

        mvc.perform(multipart("/api/v1/real-time-requests/{requestId}", requestId)
                .file(jsonFile)
                .file(updateTextFile)
                .with(authentication(new UsernamePasswordAuthenticationToken(normalUser, null, getAuthority("ROLE_ARTIST"))))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.message").value("??????????????? ????????? ?????????????????????."))
                .andExpect(jsonPath("$._links.self").isNotEmpty())
                .andExpect(jsonPath("$._links.profile").isNotEmpty())
        ;
        verify(realTimeRequestService).modifyRealTimeRequest(eq(requestId), any(User.class), any(UpdateRealTimeRequestDto.class), any());
    }

    private MockMultipartFile createMultipartFile(String name, String originalFilename, String contentType, String content) {
        return new MockMultipartFile(name, originalFilename, contentType, content.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("[??????][POST] ????????? ????????? ?????? ?????? ?????? - REAL_TIME_REQUEST_DELETED")
    public void givenDeletedRequestIdAndUpdateRealTimeRequestDto_whenRealTimRequestModify_thenRealTimeRequestDeletedError() throws Exception{
        //given
        long deletedRequestId = 1L;
        ArtistUser artistUser = getArtistUser(deletedRequestId);
        RealTimeRequestErrorCode errorCode = RealTimeRequestErrorCode.REAL_TIME_REQUEST_DELETED;
        doThrow(new RealTimeRequestException(errorCode)).when(realTimeRequestService)
                .modifyRealTimeRequest(anyLong(), any(User.class), any(UpdateRealTimeRequestDto.class), any());

        //when //then
        String jsonData = mapper.writeValueAsString(
                createUpdateRealTimeRequestDto("????????? ??????", UseType.NOT_COMMERCIAL, true)
        );
        MockMultipartFile jsonFile = createMultipartFile("data", "image.png", MediaType.APPLICATION_JSON_VALUE, jsonData);


        mvc.perform(multipart("/api/v1/real-time-requests/{requestId}", deletedRequestId)
                .file(jsonFile)
                .with(authentication(new UsernamePasswordAuthenticationToken(artistUser, null, getAuthority("ROLE_ARTIST"))))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))

        ;
        verify(realTimeRequestService).modifyRealTimeRequest(eq(deletedRequestId), any(User.class), any(UpdateRealTimeRequestDto.class), any());
    }

    @Test
    @DisplayName("[??????][PUT] ????????? ????????? ?????? ?????? ?????? - FINISH_REQUEST_CAN_NOT_UPDATE")
    public void givenFinishRequestIdAndUpdateRealTimeRequestDto_whenRealTimRequestModify_thenFinishRequestCanNotUpdateError() throws Exception{
        //given
        long deletedRequestId = 1L;
        ArtistUser artistUser = getArtistUser(deletedRequestId);
        RealTimeRequestErrorCode errorCode = RealTimeRequestErrorCode.FINISH_REQUEST_CAN_NOT_UPDATE;
        doThrow(new RealTimeRequestException(errorCode)).when(realTimeRequestService)
                .modifyRealTimeRequest(anyLong(), any(User.class), any(UpdateRealTimeRequestDto.class), any());
        String jsonData = mapper.writeValueAsString(
                createUpdateRealTimeRequestDto("????????? ??????", UseType.NOT_COMMERCIAL, true)
        );
        MockMultipartFile jsonFile = createMultipartFile("data", "image.png", MediaType.APPLICATION_JSON_VALUE, jsonData);

        //when //then
        mvc.perform(multipart("/api/v1/real-time-requests/{requestId}", deletedRequestId)
                .file(jsonFile)
                .with(authentication(new UsernamePasswordAuthenticationToken(artistUser, null, getAuthority("ROLE_ARTIST"))))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))

        ;
        verify(realTimeRequestService).modifyRealTimeRequest(eq(deletedRequestId), any(User.class), any(UpdateRealTimeRequestDto.class), any());
    }

    @Test
    @DisplayName("[??????][DELETED] ????????? ?????? ?????? ??????")
    public void givenDeleteRequestId_whenRemoveRealTimeRequest_thenNothing() throws Exception{
        //given
        long deleteRequestId = 1L;
        ArtistUser artistUser = getArtistUser(deleteRequestId);
        doNothing().when(realTimeRequestService)
                .removeRealTimeRequest(anyLong(), any(User.class));
        //when //then
        mvc.perform(delete("/api/v1/real-time-requests/{requestId}", deleteRequestId)
            .with(authentication(new UsernamePasswordAuthenticationToken(artistUser, null, getAuthority("ROLE_ARTIST"))))
            .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(true))
            .andExpect(jsonPath("$.message").value("????????? ????????? ??????????????? ?????? ???????????????."))
            .andExpect(jsonPath("$._links.self").isNotEmpty())
            .andExpect(jsonPath("$._links.profile").isNotEmpty())
        ;
        verify(realTimeRequestService).removeRealTimeRequest(eq(deleteRequestId), any(User.class));
    }

    @Test
    @DisplayName("[??????][DELETED] ?????? ????????? ????????? ?????? ?????? ?????? - REAL_TIME_REQUEST_ALREADY_DELETED")
    public void givenAlreadyDeletedRequestId_whenRemoveRealTimeRequest_thenRealTimeRequestAlreadyDeletedError() throws Exception{
        //given
        long alreadyDeletedRequestId = 1L;
        ArtistUser artistUser = getArtistUser(alreadyDeletedRequestId);
        RealTimeRequestErrorCode errorCode = RealTimeRequestErrorCode.REAL_TIME_REQUEST_ALREADY_DELETED;
        doThrow(new RealTimeRequestException(errorCode)).when(realTimeRequestService)
                .removeRealTimeRequest(anyLong(), any(User.class));
        //when //then
        mvc.perform(delete("/api/v1/real-time-requests/{requestId}", alreadyDeletedRequestId)
                .with(authentication(new UsernamePasswordAuthenticationToken(artistUser, null, getAuthority("ROLE_ARTIST"))))
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(realTimeRequestService).removeRealTimeRequest(eq(alreadyDeletedRequestId), any(User.class));
    }

    @Test
    @DisplayName("[??????][PATCH] ????????? ?????? ?????? ?????? - ?????? ??????")
    public void givenRequestId_whenRequestStatusFinishByArtistUser_thenNothing() throws Exception{
        //given
        long requestId = 1L;
        long userId = 1L;
        ArtistUser artistUser = getArtistUser(userId);

        doNothing().when(realTimeRequestService)
                .modifyRequestStatus(anyLong(), any(User.class));

        //when //then
        mvc.perform(patch("/api/v1/real-time-requests/{requestId}/finish", requestId)
            .with(authentication(new UsernamePasswordAuthenticationToken(artistUser, null, getAuthority("ROLE_ARTIST"))))
            .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result").value(true))
            .andExpect(jsonPath("$.message").value("????????? ????????? ??????????????? ?????? ???????????????."))
            .andExpect(jsonPath("$._links.self").isNotEmpty())
            .andExpect(jsonPath("$._links.profile").isNotEmpty())
        ;
        verify(realTimeRequestService).modifyRequestStatus(anyLong(), any(User.class));
    }

    @Test
    @DisplayName("[??????][PATCH] ????????? ?????? ?????? ?????? - ?????? ??????")
    public void givenRequestId_whenRequestStatusFinishByNormalUser_thenNothing() throws Exception{
        //given
        long requestId = 1L;
        long userId = 1L;
        NormalUser normalUser = getNormalUser(userId);

        doNothing().when(realTimeRequestService)
                .modifyRequestStatus(anyLong(), any(User.class));

        //when //then
        mvc.perform(patch("/api/v1/real-time-requests/{requestId}/finish", requestId)
                .with(authentication(new UsernamePasswordAuthenticationToken(normalUser, null, getAuthority("ROLE_ARTIST"))))
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.message").value("????????? ????????? ??????????????? ?????? ???????????????."))
                .andExpect(jsonPath("$._links.self").isNotEmpty())
                .andExpect(jsonPath("$._links.profile").isNotEmpty())
        ;
        verify(realTimeRequestService).modifyRequestStatus(anyLong(), any(User.class));
    }

    @Test
    @DisplayName("[??????][PATCH] ????????? ????????? ?????? ?????? ?????? ?????? - REAL_TIME_REQUEST_DELETED")
    public void givenDeleteRequestId_whenRequestStatusFinishByNormalUser_thenRealTimeRequestDeletedError() throws Exception{
        //given
        long deletedRequestId = 1L;
        long userId = 1L;
        NormalUser normalUser = getNormalUser(userId);

        RealTimeRequestErrorCode errorCode = RealTimeRequestErrorCode.REAL_TIME_REQUEST_DELETED;

        doThrow(new RealTimeRequestException(errorCode)).when(realTimeRequestService)
                .modifyRequestStatus(anyLong(), any(User.class));

        //when //then
        mvc.perform(patch("/api/v1/real-time-requests/{requestId}/finish", deletedRequestId)
                .with(authentication(new UsernamePasswordAuthenticationToken(normalUser, null, getAuthority("ROLE_ARTIST"))))
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(realTimeRequestService).modifyRequestStatus(anyLong(), any(User.class));
    }


    @Test
    @DisplayName("[??????][PATCH] ?????? ????????? ????????? ?????? ?????? ?????? ?????? - REAL_TIME_REQUEST_STATUS_ALREADY_FINISH")
    public void givenDeleteRequestId_whenRequestStatusFinishByNormalUser_thenRealTimeRequestStatusAlreadyFinishError() throws Exception{
        //given
        long finishRequestId = 1L;
        long userId = 1L;
        NormalUser normalUser = getNormalUser(userId);

        RealTimeRequestErrorCode errorCode = RealTimeRequestErrorCode.FINISH_REQUEST_CAN_NOT_UPDATE;

        doThrow(new RealTimeRequestException(errorCode)).when(realTimeRequestService)
                .modifyRequestStatus(anyLong(), any(User.class));

        //when //then
        mvc.perform(patch("/api/v1/real-time-requests/{requestId}/finish", finishRequestId)
                .with(authentication(new UsernamePasswordAuthenticationToken(normalUser, null, getAuthority("ROLE_ARTIST"))))
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(realTimeRequestService).modifyRequestStatus(anyLong(), any(User.class));
    }
}
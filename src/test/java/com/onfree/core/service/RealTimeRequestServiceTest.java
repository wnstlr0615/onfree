package com.onfree.core.service;

import com.onfree.common.error.code.RealTimeRequestErrorCode;
import com.onfree.common.error.exception.RealTimeRequestException;
import com.onfree.common.model.UploadFile;
import com.onfree.core.dto.realtimerequest.CreateRealTimeRequestDto;
import com.onfree.core.dto.realtimerequest.RealTimeRequestDetailDto;
import com.onfree.core.dto.realtimerequest.SimpleRealtimeRequestDto;
import com.onfree.core.dto.realtimerequest.UpdateRealTimeRequestDto;
import com.onfree.core.dto.user.artist.MobileCarrier;
import com.onfree.core.entity.fileitem.FileItem;
import com.onfree.core.entity.fileitem.FileStatus;
import com.onfree.core.entity.fileitem.FileType;
import com.onfree.core.entity.realtimerequset.RealTimeRequest;
import com.onfree.core.entity.realtimerequset.RequestStatus;
import com.onfree.core.entity.realtimerequset.UseType;
import com.onfree.core.entity.user.*;
import com.onfree.core.repository.FileItemRepository;
import com.onfree.core.repository.RealTimeRequestRepository;
import com.onfree.core.service.realtimerequest.RealTimeRequestService;
import com.onfree.utils.AwsS3Component;
import com.onfree.utils.FileStore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RealTimeRequestServiceTest {
    @Spy
    RealTimeRequestRepository realTimeRequestRepository;
    @Mock
    FileStore fileStore;
    @Mock
    AwsS3Component s3Component;
    @Spy
    FileItemRepository fileItemRepository;

    @InjectMocks
    RealTimeRequestService realTimeRequestService;

    @Test
    @DisplayName("[??????] ????????? ?????? ????????? ????????? ??????")
    public void givenPageAndSize_whenFindAllRealTimeRequest_thenReturnPagingList(){
        //given
        ArtistUser artistUser = getArtistUser(1L);
        NormalUser otherNormalUser = getNormalUser(2L);

        List<RealTimeRequest> realTimeRequests = List.of(
                createRealTimeRequest(1L, "??????1 ????????? / ?????????", RequestStatus.REQUEST_RECRUITING, UseType.COMMERCIAL, artistUser, true),
                createRealTimeRequest(2L, "??????2 ????????? / ??? ?????????", RequestStatus.REQUEST_RECRUITING, UseType.NOT_COMMERCIAL, artistUser, true),
                createRealTimeRequest(3L, "??????3 ????????? / ?????????", RequestStatus.REQUEST_RECRUITING, UseType.COMMERCIAL, artistUser, true),
                createRealTimeRequest(4L, "??????4 ????????? / ??? ?????????", RequestStatus.REQUEST_FINISH, UseType.NOT_COMMERCIAL, otherNormalUser, true),
                createRealTimeRequest(5L, "??????5 ????????? / ?????????", RequestStatus.REQUEST_FINISH, UseType.COMMERCIAL, otherNormalUser, true),
                createRealTimeRequest(6L, "??????6 ????????? / ??? ?????????", RequestStatus.REQUEST_FINISH, UseType.NOT_COMMERCIAL, otherNormalUser, true)
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
                .hasFieldOrPropertyWithValue("title", "??????1 ????????? / ?????????")
                .hasFieldOrPropertyWithValue("nickname", artistUser.getNickname())
                .hasFieldOrPropertyWithValue("status", RequestStatus.REQUEST_RECRUITING.getDisplayStatus())
                .hasFieldOrProperty("startDate")
                .hasFieldOrProperty("endDate")
                .hasFieldOrProperty("createDate")
        ;

        verify(realTimeRequestRepository).findAllByStatusNot(eq(pageRequest), eq(RequestStatus.REQUEST_DELETED));
    }

    private RealTimeRequest createRealTimeRequest(long realTimeRequestId, String title, RequestStatus status, UseType useType, User user, boolean adult) {
        String content = "????????? ?????? ??????";
        LocalDate startDate = LocalDate.of(2022,3,2);
        LocalDate endDate = LocalDate.of(2022,3,5);
        String referenceLink = "http://naver.com";
        LocalDateTime createdDate = LocalDateTime.of(2022, 3 ,2, 0, 0);
        String referenceFiles = getReferenceFiles();
        
        return createRealTimeRequest(realTimeRequestId, title, content, user, useType, adult, status, startDate, endDate, referenceLink, referenceFiles, createdDate);
    }

    private List<MultipartFile> getMultipartFileListContainTextFileAndImageFile() {
        return List.of(
                createMockMultipartFile("test.txt", MediaType.TEXT_PLAIN_VALUE, "textFile"),
                createMockMultipartFile("image.png", MediaType.IMAGE_PNG_VALUE, "imageFile")
        );
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
    @DisplayName("[??????] ????????? ?????? ??????  ??????")
    public void givenRequestId_whenFindOneRealTimeRequest_thenReturnRealTimeRequestDetailDto(){
        //given
        long realTimeRequestId = 1L;
        String title = "????????? ?????? ??????";
        String content = "????????? ?????? ??????";
        User user = getArtistUser(realTimeRequestId);
        UseType useType = UseType.COMMERCIAL;
        boolean adult = false;
        RequestStatus status = RequestStatus.REQUEST_REQUESTING;
        when(realTimeRequestRepository.findByRealTimeRequestId(realTimeRequestId))
                .thenReturn(
                        Optional.of(
                                createRealTimeRequest(realTimeRequestId, title, content, user, useType, adult, status)
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

    private RealTimeRequest createRealTimeRequest(long realTimeRequestId, String title, String content, User user, UseType useType, boolean adult, RequestStatus status) {
        LocalDate startDate = LocalDate.of(2022, 3, 7);
        LocalDate endDate = LocalDate.of(2022, 3, 9);
        String referenceFiles = getReferenceFiles();

        return createRealTimeRequest(realTimeRequestId, title, content, user, useType, adult, status, startDate, endDate, "referenceLink", referenceFiles, LocalDateTime.of(2022, 3, 7, 0, 0));
    }

    @Test
    @DisplayName("[??????] ?????? ????????? ?????? ?????? ?????? - NOT_FOUND_REAL_TIME_REQUEST " )
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
    @DisplayName("[??????] ????????? ????????? ?????? ?????? ?????? - REAL_TIME_REQUEST_DELETED " )
    public void givenDeletedRequestId_whenFindOneRealTimeRequest_thenRealTimeRequestDeletedError(){
        //given
        RealTimeRequestErrorCode errorCode = RealTimeRequestErrorCode.REAL_TIME_REQUEST_DELETED;
        long deletedRequestId = 1L;

        RequestStatus status = RequestStatus.REQUEST_DELETED;
        when(realTimeRequestRepository.findByRealTimeRequestId(deletedRequestId))
                .thenReturn(
                        Optional.of(
                                createRealTimeRequest(deletedRequestId, status)
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

    private RealTimeRequest createRealTimeRequest(Long requestId, RequestStatus status) {
        String title = "????????? ?????? ??????";
        String content = "????????? ?????? ??????";
        User user = getArtistUser(requestId);
        UseType useType = UseType.COMMERCIAL;
        boolean adult = false;
        return createRealTimeRequest(requestId, title, content, user, useType, adult, status);
    }


    @Test
    @Disabled
    @DisplayName("[??????] ????????? ?????? ???????????? - ?????? ??????")
    public void givenCreateRealTimeRequestDto_whenAddRealTimeRequestByArtistUser_thenCreateRealTimeRequestResponse(){
        //given
        long userId = 1L;
        ArtistUser artistUser = getArtistUser(userId);
        String title = "????????? ?????? ??????";
        String content = "????????? ?????? ??????";
        UseType useType = UseType.COMMERCIAL;
        boolean adult = false;

        CreateRealTimeRequestDto.Request request = createRealTimeRequestDtoRequest(title, content, useType, adult);

        long requestId = 1L;
        when(realTimeRequestRepository.save(any(RealTimeRequest.class)))
                .thenReturn(
                        createRealTimeRequest(requestId, title, content, artistUser, useType, adult, RequestStatus.REQUEST_REQUESTING)
                );
        List<MultipartFile> multipartFiles = new ArrayList<>();
        //when
        CreateRealTimeRequestDto.Response response = realTimeRequestService.addRealTimeRequest(artistUser, request, multipartFiles);


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
    @DisplayName("[??????] ????????? ?????? ???????????? - ?????? ??????")
    public void givenCreateRealTimeRequestDto_whenAddRealTimeRequestByNormalUser_thenCreateRealTimeRequestResponse() throws IOException {
        //given
        long userId = 1L;
        NormalUser normalUser = getNormalUser(userId);
        String title = "????????? ?????? ??????";
        String content = "????????? ?????? ??????";
        UseType useType = UseType.COMMERCIAL;
        boolean adult = false;

        CreateRealTimeRequestDto.Request request = createRealTimeRequestDtoRequest(title, content, useType, adult);
        List<MultipartFile> multipartFiles
                = List.of(createMockMultipartFile("text.txt", MediaType.TEXT_PLAIN_VALUE, "txt"));

        long requestId = 1L;
        when(realTimeRequestRepository.save(any(RealTimeRequest.class)))
                .thenReturn(
                        createRealTimeRequest(requestId, title, content, normalUser, useType, adult, RequestStatus.REQUEST_REQUESTING)
                );
        UploadFile uploadTextFile = UploadFile.createUploadFile("text.txt", String.valueOf(UUID.randomUUID()));
        File textTempFile = File.createTempFile("text", "txt");


        when(fileStore.saveFile(any(MultipartFile.class)))
                .thenReturn(
                        uploadTextFile
                );
        when(fileStore.getFile(eq(uploadTextFile)))
                .thenReturn(textTempFile);
        FileType fileType = FileType.REQUEST_REFERENCE_FILE;
        String bucketPath = "onfree-store" + fileType.getPath();
        when(s3Component.s3FileUpload(any(File.class), eq(fileType)))
                .thenReturn(bucketPath);
        when(fileItemRepository.save(any(FileItem.class)))
                .thenReturn(FileItem.createFileItem(uploadTextFile, bucketPath,FileType.REQUEST_REFERENCE_FILE, FileStatus.USED));

        //when
        CreateRealTimeRequestDto.Response response = realTimeRequestService.addRealTimeRequest(normalUser, request, multipartFiles);

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
        verify(fileStore).saveFile(any(MultipartFile.class));
        verify(fileStore).removeFile(any(File.class));
        verify(s3Component).s3FileUpload(any(File.class), any(FileType.class));
        verify(fileItemRepository).save(any(FileItem.class));


    }



    @Test
    @DisplayName("[??????] ????????? ?????? ????????????")
    public void givenRequestIdAndUpdateRequestDto_whenModifyRealTimeRequest_thenNothing() throws IOException {
        //given
        long requestId = 1L;
        long userId = 1L;
        NormalUser normalUser = getNormalUser(userId);
        LocalDate startDate = LocalDate.of(2022, 3, 7);
        LocalDate endDate = LocalDate.of(2022, 3, 9);
        List<MultipartFile> multipartFiles
                = List.of(createMockMultipartFile("text.txt", MediaType.TEXT_PLAIN_VALUE, "txt"));

        LocalDate updateStartDate = LocalDate.of(2022, 3, 8);
        LocalDate updateEndDate = LocalDate.of(2022, 3, 10);
        UpdateRealTimeRequestDto updateRequestDto = createUpdateRealTimeRequestDto(
                "????????? ??????", "????????? ??????", UseType.NOT_COMMERCIAL, true,
                updateStartDate, updateEndDate, "new Link"
        );
        UploadFile uploadTextFile = UploadFile.createUploadFile("text.txt", String.valueOf(UUID.randomUUID()));
        File textTempFile = File.createTempFile("text", "txt");

        String referenceFiles = uploadTextFile.getStoreFilename();
        RealTimeRequest realTimeRequest = createRealTimeRequest(
                requestId, "????????? ?????? ??????", "????????? ?????? ??????", normalUser, UseType.COMMERCIAL, false,
                RequestStatus.REQUEST_RECRUITING, startDate, endDate, "referenceLink", referenceFiles, LocalDateTime.of(2022, 3, 7, 0, 0));


        when(realTimeRequestRepository.findByRealTimeRequestIdAndUser(anyLong(), any(User.class)))
                    .thenReturn(
                            Optional.of(
                                    realTimeRequest
                            )
                    );




        when(fileStore.saveFile(any(MultipartFile.class)))
                .thenReturn(
                        uploadTextFile
                );
        when(fileStore.getFile(eq(uploadTextFile)))
                .thenReturn(textTempFile);
        FileType fileType = FileType.REQUEST_REFERENCE_FILE;
        String bucketPath = "onfree-store" + fileType.getPath();
        when(s3Component.s3FileUpload(any(File.class), eq(fileType)))
                .thenReturn(bucketPath);
        when(fileItemRepository.save(any(FileItem.class)))
                .thenReturn(FileItem.createFileItem(uploadTextFile, bucketPath,FileType.REQUEST_REFERENCE_FILE, FileStatus.USED));



        //when

        realTimeRequestService.modifyRealTimeRequest(requestId, normalUser, updateRequestDto, multipartFiles);

        //then
        assertThat(realTimeRequest)
                .hasFieldOrPropertyWithValue("realTimeRequestId", requestId)
                .hasFieldOrPropertyWithValue("title", "????????? ??????")
                .hasFieldOrPropertyWithValue("content", "????????? ??????")
                .hasFieldOrPropertyWithValue("startDate", updateStartDate)
                .hasFieldOrPropertyWithValue("endDate", updateEndDate)
                .hasFieldOrPropertyWithValue("useType", UseType.NOT_COMMERCIAL)
                .hasFieldOrPropertyWithValue("adult", true)
                .hasFieldOrPropertyWithValue("referenceLink", "new Link")
                .hasFieldOrPropertyWithValue("referenceFiles", referenceFiles)
                .hasFieldOrPropertyWithValue("status", RequestStatus.REQUEST_RECRUITING)
                .hasFieldOrProperty("user")
        ;
        verify(realTimeRequestRepository).findByRealTimeRequestIdAndUser(anyLong(), any(User.class));
        verify(fileStore).saveFile(any(MultipartFile.class));
        verify(fileStore).removeFile(any(File.class));
        verify(s3Component).s3FileUpload(any(File.class), any(FileType.class));
        verify(fileItemRepository).save(any(FileItem.class));
    }

    private MockMultipartFile createMockMultipartFile(String filename, String contentType, String content) {
        return new MockMultipartFile("files", filename, contentType, content.getBytes(StandardCharsets.UTF_8));
    }

    private RealTimeRequest createRealTimeRequest(long realTimeRequestId, String title, String content, User user, UseType useType, boolean adult, RequestStatus status, LocalDate startDate, LocalDate endDate, String referenceLink, String referenceFiles, LocalDateTime createDateTime) {
        return RealTimeRequest.createRealTimeRequest(
                realTimeRequestId, title, content, user, startDate, endDate,
                useType, referenceLink, referenceFiles, adult, status, createDateTime
        );
    }

    private UpdateRealTimeRequestDto createUpdateRealTimeRequestDto(String title, String content, UseType useType, boolean adult, LocalDate startDate, LocalDate endDate, String referenceLink) {
        return UpdateRealTimeRequestDto.createUpdateRealTimeRequestDto(title, content, startDate, endDate, useType, referenceLink, adult);
    }


    @Test
    @DisplayName("[??????] ????????? ????????? ?????? ?????? ?????? - REAL_TIME_REQUEST_DELETED")
    public void givenDeletedRequestIdAndUpdateRequestDto_whenModifyRealTimeRequest_thenRealTimeRequestDeletedError(){
        //given
        RealTimeRequestErrorCode errorCode = RealTimeRequestErrorCode.REAL_TIME_REQUEST_DELETED;
        long deletedRequestId = 1L;
        long userId = 1L;
        NormalUser normalUser = getNormalUser(userId);
        LocalDate startDate = LocalDate.of(2022, 3, 7);
        LocalDate endDate = LocalDate.of(2022, 3, 9);
        LocalDateTime createDated = LocalDateTime.of(2022, 3, 7, 0, 0);
        String referenceFiles = getReferenceFiles();
        RealTimeRequest realTimeRequest = createRealTimeRequest(
                deletedRequestId, "????????? ?????? ??????", "????????? ?????? ??????", normalUser, UseType.COMMERCIAL, false,
                RequestStatus.REQUEST_DELETED, startDate, endDate, "referenceLink", referenceFiles,createDated);
        when(realTimeRequestRepository.findByRealTimeRequestIdAndUser(anyLong(), any(User.class)))
                .thenReturn(
                        Optional.of(
                                realTimeRequest
                        )
                );

        LocalDate updateStartDate = LocalDate.of(2022, 3, 8);
        LocalDate updateEndDate = LocalDate.of(2022, 3, 10);
        UpdateRealTimeRequestDto updateRequestDto = createUpdateRealTimeRequestDto(
                "????????? ??????", "????????? ??????", UseType.NOT_COMMERCIAL, true,
                updateStartDate, updateEndDate, "new Link"
        );
        List<MultipartFile> files = List.of(createMockMultipartFile("test.txt", MediaType.TEXT_PLAIN_VALUE, "textFile"));
        
        //when
        RealTimeRequestException exception = assertThrows(RealTimeRequestException.class,
                () -> realTimeRequestService.modifyRealTimeRequest(deletedRequestId, normalUser, updateRequestDto, files)
        );

        //then
        assertThat(exception)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription())
        ;
        verify(realTimeRequestRepository).findByRealTimeRequestIdAndUser(anyLong(), any(User.class));
    }

    private String getReferenceFiles() {
        return UUID.randomUUID() + ".txt," + UUID.randomUUID() + ".png";
    }

    @Test
    @DisplayName("[??????] ????????? ????????? ?????? ???????????? - FINISH_REQUEST_CAN_NOT_UPDATE")
    public void givenFinishRequestIdAndUpdateRequestDto_whenModifyRealTimeRequest_thenFinishRequestCanNotUpdateError(){
        //given
        RealTimeRequestErrorCode errorCode = RealTimeRequestErrorCode.FINISH_REQUEST_CAN_NOT_UPDATE;
        long finishRequestId = 1L;
        long userId = 1L;
        NormalUser normalUser = getNormalUser(userId);
        LocalDate startDate = LocalDate.of(2022, 3, 7);
        LocalDate endDate = LocalDate.of(2022, 3, 9);
        String referenceFiles = getReferenceFiles();
        LocalDateTime createDateTime = LocalDateTime.of(2022, 3, 7, 0, 0);
        RealTimeRequest realTimeRequest = createRealTimeRequest(
                finishRequestId, "????????? ?????? ??????", "????????? ?????? ??????", normalUser, UseType.COMMERCIAL, false,
                RequestStatus.REQUEST_FINISH, startDate, endDate, "referenceLink", referenceFiles, createDateTime);
        when(realTimeRequestRepository.findByRealTimeRequestIdAndUser(anyLong(), any(User.class)))
                .thenReturn(
                        Optional.of(
                                realTimeRequest
                        )
                );

        LocalDate updateStartDate = LocalDate.of(2022, 3, 8);
        LocalDate updateEndDate = LocalDate.of(2022, 3, 10);
        UpdateRealTimeRequestDto updateRequestDto = createUpdateRealTimeRequestDto(
                "????????? ??????", "????????? ??????", UseType.NOT_COMMERCIAL, true,
                updateStartDate, updateEndDate, "new Link"
        );

        List<MultipartFile> files = List.of(createMockMultipartFile("test.txt", MediaType.TEXT_PLAIN_VALUE, "textFile"));
        
        //when
        RealTimeRequestException exception = assertThrows(RealTimeRequestException.class,
                () -> realTimeRequestService.modifyRealTimeRequest(finishRequestId, normalUser, updateRequestDto, files)
        );

        //then
        assertThat(exception)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription())
        ;

        verify(realTimeRequestRepository).findByRealTimeRequestIdAndUser(anyLong(), any(User.class));
    }

    @Test
    @DisplayName("[??????] ?????? ?????? ?????? ?????? ?????????????????? ????????? ?????? ?????? ?????? - UPDATE_START_TIME_MUST_BE_AFTER_CREATE_DATE")
    public void givenRequestIdAndUpdateRequestDto_whenModifyRealTimeRequestButNotValid_thenUpdateStartTimeMustBeAfterCreateDateError() throws IOException {
        //given
        RealTimeRequestErrorCode errorCode = RealTimeRequestErrorCode.UPDATE_START_TIME_MUST_BE_AFTER_CREATE_DATE;
        long finishRequestId = 1L;
        long userId = 1L;
        NormalUser normalUser = getNormalUser(userId);
        LocalDate startDate = LocalDate.of(2022, 3, 7);
        LocalDate endDate = LocalDate.of(2022, 3, 9);
        LocalDateTime createDateTime = LocalDateTime.of(2022, 3, 7, 0, 0);
        String referenceFiles = getReferenceFiles();
        RealTimeRequest realTimeRequest = createRealTimeRequest(
                finishRequestId, "????????? ?????? ??????", "????????? ?????? ??????", normalUser, UseType.COMMERCIAL, false,
                RequestStatus.REQUEST_RECRUITING, startDate, endDate, "referenceLink", referenceFiles, createDateTime);
        when(realTimeRequestRepository.findByRealTimeRequestIdAndUser(anyLong(), any(User.class)))
                .thenReturn(
                        Optional.of(
                                realTimeRequest
                        )
                );

        LocalDate updateStartDate = LocalDate.of(2022, 3, 6);
        LocalDate updateEndDate = LocalDate.of(2022, 3, 10);
        UpdateRealTimeRequestDto updateRequestDto = createUpdateRealTimeRequestDto(
                "????????? ??????", "????????? ??????", UseType.NOT_COMMERCIAL, true,
                updateStartDate, updateEndDate, "new Link"
        );
        List<MultipartFile> multipartFiles
                = List.of(createMockMultipartFile("text.txt", MediaType.TEXT_PLAIN_VALUE, "txt"));

        UploadFile uploadTextFile = UploadFile.createUploadFile("text.txt", String.valueOf(UUID.randomUUID()));
        File textTempFile = File.createTempFile("text", "txt");


        when(fileStore.saveFile(any(MultipartFile.class)))
                .thenReturn(
                        uploadTextFile
                );
        when(fileStore.getFile(eq(uploadTextFile)))
                .thenReturn(textTempFile);
        FileType fileType = FileType.REQUEST_REFERENCE_FILE;
        String bucketPath = "onfree-store" + fileType.getPath();
        when(s3Component.s3FileUpload(any(File.class), eq(fileType)))
                .thenReturn(bucketPath);
        when(fileItemRepository.save(any(FileItem.class)))
                .thenReturn(FileItem.createFileItem(uploadTextFile, bucketPath,FileType.REQUEST_REFERENCE_FILE, FileStatus.USED));

        //when
        RealTimeRequestException exception = assertThrows(RealTimeRequestException.class,
                () -> realTimeRequestService.modifyRealTimeRequest(finishRequestId, normalUser, updateRequestDto, multipartFiles)
        );

        //then
        assertThat(exception)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription())
        ;
        verify(realTimeRequestRepository).findByRealTimeRequestIdAndUser(anyLong(), any(User.class));
        verify(realTimeRequestRepository, never()).save(any(RealTimeRequest.class));
        verify(fileStore).saveFile(any(MultipartFile.class));
        verify(fileStore).removeFile(any(File.class));
        verify(s3Component).s3FileUpload(any(File.class), any(FileType.class));
        verify(fileItemRepository).save(any(FileItem.class));
    }

    @Test
    @DisplayName("[??????] ????????? ?????? ?????? ??????")
    public void givenRequestId_whenModifyStatus_thenNothing(){
        //given
        long requestId = 1L;
        long userId = 1L;
        NormalUser normalUser = getNormalUser(userId);

        RealTimeRequest realTimeRequest = createRealTimeRequest(requestId, RequestStatus.REQUEST_RECRUITING);
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
    @DisplayName("[??????] ????????? ?????? ?????? ?????? ?????? - REAL_TIME_REQUEST_DELETED")
    public void givenDeletedRequestId_whenModifyStatus_thenRealTimeRequestDeletedError(){
        //given
        long requestId = 1L;
        long userId = 1L;
        NormalUser normalUser = getNormalUser(userId);
        RealTimeRequestErrorCode errorCode = RealTimeRequestErrorCode.REAL_TIME_REQUEST_DELETED;

        when(realTimeRequestRepository.findByRealTimeRequestIdAndUser(anyLong(), any(User.class)))
                .thenReturn(
                        Optional.of(
                                createRealTimeRequest(requestId, RequestStatus.REQUEST_DELETED)
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
    @DisplayName("[??????] ????????? ?????? ?????? ?????? ?????? - REAL_TIME_REQUEST_STATUS_ALREADY_FINISH")
    public void givenFinishedRequestId_whenModifyStatus_thenRealTimeRequestStatusAlreadyFinishError(){
        //given
        long requestId = 1L;
        long userId = 1L;
        NormalUser normalUser = getNormalUser(userId);
        RealTimeRequestErrorCode errorCode = RealTimeRequestErrorCode.REAL_TIME_REQUEST_STATUS_ALREADY_FINISH;

        when(realTimeRequestRepository.findByRealTimeRequestIdAndUser(anyLong(), any(User.class)))
                .thenReturn(
                        Optional.of(
                                createRealTimeRequest(requestId, RequestStatus.REQUEST_FINISH)
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
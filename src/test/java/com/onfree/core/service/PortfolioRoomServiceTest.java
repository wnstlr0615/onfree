package com.onfree.core.service;

import com.onfree.common.error.code.PortfolioRoomErrorCode;
import com.onfree.common.error.exception.PortfolioRoomException;
import com.onfree.core.dto.drawingfield.artist.UsedDrawingFieldDto;
import com.onfree.core.dto.portfolioroom.PortfolioRoomDetailDto;
import com.onfree.core.dto.portfolioroom.UpdatePortfolioStatusDto;
import com.onfree.core.dto.portfolioroom.UpdateStatusMessageDto;
import com.onfree.core.dto.user.artist.MobileCarrier;
import com.onfree.core.entity.portfolioroom.PortfolioRoom;
import com.onfree.core.entity.portfolioroom.PortfolioRoomStatus;
import com.onfree.core.entity.user.*;
import com.onfree.core.repository.ArtistUserDrawingFieldRepository;
import com.onfree.core.repository.PortfolioRoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioRoomServiceTest {
    @Mock
    PortfolioRoomRepository portfolioRoomRepository;
    @Mock
    ArtistUserDrawingFieldRepository artistUserDrawingFieldRepository;
    @InjectMocks
    PortfolioRoomService portfolioRoomService;

    @Test
    @DisplayName("[성공] 포트폴리오룸 조회하기")
    public void givenPortfolioRoomURL_whenFindOnePortfolioRoom_thenReturnPortfolioRoomDetailDto(){
        //given
        ArtistUser artistUser = getArtistUser(1L);

        String portfolioRoomURL = "joon";
        when(portfolioRoomRepository.findByPortfolioRoomURL(anyString()))
                .thenReturn(
                        Optional.of(
                                createPortfolioRoom(PortfolioRoomStatus.PUBLIC_PORTFOLIO_ROOM)
                        )
                );
        when(artistUserDrawingFieldRepository.findAllUsedDrawingFieldByArtistUser(any(ArtistUser.class)))
                .thenReturn(
                        getUsedDrawingFieldDto()
                );
        //when
        PortfolioRoomDetailDto detailDto = portfolioRoomService.findOnePortfolioRoom(portfolioRoomURL);

        //then
        assertThat(detailDto)
                .hasFieldOrProperty("portFolioRoomId")
                .hasFieldOrPropertyWithValue("portfolioRoomURL", portfolioRoomURL)
                .hasFieldOrPropertyWithValue("nickname", artistUser.getNickname())
                .hasFieldOrPropertyWithValue("profileImage", artistUser.getProfileImage())
                .hasFieldOrPropertyWithValue("starPoint", 0.0)
                .hasFieldOrPropertyWithValue("statusMark", artistUser.getStatusMark())
                .hasFieldOrPropertyWithValue("portfolioRoomStatus", true)
                .hasFieldOrPropertyWithValue("statusMessage", "")
        ;

        verify(portfolioRoomRepository).findByPortfolioRoomURL(eq(portfolioRoomURL));
        verify(artistUserDrawingFieldRepository).findAllUsedDrawingFieldByArtistUser(any(ArtistUser.class));
    }

    private List<UsedDrawingFieldDto> getUsedDrawingFieldDto() {
        return List.of(
                UsedDrawingFieldDto.createUsedDrawingFieldDto("캐릭터 디자인", true),
                UsedDrawingFieldDto.createUsedDrawingFieldDto("일러스트", false),
                UsedDrawingFieldDto.createUsedDrawingFieldDto("메타버스", false)
        );
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

    @Test
    @DisplayName("[실패] 잘못된 주소로 인한 포트폴리오룸 조회하기 실패 - NOT_FOUND_PORTFOLIO_ROOM")
    public void givenWrongPortfolioRoomURL_whenFindOnePortfolioRoom_thenNotFoundPortfolioRoomError(){
        //given
        PortfolioRoomErrorCode errorCode = PortfolioRoomErrorCode.NOT_FOUND_PORTFOLIO_ROOM;
        String portfolioRoomURL = "joon";
        when(portfolioRoomRepository.findByPortfolioRoomURL(anyString()))
                .thenThrow(
                        new PortfolioRoomException(errorCode)
                );

        //when

        PortfolioRoomException exception = assertThrows(PortfolioRoomException.class,
                () -> portfolioRoomService.findOnePortfolioRoom(portfolioRoomURL)
        );

        //then
        assertAll(
                () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(exception.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );

        verify(portfolioRoomRepository).findByPortfolioRoomURL(eq(portfolioRoomURL));
        verify(artistUserDrawingFieldRepository, never()).findAllUsedDrawingFieldByArtistUser(any(ArtistUser.class));
    }

    @Test
    @DisplayName("[실패] 비공개 포트폴리오룸 조회하기 실패 - PORTFOLIO_ROOM_IS_PRIVATE")
    public void givenPrivatePortfolioRoomURL_whenFindOnePortfolioRoom_thenPortfolioRoomIsPrivateError(){
        //given
        PortfolioRoomErrorCode errorCode = PortfolioRoomErrorCode.PORTFOLIO_ROOM_IS_PRIVATE;
        String portfolioRoomURL = "joon";
        when(portfolioRoomRepository.findByPortfolioRoomURL(anyString()))
                .thenThrow(
                        new PortfolioRoomException(errorCode)
                );

        //when

        PortfolioRoomException exception = assertThrows(PortfolioRoomException.class,
                () -> portfolioRoomService.findOnePortfolioRoom(portfolioRoomURL)
        );

        //then
        assertAll(
                () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(exception.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );

        verify(portfolioRoomRepository).findByPortfolioRoomURL(eq(portfolioRoomURL));
        verify(artistUserDrawingFieldRepository, never()).findAllUsedDrawingFieldByArtistUser(any(ArtistUser.class));
    }

    @Test
    @DisplayName("[실패] 운영자에 의해 사용 제한되 포트폴리오룸 조회하기 실패 - NOT_ACCESS_PORTFOLIO_ROOM")
    public void givenNotAccessPortfolioRoomURL_whenFindOnePortfolioRoom_thenNotAccessPortfolioRoomError(){
        //given
        PortfolioRoomErrorCode errorCode = PortfolioRoomErrorCode.NOT_ACCESS_PORTFOLIO_ROOM;
        String portfolioRoomURL = "joon";
        when(portfolioRoomRepository.findByPortfolioRoomURL(anyString()))
                .thenThrow(
                        new PortfolioRoomException(errorCode)
                );

        //when

        PortfolioRoomException exception = assertThrows(PortfolioRoomException.class,
                () -> portfolioRoomService.findOnePortfolioRoom(portfolioRoomURL)
        );

        //then
        assertAll(
                () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(exception.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );

        verify(portfolioRoomRepository).findByPortfolioRoomURL(eq(portfolioRoomURL));
        verify(artistUserDrawingFieldRepository, never()).findAllUsedDrawingFieldByArtistUser(any(ArtistUser.class));
    }

    @Test
    @DisplayName("[성공] 작가유저 본인 포트폴리오룸 조회하기")
    public void givenArtistUser_whenFindMyPortfolioRoom_thenReturnPortfolioRoomDetailDto(){
        //given
        ArtistUser artistUser = getArtistUser(1L);
        when(portfolioRoomRepository.findByArtistUser(any(ArtistUser.class)))
                .thenReturn(
                        Optional.of(
                                createPortfolioRoom(PortfolioRoomStatus.PUBLIC_PORTFOLIO_ROOM)
                        )
                );
        when(artistUserDrawingFieldRepository.findAllUsedDrawingFieldByArtistUser(any(ArtistUser.class)))
                .thenReturn(
                        getUsedDrawingFieldDto()
                );
        //when
        PortfolioRoomDetailDto detailDto = portfolioRoomService.findMyPortfolioRoom(artistUser);

        //then
        assertThat(detailDto)
                .hasFieldOrProperty("portFolioRoomId")
                .hasFieldOrProperty("portfolioRoomURL")
                .hasFieldOrPropertyWithValue("nickname", artistUser.getNickname())
                .hasFieldOrPropertyWithValue("profileImage", artistUser.getProfileImage())
                .hasFieldOrPropertyWithValue("starPoint", 0.0)
                .hasFieldOrPropertyWithValue("statusMark", artistUser.getStatusMark())
                .hasFieldOrPropertyWithValue("portfolioRoomStatus", true)
                .hasFieldOrPropertyWithValue("statusMessage", "")
        ;

        verify(portfolioRoomRepository).findByArtistUser(any(ArtistUser.class));
        verify(artistUserDrawingFieldRepository).findAllUsedDrawingFieldByArtistUser(any(ArtistUser.class));
    }

    @Test
    @DisplayName("[성공] 작가유저 본인 비공개 포트폴리오룸 조회하기")
    public void givenArtistUser_whenAccessPrivatePortfolioRoomFindMyPortfolioRoom_thenReturnPortfolioRoomDetailDto(){
        //given
        ArtistUser artistUser = getArtistUser(1L);
        when(portfolioRoomRepository.findByArtistUser(any(ArtistUser.class)))
                .thenReturn(
                        Optional.of(
                                createPortfolioRoom(PortfolioRoomStatus.PRIVATE_PORTFOLIO_ROOM)
                        )
                );
        when(artistUserDrawingFieldRepository.findAllUsedDrawingFieldByArtistUser(any(ArtistUser.class)))
                .thenReturn(
                        getUsedDrawingFieldDto()
                );
        //when
        PortfolioRoomDetailDto detailDto = portfolioRoomService.findMyPortfolioRoom(artistUser);

        //then
        assertThat(detailDto)
                .hasFieldOrProperty("portFolioRoomId")
                .hasFieldOrProperty("portfolioRoomURL")
                .hasFieldOrPropertyWithValue("nickname", artistUser.getNickname())
                .hasFieldOrPropertyWithValue("profileImage", artistUser.getProfileImage())
                .hasFieldOrPropertyWithValue("starPoint", 0.0)
                .hasFieldOrPropertyWithValue("statusMark", artistUser.getStatusMark())
                .hasFieldOrPropertyWithValue("portfolioRoomStatus", false)
                .hasFieldOrPropertyWithValue("statusMessage", "")
        ;

        verify(portfolioRoomRepository).findByArtistUser(any(ArtistUser.class));
        verify(artistUserDrawingFieldRepository).findAllUsedDrawingFieldByArtistUser(any(ArtistUser.class));
    }

    @Test
    @DisplayName("[실패] 작가유저 운영자에 의해 접근 금지 포트폴리오룸 조회하기 - ")
    public void givenArtistUser_whenAccessCanNotUsePortfolioRoomFindMyPortfolioRoom_thenReturnPortfolioRoomDetailDto(){
        //given
        ArtistUser artistUser = getArtistUser(1L);
        PortfolioRoomErrorCode errorCode = PortfolioRoomErrorCode.NOT_ACCESS_PORTFOLIO_ROOM;
        when(portfolioRoomRepository.findByArtistUser(any(ArtistUser.class)))
                .thenReturn(
                        Optional.of(
                                createPortfolioRoom(PortfolioRoomStatus.CAN_NOT_USE_PORTFOLIO_ROOM)
                        )
                );

        //when
        PortfolioRoomException exception = assertThrows(PortfolioRoomException.class,
                () -> portfolioRoomService.findMyPortfolioRoom(artistUser)
        );

        //then
        assertAll(
                () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(exception.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );

        verify(portfolioRoomRepository).findByArtistUser(any(ArtistUser.class));
        verify(artistUserDrawingFieldRepository, never()).findAllUsedDrawingFieldByArtistUser(any(ArtistUser.class));
    }

    @Test
    @DisplayName("[성공] 포트폴리오룸 상태메시지 변경하기")
    public void givenStatusMessage_whenModifyStatusMessage_thenNothing(){
        //given
        String message = "새로운 상태 메시지";
        UpdateStatusMessageDto updateStatusMessageDto
                = UpdateStatusMessageDto.createUpdateStatusMessageDto(message);
        ArtistUser artistUser = getArtistUser(1L);
        String portfolioRoomURL = "joon";

        PortfolioRoom portfolioRoom = PortfolioRoom.createPortfolioRoom(artistUser, portfolioRoomURL);
        String beforeStatusMessage = portfolioRoom.getStatusMessage();
        when(portfolioRoomRepository.findByArtistUser(any(ArtistUser.class)))
                .thenReturn(
                        Optional.of(
                                portfolioRoom
                        )
                );

        //when
        portfolioRoomService.modifyStatusMessage(artistUser, updateStatusMessageDto);

        //then
        assertAll(
            () -> assertThat(beforeStatusMessage).isNotEqualTo(portfolioRoom.getStatusMessage()),
            () -> assertThat(portfolioRoom.getStatusMessage()).isEqualTo(message)
        );
        verify(portfolioRoomRepository).findByArtistUser(any(ArtistUser.class));
    }

    @Test
    @DisplayName("[성공] 포트폴리오룸 상태메시지 변경하기")
    public void givenPortfolioRoomStatus_whenModifyPortfolioRoomStatus_thenNothing(){
        //given

        boolean newStatus = false; //false 시 비공개 설정
        UpdatePortfolioStatusDto updatePortfolioStatusDto
                = UpdatePortfolioStatusDto.createUpdatePortfolioStatusDto(newStatus);
        ArtistUser artistUser = getArtistUser(1L);
        String portfolioRoomURL = "joon";

        PortfolioRoom portfolioRoom = PortfolioRoom.createPortfolioRoom(artistUser, portfolioRoomURL);
        PortfolioRoomStatus beforeStatus = portfolioRoom.getPortfolioRoomStatus();

        when(portfolioRoomRepository.findByArtistUser(any(ArtistUser.class)))
                .thenReturn(
                        Optional.of(
                                portfolioRoom
                        )
                );

        //when
        portfolioRoomService.modifyPortfolioStatus(artistUser, updatePortfolioStatusDto);

        //then
        assertAll(
                () -> assertThat(beforeStatus).isNotEqualTo(portfolioRoom.getPortfolioRoomStatus()),
                () -> assertThat(portfolioRoom.getPortfolioRoomStatus()).isEqualTo(PortfolioRoomStatus.PRIVATE_PORTFOLIO_ROOM)
        );
        verify(portfolioRoomRepository).findByArtistUser(any(ArtistUser.class));
    }


    @Test
    @DisplayName("[실패] 운영자에 의해 사용 제한되 포트폴리오 공개 설정 여부 금지 - NOT_ACCESS_PORTFOLIO_ROOM")
    public void givenPortfolioRoomStatus_whenModifyPortfolioRoomStatusButPortfolioRoomCanNotUse_thenNotAccessPortfolioRoomError(){
        //given
        boolean newStatus = false; //false 시 비공개 설정
        UpdatePortfolioStatusDto updatePortfolioStatusDto
                = UpdatePortfolioStatusDto.createUpdatePortfolioStatusDto(newStatus);
        ArtistUser artistUser = getArtistUser(1L);

        PortfolioRoom portfolioRoom = createPortfolioRoom(PortfolioRoomStatus.CAN_NOT_USE_PORTFOLIO_ROOM);

        PortfolioRoomErrorCode errorCode = PortfolioRoomErrorCode.NOT_ACCESS_PORTFOLIO_ROOM;

        when(portfolioRoomRepository.findByArtistUser(any(ArtistUser.class)))
                .thenReturn(
                        Optional.of(
                                portfolioRoom
                        )
                );
        //when

        PortfolioRoomException exception = assertThrows(PortfolioRoomException.class,
                () -> portfolioRoomService.modifyPortfolioStatus(artistUser, updatePortfolioStatusDto)
        );

        //then
        assertAll(
                () -> assertThat(exception.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(exception.getErrorCode().getDescription()).isEqualTo(errorCode.getDescription())
        );

        verify(portfolioRoomRepository).findByArtistUser(any(ArtistUser.class));
    }

    private PortfolioRoom createPortfolioRoom(PortfolioRoomStatus portfolioRoomStatus) {
        ArtistUser artistUser = getArtistUser(1L);
        String portfolioRoomURL = "joon";
        return PortfolioRoom.createPortfolioRoom(artistUser, portfolioRoomURL, "", portfolioRoomStatus);
    }

}

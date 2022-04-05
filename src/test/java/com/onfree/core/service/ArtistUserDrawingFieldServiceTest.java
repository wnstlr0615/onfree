package com.onfree.core.service;

import com.onfree.common.error.code.DrawingFieldErrorCode;
import com.onfree.common.error.exception.DrawingFieldException;
import com.onfree.core.dto.drawingfield.artist.UpdateDrawingFieldsDto;
import com.onfree.core.dto.drawingfield.artist.UsedDrawingFieldDto;
import com.onfree.core.dto.user.artist.MobileCarrier;
import com.onfree.core.entity.drawingfield.DrawingField;
import com.onfree.core.entity.drawingfield.DrawingFieldStatus;
import com.onfree.core.entity.user.*;
import com.onfree.core.repository.ArtistUserDrawingFieldRepository;
import com.onfree.core.repository.ArtistUserRepository;
import com.onfree.core.repository.DrawingFieldRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ArtistUserDrawingFieldServiceTest {
    @Mock
    ArtistUserDrawingFieldRepository artistUserDrawingFieldRepository;
    @Mock
    ArtistUserRepository artistUserRepository;
    @Mock
    DrawingFieldRepository drawingFieldRepository;

    @InjectMocks
    ArtistUserDrawingFieldService artistUserDrawingFieldService;

    @Test
    @DisplayName("[성공] 그림분야 수정")
    public void givenUpdateDrawingFields_whenUpdateDrawingFields_thenNothing(){
        //given
        final long givenUserId = 1L;
        final UpdateDrawingFieldsDto updateDrawingFieldsDto = givenUpdateDrawingFieldsDto(List.of(1L, 2L, 3L));

        when(artistUserRepository.findById(anyLong()))
                .thenReturn(
                        Optional.ofNullable(getArtistUser())
                );
        when(drawingFieldRepository.findAllByStatusNotDisabledAndTempDrawingFieldIdIn(updateDrawingFieldsDto.getDrawingFields()))
                .thenReturn(
                       getDrawingFieldList()
                );
        doNothing()
                .when(artistUserDrawingFieldRepository)
                .deleteAllByArtistUser(any(ArtistUser.class));

        when(artistUserDrawingFieldRepository.saveAll(any()))
                .thenReturn(List.of());

        //when
        artistUserDrawingFieldService.updateDrawingFields(givenUserId, updateDrawingFieldsDto);

        //then

        verify(artistUserRepository).findById(eq(givenUserId));
        verify(drawingFieldRepository).findAllByStatusNotDisabledAndTempDrawingFieldIdIn(any());
        verify(artistUserDrawingFieldRepository).deleteAllByArtistUser(any(ArtistUser.class));
        verify(artistUserDrawingFieldRepository).saveAll(any());
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


    private UpdateDrawingFieldsDto givenUpdateDrawingFieldsDto(List<Long> drawingFields) {
        return UpdateDrawingFieldsDto.builder()
                .drawingFields(
                        drawingFields
                )
                .build();
    }

    private List<DrawingField> getDrawingFieldList() {
        return List.of(
                getDrawingField(1L, "캐릭터 디자인"),
                getDrawingField(2L, "일러스트"),
                getDrawingField(3L, "버츄얼 디자인")
        );
    }

    private DrawingField getDrawingField(long drawingFieldId, String fieldName) {
        return DrawingField.builder()
                .drawingFieldId(drawingFieldId)
                .fieldName(fieldName)
                .description(fieldName)
                .status(DrawingFieldStatus.USED)
                .build();
    }

    @Test
    @DisplayName("[실패] 그림분야 수정 - 등록 되지 않은 그림분야가 포함된 경우")
    public void givenWrongUpdateDrawingFields_whenUpdateDrawingFields_thenWrongDrawingFieldError(){
        //given
        final long givenUserId = 1L;
        final List<Long> notRegisterDrawingFieldId = List.of(1L, 99L, 100L);
        final UpdateDrawingFieldsDto wrongUpdateDrawingFieldsDto = givenUpdateDrawingFieldsDto(notRegisterDrawingFieldId);
        final DrawingFieldErrorCode wrongDrawingField = DrawingFieldErrorCode.WRONG_DRAWING_FIELD;
        when(artistUserRepository.findById(anyLong()))
                .thenReturn(
                        Optional.ofNullable(getArtistUser())
                );
        when(drawingFieldRepository.findAllByStatusNotDisabledAndTempDrawingFieldIdIn(wrongUpdateDrawingFieldsDto.getDrawingFields()))
                .thenReturn(
                        getMissedDrawingFieldList()
                );
        //when
        final DrawingFieldException drawingFieldException = assertThrows(DrawingFieldException.class,
                () -> artistUserDrawingFieldService.updateDrawingFields(givenUserId, wrongUpdateDrawingFieldsDto));


        //then
        assertThat(drawingFieldException.getErrorCode()).isEqualTo(wrongDrawingField);
        assertThat(drawingFieldException.getErrorCode().getDescription()).isEqualTo(wrongDrawingField.getDescription());

        verify(artistUserRepository).findById(eq(givenUserId));
        verify(drawingFieldRepository).findAllByStatusNotDisabledAndTempDrawingFieldIdIn(any());
        verify(artistUserDrawingFieldRepository, never()).deleteAllByArtistUser(any(ArtistUser.class));
        verify(artistUserDrawingFieldRepository, never()).saveAll(any());
    }

    private List<DrawingField> getMissedDrawingFieldList() {
        return List.of(
                getDrawingField(1L, "캐릭터 디자인")
        );
    }

    @Test
    @DisplayName("[성공] 작가유저 그림분야 가져오기 - 작가 유저에 그림 분야가 있는 경우")
    public void givenUserId_whenGetAllArtistUserUsedDrawingFields_thenUsedDrawingFieldDtoList(){
        //given
        final long givenUserId = 1L;
        when(artistUserRepository.findById(anyLong()))
                .thenReturn(
                        Optional.ofNullable(
                                getArtistUser()
                        )
                );
        when(drawingFieldRepository.findAllByStatusNotDisabledAndTempOrderByTopDesc())
                .thenReturn(
                        getDrawingFieldList()
                );
        when(artistUserDrawingFieldRepository.findAllArtistUserDrawingFieldIdByArtistUser(any(ArtistUser.class)))
                .thenReturn(
                        getArtistUserDrawingFieldIdList()
                );

        //when
        final List<UsedDrawingFieldDto> serviceResult
                = artistUserDrawingFieldService.getAllArtistUserUsedDrawingFields(givenUserId);

        //then
        assertAll(
                () -> assertThat(serviceResult.get(0))
                        .hasFieldOrPropertyWithValue("drawingFieldId", 1L)
                        .hasFieldOrPropertyWithValue("drawingFieldName", "캐릭터 디자인")
                        .hasFieldOrPropertyWithValue("used", true),
                () -> assertThat(serviceResult.get(1))
                        .hasFieldOrPropertyWithValue("drawingFieldId", 2L)
                        .hasFieldOrPropertyWithValue("drawingFieldName", "일러스트")
                        .hasFieldOrPropertyWithValue("used", true),
                () -> assertThat(serviceResult.get(2))
                        .hasFieldOrPropertyWithValue("drawingFieldId", 3L)
                        .hasFieldOrPropertyWithValue("drawingFieldName", "버츄얼 디자인")
                        .hasFieldOrPropertyWithValue("used", false)
        );

        verify(artistUserDrawingFieldRepository).findAllArtistUserDrawingFieldIdByArtistUser(any(ArtistUser.class));
        verify(drawingFieldRepository).findAllByStatusNotDisabledAndTempOrderByTopDesc();
    }

    private List<Long> getArtistUserDrawingFieldIdList() {
        return List.of(1L, 2L);
    }

    @Test
    @DisplayName("[성공] 작가유저 그림분야 가져오기 - 작가유저 그림분야가 비어 있는 경우")
    public void givenUserId_whenGetAllArtistUserUsedDrawingFieldsButEmptyHasDrawingField_thenUsedDrawingFieldDtoList(){
        //given
        final long givenUserId = 1L;
        when(artistUserRepository.findById(anyLong()))
                .thenReturn(
                        Optional.ofNullable(
                                getArtistUser()
                        )
                );
        when(drawingFieldRepository.findAllByStatusNotDisabledAndTempOrderByTopDesc())
                .thenReturn(
                        getDrawingFieldList()
                );
        when(artistUserDrawingFieldRepository.findAllArtistUserDrawingFieldIdByArtistUser(any(ArtistUser.class)))
                .thenReturn(
                        List.of()
                );

        //when
        final List<UsedDrawingFieldDto> serviceResult
                = artistUserDrawingFieldService.getAllArtistUserUsedDrawingFields(givenUserId);

        //then
        assertAll(
                () -> assertThat(serviceResult.get(0))
                        .hasFieldOrPropertyWithValue("drawingFieldId", 1L)
                        .hasFieldOrPropertyWithValue("drawingFieldName", "캐릭터 디자인")
                        .hasFieldOrPropertyWithValue("used", false),
                () -> assertThat(serviceResult.get(1))
                        .hasFieldOrPropertyWithValue("drawingFieldId", 2L)
                        .hasFieldOrPropertyWithValue("drawingFieldName", "일러스트")
                        .hasFieldOrPropertyWithValue("used", false),
                () -> assertThat(serviceResult.get(2))
                        .hasFieldOrPropertyWithValue("drawingFieldId", 3L)
                        .hasFieldOrPropertyWithValue("drawingFieldName", "버츄얼 디자인")
                        .hasFieldOrPropertyWithValue("used", false)
        );

        verify(artistUserDrawingFieldRepository).findAllArtistUserDrawingFieldIdByArtistUser(any(ArtistUser.class));
        verify(drawingFieldRepository).findAllByStatusNotDisabledAndTempOrderByTopDesc();
    }
}



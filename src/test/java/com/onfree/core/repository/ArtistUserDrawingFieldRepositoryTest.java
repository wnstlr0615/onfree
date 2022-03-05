package com.onfree.core.repository;

import com.onfree.core.dto.drawingfield.artist.UsedDrawingFieldDto;
import com.onfree.core.dto.user.artist.MobileCarrier;
import com.onfree.core.entity.ArtistUserDrawingField;
import com.onfree.core.entity.drawingfield.DrawingField;
import com.onfree.core.entity.drawingfield.DrawingFieldStatus;
import com.onfree.core.entity.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@ActiveProfiles("test")
@DataJpaTest
@Transactional
class ArtistUserDrawingFieldRepositoryTest {
    @Autowired
    ArtistUserDrawingFieldRepository artistUserDrawingFieldRepository;
    @Autowired
    DrawingFieldRepository drawingFieldRepository;
    @Autowired
    ArtistUserRepository artistUserRepository;

    @BeforeEach
    private void init(){
        saveDrawingField("캐릭터 디자인", "캐릭터 디자인", DrawingFieldStatus.USED);
        saveDrawingField("버츄얼 디자인", "버츄얼 디자인", DrawingFieldStatus.USED);
        saveDrawingField("일러스트", "일러스트", DrawingFieldStatus.USED);
        saveDrawingField("게임삽화/원화", "게임삽화/원화", DrawingFieldStatus.USED);
        saveDrawingField("만화, 소설표지", "만화, 소설표지", DrawingFieldStatus.USED);
        saveDrawingField("애니메이팅/밈", "애니메이팅/밈", DrawingFieldStatus.USED);
        saveDrawingField("파츠 제작", "파츠 제작", DrawingFieldStatus.USED);
        saveDrawingField("19+", "19+", DrawingFieldStatus.USED);
    }

    private void saveDrawingField(String fieldName, String description, DrawingFieldStatus status) {
        saveDrawingField(
                createDrawingField(fieldName, description, status)
        );
    }

    private DrawingField createDrawingField(String fieldName, String description, DrawingFieldStatus status) {
        return DrawingField.createDrawingField(fieldName,description, status);
    }

    private DrawingField saveDrawingField(DrawingField drawingField) {
        return drawingFieldRepository.save(drawingField);
    }

    @Test
    @DisplayName("사용자 그림분야 가져오기 테스트")
    public void givenNothing_whenFindAllUsedDrawingFieldByArtistUser_thenUsedDrawingFieldList(){
        //given
        ArtistUser artistUser = artistUserRepository.save(
                getArtistUser("joon1@naver.com", "http://www.onfree.co.kr/folioUrl/dasdasfasd1")
        );

        ArtistUser otherArtistUser = artistUserRepository.save(
                getArtistUser("otheruser@naver.com", "http://www.onfree.co.kr/folioUrl/dasdasfasd2")
        );


        //사용자 그림 분야 저장
        List<DrawingField> drawingFields = drawingFieldRepository.findAllById(List.of(1L, 2L, 3L, 4L));
        for (DrawingField drawingField : drawingFields) {
            saveArtistUserDrawingField(artistUser, drawingField);
        }

        List<DrawingField> otherDrawingFields = drawingFieldRepository.findAllById(List.of(2L, 4L, 6L, 8L));
        for (DrawingField drawingField : otherDrawingFields) {
            saveArtistUserDrawingField(otherArtistUser, drawingField);
        }

        //when
        List<UsedDrawingFieldDto> usedDrawingFieldDtos = artistUserDrawingFieldRepository.findAllUsedDrawingFieldByArtistUser(artistUser);
        usedDrawingFieldDtos.forEach(System.out::println);
        System.out.println();
        List<UsedDrawingFieldDto> otherUsedDrawingFieldDtos = artistUserDrawingFieldRepository.findAllUsedDrawingFieldByArtistUser(otherArtistUser);
        otherUsedDrawingFieldDtos.forEach(System.out::println);
        //then

    }

    private void saveArtistUserDrawingField(ArtistUser artistUser, DrawingField drawingField) {
        artistUserDrawingFieldRepository.save(
                ArtistUserDrawingField.createArtistUserDrawingField(artistUser, drawingField)
        );
    }

    private ArtistUser getArtistUser(String email, String portfolioUrl) {
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
                .email(email)
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
                .portfolioUrl(portfolioUrl)
                .build();
    }

}
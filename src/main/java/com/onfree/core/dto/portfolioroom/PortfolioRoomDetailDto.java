package com.onfree.core.dto.portfolioroom;

import com.onfree.core.dto.drawingfield.artist.UsedDrawingFieldDto;
import com.onfree.core.entity.portfolioroom.PortfolioRoom;
import com.onfree.core.entity.user.StatusMark;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PortfolioRoomDetailDto extends RepresentationModel<PortfolioRoomDetailDto> {
    private Long portFolioRoomId;

    private String portfolioRoomURL;

    //== 사용자 정보 ==//
    private String nickname; //회원이름
    private String profileImage; //프로필이미지
    private double starPoint;  // 별점

    // 설정한 분야
    @Setter
    private List<UsedDrawingFieldDto> drawingFields;

    // 영업마크 설정
    private StatusMark statusMark;

    // 포트폴리오룸 공개 여부
    private Boolean portfolioRoomStatus;

    //상태 메시지
    private String statusMessage;


    //== 생성 메서드 ==//
    public static PortfolioRoomDetailDto fromEntity(PortfolioRoom portfolioRoom) {
        //TODO startPoint 구현하기
        return PortfolioRoomDetailDto.builder()
                .portFolioRoomId(portfolioRoom.getPortFolioRoomId())
                .portfolioRoomURL(portfolioRoom.getPortfolioRoomURL())
                .nickname(portfolioRoom.getArtistUser().getNickname())
                .profileImage(portfolioRoom.getArtistUser().getProfileImage())
                .starPoint(0)
                .statusMark(portfolioRoom.getArtistUser().getStatusMark())
                .portfolioRoomStatus(portfolioRoom.getPortfolioRoomStatus().isPublic())
                .statusMessage(portfolioRoom.getStatusMessage())
                .build();
    }

    public static PortfolioRoomDetailDto createPortfolioRoomDetailDto(
            Long portFolioRoomId, String portfolioRoomURL, String nickname, String profileImage, double starPoint,
            List<UsedDrawingFieldDto> drawingFields, StatusMark statusMark, Boolean portfolioRoomStatus, String statusMessage) {
        return PortfolioRoomDetailDto.builder()
                .portFolioRoomId(portFolioRoomId)
                .portfolioRoomURL(portfolioRoomURL)
                .nickname(nickname)
                .profileImage(profileImage)
                .starPoint(0)
                .statusMark(statusMark)
                .drawingFields(drawingFields)
                .portfolioRoomStatus(portfolioRoomStatus)
                .statusMessage(statusMessage)
                .build();
    }
}

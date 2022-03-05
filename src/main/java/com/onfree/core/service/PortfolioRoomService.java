package com.onfree.core.service;

import com.onfree.common.error.code.PortfolioRoomErrorCode;
import com.onfree.common.error.exception.PortfolioRoomException;
import com.onfree.core.dto.drawingfield.artist.UsedDrawingFieldDto;
import com.onfree.core.dto.portfolioroom.PortfolioRoomDetailDto;
import com.onfree.core.dto.portfolioroom.UpdatePortfolioStatusDto;
import com.onfree.core.dto.portfolioroom.UpdateStatusMessageDto;
import com.onfree.core.entity.portfolioroom.PortfolioRoom;
import com.onfree.core.entity.portfolioroom.PortfolioRoomStatus;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.repository.ArtistUserDrawingFieldRepository;
import com.onfree.core.repository.PortfolioRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioRoomService {
    private final PortfolioRoomRepository portfolioRoomRepository;
    private final ArtistUserDrawingFieldRepository artistUserDrawingFieldRepository;

    public PortfolioRoomDetailDto findOnePortfolioRoom(String portfolioRoomUrl) {
        PortfolioRoom portfolioRoom = getPortfolioRoomByURL(portfolioRoomUrl); //포트폴리오룸 조회
        validateAccessPortfolioRoom(portfolioRoom); // 포트폴리오 접근 가능 한지 검사
        List<UsedDrawingFieldDto> usedDrawingFieldDtos = getUsedDrawingFieldByArtistUser(portfolioRoom); //작가 그림분야 조회

        return getPortfolioRoomDetail(portfolioRoom, usedDrawingFieldDtos);
    }

    private PortfolioRoom getPortfolioRoomByURL(String portfolioRoomUrl) {
        return portfolioRoomRepository.findByPortfolioRoomURL(portfolioRoomUrl)
                .orElseThrow(() -> new PortfolioRoomException(PortfolioRoomErrorCode.NOT_FOUND_PORTFOLIO_ROOM));
    }

    private void validateAccessPortfolioRoom(PortfolioRoom portfolioRoom) {
        switch (portfolioRoom.getPortfolioRoomStatus()){
            case PUBLIC_PORTFOLIO_ROOM:
                break;
            case PRIVATE_PORTFOLIO_ROOM:
                throw new PortfolioRoomException(PortfolioRoomErrorCode.PORTFOLIO_ROOM_IS_PRIVATE);
            case CAN_NOT_USE_PORTFOLIO_ROOM:
                throw new PortfolioRoomException(PortfolioRoomErrorCode.NOT_ACCESS_PORTFOLIO_ROOM);
        }
    }

    private List<UsedDrawingFieldDto> getUsedDrawingFieldByArtistUser(PortfolioRoom portfolioRoom) {
        return artistUserDrawingFieldRepository.findAllUsedDrawingFieldByArtistUser(portfolioRoom.getArtistUser());
    }

    private PortfolioRoomDetailDto getPortfolioRoomDetail(PortfolioRoom portfolioRoom, List<UsedDrawingFieldDto> usedDrawingFieldDtos) {
        PortfolioRoomDetailDto portfolioRoomDetailDto = PortfolioRoomDetailDto.fromEntity(portfolioRoom);
        portfolioRoomDetailDto.setDrawingFields(usedDrawingFieldDtos);
        return portfolioRoomDetailDto;
    }

    /** 작가유저 본인 포트폴리오룸 조회 */
    public PortfolioRoomDetailDto findMyPortfolioRoom(ArtistUser artistUser) {
        PortfolioRoom portfolioRoom = getPortfolioRoomByArtistUser(artistUser); //포트폴리오룸 조회
        validateAccessMyPortfolioRoom(portfolioRoom); // 포트폴리오 접근 가능 한지 검사
        List<UsedDrawingFieldDto> usedDrawingFieldDtos = getUsedDrawingFieldByArtistUser(portfolioRoom); //작가 그림분야 조회
        return getPortfolioRoomDetail(portfolioRoom, usedDrawingFieldDtos);
    }

    private void validateAccessMyPortfolioRoom(PortfolioRoom portfolioRoom) {
        switch (portfolioRoom.getPortfolioRoomStatus()){
            case PUBLIC_PORTFOLIO_ROOM:
            case PRIVATE_PORTFOLIO_ROOM:
                break;
            case CAN_NOT_USE_PORTFOLIO_ROOM:
                throw new PortfolioRoomException(PortfolioRoomErrorCode.NOT_ACCESS_PORTFOLIO_ROOM);
        }
    }

    /** 포트폴리오룸 상태메시지 변경하기 */
    @Transactional
    public void modifyStatusMessage(ArtistUser artistUser, UpdateStatusMessageDto dto) {
        PortfolioRoom portfolioRoom = getPortfolioRoomByArtistUser(artistUser); //사용자 포트폴리오룸 조회
        updateStatusMessage(portfolioRoom, dto.getStatusMessage()); // 상태 메시지 변경
    }

    private void updateStatusMessage(PortfolioRoom portfolioRoom, String statusMessage) {
        portfolioRoom.updateStatusMessage(statusMessage);
    }

    /** 포트폴리오룸 공배/비공개 설정 하기*/
    @Transactional
    public void modifyPortfolioStatus(ArtistUser artistUser, UpdatePortfolioStatusDto dto) {
        PortfolioRoom portfolioRoom = getPortfolioRoomByArtistUser(artistUser); //사용자 포트폴리오룸 조회
        updatePortfolioStatus(portfolioRoom, dto);
    }

    private PortfolioRoom getPortfolioRoomByArtistUser(ArtistUser artistUser) {
        return portfolioRoomRepository.findByArtistUser(artistUser)
                .orElseThrow(() -> new PortfolioRoomException(PortfolioRoomErrorCode.NOT_ACCESS_PORTFOLIO_ROOM));
    }

    private void updatePortfolioStatus(PortfolioRoom portfolioRoom, UpdatePortfolioStatusDto dto) {
        validateUpdatePortfolioStatus(portfolioRoom); //포트폴리오 상태를 업데이트 가능한 지 여부 검사
        PortfolioRoomStatus status
                = dto.getPortfolioStatus() ? PortfolioRoomStatus.PUBLIC_PORTFOLIO_ROOM : PortfolioRoomStatus.PRIVATE_PORTFOLIO_ROOM;
        portfolioRoom.updatePortfolioRoomStatus(status);
    }

    private void validateUpdatePortfolioStatus(PortfolioRoom portfolioRoom) {
        switch (portfolioRoom.getPortfolioRoomStatus()){
            case PUBLIC_PORTFOLIO_ROOM:
            case PRIVATE_PORTFOLIO_ROOM:
                break;
            case CAN_NOT_USE_PORTFOLIO_ROOM:
                throw new PortfolioRoomException(PortfolioRoomErrorCode.NOT_ACCESS_PORTFOLIO_ROOM);
        }
    }

}

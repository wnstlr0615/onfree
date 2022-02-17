package com.onfree.core.service;

import com.onfree.common.error.code.PortfolioErrorCode;
import com.onfree.common.error.code.UserErrorCode;
import com.onfree.common.error.exception.PortfolioException;
import com.onfree.common.error.exception.UserException;
import com.onfree.core.dto.drawingfield.artist.UsedDrawingFieldDto;
import com.onfree.core.dto.portfolio.CreatePortfolioDto;
import com.onfree.core.dto.portfolio.PortfolioDetailDto;
import com.onfree.core.dto.portfolio.PortfolioSimpleDto;
import com.onfree.core.dto.portfolio.UpdatePortfolioDto;
import com.onfree.core.entity.DrawingField;
import com.onfree.core.entity.PortfolioDrawingField;
import com.onfree.core.entity.portfolio.Portfolio;
import com.onfree.core.entity.portfolio.PortfolioStatus;
import com.onfree.core.entity.portfoliocontent.PortfolioContent;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.onfree.core.dto.drawingfield.artist.UsedDrawingFieldDto.createUsedDrawingFieldDtoFromEntity;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PortfolioService {
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final DrawingFieldRepository drawingFieldRepository;
    private final PortfolioContentRepository portfolioContentRepository;
    private final PortfolioDrawingFieldRepository portfolioDrawingFieldRepository;

    /** 포트폴리오 추가*/
    @Transactional
    public CreatePortfolioDto.Response addPortfolio(ArtistUser artistUser, CreatePortfolioDto.Request dto) {
        return getCreatePortfolioDtoResponseFromEntity( // CreatePortfolioDtoResponse 로 변환
                    savePortfolio( // 포트폴리오 저장
                            createPortfolio(artistUser, dto) // 포트폴리오 생성
                )
        );
    }
    private CreatePortfolioDto.Response getCreatePortfolioDtoResponseFromEntity(Portfolio portfolio) {
        return CreatePortfolioDto.Response.fromEntity(
                portfolio
        );
    }

    private Portfolio savePortfolio(Portfolio portfolio) {
        return portfolioRepository.save(
                portfolio
        );
    }



    private Portfolio createPortfolio(ArtistUser artistUser, CreatePortfolioDto.Request dto) {

        // 포트폴리오 그림분야 설정
        final List<PortfolioDrawingField> portfolioDrawingFields = getPortfolioDrawingFields(
                getDrawingFieldsById(
                        dto.getDrawingFieldIds()
                )
        );


        //포트폴리오 내용 설정
        final List<PortfolioContent> portfolioContents = dto.toPortfolioContentList();

        //태그 설정
        final String tags = getTagsToString(dto.getTags());

        //포트폴리오 상태 설정
        final PortfolioStatus portfolioStatus
                = getPortfolioStatusOnIsTemporary(dto.isTemporary());

        //포트 폴리오 생성
        return Portfolio.createPortfolio(
                artistUser, dto.getMainImageUrl(), dto.getTitle(), portfolioContents, tags,
                portfolioDrawingFields, portfolioStatus
        );
    }

    private String getTagsToString(List<String> tags) {
        if(tags != null && !tags.isEmpty()) {
            return String.join(",", tags);
        }
        return "";
    }

    private List<DrawingField> getDrawingFieldsById(List<Long> drawingFieldIds) {
        if(drawingFieldIds != null && !drawingFieldIds.isEmpty()) {
            return drawingFieldRepository.findAllByDisabledIsFalseAndDrawingFieldIdIn(drawingFieldIds);
        }
        return Collections.emptyList();
    }

    private List<PortfolioDrawingField> getPortfolioDrawingFields(List<DrawingField> drawingFields) {
        return drawingFields.stream()
                .map(PortfolioDrawingField::createPortfolioDrawingField)
                .collect(Collectors.toList());
    }

    private PortfolioStatus getPortfolioStatusOnIsTemporary(boolean temporary) {
        return temporary ? PortfolioStatus.TEMPORARY : PortfolioStatus.NORMAL;
    }

    /** 포트폴리오 상세 조회 */
    @Transactional
    public PortfolioDetailDto findPortfolio(Long portfolioId) {
        return getPortfolioDetailDto( // 포트폴리오 PortfolioDetailDto 로 변환
                portfolioIncreaseView( // 조회수 증가
                        validateReadablePortfolio( // 포트폴리오를 읽을 수 있는 지 검증
                            getPortfolioByPortfolioId(portfolioId) // 포트폴리오 조회
                        )
                )
        );
    }

    private Portfolio validateReadablePortfolio(Portfolio portfolio) {
        switch (portfolio.getStatus()){
            case DELETED:
            case TEMPORARY:
            case HIDDEN:
                throw new PortfolioException(PortfolioErrorCode.NOT_FOUND_PORTFOLIO);
        }
        return portfolio;
    }

    private Portfolio getPortfolioByPortfolioId(Long portfolioId) {
        return portfolioRepository.findByPortfolioId(portfolioId)
                .orElseThrow(
                        () -> new PortfolioException(PortfolioErrorCode.NOT_FOUND_PORTFOLIO)
                );
    }

    private Portfolio portfolioIncreaseView(Portfolio portfolio) {
        portfolio.increaseView();
        return portfolio;
    }

    private PortfolioDetailDto getPortfolioDetailDto(Portfolio portfolio) {
        final PortfolioDetailDto portfolioDetailDto = PortfolioDetailDto.fromEntity(portfolio);

        //그림 분야 설정
        portfolioDetailDto.setDrawingFields(
                getUsedDrawingFieldDtos( //그림분야를 사용유무에 따른 그림 분야로 변환
                        getAllDrawingFieldList(),
                        portfolio.getDrawingFieldIds()
                )
        );

        return portfolioDetailDto;
    }

    private List<DrawingField> getAllDrawingFieldList() {
        return drawingFieldRepository.findAllByDisabledIsFalseOrderByTopDesc();
    }

    private List<UsedDrawingFieldDto> getUsedDrawingFieldDtos(List<DrawingField> allDrawingFields, List<Long> drawingFieldIdsOfPortfolio) {
        if(drawingFieldIdsOfPortfolio.isEmpty()){ // 사용자 그림 분야가 없는 경우 ex) 임시 저장 글
            return allDrawingFields.stream()
                    .map(drawingField -> createUsedDrawingFieldDtoFromEntity(drawingField, false))
                    .collect(Collectors.toList());
        }

        return allDrawingFields.stream() // 사용자 그림 분야가 있는 경우
                .map(drawingField -> {
                    if(hasDrawingFieldId(drawingFieldIdsOfPortfolio, drawingField)){
                        return createUsedDrawingFieldDtoFromEntity(drawingField, true);
                    }
                    return createUsedDrawingFieldDtoFromEntity(drawingField, false);
                }).collect(Collectors.toList());
    }

    private boolean hasDrawingFieldId(List<Long> drawingFieldIdsOfPortfolio, DrawingField drawingField) {
        return drawingFieldIdsOfPortfolio.contains(
                drawingField.getDrawingFieldId()
        );
    }

    /** 임시 저장 포트폴리오 상세 조회*/
    @Transactional
    public PortfolioDetailDto findTempPortfolio(Long portfolioId, ArtistUser artistUser) {
        return getPortfolioDetailDto( // 포트폴리오 PortfolioDetailDto 로 변환
                validateIsTempPortfolio(
                    getTempPortfolioByPortfolioIdAndArtistUser(portfolioId, artistUser) // 사용자 임시 포트폴리오 조회
                )
        );
    }



    private Portfolio getTempPortfolioByPortfolioIdAndArtistUser(Long portfolioId, ArtistUser artistUser) {
        return portfolioRepository
                .findByPortfolioIdAndArtistUser(portfolioId, artistUser)
                .orElseThrow(
                        () -> new PortfolioException(PortfolioErrorCode.NOT_FOUND_PORTFOLIO)
                );
    }
    private Portfolio validateIsTempPortfolio(Portfolio portfolio) {
        switch (portfolio.getStatus()){
            case HIDDEN:
            case DELETED:
                throw new PortfolioException(PortfolioErrorCode.NOT_FOUND_PORTFOLIO);
            case NORMAL:
            case REPRESENTATION:
                throw new PortfolioException(PortfolioErrorCode.IS_NOT_TEMP_PORTFOLIO);
        }
        return portfolio;
    }

    /** 작가 포트폴리오 전체 조회 */
    public Page<PortfolioSimpleDto> findAllPortfolioByUserId(Long userId, PageRequest pageable) {
        return getPortfolioSimpleDtosByArtistUser(//Portfolio 리스트를  PortfolioSimpleDto 리스트로 변환
                    getPagingPortfoliosByArtistUser( // 작가유저 포트폴리오 조회
                        getArtistUser(userId), // 작가 유저 조회,
                            pageable
                    )
            );
    }

    private ArtistUser getArtistUser(Long userId) {
        return (ArtistUser) userRepository.findById(userId)
                .orElseThrow(
                        () ->  new UserException(UserErrorCode.NOT_FOUND_USERID)
                );
    }

    private Page<Portfolio> getPagingPortfoliosByArtistUser(ArtistUser artistUser, Pageable pageable) {
        final List<PortfolioStatus> readableStatus = List.of(PortfolioStatus.NORMAL, PortfolioStatus.REPRESENTATION);
        return portfolioRepository.findByArtistUserAndStatusIn(artistUser, readableStatus, pageable);
    }




    private Page<PortfolioSimpleDto> getPortfolioSimpleDtosByArtistUser(Page<Portfolio> portfolios) {
        return  portfolios.map(PortfolioSimpleDto::fromEntity);
    }

    /** 작가 유저 임시 포트폴리오 전체 조회 */
    public Page<PortfolioSimpleDto> findTempPortfolioByArtistUser(ArtistUser artistUser, Pageable pageable) {
        return getPortfolioSimpleDtosByArtistUser( //Portfolio 리스트를  PortfolioSimpleDto 리스트로 변환
                    getPagingTempPortfoliosByArtistUser( // 작가유저 임시 저장 포트폴리오 조회
                        artistUser, pageable
                    )
                );
    }

    private Page<Portfolio> getPagingTempPortfoliosByArtistUser(ArtistUser artistUser, Pageable pageable) {
        return portfolioRepository.findPageByArtistUserAndStatus(artistUser, PortfolioStatus.TEMPORARY, pageable);
    }


    /** 포트폴리오 삭제 */
    @Transactional
    public void removePortfolio(Long portfolioId, ArtistUser artistUser) {
        deletePortfolio( //포트폴리오 제거
            validatePossibleDelete( // 포트폴리오가 이미 제거되었는지 확인
                    getPortfolioByArtistUser(portfolioId, artistUser) // 포트폴리오 가져오기
            )
        );
    }

    private Portfolio getPortfolioByArtistUser(Long portfolioId, ArtistUser artistUser) {
        return portfolioRepository.findByPortfolioIdAndArtistUser(portfolioId, artistUser)
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.NOT_FOUND_PORTFOLIO));
    }

    private Portfolio validatePossibleDelete(Portfolio portfolio) {
        switch (portfolio.getStatus()) {
            case DELETED:
                throw new PortfolioException(PortfolioErrorCode.ALREADY_DELETED_PORTFOLIO);
            case HIDDEN:
                throw new PortfolioException(PortfolioErrorCode.CAN_NOT_REMOVE_PORTFOLIO);
        }
        return portfolio;
    }

    private void deletePortfolio(Portfolio portfolio) {
        portfolio.remove();
    }

    /** 포트폴리오 대표 설정*/
    @Transactional
    public void representPortfolio(Long portfolioId, ArtistUser artistUser) {
        representPortfolios( // 포트폴리오 대표 지정
            validatePortfolioPossiblesRepresentative( // 포트폴리오가 대표로 지정 가능한지 확인
                getPortfolioByArtistUser(portfolioId, artistUser) //작가 포트폴리오 조회
            )
        );
    }

    private Portfolio validatePortfolioPossiblesRepresentative(Portfolio portfolio) {
        switch (portfolio.getStatus()){
            case TEMPORARY:
                throw new PortfolioException(PortfolioErrorCode.NOT_ALLOW_REPRESENTATIVE_TEMP_PORTFOLIO);
            case DELETED:
            case HIDDEN:
                throw new PortfolioException(PortfolioErrorCode.NOT_FOUND_PORTFOLIO);
        }
        return portfolio;
    }

    private void representPortfolios(Portfolio portfolio) {
        representCancelOtherPortfolios( // 지정 되었던 다른 대표 포트폴리오가 있을 경우 취소
                getRepresentPortfoliosByArtistUser( // 사용자 모든 포트폴리오 조회
                        portfolio.getArtistUser()
                )
        );

        //포트폴리오 대표 지정
        portfolio.represent();
    }

    private List<Portfolio> getRepresentPortfoliosByArtistUser(ArtistUser artistUser) {
        return portfolioRepository.findAllByArtistUserAndStatus(artistUser, PortfolioStatus.REPRESENTATION);
    }

    private void representCancelOtherPortfolios(List<Portfolio> portfolios) {
        portfolios.forEach(Portfolio::representCancel);
    }

    /** 포트폴리오 수정하기 */
    @Transactional
    public void updatePortfolio(Long portfolioId, ArtistUser artistUser, UpdatePortfolioDto dto) {
        final Portfolio portfolio = deletePortfolioDrawingField( // 포트폴리오 그림 분야 삭제
                                        deletePortfolioContents( //기존 포트폴리오 내용 삭제
                                                validateUpdatablePortfolio( // 수정가능한 포트폴리오인지 확인
                                                    getPortfolioByArtistUser(portfolioId, artistUser) // 포트폴리오 조회
                                                )
                                        )
                                    );

        //포트폴리오 내용 재 설정
        final List<PortfolioContent> contents = dto.toPortfolioContentList();

        //포트폴리오 그림 분야 재 설정
        final List<PortfolioDrawingField> portfolioDrawingFields = getPortfolioDrawingFields(
                getDrawingFieldsById(dto.getDrawingFieldIds())
        );

        // 포트폴리오 상태 설정
        final PortfolioStatus portfolioStatus = getPortfolioStatusOnIsTemporary(dto.getTemporary());

        //포트폴리오 업데이트
        portfolio.updatePortfolio(
                dto.getTitle(), dto.getMainImageUrl(), contents, portfolioDrawingFields, dto.getTags(), portfolioStatus
        );
    }

    private Portfolio validateUpdatablePortfolio(Portfolio portfolio) {
        switch (portfolio.getStatus()){
            case DELETED:
            case HIDDEN:
                throw new PortfolioException(PortfolioErrorCode.NOT_FOUND_PORTFOLIO);
        }
        return portfolio;
    }

    private Portfolio deletePortfolioDrawingField(Portfolio portfolio) {
        portfolioDrawingFieldRepository.deleteAllInBatch(
                portfolio.getPortfolioDrawingFields()
        );
        return portfolio;
    }

    private Portfolio deletePortfolioContents(Portfolio portfolio) {
        portfolioContentRepository.deleteAllInBatch(
                portfolio.getPortfolioContents()
        );
        return portfolio;
    }
}

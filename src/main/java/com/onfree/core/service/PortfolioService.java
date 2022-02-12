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
import com.onfree.core.entity.Portfolio;
import com.onfree.core.entity.PortfolioDrawingField;
import com.onfree.core.entity.portfoliocontent.PortfolioContent;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void addPortfolio(Long userId, CreatePortfolioDto.Request dto) {
        ArtistUser artistUser = getArtistUser(userId);
        portfolioRepository.save(
                createPortfolio(artistUser, dto)
        );
    }

    private ArtistUser getArtistUser(Long userId) {
        return (ArtistUser) userRepository.findById(userId)
                .orElseThrow(() ->  new UserException(UserErrorCode.NOT_FOUND_USERID));
    }

    private Portfolio createPortfolio(ArtistUser artistUser, CreatePortfolioDto.Request dto) {
        // 포트폴리오 그림분야 설정
        final List<PortfolioDrawingField> portfolioDrawingFields = getPortfolioDrawingFields(
                getDrawingFieldsById(dto.getDrawingFieldIds())
        );

        final List<PortfolioContent> portfolioContentList = dto.toPortfolioContentList(); //포트폴리오 내용

        final String tags = String.join(",", dto.getTags()); //태그 설정

        return Portfolio.createPortfolio(
                artistUser, dto.getMainImageUrl(), dto.getTitle(), portfolioContentList, tags,
                portfolioDrawingFields, false, dto.isTemporary()
        );
    }

    private List<PortfolioDrawingField> getPortfolioDrawingFields(List<DrawingField> drawingFields) {
        return drawingFields.stream()
                .map(PortfolioDrawingField::createPortfolioDrawingField)
                .collect(Collectors.toList());
    }

    private List<DrawingField> getDrawingFieldsById(List<Long> drawingFieldIds) {
        return drawingFieldRepository.findAllByDisabledIsFalseAndDrawingFieldIdIn(drawingFieldIds);
    }

    /** 포트폴리오 상세 조회*/
    @Transactional
    public PortfolioDetailDto findPortfolio(Long portfolioId, boolean temporary) {
        Portfolio portfolio = getPortfolioByPortfolioIdAndTemporary(portfolioId, temporary);
        portfolio.increaseView();
        return getPortfolioDetailDto(portfolio);
    }

    private Portfolio getPortfolioByPortfolioIdAndTemporary(Long portfolioId, boolean temporary) {
        return portfolioRepository.findByPortfolioIdAndTemporaryAndDeletedIsFalse(portfolioId, temporary)
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.NOT_FOUND_PORTFOLIO));
    }

    private PortfolioDetailDto getPortfolioDetailDto(Portfolio portfolio) {
        final PortfolioDetailDto portfolioDetailDto = PortfolioDetailDto.fromEntity(portfolio);

        portfolioDetailDto.setDrawingFields(
                getUsedDrawingFieldDtos(
                        getAllDrawingFieldList(),
                        getDrawingFieldIdsOfPortfolio(portfolio))
        );
        return portfolioDetailDto;
    }

    private List<Long> getDrawingFieldIdsOfPortfolio(Portfolio portfolio) {
        return portfolio.getPortfolioDrawingFields().stream()
                .map(PortfolioDrawingField::getDrawingFieldId)
                .collect(Collectors.toList());
    }

    private List<DrawingField> getAllDrawingFieldList() {
        return drawingFieldRepository.findAllByDisabledIsFalseOrderByTopDesc();
    }

    private List<UsedDrawingFieldDto> getUsedDrawingFieldDtos(List<DrawingField> allDrawingFields, List<Long> drawingFieldIdsOfPortfolio) {
        if(drawingFieldIdsOfPortfolio.isEmpty()){
            return allDrawingFields.stream()
                    .map(drawingField -> createUsedDrawingFieldDtoFromEntity(drawingField, false))
                    .collect(Collectors.toList());
        }

        return allDrawingFields.stream()
                .map(drawingField -> {
                    if(drawingFieldIdsOfPortfolio.contains(drawingField.getDrawingFieldId())){
                        return createUsedDrawingFieldDtoFromEntity(drawingField, true);
                    }
                    return createUsedDrawingFieldDtoFromEntity(drawingField, false);
                }).collect(Collectors.toList());
    }

    /** 작가 포트폴리오 조회*/
    public List<PortfolioSimpleDto> findAllPortfolioByUserIdAndTemporary(Long userId, Boolean temporary) {
        final ArtistUser artistUser = getArtistUser(userId);
        return getAllPortfolioSimpleDtoByArtistUser(artistUser, temporary);
    }

    private List<PortfolioSimpleDto> getAllPortfolioSimpleDtoByArtistUser(ArtistUser artistUser, boolean temporary) {
        return portfolioRepository.findByArtistUserAndTemporaryAndDeletedIsFalse(artistUser, temporary).stream()
                .map(PortfolioSimpleDto::fromEntity)
                .collect(Collectors.toList());
    }
    /** 포트폴리오 삭제 */
    @Transactional
    public void removePortfolio(Long portfolioId, Long userId) {
        final Portfolio portfolio = getPortfolio(portfolioId);
        validPortfolioIsWriter(userId, portfolio);
        validateAlreadyDeleted(portfolio);
        portfolio.remove();
    }

    private void validateAlreadyDeleted(Portfolio portfolio) {
        if(portfolio.isDeleted()){
            throw new PortfolioException(PortfolioErrorCode.ALREADY_DELETED_PORTFOLIO);
        }
    }

    private Portfolio getPortfolio(Long portfolioId) {
        return portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new PortfolioException(PortfolioErrorCode.NOT_FOUND_PORTFOLIO));
    }
    /** 포트폴리오 대표 설정*/
    @Transactional
    public void representPortfolio(Long portfolioId, Long userId) {
        final Portfolio portfolio = getPortfolio(portfolioId);
        validPortfolioIsWriter(userId, portfolio);
        validatePortfolioPossiblesRepresentative(portfolio);
        updateOffRepresentativeOfPortfolios(
                getAllPortfolioByArtistUser(portfolio)
        );
        portfolio.represent();
    }

    private void validatePortfolioPossiblesRepresentative(Portfolio portfolio) {
        if(portfolio.isTemporary()){
            throw new PortfolioException(PortfolioErrorCode.NOT_ALLOW_REPRESENTATIVE_TEMP_PORTFOLIO);
        }
        if(portfolio.isRepresentative()){
            throw new PortfolioException(PortfolioErrorCode.ALREADY_REGISTER_REPRESENTATIVE_PORTFOLIO);
        }
    }

    private List<Portfolio> getAllPortfolioByArtistUser(Portfolio portfolio) {
        return portfolioRepository.findByArtistUserAndDeletedIsFalse(portfolio.getArtistUser());
    }

    private void updateOffRepresentativeOfPortfolios(List<Portfolio> artistAllPortfolios) {
        artistAllPortfolios.forEach(Portfolio::representCancel);
    }
    /** 포트폴리오 수정하기 */
    @Transactional
    public void updatePortfolio(Long portfolioId, Long userId, UpdatePortfolioDto dto) {
        final Portfolio portfolio = getPortfolio(portfolioId);
        validPortfolioIsWriter(userId, portfolio);

        //내용 및 그림 분야 삭제
        deletePortfolioContents(portfolio);
        deletePortfolioDrawingField(portfolio);

        //내용 재 설정
        final List<PortfolioContent> contents = dto.toPortfolioContentList();

        //그림 분야 재 설정
        final List<PortfolioDrawingField> portfolioDrawingFields = getPortfolioDrawingFields(
                getDrawingFieldsById(dto.getDrawingFieldIds())
        );

        portfolio.updatePortfolio(
                dto.getTitle(), dto.getMainImageUrl(), contents, portfolioDrawingFields, dto.getTags(), dto.getTemporary()
        );
    }
    private void validPortfolioIsWriter(Long userId, Portfolio portfolio) {
        if(!portfolio.isWriter(userId)){
            throw new PortfolioException(PortfolioErrorCode.NOT_ACCESS_PORTFOLIO);
        }
    }

    private void deletePortfolioDrawingField(Portfolio portfolio) {
        portfolioDrawingFieldRepository.deleteAllInBatch(portfolio.getPortfolioDrawingFields());
    }

    private void deletePortfolioContents(Portfolio portfolio) {
        portfolioContentRepository.deleteAllInBatch(portfolio.getPortfolioContents());
    }




}

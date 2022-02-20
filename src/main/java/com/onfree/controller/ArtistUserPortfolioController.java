package com.onfree.controller;

import com.onfree.common.annotation.CurrentArtistUser;
import com.onfree.core.dto.portfolio.PortfolioSimpleDto;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.service.ArtistUserPortfolioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
@Api(tags = "작가유저와 포트폴리오 관련 API")
@RequestMapping(value = "/api/v1/users/artist", produces = MediaTypes.HAL_JSON_VALUE)
public class ArtistUserPortfolioController {
    private final ArtistUserPortfolioService artistUserPortfolioService;
    public final int PAGESIZE = 6;

    /** 작가 포트폴리오 전체 조회*/
    @ApiOperation(value = "작가 포트폴리오 전체 조회 API", notes = "임시 저장 포트폴리오 목록에 경우 작가유저 본인만 접근 가능, 나머지는 누구나 조회 가능")
    @GetMapping("/{userId}/portfolios")
    public ResponseEntity portfolioList(
            @ApiParam("작가유저 PK")
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            PagedResourcesAssembler<PortfolioSimpleDto> assembler
    ){
        // 서비스 응답
        final PageRequest pageRequest = PageRequest.of(page, PAGESIZE);
        final Page<PortfolioSimpleDto> portfolioSimpleDtos
                = artistUserPortfolioService.findAllPortfolioByUserId(userId, pageRequest);

        // 링크 추가 작업
        PagedModel<EntityModel<PortfolioSimpleDto>> entityModels = assembler.toModel(portfolioSimpleDtos);
        addLinkToPortfolios(entityModels);

        return ResponseEntity.ok(entityModels);
    }

    private void addLinkToPortfolios(PagedModel<EntityModel<PortfolioSimpleDto>> entityModels) {
        entityModels.forEach(entityModel -> entityModel.add(
                linkTo(PortfolioController.class)
                    .slash(entityModel.getContent().getPortfolioId())
                    .withRel("detail-portfolio")
        ));
    }

    /** 작가 임시 저장 포트폴리오 전체 조회*/
    @ApiOperation(value = "작가 임시 포트폴리오 전체 조회 API", notes = "임시 저장 포트폴리오 목록에 경우 작가유저 본인만 접근 가능, 나머지는 누구나 조회 가능")
    @PreAuthorize("hasRole('ARTIST')")
    @GetMapping("/me/portfolios/temp")
    public ResponseEntity tempPortfolioList(
            @RequestParam(defaultValue = "0") int page,
            @CurrentArtistUser ArtistUser artistUser,
            PagedResourcesAssembler<PortfolioSimpleDto> assembler
    ){
        Page<PortfolioSimpleDto> portfolioSimpleDtos
                = artistUserPortfolioService.findAllTempPortfolioByArtistUser(artistUser, page);

        // 링크 추가 작업
        PagedModel<EntityModel<PortfolioSimpleDto>> entityModels = assembler.toModel(portfolioSimpleDtos);
        addLinkToTempPortfolio(entityModels);

        return ResponseEntity.ok(entityModels);
    }

    private void addLinkToTempPortfolio(PagedModel<EntityModel<PortfolioSimpleDto>> entityModels) {
        entityModels.forEach(entityModel -> entityModel.add(
                linkTo(PortfolioController.class).slash(
                        entityModel.getContent().getPortfolioId()
                ).slash("temp")
                        .withRel("detail-portfolio")
        ));
    }
}

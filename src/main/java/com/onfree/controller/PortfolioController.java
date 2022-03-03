package com.onfree.controller;

import com.onfree.common.annotation.CurrentArtistUser;
import com.onfree.common.model.SimpleResponse;
import com.onfree.core.dto.portfolio.CreatePortfolioDto;
import com.onfree.core.dto.portfolio.PortfolioDetailDto;
import com.onfree.core.dto.portfolio.UpdatePortfolioDto;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.service.PortfolioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.onfree.common.model.SimpleResponse.success;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/portfolios",  consumes = MediaType.APPLICATION_JSON_VALUE)
public class PortfolioController {
    private final PortfolioService portfolioService;

    /** 포트폴리오 추가 */
    @ApiOperation(value = "포트폴리오 작성 API", notes = "작가 포트폴리오를 작성하는 API")
    @PreAuthorize("hasRole('ARTIST')")
    @PostMapping()
    public ResponseEntity<CreatePortfolioDto.Response> portfolioAdd(
            @CurrentArtistUser ArtistUser artistUser,
            @Valid @RequestBody CreatePortfolioDto.Request dto,
            BindingResult errors
            ) {

        final CreatePortfolioDto.Response response
                = portfolioService.addPortfolio(artistUser, dto);
        response.add(
                linkTo(PortfolioController.class).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) +"/#/portfolio-controller/portfolioAddUsingPOST").withRel("profile")
        );
        return ResponseEntity
                .created(
                        linkTo(PortfolioController.class)
                                .slash(response.getPortfolioId())
                                .toUri()
                )
                .body(
                        response
                );
    }

    /** 포트폴리오 상세 조회 */
    @GetMapping("/{portfolioId}")
    @ApiOperation(value = "포트폴리오 상세 조회 API", notes = "포트폴리오 상세 조회 API")
    public PortfolioDetailDto portfolioDetails(
            @ApiParam("포트폴리오 PK")
            @PathVariable("portfolioId") Long portfolioId
    ){
        final PortfolioDetailDto portfolio = portfolioService.findPortfolio(portfolioId);
        portfolio.add(
                linkTo(PortfolioController.class).slash(portfolio.getPortfolioId()).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/portfolio-controller/portfolioDetailsUsingGET").withRel("profile")
        );
        return portfolio;
    }

    /** 임시 저장 포트폴리오 조회*/
    @ApiOperation(value = "임시 저장 포트폴리오 상세 조회 API", notes = "작가 유저 본인만 접근 가능")
    @PreAuthorize("hasRole('ARTIST')")
    @GetMapping("/{portfolioId}/temp")
    public PortfolioDetailDto tempPortfolioDetails(
            @ApiParam("포트폴리오 PK")
            @PathVariable("portfolioId") Long portfolioId,
            @CurrentArtistUser ArtistUser artistUser
    ){
        final PortfolioDetailDto tempPortfolio = portfolioService.findTempPortfolio(portfolioId, artistUser);
        tempPortfolio.add(
                linkTo(PortfolioController.class).slash(tempPortfolio.getPortfolioId()).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/portfolio-controller/tempPortfolioDetailsUsingGET").withRel("profile"),
                linkTo(PortfolioController.class).slash(portfolioId).withRel("update-portfolio")
        );
        return tempPortfolio;
    }

    /** 포트폴리오 삭제하기 */
    @ApiOperation(value = "포트폴리오 삭제 API", notes = "작가유저 본인만 접근 가능")
    @PreAuthorize("hasRole('ARTIST')")
    @DeleteMapping("/{portfolioId}")
    public SimpleResponse portfolioRemove(
            @ApiParam("포트폴리오 PK")
            @PathVariable("portfolioId") Long portfolioId,
            @CurrentArtistUser ArtistUser artistUser

    ){
        portfolioService.removePortfolio(portfolioId, artistUser);
        final SimpleResponse success = success("포트폴리오를 성공적으로 삭제하였습니다.");
        success.add(
          linkTo(PortfolioController.class).slash(portfolioId).withSelfRel(),
          Link.of(linkTo(SwaggerController.class) + "/#/portfolio-controller/portfolioRemoveUsingDELETE").withRel("profile")
        );
        return success;
    }

    /** 대표 포트폴리오 지정*/
    @ApiOperation(value = "포트폴리오 대표 설정", notes = "포트폴리오 대표 설정")
    @PreAuthorize("hasRole('ARTIST')")
    @PutMapping("/{portfolioId}/representative")
    public SimpleResponse portfolioRepresent(
            @ApiParam("포트폴리오 PK")
            @PathVariable Long portfolioId,
            @CurrentArtistUser ArtistUser artistUser
    ){
        portfolioService.representPortfolio(portfolioId, artistUser);
        final SimpleResponse success = success("해당 포트폴리오를 대표설정하였습니다.");
        success.add(
                linkTo(PortfolioController.class).slash(portfolioId).slash("representative").withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/portfolio-controller/portfolioRepresentUsingPUT").withRel("profile")
        );
        return success;

    }

    /** 포트폴리오 수정*/
    @ApiOperation(value = "포트폴리오 수정 API", notes = "자기 포트폴리오 수정하기")
    @PreAuthorize("hasRole('ARTIST')")
    @PutMapping("/{portfolioId}")
    public SimpleResponse portfolioUpdate(
            @PathVariable Long portfolioId,
            @CurrentArtistUser ArtistUser artistUser,
            @Valid @RequestBody UpdatePortfolioDto updatePortfolioDto,
            BindingResult errors
    ){
        portfolioService.updatePortfolio(portfolioId, artistUser, updatePortfolioDto);
        final SimpleResponse success = success("포트폴리오 수정을 완료 하였습니다.");
        success.add(
                linkTo(PortfolioController.class).slash(portfolioId).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/portfolio-controller/portfolioUpdateUsingPUT").withRel("profile")
        );

        return success;
    }
}
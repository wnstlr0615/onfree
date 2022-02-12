package com.onfree.controller;

import com.onfree.common.model.SimpleResponse;
import com.onfree.core.dto.portfolio.CreatePortfolioDto;
import com.onfree.core.dto.portfolio.PortfolioDetailDto;
import com.onfree.core.dto.portfolio.PortfolioSimpleDto;
import com.onfree.core.dto.portfolio.UpdatePortfolioDto;
import com.onfree.core.service.PortfolioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.onfree.common.model.SimpleResponse.success;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolios")
@Api(tags = "포트폴리오 API", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class PortfolioController {
    private final PortfolioService portfolioService;

    /** 포트폴리오 추가*/
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "포트폴리오 작성 API", notes = "작가 포트폴리오를 작성하는 API")
    @PreAuthorize("hasRole('ARTIST') and @checker.isSelf(#userId)")
    @PostMapping()
    public SimpleResponse<?> portfolioAdd(
            @ApiParam("작가유저 PK")
            @RequestParam Long userId,
            @Valid @RequestBody CreatePortfolioDto.Request dto,
            BindingResult errors
    ){
        //TODO 임시 저장글일 경우 대표 설정 끄기 validation 진행
        portfolioService.addPortfolio(userId, dto);
        return success("포트폴리오가 성공적으로 등록되었습니다.");
    }

    /** 포트폴리오 상세 조회 */
    @GetMapping("/{portfolioId}")
    @ApiOperation(value = "포트폴리오 상세 조회 API", notes = "포트폴리오 상세 조회 API")
    public SimpleResponse<PortfolioDetailDto> portfolioDetails(
            @ApiParam("포트폴리오 PK")
            @PathVariable("portfolioId") Long portfolioId
    ){
        return success(
                portfolioService.findPortfolio(portfolioId, false)
        );
    }

    /** 임시 저장 포트폴리오 조회*/
    @ApiOperation(value = "임시 저장 포트폴리오 상세 조회 API", notes = "작가유저 본인만 접근 가능")
    @PreAuthorize("hasRole('ARTIST') and @checker.isSelf(#userId)")
    @GetMapping("/{portfolioId}/temp")
    public SimpleResponse<PortfolioDetailDto> tempPortfolioDetails(
            @ApiParam("포트폴리오 PK")
            @PathVariable("portfolioId") Long portfolioId,
            @ApiParam("작가유저 PK")
            @RequestParam Long userId
    ){
        return success(
                portfolioService.findPortfolio(portfolioId, true)
        );
    }

    /** 작가 포트폴리오 전체 조회*/
    @ApiOperation(value = "작가 포트폴리오 전체 조회 API", notes = "임시 저장 포트폴리오 목록에 경우 작가유저 본인만 접근 가능, 나머지는 누구나 조회 가능")
    @PreAuthorize("!#temporary or (#temporary and hasRole('ARTIST') and @checker.isSelf(#userId))")
    @GetMapping()
    public SimpleResponse<PortfolioSimpleDto> portfolioDetailList(
            @ApiParam("작가유저 PK")
            @RequestParam Long userId,
            @ApiParam(value = "임시 저장글 조회 유무", required = true, defaultValue = "false")
            @RequestParam(defaultValue = "false") boolean temporary
    ){
        return success(
               portfolioService.findAllPortfolioByUserIdAndTemporary(userId, temporary)
        );
    }

    /** 포트폴리오 삭제하기 */
    @ApiOperation(value = "포트폴리오 삭제 API", notes = "작가유저 본인만 접근 가능")
    @PreAuthorize("hasRole('ARTIST') and @checker.isSelf(#userId)")
    @DeleteMapping("/{portfolioId}")
    public SimpleResponse portfolioRemove(
            @ApiParam("포트폴리오 PK")
            @PathVariable("portfolioId") Long portfolioId,
            @ApiParam("작가유저 PK")
            @RequestParam Long userId
    ){
        portfolioService.removePortfolio(portfolioId, userId);
        return success("포트폴리오를 성공적으로 삭제하였습니다.");
    }

    /** 대표 포트폴리오 지정*/
    @ApiOperation(value = "포트폴리오 대표 설정", notes = "포트폴리오 대표 설정")
    @PreAuthorize("hasRole('ARTIST') and @checker.isSelf(#userId)")
    @PutMapping("/{portfolioId}/representative")
    public SimpleResponse<?> portfolioRepresent(
            @ApiParam("포트폴리오 PK")
            @PathVariable Long portfolioId,
            @ApiParam("작가유저 PK")
            @RequestParam Long userId
    ){
        portfolioService.representPortfolio(portfolioId, userId);

        return success("해당 포트폴리오를 대표설정하였습니다.");

    }

    /** 포트폴리오 수정*/
    @ApiOperation(value = "포트폴리오 수정 API", notes = "자기 포트폴리오 수정하기")
    @PreAuthorize("hasRole('ARTIST') and @checker.isSelf(#userId)")
    @PutMapping("/{portfolioId}")
    public SimpleResponse<?> portfolioUpdate(
            @PathVariable Long portfolioId,
            @RequestParam Long userId,
            @Valid @RequestBody UpdatePortfolioDto updatePortfolioDto
    ){
        portfolioService.updatePortfolio(portfolioId, userId, updatePortfolioDto);
        return success("포트폴리오 수정을 완료 하였습니다.");
    }
}
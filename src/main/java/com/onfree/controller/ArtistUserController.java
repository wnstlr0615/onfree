package com.onfree.controller;

import com.onfree.common.annotation.CurrentArtistUser;
import com.onfree.common.error.code.GlobalErrorCode;
import com.onfree.common.error.exception.GlobalException;
import com.onfree.common.model.SimpleResponse;
import com.onfree.core.dto.portfolio.PortfolioSimpleDto;
import com.onfree.core.dto.user.DeletedUserResponse;
import com.onfree.core.dto.user.artist.ArtistUserDetailDto;
import com.onfree.core.dto.user.artist.CreateArtistUserDto;
import com.onfree.core.dto.user.artist.UpdateArtistUserDto;
import com.onfree.core.dto.user.artist.status.StatusMarkDto;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.service.ArtistUserService;
import com.onfree.core.service.PortfolioService;
import com.onfree.validator.StatusMarkValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Api(tags = "작가유저 기본기능 제공 컨트롤러",  consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/users/artist",  consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtistUserController {
    public final int PAGESIZE = 6;
    private final ArtistUserService artistUserService;
    private final PortfolioService portfolioService;

    private final StatusMarkValidator statusMarkValidator;
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(value = "!isAuthenticated()")
    @ApiOperation(value = "작가 유저 회원 가입 요청" , notes = "작가 유저 회원 가입 요청")
    @PostMapping("")
    public CreateArtistUserDto.Response createNormalUser(
            @RequestBody @Valid  CreateArtistUserDto.Request request,
            BindingResult errors){
        return artistUserService.createArtistUser(request);
    }

    @PreAuthorize("hasRole('ARTIST') and @checker.isSelf(#userId)")
    @ApiOperation(value = "작가 유저 사용자 정보 조회", notes = "작가 유저 사용자 정보 조회")
    @GetMapping("/{userId}")
    public ArtistUserDetailDto getUserInfo(
            @ApiParam(value = "사용자 userId ") @PathVariable(name = "userId") Long userId
    ){
        return artistUserService.getUserDetail(userId);
    }

    @PreAuthorize("hasRole('ARTIST') and @checker.isSelf(#userId)")
    @ApiOperation(value = "작가 유저 사용자 deleted 처리")
    @DeleteMapping("/{deletedUserId}")
    public DeletedUserResponse deletedNormalUser(
            @ApiParam(value = "사용자 userId ") @PathVariable(name = "deletedUserId") Long userId
    ){
        return artistUserService.deletedArtistUser(userId);
    }

    @PreAuthorize("hasRole('ARTIST') and @checker.isSelf(#userId)")
    @ApiOperation(value = "작가 유저 정보수정")
    @PutMapping("/{userId}")
    public UpdateArtistUserDto.Response updateUserInfo(
            @ApiParam(value = "업데이트 할 사용자 ID") @PathVariable("userId") Long userId,
            @RequestBody @Valid UpdateArtistUserDto.Request request,
            BindingResult errors
    ){
        return artistUserService.modifiedUser(userId, request);
    }


    @PreAuthorize("hasRole('ARTIST') and @checker.isSelf(#userId)")
    @ApiOperation(value = "영업마크 설정")
    @PutMapping("/{userId}/status")
    public SimpleResponse updateStatusMark(
            @ApiParam(value = "유저 PK", example = "1")
            @PathVariable("userId") Long userId,
            @Valid @RequestBody StatusMarkDto statusMarkDto,
            BindingResult errors
    ){
        statusMarkValidator.validate(statusMarkDto, errors);
        validStatusMark(errors);
        artistUserService.updateStatusMark(userId, statusMarkDto);
        return SimpleResponse.success("영업마크가 성공적으로 변경 되었습니다.");
    }

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
                = portfolioService.findAllPortfolioByUserId(userId, pageRequest);

        // 링크 추가 작업
        final var entityModels = assembler.toModel(portfolioSimpleDtos);
        entityModels.forEach(entityModel -> entityModel.add(
                linkTo(PortfolioController.class).slash(
                        entityModel.getContent().getPortfolioId()
                        ).withRel("detail-portfolio")
        ));

        return ResponseEntity.ok(entityModels);
    }

    @ApiOperation(value = "작가 임시 포트폴리오 전체 조회 API", notes = "임시 저장 포트폴리오 목록에 경우 작가유저 본인만 접근 가능, 나머지는 누구나 조회 가능")
    @PreAuthorize("hasAnyRole('ARTIST')")
    @GetMapping("/{userId}/portfolios/temp")
    public ResponseEntity tempPortfolioList(
            @ApiParam("작가유저 PK")
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @CurrentArtistUser ArtistUser artistUser,
            PagedResourcesAssembler<PortfolioSimpleDto> assembler

    ){
        validateAccessUser(userId, artistUser);
        final PageRequest pageRequest = PageRequest.of(page, PAGESIZE);
        final Page<PortfolioSimpleDto> portfolioSimpleDtos
                = portfolioService.findAllTempPortfolioByArtistUser(artistUser, pageRequest);

        // 링크 추가 작업
        final var entityModels = assembler.toModel(portfolioSimpleDtos);
        entityModels.forEach(entityModel -> entityModel.add(
                linkTo(PortfolioController.class).slash(
                        entityModel.getContent().getPortfolioId()
                ).slash("temp")
                        .withRel("detail-portfolio")
        ));

        return ResponseEntity.ok(entityModels);
    }

    private void validateAccessUser(Long userId, ArtistUser artistUser) {
        if(!artistUser.getUserId().equals(userId)){
            throw new GlobalException(GlobalErrorCode.ACCESS_DENIED);
        }
    }

    private void validStatusMark(BindingResult error) {
        if(error.hasErrors()){
            throw new GlobalException(GlobalErrorCode.NOT_VALIDATED_REQUEST, error.getFieldErrors());
        }
    }

}

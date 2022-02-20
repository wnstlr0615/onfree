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
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
@RestController
@RequiredArgsConstructor
@Api(tags = "작가유저 기본기능 제공 컨트롤러",  consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping(value = "/api/v1/users/artist",  consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtistUserController {

    private final ArtistUserService artistUserService;

    private final StatusMarkValidator statusMarkValidator;

    /** 회원 가입*/
    @PreAuthorize(value = "!isAuthenticated()")
    @ApiOperation(value = "작가 유저 회원 가입 요청" , notes = "작가 유저 회원 가입 요청")
    @PostMapping("")
    public ResponseEntity<CreateArtistUserDto.Response> userAdd(
            @RequestBody @Valid  CreateArtistUserDto.Request request,
            BindingResult errors){

        return ResponseEntity.created(
                linkTo(ArtistUserController.class).slash("me").toUri()
            )
                .body(artistUserService.addArtistUser(request));
    }

    /** 본인 정보 상세 조회*/
    @PreAuthorize("hasRole('ARTIST')")
    @ApiOperation(value = "작가 유저 본인 사용자 정보 조회", notes = "작가 유저 본전 사용자 정보 조회")
    @GetMapping("/me")
    public ArtistUserDetailDto userDetails(
            @CurrentArtistUser ArtistUser artistUser
    ){
        final ArtistUserDetailDto response = artistUserService.getUserDetail(artistUser.getUserId());
        response.add(
                linkTo(ArtistUserController.class).slash("me").withSelfRel()
                .withProfile("/swagger-ui/#/작가유저%20기본기능%20제공%20컨트롤러/getUserInfoUsingGET")
        );
        return response;
    }

    /** 사용자 탈퇴 */
    @PreAuthorize("hasRole('ARTIST')")
    @ApiOperation(value = "작가 유저 사용자 deleted 처리")
    @DeleteMapping("/me")
    public SimpleResponse userRemove(
            @CurrentArtistUser ArtistUser artistUser
    ){
        artistUserService.removeArtistUser(artistUser.getUserId());
        return SimpleResponse.success("사용자가 정상적으로 삭제되었습니다.");
    }

    /** 사용자 정보 수정*/
    @PreAuthorize("hasRole('ARTIST')")
    @ApiOperation(value = "작가 유저 정보수정")
    @PutMapping("/me")
    public SimpleResponse artistUserModify(
            @CurrentArtistUser ArtistUser artistUser,
            @RequestBody @Valid UpdateArtistUserDto.Request request,
            BindingResult errors
    ){
        artistUserService.modifyArtistUser(artistUser.getUserId(), request);
        return SimpleResponse.success("사용자 정보가 정상적으로 수정 되었습니다.");
    }

    /** 작가유저 영업마크 설정*/
    @PreAuthorize("hasRole('ARTIST')")
    @ApiOperation(value = "영업마크 설정")
    @PatchMapping("/me/status")
    public SimpleResponse updateStatusMark(
            @CurrentArtistUser ArtistUser artistUser,
            @Valid @RequestBody StatusMarkDto statusMarkDto,
            BindingResult errors
    ){
        statusMarkValidator.validate(statusMarkDto, errors);
        validStatusMark(errors);
        artistUserService.updateStatusMark(artistUser.getUserId(), statusMarkDto);
        return SimpleResponse.success("영업마크가 성공적으로 변경 되었습니다.");
    }

    private void validStatusMark(BindingResult error) {
        if(error.hasErrors()){
            throw new GlobalException(GlobalErrorCode.NOT_VALIDATED_REQUEST, error.getFieldErrors());
        }
    }

}

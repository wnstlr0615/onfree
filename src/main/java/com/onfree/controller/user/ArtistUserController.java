package com.onfree.controller.user;

import com.onfree.common.annotation.CurrentArtistUser;
import com.onfree.common.error.code.GlobalErrorCode;
import com.onfree.common.error.exception.GlobalException;
import com.onfree.common.model.SimpleResponse;
import com.onfree.controller.SwaggerController;
import com.onfree.core.dto.user.artist.ArtistUserDetailDto;
import com.onfree.core.dto.user.artist.CreateArtistUserDto;
import com.onfree.core.dto.user.artist.UpdateArtistUserDto;
import com.onfree.core.dto.user.artist.UpdateNicknameDto;
import com.onfree.core.dto.user.artist.status.StatusMarkDto;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.service.user.ArtistUserService;
import com.onfree.validator.StatusMarkValidator;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/users/artist")
public class ArtistUserController {

    private final ArtistUserService artistUserService;
    private final StatusMarkValidator statusMarkValidator;

    /** 회원 가입*/
    @PreAuthorize(value = "!isAuthenticated()")
    @ApiOperation(value = "작가 유저 회원 가입 요청" , notes = "작가 유저 회원 가입 요청")
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateArtistUserDto.Response> artistUserAdd(
            @RequestBody @Valid  CreateArtistUserDto.Request request
    ){
        CreateArtistUserDto.Response response = artistUserService.addArtistUser(request);

        //링크 추가
        response.add(
                linkTo(NormalUserController.class).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/artist-user-controller/artistUserAddUsingPOST").withRel("profile")
        );

        return ResponseEntity.created(
                linkTo(ArtistUserController.class).slash("me").toUri()
            ).body(response);
    }

    /** 본인 정보 상세 조회*/
    @PreAuthorize("hasRole('ARTIST')")
    @ApiOperation(value = "작가 유저 본인 사용자 정보 조회", notes = "작가 유저 본전 사용자 정보 조회")
    @GetMapping("/me")
    public ArtistUserDetailDto artistUserDetails(
            @CurrentArtistUser ArtistUser artistUser
    ){
        ArtistUserDetailDto response = artistUserService.getUserDetail(artistUser.getUserId());
        //링크 추가
        response.add(
                linkTo(ArtistUserController.class).slash("me").withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/artist-user-controller/artistUserDetailsUsingGET").withRel("profile")
        );
        return response;
    }

    /** 사용자 탈퇴 */
    @PreAuthorize("hasRole('ARTIST')")
    @ApiOperation(value = "작가 유저 사용자 deleted 처리")
    @DeleteMapping("/me")
    public SimpleResponse artistUserRemove(
            @CurrentArtistUser ArtistUser artistUser
    ){
        artistUserService.removeArtistUser(artistUser.getUserId());
        SimpleResponse response = SimpleResponse.success("사용자가 정상적으로 삭제되었습니다.");

        //링크 추가
        response.add(
                linkTo(ArtistUser.class).slash("me").withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/artist-user-controller/artistUserRemoveUsingDELETE").withRel("profile")
        );

        return response;
    }

    /** 사용자 정보 수정*/
    @PreAuthorize("hasRole('ARTIST')")
    @ApiOperation(value = "작가 유저 정보수정")
    @PutMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SimpleResponse artistUserModify(
            @CurrentArtistUser ArtistUser artistUser,
            @RequestBody @Valid UpdateArtistUserDto.Request request
    ){
        artistUserService.modifyArtistUser(artistUser.getUserId(), request);
        SimpleResponse response = SimpleResponse.success("사용자 정보가 정상적으로 수정 되었습니다.");
        // 링크 추가
        response.add(
                linkTo(ArtistUserController.class).slash("me").withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/artist-user-controller/artistUserModifyUsingPUT").withProfile("profile")
        );

        return response;
    }

    /** 작가유저 영업마크 설정*/
    @PreAuthorize("hasRole('ARTIST')")
    @ApiOperation(value = "영업마크 설정")
    @PatchMapping(value = "/me/status", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SimpleResponse statusMarkModify(
            @CurrentArtistUser ArtistUser artistUser,
            @Valid @RequestBody StatusMarkDto statusMarkDto,
            BindingResult errors
    ){
        statusMarkValidator.validate(statusMarkDto, errors);
        validStatusMark(errors);
        artistUserService.updateStatusMark(artistUser.getUserId(), statusMarkDto);
        SimpleResponse response = SimpleResponse.success("영업마크가 성공적으로 변경 되었습니다.");

        response.add(
                linkTo(ArtistUserController.class).slash("me").slash("status").withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/artist-user-controller/updateStatusMarkUsingPATCH").withRel("profile")
        );
        return response;
    }

    /** 닉네임 변경 하기*/
    @PreAuthorize("hasRole('ARTIST')")
    @PatchMapping(value = "/me/nickname", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SimpleResponse nicknameModify(
            @RequestBody @Valid UpdateNicknameDto updateNicknameDto,
            @CurrentArtistUser ArtistUser artistUser
    ){
        artistUserService.updateNickname(artistUser.getUserId(), updateNicknameDto.getNickname());

        SimpleResponse response = SimpleResponse.success("닉네임이 성공적으로 변경되었습니다.");

        //링크 추가
        response.add(
          linkTo(ArtistUserController.class).slash("me").slash("nickname").withSelfRel(),
          Link.of(linkTo(SwaggerController.class) + "/#/artist-user-controller/updateNicknameUsingPatch").withRel("profile")
        );

        return response;
    }

    private void validStatusMark(BindingResult error) {
        if(error.hasErrors()){
            throw new GlobalException(GlobalErrorCode.NOT_VALIDATED_REQUEST, error.getFieldErrors());
        }
    }

}

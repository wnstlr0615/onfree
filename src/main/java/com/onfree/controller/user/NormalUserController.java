package com.onfree.controller.user;

import com.onfree.common.annotation.CurrentNormalUser;
import com.onfree.common.model.SimpleResponse;
import com.onfree.controller.SwaggerController;
import com.onfree.core.dto.user.normal.CreateNormalUserDto;
import com.onfree.core.dto.user.normal.NormalUserDetailDto;
import com.onfree.core.dto.user.normal.UpdateNormalUserDto;
import com.onfree.core.entity.user.NormalUser;
import com.onfree.core.service.user.NormalUserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/users/normal")
public class NormalUserController {
    private final NormalUserService normalUserService;

    @PreAuthorize("!isAuthenticated()")
    @ApiOperation(value = "일반 유저 회원 가입 요청" , notes = "일반 유저 회원 가입 요청")
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> normalUserAdd(
            @RequestBody @Valid  CreateNormalUserDto.Request request
    ){
        CreateNormalUserDto.Response response = normalUserService.addNormalUser(request);
        //링크 추가
        response.add(
                linkTo(NormalUserController.class).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/normal-user-controller/normalUserAddUsingPOST").withRel("profile")
        );

        return ResponseEntity.created(
                linkTo(NormalUserController.class).slash("me").toUri()
        ).body(response);
    }

    @PreAuthorize(value = "hasRole('NORMAL') ")
    @ApiOperation(value = "일반 유저 사용자 정보 조회", notes = "일반 유저 사용자 정보 조회")
    @GetMapping("/me")
    public NormalUserDetailDto normalUserDetails(
            @CurrentNormalUser NormalUser normalUser
    ){
        NormalUserDetailDto response = normalUserService.getUserDetail(normalUser.getUserId());
        //링크 추가
        response.add(
                linkTo(NormalUserController.class).slash("me").withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/normal-user-controller/normalUserDetailsUsingGET").withRel("profile")
        );
        return response;
    }

    @PreAuthorize(value = "hasRole('NORMAL') ")
    @ApiOperation(value = "일반 유저 사용자 deleted 처리")
    @DeleteMapping("/me")
    public SimpleResponse normalUserRemove(
            @CurrentNormalUser NormalUser normalUser
    ){
        normalUserService.removeNormalUser(normalUser.getUserId());
        SimpleResponse response = SimpleResponse.success("사용자가 정상적으로 삭제되었습니다.");

        //링크 추가
        response.add(
                linkTo(NormalUserController.class).slash("me").withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/normal-user-controller/normalUserRemoveUsingDELETE").withRel("profile")
        );
        return response;
    }

    @PreAuthorize(value = "hasRole('NORMAL')")
    @ApiOperation(value = "일반 유저 정보수정")
    @PutMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SimpleResponse normalUserModify(
            @CurrentNormalUser NormalUser normalUser,
            @RequestBody @Valid UpdateNormalUserDto.Request request
    ){
         normalUserService.modifyNormalUser(normalUser.getUserId(), request);
        SimpleResponse response = SimpleResponse.success("사용자 정보가 정상적으로 수정 되었습니다.");

        // 링크 추가
        response.add(
                linkTo(NormalUserController.class).slash("me").withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/normal-user-controller/normalUserModifyUsingPUT").withRel("profile")
        );

        return response;

    }
}

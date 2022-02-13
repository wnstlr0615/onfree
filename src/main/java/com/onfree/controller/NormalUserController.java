package com.onfree.controller;

import com.onfree.core.dto.user.DeletedUserResponse;
import com.onfree.core.dto.user.normal.CreateNormalUserDto;
import com.onfree.core.dto.user.normal.NormalUserDetailDto;
import com.onfree.core.dto.user.normal.UpdateNormalUserDto;
import com.onfree.core.service.NormalUserService;
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

@Api(tags = "일반유저 기본기능 제공 컨트롤러",  consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/users/normal", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class NormalUserController {
    private final NormalUserService normalUserService;

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("!isAuthenticated()")
    @ApiOperation(value = "일반 유저 회원 가입 요청" , notes = "일반 유저 회원 가입 요청")
    @PostMapping("")
    public CreateNormalUserDto.Response createNormalUser(
            @RequestBody @Valid  CreateNormalUserDto.Request request,
            BindingResult errors
    ){
        return normalUserService.createdNormalUser(request);
    }
    @PreAuthorize(value = "hasRole('NORMAL') and @checker.isSelf(#userId)")
    @ApiOperation(value = "일반 유저 사용자 정보 조회", notes = "일반 유저 사용자 정보 조회")
    @GetMapping("/{userId}")
    public NormalUserDetailDto getUserInfo(
            @ApiParam(value = "사용자 userId ") @PathVariable(name = "userId") Long userId
    ){
        return normalUserService.getUserDetail(userId);
    }

    @PreAuthorize(value = "hasRole('NORMAL') and @checker.isSelf(#userId)")
    @ApiOperation(value = "일반 유저 사용자 deleted 처리")
    @DeleteMapping("/{deletedUserId}")
    public DeletedUserResponse deletedNormalUser(
            @ApiParam(value = "사용자 userId ") @PathVariable(name = "deletedUserId") Long userId
    ){
        return normalUserService.deletedNormalUser(userId);
    }

    @PreAuthorize(value = "hasRole('NORMAL') and @checker.isSelf(#userId)")
    @ApiOperation(value = "일반 유저 정보수정")
    @PutMapping("/{userId}")
    public UpdateNormalUserDto.Response updateUserInfo(
            @ApiParam(value = "업데이트 할 사용자 ID") @PathVariable("userId") Long userId,
            @RequestBody @Valid UpdateNormalUserDto.Request request,
            BindingResult errors
    ){
        return normalUserService.modifyedUser(userId, request);
    }
}

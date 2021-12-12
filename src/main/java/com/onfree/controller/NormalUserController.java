package com.onfree.controller;

import com.onfree.core.dto.NormalUserInfo;
import com.onfree.core.dto.user.CreateNormalUser;
import com.onfree.core.service.UserService;
import com.onfree.error.code.UserErrorCode;
import com.onfree.error.exception.UserException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "일반유저 기본기능 제공 컨트롤러")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/normal")
public class NormalUserController {
    private final UserService userService;

    @ApiOperation(value = "일반 유저 회원 가입 요청" , notes = "일반 유저 회원 가입 요청")
    @PostMapping("")
    public CreateNormalUser.Response createNormalUser(
            @RequestBody @Valid  CreateNormalUser.Request request,
            BindingResult errors){
        validParameter(errors);
        return userService.createNormalUser(request);
    }
    @ApiOperation(value = "일반 유저 사용자 정보 조회", notes = "일반 유저 사용자 정보 조회")
    @GetMapping("/{userId}")
    public NormalUserInfo getUserInfo(
            @ApiParam(value = "사용자 userId ") @PathVariable(name = "userId") Long userId
    ){
        return userService.getUserInfo(userId);
    }



    private void validParameter(BindingResult errors) {
        if(errors.hasErrors()){
            printFiledLog(errors);
            throw new UserException(UserErrorCode.NOT_VALID_REQUEST_PARAMETERS);
        }
    }

    private void printFiledLog(BindingResult errors) {
        getFieldErrors(errors)
                .forEach(fieldError ->
                        log.error("field :{} ,rejectValue : {} , message : {}", fieldError.getField(), fieldError.getRejectedValue(), fieldError.getDefaultMessage()));
    }

    private List<FieldError> getFieldErrors(BindingResult errors) {
        return errors.getAllErrors().stream().map(objectError -> (FieldError) objectError).collect(Collectors.toList());
    }
}

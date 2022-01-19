package com.onfree.controller.admin;

import com.onfree.core.dto.notice.CreateNoticeDto;
import com.onfree.core.dto.notice.UpdateNoticeDto;
import com.onfree.core.dto.question.CreateQuestionDto;
import com.onfree.core.dto.question.UpdateQuestionDto;
import com.onfree.core.service.CustomerCenterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/admin/api", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "고객센터 ADMIN 유저 컨트롤러", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomerCenterAdminController {
    private final CustomerCenterService customerCenterService;

    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "공지 생성", notes = "ADMIN 사용자가 공지를 추가하는 요청")
    @PreAuthorize("hasRole('ADMIN') and isAuthenticated()")
    @PostMapping("/notices")
    public CreateNoticeDto.Response createNotice(
            @Valid @RequestBody CreateNoticeDto.Request request,
            BindingResult errors){
        return customerCenterService.createNotice(
                request
        );
    }
    @ApiOperation(value = "공지 수정", notes = "ADMIN 사용자가 공지를 수정하는 요청")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/notices/{noticeId}")
    public UpdateNoticeDto.Response updateNotice(
            @ApiParam(value = "해당 공지 ID", example = "1") @PathVariable("noticeId")Long noticeId,
            @Valid @RequestBody UpdateNoticeDto.Request request,
            BindingResult errors){
        return customerCenterService.updateNotice(
                noticeId, request
        );
    }

    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "질문 생성", notes = "ADMIN 사용자가 질문을 추가하는 요청")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/questions")
    public CreateQuestionDto.Response createQuestion(
            @Valid @RequestBody CreateQuestionDto.Request request,
            BindingResult errors){
        return customerCenterService.createQuestion(
                request
        );
    }

    @ApiOperation(value = "질문 수정", notes = "ADMIN 사용자가 질문을 수정하는 요청")
    @PreAuthorize("hasRole('ADMIN') ")
    @PutMapping("/questions/{questionId}")
    public UpdateQuestionDto.Response updateQuestion(
            @ApiParam(value = "해당 질문 ID", example = "1") @PathVariable("questionId")Long questionId,
            @Valid @RequestBody UpdateQuestionDto.Request request,
            BindingResult errors){
        return customerCenterService.updateQuestion(
                questionId, request
        );
    }

}

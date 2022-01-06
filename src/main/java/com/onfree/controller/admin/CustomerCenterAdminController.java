package com.onfree.controller.admin;

import com.onfree.core.dto.notice.CreateNoticeDto;
import com.onfree.core.dto.notice.UpdateNoticeDto;
import com.onfree.core.dto.question.CreateQuestionDto;
import com.onfree.core.dto.question.UpdateQuestionDto;
import com.onfree.core.service.CustomerCenterService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api")
@Api(value = "고객센터 ADMIN 유저 컨트롤러")
public class CustomerCenterAdminController {
    private final CustomerCenterService customerCenterService;

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/notices")
    public CreateNoticeDto.Response createNotice(
            @Valid @RequestBody CreateNoticeDto.Request request,
            BindingResult errors){
        return customerCenterService.createNotice(
                request
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/notices/{noticeId}")
    public UpdateNoticeDto.Response updateNotice(
            @PathVariable("noticeId")Long noticeId,
            @Valid @RequestBody UpdateNoticeDto.Request request,
            BindingResult errors){
        return customerCenterService.updateNotice(
                noticeId, request
        );
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/questions")
    public CreateQuestionDto.Response createQuestion(
            @Valid @RequestBody CreateQuestionDto.Request request,
            BindingResult errors){
        return customerCenterService.createQuestion(
                request
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/questions/{questionId}")
    public UpdateQuestionDto.Response updateQuestion(
            @PathVariable("questionId")Long questionId,
            @Valid @RequestBody UpdateQuestionDto.Request request,
            BindingResult errors){
        return customerCenterService.updateQuestion(
                questionId, request
        );
    }

}

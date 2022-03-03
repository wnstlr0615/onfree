package com.onfree.controller.admin;

import com.onfree.common.model.SimpleResponse;
import com.onfree.controller.CustomerCenterController;
import com.onfree.controller.SwaggerController;
import com.onfree.core.dto.notice.CreateNoticeDto;
import com.onfree.core.dto.notice.UpdateNoticeDto;
import com.onfree.core.dto.question.CreateQuestionDto;
import com.onfree.core.dto.question.UpdateQuestionDto;
import com.onfree.core.service.CustomerCenterService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/admin/api/v1", consumes = MediaType.APPLICATION_JSON_VALUE)
public class CustomerCenterAdminController {
    private final CustomerCenterService customerCenterService;

    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "공지 생성", notes = "ADMIN 사용자가 공지를 추가하는 요청")
    @PreAuthorize("hasRole('ADMIN') and isAuthenticated()")
    @PostMapping("/notices")
    public CreateNoticeDto.Response noticeAdd(
            @Valid @RequestBody CreateNoticeDto.Request request,
            BindingResult errors
    ){
        final CreateNoticeDto.Response response = customerCenterService.createNotice(
                request
        );
        //링크 추가
        response.add(
                linkTo(CustomerCenterAdminController.class).slash("notices").withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/customer-center-admin-controller/noticeAddUsingPOST").withRel("profile")
        );

        return response;
    }
    @ApiOperation(value = "공지 수정", notes = "ADMIN 사용자가 공지를 수정하는 요청")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/notices/{noticeId}")
    public SimpleResponse noticeModify(
            @ApiParam(value = "해당 공지 ID", example = "1")
            @PathVariable("noticeId")Long noticeId,
            @Valid @RequestBody UpdateNoticeDto.Request request,
            BindingResult errors
    ){
        customerCenterService.updateNotice(noticeId, request);

        SimpleResponse response = SimpleResponse.success("공지가 성공적으로 수정되었습니다.");

        // 링크 추가
        response.add(
                linkTo(methodOn(CustomerCenterController.class).noticeDetails(noticeId)).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/customer-center-admin-controller/noticeModifyUsingPUT").withRel("profile")
        );

        return response;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "질문 생성", notes = "ADMIN 사용자가 질문을 추가하는 요청")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/questions")
    public CreateQuestionDto.Response questionAdd(
            @Valid @RequestBody CreateQuestionDto.Request request,
            BindingResult errors){
        CreateQuestionDto.Response response = customerCenterService.createQuestion(request);

        //링크 추가
        response.add(
                linkTo(CustomerCenterAdminController.class).slash("question").withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/customer-center-admin-controller/questionAddUsingPOST").withRel("profile")
        );

        return response;
    }
//
    @ApiOperation(value = "질문 수정", notes = "ADMIN 사용자가 질문을 수정하는 요청")
    @PreAuthorize("hasRole('ADMIN') ")
    @PutMapping("/questions/{questionId}")
    public SimpleResponse questionModify(
            @ApiParam(value = "해당 질문 ID", example = "1")
            @PathVariable("questionId")Long questionId,
            @Valid @RequestBody UpdateQuestionDto.Request request,
            BindingResult errors
    ){
         customerCenterService.updateQuestion(questionId, request);

        SimpleResponse response = SimpleResponse.success("질문이 성공적으로 수정되었습니다.");

        // 링크 추가
        response.add(
                linkTo(methodOn(CustomerCenterController.class).questionDetails(questionId)).withSelfRel(),
               Link.of(linkTo(SwaggerController.class) + "/#/customer-center-admin-controller/questionModifyUsingPUT").withRel("profile")
        );
        return response;
    }

}

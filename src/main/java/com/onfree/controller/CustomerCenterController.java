package com.onfree.controller;

import com.onfree.core.dto.notice.NoticeDetailDto;
import com.onfree.core.dto.notice.NoticeSimpleDto;
import com.onfree.core.dto.question.QuestionDetailDto;
import com.onfree.core.dto.question.QuestionSimpleDto;
import com.onfree.core.service.CustomerCenterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1" )
public class CustomerCenterController {
    private final CustomerCenterService customerCenterService;

    @GetMapping("/notices")
    @ApiOperation(value = "공지사항 전체 조회", notes = "공지사항을 조회하는 API")
    public PagedModel<EntityModel<NoticeSimpleDto>> noticeList(
            @ApiParam(value = "페이지 번호", defaultValue = "0", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @ApiParam(value = "보여질 페이지 사이즈", defaultValue = "10", example = "10")
            @RequestParam(defaultValue = "10") int size,
            PagedResourcesAssembler<NoticeSimpleDto> assembler
        ){

        PagedModel<EntityModel<NoticeSimpleDto>> pagedModel = assembler.toModel(
                customerCenterService.getNoticeSimpleDtoList(
                        PageRequest.of(page, size)
                )
        );
        // profile 추가
        pagedModel.add(
                Link.of(linkTo(SwaggerController.class) + "/#/customer-center-controller/noticeListUsingGET").withRel("profile")
        );
        //개별 접근 링크 추가
        pagedModel.forEach(entityModel ->
                entityModel.add(
                        linkTo(methodOn(CustomerCenterController.class)
                        .noticeDetails(entityModel.getContent().getNoticeId()))
                        .withRel("notice-details")
                )
        );
        return pagedModel;
    }

    @GetMapping("/notices/{noticeId}")
    @ApiOperation(value = "공지사항 상세 조회", notes = "공지사항을 상세조회하는 API")
    public NoticeDetailDto noticeDetails(
            @ApiParam(value = "공지글 ID", defaultValue = "1L", example = "1")
            @PathVariable("noticeId") Long noticeId
    ){
        final NoticeDetailDto response = customerCenterService.getNoticeDetailDto(noticeId);

        response.add(
                linkTo(methodOn(CustomerCenterController.class).noticeDetails(noticeId)).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/customer-center-controller/noticeDetailsUsingGET").withRel("profile")
        );
        return response;
    }

    @GetMapping("/questions")
    @ApiOperation(value = "자주 하는 질문 전체 조회", notes = "자주하는 질문을 조회하는 API")
    public PagedModel<EntityModel<QuestionSimpleDto>> questionList(
            @ApiParam(value = "페이지 번호", defaultValue = "0", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @ApiParam(value = "보여질 페이지 사이즈", defaultValue = "10", example = "10")
            @RequestParam(defaultValue = "10") int size,
            PagedResourcesAssembler<QuestionSimpleDto> assembler
    ){

        PagedModel<EntityModel<QuestionSimpleDto>> pagedModel = assembler.toModel(
                customerCenterService.getQuestionSimpleDtoList(
                        PageRequest.of(page, size)
                )
        );
        // profile 추가
        pagedModel.add(
                Link.of(linkTo(SwaggerController.class) + "/#/customer-center-controller/questionListUsingGET").withRel("profile")
        );
        //개별 접근 링크 추가
        pagedModel.forEach(entityModel ->
        entityModel.add(
                    linkTo(methodOn(CustomerCenterController.class)
                        .questionDetails(entityModel.getContent().getQuestionId()))
                        .withRel("question-details")
                )
        );
        return pagedModel;
    }

    @GetMapping("/questions/{questionId}")
    @ApiOperation(value = "자주하는 질문 상세 조회", notes = "자주하는 질문을 상세조회하는 API")
    public QuestionDetailDto questionDetails(
            @ApiParam(value = "자주하는 질문 글 ID", defaultValue = "1L", example = "1") @PathVariable("questionId") Long questionId
    ){
        final QuestionDetailDto response = customerCenterService.getQuestionDetailDto(questionId);

        response.add(
                linkTo(methodOn(CustomerCenterController.class).noticeDetails(questionId)).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/customer-center-controller/questionDetailsUsingGET").withRel("profile")
        );
        return response;
    }

}

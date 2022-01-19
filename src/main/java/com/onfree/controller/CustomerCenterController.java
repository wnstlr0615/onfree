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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@Api(tags = "고객센터 컨트롤러", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api" )
public class CustomerCenterController {
    private final CustomerCenterService customerCenterService;

    @GetMapping("/notices")
    @ApiOperation(value = "공지사항 전체 조회", notes = "공지사항을 조회하는 API")
    public Page<NoticeSimpleDto> getNoticeSimpleDtoList(
            @ApiParam(value = "페이지 번호", defaultValue = "0", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @ApiParam(value = "보여질 페이지 사이즈", defaultValue = "10", example = "10")
            @RequestParam(defaultValue = "10") int size
            ){

        return customerCenterService.getNoticeSimpleDtoList(
                PageRequest.of(page, size)
        );
    }

    @GetMapping("/notices/{noticeId}")
    @ApiOperation(value = "공지사항 상세 조회", notes = "공지사항을 상세조회하는 API")
    public NoticeDetailDto getNoticeDetailDto(
            @ApiParam(value = "공지글 ID", defaultValue = "1L", example = "1") @PathVariable("noticeId") Long noticeId
    ){
        return customerCenterService.getNoticeDetailDto(noticeId);
    }

    @GetMapping("/questions")
    @ApiOperation(value = "자주 하는 질문 전체 조회", notes = "자주하는 질문을 조회하는 API")
    public Page<QuestionSimpleDto> getQuestionDtoList(
            @ApiParam(value = "페이지 번호", defaultValue = "0", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @ApiParam(value = "보여질 페이지 사이즈", defaultValue = "10", example = "10")
            @RequestParam(defaultValue = "10") int size
    ){

        return customerCenterService.getQuestionSimpleDtoList(
                PageRequest.of(page, size)
        );
    }

    @GetMapping("/questions/{questionId}")
    @ApiOperation(value = "자주하는 질문 상세 조회", notes = "자주하는 질문을 상세조회하는 API")
    public QuestionDetailDto getQuestionDetailDto(
            @ApiParam(value = "자주하는 질문 글 ID", defaultValue = "1L", example = "1") @PathVariable("questionId") Long questionId
    ){
        return customerCenterService.getQuestionDetailDto(questionId);
    }

}

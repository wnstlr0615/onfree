package com.onfree.controller;

import com.onfree.core.dto.notice.NoticeDetailDto;
import com.onfree.core.dto.notice.NoticeSimpleDto;
import com.onfree.core.dto.question.QuestionDetailDto;
import com.onfree.core.dto.question.QuestionSimpleDto;
import com.onfree.core.service.CustomerCenterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "고객센터 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api" ,consumes = MediaType.APPLICATION_JSON_VALUE)
public class CustomerCenterController {
    private final CustomerCenterService customerCenterService;

    @GetMapping("/notices")
    @ApiOperation(value = "공지사항 전체 조회", notes = "공지사항을 조회하는 API")
    public Page<NoticeSimpleDto> getNoticeSimpleDtoList(@PageableDefault Pageable pageable){
        return customerCenterService.getNoticeSimpleDtoList(pageable);
    }

    @GetMapping("/notices/{noticeId}")
    @ApiOperation(value = "공지사항 상세 조회", notes = "공지사항을 상세조회하는 API")
    public NoticeDetailDto getNoticeDetailDto(@PathVariable("noticeId") Long noticeId){
        return customerCenterService.getNoticeDetailDto(noticeId);
    }

    @GetMapping("/questions")
    @ApiOperation(value = "자주 하는 질문 전체 조회", notes = "자주하는 질문을 조회하는 API")
    public Page<QuestionSimpleDto> getQuestionDtoList(@PageableDefault Pageable pageable){
        return customerCenterService.getQuestionSimpleDtoList(pageable);
    }

    @GetMapping("/questions/{questionId}")
    @ApiOperation(value = "자주하는 질문 상세 조회", notes = "자주하는 질문을 상세조회하는 API")
    public QuestionDetailDto getQuestionDetailDto(@PathVariable("questionId") Long questionId){
        return customerCenterService.getQuestionDetailDto(questionId);
    }

}

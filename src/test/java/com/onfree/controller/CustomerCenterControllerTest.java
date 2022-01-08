package com.onfree.controller;

import com.onfree.common.WebMvcBaseTest;
import com.onfree.core.dto.notice.NoticeDetailDto;
import com.onfree.core.dto.notice.NoticeSimpleDto;
import com.onfree.core.dto.question.QuestionDetailDto;
import com.onfree.core.dto.question.QuestionSimpleDto;
import com.onfree.core.service.CustomerCenterService;
import com.onfree.error.code.CustomerCenterErrorCode;
import com.onfree.error.exception.CustomerCenterException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CustomerCenterController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ActiveProfiles("test")
class CustomerCenterControllerTest extends WebMvcBaseTest {

    @MockBean
    CustomerCenterService customerCenterService;

    @Test
    @WithAnonymousUser
    @DisplayName("[성공][GET] 공지사항 전체 조회")
    public void givenPageable_WhenSuccessGetNoticeList_thenNoticeDtoList() throws Exception{
        //given
        when(customerCenterService.getNoticeSimpleDtoList(any()))
                .thenReturn(
                        getPageNoticesSimpleList()
                );
        //when// then
        mvc.perform(get("/api/notices")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isNotEmpty())
            .andExpect(jsonPath("$.content[0].noticeId").value(1L))
            .andExpect(jsonPath("$.content[0].title").value("title1"))
            .andExpect(jsonPath("$.content[0].top").value(false))
            .andExpect(jsonPath("$.content[0].view").value(0))
            .andExpect(jsonPath("$.content[0].createdDate").isNotEmpty())
            .andExpect(jsonPath("$.content[0].createdBy").value("운영자"))
            .andExpect(jsonPath("$.totalElements").value(5))

            ;
    }


    private Page<NoticeSimpleDto> getPageNoticesSimpleList() {
        return new PageImpl<>(
                getNoticeSimpleList()
        );
    }

    private List<NoticeSimpleDto> getNoticeSimpleList() {
        List<NoticeSimpleDto> noticeSimpleDtos=new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            noticeSimpleDtos.add(
                    createNoticeSimpleDto(i, "title"+i)
            );
        }
        return noticeSimpleDtos;
    }

    private NoticeSimpleDto createNoticeSimpleDto(long noticeId, String title) {
        return NoticeSimpleDto.builder()
                .noticeId(noticeId)
                .title(title)
                .top(false)
                .view(0)
                .createdDate(LocalDateTime.now().toLocalDate())
                .createdBy("운영자")
                .build();
    }

    @Test
    @WithAnonymousUser
    @DisplayName("[실패][GET] 공지사항 전체 조회 - 게시글이 없는 경우")
    public void givenPageAble_WhenGetNoticeSimpleDtoListIsEmpty_thenNoticeIsEmptyError() throws Exception{
        //given
        final CustomerCenterErrorCode errorCode = CustomerCenterErrorCode.NOTICES_IS_EMPTY;
        when(customerCenterService.getNoticeSimpleDtoList(any()))
                .thenThrow(new CustomerCenterException(errorCode));
        //when// then
        mvc.perform(get("/api/notices")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("errorMessage").value(errorCode.getDescription()))
        ;
    }

    @Test
    @WithAnonymousUser
    @DisplayName("[성공][GET] 공지사항 상세 조회")
    public void giveNoticeId_WhenSuccessGetNoticeDetail_thenNoticeDetail() throws Exception{
        //given
        final long noticeId = 1L;
        when(customerCenterService.getNoticeDetailDto(
                eq(noticeId))
        ).thenReturn(
                        getNoticeDetailDto()
        );
        //when//then
        mvc.perform(get("/api/notices/{noticeId}", noticeId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.noticeId").value(1L))
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"))
                .andExpect(jsonPath("$.view").value(1))
                .andExpect(jsonPath("$.createdDate").isNotEmpty())
                .andExpect(jsonPath("$.createdBy").value("운영자"))
        ;
    }

    private NoticeDetailDto getNoticeDetailDto() {
        return NoticeDetailDto.builder()
                .noticeId(1L)
                .title("제목")
                .content("내용")
                .view(1)
                .createdDate(LocalDateTime.now().toLocalDate())
                .createdBy("운영자")
                .build();
    }

    @Test
    @WithAnonymousUser
    @DisplayName("[실패][GET] 공지사항 상세 조회 - 잘못된 noticeId 입력")
    public void giveWrongNoticeId_WhenGetNoticeDetail_thenNotFoundNoticeError() throws Exception{
        //given
        final long noticeId = 100000L;
        final CustomerCenterErrorCode errorCode = CustomerCenterErrorCode.NOT_FOUND_NOTICE;
        when(customerCenterService.getNoticeDetailDto(
                eq(noticeId))
        ).thenThrow(new CustomerCenterException(errorCode));

        //when//then
        mvc.perform(get("/api/notices/{noticeId}", noticeId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("errorMessage").value(errorCode.getDescription()))
        ;
    }

    @Test
    @WithAnonymousUser
    @DisplayName("[성공][GET] 자주하는 질문 전체 조회")
    public void givenNotting_WhenSuccessGetQuestionList_thenNoticeDtoList() throws Exception{
        //given
        when(customerCenterService.getQuestionSimpleDtoList(any()))
                .thenReturn(
                        getPageQuestionSimpleList()
                );

        //when//then
        mvc.perform(get("/api/questions")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].questionId").value(1L))
                .andExpect(jsonPath("$.content[0].title").value("title1"))
                .andExpect(jsonPath("$.content[0].top").value(false))
                .andExpect(jsonPath("$.content[0].view").value(0))
                .andExpect(jsonPath("$.content[0].createdDate").isNotEmpty())
                .andExpect(jsonPath("$.content[0].createdBy").value("운영자"))
                .andExpect(jsonPath("$.totalElements").value(5))
        ;
    }

    private Page<QuestionSimpleDto> getPageQuestionSimpleList() {
        return new PageImpl<>(
                getQuestionSimpleList()
        );
    }

    private List<QuestionSimpleDto> getQuestionSimpleList() {
        List<QuestionSimpleDto> questionSimpleDtos=new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            questionSimpleDtos.add(
                    createQuestionSimpleDto(i, "title"+i)
            );
        }
        return questionSimpleDtos;
    }

    private QuestionSimpleDto createQuestionSimpleDto(long questionId, String title) {
        return QuestionSimpleDto.builder()
                .questionId(questionId)
                .title(title)
                .top(false)
                .view(0)
                .createdDate(LocalDateTime.now().toLocalDate())
                .createdBy("운영자")
                .build();
    }

    @Test
    @WithAnonymousUser
    @DisplayName("[실패][GET] 공지사항 전체 조회 - 게시글이 없는 경우")
    public void givenPageAble_WhenGetQuestionSimpleDtoListIsEmpty_thenQuestionIsEmptyError() throws Exception{
        //given
        final CustomerCenterErrorCode errorCode = CustomerCenterErrorCode.QUESTION_IS_EMPTY;
        when(customerCenterService.getQuestionSimpleDtoList(any()))
                .thenThrow(new CustomerCenterException(errorCode));

        //when// then
        mvc.perform(get("/api/questions")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("errorMessage").value(errorCode.getDescription()))
        ;
    }

    @Test
    @WithAnonymousUser
    @DisplayName("[성공][GET] 자주하는 질문 상세조회")
    public void giveQuestionId_WhenSuccessGetQuestionDetail_thenQuestionDetail() throws Exception{
        //given
        final long questionId = 1L;
        when(customerCenterService.getQuestionDetailDto(questionId))
                .thenReturn(
                        getQuestionDetailDto()
                );

        //when//then
        mvc.perform(get("/api/questions/{questionId}", questionId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionId").value(1L))
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"))
                .andExpect(jsonPath("$.view").value(1))
                .andExpect(jsonPath("$.createdDate").isNotEmpty())
                .andExpect(jsonPath("$.createdBy").value("운영자"))

        ;
    }

    private QuestionDetailDto getQuestionDetailDto() {
        return QuestionDetailDto.builder()
                .questionId(1L)
                .title("제목")
                .content("내용")
                .view(1)
                .createdDate(LocalDateTime.now().toLocalDate())
                .createdBy("운영자")
                .build();
    }

    @Test
    @WithAnonymousUser
    @DisplayName("[실패][GET] 공지사항 상세 조회 - 잘못된 questionId 입력")
    public void giveWrongQuestionId_WhenGetQuestionDetail_thenNotFoundQuestion() throws Exception{
        //given
        final long questionId = 100000L;
        final CustomerCenterErrorCode errorCode = CustomerCenterErrorCode.NOT_FOUND_QUESTION;
        when(customerCenterService.getQuestionDetailDto(
                eq(questionId))
        ).thenThrow(new CustomerCenterException(errorCode));

        //when//then
        mvc.perform(get("/api/questions/{questionId}", questionId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("errorMessage").value(errorCode.getDescription()))
        ;
    }

}
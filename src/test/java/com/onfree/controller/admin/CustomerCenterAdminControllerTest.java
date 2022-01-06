package com.onfree.controller.admin;

import com.onfree.anotation.WithAdminUser;
import com.onfree.common.WebMvcBaseTest;
import com.onfree.core.dto.notice.CreateNoticeDto;
import com.onfree.core.dto.notice.UpdateNoticeDto;
import com.onfree.core.dto.question.CreateQuestionDto;
import com.onfree.core.dto.question.UpdateQuestionDto;
import com.onfree.core.service.CustomerCenterService;
import com.onfree.error.code.CustomerCenterErrorCode;
import com.onfree.error.exception.CustomerCenterException;
import com.onfree.error.exception.GlobalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CustomerCenterAdminController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ActiveProfiles("test")
class CustomerCenterAdminControllerTest extends WebMvcBaseTest {
    @MockBean
    CustomerCenterService customerCenterService;

    @Test
    @DisplayName("[POST][성공] 공지 글 생성")
    @WithAdminUser
    public void givenCreateNoticeDtoReq_whenCreateNotice_thenCreateNoticeDtoRes() throws Exception{
        //given
        when(customerCenterService.createNotice(any(CreateNoticeDto.Request.class)))
                .thenReturn(
                        getCreateNoticeDtoResponse()
                );
        //when //then
        mvc.perform(post("/admin/api/notices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                getCreateNoticeDtoRequest()
                        )
                )
        )
            .andDo(print())
            .andExpect(status().isCreated())
                .andExpect(jsonPath("$.noticeId").value(1))
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"))
                .andExpect(jsonPath("$.top").value(true))
                .andExpect(jsonPath("$.disabled").value(false))
            ;
    }

    private CreateNoticeDto.Request getCreateNoticeDtoRequest() {
        return CreateNoticeDto.Request.builder()
                .title("제목")
                .content("내용")
                .top(true)
                .build();
    }

    private CreateNoticeDto.Response getCreateNoticeDtoResponse() {
        return CreateNoticeDto.Response.builder()
                .noticeId(1L)
                .title("제목")
                .content("내용")
                .top(true)
                .disabled(false)
                .build();
    }

    @Test
    @DisplayName("[PUT][성공] 공지 글 수정")
    @WithAdminUser
    public void givenUpdateNoticeDtoReq_whenUpdateNotice_thenUpdateNoticeDtoRes() throws Exception{
        //given
        final long noticeId = 1L;

        when(customerCenterService.updateNotice(eq(noticeId),  any(UpdateNoticeDto.Request.class)))
                .thenReturn(getUpdateNoticeDtoResponse());
        //when

        //then
        mvc.perform(put("/admin/api/notices/{noticeId}", noticeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsBytes(
                                getUpdateNoticeDtoRequest()
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.noticeId").value(1L))
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"))
                .andExpect(jsonPath("$.top").value(true))
                .andExpect(jsonPath("$.disabled").value(false))
        ;
    }

    private UpdateNoticeDto.Request getUpdateNoticeDtoRequest() {
        return UpdateNoticeDto.Request.builder()
                .title("제목")
                .content("내용")
                .top(true)
                .disabled(false)
                .build();
    }

    private UpdateNoticeDto.Response getUpdateNoticeDtoResponse() {
        return UpdateNoticeDto.Response.builder()
                .noticeId(1L)
                .title("제목")
                .content("내용")
                .top(true)
                .disabled(false)
                .build();
    }
    @Test
    @DisplayName("[PUT][실패] 공지 글 수정 - 존재 하지 않는 공지 ID")
    @WithAdminUser
    public void givenUpdateNoticeDtoReq_whenUpdateNoticeButNotFoundNoticeId_thenNotFoundNoticeError() throws Exception{
        //given
        final long noticeId = 1L;
        final CustomerCenterErrorCode errorCode = CustomerCenterErrorCode.NOT_FOUND_NOTICE;
        when(customerCenterService.updateNotice(eq(noticeId),  any(UpdateNoticeDto.Request.class)))
                .thenThrow(new CustomerCenterException(errorCode));
        //when

        //then
        mvc.perform(put("/admin/api/notices/{noticeId}", noticeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsBytes(
                                getUpdateNoticeDtoRequest()
                        )
                )
        )
                .andDo(print())
                .andExpect(status().is(errorCode.getStatus()))
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
    }

    @Test
    @DisplayName("[POST][성공] 자주하는 질문 생성")
    @WithAdminUser
    public void givenCreateQuestionDtoReq_whenCreateQuestion_thenCreateQuestionDtoRes() throws Exception{
        //given
        when(customerCenterService.createQuestion(any()))
                .thenReturn(
                        getCreateQuestionDtoResponse()
                );
        //when
        //then
        mvc.perform(post("/admin/api/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                getCreateQuestionDtoRequest()
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.questionId").value(1))
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"))
                .andExpect(jsonPath("$.top").value(true))
                .andExpect(jsonPath("$.disabled").value(false))
        ;

    }

    private CreateQuestionDto.Request getCreateQuestionDtoRequest() {
        return CreateQuestionDto.Request.builder()
                .title("제목")
                .content("내용")
                .top(true)
                .build();
    }

    private CreateQuestionDto.Response getCreateQuestionDtoResponse() {
        return CreateQuestionDto.Response.builder()
                .questionId(1L)
                .title("제목")
                .content("내용")
                .top(true)
                .disabled(false)
                .build();
    }

    @Test
    @DisplayName("[PUT][성공] 자주하는 질문 수정")
    @WithAdminUser
    public void givenUpdateQuestionDtoReq_whenUpdateQuestion_thenUpdateQuestionDtoRes() throws Exception{
        //given
        final long questionId = 1L;
        when(customerCenterService.updateQuestion(eq(questionId), any(UpdateQuestionDto.Request.class)))
                .thenReturn(
                        getUpdateQuestionDtoResponse()
                );
        //when

        //then
        mvc.perform(put("/admin/api/questions/{questionId}", questionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                getUpdateQuestionDtoResponse()
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questionId").value(1L))
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"))
                .andExpect(jsonPath("$.top").value(true))
                .andExpect(jsonPath("$.disabled").value(false))
        ;
    }

    private UpdateQuestionDto.Request getUpdateQuestionDtoRequest() {
        return UpdateQuestionDto.Request.builder()
                .title("제목")
                .content("내용")
                .top(true)
                .disabled(false)
                .build();
    }

    private UpdateQuestionDto.Response getUpdateQuestionDtoResponse() {
        return UpdateQuestionDto.Response.builder()
                .questionId(1L)
                .title("제목")
                .content("내용")
                .top(true)
                .disabled(false)
                .build();
    }

    @Test
    @DisplayName("[PUT][실패] 자주하는 질문 수정 - 존재하지 않는 질문 ID")
    @WithAdminUser
    public void givenUpdateQuestionDtoReq_whenUpdateQuestionButNotFoundQuestionId_thenNotFoundQuestionError() throws Exception{
        //given
        final long questionId = 1L;
        final CustomerCenterErrorCode errorCode = CustomerCenterErrorCode.NOT_FOUND_QUESTION;
        when(customerCenterService.updateQuestion(eq(questionId), any(UpdateQuestionDto.Request.class)))
                .thenThrow(new CustomerCenterException(errorCode));
        //when

        //then
        mvc.perform(put("/admin/api/questions/{questionId}", questionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                getUpdateQuestionDtoResponse()
                        )
                )
        )
                .andDo(print())
                .andExpect(status().is(errorCode.getStatus()))
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
    }

}
package com.onfree.controller.admin;

import com.onfree.anotation.WithAdminUser;
import com.onfree.common.ControllerBaseTest;
import com.onfree.core.dto.notice.CreateNoticeDto;
import com.onfree.core.dto.notice.UpdateNoticeDto;
import com.onfree.core.dto.question.CreateQuestionDto;
import com.onfree.core.dto.question.UpdateQuestionDto;
import com.onfree.core.service.CustomerCenterService;
import com.onfree.common.error.code.CustomerCenterErrorCode;
import com.onfree.common.error.exception.CustomerCenterException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CustomerCenterAdminController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ActiveProfiles("test")
class CustomerCenterAdminControllerTest extends ControllerBaseTest {
    @MockBean
    CustomerCenterService customerCenterService;

    @Test
    @DisplayName("[성공][POST] 공지 글 생성")
    @WithAdminUser
    public void givenCreateNoticeDtoReq_whenCreateNotice_thenCreateNoticeDtoRes() throws Exception{
        //given
        when(customerCenterService.createNotice(any(CreateNoticeDto.Request.class)))
                .thenReturn(
                        givenCreateNoticeDtoResponse()
                );
        //when //then
        mvc.perform(post("/admin/api/v1/notices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                givenCreateNoticeDtoRequest()
                        )
                )
        )
            .andDo(print())
            .andExpect(status().isCreated())
                .andExpect(jsonPath("$.noticeId").value(1))
                .andExpect(jsonPath("$.title").value("안녕하세요 제목입니다."))
                .andExpect(jsonPath("$.content").value("내용"))
                .andExpect(jsonPath("$.top").value(true))
                .andExpect(jsonPath("$.disabled").value(false))
                .andExpect(jsonPath("$._links.self.href").isNotEmpty())
                .andExpect(jsonPath("$._links.profile.href").isNotEmpty())
            ;
        verify(customerCenterService).createNotice(any());
    }

    private CreateNoticeDto.Request givenCreateNoticeDtoRequest() {
        return CreateNoticeDto.Request.builder()
                .title("안녕하세요 제목입니다.")
                .content("내용")
                .top(true)
                .build();
    }

    private CreateNoticeDto.Response givenCreateNoticeDtoResponse() {
        return CreateNoticeDto.Response.builder()
                .noticeId(1L)
                .title("안녕하세요 제목입니다.")
                .content("내용")
                .top(true)
                .disabled(false)
                .build();
    }

    @Test
    @DisplayName("[성공][PUT] 공지 글 수정")
    @WithAdminUser
    public void givenUpdateNoticeDtoReq_whenUpdateNotice_thenUpdateNoticeDtoRes() throws Exception{
        //given
        final long noticeId = 1L;

        when(customerCenterService.updateNotice(eq(noticeId),  any(UpdateNoticeDto.Request.class)))
                .thenReturn(givenUpdateNoticeDtoResponse());
        //when

        //then
        mvc.perform(put("/admin/api/v1/notices/{noticeId}", noticeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsBytes(
                                givenUpdateNoticeDtoRequest()
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.message").value("공지가 성공적으로 수정되었습니다."))
                .andExpect(jsonPath("$._links.self.href").isNotEmpty())
                .andExpect(jsonPath("$._links.profile.href").isNotEmpty())
        ;
        verify(customerCenterService).updateNotice(eq(noticeId), any());
    }

    private UpdateNoticeDto.Request givenUpdateNoticeDtoRequest() {
        return UpdateNoticeDto.Request.builder()
                .title("안녕하세요 공지 제목입니다.")
                .content("내용")
                .top(true)
                .disabled(false)
                .build();
    }

    private UpdateNoticeDto.Response givenUpdateNoticeDtoResponse() {
        return UpdateNoticeDto.Response.builder()
                .noticeId(1L)
                .title("안녕하세요 공지 제목입니다.")
                .content("내용")
                .top(true)
                .disabled(false)
                .build();
    }
    @Test
    @DisplayName("[실패][PUT] 공지 글 수정 - 존재 하지 않는 공지 ID")
    @WithAdminUser
    public void givenUpdateNoticeDtoReq_whenUpdateNoticeButNotFoundNoticeId_thenNotFoundNoticeError() throws Exception{
        //given
        final long noticeId = 1L;
        final CustomerCenterErrorCode errorCode = CustomerCenterErrorCode.NOT_FOUND_NOTICE;
        when(customerCenterService.updateNotice(eq(noticeId),  any(UpdateNoticeDto.Request.class)))
                .thenThrow(new CustomerCenterException(errorCode));
        //when

        //then
        mvc.perform(put("/admin/api/v1/notices/{noticeId}", noticeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsBytes(
                                givenUpdateNoticeDtoRequest()
                        )
                )
        )
                .andDo(print())
                .andExpect(status().is(errorCode.getStatus()))
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(customerCenterService).updateNotice(eq(noticeId), any());

    }

    @Test
    @DisplayName("[성공][POST] 자주하는 질문 생성")
    @WithAdminUser
    public void givenCreateQuestionDtoReq_whenCreateQuestion_thenCreateQuestionDtoRes() throws Exception{
        //given
        when(customerCenterService.createQuestion(any()))
                .thenReturn(
                        givenCreateQuestionDtoResponse()
                );
        //when
        //then
        mvc.perform(post("/admin/api/v1/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                givenCreateQuestionDtoRequest()
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.questionId").value(1))
                .andExpect(jsonPath("$.title").value("안녕하세요 질문하기 제목입니다."))
                .andExpect(jsonPath("$.content").value("내용"))
                .andExpect(jsonPath("$.top").value(true))
                .andExpect(jsonPath("$.disabled").value(false))
                .andExpect(jsonPath("$._links.self.href").isNotEmpty())
                .andExpect(jsonPath("$._links.profile.href").isNotEmpty())
        ;
        verify(customerCenterService).createQuestion(any());
    }

    private CreateQuestionDto.Request givenCreateQuestionDtoRequest() {
        return CreateQuestionDto.Request.builder()
                .title("안녕하세요 질문하기 제목입니다.")
                .content("내용")
                .top(true)
                .build();
    }

    private CreateQuestionDto.Response givenCreateQuestionDtoResponse() {
        return CreateQuestionDto.Response.builder()
                .questionId(1L)
                .title("안녕하세요 질문하기 제목입니다.")
                .content("내용")
                .top(true)
                .disabled(false)
                .build();
    }

    @Test
    @DisplayName("[성공][PUT] 자주하는 질문 수정")
    @WithAdminUser
    public void givenUpdateQuestionDtoReq_whenUpdateQuestion_thenUpdateQuestionDtoRes() throws Exception{
        //given
        final long questionId = 1L;
        when(customerCenterService.updateQuestion(eq(questionId), any(UpdateQuestionDto.Request.class)))
                .thenReturn(
                        givenUpdateQuestionDtoResponse()
                );
        //when

        //then
        mvc.perform(put("/admin/api/v1/questions/{questionId}", questionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                givenUpdateQuestionDtoRequest()
                        )
                )
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.message").value("질문이 성공적으로 수정되었습니다."))
                .andExpect(jsonPath("$._links.self.href").isNotEmpty())
                .andExpect(jsonPath("$._links.profile.href").isNotEmpty())
        ;
        verify(customerCenterService).updateQuestion(eq(questionId), any());

    }

    private UpdateQuestionDto.Request givenUpdateQuestionDtoRequest() {
        return UpdateQuestionDto.Request.builder()
                .title("수정된 질문 제목")
                .content("내용")
                .top(true)
                .disabled(false)
                .build();
    }

    private UpdateQuestionDto.Response givenUpdateQuestionDtoResponse() {
        return UpdateQuestionDto.Response.builder()
                .questionId(1L)
                .title("수정된 질문 제목")
                .content("내용")
                .top(true)
                .disabled(false)
                .build();
    }

    @Test
    @DisplayName("[실패][PUT] 자주하는 질문 수정 - 존재하지 않는 질문 ID")
    @WithAdminUser
    public void givenUpdateQuestionDtoReq_whenUpdateQuestionButNotFoundQuestionId_thenNotFoundQuestionError() throws Exception{
        //given
        final long questionId = 1L;
        final CustomerCenterErrorCode errorCode = CustomerCenterErrorCode.NOT_FOUND_QUESTION;
        when(customerCenterService.updateQuestion(eq(questionId), any(UpdateQuestionDto.Request.class)))
                .thenThrow(new CustomerCenterException(errorCode));
        //when

        //then
        mvc.perform(put("/admin/api/v1/questions/{questionId}", questionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        mapper.writeValueAsString(
                                givenUpdateQuestionDtoResponse()
                        )
                )
        )
                .andDo(print())
                .andExpect(status().is(errorCode.getStatus()))
                .andExpect(jsonPath("$.errorCode").value(errorCode.toString()))
                .andExpect(jsonPath("$.errorMessage").value(errorCode.getDescription()))
        ;
        verify(customerCenterService).updateQuestion(eq(questionId), any());
    }

}
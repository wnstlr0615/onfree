package com.onfree.core.service;

import com.onfree.core.dto.notice.CreateNoticeDto;
import com.onfree.core.dto.notice.NoticeDetailDto;
import com.onfree.core.dto.notice.NoticeSimpleDto;
import com.onfree.core.dto.notice.UpdateNoticeDto;
import com.onfree.core.dto.question.CreateQuestionDto;
import com.onfree.core.dto.question.QuestionDetailDto;
import com.onfree.core.dto.question.QuestionSimpleDto;
import com.onfree.core.dto.question.UpdateQuestionDto;
import com.onfree.core.entity.Notice;
import com.onfree.core.entity.Question;
import com.onfree.core.repository.NoticeRepository;
import com.onfree.core.repository.QuestionRepository;
import com.onfree.error.code.CustomerCenterErrorCode;
import com.onfree.error.exception.CustomerCenterException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CustomerCenterServiceTest {
    @Mock
    NoticeRepository noticeRepository;

    @Mock
    QuestionRepository questionRepository;

    @InjectMocks
    CustomerCenterService customerCenterService;

    @Test
    @DisplayName("[성공] 공지사항 목록 조회 - NoticeSimpleDtoList로 반환")
    public void givenPageable_whenGetNoticeSimpleDtoList_thenNoticeSimpleDotList() throws Exception{
        //given
        final PageRequest pageRequest = PageRequest.of(0, 10);
        final List<Notice> noticeList = getNoticeList();
        final PageImpl<Notice> noticePage = new PageImpl<>(
                noticeList
        );
        when(noticeRepository.findAllByDisabledIsFalseOrderByTopDescNoticeIdAsc(
                any(Pageable.class))
        ).thenReturn(noticePage);

        //when
        final Page<NoticeSimpleDto> noticeSimpleDtoPage = customerCenterService.getNoticeSimpleDtoList(pageRequest);
        final List<NoticeSimpleDto> noticeSimpleDtoList = noticeSimpleDtoPage.get().collect(Collectors.toList());
        //then
        assertAll(
                () -> assertThat(noticeSimpleDtoList.size())
                        .isEqualTo(noticeList.size()),
                () -> assertThat(noticeSimpleDtoList.get(0))
                        .hasFieldOrPropertyWithValue("noticeId", noticeList.get(0).getNoticeId())
                        .hasFieldOrPropertyWithValue("title", noticeList.get(0).getTitle())
                        .hasFieldOrPropertyWithValue("view", noticeList.get(0).getView())
                        .hasFieldOrPropertyWithValue("top", noticeList.get(0).isTop())
        );

    }

    private List<Notice> getNoticeList() {
        List<Notice> notices = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            notices.add(createNotice(i, true));
        }
        for (int i = 6; i <= 10; i++) {
            notices.add(createNotice(i, false));
        }
        return notices;
    }

    private Notice createNotice(int n, boolean top) {
        return Notice.builder()
                .noticeId((long) n)
                .title("제목"+n)
                .content("내용"+n)
                .top(top)
                .view(n+10)
                .disabled(false)
                .build();
    }


    @Test
    @DisplayName("[실패] 공지사항 목록 조회 - 공지가 없으므로 NOTICE_IS_EMPTY 에러 발생")
    public void givenPageable_whenGetNoticeSimpleDtoEmptyList_thenNoticeIsEmptyError() throws Exception{
        //given
        final PageRequest pageRequest = PageRequest.of(0, 10);
        final CustomerCenterErrorCode errorCode = CustomerCenterErrorCode.NOTICES_IS_EMPTY;
        when(noticeRepository.findAllByDisabledIsFalseOrderByTopDescNoticeIdAsc(
                any(Pageable.class))
        ).thenThrow(new CustomerCenterException(errorCode));

        //when
        final CustomerCenterException customerCenterException = assertThrows(CustomerCenterException.class,
                () -> customerCenterService.getNoticeSimpleDtoList(pageRequest)
        );

        //then
        assertAll(
                () -> assertThat(customerCenterException.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(customerCenterException.getErrorMessage()).isEqualTo(errorCode.getDescription())
        );
    }

    @Test
    @DisplayName("[성공] 공지사항 상세 조회")
    public void givenNoticeId_whenGetNoticeDetailDto_thenNoticeDetailDto() throws Exception{
        //given
        final long noticeId = 1L;
        final Notice notice = createNotice((int) noticeId);
        when(noticeRepository.findByNoticeIdAndDisabledFalse(noticeId))
                .thenReturn(
                        Optional.ofNullable(
                                notice
                        )
                );
        //when
        final NoticeDetailDto noticeDetailDto = customerCenterService.getNoticeDetailDto(noticeId);

        //then
        assertAll(
                () -> assertThat(noticeDetailDto.getNoticeId()).isEqualTo(notice.getNoticeId()),
                () -> assertThat(noticeDetailDto.getTitle()).isEqualTo(notice.getTitle()),
                () -> assertThat(noticeDetailDto.getView()).isEqualTo(notice.getView()),
                () -> assertThat(noticeDetailDto.getCreatedBy()).isNotNull(),
                () -> assertThat(noticeDetailDto.getCreatedDate()).isNotNull()
        );
    }

    private Notice createNotice(int n) {
        return createNotice(n, false);
    }

    @Test
    @DisplayName("[실패] 공지사항 상세 조회 - 해당 공지 ID가 존재하지 않아 NOT_FOUND_NOTICE 에러 발생")
    public void givenNoticeId_whenGetNoticeDetailDto_thenNotFoundNoticeError() throws Exception{
        //given
        final long noticeId = 1L;
        final CustomerCenterErrorCode errorCode = CustomerCenterErrorCode.NOT_FOUND_NOTICE;

        when(noticeRepository.findByNoticeIdAndDisabledFalse(noticeId)
        ).thenThrow(new CustomerCenterException(errorCode));

        //when
        final CustomerCenterException customerCenterException = assertThrows(CustomerCenterException.class,
                () -> customerCenterService.getNoticeDetailDto(noticeId)
        );

        //then
        assertAll(
                () -> assertThat(customerCenterException.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(customerCenterException.getErrorMessage()).isEqualTo(errorCode.getDescription())
        );
    }

    @Test
    @DisplayName("[성공] 자주하는 질문 목록 조회 - QuestionSimpleDtoList 반환")
    public void givenPageable_whenGetQuestionSimpleDtoList_thenQuestionSimpleDotList() throws Exception{
        //given
        final PageRequest pageRequest = PageRequest.of(0, 10);
        final List<Question> questionList = getQuestionList();
        final PageImpl<Question> questionPage = new PageImpl<>(
                questionList
        );
        when(questionRepository.findAllByDisabledIsFalseOrderByTopDescQuestionIdAsc(
                any(Pageable.class))
        ).thenReturn(questionPage);

        //when
        final Page<QuestionSimpleDto> questionSimpleDtoPage = customerCenterService.getQuestionSimpleDtoList(pageRequest);
        final List<QuestionSimpleDto> questionSimpleDtoList = questionSimpleDtoPage.get().collect(Collectors.toList());
        //then
        assertAll(
                () -> assertThat(questionSimpleDtoList.size())
                        .isEqualTo(questionList.size()),
                () -> assertThat(questionSimpleDtoList.get(0))
                        .hasFieldOrPropertyWithValue("questionId", questionList.get(0).getQuestionId())
                        .hasFieldOrPropertyWithValue("title", questionList.get(0).getTitle())
                        .hasFieldOrPropertyWithValue("view", questionList.get(0).getView())
                        .hasFieldOrPropertyWithValue("top", questionList.get(0).isTop())
        );

    }

    private List<Question> getQuestionList() {
        List<Question> questions = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            questions.add(
                    createQuestion(i, true)
            );
        }
        for (int i = 6; i <= 10; i++) {
            questions.add(
                    createQuestion(i, false)
            );
        }
        return questions;
    }

    private Question createQuestion(int n, boolean top) {
        return Question.builder()
                .questionId((long) n)
                .title("제목"+n)
                .content("내용"+n)
                .top(top)
                .view(n+10)
                .disabled(false)
                .build();
    }


    @Test
    @DisplayName("[실패] 자주하는 질문 목록 조회 -  자주하는 질문 목록이 없어서 QUESTION_IS_EMPTY 에러 발생")
    public void givenPageable_whenGetQuestionSimpleDtoEmptyList_thenQuestionIsEmptyError() throws Exception{
        //given
        final PageRequest pageRequest = PageRequest.of(0, 10);
        final CustomerCenterErrorCode errorCode = CustomerCenterErrorCode.QUESTION_IS_EMPTY;
        when(questionRepository.findAllByDisabledIsFalseOrderByTopDescQuestionIdAsc(
                any(Pageable.class))
        ).thenThrow(new CustomerCenterException(errorCode));

        //when
        final CustomerCenterException customerCenterException = assertThrows(CustomerCenterException.class,
                () -> customerCenterService.getQuestionSimpleDtoList(pageRequest)
        );

        //then
        assertAll(
                () -> assertThat(customerCenterException.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(customerCenterException.getErrorMessage()).isEqualTo(errorCode.getDescription())
        );
    }

    @Test
    @DisplayName("[성공] 자주하는 질문 상세 조회")
    public void givenQuestionId_whenGetQuestionDetailDto_thenQuestionDetailDto() throws Exception{
        //given
        final long questionId = 1L;
        final Question question = createQuestion((int) questionId);
        when(questionRepository.findByQuestionIdAndDisabledFalse(questionId))
                .thenReturn(
                        Optional.ofNullable(
                                question
                        )
                );
        //when
        final QuestionDetailDto questionDetailDto = customerCenterService.getQuestionDetailDto(questionId);

        //then
        assertAll(
                () -> assertThat(questionDetailDto.getQuestionId()).isEqualTo(question.getQuestionId()),
                () -> assertThat(questionDetailDto.getTitle()).isEqualTo(question.getTitle()),
                () -> assertThat(questionDetailDto.getView()).isEqualTo(question.getView()),
                () -> assertThat(questionDetailDto.getCreatedBy()).isNotNull(),
                () -> assertThat(questionDetailDto.getCreatedDate()).isNotNull()
        );
    }



    private Question createQuestion(int n) {
        return createQuestion(n, false);
    }

    @Test
    @DisplayName("[실패] 자주하는 질문 상세 조회 - 해당 자주하는 질문 ID가 존재하지 않아 NOT_FOUND_QUESTION 에러 발생")
    public void givenQuestionId_whenGetQuestionDetailDto_thenNotFoundQuestionError() throws Exception {
        //given
        final long questionId = 1L;
        final CustomerCenterErrorCode errorCode = CustomerCenterErrorCode.NOT_FOUND_QUESTION;

        when(questionRepository.findByQuestionIdAndDisabledFalse(questionId)
        ).thenThrow(new CustomerCenterException(errorCode));

        //when
        final CustomerCenterException customerCenterException = assertThrows(CustomerCenterException.class,
                () -> customerCenterService.getQuestionDetailDto(questionId)
        );

        //then
        assertAll(
                () -> assertThat(customerCenterException.getErrorCode()).isEqualTo(errorCode),
                () -> assertThat(customerCenterException.getErrorMessage()).isEqualTo(errorCode.getDescription())
        );
    }

    @Test
    @DisplayName("[성공] 공지 추가 하기")
    public void givenCreateNoticeDtoRequest_whenCreateNotice_thenCreateNoticeDtoResponse() throws Exception{
        //given
            when(noticeRepository.save(
                    any(Notice.class))
            ).thenReturn(
                    givenNoticeEntity()
            );
        //when
        CreateNoticeDto.Response response = customerCenterService.createNotice(
                givenCreateNoticeDtoRequest()
        );
        //then
        assertThat(response)
                .hasFieldOrPropertyWithValue("noticeId", 1L)
                .hasFieldOrPropertyWithValue("title", "온프리에 오신 것을 환영합니다.")
                .hasFieldOrPropertyWithValue("content", "안녕하세요 온프리입니다....")
                .hasFieldOrPropertyWithValue("top", true)
                .hasFieldOrPropertyWithValue("disabled", false)
        ;

        verify(noticeRepository).save(any());
    }

    private CreateNoticeDto.Request givenCreateNoticeDtoRequest() {
        return CreateNoticeDto.Request.builder()
                .title("제목")
                .content("내용")
                .top(true)
                .build();
    }

    private Notice givenNoticeEntity() {
        return Notice.builder()
                .noticeId(1L)
                .title("온프리에 오신 것을 환영합니다.")
                .top(true)
                .disabled(false)
                .content("안녕하세요 온프리입니다....")
                .view(0)
                .build();
    }

    @Test
    @DisplayName("[성공] 공지 수정 하기")
    public void givenUpdateNoticeDtoRequest_whenUpdateNotice_thenUpdateNoticeDtoResponse() throws Exception{
        //given
        long noticeId = 1L;

        when(noticeRepository.findByNoticeIdAndDisabledFalse(anyLong()))
                .thenReturn(
                    Optional.of(
                            givenNoticeEntity()
                    )
            );
            //when
        UpdateNoticeDto.Response response = customerCenterService.updateNotice(
                noticeId, givenUpdateNoticeDtoRequest()
        );
        //then
        assertThat(response)
                .hasFieldOrPropertyWithValue("noticeId", noticeId)
                .hasFieldOrPropertyWithValue("title", "(수정)온프리에 오신 것을 환영합니다.")
                .hasFieldOrPropertyWithValue("content", "(수정)안녕하세요 온프리입니다....")
                .hasFieldOrPropertyWithValue("top", true)
                .hasFieldOrPropertyWithValue("disabled", true)
        ;
        verify(noticeRepository).findByNoticeIdAndDisabledFalse(eq(noticeId));
    }

    private UpdateNoticeDto.Request givenUpdateNoticeDtoRequest() {
        return UpdateNoticeDto.Request.builder()
                .title("(수정)온프리에 오신 것을 환영합니다.")
                .content("(수정)안녕하세요 온프리입니다....")
                .top(true)
                .disabled(true)
                .build();
    }

    @Test
    @DisplayName("[실패] 공지 수정 하기 -  해당 공지 ID가 존재하지 않아 NOT_FOUND_NOTICE 에러 발생")
    public void givenUpdateNoticeDtoRequest_whenUpdateNoticeButNotFoundNotice_thenNotFoundNoticeError() throws Exception{
        //given
        long noticeId = 1L;
        CustomerCenterErrorCode errorCode = CustomerCenterErrorCode.NOT_FOUND_NOTICE;

        when(noticeRepository.findByNoticeIdAndDisabledFalse(anyLong()))
                .thenReturn(
                        Optional.empty()
                );
        //when
        CustomerCenterException customerCenterException = assertThrows(CustomerCenterException.class,
                () -> customerCenterService.updateNotice(
                        noticeId, givenUpdateNoticeDtoRequest()
                )
        );
        //then
        assertThat(customerCenterException)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription())
        ;
        verify(noticeRepository).findByNoticeIdAndDisabledFalse(eq(noticeId));
    }

    @Test
    @DisplayName("[성공] 질문 추가 하기")
    public void givenCreateQuestionDtoRequest_whenCreateQuestion_thenCreateQuestionDtoResponse() throws Exception{
        //given
        when(questionRepository.save(
                any(Question.class))
        ).thenReturn(
                givenQuestionEntity()
        );
        //when
        CreateQuestionDto.Response response = customerCenterService.createQuestion(
                givenCreateQuestionDtoRequest()
        );
        //then
        assertThat(response)
                .hasFieldOrPropertyWithValue("questionId", 1L)
                .hasFieldOrPropertyWithValue("title", "온프리에 오신 것을 환영합니다.")
                .hasFieldOrPropertyWithValue("content", "안녕하세요 온프리입니다....")
                .hasFieldOrPropertyWithValue("top", true)
                .hasFieldOrPropertyWithValue("disabled", false)
        ;

        verify(questionRepository).save(any());
    }

    private CreateQuestionDto.Request givenCreateQuestionDtoRequest() {
        return CreateQuestionDto.Request.builder()
                .title("제목")
                .content("내용")
                .top(true)
                .build();
    }

    private Question givenQuestionEntity() {
        return Question.builder()
                .questionId(1L)
                .title("온프리에 오신 것을 환영합니다.")
                .top(true)
                .disabled(false)
                .content("안녕하세요 온프리입니다....")
                .view(0)
                .build();
    }

    @Test
    @DisplayName("[성공] 질문 수정 하기")
    public void givenUpdateQuestionDtoRequest_whenUpdateQuestion_thenUpdateQuestionDtoResponse() throws Exception{
        //given
        long questionId = 1L;

        when(questionRepository.findByQuestionIdAndDisabledFalse(anyLong()))
                .thenReturn(
                        Optional.of(
                                givenQuestionEntity()
                        )
                );
        //when
        UpdateQuestionDto.Response response = customerCenterService.updateQuestion(
                questionId, givenUpdateQuestionDtoRequest()
        );
        //then
        assertThat(response)
                .hasFieldOrPropertyWithValue("questionId", questionId)
                .hasFieldOrPropertyWithValue("title", "(수정)온프리에 오신 것을 환영합니다.")
                .hasFieldOrPropertyWithValue("content", "(수정)안녕하세요 온프리입니다....")
                .hasFieldOrPropertyWithValue("top", true)
                .hasFieldOrPropertyWithValue("disabled", true)
        ;
        verify(questionRepository).findByQuestionIdAndDisabledFalse(eq(questionId));
    }

    private UpdateQuestionDto.Request givenUpdateQuestionDtoRequest() {
        return UpdateQuestionDto.Request.builder()
                .title("(수정)온프리에 오신 것을 환영합니다.")
                .content("(수정)안녕하세요 온프리입니다....")
                .top(true)
                .disabled(true)
                .build();
    }

    @Test
    @DisplayName("[실패] 질문 수정 하기 -  해당 질문 ID가 존재하지 않아 NOT_FOUND_QUESTION 에러 발생")
    public void givenUpdateQuestionDtoReqQuestionUpdateQuestionButNotFoundQuestion_thenNotFoundQuestionError() throws Exception{
        //given
        long questionId = 1L;
        CustomerCenterErrorCode errorCode = CustomerCenterErrorCode.NOT_FOUND_QUESTION;

        when(questionRepository.findByQuestionIdAndDisabledFalse(anyLong()))
                .thenReturn(
                        Optional.empty()
                );
        //when
        CustomerCenterException customerCenterException = assertThrows(CustomerCenterException.class,
                () -> customerCenterService.updateQuestion(
                        questionId, givenUpdateQuestionDtoRequest()
                )
        );
        //then
        assertThat(customerCenterException)
                .hasFieldOrPropertyWithValue("errorCode", errorCode)
                .hasFieldOrPropertyWithValue("errorMessage", errorCode.getDescription())
        ;
        verify(questionRepository).findByQuestionIdAndDisabledFalse(eq(questionId));
    }


}

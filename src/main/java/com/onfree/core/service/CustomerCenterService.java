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
import com.onfree.common.error.code.CustomerCenterErrorCode;
import com.onfree.common.error.exception.CustomerCenterException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomerCenterService {
    private final NoticeRepository noticeRepository;
    private final QuestionRepository questionRepository;

    /** 공지사항 목록 조회*/
    public Page<NoticeSimpleDto> getNoticeSimpleDtoList(Pageable pageable) {
        return getNoticeList(pageable)
                .map(NoticeSimpleDto::fromEntity);
    }

    private Page<Notice> getNoticeList(Pageable pageable) {
        final Page<Notice> noticeList = noticeRepository.findAllByDisabledIsFalseOrderByTopDescNoticeIdAsc(pageable);
        if(noticeList.getTotalElements() <= 0){
            throw new CustomerCenterException(CustomerCenterErrorCode.NOTICES_IS_EMPTY);
        }
        return noticeList;
    }

    /** 공지사항 상세 조회*/
    @Transactional
    public NoticeDetailDto getNoticeDetailDto(Long noticeId) {
        return NoticeDetailDto.fromEntity(
                getNoticeAndUpdateView(noticeId)
        );
    }

    private Notice getNoticeAndUpdateView(Long noticeId) {
        final Notice notice = getNotice(noticeId);
            notice.updateView();
        return notice;
    }

    private Notice getNotice(Long noticeId) {
        return noticeRepository.findByNoticeIdAndDisabledFalse(noticeId)
                .orElseThrow(() -> new CustomerCenterException(CustomerCenterErrorCode.NOT_FOUND_NOTICE));
    }

    /** 자주하는 질문 목록 조회*/
    public Page<QuestionSimpleDto> getQuestionSimpleDtoList(Pageable pageable) {
        return getQuestionList(pageable)
                .map(QuestionSimpleDto::fromEntity);
    }

    private Page<Question> getQuestionList(Pageable pageable) {
        final Page<Question> questionList = questionRepository.findAllByDisabledIsFalseOrderByTopDescQuestionIdAsc(pageable);
        if(questionList.getTotalElements() <= 0){
            throw new CustomerCenterException(CustomerCenterErrorCode.QUESTION_IS_EMPTY);
        }
        return questionList;
    }

    /** 자주하는 질문 상세 조회*/
    @Transactional
    public QuestionDetailDto getQuestionDetailDto(Long questionId) {
        return QuestionDetailDto
                .fromEntity(
                        getQuestionAndUpdateView(questionId)
                );
    }

    private Question getQuestionAndUpdateView(Long questionId) {
        final Question question = getQuestion(questionId);
        question.updateView();
        return question;
    }

    private Question getQuestion(Long questionId) {
        return questionRepository.findByQuestionIdAndDisabledFalse(questionId)
                .orElseThrow(() -> new CustomerCenterException(CustomerCenterErrorCode.NOT_FOUND_QUESTION));
    }

    /** 공지 추가 (Only ADMIN)*/
    @Transactional
    public CreateNoticeDto.Response createNotice(CreateNoticeDto.Request request) {
        return CreateNoticeDto.Response
                .fromEntity(
                        saveNotice(request.toEntity())
                );
    }

    private Notice saveNotice(Notice notice) {
        return noticeRepository.save(
                notice
        );
    }
    /** 공지 수정 (Only ADMIN)*/
    @Transactional
    public UpdateNoticeDto.Response updateNotice(Long noticeId, UpdateNoticeDto.Request request) {
        final Notice notice = getNotice(noticeId);
        notice.updateByUpdateNoticeDto(request);
        return UpdateNoticeDto.Response
                .fromEntity(notice);
    }
    /** 자주하는 질문 추가*/
    @Transactional
    public CreateQuestionDto.Response createQuestion(CreateQuestionDto.Request request) {
        return CreateQuestionDto.Response
                .fromEntity(
                        saveQuestion(request.toEntity())
                );
    }

    private Question saveQuestion(Question question) {
        return questionRepository.save(
                question
        );
    }

    /** 자주하는 질문 수정*/
    @Transactional
    public UpdateQuestionDto.Response updateQuestion(Long questionId, UpdateQuestionDto.Request request) {
        final Question question = getQuestion(questionId);
        question.updateByUpdateQuestionDto(request);
        return UpdateQuestionDto.Response
                .fromEntity(question);
    }
}

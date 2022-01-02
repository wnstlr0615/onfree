package com.onfree.core.service;

import com.onfree.core.dto.notice.NoticeDetailDto;
import com.onfree.core.dto.notice.NoticeSimpleDto;
import com.onfree.core.dto.question.QuestionDetailDto;
import com.onfree.core.dto.question.QuestionSimpleDto;
import com.onfree.core.entity.Notice;
import com.onfree.core.entity.Question;
import com.onfree.core.repository.NoticeRepository;
import com.onfree.core.repository.QuestionRepository;
import com.onfree.error.code.CustomerCenterErrorCode;
import com.onfree.error.exception.CustomerCenterException;
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
        final Page<Notice> noticeList = noticeRepository.findAll(pageable);
        if(noticeList.getTotalElements() <= 0){
            throw new CustomerCenterException(CustomerCenterErrorCode.NOTICES_IS_EMPTY);
        }
        return noticeList;
    }

    /** 공지사항 상세 조회*/
    @Transactional
    public NoticeDetailDto getNoticeDetailDto(Long noticeId) {
        return NoticeDetailDto.fromEntity(
                getNotice(noticeId)
        );
    }

    private Notice getNotice(Long noticeId) {
        final Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new CustomerCenterException(CustomerCenterErrorCode.NOT_FOUND_NOTICE));
            notice.updateView();
        return notice;
    }

    /** 자주하는 질문 목록 조회*/
    public Page<QuestionSimpleDto> getQuestionSimpleDtoList(Pageable pageable) {
        return getQuestionList(pageable)
                .map(QuestionSimpleDto::fromEntity);
    }

    private Page<Question> getQuestionList(Pageable pageable) {
        final Page<Question> questionList = questionRepository.findAll(pageable);
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
                        getQuestion(questionId)
                );
    }

    private Question getQuestion(Long questionId) {
        final Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new CustomerCenterException(CustomerCenterErrorCode.NOT_FOUND_QUESTION));
        question.updateView();
        return question;
    }
}

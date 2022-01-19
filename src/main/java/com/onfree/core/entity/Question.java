package com.onfree.core.entity;

import com.onfree.core.dto.question.UpdateQuestionDto;
import com.onfree.common.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Question extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    private boolean top;

    @Column(nullable = false)
    private boolean disabled;

    @Column(nullable = false)
    private int view;

    public void updateView() {
        view = view + 1;
    }

    public void updateByUpdateQuestionDto(UpdateQuestionDto.Request request) {
        title = request.getTitle() != null ? request.getTitle() : title;
        content = request.getContent() != null ? request.getContent() : content;
        top = request.getTop() != null ? request.getTop() : top;
        disabled = request.getDisabled() != null ? request.getDisabled() : disabled;
    }
}

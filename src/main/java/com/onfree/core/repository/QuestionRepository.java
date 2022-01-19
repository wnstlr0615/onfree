package com.onfree.core.repository;

import com.onfree.core.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findAllByDisabledIsFalseOrderByTopDescQuestionIdAsc(Pageable any);

    Optional<Question> findByQuestionIdAndDisabledFalse(long questionId);
}

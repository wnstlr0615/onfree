package com.onfree.core.repository;

import com.onfree.core.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Page<Notice> findAllByDisabledIsFalseOrderByTopDescNoticeIdAsc(Pageable pageable);
    Optional<Notice> findByNoticeIdAndDisabledFalse(Long noticeId);
}

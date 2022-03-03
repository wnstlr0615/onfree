package com.onfree.core.repository;

import com.onfree.core.entity.realtimerequset.RealTimeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RealTimeRequestRepository extends JpaRepository<RealTimeRequest, Long> {

    @EntityGraph(attributePaths = "user")
    @Query(value = "select r from RealTimeRequest as r where r.user.userId = :userId ")
    Page<RealTimeRequest> findAllByUserId(@Param("userId")Long userId, Pageable pageable);
}

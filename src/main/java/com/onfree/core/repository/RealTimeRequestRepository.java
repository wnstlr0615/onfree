package com.onfree.core.repository;

import com.onfree.core.entity.realtimerequset.RealTimeRequest;
import com.onfree.core.entity.realtimerequset.RequestStatus;
import com.onfree.core.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RealTimeRequestRepository extends JpaRepository<RealTimeRequest, Long> {

    @EntityGraph(attributePaths = "user")
    Page<RealTimeRequest> findAllByUserAndStatusNot(User user, RequestStatus status, Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Optional<RealTimeRequest> findByRealTimeRequestId(Long realTimeRequestId);

    @EntityGraph(attributePaths = "user")
    Optional<RealTimeRequest> findByRealTimeRequestIdAndStatusNot(Long realTimeRequestId, RequestStatus status);

    @EntityGraph(attributePaths = "user")
    Optional<RealTimeRequest> findByRealTimeRequestIdAndUser(Long realTimeRequestId, User user);

    Page<RealTimeRequest> findAllByStatusNot(Pageable pageable, RequestStatus status);
}

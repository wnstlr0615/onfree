package com.onfree.core.repository;

import com.onfree.core.entity.realtimerequset.RealTimeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RealTimeRequestRepository extends JpaRepository<RealTimeRequest, Long> {
}

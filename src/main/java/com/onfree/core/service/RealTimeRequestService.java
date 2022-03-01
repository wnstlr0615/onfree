package com.onfree.core.service;

import com.onfree.core.repository.RealTimeRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RealTimeRequestService {
    private final RealTimeRequestRepository realTimeRequestRepository;
}

package com.onfree.core.service;

import com.onfree.core.dto.realtimerequest.SimpleRealtimeRequestDto;
import com.onfree.core.entity.realtimerequset.RealTimeRequest;
import com.onfree.core.repository.RealTimeRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RealTimeRequestService {
    private final RealTimeRequestRepository realTimeRequestRepository;

    /**
     * 페이징 실시간 의뢰 조회
     */
    public Page<SimpleRealtimeRequestDto> findAllRealTimeRequest(int page, int size) {
        return getPageSimpleRealTimeRequestDto( //simpleRealTimeRequestDto 로 변환
                getPagingRealTimeRequest( // 페이징 처리된 실시간 의뢰 조회
                        PageRequest.of(page, size)
                )
        );
    }

    private Page<SimpleRealtimeRequestDto> getPageSimpleRealTimeRequestDto(Page<RealTimeRequest> realTimeRequestPage) {
        return realTimeRequestPage.map(SimpleRealtimeRequestDto::fromEntity);
    }

    private Page<RealTimeRequest> getPagingRealTimeRequest(PageRequest pageRequest) {
        return realTimeRequestRepository.findAll(pageRequest);
    }

    /**
     * 사용자(userId) 실시간 의뢰 조회
     */
    public Page<SimpleRealtimeRequestDto> findAllRealTimeRequestByUserId(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return getPageSimpleRealTimeRequestDto( // SimpleRealTimeRequestDto로 변환
                getPagingRealTimeRequestByUserIdAndPageable( // 사용자 실시간 의뢰 조회
                        userId, pageRequest
                )
        );
    }

    private Page<RealTimeRequest> getPagingRealTimeRequestByUserIdAndPageable(Long userId, PageRequest pageRequest) {
        return realTimeRequestRepository.findAllByUserId(userId, pageRequest);
    }
}



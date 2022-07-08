package com.onfree.core.service.realtimerequest;

import com.onfree.core.dto.realtimerequest.SimpleRealtimeRequestDto;
import com.onfree.core.entity.realtimerequset.RealTimeRequest;
import com.onfree.core.entity.realtimerequset.RequestStatus;
import com.onfree.core.entity.user.User;
import com.onfree.core.repository.RealTimeRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRealTimeRequestService {
    private final RealTimeRequestRepository realTimeRequestRepository;
    /**
     * 사용자(userId) 실시간 의뢰 조회
     */
    public Page<SimpleRealtimeRequestDto> findAllRealTimeRequestByUserId(User user, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return getPageSimpleRealTimeRequestDto( // SimpleRealTimeRequestDto 로 변환
                    getPagingRealTimeRequestByUserIdAndPageable( // 사용자 실시간 의뢰 조회
                            user, pageRequest
                    )
        );
    }
    private Page<RealTimeRequest> getPagingRealTimeRequestByUserIdAndPageable(User user, PageRequest pageRequest) {
        return realTimeRequestRepository.findAllByUserAndStatusNot(user, RequestStatus.REQUEST_DELETED, pageRequest);
    }


    private Page<SimpleRealtimeRequestDto> getPageSimpleRealTimeRequestDto(Page<RealTimeRequest> realTimeRequestPage) {
        return realTimeRequestPage.map(SimpleRealtimeRequestDto::fromEntity);
    }
}

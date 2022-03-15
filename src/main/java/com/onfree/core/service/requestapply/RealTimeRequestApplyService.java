package com.onfree.core.service.requestapply;

import com.onfree.common.error.code.RealTimeRequestErrorCode;
import com.onfree.common.error.code.RequestApplyErrorCode;
import com.onfree.common.error.exception.RealTimeRequestException;
import com.onfree.common.error.exception.RequestApplyException;
import com.onfree.core.entity.realtimerequset.RealTimeRequest;
import com.onfree.core.entity.requestapply.RealTimeRequestApply;
import com.onfree.core.entity.requestapply.RequestApplyStatus;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.repository.RealTimeRequestRepository;
import com.onfree.core.repository.requestappy.RealTimeRequestApplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RealTimeRequestApplyService {
    private final RealTimeRequestApplyRepository realTimeRequestApplyRepository;
    private final RealTimeRequestRepository realTimeRequestRepository;

    /** 실시간 의뢰 지원하기 */
    @Transactional
    public void addRequestApply(Long requestId, ArtistUser artistUser) {
        //실시간 의뢰 조회
        RealTimeRequest realTimeRequest = getRealTimeRequestById(requestId);

        // 실시간 의뢰 지원 여부
        validateRequestApplicable(realTimeRequest);

        saveRealTimeRequestApply( // 실시간 의뢰 지원 Entity 저장
                createRealTimeRequestApply(realTimeRequest, artistUser) // 실시간 의뢰 지원 Entity 생성
        );
    }

    private void validateRequestApplicable(RealTimeRequest realTimeRequest) {
        switch (realTimeRequest.getStatus()){
            case REQUEST_DELETED:
                throw new RequestApplyException(RequestApplyErrorCode.CAN_NOT_APPLY_REAL_TIME_REQUEST_DELETED);
            case REQUEST_FINISH:
                throw new RequestApplyException(RequestApplyErrorCode.CAN_NOT_APPLY_REAL_TIME_REQUEST_FINISHED);
            case REQUEST_REQUESTING:
                throw new RequestApplyException(RequestApplyErrorCode.CAN_NOT_APPLY_REAL_TIME_REQUEST_REQUESTING);
            case REQUEST_RECRUITING:
            default:
        }
    }

    private void saveRealTimeRequestApply(RealTimeRequestApply realTimeRequestApply) {
        realTimeRequestApplyRepository.save(realTimeRequestApply);
    }

    private RealTimeRequestApply createRealTimeRequestApply(RealTimeRequest realTimeRequest, ArtistUser artistUser) {
        return RealTimeRequestApply.createRealTimeRequestApply(realTimeRequest, artistUser, RequestApplyStatus.REQUEST_APPLY_CRATED);
    }

    private RealTimeRequest getRealTimeRequestById(Long requestId) {
        return realTimeRequestRepository.findById(requestId)
                .orElseThrow(
                        () -> new RealTimeRequestException(RealTimeRequestErrorCode.NOT_FOUND_REAL_TIME_REQUEST)
                );
    }
}

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

        //실시간 의뢰 작성자와 지원 작가가 다른 지 확인
        validateRequestWriterNotEqualArtistUser(artistUser, realTimeRequest);

        // 실시간 의뢰 지원 여부
        validateRequestApplicable(realTimeRequest);

        // 실시간 의뢰 지원 Entity 생성
        RealTimeRequestApply realTimeRequestApply = createRealTimeRequestApply(realTimeRequest, artistUser);

        // 실시간 의뢰 지원 Entity 저장
        saveRealTimeRequestApply(realTimeRequestApply);
    }

    private void validateRequestWriterNotEqualArtistUser(ArtistUser artistUser, RealTimeRequest realTimeRequest) {
        if(realTimeRequest.getUser().isEqualsUserId(artistUser)){
            throw new RequestApplyException(RequestApplyErrorCode.CAN_NOT_APPLY_REAL_TIME_REQUEST_DELETED);
        }
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
        //중복 지원 여부 확인
        if(isAlreadyRequestApply(realTimeRequestApply)){
            throw new RequestApplyException(RequestApplyErrorCode.ALREADY_REQUEST_APPLY);
        }

        realTimeRequestApplyRepository.save(realTimeRequestApply);
    }

    private boolean isAlreadyRequestApply(RealTimeRequestApply realTimeRequestApply) {
        return realTimeRequestApplyRepository
                .countByRealTimeRequestAndArtistUser(
                        realTimeRequestApply.getRealTimeRequest(), realTimeRequestApply.getArtistUser()
                ) > 0;
    }

    private RealTimeRequestApply createRealTimeRequestApply(RealTimeRequest realTimeRequest, ArtistUser artistUser) {
        return RealTimeRequestApply.createRealTimeRequestApply(realTimeRequest, artistUser, RequestApplyStatus.READY);
    }

    private RealTimeRequest getRealTimeRequestById(Long requestId) {
        return realTimeRequestRepository.findById(requestId)
                .orElseThrow(
                        () -> new RealTimeRequestException(RealTimeRequestErrorCode.NOT_FOUND_REAL_TIME_REQUEST)
                );
    }
}

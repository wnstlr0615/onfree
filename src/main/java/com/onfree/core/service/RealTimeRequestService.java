package com.onfree.core.service;

import com.onfree.common.error.code.RealTimeRequestErrorCode;
import com.onfree.common.error.exception.RealTimeRequestException;
import com.onfree.core.dto.realtimerequest.CreateRealTimeRequestDto;
import com.onfree.core.dto.realtimerequest.RealTimeRequestDetailDto;
import com.onfree.core.dto.realtimerequest.SimpleRealtimeRequestDto;
import com.onfree.core.dto.realtimerequest.UpdateRealTimeRequestDto;
import com.onfree.core.entity.realtimerequset.RealTimeRequest;
import com.onfree.core.entity.realtimerequset.RequestStatus;
import com.onfree.core.entity.user.User;
import com.onfree.core.repository.RealTimeRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RealTimeRequestService {
    private final RealTimeRequestRepository realTimeRequestRepository;

    /**
     * 페이징 실시간 의뢰 전체 조회
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
        return realTimeRequestRepository.findAllByStatusNot(pageRequest, RequestStatus.REQUEST_DELETED);
    }

    /** 실시간 의뢰 상세 조회*/
    public RealTimeRequestDetailDto findOneRealTimeRequest(Long requestId) {
        return getRealTimeRequestDetail( // RealTimeRequestDetail 로 변환
                validateReadableRealTimeRequest( // 읽기 가능 여부 확인
                    getRealTimeRequestDetailById(requestId) // id로 실시간 의뢰 조회
                )
        );
    }

    private RealTimeRequest getRealTimeRequestDetailById(Long requestId) {
        return realTimeRequestRepository.findByRealTimeRequestId(requestId)
                .orElseThrow(
                        () -> new RealTimeRequestException(RealTimeRequestErrorCode.NOT_FOUND_REAL_TIME_REQUEST)
                );
    }

    private RealTimeRequest validateReadableRealTimeRequest(RealTimeRequest realTimeRequest) {
        switch (realTimeRequest.getStatus()){
            case REQUEST_DELETED:
                throw new RealTimeRequestException(RealTimeRequestErrorCode.REAL_TIME_REQUEST_DELETED);
            case REQUEST_REQUESTING:
            case REQUEST_RECRUITING:
            case REQUEST_FINISH:
            default:
        }
        return realTimeRequest;
    }

    private RealTimeRequestDetailDto getRealTimeRequestDetail(RealTimeRequest realTimeRequest) {
        return RealTimeRequestDetailDto.fromEntity(
                realTimeRequest
        );
    }

    /** 실시간 의뢰 추가하기 */
    @Transactional
    public CreateRealTimeRequestDto.Response addRealTimeRequest(User user, CreateRealTimeRequestDto.Request request) {
        return getCreateRealTimeRequestDtoResponse(// Reseponse Dto로 변환
                 saveRealTimRequest( // RealTimeRequest Entity 저장
                        createRealTimeRequestFromDto(user, request) // requestDto로 RealTimeRequest 생성
                )
        );
    }
    private RealTimeRequest createRealTimeRequestFromDto(User user, CreateRealTimeRequestDto.Request request) {
        RealTimeRequest realTimeRequest = request.toEntity();
        realTimeRequest.setUser(user);
        return realTimeRequest;
    }

    private RealTimeRequest saveRealTimRequest(RealTimeRequest realTimeRequest) {
        return realTimeRequestRepository.save(realTimeRequest);
    }

    private CreateRealTimeRequestDto.Response getCreateRealTimeRequestDtoResponse(RealTimeRequest entity) {
        return CreateRealTimeRequestDto.Response.fromEntity(entity);
    }

    /** 실시간 의뢰 수정 하기 */
    @Transactional
    public void modifyRealTimeRequest(Long requestId, User user, UpdateRealTimeRequestDto updateRealTimeRequestDto) {
        RealTimeRequest realTimeRequest = getRealTimeRequestByRealTimeRequestByIdAndUser(requestId, user); // 실시간 의뢰 조회
        validateUpdatableRealTimeRequest(realTimeRequest); //실시간 의뢰 수정 가능 여부 확인
        updateRealTimeRequestByDto(realTimeRequest, updateRealTimeRequestDto); // 실시간 의뢰 업데이트
    }

    private RealTimeRequest getRealTimeRequestByRealTimeRequestByIdAndUser(Long requestId, User user) {
        return realTimeRequestRepository.findByRealTimeRequestIdAndUser(requestId, user)
                .orElseThrow(
                        () -> new RealTimeRequestException(RealTimeRequestErrorCode.NOT_FOUND_REAL_TIME_REQUEST)
                );
    }

    private void validateUpdatableRealTimeRequest(RealTimeRequest realTimeRequest) {
        switch (realTimeRequest.getStatus()){
            case REQUEST_DELETED:
                throw new RealTimeRequestException(RealTimeRequestErrorCode.REAL_TIME_REQUEST_DELETED);
            case REQUEST_FINISH:
                throw new RealTimeRequestException(RealTimeRequestErrorCode.FINISH_REQUEST_CAN_NOT_UPDATE);
            case REQUEST_REQUESTING:
            case REQUEST_RECRUITING:
            default:
        }
    }

    private void updateRealTimeRequestByDto(RealTimeRequest realTimeRequest, UpdateRealTimeRequestDto dto) {
        validateUpdateStartDateIsAfterCreateDate(realTimeRequest, dto);// 새로운 시작 시간 검증
        realTimeRequest.update(
                dto.getTitle(), dto.getContent(), dto.getStartDate(), dto.getEndDate(),
                dto.getUseType(), dto.getReferenceLink(), dto.getAdult()
        );
    }

    private void validateUpdateStartDateIsAfterCreateDate(RealTimeRequest realTimeRequest, UpdateRealTimeRequestDto dto) {
        LocalDate createDate = realTimeRequest.getCreatedDate().toLocalDate();
        if(createDate.isAfter(dto.getStartDate())){ // 업데이트하는 시작 시간이 생성 시간보다 빠른 경우
            throw new RealTimeRequestException(RealTimeRequestErrorCode.UPDATE_START_TIME_MUST_BE_AFTER_CREATE_DATE);
        }
    }

    /** 실시간 의뢰 삭제*/
    @Transactional
    public void removeRealTimeRequest(Long requestId, User user) {
        deleteRealTimeRequest( // 실시간 의뢰 삭제
                validateDeletableRealTimeRequest( //삭제 가능 여부 확인
                    getRealTimeRequestByRealTimeRequestByIdAndUser(requestId, user) // 실시간 의뢰 조회
                )
        );
    }
    private RealTimeRequest validateDeletableRealTimeRequest(RealTimeRequest realTimeRequest) {
        switch (realTimeRequest.getStatus()){
            case REQUEST_DELETED:
                throw new RealTimeRequestException(RealTimeRequestErrorCode.REAL_TIME_REQUEST_ALREADY_DELETED);
            case REQUEST_FINISH:
            case REQUEST_REQUESTING:
            case REQUEST_RECRUITING:
            default:
        }
        return realTimeRequest;
    }

    private void deleteRealTimeRequest(RealTimeRequest realTimeRequest) {
        realTimeRequest.delete();
    }
    
    @Transactional
    public void modifyRequestStatus(Long requestId, User user) {
        finishRequestStatus( // 실시간 의뢰 삭제
                validateUpdatableRequestStatus( //실시간 의뢰 마감 설정 가능 유무 확인
                        getRealTimeRequestByRealTimeRequestByIdAndUser(requestId, user) // 실시간 의뢰 조회
                )
        );
    }

    private void finishRequestStatus(RealTimeRequest realTimeRequest) {
        realTimeRequest.finish();
    }

    private RealTimeRequest validateUpdatableRequestStatus(RealTimeRequest realTimeRequest) {
        switch (realTimeRequest.getStatus()){
            case REQUEST_DELETED:
                throw new RealTimeRequestException(RealTimeRequestErrorCode.REAL_TIME_REQUEST_DELETED);
            case REQUEST_FINISH:
                throw new RealTimeRequestException(RealTimeRequestErrorCode.REAL_TIME_REQUEST_STATUS_ALREADY_FINISH);
            case REQUEST_REQUESTING:
            case REQUEST_RECRUITING:
            default:
        }
        return realTimeRequest;
    }
}



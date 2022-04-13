package com.onfree.core.service;

import com.onfree.common.error.code.RealTimeRequestErrorCode;
import com.onfree.common.error.exception.RealTimeRequestException;
import com.onfree.common.model.UploadFile;
import com.onfree.core.dto.realtimerequest.CreateRealTimeRequestDto;
import com.onfree.core.dto.realtimerequest.RealTimeRequestDetailDto;
import com.onfree.core.dto.realtimerequest.SimpleRealtimeRequestDto;
import com.onfree.core.dto.realtimerequest.UpdateRealTimeRequestDto;
import com.onfree.core.entity.fileitem.FileItem;
import com.onfree.core.entity.fileitem.FileStatus;
import com.onfree.core.entity.fileitem.FileType;
import com.onfree.core.entity.realtimerequset.RealTimeRequest;
import com.onfree.core.entity.realtimerequset.RequestStatus;
import com.onfree.core.entity.user.User;
import com.onfree.core.repository.FileItemRepository;
import com.onfree.core.repository.RealTimeRequestRepository;
import com.onfree.utils.AwsS3Component;
import com.onfree.utils.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RealTimeRequestService {
    private final RealTimeRequestRepository realTimeRequestRepository;
    private final FileStore fileStore;
    private final AwsS3Component s3Component;
    private final FileItemRepository fileItemRepository;
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
        return realTimeRequestPage. map(SimpleRealtimeRequestDto::fromEntity);
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
    public CreateRealTimeRequestDto.Response addRealTimeRequest(User user, CreateRealTimeRequestDto.Request request, List<MultipartFile> files) {

        //S3에 파일 저장
        List<FileItem> fileItems = saveMultipartFileInS3(files);

        //실시간 의뢰 객체 생성
        RealTimeRequest realTimeRequest = createRealTimeRequestFromDto(user, request, fileItems);

        //RealTimeRequest Entity 저장
        RealTimeRequest saveRealTimeRequestEntity = saveRealTimRequest(realTimeRequest);

        // Reseponse Dto로 변환
        return getCreateRealTimeRequestDtoResponse(saveRealTimeRequestEntity);
    }

    private  List<FileItem> saveMultipartFileInS3(@NonNull List<MultipartFile> files) {
        List<FileItem> fileItems = new ArrayList<>();
        for (MultipartFile file : files) {
            //파일 로컬에 저장
            UploadFile uploadFile = fileStore.saveFile(file);

            //파일 불러오기
            File localFile = fileStore.getFile(uploadFile);

            //S3에 파일 저장
            String filePathInS3 = s3Component.s3FileUpload(localFile, FileType.REQUEST_REFERENCE_FILE);

            //DB에 파일 정보 저장
            FileItem fileItem = saveFileItem(uploadFile, filePathInS3);
            fileItems.add(fileItem);

            //로컬 파일 제거
            fileStore.removeFile(localFile);

        }
        return fileItems;
    }

    private FileItem saveFileItem(UploadFile uploadFile, String filePathInS3) {
        FileItem fileItem = FileItem.createFileItem(uploadFile, filePathInS3, FileType.REQUEST_REFERENCE_FILE, FileStatus.USED);
        return fileItemRepository.save(fileItem);
    }

    private RealTimeRequest createRealTimeRequestFromDto(User user, CreateRealTimeRequestDto.Request request, @NonNull List<FileItem> fileItems) {
        RealTimeRequest realTimeRequest = request.toEntity();
        realTimeRequest.setUser(user);
        // 참조 파일 명 , 로 분리하여 저장
        realTimeRequestSetConcatReferenceFilenames(fileItems, realTimeRequest);
        return realTimeRequest;
    }

    private void realTimeRequestSetConcatReferenceFilenames(List<FileItem> fileItems, RealTimeRequest realTimeRequest) {
        if(!fileItems.isEmpty()){
            String concatFilename = getConcatFilename(fileItems);
            realTimeRequest.setReferenceFiles(concatFilename);
        }
    }

    private String getConcatFilename(List<FileItem> fileItems) {
        return fileItems.stream()
                .map(fileItem -> fileItem.getUploadFile().getStoreFilename())
                .collect(Collectors.joining(","));
    }

    private RealTimeRequest saveRealTimRequest(RealTimeRequest realTimeRequest) {
        return realTimeRequestRepository.save(realTimeRequest);
    }

    private CreateRealTimeRequestDto.Response getCreateRealTimeRequestDtoResponse(RealTimeRequest entity) {
        return CreateRealTimeRequestDto.Response.fromEntity(entity);
    }

    /** 실시간 의뢰 수정 하기 */

    @Transactional
    public void modifyRealTimeRequest(Long requestId, User user, UpdateRealTimeRequestDto updateRealTimeRequestDto, List<MultipartFile> files) {
        // 실시간 의뢰 조회
        RealTimeRequest realTimeRequest = getRealTimeRequestByRealTimeRequestByIdAndUser(requestId, user);

        //실시간 의뢰 수정 가능 여부 확인
        validateUpdatableRealTimeRequest(realTimeRequest);

        //새로 추가 된 참고파일 S3에 파일 저장 또는 제거
        updateReferenceFiles(realTimeRequest, files);

        //실시간 의뢰 업데이트
        updateRealTimeRequestByDto(realTimeRequest, updateRealTimeRequestDto);
    }

    private void updateReferenceFiles(RealTimeRequest realTimeRequest, List<MultipartFile> files) {
        //기존 저장 파일 삭제 처리
        String referenceFiles = realTimeRequest.getReferenceFiles();
        if(StringUtils.hasText(referenceFiles)){
            List<String> storedFilenames = Arrays.stream(referenceFiles.split(",")).collect(Collectors.toList());
            List<FileItem> fileItems = fileItemRepository.findAllByStoreFilenameIn(storedFilenames);
            fileItems.forEach(FileItem::deleted);
        }
        // 새로운 파일 S3에 업로드 및 FileItem 저장
        if(!files.isEmpty()){
            List<FileItem> fileItems = saveMultipartFileInS3(files);
            fileItemRepository.saveAll(fileItems);
            String referenceFileConcatName =
                    fileItems.stream()
                            .map(fileItem -> fileItem.getUploadFile().getStoreFilename())
                            .collect(
                                    Collectors.joining(",")
                            ).toString();
            realTimeRequest.setReferenceFiles(referenceFileConcatName);
        }
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

    /** 실시간 의뢰 마감 */
    @Transactional
    public void modifyRequestStatus(Long requestId, User user) {
        finishRequestStatus( // 실시간 의뢰 마감 설정
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



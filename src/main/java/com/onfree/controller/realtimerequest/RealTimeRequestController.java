package com.onfree.controller.realtimerequest;

import com.onfree.common.annotation.LoginUser;
import com.onfree.common.model.SimpleResponse;
import com.onfree.controller.SwaggerController;
import com.onfree.core.dto.realtimerequest.CreateRealTimeRequestDto;
import com.onfree.core.dto.realtimerequest.RealTimeRequestDetailDto;
import com.onfree.core.dto.realtimerequest.SimpleRealtimeRequestDto;
import com.onfree.core.dto.realtimerequest.UpdateRealTimeRequestDto;
import com.onfree.core.entity.user.User;
import com.onfree.core.service.realtimerequest.RealTimeRequestService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/real-time-requests")
public class RealTimeRequestController {
    private final RealTimeRequestService realTimeRequestService;

    /**
     * 실시간 의뢰 전체 조회
     */
    @ApiOperation(value = "실시간 의뢰 전체 보기")
    @GetMapping("")
    public ResponseEntity<?>realTimeRequestList(
            @ApiParam(name = "page", example = "0", required = true)
            @RequestParam(defaultValue = "0") int page,
            @ApiParam(name = "size", example = "10", required = true)
            @RequestParam(defaultValue = "10") int size,
            PagedResourcesAssembler<SimpleRealtimeRequestDto> assembler
    ) {
        Page<SimpleRealtimeRequestDto> response = realTimeRequestService.findAllRealTimeRequest(page, size);
        PagedModel<EntityModel<SimpleRealtimeRequestDto>> pagedModel = assembler.toModel(response);

        //링크 추가
        pagedModel.add(
                Link.of(linkTo(SwaggerController.class) + "/#/real-time-request-controller/RealTimeRequestUsingGET").withRel("profile")
        );

        pagedModel.forEach(
                dtoEntityModel -> dtoEntityModel.add(
                        linkTo(methodOn(RealTimeRequestController.class)
                                .realTimeRequestDetails(
                                        dtoEntityModel.getContent().getRealTimeRequestId()
                                )
                        ).withSelfRel()
                )
        );
        return ResponseEntity.ok(pagedModel);
    }

    /**
     * 실시간 의뢰 상세 조회
     */
    @ApiOperation("실시간 의뢰 상세 조회")
    @GetMapping("/{requestId}")
    public RealTimeRequestDetailDto realTimeRequestDetails(
            @ApiParam(value = "실시간 의뢰 PK", example = "1L")
            @PathVariable Long requestId
    ) {
        RealTimeRequestDetailDto response = realTimeRequestService.findOneRealTimeRequest(requestId);

        //링크 추가
        response.add(
                linkTo(methodOn(RealTimeRequestController.class).realTimeRequestDetails(requestId)).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/real-time-request-controller/realTimeRequestDetailsUsingGET").withRel("profile")
        );

        return response;
    }

    /**
     * 실시간 의뢰 추가
     */
    @ApiOperation("실시간 의뢰 추가")
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "")
    public ResponseEntity<?>realTimeRequestAdd(
            @LoginUser User user,
            @Valid @RequestPart(value = "data") CreateRealTimeRequestDto.Request request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
            ) {
        if(files == null){
            files = new ArrayList<>();
        }
        for (MultipartFile file: files) {
            log.info(file.getOriginalFilename());
        }
        log.info("=============================end");

        CreateRealTimeRequestDto.Response reseponse = realTimeRequestService.addRealTimeRequest(user, request, files);

        //링크 추가
        reseponse.add(
                linkTo(RealTimeRequestController.class).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/real-time-request-controller/realTimeRequestAddUsingPOST").withRel("profile")
        );
        return ResponseEntity.created(
                linkTo(
                        methodOn(RealTimeRequestController.class)
                                .realTimeRequestDetails(reseponse.getRealTimeRequestId())
                ).toUri()
        ).body(reseponse);
    }

    /**
     * 실시간 의뢰 수정
     */
    @ApiOperation("실시간 의뢰 수정")
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/{requestId}")
    public SimpleResponse realTimeRequestModify(
            @PathVariable Long requestId,
            @LoginUser User user,
            @Valid @RequestPart(value = "data") UpdateRealTimeRequestDto updateRealTimeRequestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        realTimeRequestService.modifyRealTimeRequest(requestId, user, updateRealTimeRequestDto, files);
        SimpleResponse response = SimpleResponse.success("정상적으로 수정을 완료하였습니다.");

        //링크 추가
        response.add(
                linkTo(methodOn(RealTimeRequestController.class).realTimeRequestDetails(requestId)).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/real-time-request-controller/realTimeRequestModifyUsingPUT").withRel("profile")
        );

        return response;
    }

    /**
     * 실시간 의뢰 삭제
     */
    @ApiOperation("실시간 의뢰 삭제")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{requestId}")
    public SimpleResponse realTimeRequestRemove(
            @ApiParam(value = "실시간 의뢰 PK", example = "1L")
            @PathVariable Long requestId,
            @LoginUser User user
    ) {
        realTimeRequestService.removeRealTimeRequest(requestId, user);
        SimpleResponse response = SimpleResponse.success("실시간 의뢰가 정상적으로 삭제 되었습니다.");

        //링크 추가
        response.add(
                linkTo(methodOn(RealTimeRequestController.class).realTimeRequestDetails(requestId)).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/real-time-request-controller/removeRealTimeRequestUsingDELETE").withRel("profile")
        );

        return response;
    }
    /** 실시간 의뢰 마감 설정 */
    @ApiOperation("실시간 의뢰 마감 설정")
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(value = "/{requestId}/finish", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SimpleResponse requestStatusFinish(
            @ApiParam(value = "실시간 의뢰 PK", example = "1L")
            @PathVariable Long requestId,
            @LoginUser User user
    ) {
        realTimeRequestService.modifyRequestStatus(requestId, user);
        SimpleResponse response = SimpleResponse.success("실시간 의뢰가 정상적으로 마감 되었습니다.");

        //링크 추가
        response.add(
                linkTo(methodOn(RealTimeRequestController.class).realTimeRequestDetails(requestId)).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/real-time-request-controller/requestStatusFinishUsingPATCH").withRel("profile")
        );
        return response;
    }


}

package com.onfree.controller;

import com.onfree.core.dto.realtimerequest.SimpleRealtimeRequestDto;
import com.onfree.core.service.RealTimeRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/real-time-requests",  consumes = MediaType.APPLICATION_JSON_VALUE)
public class RealTimeRequestController {
    private final RealTimeRequestService realTimeRequestService;

    /** 실시간 의뢰 전체 조회 */
    @GetMapping("")
    public ResponseEntity realTimeRequestList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            PagedResourcesAssembler<SimpleRealtimeRequestDto> assembler
    ){
        Page<SimpleRealtimeRequestDto> response = realTimeRequestService.findAllRealTimeRequest(page, size);
        PagedModel<EntityModel<SimpleRealtimeRequestDto>> pagedModel = assembler.toModel(response);

        //링크 추가
        pagedModel.add(
                Link.of(linkTo(SwaggerController.class) + "/#/real-time-request-controller/RealTimeRequestUsingGet").withRel("profile")
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

    /** 실시간 의뢰 상세 조회 */
    @GetMapping("/{realTimeRequestId}")
    public Object realTimeRequestDetails(@PathVariable Long realTimeRequestId){
        return null;
    }

    /** 실시간 의뢰 추가 */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("")
    public void realTimeRequestAdd(){

    }

    /** 실시간 의뢰 수정 */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{realTimeRequestId}")
    public void realTimeRequestModify(@PathVariable Long realTimeRequestId){

    }

    /** 실시간 의뢰 삭제 */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{realTimeRequestId}")
    public void realTimeRequestRemove(@PathVariable Long realTimeRequestId){

    }
}

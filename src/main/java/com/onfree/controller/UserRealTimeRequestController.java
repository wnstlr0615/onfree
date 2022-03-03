package com.onfree.controller;

import com.onfree.common.annotation.LoginUser;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/users/me/real-time-requests", consumes = MediaType.APPLICATION_JSON_VALUE)
public class UserRealTimeRequestController {
    private final RealTimeRequestService realTimeRequestService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("")
    public ResponseEntity<?> myRealTimeRequestList(
            @LoginUser Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            PagedResourcesAssembler<SimpleRealtimeRequestDto> assembler1
    ){
        Page<SimpleRealtimeRequestDto> response = realTimeRequestService.findAllRealTimeRequestByUserId(userId, page, size);
        PagedModel<EntityModel<SimpleRealtimeRequestDto>> pagedModel = assembler1.toModel(response);

        //링크 추가
        pagedModel.add(
                Link.of(linkTo(SwaggerController.class) + "/#/user-controller/RealTimeRequestUsingGet").withRel("profile")
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
}

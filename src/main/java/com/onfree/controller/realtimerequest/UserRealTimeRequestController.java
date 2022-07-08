package com.onfree.controller.realtimerequest;

import com.onfree.common.annotation.LoginUser;
import com.onfree.controller.SwaggerController;
import com.onfree.core.dto.realtimerequest.SimpleRealtimeRequestDto;
import com.onfree.core.entity.user.User;
import com.onfree.core.service.realtimerequest.UserRealTimeRequestService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
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
@RequestMapping(value = "/api/v1/users/me/real-time-requests")
public class UserRealTimeRequestController {
    private final UserRealTimeRequestService userRealTimeRequestService;

    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "본인 실시간 의뢰 전체 보기")
    @GetMapping("")
    public ResponseEntity<?> myRealTimeRequestList(
            @ApiParam(hidden = true)
            @LoginUser User user,
            @ApiParam(name = "page", example = "0", required = true)
            @RequestParam(defaultValue = "0") int page,
            @ApiParam(name = "size", example = "10", required = true)
            @RequestParam(defaultValue = "10") int size,
            PagedResourcesAssembler<SimpleRealtimeRequestDto> assembler
    ){
        Page<SimpleRealtimeRequestDto> response = userRealTimeRequestService.findAllRealTimeRequestByUserId(user, page, size);
        PagedModel<EntityModel<SimpleRealtimeRequestDto>> pagedModel = assembler.toModel(response);

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

package com.onfree.controller.requestapply;

import com.onfree.common.annotation.CurrentArtistUser;
import com.onfree.common.model.SimpleResponse;
import com.onfree.controller.SwaggerController;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.service.requestapply.RealTimeRequestApplyService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class RealTimeRequestApplyController {
    private final RealTimeRequestApplyService realTimeRequestApplyService;

    @ApiOperation(value = "실시간 의뢰 지원하기")
    @PreAuthorize(value = "hasRole('ARTIST')")
    @PostMapping(value = "/api/v1/real-time-requests/{requestId}/apply", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SimpleResponse requestApplyAdd(
            @ApiParam(value = "실시간 의뢰 pk", example = "1L")
            @PathVariable Long requestId,
            @CurrentArtistUser ArtistUser artistUser
        ){
        realTimeRequestApplyService.addRequestApply(requestId, artistUser);

        SimpleResponse response = SimpleResponse.success("의뢰 신청이 완료되었습니다.");

        response.add(
                linkTo(methodOn(RealTimeRequestApplyController.class).requestApplyAdd(requestId,artistUser)).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/real-time-request-apply-controller/requestApplyAddUsingPOST").withRel("profile")
        );

        return response;
    }
}

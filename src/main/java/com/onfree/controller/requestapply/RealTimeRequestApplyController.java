package com.onfree.controller.requestapply;

import com.onfree.common.annotation.CurrentArtistUser;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.service.requestapply.RealTimeRequestApplyService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RealTimeRequestApplyController {
    private final RealTimeRequestApplyService realTimeRequestApplyService;

    @ApiOperation(value = "실시간 의뢰 지원하기")
    @PreAuthorize(value = "hasRole('ARTIST')")
    @PostMapping(value = "/api/v1/real-time-requests/{requestId}/apply", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void requestApplyAdd(
            @ApiParam(value = "실시간 의뢰 pk", example = "1L")
            @PathVariable Long requestId,
            @CurrentArtistUser ArtistUser artistUser
        ){
        realTimeRequestApplyService.addRequestApply(requestId, artistUser);
    }

}

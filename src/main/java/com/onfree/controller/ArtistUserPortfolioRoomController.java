package com.onfree.controller;

import com.onfree.common.annotation.CurrentArtistUser;
import com.onfree.common.model.SimpleResponse;
import com.onfree.core.dto.portfolioroom.PortfolioRoomDetailDto;
import com.onfree.core.dto.portfolioroom.UpdatePortfolioStatusDto;
import com.onfree.core.dto.portfolioroom.UpdateStatusMessageDto;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.service.PortfolioRoomService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/artist/me/portfolio-room")
public class ArtistUserPortfolioRoomController {
    private final PortfolioRoomService portfolioRoomService;

    @ApiOperation("작가유저 본인 포트폴리오룸 조회")
    @PreAuthorize("hasRole('ARTIST')")
    @GetMapping()
    public PortfolioRoomDetailDto myPortfolioRoomDetails(
            @CurrentArtistUser ArtistUser artistUser
    ){
        PortfolioRoomDetailDto response = portfolioRoomService.findMyPortfolioRoom(artistUser);
        response.add(
                linkTo(ArtistUserPortfolioRoomController.class).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/artist-user-portfolio-room-controller/myPortfolioRoomDetailsUsingGET").withRel("profile")
        );
        return response;
    }

    /** 상태메시지 변경하기 */
    @ApiOperation("작가유저 포트폴리오룸 상태메시지 변경하기")
    @PreAuthorize("hasRole('ARTIST')")
    @PutMapping(value = "/status-message", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SimpleResponse statusMessageModify(
            @CurrentArtistUser ArtistUser artistUser,
            @Valid @RequestBody UpdateStatusMessageDto dto,
            BindingResult errors
    ){
        portfolioRoomService.modifyStatusMessage(artistUser, dto);
        SimpleResponse response = SimpleResponse.success("상태메시지가 성공적으로 변경 되었습니다.");

        response.add(
                linkTo(ArtistUserPortfolioRoomController.class).slash("status-message").withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/artist-user-portfolio-room-controller/statusMessageModifyUsingPut").withRel("profile")
        );
        return response;
    }
    /** 포트폴리오룸 상태 변경하기 */
    @ApiOperation("작가유저 포트폴리오룸 상태 변경하기")
    @PreAuthorize("hasRole('ARTIST')")
    @PutMapping(value = "/status", consumes = MediaType.APPLICATION_JSON_VALUE)
    public SimpleResponse portfolioRoomStatusModify(
            @CurrentArtistUser ArtistUser artistUser,
            @Valid @RequestBody UpdatePortfolioStatusDto dto,
            BindingResult errors
    ){
        portfolioRoomService.modifyPortfolioStatus(artistUser, dto);

        SimpleResponse response = SimpleResponse.success("포트폴리오룸 상태가 성공적으로 변경 되었습니다.");

        response.add(
                linkTo(ArtistUserPortfolioRoomController.class).slash("status").withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/artist-user-portfolio-room-controller/portfolioRoomStatusModifyUsingPut").withRel("profile")
        );
        return response;
    }
}

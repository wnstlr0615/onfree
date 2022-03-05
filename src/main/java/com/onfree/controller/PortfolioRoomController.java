package com.onfree.controller;

import com.onfree.core.dto.portfolioroom.PortfolioRoomDetailDto;
import com.onfree.core.service.PortfolioRoomService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/portfolio-rooms", consumes = MediaType.APPLICATION_JSON_VALUE)
public class PortfolioRoomController {
    private final PortfolioRoomService portfolioRoomService;

    /** 포트폴리오룸 조회하기*/
    @ApiOperation(value = "포트폴리오룸 조회하기")
    @GetMapping("/{portfolioRoomUrl}")
    public PortfolioRoomDetailDto portfolioRoomDetails(
            @ApiParam(value = "portfolioRoomUrl", example = "myRoom")
            @PathVariable String portfolioRoomUrl
    ){
        PortfolioRoomDetailDto response = portfolioRoomService.findOnePortfolioRoom(portfolioRoomUrl);
        response.add(
                linkTo(methodOn(PortfolioRoomController.class).portfolioRoomDetails(portfolioRoomUrl)).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/portfolio-room-controller/portfolioRoomDetailsUsingGET").withRel("profile")
        );
        return response;
    }

}

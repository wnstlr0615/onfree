package com.onfree.controller;

import com.onfree.common.ControllerBaseTest;
import com.onfree.core.dto.realtimerequest.SimpleRealtimeRequestDto;
import com.onfree.core.entity.realtimerequset.RequestStatus;
import com.onfree.core.service.RealTimeRequestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest( controllers = RealTimeRequestController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class RealTimeRequestControllerTest extends ControllerBaseTest {
    @MockBean
    RealTimeRequestService realTimeRequestService;

    @Test
    @DisplayName("[성공][GET] 실시간 의뢰 페이징 테스트")
    public void givenPageAndPageSize_whenRealTimeRequestList_thenPagingList() throws Exception{
        //given
        int page = 0;
        int size = 10;
        int total = 6;

        PageRequest pageRequest = PageRequest.of(page, size);

        List<SimpleRealtimeRequestDto> simpleRealtimeRequestDtos =  List.of(
                createSimpleRealTimeRequestDto(1L, "제목1", RequestStatus.REQUEST_RECRUITING.getDisplayStatus()),
                createSimpleRealTimeRequestDto(2L, "제목2", RequestStatus.REQUEST_RECRUITING.getDisplayStatus()),
                createSimpleRealTimeRequestDto(3L, "제목3", RequestStatus.REQUEST_RECRUITING.getDisplayStatus()),
                createSimpleRealTimeRequestDto(4L, "제목4", RequestStatus.REQUEST_FINISH.getDisplayStatus()),
                createSimpleRealTimeRequestDto(5L, "제목5", RequestStatus.REQUEST_FINISH.getDisplayStatus()),
                createSimpleRealTimeRequestDto(6L, "제목6", RequestStatus.REQUEST_FINISH.getDisplayStatus())
        );

        PageImpl<SimpleRealtimeRequestDto> dtoPage = new PageImpl<>(simpleRealtimeRequestDtos, pageRequest, total);

        when(realTimeRequestService.findAllRealTimeRequest(page, size))
                .thenReturn(
                        dtoPage
                );
        //when //then
        mvc.perform(get("/api/v1/real-time-requests")
            .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.items[0].realTimeRequestId").value(1L))
            .andExpect(jsonPath("$._embedded.items[0].title").value("제목1"))
            .andExpect(jsonPath("$._embedded.items[0].status").value(RequestStatus.REQUEST_RECRUITING.getDisplayStatus()))
            .andExpect(jsonPath("$._embedded.items[0].nickname").isNotEmpty())
            .andExpect(jsonPath("$._embedded.items[0].startDate").isNotEmpty())
            .andExpect(jsonPath("$._embedded.items[0].endDate").isNotEmpty())
            .andExpect(jsonPath("$._embedded.items[0].createDate").isNotEmpty())
            .andExpect(jsonPath("$._embedded.items[0]._links.self").isNotEmpty())
            .andExpect(jsonPath("$._links.self").isNotEmpty())
            .andExpect(jsonPath("$._links.profile").isNotEmpty())

        ;
        verify(realTimeRequestService).findAllRealTimeRequest(anyInt(), anyInt());
    }

    private SimpleRealtimeRequestDto createSimpleRealTimeRequestDto(long requestId, String title, String status) {
        LocalDate startDate = LocalDate.of(2022,3,3);
        LocalDate endDate = LocalDate.of(2022,3,6);
        LocalDate createDate = LocalDate.of(2022,3,2);
        String nickname = "닉네임";
        return SimpleRealtimeRequestDto.createSimpleRealtimeRequestDto(requestId, title, nickname, status, startDate, endDate, createDate);
    }
}
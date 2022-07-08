package com.onfree.controller.external.toss;

import com.onfree.common.model.SimpleResponse;
import com.onfree.core.service.external.toss.TossPaymentService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/toss/payments")
public class TossPaymentsController {
    private final TossPaymentService tossPaymentService;

    @ApiOperation(value = "토스 결제 승인 요청 API", hidden = true)
    @GetMapping("/success")
    public SimpleResponse success(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount
    ) {
        tossPaymentService.requestApproval (paymentKey, orderId, amount);
        return SimpleResponse.success("결제가 성공적으로 성공하였습니다.");
    }
}

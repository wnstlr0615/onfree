package com.onfree.controller;

import com.onfree.core.service.RealTimeRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class RealTimeRequestController {
    private final RealTimeRequestService realTimeRequestService;
}

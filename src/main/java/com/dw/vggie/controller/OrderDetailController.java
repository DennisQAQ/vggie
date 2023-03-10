package com.dw.vggie.controller;

import com.dw.vggie.service.OrderDetailService;
import com.dw.vggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/orderDetail")

public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

}

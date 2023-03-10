package com.dw.vggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dw.vggie.entities.Orders;

public interface OrdersService extends IService<Orders> {
    void submit(Orders orders);
}

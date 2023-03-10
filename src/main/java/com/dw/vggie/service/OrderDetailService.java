package com.dw.vggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dw.vggie.entities.OrderDetail;
import com.dw.vggie.entities.Orders;
import org.springframework.core.annotation.Order;

import java.util.List;

public interface OrderDetailService extends IService<OrderDetail> {

    /**
     * 根据订单id查询订单明细
     * @param id
     * @return
     */
    List<OrderDetail> getOrderDetailByOrderId(Long id);
}

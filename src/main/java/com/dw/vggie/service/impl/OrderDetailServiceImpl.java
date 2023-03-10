package com.dw.vggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dw.vggie.entities.OrderDetail;
import com.dw.vggie.entities.Orders;
import com.dw.vggie.mapper.OrderDetailMapper;
import com.dw.vggie.mapper.OrdersMapper;
import com.dw.vggie.service.OrderDetailService;
import com.dw.vggie.service.OrdersService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
    @Override
    public List<OrderDetail> getOrderDetailByOrderId(Long id) {
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,id);
        List<OrderDetail> orderDetails = this.list(queryWrapper);
        return orderDetails;
    }
}

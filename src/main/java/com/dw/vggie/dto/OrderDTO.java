package com.dw.vggie.dto;

import com.dw.vggie.entities.OrderDetail;
import com.dw.vggie.entities.Orders;
import lombok.Data;

import java.util.List;

@Data
public class OrderDTO extends Orders {
    List<OrderDetail> OrderDetails;
}

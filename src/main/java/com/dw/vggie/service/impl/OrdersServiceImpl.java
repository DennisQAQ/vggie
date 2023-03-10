package com.dw.vggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dw.vggie.common.BaseContext;
import com.dw.vggie.common.CustomException;
import com.dw.vggie.entities.*;
import com.dw.vggie.mapper.OrdersMapper;
import com.dw.vggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private AddressBookService addressBookService;


    /**
     * 提交订单方法
     * @param orders
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        //获取当前用户id
        Long currentId = BaseContext.getCurrentId();

        //查询当前用户的购物车数据
         LambdaQueryWrapper<ShoppingCart> queryWrapper =new LambdaQueryWrapper();
         queryWrapper.eq(ShoppingCart::getUserId,currentId);
         List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
         if(shoppingCartList==null||shoppingCartList.size()==0){
             throw new CustomException("购物车为空，无法下单！");
         }
         //查询用户数据
        User user = userService.getById(currentId);

        //查询用户地址
        Long addressBookId = orders.getAddressBookId();

        AddressBook addressBook = addressBookService.getById(addressBookId);


        if (addressBook ==null){
            throw new CustomException("用户地址信息错误，无法下单！");
        }
        long id = IdWorker.getId();//订单号
        AtomicInteger amount = new AtomicInteger();

        //向订单表插入数据
        List<OrderDetail> orderDetailList = shoppingCartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(id);

            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;

        }).collect(Collectors.toList());

        orders.setId(id);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(currentId);
        orders.setNumber(String.valueOf(id));
//        orders.setUserName(user.getName());
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        this.save(orders);
        //向订单明细表插入数据
        orderDetailService.saveBatch(orderDetailList);
        //删除购物车数据
        shoppingCartService.remove(queryWrapper);
    }
}

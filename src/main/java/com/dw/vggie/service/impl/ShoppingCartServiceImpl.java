package com.dw.vggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dw.vggie.common.BaseContext;
import com.dw.vggie.entities.ShoppingCart;
import com.dw.vggie.mapper.ShoppingCartMapper;
import com.dw.vggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

//    @Override
//    public void clean(Long id) {
//        //SQL:delete from shopping_cart where user_id=?
//        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(ShoppingCart::getUserId, id);
//        this.remove(queryWrapper);
//    }
    @Override
    public void clean() {
        //SQL:delete from shopping_cart where user_id=?
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        this.remove(queryWrapper);
    }
}


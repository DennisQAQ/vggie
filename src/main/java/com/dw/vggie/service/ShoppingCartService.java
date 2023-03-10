package com.dw.vggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dw.vggie.entities.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {
//    void clean(Long id);
    void clean();
}

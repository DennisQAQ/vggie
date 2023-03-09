package com.dw.vggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dw.vggie.entities.Category;


public interface CategoryService extends IService<Category> {
    void remove(Long id);
}

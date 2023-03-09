package com.dw.vggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dw.vggie.dto.DishDto;
import com.dw.vggie.entities.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish dishflavor
    public void saveWithFlavor (DishDto dto);

    //根据id信息查询对应的菜品信息和口味信息
    public DishDto getByIdWithFlavor(Long id);
    //修改菜品信息，
    public void updateWithFlavor(DishDto dto);

    //逻辑删除菜品信息
    public void deleteByids(List<Long > ids);


}

package com.dw.vggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dw.vggie.dto.SetmealDto;
import com.dw.vggie.entities.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时保持与菜品的关联关系
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时删除与套餐关联的菜品数据
     */

    void removeWithDish(List<Long> ids);

    /**
     * 修改套餐状态，停售启售
     */
    void updateSetmealStatusById(Integer status,List<Long>ids);

    SetmealDto getData(Long id);
}

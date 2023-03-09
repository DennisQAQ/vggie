package com.dw.vggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dw.vggie.common.CustomException;
import com.dw.vggie.entities.Dish;
import com.dw.vggie.entities.Setmeal;
import com.dw.vggie.service.DishService;
import com.dw.vggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.dw.vggie.entities.Category;
import com.dw.vggie.mapper.CategoryMapper;
import com.dw.vggie.service.CategoryService;

@Service
public class CatagoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    //根据id删除之前，判断是否缓存关联表
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> DishQueryWrapper = new LambdaQueryWrapper<>();
        DishQueryWrapper.eq(Dish::getCategoryId,id);
        int countOfDish = dishService.count();
        //1,查询当前分类是否关联其他菜品，如果已经关联，抛出一个业务异常
        if(countOfDish>0){
            //已经关联菜品，抛出业务异常；
            throw new CustomException("已经关联菜品不能 删除！");
        }
        //2,查询当前分类是否管理其他套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.eq(Setmeal::getCategoryId,id);
        int countOfSetmeal = setmealService.count();
        if (countOfSetmeal>0){
            //已经关联套餐，抛出业务异常；
            throw new CustomException("已经关联套餐不能删除！");
        }
        //3,正常删除
        super.removeById(id);
    }
}

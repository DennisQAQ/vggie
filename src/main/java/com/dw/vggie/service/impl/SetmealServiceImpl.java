package com.dw.vggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dw.vggie.common.CustomException;
import com.dw.vggie.dto.SetmealDto;
import com.dw.vggie.entities.Setmeal;
import com.dw.vggie.entities.SetmealDish;
import com.dw.vggie.mapper.SetmealMapper;
import com.dw.vggie.service.SetmealDishService;
import com.dw.vggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal ，执行insert
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //保存套餐和菜品的关联关系，操作setmeal_dish,执行insert
        setmealDishService.saveBatch(setmealDishes);


    }

    /**
     * 删除套餐，同时删除与套餐关联的菜品数据
     * @param ids
     */
    @Override
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态,确定是否可用于删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        if(count>0){
            //如果不能删除，则抛出业务异常
            throw  new CustomException("套餐正在售卖中，不能删删除！");
        }

        //如果可以删除，先删除套餐中的数据 ---setmeal
        this.removeByIds(ids);
        //删除关系表中的数据
        LambdaQueryWrapper<SetmealDish> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper1);

    }

    /**
     * 修改套餐状态，停售启售
     * @param status
     * @param ids
     */
    @Override
    public void updateSetmealStatusById(Integer status, List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.in(ids!=null,Setmeal::getId,ids);
        List<Setmeal> list = this.list(queryWrapper);

        for (Setmeal setmeal:list) {
            if(setmeal!=null){
                setmeal.setStatus(status);
                this.updateById(setmeal);
            }
        }
    }

    @Override
    public SetmealDto getData(Long id) {
        //根据前传传递id获取Setmeal信息
        Setmeal setmeal =this.getById(id);
        //
        SetmealDto setmealDto =new SetmealDto();

        LambdaQueryWrapper<SetmealDish> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(id!=null,SetmealDish::getSetmealId,id);

        if (setmeal!=null){
            BeanUtils.copyProperties(setmeal,setmealDto);
            List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
            setmealDto.setSetmealDishes(setmealDishes);
            return setmealDto;
        }
        return null;
    }
}

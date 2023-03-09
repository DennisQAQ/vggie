package com.dw.vggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dw.vggie.common.CustomException;
import com.dw.vggie.dto.DishDto;
import com.dw.vggie.entities.Dish;
import com.dw.vggie.entities.DishFlavor;
import com.dw.vggie.mapper.DishMapper;
import com.dw.vggie.service.DishFLavorService;
import com.dw.vggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFLavorService dishFLavorService ;
    @Override

    @Transactional
    public void saveWithFlavor(DishDto dto) {
        //保存菜品的基本信息到口味表dish
        this.save(dto);
        Long dishId=dto.getId();
        List<DishFlavor> flavors = dto.getFlavors();//菜品口味
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        //保存菜品口味到口味表dishflavor
        dishFLavorService.saveBatch(flavors);

    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品信息
        Dish dish = this.getById(id);
        DishDto dishDto =new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //查询当前菜品对应的口味信息,从dishflavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper  =new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> list = dishFLavorService.list(queryWrapper);
        dishDto.setFlavors(list);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dto) {
        //1,更新bish表基本信息
        this.updateById(dto);
        //2,清理当前表对应口味数据--dish-flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dto.getId());
        dishFLavorService.remove(queryWrapper);
        //3,添加提交过来的口味数据--dish-flavor表的insert操作
        List<DishFlavor> flavors = dto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFLavorService.saveBatch(flavors);
    }

    /**
     * 套餐批量删除和单个删除
     * @param ids
     */
    @Override
    public void deleteByids(List<Long> ids) {
        //构造条件查询器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //先查询该菜品是否在售卖，如果是则抛出业务异常
        queryWrapper.in(ids!=null,Dish::getId,ids);
        List<Dish> list = this.list(queryWrapper);
        for (Dish dish : list) {
            Integer status = dish.getStatus();
            //如果不是在售卖,则可以删除
            if (status == 0){
                this.removeById(dish.getId());
            }else {
                //此时应该回滚,因为可能前面的删除了，但是后面的是正在售卖
                throw new CustomException("删除菜品中有正在售卖菜品,无法全部删除");
            }
        }

    }


}

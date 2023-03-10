package com.dw.vggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dw.vggie.common.R;
import com.dw.vggie.dto.DishDto;
import com.dw.vggie.dto.SetmealDto;
import com.dw.vggie.entities.Category;
import com.dw.vggie.entities.Dish;
import com.dw.vggie.entities.Setmeal;
import com.dw.vggie.entities.SetmealDish;
import com.dw.vggie.service.CategoryService;
import com.dw.vggie.service.DishService;
import com.dw.vggie.service.SetmealDishService;
import com.dw.vggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理类
 *
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name){
        Page<Setmeal> setmealPage =new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        //条件查询构造器
        LambdaQueryWrapper<Setmeal> queryWrapper =new LambdaQueryWrapper<>();
        //添加查询条件，根据name进行模糊查询
        queryWrapper.like(name!=null,Setmeal::getName,name);
        //添加排序条件，根据更新时间进行排序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //调用查询接口
        setmealService.page(setmealPage,queryWrapper);
        /////////////////////////////////////////////////
        //对象拷贝
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto SetmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item, SetmealDto);
            //分类ID
            Long categoryId = item.getCategoryId();
            //根据分类ID查询分类名称
            Category byId = categoryService.getById(categoryId);
            if (byId != null) {
                String categoryName = byId.getName();
                SetmealDto.setCategoryName(categoryName);
            }
            return SetmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids:{}",ids);
        setmealService.removeWithDish(ids);
        return R.success("删除成功！");
    }

    /**
     * 修改套餐状态，停售启售
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    //这个参数这里一定记得加注解才能获取到参数，否则这里非常容易出问题
    public R<String> status(@PathVariable("status") Integer status,@RequestParam List<Long> ids){
        setmealService.updateSetmealStatusById(status,ids);
        return R.success("售卖状态修改成功");
    }

    /**
     * 回显套餐数据：根据套餐id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getData(@PathVariable Long id){
        log.info("修改的id是{}",id);
        SetmealDto setmealDto = setmealService.getData(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> updateById(@RequestBody SetmealDto setmealDto){
        if (setmealDto==null){
            return R.error("请求异常！");
        }
        if (setmealDto.getSetmealDishes()==null){
            return R.error("套餐中没包含菜品，请添加！");
        }
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        Long setmealDishId = setmealDto.getId();

        LambdaQueryWrapper<SetmealDish> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDishId);
        setmealDishService.remove(queryWrapper);
        for (SetmealDish setmealDis: setmealDishes) {
            setmealDis.setSetmealId(setmealDishId);

        }
        setmealDishService.saveBatch(setmealDishes);
        setmealService.updateById(setmealDto);
        return R.success("套餐修改成功！");
    }

    /**
     * 根据条件查询套餐数据
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null!=setmeal.getCategoryId(),Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(null!=setmeal.getStatus(),Setmeal::getStatus,setmeal.getStatus());

        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> setmealList = setmealService.list(queryWrapper);
        return R.success(setmealList);

    }

    /**
     * 移动端点击套餐图片查看套餐具体内容
     * @param setmealID
     * @return
     */
    @GetMapping("/dish/{id}")
    public R<List<DishDto>> checkDishDetail(@PathVariable("id") Long setmealID){
        LambdaQueryWrapper <SetmealDish> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealID);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);

        List<DishDto> dishDtos = list.stream().map((setmealDish) -> {
            DishDto dishDto = new DishDto();
            //其实这个BeanUtils的拷贝是浅拷贝，这里要注意一下
            BeanUtils.copyProperties(setmealDish, dishDto);
            //这里是为了把套餐中的菜品的基本信息填充到dto中，比如菜品描述，菜品图片等菜品的基本信息
            Long dishId = setmealDish.getDishId();
            Dish dish = dishService.getById(dishId);
            BeanUtils.copyProperties(dish, dishDto);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtos);

    }
}

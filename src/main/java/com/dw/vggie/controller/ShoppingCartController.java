package com.dw.vggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dw.vggie.common.BaseContext;
import com.dw.vggie.common.R;
import com.dw.vggie.entities.ShoppingCart;
import com.dw.vggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){

        //1,获取当前用户id，指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);


        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        //2,查询当前的菜品或者套餐再购物车中否是已经存
        Long dishId = shoppingCart.getDishId();
        if(dishId!=null){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }
        else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);
        if (shoppingCart1!=null){
            //2,1如果已经存在则在原来的数量上加1
            Integer number = shoppingCart1.getNumber();
            shoppingCart1.setNumber(number+1);
            shoppingCartService.updateById(shoppingCart1);
        }else {
            //2,2如果不存在，则添加到购物车中，默认数量为1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            shoppingCart1=shoppingCart;

        }
        return R.success(shoppingCart1);
    }

    /**
     * 减少购物车数量
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        //指定用户的购物车
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper =new LambdaQueryWrapper<>();

        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        //2,查询当前的菜品或者套餐再购物车中否是已经存
        Long dishId = shoppingCart.getDishId();
        if(dishId!=null){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }
        else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);

        if (shoppingCart1.getNumber()>1){
            //2,1如果已经存在则在原来的数量上-1
            Integer number = shoppingCart1.getNumber();
            shoppingCart1.setNumber(number-1);
            shoppingCartService.updateById(shoppingCart1);
            return R.success(shoppingCart1);
        }else {
            //2,2如果不存在，则删除到购物车
            shoppingCartService.remove(queryWrapper);
//            return R.error("菜品或套餐数量最少为1！");
            return R.success(shoppingCart1);
        }
    }
    /**
     * 展示购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车");
        LambdaQueryWrapper<ShoppingCart> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    @DeleteMapping("/clean")
    public R<String> clean(){
//        Long currentId = BaseContext.getCurrentId();
//        shoppingCartService.clean( currentId);
        shoppingCartService.clean();
        return R.success("清空购物车成功！");

    }

}
